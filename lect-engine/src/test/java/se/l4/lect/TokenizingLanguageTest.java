package se.l4.lect;

import java.util.Locale;

import org.junit.Test;

import se.l4.lect.location.Location;
import se.l4.lect.tokens.SentenceTestTokenizer;
import se.l4.lect.tokens.TokenType;
import se.l4.lect.tokens.WhitespaceTokenizer;

/**
 * Tests for {@link TokenizingLanguage}.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenizingLanguageTest
{
	private TokenizingLanguage create(LanguageEncounter encounter)
	{
		return (TokenizingLanguage) TokenizingLanguage.create(Locale.ENGLISH, SentenceTestTokenizer::new, WhitespaceTokenizer::new, encounter);
	}

	@Test
	public void testSimple()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		TokenizingLanguage parser = create(encounter);
		parser.text("Hello world!", "Hello world!", Location.text(0, 0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(TokenType.UNKNOWN, Location.text(0, 0, 0), Location.text(5, 0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(5, 0, 5), Location.text(6, 0, 6), " ");
		encounter.verifyToken(TokenType.UNKNOWN, Location.text(6, 0, 6), Location.text(12, 0, 12), "world!");
		encounter.verifySentenceEnd(Location.text(12, 0, 12));
	}

	@Test
	public void testMultipleSentences()
	{
		VerifyingLanguageEncounter encounter = new VerifyingLanguageEncounter();
		TokenizingLanguage parser = create(encounter);
		parser.text("Hello world! Hello cookies.", "Hello world! Hello cookies.", Location.text(0, 0, 0));
		parser.flush();

		encounter.verifySentenceStart(Location.text(0, 0, 0));
		encounter.verifyToken(TokenType.UNKNOWN, Location.text(0, 0, 0), Location.text(5, 0, 5), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(5, 0, 5), Location.text(6, 0, 6), " ");
		encounter.verifyToken(TokenType.UNKNOWN, Location.text(6, 0, 6), Location.text(12, 0, 12), "world!");
		encounter.verifySentenceEnd(Location.text(12, 0, 12));
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(12, 0, 12), Location.text(13, 0, 13), " ");
		encounter.verifySentenceStart(Location.text(13, 0, 13));
		encounter.verifyToken(TokenType.UNKNOWN, Location.text(13, 0, 13), Location.text(18, 0, 18), "Hello");
		encounter.verifyToken(TokenType.WHITESPACE, Location.text(18, 0, 18), Location.text(19, 0, 19), " ");
		encounter.verifyToken(TokenType.UNKNOWN, Location.text(19, 0, 19), Location.text(27, 0, 27), "cookies.");
		encounter.verifySentenceEnd(Location.text(27, 0, 27));
	}
}
