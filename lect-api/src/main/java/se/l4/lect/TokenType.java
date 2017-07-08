package se.l4.lect;

/**
 * Enumeration for the different types of tokens extracted from text. These are limited to three categories, white
 * space, symbols and words.
 *
 * @author Andreas Holstenson
 *
 */
public enum TokenType
{
	/**
	 * The token represents a word.
	 */
	WORD,

	/**
	 * The token represents one or more symbols.
	 */
	SYMBOL,

	/**
	 * The token represents white space.
	 */
	WHITESPACE
}