package se.l4.lect;

import java.io.IOException;

import se.l4.lect.internal.EmptyTextSource;

/**
 * Abstraction over sources of text, used to support different formats such as
 * plain text and HTML.
 *
 * @author Andreas Holstenson
 *
 */
public interface TextSource
{
	/**
	 * Parse the source using the given encounter.
	 *
	 * @param encounter
	 * @throws IOException
	 */
	void parse(TextSourceEncounter encounter)
		throws IOException;

	/**
	 * Start building a pipeline to process contents in this source.
	 *
	 * @return
	 */
	default PipelineRunner<?> process()
	{
		return Pipeline.over(this);
	}

	/**
	 * Get a {@link TextSource} that is empty.
	 *
	 * @return
	 */
	public static TextSource empty()
	{
		return EmptyTextSource.INSTANCE;
	}
}
