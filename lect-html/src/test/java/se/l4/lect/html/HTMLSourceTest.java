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
			Location.text(0, 3),
			Location.text(0, 8), // After Hello
			Location.text(0, 9), // After space,
			Location.text(0, 15), // After world!
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
			Location.text(0, 3),
			Location.text(0, 7), // After Hell,
			Location.text(0, 13), // After &ouml;
			Location.text(0, 14), // After space,
			Location.text(0, 20), // After world!
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
			Location.text(0, 3),
			Location.text(0, 8), // After Hello
			Location.text(1, 0), // After \n
			Location.text(1, 6), // After world!
			Location.text(1, 6)
		);
	}

	@Test
	public void testSingleParagraphWithLineBreakAndIndent()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>\n  Hello world!\n</p>").parse(mock);
		mock.verifyParagraph(" Hello world! ",
			Location.text(0, 3),
			Location.text(1, 0), // After \n + space
			Location.text(1, 7), // After Hello
			Location.text(1, 8), // After space,
			Location.text(1, 14), // After world!
			Location.text(2, 0), // After \n
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
			Location.text(0, 14), // After Hello,
			Location.text(0, 15), // After single space,
			Location.text(0, 22), // After world!
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
			Location.text(0, 14), // After Hello
			Location.text(0, 15), // After space
			Location.text(0, 21), // After &nbsp;
			Location.text(0, 27), // After world!
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
			Location.text(0, 14), // After Hello,
			Location.text(0, 15), // After single space
			Location.text(0, 21), // After world!
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
			Location.text(0, 17), // After Hello
			Location.text(0, 18), // After space
			Location.text(0, 24), // After world!
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
			Location.text(0, 36), // After Hello
			Location.text(0, 37), // After space
			Location.text(0, 43), // After world!
			Location.text(0, 43)
		);
	}

	@Test
	public void testBrokenHTML()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<br>Hello</br> world!").parse(mock);
		mock.verifyParagraph("\nHello\n world!",
			Location.text(0, 0),
			Location.text(0, 4), // After <br>
			Location.text(0, 9), // After Hello
			Location.text(0, 14), // After </br>
			Location.text(0, 15), // After space
			Location.text(0, 21), // After world!
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
			Location.text(0, 51), // After Hello
			Location.text(0, 52), // After space
			Location.text(0, 58), // After world!
			Location.text(0, 58)
		);
	}
}
