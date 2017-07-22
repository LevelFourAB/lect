package se.l4.lect.tokens;

import java.util.Map;

import se.l4.lect.location.Location;

/**
 * Token containing some text that is either words, symbols or whitespace.
 *
 * @author Andreas Holstenson
 *
 */
public interface Token
{
	/**
	 * Get the type of token.
	 *
	 * @return
	 */
	TokenType getType();

	/**
	 * Get the text of the token.
	 *
	 * @return
	 */
	CharSequence getText();

	/**
	 * Get where this token starts.
	 *
	 * @return
	 */
	Location getStart();

	/**
	 * Get where this token ends.
	 *
	 * @return
	 */
	Location getEnd();

	/**
	 * Get some optional data for this token.
	 *
	 * @param property
	 */
	<T> T get(TokenProperty<T> property);

	/**
	 * Get if this token has the given data.
	 *
	 * @param property
	 * @return
	 */
	boolean has(TokenProperty<?> property);

	/**
	 * Get properties as a {@link Map}.
	 *
	 * @return
	 */
	Map<String, Object> getProperties();

	/**
	 * Get a copy of this token that can be used
	 * @return
	 */
	Token copy();
}
