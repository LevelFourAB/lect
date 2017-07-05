package se.l4.lect;

import java.util.Locale;

/**
 * Language parser used for parsing contents within paragraph-level content.
 *
 * @author Andreas Holstenson
 *
 */
public interface LanguageParser
{
	/**
	 * Get the locale of this parser.
	 *
	 * @return
	 */
	Locale locale();

	/**
	 * Add some text that should be parsed.
	 *
	 * @param text
	 */
	void text(CharSequence text, Location start, Location end);

	/**
	 * Indicate that the current paragraph-level content is finished and that parsed sentences and words need to be
	 * flushed.
	 */
	void flush();
}
