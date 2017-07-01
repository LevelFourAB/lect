package se.l4.lect.text;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import se.l4.lect.VerifyingSyntaxTreeEncounter;

public class PlainTextSourceTest
{
	@Test
	public void testSingeLine()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!");
	}

	@Test
	public void testParagraphWithLineBreak1()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\nworld!").parse(mock);
		mock.verifyParagraph("Hello\nworld!");
	}

	@Test
	public void testParagraphWithLineBreak2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\r\nworld!").parse(mock);
		mock.verifyParagraph("Hello\r\nworld!");
	}

	@Test
	public void testParagraphWithLineBreak3()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n world!").parse(mock);
		mock.verifyParagraph("Hello\n world!");
	}

	@Test
	public void testMultipleParagraphs()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies").parse(mock);
		mock.verifyParagraph("Hello\n");
		mock.verifyWhitespace("\n");
		mock.verifyParagraph("Cookies");
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceStart1()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString(" Hello\n\nCookies").parse(mock);
		mock.verifyWhitespace(" ");
		mock.verifyParagraph("Hello\n");
		mock.verifyWhitespace("\n");
		mock.verifyParagraph("Cookies");
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceStart2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString(" \nHello\n\nCookies").parse(mock);
		mock.verifyWhitespace(" \n");
		mock.verifyParagraph("Hello\n");
		mock.verifyWhitespace("\n");
		mock.verifyParagraph("Cookies");
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceBetween()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n \nCookies").parse(mock);
		mock.verifyParagraph("Hello\n");
		mock.verifyWhitespace(" \n");
		mock.verifyParagraph("Cookies");
	}

	@Test
	public void testTrailingLinebreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies\n").parse(mock);
		mock.verifyParagraph("Hello\n");
		mock.verifyWhitespace("\n");
		mock.verifyParagraph("Cookies\n");
	}

	@Test
	public void testTrailingLinebreaks()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies\n\n").parse(mock);
		mock.verifyParagraph("Hello\n");
		mock.verifyWhitespace("\n");
		mock.verifyParagraph("Cookies\n");
		mock.verifyWhitespace("\n");
	}
}
