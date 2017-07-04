package se.l4.lect.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import se.l4.lect.Encounter;
import se.l4.lect.Handler;
import se.l4.lect.LanguageEncounter;
import se.l4.lect.LanguageParser;
import se.l4.lect.Pipeline;
import se.l4.lect.PipelineBuilder;

/**
 * Implementation of {@link PipelineBuilder}.
 *
 * @author Andreas Holstenson
 *
 */
public class PipelineBuilderImpl
	implements PipelineBuilder
{
	private final List<Function<Encounter, Handler>> handlers;
	private Function<LanguageEncounter, LanguageParser> languageCreator;

	public PipelineBuilderImpl()
	{
		handlers = new ArrayList<>();
	}

	@Override
	public PipelineBuilder language(Function<LanguageEncounter, LanguageParser> parserCreator)
	{
		this.languageCreator = parserCreator;
		return this;
	}

	@Override
	public PipelineBuilder with(Handler handler)
	{
		return with(encounter -> handler);
	}

	@Override
	public PipelineBuilder with(Function<Encounter, Handler> handler)
	{
		handlers.add(handler);
		return this;
	}

	@Override
	public Pipeline build()
	{
		return new PipelineImpl(languageCreator, handlers);
	}

}
