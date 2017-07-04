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
		parser.text("Hello world!", "Hello world!", Location.text(0, 0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 0, 0), Location.text(0, 5, 5), "Hello");
		encounter.verifyToken(Token.Type.WHITESPACE, Location.text(0, 5, 5), Location.text(0, 6, 6), " ");
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 6, 6), Location.text(0, 11, 11), "world");
		encounter.verifyToken(Token.Type.SYMBOL, Location.text(0, 11, 11), Location.text(0, 12, 12),  "!");
		encounter.verifySentenceEnd(Location.text(0, 12, 12));
	}

	@Test
	public void testMultipleTextsNoGap()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello", "Hello", Location.text(0, 0, 0));
		parser.text(" world!", " world!", Location.text(0, 5, 5));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 0, 0), Location.text(0, 5, 5), "Hello");
		encounter.verifyToken(Token.Type.WHITESPACE, Location.text(0, 5, 5), Location.text(0, 6, 6), " ");
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 6, 6), Location.text(0, 11, 11), "world");
		encounter.verifyToken(Token.Type.SYMBOL, Location.text(0, 11, 11), Location.text(0, 12, 12), "!");
		encounter.verifySentenceEnd(Location.text(0, 12, 12));
	}

	@Test
	public void testMultipleTextsGapBetweenTokens()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello", "Hello", Location.text(0, 0, 0));
		parser.text(" world!", " world!", Location.text(0, 10, 10));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 0, 0), Location.text(0, 5, 5), "Hello");
		encounter.verifyToken(Token.Type.WHITESPACE, Location.text(0, 10, 10), Location.text(0, 11, 11), " ");
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 11, 11), Location.text(0, 16, 16), "world");
		encounter.verifyToken(Token.Type.SYMBOL, Location.text(0, 16, 16), Location.text(0, 17, 17), "!");
		encounter.verifySentenceEnd(Location.text(0, 17, 17));
	}

	@Test
	public void testMultipleTextsGapInToken()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hel", "Hel", Location.text(0, 0, 0));
		parser.text("lo world!", "lo world!", Location.text(0, 10, 10));
		parser.flush();

		// TODO: This should verify the end of the token
		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(Token.Type.WORD,Location.text(0, 0, 0), Location.text(0, 12, 12), "Hello");
		encounter.verifyToken(Token.Type.WHITESPACE, Location.text(0, 12, 12), Location.text(0, 13, 13), " ");
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 13, 13), Location.text(0, 18, 18), "world");
		encounter.verifyToken(Token.Type.SYMBOL, Location.text(0, 18, 18), Location.text(0, 19, 19), "!");
		encounter.verifySentenceEnd(Location.text(0, 19, 19));
	}

	@Test
	public void testSingleTextWithSourceDiffGuessing()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("Hello world!", "&#072;ello world!", Location.text(0, 0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 0, 0), Location.text(0, 5, 5), "Hello");
		encounter.verifyToken(Token.Type.WHITESPACE, Location.text(0, 5, 5), Location.text(0, 6, 6), " ");
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 6, 6), Location.text(0, 11, 11), "world");
		encounter.verifyToken(Token.Type.SYMBOL, Location.text(0, 11, 11), Location.text(0, 17, 17),  "!");
		encounter.verifySentenceEnd(Location.text(0, 17, 17));
	}

	@Test
	public void testSingleTextWithSourceDiffProper()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		ICULanguage parser = new ICULanguage(Locale.ENGLISH, encounter);
		parser.text("H", "&#072;", Location.text(0, 0, 0));
		parser.text("ello world!", "ello world!", Location.text(0, 6, 6));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 0, 0), Location.text(0, 10, 10), "Hello");
		encounter.verifyToken(Token.Type.WHITESPACE, Location.text(0, 10, 10), Location.text(0, 11, 11), " ");
		encounter.verifyToken(Token.Type.WORD, Location.text(0, 11, 11), Location.text(0, 16, 16), "world");
		encounter.verifyToken(Token.Type.SYMBOL, Location.text(0, 16, 16), Location.text(0, 17, 17), "!");
		encounter.verifySentenceEnd(Location.text(0, 17, 17));
	}
}
