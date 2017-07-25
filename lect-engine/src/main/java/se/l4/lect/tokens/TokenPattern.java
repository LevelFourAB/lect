package se.l4.lect.tokens;

import java.util.regex.Pattern;

import se.l4.lect.internal.tokens.TokenPatternImpl;

/**
 * Pattern for matching a sequence of tokens. Patterns work similar to how a {@link Pattern regular expression pattern}
 * does, but match on {@link Token tokens} and their properties.
 *
 * <p>
 * Patterns are compiled via {@link #compile(String)} and follow a simple format. A token is matched using it's type
 * or the special type {@code any} and a sequence of tokens are separated by a space. For example
 * {@code word symbol} would match {@link TokenType#WORD word} followed by a {@link TokenType#SYMBOL symbol}.
 *
 * <p>
 * Tokens can also match on its text or {@link TokenProperty properties}:
 *
 * <pre>
 * // Match any token
 * TokenPattern.compile("any");
 * // Match a word
 * TokenPattern.compile("word");
 * // Match against token.getText()
 * TokenPattern.compile("word='Test'");
 * // Shortcut to match the text of any type of token
 * TokenPattern.compile("'Test'");
 * // Match against TokenProperty.NORMALIZED
 * TokenPattern.compile("word,normalized='test'");
 * // Match word followed by symbol
 * TokenPattern.compile("word symbol")
 * // Match against regular expression
 * TokenPattern.compile("word=/test/i");
 * // Shortcut to match via regex for any type of token
 * TokenPattern.compile("/test/i");
 * </pre>
 *
 * <p>
 * By default whitespace tokens are ignored, to enable whitespace matching use {@link #WITH_WHITESPACE}:
 *
 * <pre>
 * TokenPattern.compile("word whitespace symbol", TokenPattern.WITH_WHITESPACE);
 * </pre>
 *
 * <p>
 * Tokens can be set to optional or to not match:
 *
 * <pre>
 * // Optional symbol followed by a word
 * TokenPattern.compile("symbol? word");
 * // Words that are not preceded by a symbol
 * TokenPattern.compile("!symbol word");
 * // Match the token at least once
 * TokenPattern.compile("symbol='$'+ word");
 * // Match the token zero or more times
 * TokenPattern.compile("symbol* word");
 * // Match the token twice
 * TokenPattern.compile("symbol{2} word");
 * // Match the token between one and five times
 * TokenPattern.compile("symbol,normalized='#'{1,5} word");
 * // Match the token zero to five times
 * TokenPattern.compile("symbol{,5} word");
 * // Match the token two or more times
 * TokenPattern.compile("symbol{2,} word");
 * </pre>
 *
 * <p>
 * Groups are also supported:
 *
 * <pre>
 * // Use parenthesis to create an optional group of Mrs + period
 * TokenPattern.compile("(word,normalized='mrs' symbol,text='.',continuation)? word");
 * // Use brackets to create an OR between tokens or groups
 * TokenPattern.compile("[word,normalized='mrs' word,normalized='mr'] symbol,text='.',continuation?");
 * </pre>
 *
 * @author Andreas Holstenson
 *
 */
public interface TokenPattern
{
	static final int WITH_WHITESPACE = 1;

	/**
	 * Create a new matcher for streaming matching.
	 *
	 * @return
	 */
	TokenMatcher matcher();

	/**
	 * Compile a pattern that can be used to match tokens.
	 *
	 * @param pattern
	 * @return
	 */
	static TokenPattern compile(String pattern)
	{
		return compile(pattern, 0);
	}

	/**
	 * Compile a pattern that can be used to match tokens.
	 *
	 * @param pattern
	 * @param flags
	 * @return
	 */
	static TokenPattern compile(String pattern, int flags)
	{
		return TokenPatternImpl.compile(pattern, flags);
	}
}
