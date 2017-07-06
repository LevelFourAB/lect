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
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 12), // After entire line
			Location.text(0, 12)
		);
	}

	@Test
	public void testParagraphWithLineBreak1()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\nworld!").parse(mock);
		mock.verifyParagraph("Hello\nworld!",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(1, 0), // After \n
			Location.text(1, 0), Location.text(1, 6), // After world!
			Location.text(1, 6)
		);
	}

	@Test
	public void testParagraphWithLineBreak2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\r\nworld!").parse(mock);
		mock.verifyParagraph("Hello\r\nworld!",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(1, 0), // After \r\n
			Location.text(1, 0), Location.text(1, 6), // After world!
			Location.text(1, 6)
		);
	}

	@Test
	public void testParagraphWithLineBreak3()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n world!").parse(mock);
		mock.verifyParagraph("Hello\n world!",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			//Location.text(1, 0), // After \n (TODO?)
			Location.text(0, 5), Location.text(1, 1), // After space
			Location.text(1, 1), Location.text(1, 7), // After world!
			Location.text(1, 7)
		);
	}

	@Test
	public void testMultipleParagraphs()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies").parse(mock);
		mock.verifyParagraph("Hello\n",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After hello
			Location.text(0, 5), Location.text(1, 0), // Line break after \n
			Location.text(1, 0)
		);
		mock.verifyWhitespace("\n",
			Location.text(1, 0),
			Location.text(1, 0), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
		mock.verifyParagraph("Cookies",
			Location.text(2, 0),
			Location.text(2, 0), Location.text(2, 7), // After Cookies
			Location.text(2, 7)
		);
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceStart1()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString(" Hello\n\nCookies").parse(mock);
		mock.verifyWhitespace(" ",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 1), // After space
			Location.text(0, 1)
		);
		mock.verifyParagraph("Hello\n",
			Location.text(0, 1),
			Location.text(0, 1), Location.text(0, 6), // After Hello
			Location.text(0, 6), Location.text(1, 0), // After \n
			Location.text(1, 0)
		);
		mock.verifyWhitespace("\n",
			Location.text(1, 0),
			Location.text(1, 0), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
		mock.verifyParagraph("Cookies",
			Location.text(2, 0),
			Location.text(2, 0), Location.text(2, 7), // After Cookies
			Location.text(2, 7)
		);
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceStart2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString(" \nHello\n\nCookies").parse(mock);
		mock.verifyWhitespace(" \n",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(1, 0), // After \n
			Location.text(1, 0)
		);
		mock.verifyParagraph("Hello\n",
			Location.text(1, 0),
			Location.text(1, 0), Location.text(1, 5), // After Hello
			Location.text(1, 5), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
		mock.verifyWhitespace("\n",
			Location.text(2, 0),
			Location.text(2, 0), Location.text(3, 0), // After \n
			Location.text(3, 0)
		);
		mock.verifyParagraph("Cookies",
			Location.text(3, 0),
			Location.text(3, 0), Location.text(3, 7), // After Cookies
			Location.text(3, 7)
		);
	}

	@Test
	public void testMultipleParagraphsWithWhitespaceBetween()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n \nCookies").parse(mock);
		mock.verifyParagraph("Hello\n",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(1, 0), // After \n
			Location.text(1, 0)
		);
		mock.verifyWhitespace(" \n",
			Location.text(1, 0),
			Location.text(1, 0), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
		mock.verifyParagraph("Cookies",
			Location.text(2, 0),
			Location.text(2, 0), Location.text(2, 7), // After Cookies
			Location.text(2, 7)
		);
	}

	@Test
	public void testTrailingLinebreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies\n").parse(mock);
		mock.verifyParagraph("Hello\n",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(1, 0), // After \n
			Location.text(1, 0)
		);
		mock.verifyWhitespace("\n",
			Location.text(1, 0),
			Location.text(1, 0), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
		mock.verifyParagraph("Cookies\n",
			Location.text(2, 0),
			Location.text(2, 0), Location.text(2, 7), // After Cookies
			Location.text(2, 7), Location.text(3, 0), // After \n
			Location.text(3, 0)
		);
	}

	@Test
	public void testTrailingLinebreaks()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		PlainTextSource.forString("Hello\n\nCookies\n\n").parse(mock);
		mock.verifyParagraph("Hello\n",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(1, 0), // After \n
			Location.text(1, 0)
		);
		mock.verifyWhitespace("\n",
			Location.text(1, 0),
			Location.text(1, 0), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
		mock.verifyParagraph("Cookies\n",
			Location.text(2, 0),
			Location.text(2, 0), Location.text(2, 7), // After Cookies
			Location.text(2, 7), Location.text(3, 0), // After \n
			Location.text(3, 0)
		);
		mock.verifyWhitespace("\n",
			Location.text(3, 0),
			Location.text(3, 0), Location.text(4, 0), // After \n
			Location.text(4, 0)
		);
	}
}
