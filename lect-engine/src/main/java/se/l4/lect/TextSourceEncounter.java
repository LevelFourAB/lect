package se.l4.lect;

import java.util.Locale;

import se.l4.lect.location.Location;
import se.l4.lect.tokens.TokenProperty;

/**
 * Encounter given to {@link TextSource}s when they should parse their content.
 * Sources should use {@link #location(Location)} to update the location of
 * things before calling methods such as {@link #startParagraph()}, 
 * {@link #text(CharSequence, Location)} and {@link #endParagraph()}.
 * 
 * <p>
 * Sources may set attributes on tokens emitted via {@link #setAttribute(TokenProperty, Object)}
 * and {@link #clearAttribute(TokenProperty)}.
 * 
 * @author Andreas Holstenson
 */
public interface TextSourceEncounter
{
	/**
	 * Get the locale we are building the tree for.
	 *
	 * @return
	 */
	Locale locale();

	/**
	 * Get the currently set location.
	 *
	 * @return
	 */
	Location location();

	/**
	 * Set the location of the next event.
	 *
	 * @param location
	 */
	void location(Location location);

	/**
	 * Check if we are currently in a paragraph.
	 *
	 * @return
	 */
	boolean inParagraph();

	/**
	 * Start new paragraph-level content.
	 */
	void startParagraph();

	/**
	 * End current paragraph-level content.
	 */
	void endParagraph();

	/**
	 * Indicate that we are done parsing.
	 */
	void done();

	/**
	 * Add some text. If the text is outside a paragraph this method will only
	 * accept whitespace. If it is within a paragraph the text will
	 * automatically be processed by the {@link LanguageParser} and split into
	 * sentences and tokens such as words, symbols and whitespace.
	 *
	 * <p>
	 * All of the text in a paragraph does not need to be added at once, it is
	 * possible to add it in chunks. For example a HTML parser will decode
	 * entities and will want update the source location as it does this.
	 *
	 * @param text
	 *   the text to add
	 * @param end
	 *   the location at which the text ends
	 */
	void text(CharSequence text, Location end);

	/**
	 * Set an attribute at the current location.
	 *
	 * @param attribute
	 * @param value
	 */
	<T> void setAttribute(TokenProperty<T> attribute, T value);

	/**
	 * Clear an attribute.
	 *
	 * @param attribute
	 */
	void clearAttribute(TokenProperty<?> attribute);
}
