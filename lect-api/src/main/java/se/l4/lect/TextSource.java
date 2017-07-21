package se.l4.lect;

import java.io.IOException;

/**
 * Abstraction over sources of text, used to support different formats such as plain text and HTML.
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
}
