package se.l4.lect.handlers;

import se.l4.lect.Handler;
import se.l4.lect.location.Location;
import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.TokenProperty;

/**
 * Implementation of {@link Handler} that provides a starting point for
 * implementing custom handlers. This handler provides methods for handling
 * different types of tokens without having to check their type.
 * 
 * @author Andreas Holstenson
 *
 */
public abstract class DefaultHandler
	implements Handler
{
	@Override
	public void start()
	{
	}

	@Override
	public void startParagraph(Location location)
	{
	}

	@Override
	public void endParagraph(Location location)
	{
	}

	@Override
	public void startSentence(Location location)
	{
	}

	@Override
	public void endSentence(Location location)
	{
	}

	@Override
	public void startAttribute(Location location, TokenProperty<?> property, Object value)
	{
	}

	@Override
	public void endAttribute(Location location, TokenProperty<?> property)
	{
	}

	@Override
	public void token(Token token)
	{
		switch(token.getType())
		{
			case SYMBOL:
				symbol(token);
				break;
			case WHITESPACE:
				whitespace(token);
				break;
			case WORD:
				word(token);
				break;
			case SPECIAL:
				special(token);
				break;
			case UNKNOWN:
				break;
		}
	}

	@Override
	public void done()
	{
	}

	/**
	 * A word has been found in a sentence. Tokens should be copied if their
	 * contents should outlive the scope of this method.
	 *
	 * @param token
	 *   the token representing the word.
	 */
	protected void word(Token token)
	{
	}

	/**
	 * A sequence of one or more symbols has been found in a sentence. Tokens
	 * should be copied if their contents should outlive the scope of this
	 * method.
	 *
	 * @param token
	 *   the token representing the symbol.
	 */
	protected void symbol(Token token)
	{
	}

	/**
	 * Whitespace either within our outside a sentence. Tokens should be copied
	 * if their contents should outlive the scope of this method.
	 * 
	 * @param token
	 */
	protected void whitespace(Token token)
	{
	}

	/**
	 * Special token has been found. Tokens should be copied if their contents
	 * should outlive the scope of this method.
	 * 
	 * @param token
	 */
	protected void special(Token token)
	{
	}

	/**
	 * Unknown token has been found, uncommon case as most language
	 * implementations will not emit unknown tokens. Tokens should be copied
	 * if their contents should outlive the scope of this method.
	 */
	protected void unknown(Token token)
	{
	}
}
