package se.l4.lect;

import java.util.function.Function;

/**
 * Assembly interface for creating pipelines, used to support both {@link PipelineBuilder} and {@link PipelineRunner}.
 *
 * @author Andreas Holstenson
 *
 * @param <Self>
 */
public interface PipelineAssembly<Self extends PipelineAssembly<Self>>
{
	/**
	 * Set the function used to create the parser. Usually gotten via a static method of the instance you wish
	 * to use.
	 *
	 * <pre>
	 * builder.language(ICULanguageParser.forLocale(Locale.ENGLISH))
	 * </pre>
	 *
	 * @see ICULanguage#forLocale(java.util.Locale)
	 * @param parserCreator
	 * @return
	 */
	Self language(Function<LanguageEncounter, LanguageParser> parserCreator);

	/**
	 * Add a handler to the pipeline.
	 *
	 * @param handler
	 * @return
	 */
	Self with(Handler handler);

	/**
	 * Add a handler to the pipeline, to be constructed when processing begins.
	 *
	 * @param handler
	 * @return
	 */
	Self with(Function<Encounter, Handler> handler);
}
