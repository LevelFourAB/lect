package se.l4.lect.tokens;

import se.l4.lect.location.MutableOffsetLocation;
import se.l4.lect.location.OffsetLocation;

/**
 * Tokenizer that tokenizes using {@link OffsetLocation}s.
 *
 * @author Andreas Holstenson
 *
 */
public abstract class OffsetTokenizer
	implements Tokenizer
{
	private MutableOffsetLocation start;
	private MutableOffsetLocation end;
	private MutableOffsetLocation pendingStart;
	private MutableOffsetLocation pendingEnd;

	private MutableToken token;
	private MutableToken pendingToken;

	private boolean init;

	public OffsetTokenizer()
	{
		start = new MutableOffsetLocation();
		end = new MutableOffsetLocation();
		pendingStart = new MutableOffsetLocation();
		pendingEnd = new MutableOffsetLocation();

		pendingToken = new MutableToken();
		token = new MutableToken();
	}

	@Override
	public boolean hasNext()
	{
		if(! init)
		{
			findNextToken(0);
			init = true;
		}

		return pendingToken != null;
	}

	@Override
	public Token next()
	{
		if(token == null)
		{
			throw new IllegalStateException("No more tokens");
		}

		if(! init)
		{
			findNextToken(0);
			init = true;
		}

		// Swap the tokens and location
		int previousEnd = pendingEnd.get();
		swap();

		if(! findNextToken(previousEnd))
		{
			pendingToken = null;
		}

		return token;
	}

	/**
	 * Find the next token.
	 * 
	 * @param previousEnd
	 *   the offset at which the previous token ended
	 * @return
	 *   {@code true} if a token was found, {@code false} if no more tokens
	 *   are available.
	 */
	protected abstract boolean findNextToken(int previousEnd);

	/**
	 * Get the token that has been set by the last call to
	 * {@link #setNextToken(TokenType, int, CharSequence)}.
	 *
	 * @return
	 */
	protected MutableToken nextToken()
	{
		return pendingToken;
	}

	/**
	 * Set the next token. Should be called by {@link #findNextToken(int)} to
	 * update information about the token that has been found.
	 */
	protected void setNextToken(TokenType type, int start, CharSequence text)
	{
		if(text.length() == 0)
		{
			throw new IllegalStateException("Empty token encountered at @" + start);
		}

		this.pendingStart.set(start);
		this.pendingEnd.set(start + text.length());
		pendingToken.update(type, this.pendingStart, this.pendingEnd, text);
	}

	/**
	 * Enhance the next token. Can be called after
	 * {@link #setNextToken(TokenType, int, CharSequence)} to enhance the token
	 * with properties.
	 */
	protected <T> void enhanceNextToken(TokenProperty<T> property, T value)
	{
		pendingToken.set(property, value);
	}

	private void swap()
	{
		MutableToken tempToken = pendingToken;
		pendingToken = token;
		token = tempToken;

		MutableOffsetLocation temp;

		temp = start;
		start = pendingStart;
		pendingStart = temp;

		temp = end;
		end = pendingEnd;
		pendingEnd = temp;
	}
}
