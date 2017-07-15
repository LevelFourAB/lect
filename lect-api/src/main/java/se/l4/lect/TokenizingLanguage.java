package se.l4.lect;

import java.util.Locale;
import java.util.function.Function;

import se.l4.lect.location.Location;
import se.l4.lect.location.OffsetLocation;
import se.l4.lect.tokens.TextTokenizerFactory;
import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.Tokenizer;

/**
 * Implementation of {@link LanguageParser} that uses {@link Tokenizer}s to split text into sentences and tokens.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenizingLanguage
	extends ChunkedLanguageParser
{
	private final Locale locale;
	private final TextTokenizerFactory sentenceTokenizers;
	private final TextTokenizerFactory wordTokenizers;

	protected TokenizingLanguage(
			Locale locale,
			TextTokenizerFactory sentenceTokenizers,
			TextTokenizerFactory wordTokenizers,
			LanguageEncounter encounter)
	{
		super(encounter);

		this.locale = locale;
		this.sentenceTokenizers = sentenceTokenizers;
		this.wordTokenizers = wordTokenizers;
	}

	@Override
	public Locale locale()
	{
		return locale;
	}

	@Override
	protected void handleChunk(CharSequence sequence)
	{
		// Outer: Tokenize and find the sentences
		Tokenizer sentences = sentenceTokenizers.create(sequence);
		while(sentences.hasNext())
		{
			Token sentence = sentences.next();
			int offset = toOffset(sentence.getStart());

			// Start the sentence
			startSentence(offset);

			// Inner: Tokenize all of the individual words/symbols/etc in the sentence
			Tokenizer words = wordTokenizers.create(sentence);
			while(words.hasNext())
			{
				Token word = words.next();

				int start = toOffset(word.getStart());
				int end = toOffset(word.getEnd());
				emitToken(offset + start, word.getType(), end - start);
			}

			// End the sentence
			endSentence(toOffset(sentence.getEnd()));
		}
	}

	private int toOffset(Location location)
	{
		if(location instanceof OffsetLocation)
		{
			return ((OffsetLocation) location).get();
		}

		throw new IllegalArgumentException("Can not turn " + location + " into an offset");
	}

	/**
	 * Create an instance for the given locale and tokenizers.
	 *
	 * @param locale
	 * @param sentenceTokenizers
	 * @param wordTokenizers
	 * @param encounter
	 * @return
	 */
	public static LanguageParser create(
			Locale locale,
			TextTokenizerFactory sentenceTokenizers,
			TextTokenizerFactory wordTokenizers,
			LanguageEncounter encounter)
	{
		return new TokenizingLanguage(locale, sentenceTokenizers, wordTokenizers, encounter);
	}

	/**
	 * Create a {@link Function} that can create a parser for the given locale.
	 *
	 * @param locale
	 * @param sentenceTokenizers
	 * @param wordTokenizers
	 * @return
	 */
	public static Function<LanguageEncounter, LanguageParser> create(Locale locale,
			TextTokenizerFactory sentenceTokenizers,
			TextTokenizerFactory wordTokenizers)
	{
		return encounter -> create(locale, sentenceTokenizers, wordTokenizers, encounter);
	}
}
