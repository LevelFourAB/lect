package se.l4.lect.tokens;

import java.util.List;

import se.l4.lect.location.Location;

/**
 * Utilities for working with {@link Token}s.
 *
 * @author Andreas Holstenson
 *
 */
public class Tokens
{
	private Tokens()
	{
	}

	/**
	 * Merge the given tokens into a new token.
	 *
	 * @param tokens
	 * @return
	 */
	public static Token merge(Iterable<Token> tokens)
	{
		StringBuilder builder = new StringBuilder();
		Location start = null;
		Location end = null;

		for(Token token : tokens)
		{
			if(start == null)
			{
				start = token.getStart();
			}

			end = token.getEnd();

			builder.append(token.getText());
		}

		return new ImmutableToken(TokenType.UNKNOWN, start, end, builder.toString(), null);
	}

	public static Token merge(List<Token> tokens, int startIdx, int endIdx)
	{
		StringBuilder builder = new StringBuilder();
		Location start = tokens.get(startIdx).getStart();
		Location end = null;

		for(int i=startIdx, n=endIdx; i<n; i++)
		{
			Token token = tokens.get(i);


			if(! token.getStart().equals(end))
			{
				builder.append(' ');
			}
			builder.append(token.getText());

			end = token.getEnd();
		}

		return new ImmutableToken(TokenType.UNKNOWN, start, end, builder.toString(), null);
	}
}
