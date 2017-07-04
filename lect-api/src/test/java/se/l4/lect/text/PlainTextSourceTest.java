package se.l4.lect.text;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import se.l4.lect.Location;
import se.l4.lect.VerifyingSyntaxTreeEncounter;

public class PlainTextSourceTest
{
	@Test
	public void testSingeLine()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 0, 0), Location.text(0, 12, 12));
	}

	@Test
	public void testParagraphWithLineBreak1()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\nworld!").parse(mock);
		mock.verifyParagraph("Hello\nworld!", Location.text(0, 0, 0), Location.text(1, 6, 12));
	}

	@Test
	public void testParagraphWithLineBreak2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\r\nworld!").parse(mock);
		mock.verifyParagraph("Hello\r\nworld!", Location.text(0, 0, 0), Location.text(1, 6, 13));
	}

	@Test
	public void testParagraphWithLineBreak3()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n world!").parse(mock);
		mock.verifyParagraph("Hello\n world!", Location.text(0, 0, 0), Location.text(1, 7, 13));
	}

	@Test
	public void testMultipleParagraphs()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies").parse(mock);
		mock.verifyParagraph("Hello\n", Location.text(0, 0, 0), Location.text(1, 0, 6));
		mock.verifyWhitespace("\n", Location.text(1, 0, 6), Location.text(2, 0, 7));
		mock.verifyParagraph("Cookies", Location.text(2, 0, 7), Location.text(2, 7, 14));
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceStart1()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString(" Hello\n\nCookies").parse(mock);
		mock.verifyWhitespace(" ", Location.text(0, 0, 0), Location.text(0, 1, 1));
		mock.verifyParagraph("Hello\n", Location.text(0, 1, 1), Location.text(1, 0, 7));
		mock.verifyWhitespace("\n", Location.text(1, 0, 7), Location.text(2, 0, 8));
		mock.verifyParagraph("Cookies", Location.text(2, 0, 8), Location.text(2, 7, 15));
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceStart2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString(" \nHello\n\nCookies").parse(mock);
		mock.verifyWhitespace(" \n", Location.text(0, 0, 0), Location.text(1, 0, 2));
		mock.verifyParagraph("Hello\n", Location.text(1, 0, 2), Location.text(2, 0, 8));
		mock.verifyWhitespace("\n", Location.text(2, 0, 8), Location.text(3, 0, 9));
		mock.verifyParagraph("Cookies", Location.text(3, 0, 9), Location.text(3, 7, 16));
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceBetween()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n \nCookies").parse(mock);
		mock.verifyParagraph("Hello\n", Location.text(0, 0, 0), Location.text(1, 0, 6));
		mock.verifyWhitespace(" \n", Location.text(1, 0, 6), Location.text(2, 0, 8));
		mock.verifyParagraph("Cookies", Location.text(2, 0, 8), Location.text(2, 7, 15));
	}

	@Test
	public void testTrailingLinebreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies\n").parse(mock);
		mock.verifyParagraph("Hello\n", Location.text(0, 0, 0), Location.text(1, 0, 6));
		mock.verifyWhitespace("\n", Location.text(1, 0, 6), Location.text(2, 0, 7));
		mock.verifyParagraph("Cookies\n", Location.text(2, 0, 7), Location.text(3, 0, 15));
	}

	@Test
	public void testTrailingLinebreaks()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies\n\n").parse(mock);
		mock.verifyParagraph("Hello\n", Location.text(0, 0, 0), Location.text(1, 0, 6));
		mock.verifyWhitespace("\n", Location.text(1, 0, 6), Location.text(2, 0, 7));
		mock.verifyParagraph("Cookies\n", Location.text(2, 0, 7), Location.text(3, 0, 15));
		mock.verifyWhitespace("\n", Location.text(3, 0, 15), Location.text(4, 0, 16));
	}
}
