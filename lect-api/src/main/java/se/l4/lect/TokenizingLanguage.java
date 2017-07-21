package se.l4.lect;

import java.util.Locale;
import java.util.function.Function;

import se.l4.lect.location.Location;
import se.l4.lect.location.OffsetLocation;
import se.l4.lect.tokens.TokenizerFactory;
import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.Tokenizer;

/**
 * Implementation of {@link LanguageParser} that uses {@link Tokenizer}s to split text into sentences and tokens.
 * The tokenizers used should use {@link OffsetLocation} for its tokens so that the language can correctly map
 * the locations of the tokens to the actual location in the {@link Source}.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenizingLanguage
	extends ChunkedLanguageParser
{
	private final Locale locale;
	private final TokenizerFactory paragraphTokenizers;
	private final TokenizerFactory sentenceTokenizers;

	protected TokenizingLanguage(
			Locale locale,
			TokenizerFactory paragraphTokenizers,
			TokenizerFactory sentenceTokenizers,
			LanguageEncounter encounter)
	{
		super(encounter);

		this.locale = locale;
		this.paragraphTokenizers = paragraphTokenizers;
		this.sentenceTokenizers = sentenceTokenizers;
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
		Tokenizer sentences = paragraphTokenizers.create(sequence);
		while(sentences.hasNext())
		{
			Token sentence = sentences.next();
			int offset = toOffset(sentence.getStart());

			// Start the sentence
			startSentence(offset);

			// Inner: Tokenize all of the individual words/symbols/etc in the sentence
			Tokenizer tokens = sentenceTokenizers.create(sentence);
			while(tokens.hasNext())
			{
				Token token = tokens.next();

				int start = toOffset(token.getStart());
				int end = toOffset(token.getEnd());
				emitToken(offset + start, token.getType(), end - start);
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
	 *   the {@link Locale} that the parser is for
	 * @param paragraphTokenizers
	 *   the tokenizer used to tokenize a paragraph into sentences. The tokenizer must use {@link OffsetLocation} for
	 *   its tokens.
	 * @param sentenceTokenizers
	 *   the tokenizer used to tokenize a sentence into individual tokens. The tokenizer must use {@link OffsetLocation}
	 *   for its tokens.
	 * @param encounter
	 *   the encounter that should receive results
	 * @return
	 *   instance of {@link TokenizingLanguage}
	 */
	public static LanguageParser create(
			Locale locale,
			TokenizerFactory paragraphTokenizers,
			TokenizerFactory sentenceTokenizers,
			LanguageEncounter encounter)
	{
		return new TokenizingLanguage(locale, paragraphTokenizers, sentenceTokenizers, encounter);
	}

	/**
	 * Create a {@link Function} that can create a parser for the given locale.
	 *
	 * @param locale
	 *   the {@link Locale} that the parser is for
	 * @param paragraphTokenizers
	 *   the tokenizer used to tokenize a paragraph into sentences. The tokenizer must use {@link OffsetLocation} for
	 *   its tokens.
	 * @param sentenceTokenizers
	 *   the tokenizer used to tokenize a sentence into individual tokens. The tokenizer must use {@link OffsetLocation}
	 *   for its tokens.
	 * @return
	 *   function that creates a {@link TokenizingLanguage} for a given {@link LanguageEncounter}
	 */
	public static LanguageFactory create(Locale locale,
			TokenizerFactory paragraphTokenizers,
			TokenizerFactory sentenceTokenizers)
	{
		return encounter -> create(locale, paragraphTokenizers, sentenceTokenizers, encounter);
	}
}
