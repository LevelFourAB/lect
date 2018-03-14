package se.l4.lect.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import se.l4.lect.Encounter;
import se.l4.lect.Handler;
import se.l4.lect.HandlerFactory;
import se.l4.lect.LanguageEncounter;
import se.l4.lect.LanguageFactory;
import se.l4.lect.LanguageParser;
import se.l4.lect.Pipeline;
import se.l4.lect.TextSource;
import se.l4.lect.TextSourceEncounter;
import se.l4.lect.handlers.MultiStageHandler;
import se.l4.lect.location.Location;
import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.TokenProperty;

/**
 * Implementation of {@link Pipeline}.
 *
 * @author Andreas Holstenson
 *
 */
public class PipelineImpl<Collector>
	implements Pipeline<Collector>
{
	private final LanguageFactory languageFactory;
	private final List<HandlerFactory<?>> handlers;

	public PipelineImpl(
			LanguageFactory languageFactory,
			List<HandlerFactory<?>> handlers)
	{
		this.languageFactory = languageFactory;
		this.handlers = handlers;
	}

	@Override
	public void run(TextSource source)
		throws IOException
	{
		run(source, null);
	}

	@Override
	public void run(TextSource source, Collector collector)
		throws IOException
	{
		Runner runner = new Runner(handlers, collector);
		while(runner.hasMore())
		{
			runner.start();
			source.parse(runner);
		}
	}

	/**
	 * Runner that takes care to take the source parts and turm them into
	 * events and tokens.
	 */
	private class Runner
		implements TextSourceEncounter, LanguageEncounter, Encounter<Collector>
	{
		private final Collector collector;

		private final LanguageParser language;
		private final List<Handler> handlers;

		private LinkedList<AttributeDeclaration> attributes;
		private List<AttributeDeclaration> activeAttributes;

		private Handler[] activeHandlers;

		private Location location;
		private boolean inParagraph;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Runner(List<HandlerFactory<?>> handlers, Collector collector)
		{
			this.collector = collector;

			this.language = languageFactory.create(this);

			// Create all of the handlers that should be run for the source
			List<Handler> instances = new ArrayList<>(handlers.size());
			for(HandlerFactory<?> h : handlers)
			{
				instances.add(h.create((Encounter) this));
			}
			this.handlers = instances;

			// Turn the active handlers into an array to make iteration of them a bit faster
			this.activeHandlers = instances.toArray(new Handler[instances.size()]);
		}

		/**
		 * Go through and see which handlers should be active for the next
		 * pass over the source.
		 */
		private void resolveActive()
		{
			List<Handler> active = new ArrayList<>();
			for(Handler h : handlers)
			{
				if(h instanceof MultiStageHandler)
				{
					/*
					 * MultiStageHandlers might need to pass through the source
					 * several times.
					 */
					if(((MultiStageHandler) h).hasMoreStages())
					{
						active.add(h);
					}
				}
			}

			// Either store as an array or as a null if no handlers are active
			this.activeHandlers = active.isEmpty() ? null : active.toArray(new Handler[active.size()]);
		}

		/**
		 * Get if there any more handlers that need to run.
		 * 
		 * @return
		 *   if there are any active handlers
		 */
		private boolean hasMore()
		{
			return this.activeHandlers != null;
		}

		/**
		 * Indicate that we are starting processing.
		 */
		private void start()
		{
			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].start();
			}
		}

		@Override
		public Locale locale()
		{
			return language.locale();
		}

		@Override
		public Collector collector()
		{
			return collector;
		}

		@Override
		public Location location()
		{
			return location;
		}

		@Override
		public void location(Location location)
		{
			this.location = location;
		}

		@Override
		public boolean inParagraph()
		{
			return inParagraph;
		}

		@Override
		public void startParagraph()
		{
			/*
			 * Flush the language parser so it can output tokens between
			 * paragraphs such as whitespace.
			 */
			language.flush();

			// Indicate that we are within a paragraph
			inParagraph = true;

			// Tell all the handlers about the new paragraph
			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].startParagraph(location);
			}
		}

		@Override
		public void endParagraph()
		{
			/*
			 * Flush the language parser so it can output the tokens within
			 * the paragraph.
			 */
			language.flush();

			// End all attributes started within the paragraph
			endAttributes(location, true);
			if(attributes != null) attributes.clear();

			// Indicate that we are no longer in a paragraph
			inParagraph = false;

			// Tell all the handlers about the paragraph being ended
			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].endParagraph(location);
			}
		}

		@Override
		public void done()
		{
			/**
			 * Flush the language parser to output the last few tokens. This
			 * may emit some extra whitespace tokens.
			 */
			language.flush();

			// Tell all the handlers that we are done
			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].done();
			}

			// Resolve the handlers to run in the next step
			resolveActive();
		}

		@Override
		public <T> void setAttribute(TokenProperty<T> attribute, T value)
		{
			if(attributes == null)
			{
				attributes = new LinkedList<>();
				activeAttributes = new LinkedList<>();
			}
			else
			{
				clearAttribute(attribute);
			}

			attributes.add(new AttributeDeclaration(attribute, value, location));
		}

		@Override
		public void clearAttribute(TokenProperty<?> attribute)
		{
			if(attributes == null) return;

			ListIterator<AttributeDeclaration> it = attributes.listIterator(attributes.size());
			while(it.hasPrevious())
			{
				AttributeDeclaration decl = it.previous();
				if(decl.attribute.equals(attribute))
				{
					if(decl.end == null)
					{
						decl.end = location.copy();
					}
					break;
				}
			}
		}

		/**
		 * End attributes that are no longer active after the given location.
		 * 
		 * @param location
		 *   the current location
		 * @param clearAll
		 *   if all the attributes should be cleared
		 */
		private void endAttributes(Location location, boolean clearAll)
		{
			if(activeAttributes == null || activeAttributes.isEmpty()) return;

			Iterator<AttributeDeclaration> it = activeAttributes.iterator();
			while(it.hasNext())
			{
				AttributeDeclaration decl = it.next();
				if(decl.end.isSameOrBefore(location))
				{
					// This attribute has ended
					for(int i=0, n=activeHandlers.length; i<n; i++)
					{
						activeHandlers[i].endAttribute(location, decl.attribute);
					}

					it.remove();
				}
				else if(clearAll)
				{
					// Clear all is active, end all attributes
					for(int i=0, n=activeHandlers.length; i<n; i++)
					{
						activeHandlers[i].endAttribute(location, decl.attribute);
					}
				}
				else
				{
					break;
				}
			}
		}

		/**
		 * Tell the handlers about the active attributes at the given location.
		 */
		private void emitActiveAttributes(Location location)
		{
			if(activeAttributes == null || activeAttributes.isEmpty()) return;

			Iterator<AttributeDeclaration> it = activeAttributes.iterator();
			while(it.hasNext())
			{
				AttributeDeclaration decl = it.next();
				for(int i=0, n=activeHandlers.length; i<n; i++)
				{
					activeHandlers[i].startAttribute(location, decl.attribute, decl.value);
				}
			}
		}

		/**
		 * Activate attributes that intersect the given location.
		 */
		private void startAttributes(Location location)
		{
			if(attributes == null || attributes.isEmpty()) return;

			Iterator<AttributeDeclaration> it = attributes.iterator();
			while(it.hasNext())
			{
				AttributeDeclaration decl = it.next();
				if(decl.start.isSameOrBefore(location))
				{
					// This attribute started before or at this token, make it active
					activeAttributes.add(decl);
					it.remove();

					for(int i=0, n=activeHandlers.length; i<n; i++)
					{
						activeHandlers[i].startAttribute(location, decl.attribute, decl.value);
					}
				}
				else
				{
					break;
				}
			}
		}

		@Override
		public void text(CharSequence text, Location end)
		{
			language.text(text, location, end);
		}

		@Override
		public void startSentence(Location location)
		{
			Location old = this.location;
			this.location = location;

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].startSentence(location);
			}

			// After starting a sentence output all of the active attributes again
			emitActiveAttributes(location);

			this.location = old;
		}

		@Override
		public void endSentence(Location location)
		{
			Location old = this.location;
			this.location = location;

			// End all of the active attributes
			endAttributes(location, true);

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].endSentence(location);
			}

			this.location = old;
		}

		@Override
		public void token(Token token)
		{
			// End attributes that come before this token
			endAttributes(token.getStart(), false);

			// Start attributes that come before the end of this token
			startAttributes(token.getEnd());

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].token(token);
			}
		}
	}

	private static class AttributeDeclaration
	{
		private final TokenProperty<?> attribute;
		private final Object value;

		private Location start;
		private Location end;

		public AttributeDeclaration(TokenProperty<?> attribute, Object value, Location start)
		{
			this.attribute = attribute;
			this.value = value;
			this.start = start.copy();
		}

		@Override
		public String toString()
		{
			return "Attr{" + attribute.getId() + ", start=" + start + ", end=" + end + ", value=" + value + "}";
		}
	}
}
