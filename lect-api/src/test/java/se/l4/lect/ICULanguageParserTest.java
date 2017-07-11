package se.l4.lect;

import java.util.Locale;

import org.junit.Test;

public class ICULanguageParserTest
{
	@Test
	public void testSingleText()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world!", "Hello world!", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12),  "!");
		encounter.verifySentenceEnd(Location.text(0, 12));
	}

	@Test
	public void testMultipleTextsNoGap()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello", "Hello", Location.text(0, 0));
		parser.text(" world!", " world!", Location.text(0, 5));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12), "!");
		encounter.verifySentenceEnd(Location.text(0, 12));
	}

	@Test
	public void testMultipleTextsGapBetweenTokens()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello", "Hello", Location.text(0, 0));
		parser.text(" world!", " world!", Location.text(0, 10));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 10), Location.text(0, 11), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 11), Location.text(0, 16), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 16), Location.text(0, 17), "!");
		encounter.verifySentenceEnd(Location.text(0, 17));
	}

	@Test
	public void testMultipleTextsGapInToken()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hel", "Hel", Location.text(0, 0));
		parser.text("lo world!", "lo world!", Location.text(0, 10));
		parser.flush();

		// TODO: This should verify the end of the token
		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD,Location.text(0, 0), Location.text(0, 12), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 12), Location.text(0, 13), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 13), Location.text(0, 18), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 18), Location.text(0, 19), "!");
		encounter.verifySentenceEnd(Location.text(0, 19));
	}

	@Test
	public void testSingleTextWithSourceDiffGuessing()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world!", "&#072;ello world!", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 17),  "!");
		encounter.verifySentenceEnd(Location.text(0, 17));
	}

	@Test
	public void testSingleTextWithSourceDiffProper()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("H", "&#072;", Location.text(0, 0));
		parser.text("ello world!", "ello world!", Location.text(0, 6));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 10), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 10), Location.text(0, 11), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 11), Location.text(0, 16), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 16), Location.text(0, 17), "!");
		encounter.verifySentenceEnd(Location.text(0, 17));
	}

	@Test
	public void testMultipleSentences1()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world! Hello", "Hello world! Hello", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12),  "!");
		encounter.verifySentenceEnd(Location.text(0, 12));
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 12), Location.text(0, 13), " ");
		encounter.verifySentenceStart(Location.text(0, 13));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 13), Location.text(0, 18), "Hello");
		encounter.verifySentenceEnd(Location.text(0, 18));
	}

	@Test
	public void testMultipleSentences2()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world! Hello cookies.", "Hello world! Hello cookies.", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12),  "!");
		encounter.verifySentenceEnd(Location.text(0, 12));
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 12), Location.text(0, 13), " ");
		encounter.verifySentenceStart(Location.text(0, 13));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 13), Location.text(0, 18), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 18), Location.text(0, 19), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 19), Location.text(0, 26), "cookies");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 26), Location.text(0, 27), ".");
		encounter.verifySentenceEnd(Location.text(0, 27));
	}

	@Test
	public void testMultipleSentencesWithWhitespaceBetween()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world!   Hello", "Hello world!   Hello", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12),  "!");
		encounter.verifySentenceEnd(Location.text(0, 12));
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 12), Location.text(0, 15), "   ");
		encounter.verifySentenceStart(Location.text(0, 15));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 15), Location.text(0, 20), "Hello");
		encounter.verifySentenceEnd(Location.text(0, 20));
	}

	@Test
	public void testMultipleSentencesWithWhitespaceAndGapsBetween()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world!", "Hello world!", Location.text(0, 0));
		parser.text(" ", " ", Location.text(0, 12));
		parser.text("\u00A0", "&nbsp;", Location.text(0, 13));
		parser.text("\u00A0", "&nbsp;", Location.text(0, 19));
		parser.text("Hello.", "Hello.", Location.text(0, 25));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12),  "!");
		encounter.verifySentenceEnd(Location.text(0, 12));
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 12), Location.text(0, 25), " \u00A0\u00A0");
		encounter.verifySentenceStart(Location.text(0, 25));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 25), Location.text(0, 30), "Hello");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 30), Location.text(0, 31),  ".");
		encounter.verifySentenceEnd(Location.text(0, 31));
	}

	@Test
	public void testSimpleDoubleQuotes()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("\"Hello world!\"", "\"Hello world!\"", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 0), Location.text(0, 1),  "\"");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 1), Location.text(0, 6), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 6), Location.text(0, 7), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 7), Location.text(0, 12), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 12), Location.text(0, 13),  "!");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 13), Location.text(0, 14),  "\"");
		encounter.verifySentenceEnd(Location.text(0, 14));
	}

	@Test
	public void testMultiplePeriodsAtSentenceEnd()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world...", "Hello world...", Location.text(0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0));
		encounter.verifyToken(TokenType.WORD, Location.text(0, 0), Location.text(0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(0, 5), Location.text(0, 6), " ");
		encounter.verifyToken(TokenType.WORD, Location.text(0, 6), Location.text(0, 11), "world");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 11), Location.text(0, 12),  ".");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 12), Location.text(0, 13),  ".");
		encounter.verifyToken(TokenType.SYMBOL, Location.text(0, 13), Location.text(0, 14),  ".");
		encounter.verifySentenceEnd(Location.text(0, 14));
	}
}
