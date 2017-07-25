package se.l4.lect.tokens;

/**
 * Factory for instances of {@link Tokenizer} working on {@link CharSequence}s.
 *
 * @author Andreas Holstenson
 *
 */
public interface TokenizerFactory
{
	/**
	 * Create a new {@link Tokenizer} for the given input text.
	 *
	 * @param text
	 * @return
	 */
	Tokenizer create(CharSequence text);

	/**
	 * Create a new {@link Tokenizer} for the given input token.
	 *
	 * @param token
	 * @return
	 */
	default Tokenizer create(Token token)
	{
		return create(token.getText());
	}
}
