package se.l4.lect;

/**
 * Language parser used for parsing contents within paragraph-level content.
 *
 * @author Andreas Holstenson
 *
 */
public interface LanguageParser
{
	/**
	 * Add some text that should be parsed.
	 *
	 * @param text
	 */
	void text(String text, Location start, Location end);

	/**
	 * Indicate that the current paragraph-level content is finished and that parsed sentences and words need to be
	 * flushed.
	 */
	void flush();
}
