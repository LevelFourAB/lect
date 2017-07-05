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
		mock.verifyParagraph("Hello world!", Location.text(0, 2, -1), Location.text(0, 14, -1));
	}

	@Test
	public void testSingleParagraphWithEntity()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hell&ouml; world!</p>").parse(mock);
		mock.verifyParagraph("Hellö world!", Location.text(0, 2, -1), Location.text(0, 19, -1));
	}

	@Test
	public void testSingleParagraphWithLineBreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<p>Hello\nworld!</p>").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 2, -1), Location.text(1, 5, -1));
	}

	@Test
	public void testNoElements()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 0, -1), Location.text(0, 10, -1));
	}

	@Test
	public void testSection()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<section>Hello world!</section>").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 8, -1), Location.text(0, 20, -1));
	}

	@Test
	public void testDocument()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<html><body>Hello world!</body></html>").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 11, -1), Location.text(0, 23, -1));
	}

	@Test
	public void testDocumentWithHead()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<html><head><meta></head><body>Hello world!</body></html>").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 30, -1), Location.text(0, 42, -1));
	}

	@Test
	public void testBrokenHTML()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<br>Hello</br> world!").parse(mock);
		mock.verifyParagraph("\nHello\n world!", Location.text(0, 0, -1), Location.text(0, 19, -1));
	}

	@Test
	public void testDocumentWithDoctype()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		HTMLSource.forString("<!DOCTYPE html><html><head><meta></head><body>Hello world!</body></html>").parse(mock);
		mock.verifyParagraph("Hello world!", Location.text(0, 45, -1), Location.text(0, 57, -1));
	}
}
