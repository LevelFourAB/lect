package se.l4.lect;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import se.l4.lect.internal.PipelineBuilderImpl;

/**
 * Pipeline over a {@link Source} that will run {@link Handler}s on the text in the source.
 *
 * @author Andreas Holstenson
 *
 */
public interface Pipeline
{
	/**
	 * Run this pipeline on the given source.
	 *
	 * @param source
	 * @throws IOException
	 */
	List<Object> run(Source source)
		throws IOException;

	/**
	 * Run this pipeline on the given source and collect results using the given collector.
	 *
	 * @param source
	 * @param collector
	 * @throws IOException
	 */
	void run(Source source, Consumer<Object> collector)
		throws IOException;

	/**
	 * Start creating a reusable pipeline.
	 *
	 * @return
	 */
	static PipelineBuilder newBuilder()
	{
		return new PipelineBuilderImpl();
	}

	/**
	 * Start creating a pipeline with the intention of executing over the given source.
	 *
	 * @param source
	 * @return
	 */
	static PipelineRunner over(Source source)
	{
		return new PipelineRunner(source);
	}
}
