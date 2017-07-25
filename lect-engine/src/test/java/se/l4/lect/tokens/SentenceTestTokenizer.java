package se.l4.lect.tokens;

/**
 * Naive tokenizer for sentences that only looks for periods, question marks and exclamation marks. Should not be used
 * for anything other than testing.
 *
 * @author Andreas Holstenson
 *
 */
public class SentenceTestTokenizer
	extends OffsetTokenizer
{
	private final CharSequence text;

	public SentenceTestTokenizer(CharSequence text)
	{
		this.text = text;
	}

	@Override
	protected boolean findNextToken(int previousEnd)
	{
		if(previousEnd == text.length())
		{
			return false;
		}

		int start = TextTokenization.findNonWhitespace(text, previousEnd);
		if(start == -1) return false;

		int end = start;
		for(int n=text.length(); end<n; end++)
		{
			char c = text.charAt(end);
			if(c == '.' || c == '?' || c == '!')
			{
				end++;
				break;
			}
		}

		setNextToken(TokenType.SPECIAL, start, text.subSequence(start, end));
		return true;
	}
}
