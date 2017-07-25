package se.l4.lect.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import se.l4.lect.Encounter;
import se.l4.lect.Handler;
import se.l4.lect.LanguageEncounter;
import se.l4.lect.LanguageFactory;
import se.l4.lect.LanguageParser;
import se.l4.lect.Pipeline;
import se.l4.lect.TextSource;
import se.l4.lect.TextSourceEncounter;
import se.l4.lect.handlers.MultiStageHandler;
import se.l4.lect.location.Location;
import se.l4.lect.tokens.Token;

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
	private final List<Function<Encounter, Handler>> handlers;

	public PipelineImpl(
			LanguageFactory languageFactory,
			List<Function<Encounter, Handler>> handlers)
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

	private class Runner
		implements TextSourceEncounter, LanguageEncounter, Encounter<Collector>
	{
		private final Collector collector;

		private final LanguageParser language;
		private final List<Handler> handlers;

		private Handler[] activeHandlers;

		private Location location;
		private boolean inParagraph;

		public Runner(List<Function<Encounter, Handler>> handlers, Collector collector)
		{
			this.collector = collector;

			this.language = languageFactory.create(this);

			List<Handler> instances = new ArrayList<>(handlers.size());
			for(Function<Encounter, Handler> h : handlers)
			{
				instances.add(h.apply(this));
			}
			this.handlers = instances;
			this.activeHandlers = instances.toArray(new Handler[instances.size()]);
		}

		private void resolveActive()
		{
			List<Handler> active = new ArrayList<>();
			for(Handler h : handlers)
			{
				if(h instanceof MultiStageHandler)
				{
					if(((MultiStageHandler) h).hasMoreStages())
					{
						active.add(h);
					}
				}
			}
			this.activeHandlers = active.isEmpty() ? null : active.toArray(new Handler[active.size()]);
		}

		private boolean hasMore()
		{
			return this.activeHandlers != null;
		}

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
			language.flush();

			inParagraph = true;

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].startParagraph(location);
			}
		}

		@Override
		public void endParagraph()
		{
			language.flush();

			inParagraph = false;

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].endParagraph(location);
			}
		}

		@Override
		public void done()
		{
			language.flush();

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].done();
			}

			resolveActive();
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

			this.location = old;
		}

		@Override
		public void endSentence(Location location)
		{
			Location old = this.location;
			this.location = location;

			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].endSentence(location);
			}

			this.location = old;
		}

		@Override
		public void token(Token token)
		{
			for(int i=0, n=activeHandlers.length; i<n; i++)
			{
				activeHandlers[i].token(token);
			}
		}
	}
}
