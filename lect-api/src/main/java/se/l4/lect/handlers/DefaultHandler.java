package se.l4.lect.handlers;

import se.l4.lect.Handler;
import se.l4.lect.Token;
import se.l4.lect.location.Location;

/**
 * Implementation of {@link Handler} that provides a starting point for implementing custom handlers.
 *
 * @author Andreas Holstenson
 *
 */
public abstract class DefaultHandler
	implements Handler
{

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
		}
	}

	@Override
	public void done()
	{
	}

	/**
	 * A word has been found in a sentence.
	 *
	 * @param word
	 */
	protected void word(Token token)
	{
	}

	/**
	 * A sequence of one or more symbols has been found in a sentence.
	 *
	 * @param symbol
	 */
	protected void symbol(Token token)
	{
	}

	/**
	 *
	 * @param whitespace
	 */
	protected void whitespace(Token token)
	{
	}
}
