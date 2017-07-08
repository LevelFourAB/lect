package se.l4.lect;

import java.util.Locale;
import java.util.function.Function;

import com.ibm.icu.text.BreakIterator;

/**
 * {@link LanguageParser} that uses {@link BreakIterator} from ICU4J to split text into sentences and tokens.
 *
 * @author Andreas Holstenson
 *
 */
public class ICULanguage
	extends ChunkedLanguageParser
{
	private final Locale locale;

	public ICULanguage(Locale locale, LanguageEncounter encounter)
	{
		super(encounter);
		this.locale = locale;
	}

	/**
	 * Get a factory that will create a language parser for the given {@link Locale}.
	 *
	 * @param locale
	 * @return
	 */
	public static Function<LanguageEncounter, LanguageParser> forLocale(Locale locale)
	{
		return encounter -> new ICULanguage(locale, encounter);
	}

	@Override
	public Locale locale()
	{
		return locale;
	}

	@Override
	protected void handleChunk(CharSequence sequence)
	{
		BreakIterator sentences = BreakIterator.getSentenceInstance(locale);
		BreakIterator words = BreakIterator.getWordInstance(locale);

		String string = sequence.toString();
		sentences.setText(string);
		words.setText(string);

		int lastOffset = sentences.first();
		if(lastOffset > 0)
		{
			// Emit whitespace between sentences
			emitToken(0, TokenType.WHITESPACE, string.substring(0, lastOffset));
		}

		while(sentences.next() != BreakIterator.DONE)
		{
			int offset = sentences.current();
			startSentence(lastOffset);

			words.preceding(lastOffset);
			int startOfWord = words.current();
			while(startOfWord < lastOffset)
			{
				startOfWord = words.next();
			}

			boolean endedSentence = false;
			while(words.next() != BreakIterator.DONE)
			{
				int endOfWord = words.current();
				if(endOfWord > offset) break;

				TokenType type = TokenType.WORD;
				String value = string.substring(startOfWord, endOfWord);
				if(isWhitespace(value))
				{
					type = TokenType.WHITESPACE;

					if(endOfWord == offset)
					{
						// If this is some whitespace at the end of the sentence, treat it as outside the sentence
						endSentence(startOfWord);
						endedSentence = true;
					}
				}
				else if(isSymbol(value))
				{
					type = TokenType.SYMBOL;
				}
				emitToken(startOfWord, type, value);

				startOfWord = endOfWord;
			}

			if(! endedSentence)
			{
				endSentence(offset);
			}
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
