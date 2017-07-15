package se.l4.lect.tokens;

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
		start++;
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
		start++;
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
