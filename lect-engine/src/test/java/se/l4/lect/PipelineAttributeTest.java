package se.l4.lect;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import se.l4.lect.handlers.DefaultHandler;
import se.l4.lect.location.Location;
import se.l4.lect.location.MutableOffsetLocation;
import se.l4.lect.tokens.TokenProperty;

public class PipelineAttributeTest
{
	@Test
	public void testSingleTokenWithin()
		throws IOException
	{
		new DefinedTextSource("abc ", "def", " ghi")
			.process()
			.language(ICULanguage.forLocale(Locale.ENGLISH))
			.with(new VerifyingHandler(4, 7))
			.run();
	}

	@Test
	public void testMultipleTokensWithin()
		throws IOException
	{
		new DefinedTextSource("abc ", "def ghi", " jkl")
			.process()
			.language(ICULanguage.forLocale(Locale.ENGLISH))
			.with(new VerifyingHandler(4, 11))
			.run();
	}

	@Test
	public void testWithinToken()
		throws IOException
	{
		new DefinedTextSource("abc ", "de", "f ghi")
			.process()
			.language(ICULanguage.forLocale(Locale.ENGLISH))
			.with(new VerifyingHandler(4, 7))
			.run();
	}

	private static class DefinedTextSource
		implements TextSource
	{
		private String before;
		private String within;
		private String after;

		public DefinedTextSource(String before, String within, String after)
		{
			this.before = before;
			this.within = within;
			this.after = after;
		}

		@Override
		public void parse(TextSourceEncounter encounter)
			throws IOException
		{
			MutableOffsetLocation location = new MutableOffsetLocation();
			MutableOffsetLocation end = new MutableOffsetLocation();
			encounter.location(location);
			encounter.startParagraph();

			end.set(before.length());
			encounter.text(before, end);

			location.set(end.get());
			encounter.location(location);
			encounter.setAttribute(TokenProperty.EXTENDED_TYPE, "test");

			end.moveTextIndex(within.length());
			encounter.text(within, end);

			location.set(end.get());
			encounter.location(location);
			encounter.clearAttribute(TokenProperty.EXTENDED_TYPE);

			end.moveTextIndex(after.length());
			encounter.text(after, end);

			encounter.location(location);
			encounter.endParagraph();

			encounter.done();
		}

	}

	private static class VerifyingHandler
		extends DefaultHandler
	{
		private MutableOffsetLocation start;
		private MutableOffsetLocation end;

		private boolean matchedStart;
		private boolean matchedEnd;

		public VerifyingHandler(int start, int end)
		{
			this.start = new MutableOffsetLocation(start);
			this.end = new MutableOffsetLocation(end);
		}

		@Override
		public void startAttribute(Location location, TokenProperty<?> property, Object value)
		{
			if(! location.equals(start))
			{
				throw new AssertionError("Start of attribute mismatch, expected " + start + ", got " + location);
			}
			matchedStart = true;
		}

		@Override
		public void endAttribute(Location location, TokenProperty<?> property)
		{
			if(! location.equals(end))
			{
				throw new AssertionError("Start of attribute mismatch, expected " + end + ", got " + location);
			}
			matchedEnd = true;
		}

		@Override
		public void done()
		{
			if(! matchedStart)
			{
				throw new AssertionError("Attribute was never started");
			}

			if(! matchedEnd)
			{
				throw new AssertionError("Attribute was never ended");
			}
		}
	}
}
