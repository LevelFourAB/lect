package se.l4.lect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class VerifyingSyntaxTreeEncounter
	implements SourceEncounter
{
	private static final Location[] EMTPY = new Location[0];

	private final Locale locale;

	private boolean inParagraph;

	private LinkedList<Data> data;
	private StringBuilder buffer;
	private List<Location> stops;

	private boolean calledDone;

	private Location location;
	private Location locationBuffer;

	public VerifyingSyntaxTreeEncounter(Locale locale)
	{
		this.locale = locale;

		data = new LinkedList<>();
		buffer = new StringBuilder();
		stops = new ArrayList<>();
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
			stops.add(location);
			data.add(new Data(false, buffer.toString(), locationBuffer == null ? location : locationBuffer, stops.toArray(EMTPY)));
			stops.clear();
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
			stops.add(location);
			data.add(new Data(true, buffer.toString(), locationBuffer, stops.toArray(EMTPY)));
			stops.clear();
			buffer.setLength(0);
		}

		locationBuffer = null;
	}

	@Override
	public void text(CharSequence text, Location end)
	{
		if(locationBuffer == null)
		{
			locationBuffer = location.copy();
		}
		buffer.append(text);
		stops.add(location);
		stops.add(end.copy());
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
			stops.add(location.copy());
			data.add(new Data(false, buffer.toString(), locationBuffer == null ? location : locationBuffer, stops.toArray(EMTPY)));
			buffer.setLength(0);
		}

		calledDone = true;
	}

	public void verifyParagraph(String text, Location start, Location... stops)
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

		if(stops.length != d.stops.length)
		{
			throw new AssertionError("Number of stops differ, expected " + stops.length + ", but got " + d.stops.length + " with values " + Arrays.toString(d.stops));
		}

		for(int i=0, n=stops.length; i<n; i++)
		{
			if(! stops[i].equals(d.stops[i]))
			{
				throw new AssertionError("Stop " + i + " does not match, expected " + stops[i] + " but got " + d.stops[i]);
			}
		}
	}

	public void verifyWhitespace(String text, Location start, Location... stops)
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

		if(stops.length != d.stops.length)
		{
			throw new AssertionError("Number of stops differ, expected " + stops.length + ", but got " + d.stops.length + " with values " + Arrays.toString(d.stops));
		}

		for(int i=0, n=stops.length; i<n; i++)
		{
			if(! stops[i].equals(d.stops[i]))
			{
				throw new AssertionError("Stop " + i + " does not match, expected " + stops[i] + " but got " + d.stops[i]);
			}
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
		private Location[] stops;

		public Data(boolean paragraph, String text, Location location, Location[] stops)
		{
			this.paragraph = paragraph;
			this.text = text;
			this.location = location;
			this.stops = stops;
		}
	}
}
