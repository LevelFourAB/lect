package se.l4.lect.tokens;

/**
 * Naive tokenizer that splits things that are separated by whitespace.
 *
 * @author Andreas Holstenson
 *
 */
public class WhitespaceTokenizer
	extends OffsetTokenizer
{
	private final CharSequence text;

	public WhitespaceTokenizer(CharSequence text)
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

		int end = TextTokenization.findWhitespace(text, start);
		if(end == -1)
		{
			end = text.length();
		}

		setNextToken(TokenType.UNKNOWN, start, text.subSequence(start, end));
		return true;
	}
}
