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

	protected abstract boolean findNextToken(int previousEnd);

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
