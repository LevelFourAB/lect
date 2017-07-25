package se.l4.lect.tokens;

import se.l4.lect.CharPredicate;

/**
 * Utilities to help with some text tokenization tasks.
 *
 * @author Andreas Holstenson
 *
 */
public class TextTokenization
{
	private TextTokenization()
	{
	}

	/**
	 * Find the best token type for the given text.
	 *
	 * @param text
	 * @return
	 */
	public static TokenType findBestTokenType(CharSequence text)
	{
		boolean allSymbols = true;
		boolean allWhitespace = true;
		for(int i=0, n=text.length(); i<n; i++)
		{
			char c = text.charAt(i);
			boolean isWhitespace = Character.isWhitespace(c) || c == '\u00A0' /* Non-breaking space */;
			if(allWhitespace && ! isWhitespace)
			{
				allWhitespace = false;
			}

			if(allSymbols && (Character.isLetterOrDigit(c) || isWhitespace))
			{
				allSymbols = false;
			}
		}

		if(allSymbols)
		{
			return TokenType.SYMBOL;
		}
		else if(allWhitespace)
		{
			return TokenType.WHITESPACE;
		}
		else
		{
			return TokenType.WORD;
		}
	}

	/**
	 * Find the index after or at the given start that matches the predicate.
	 *
	 * @param text
	 * @param start
	 * @param predicate
	 * @return
	 */
	public static int find(CharSequence text, int start, CharPredicate predicate)
	{
		for(int n=text.length(); start<n; start++)
		{
			if(predicate.test(text.charAt(start)))
			{
				return start;
			}
		}

		return -1;
	}

	/**
	 * Find the index after the given start that matches the predicate.
	 *
	 * @param text
	 * @param start
	 * @param predicate
	 * @return
	 */
	public static int findNext(CharSequence text, int start, CharPredicate predicate)
	{
		return find(text, start + 1, predicate);
	}

	/**
	 * Find the index where the first non-whitespace character is available, either at the given start of after it.
	 *
	 * @param text
	 *   text to search in
	 * @param start
	 *   starting offset
	 * @return
	 *   offset if found, or {@code -1} if not found
	 */
	public static int findNonWhitespace(CharSequence text, int start)
	{
		for(int n=text.length(); start<n; start++)
		{
			if(! Character.isWhitespace(text.charAt(start)))
			{
				return start;
			}
		}

		return -1;
	}

	/**
	 * Find the index where the first whitespace character is available, either at the given start of after it.
	 *
	 * @param text
	 *   text to search in
	 * @param start
	 *   starting offset
	 * @return
	 *   offset if found, or {@code -1} if not found
	 */
	public static int findWhitespace(CharSequence text, int start)
	{
		for(int n=text.length(); start<n; start++)
		{
			if(Character.isWhitespace(text.charAt(start)))
			{
				return start;
			}
		}

		return -1;
	}

	/**
	 * Find the index of the first non-whitespace character following the given start index.
	 *
	 * @param text
	 *   text to search in
	 * @param start
	 *   starting offset
	 * @return
	 *   offset if found, or {@code -1} if not found
	 */
	public static int findNextNonWhitespace(CharSequence text, int start)
	{
		return findNonWhitespace(text, start + 1);
	}

	/**
	 * Find the index of the first whitespace character following the given start index.
	 *
	 * @param text
	 *   text to search in
	 * @param start
	 *   starting offset
	 * @return
	 *   offset if found, or {@code -1} if not found
	 */
	public static int findNextWhitespace(CharSequence text, int start)
	{
		return findWhitespace(text, start + 1);
	}

	/**
	 * Find the index of the first non-whitespace character preceding the given start index.
	 *
	 * @param text
	 *   text to search in
	 * @param start
	 *   starting offset
	 * @return
	 *   offset if found, or {@code -1} if not found
	 */
	public static int findPreviousNonWhitespace(CharSequence text, int start)
	{
		start--;
		for(int n=text.length(); start<n; start++)
		{
			if(! Character.isWhitespace(text.charAt(start)))
			{
				return start;
			}
		}

		return -1;
	}

	/**
	 * Find the index of the first whitespace character preceding the given start index.
	 *
	 * @param text
	 *   text to search in
	 * @param start
	 *   starting offset
	 * @return
	 *   offset if found, or {@code -1} if not found
	 */
	public static int findPreviousWhitespace(CharSequence text, int start)
	{
		start--;
		for(int n=text.length(); start<n; start++)
		{
			if(Character.isWhitespace(text.charAt(start)))
			{
				return start;
			}
		}

		return -1;
	}
}
