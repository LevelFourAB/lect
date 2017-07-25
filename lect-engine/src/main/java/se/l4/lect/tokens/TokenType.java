package se.l4.lect.tokens;

/**
 * Enumeration for the different types of tokens extracted from text. These are limited to four categories, white
 * space, symbols, words and special tokens.
 *
 * @author Andreas Holstenson
 *
 */
public enum TokenType
{
	/**
	 * The type of token is unknown. Usually used with a {@link Tokenizer} where the consumer should handle the
	 * resolving the type of token.
	 */
	UNKNOWN,

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
	WHITESPACE,

	/**
	 * The token represents special tokens, such as URLs, e-mails and phone numbers.
	 */
	SPECIAL;
}