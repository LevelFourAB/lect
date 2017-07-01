package se.l4.lect;

import java.io.IOException;

/**
 * Source abstraction, used to support different formats such as
 * plain text and HTML.
 *
 * @author Andreas Holstenson
 *
 */
public interface Source
{
	/**
	 * Parse the source using the given encounter.
	 *
	 * @param encounter
	 * @throws IOException
	 */
	void parse(SourceEncounter encounter)
		throws IOException;
}
