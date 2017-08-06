package se.l4.lect;

/**
 * Assembly interface for creating pipelines, used to support both {@link PipelineBuilder} and {@link PipelineRunner}.
 *
 * @author Andreas Holstenson
 *
 * @param <Self>
 */
public interface PipelineAssembly<Collector, Self extends PipelineAssembly<Collector, Self>>
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
	 * @param factory
	 * @return
	 */
	Self language(LanguageFactory factory);

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
	Self with(HandlerFactory<Collector> handler);
}
