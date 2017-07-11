package se.l4.lect.html;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import se.l4.lect.Location;
import se.l4.lect.VerifyingSyntaxTreeEncounter;

public class HTMLSourceTest
{
	@Test
	public void testSingleParagraph()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello world!</p>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 8), // After Hello
			Location.text(0, 8), Location.text(0, 9), // After space,
			Location.text(0, 9), Location.text(0, 15), // After world!
			Location.text(0, 15)
		);
	}

	@Test
	public void testSingleParagraphWithEntity()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hell&ouml; world!</p>").parse(mock);
		mock.verifyParagraph("Hellö world!",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 7), // After Hell,
			Location.text(0, 7), Location.text(0, 13), // After &ouml;
			Location.text(0, 13), Location.text(0, 14), // After space,
			Location.text(0, 14), Location.text(0, 20), // After world!
			Location.text(0, 20)
		);
	}

	@Test
	public void testSingleParagraphWithLineBreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello\nworld!</p>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 8), // After Hello
			Location.text(0, 8), Location.text(1, 0), // After \n
			Location.text(1, 0), Location.text(1, 6), // After world!
			Location.text(1, 6)
		);
	}

	@Test
	public void testSingleParagraphWithLineBreakAndIndent()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>\n  Hello world!\n</p>").parse(mock);
		mock.verifyParagraph("Hello world! ",
			Location.text(0, 0),
			//Location.text(0, 3), Location.text(1, 0), // After \n + space
			Location.text(1, 2), Location.text(1, 7), // After Hello
			Location.text(1, 7), Location.text(1, 8), // After space,
			Location.text(1, 8), Location.text(1, 14), // After world!
			Location.text(1, 14), Location.text(2, 0), // After \n
			Location.text(2, 0)
		);
	}

	@Test
	public void testSingleParagraphWithExtraSpace()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<section>Hello  world!</section>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 9),
			Location.text(0, 9), Location.text(0, 14), // After Hello,
			Location.text(0, 14), Location.text(0, 15), // After single space,
			Location.text(0, 16), Location.text(0, 22), // After world!
			Location.text(0, 22)
		);
	}

	@Test
	public void testSingleParagraphWithEntitySpace()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<section>Hello &nbsp;world!</section>").parse(mock);
		mock.verifyParagraph("Hello \u00a0world!",
			Location.text(0, 9),
			Location.text(0, 9), Location.text(0, 14), // After Hello
			Location.text(0, 14), Location.text(0, 15), // After space
			Location.text(0, 15), Location.text(0, 21), // After &nbsp;
			Location.text(0, 21), Location.text(0, 27), // After world!
			Location.text(0, 27)
		);
	}

	@Test
	public void testNoElements()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(0, 6), // After space
			Location.text(0, 6), Location.text(0, 12), // After world!
			Location.text(0, 12)
		);
	}

	@Test
	public void testBreakInImplicit()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("Hello cookies.<br>Test.").parse(mock);
		mock.verifyParagraph("Hello cookies.\nTest.",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 5), // After Hello
			Location.text(0, 5), Location.text(0, 6), // After space
			Location.text(0, 6), Location.text(0, 14), // After cookies.
			Location.text(0, 14), Location.text(0, 18), // After <br>
			Location.text(0, 18), Location.text(0, 23), // After Test.
			Location.text(0, 23)
		);
	}

	@Test
	public void testBreakInExplicit()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello cookies.<br>Test.</p>").parse(mock);
		mock.verifyParagraph("Hello cookies.\nTest.",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 8), // After Hello
			Location.text(0, 8), Location.text(0, 9), // After space
			Location.text(0, 9), Location.text(0, 17), // After cookies.
			Location.text(0, 17), Location.text(0, 21), // After <br>
			Location.text(0, 21), Location.text(0, 26), // After Test.
			Location.text(0, 26)
		);
	}

	@Test
	public void testParagraphWithBreakFollowedByNewline()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello cookies.<br>\nTest.</p>").parse(mock);
		mock.verifyParagraph("Hello cookies.\nTest.",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 8), // After Hello
			Location.text(0, 8), Location.text(0, 9), // After space
			Location.text(0, 9), Location.text(0, 17), // After cookies.
			Location.text(0, 17), Location.text(0, 21), // After <br>
			Location.text(1, 0), Location.text(1, 5), // After Test.
			Location.text(1, 5)
		);
	}

	@Test
	public void testParagraphWithInlineLinkWithSpaceAfter()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p><a>Hello</a> world!</p>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0),
			Location.text(0, 6), Location.text(0, 11), // After Hello
			Location.text(0, 15), Location.text(0, 16), // After space
			Location.text(0, 16), Location.text(0, 22), // After world!
			Location.text(0, 22)
		);
	}

	@Test
	public void testParagraphFollowedByParagraph()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello</p><p>World</p>").parse(mock);
		mock.verifyParagraph("Hello",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 8), // After Hello
			Location.text(0, 8)
		);
		mock.verifyParagraph("World",
			Location.text(0, 12),
			Location.text(0, 15), Location.text(0, 20), // After Hello
			Location.text(0, 20)
		);
		mock.verifyEmpty();
	}

	@Test
	public void testParagraphFollowedBySpaceAndParagraph()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello</p>\n<p>World</p>").parse(mock);
		mock.verifyParagraph("Hello",
			Location.text(0, 0),
			Location.text(0, 3), Location.text(0, 8), // After Hello
			Location.text(0, 8)
		);
		mock.verifyWhitespace(" ",
			Location.text(0, 12),
			Location.text(0, 12), Location.text(1, 0), // After space
			Location.text(1, 0)
		);
		mock.verifyParagraph("World",
			Location.text(1, 0),
			Location.text(1, 3), Location.text(1, 8), // After World
			Location.text(1, 8)
		);
		mock.verifyEmpty();
	}

	//@Test
	public void testInlineLinkWithSpaceAfter()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<a>Hello</a> world!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0),
			Location.text(0, 5), // After Hello
			Location.text(0, 6), // After space
			Location.text(0, 12), // After world!
			Location.text(0, 12)
		);
	}

	@Test
	public void testSection()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<section>Hello world!</section>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 9),
			Location.text(0, 9), Location.text(0, 14), // After Hello,
			Location.text(0, 14), Location.text(0, 15), // After single space
			Location.text(0, 15), Location.text(0, 21), // After world!
			Location.text(0, 21)
		);
	}

	@Test
	public void testDocument()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<html><body>Hello world!</body></html>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 12),
			Location.text(0, 12), Location.text(0, 17), // After Hello
			Location.text(0, 17), Location.text(0, 18), // After space
			Location.text(0, 18), Location.text(0, 24), // After world!
			Location.text(0, 24)
		);
	}

	@Test
	public void testDocumentWithHead()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<html><head><meta></head><body>Hello world!</body></html>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 31),
			Location.text(0, 31), Location.text(0, 36), // After Hello
			Location.text(0, 36), Location.text(0, 37), // After space
			Location.text(0, 37), Location.text(0, 43), // After world!
			Location.text(0, 43)
		);
	}

	@Test
	public void testBrokenHTML()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<br>Hello</br> world!").parse(mock);
		mock.verifyParagraph("\nHello world!",
			Location.text(0, 0),
			Location.text(0, 0), Location.text(0, 4), // After <br>
			Location.text(0, 4), Location.text(0, 9), // After Hello
			Location.text(0, 14), Location.text(0, 15), // After </br> + space
			Location.text(0, 15), Location.text(0, 21), // After world!
			Location.text(0, 21)
		);
	}

	@Test
	public void testDocumentWithDoctype()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<!DOCTYPE html><html><head><meta></head><body>Hello world!</body></html>").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 46),
			Location.text(0, 46), Location.text(0, 51), // After Hello
			Location.text(0, 51), Location.text(0, 52), // After space
			Location.text(0, 52), Location.text(0, 58), // After world!
			Location.text(0, 58)
		);
	}
}
