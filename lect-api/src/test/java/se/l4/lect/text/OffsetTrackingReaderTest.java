package se.l4.lect.text;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import se.l4.lect.location.TextLocation;

public class OffsetTrackingReaderTest
{
	private static OffsetTrackingReader read(String str)
	{
		try
		{
			OffsetTrackingReader r = new OffsetTrackingReader(new StringReader(str));
			char[] buf =  new char[1024];
			while(r.read(buf) != -1) {};
			return r;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testSingleLineStart()
	{
		OffsetTrackingReader reader = read("Cookies are tasty");
		TextLocation loc = reader.offsetToLocation(0);

		assertThat(loc.getLine(), is(0));
		assertThat(loc.getColumn(), is(0));
	}

	@Test
	public void testSingleLineMiddle()
	{
		OffsetTrackingReader reader = read("Cookies are tasty");
		TextLocation loc = reader.offsetToLocation(5);

		assertThat(loc.getLine(), is(0));
		assertThat(loc.getColumn(), is(5));
	}

	@Test
	public void testTwoLinesMiddleOfFirstLine()
	{
		OffsetTrackingReader reader = read("Cookies\nare tasty");
		TextLocation loc = reader.offsetToLocation(5);

		assertThat(loc.getLine(), is(0));
		assertThat(loc.getColumn(), is(5));
	}

	@Test
	public void testTwoLinesEndOfFirstLine()
	{
		OffsetTrackingReader reader = read("Cookies\nare tasty");
		TextLocation loc = reader.offsetToLocation(7);

		assertThat(loc.getLine(), is(0));
		assertThat(loc.getColumn(), is(7));
	}

	@Test
	public void testTwoLinesStartAtSecondLine()
	{
		OffsetTrackingReader reader = read("Cookies\nare tasty");
		TextLocation loc = reader.offsetToLocation(8);

		assertThat(loc.getLine(), is(1));
		assertThat(loc.getColumn(), is(0));
	}

	@Test
	public void testTwoLinesMiddleInSecondLine()
	{
		OffsetTrackingReader reader = read("Cookies\nare tasty");
		TextLocation loc = reader.offsetToLocation(12);

		assertThat(loc.getLine(), is(1));
		assertThat(loc.getColumn(), is(4));
	}

	@Test
	public void testTwoLinesOutsideRange()
	{
		OffsetTrackingReader reader = read("Cookies\nare tasty");
		TextLocation loc = reader.offsetToLocation(30);

		assertThat(loc.getLine(), is(1));
		assertThat(loc.getColumn(), is(22));
	}

	@Test
	public void testCarriageReturn()
	{
		OffsetTrackingReader reader = read("Cookies\r\nare tasty");
		TextLocation loc = reader.offsetToLocation(9);

		assertThat(loc.getLine(), is(1));
		assertThat(loc.getColumn(), is(0));
	}

	@Test
	public void testMultipleNewLines()
	{
		OffsetTrackingReader reader = read("Cookies\n\nare tasty");
		TextLocation loc = reader.offsetToLocation(8);
		assertThat(loc.getLine(), is(1));
		assertThat(loc.getColumn(), is(0));

		loc = reader.offsetToLocation(9);
		assertThat(loc.getLine(), is(2));
		assertThat(loc.getColumn(), is(0));
	}
}
