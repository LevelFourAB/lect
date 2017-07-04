package se.l4.lect.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import se.l4.lect.Encounter;
import se.l4.lect.Handler;
import se.l4.lect.LanguageEncounter;
import se.l4.lect.LanguageParser;
import se.l4.lect.Location;
import se.l4.lect.Pipeline;
import se.l4.lect.Source;
import se.l4.lect.SourceEncounter;
import se.l4.lect.Token;

/**
 * Implementation of {@link Pipeline}.
 *
 * @author Andreas Holstenson
 *
 */
public class PipelineImpl
	implements Pipeline
{
	private final Function<LanguageEncounter, LanguageParser> languageCreator;
	private final List<Function<Encounter, Handler>> handlers;

	public PipelineImpl(
			Function<LanguageEncounter, LanguageParser> languageCreator,
			List<Function<Encounter, Handler>> handlers)
	{
		this.languageCreator = languageCreator;
		this.handlers = handlers;
	}

	@Override
	public List<Object> run(Source source)
		throws IOException
	{
		List<Object> result = new ArrayList<>();
		run(source, result::add);
		return result;
	}

	@Override
	public void run(Source source, Consumer<Object> collector)
		throws IOException
	{
		Runner runner = new Runner(handlers, collector);
		source.parse(runner);
	}

	private class Runner
		implements SourceEncounter, LanguageEncounter, Encounter
	{
		private final Consumer<Object> collector;
		private final LanguageParser language;
		private final Handler[] handlers;

		private Location location;
		private boolean inParagraph;

		public Runner(List<Function<Encounter, Handler>> handlers, Consumer<Object> collector)
		{
			this.collector = collector;
			this.language = languageCreator.apply(this);

			List<Handler> instances = new ArrayList<>(handlers.size());
			for(Function<Encounter, Handler> h : handlers)
			{
				instances.add(h.apply(this));
			}
			this.handlers = instances.toArray(new Handler[instances.size()]);
		}

		@Override
		public void add(Object o)
		{
			collector.accept(o);
		}

		@Override
		public Locale locale()
		{
			return language.locale();
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

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].startParagraph();
			}
		}

		@Override
		public void endParagraph()
		{
			language.flush();

			inParagraph = false;

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].endParagraph();
			}
		}

		@Override
		public void done()
		{
			language.flush();

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].done();
			}
		}

		@Override
		public void text(String text, String source)
		{
			language.text(text, location, location.copy().moveTextIndex(source.length()));
		}

		@Override
		public void startSentence()
		{
			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].startSentence();
			}
		}

		@Override
		public void endSentence()
		{
			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].endSentence();
			}
		}

		@Override
		public void token(Token token)
		{
			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].token(token);
			}
		}
	}
}
