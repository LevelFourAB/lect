package se.l4.lect;

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
	String getText();

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
	 * Get a copy of this token that can be used
	 * @return
	 */
	Token copy();
}
