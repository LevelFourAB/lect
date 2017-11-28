package se.l4.lect.markdown;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import se.l4.lect.VerifyingSyntaxTreeEncounter;
import se.l4.lect.location.Location;

public class MarkdownSourceTest
{
	@Test
	public void testSingleParagraph()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(12, 0, 12),
			Location.text(12, 0, 12)
		);
	}

	@Test
	public void testSingleParagraphFollowedByLineBreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello world!\n").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(12, 0, 12),
			Location.text(13, 1, 0)
		);
	}

	@Test
	public void testSingleParagraphWithEmphasis()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello *world*!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(6, 0, 6), // After Hello
			Location.text(7, 0, 7), Location.text(12, 0, 12), // After world
			Location.text(13, 0, 13), Location.text(14, 0, 14), // After !
			Location.text(14, 0, 14)
		);
	}

	@Test
	public void testSingleParagraphWithStrongEmphasis()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello **world**!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(6, 0, 6), // After Hello + space
			Location.text(8, 0, 8), Location.text(13, 0, 13), // After world
			Location.text(15, 0, 15), Location.text(16, 0, 16), // After !
			Location.text(16, 0, 16)
		);
	}


	@Test
	public void testSingleParagraphWithCode()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello `world`!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(6, 0, 6), // After Hello
			Location.text(7, 0, 7), Location.text(12, 0, 12), // After world
			Location.text(13, 0, 13), Location.text(14, 0, 14), // After !
			Location.text(14, 0, 14)
		);
	}

	@Test
	public void testSingleParagraphWithLineBreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello\nworld!").parse(mock);
		mock.verifyParagraph("Hello\nworld!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(5, 0, 5), // After Hello
			Location.text(5, 0, 5), Location.text(6, 1, 0),
			Location.text(6, 1, 0), Location.text(12, 1, 6), // After world!
			Location.text(12, 1, 6)
		);
	}

	@Test
	public void testSingleParagraphWithHardLineBreak()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello  \nworld!").parse(mock);
		mock.verifyParagraph("Hello\nworld!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(5, 0, 5), // After Hello
			Location.text(5, 0, 5), Location.text(8, 1, 0),
			Location.text(8, 1, 0), Location.text(14, 1, 6), // After world!
			Location.text(14, 1, 6)
		);
	}

	@Test
	public void testSingleParagraphWithHtmlEntity()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hell&ouml; world!").parse(mock);
		mock.verifyParagraph("Hellö world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(4, 0, 4), // After Hell
			Location.text(4, 0, 4), Location.text(10, 0, 10), // After &ouml;
			Location.text(10, 0, 10), Location.text(17, 0, 17), // After space + world!
			Location.text(17, 0, 17)
		);
	}

	@Test
	public void testSingleParagraphWithInlineHtml()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello <em>world</em>!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(6, 0, 6), // After Hell
			Location.text(10, 0, 10), Location.text(15, 0, 15), // After &ouml;
			Location.text(20, 0, 20), Location.text(21, 0, 21), // After !
			Location.text(21, 0, 21)
		);
	}

	@Test
	public void testSingleParagraphWithAutoLink()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("<http://example.org>").parse(mock);
		mock.verifyParagraph("http://example.org",
			Location.text(0, 0, 0),
			Location.text(1, 0, 1), Location.text(19, 0, 19),
			Location.text(20, 0, 20)
		);
	}

	@Test
	public void testSingleParagraphWithLink()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("[Hello world!](http://example.org)").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(1, 0, 1), Location.text(13, 0, 13),
			Location.text(34, 0, 34)
		);
	}

	@Test
	public void testMultipleParagraphs()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("Hello world!\n\nWith cookies").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(0, 0, 0), Location.text(12, 0, 12),
			Location.text(13, 1, 0)
		);
		mock.verifyParagraph("With cookies",
			Location.text(14, 2, 0),
			Location.text(14, 2, 0), Location.text(26, 2, 12),
			Location.text(26, 2, 12)
		);
	}

	@Test
	public void testHeadingSingleLine()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("# Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(14, 0, 14)
		);
	}

	@Test
	public void testHeadingSingleLine2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("# Hello world! #").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(16, 0, 16)
		);
	}

	@Test
	public void testBlockquoteSingleLine()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("> Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(2, 0, 2),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(14, 0, 14)
		);
	}

	@Test
	public void testBlockquoteMultipleLines()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("> Hello\n> world!").parse(mock);
		mock.verifyParagraph("Hello\nworld!",
			Location.text(2, 0, 2),
			Location.text(2, 0, 2), Location.text(7, 0, 7), // After Hello
			Location.text(7, 0, 7), Location.text(8, 1, 0), // After \n
			Location.text(10, 1, 2), Location.text(16, 1, 8), // After world!
			Location.text(16, 1, 8)
		);
	}

	@Test
	public void testListSingleItem()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("* Hello world!").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(2, 0, 2),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(14, 0, 14)
		);
	}

	@Test
	public void testListMultipleItems()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("* Hello world!\n* With cookies").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(2, 0, 2),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(15, 1, 0)
		);
		mock.verifyParagraph("With cookies",
			Location.text(17, 1, 2),
			Location.text(17, 1, 2), Location.text(29, 1, 14),
			Location.text(29, 1, 14)
		);
	}

	@Test
	public void testListMultipleItems2()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("* Hello world!\n\n* With cookies").parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(2, 0, 2),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(15, 1, 0)
		);
		mock.verifyParagraph("With cookies",
			Location.text(18, 2, 2),
			Location.text(18, 2, 2), Location.text(30, 2, 14),
			Location.text(30, 2, 14)
		);
	}

	@Test
	public void testGithubTasklist()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("- [ ] Hello world!").withFlavor(MarkdownFlavor.GITHUB).parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(6, 0, 6),
			Location.text(6, 0, 6), Location.text(18, 0, 18),
			Location.text(18, 0, 18)
		);
	}

	@Test
	public void testGithubStrikethough()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("~~Hello world!~~").withFlavor(MarkdownFlavor.GITHUB).parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(2, 0, 2), Location.text(14, 0, 14),
			Location.text(16, 0, 16)
		);
	}

	@Test
	public void testGithubSubscript()
		throws IOException
	{
		VerifyingSyntaxTreeEncounter mock = new VerifyingSyntaxTreeEncounter(Locale.ENGLISH);
		MarkdownSource.forString("~Hello world!~").withFlavor(MarkdownFlavor.GITHUB).parse(mock);
		mock.verifyParagraph("Hello world!",
			Location.text(0, 0, 0),
			Location.text(1, 0, 1), Location.text(13, 0, 13),
			Location.text(14, 0, 14)
		);
	}
}
