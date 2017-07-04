package se.l4.lect;

import java.util.Locale;

import com.ibm.icu.text.BreakIterator;

/**
 * {@link LanguageParser} that uses {@link BreakIterator} from ICU4J to split text into sentences and tokens.
 *
 * @author Andreas Holstenson
 *
 */
public class ICULanguageParser
	extends ChunkedLanguageParser
{
	private final Locale locale;

	public ICULanguageParser(Locale locale, LanguageEncounter encounter)
	{
		super(encounter);
		this.locale = locale;
	}

	@Override
	protected void handleChunk(CharSequence sequence)
	{
		BreakIterator sentences = BreakIterator.getSentenceInstance(locale);
		BreakIterator words = BreakIterator.getWordInstance(locale);

		String string = sequence.toString();
		sentences.setText(string);
		words.setText(string);

		int lastOffset = 0;
		while(sentences.next() != BreakIterator.DONE)
		{
			int offset = sentences.current();
			startSentence(lastOffset);

			words.preceding(lastOffset);
			int startOfWord = words.current();
			while(words.next() != BreakIterator.DONE)
			{
				int endOfWord = words.current();
				if(endOfWord > offset) break;

				Token.Type type = Token.Type.WORD;
				String value = string.substring(startOfWord, endOfWord);
				if(isWhitespace(value))
				{
					type = Token.Type.WHITESPACE;
				}
				else if(isSymbol(value))
				{
					type = Token.Type.SYMBOL;
				}
				emitToken(startOfWord, type, value);

				startOfWord = endOfWord;
			}

			endSentence(offset);
			lastOffset = offset;
		}
	}

	private boolean isSymbol(String value)
	{
		for(int i=0, n=value.length(); i<n; i++)
		{
			if(Character.isLetterOrDigit(value.charAt(i))) return false;
		}
		return true;
	}

	private boolean isWhitespace(String value)
	{
		for(int i=0, n=value.length(); i<n; i++)
		{
			if(! Character.isWhitespace(value.charAt(i))) return false;
		}
		return true;
	}
}
