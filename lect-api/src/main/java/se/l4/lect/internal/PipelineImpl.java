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
		source.parse(runner);
	}

	private class Runner
		implements TextSourceEncounter, LanguageEncounter, Encounter<Collector>
	{
		private final Collector collector;

		private final LanguageParser language;
		private final Handler[] handlers;

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
			this.handlers = instances.toArray(new Handler[instances.size()]);
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

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].startParagraph(location);
			}
		}

		@Override
		public void endParagraph()
		{
			language.flush();

			inParagraph = false;

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].endParagraph(location);
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
		public void text(CharSequence text, Location end)
		{
			language.text(text, location, end);
		}

		@Override
		public void startSentence(Location location)
		{
			Location old = this.location;
			this.location = location;

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].startSentence(location);
			}

			this.location = old;
		}

		@Override
		public void endSentence(Location location)
		{
			Location old = this.location;
			this.location = location;

			for(int i=0, n=handlers.length; i<n; i++)
			{
				handlers[i].endSentence(location);
			}

			this.location = old;
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
