package se.l4.lect;

import java.util.LinkedList;
import java.util.Locale;

public class VerifyingSyntaxTreeEncounter
	implements SourceEncounter
{
	private final Locale locale;

	private boolean inParagraph;

	private LinkedList<Data> data;
	private StringBuilder buffer;

	private boolean calledDone;

	private Location location;
	private Location locationBuffer;
	private Location locationEnd;

	public VerifyingSyntaxTreeEncounter(Locale locale)
	{
		this.locale = locale;

		data = new LinkedList<>();
		buffer = new StringBuilder();
	}

	@Override
	public Locale locale()
	{
		return locale;
	}

	@Override
	public Location location()
	{
		return location;
	}

	@Override
	public void location(Location location)
	{
		this.location = location.copy();
	}

	@Override
	public boolean inParagraph()
	{
		return inParagraph;
	}

	@Override
	public void startParagraph()
	{
		if(inParagraph)
		{
			throw new AssertionError("Trying to start a paragraph while already in one");
		}

		inParagraph = true;

		if(buffer.length() > 0)
		{
			data.add(new Data(false, buffer.toString(), locationBuffer == null ? location : locationBuffer, locationEnd));
			buffer.setLength(0);
		}

		locationBuffer = location.copy();
	}

	@Override
	public void endParagraph()
	{
		if(! inParagraph)
		{
			throw new AssertionError("Trying to end a paragraph while not in one");
		}

		inParagraph = false;

		if(buffer.length() > 0)
		{
			data.add(new Data(true, buffer.toString(), locationBuffer, locationEnd));
			buffer.setLength(0);
		}

		locationEnd = location;
		locationBuffer = null;
	}

	@Override
	public void text(String text, Location end)
	{
		if(locationBuffer == null)
		{
			locationBuffer = location.copy();
		}
		locationEnd = end;
		buffer.append(text);
	}

	@Override
	public void done()
	{
		if(inParagraph)
		{
			throw new AssertionError("Called done() while in a paragraph");
		}

		if(buffer.length() > 0)
		{
			data.add(new Data(false, buffer.toString(), locationBuffer == null ? location : locationBuffer, locationEnd));
			buffer.setLength(0);
		}

		calledDone = true;
	}

	public void verifyParagraph(String text, Location start, Location end)
	{
		if(! calledDone) throw new AssertionError("done() was never called");
		if(data.isEmpty()) throw new AssertionError("No more data, expected to find paragraph with text `" + text + "`");

		Data d = data.removeFirst();
		if(! d.paragraph)
		{
			throw new AssertionError("Expected a paragraph but got whitespace between paragraphs");
		}

		if(! d.text.equals(text))
		{
			throw new AssertionError("Expected a paragraph with text `" + text + "` but got `" + d.text + "`");
		}

		if(start != null && ! start.equals(d.location))
		{
			throw new AssertionError("Start does not match, expected " + start + " but got " + d.location);
		}

		if(end != null && ! end.equals(d.end))
		{
			throw new AssertionError("End does not match, expected " + end + " but got " + d.end);
		}
	}

	public void verifyWhitespace(String text, Location start, Location end)
	{
		if(! calledDone) throw new AssertionError("done() was never called");
		if(data.isEmpty()) throw new AssertionError("No more data, expected to find whitespace with value `" + text + "`");

		Data d = data.removeFirst();
		if(d.paragraph)
		{
			throw new AssertionError("Expected a whitespace, but got a paragraph");
		}

		if(! d.text.equals(text))
		{
			throw new AssertionError("Expected a whitespace with value `" + text + "` but got `" + d.text + "`");
		}

		if(start != null && ! start.equals(d.location))
		{
			throw new AssertionError("Start does not match, expected " + start + " but got " + d.location);
		}

		if(end != null && ! end.equals(d.end))
		{
			throw new AssertionError("End does not match, expected " + end + " but got " + d.end);
		}
	}

	public void verifyEmpty()
	{
		if(! data.isEmpty())
		{
			throw new AssertionError("Did not expect more data, but have " + data.size() + " items");
		}
	}

	class Data
	{
		private boolean paragraph;
		private String text;
		private Location location;
		private Location end;

		public Data(boolean paragraph, String text, Location location, Location end)
		{
			this.paragraph = paragraph;
			this.text = text;
			this.location = location;
			this.end = end;
		}
	}
}
