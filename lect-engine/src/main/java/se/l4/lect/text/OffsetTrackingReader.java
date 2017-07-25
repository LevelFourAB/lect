package se.l4.lect.text;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import se.l4.lect.location.MutableTextLocation;
import se.l4.lect.location.TextLocation;

/**
 * {@link Reader} that tracks offsets and allows mapping between offset and line and column.
 *
 * @author Andreas Holstenson
 *
 */
public class OffsetTrackingReader
	extends FilterReader
{
	private int offset;
	private int line;
	private int[] lineStartOffsets;
	private boolean lastWasCarriageReturn;

	public OffsetTrackingReader(Reader in)
	{
		super(in);

		line = 0;
		lineStartOffsets = new int[64];
	}

	private void ensureCapacity(int c)
	{
		if(c < lineStartOffsets.length) return;

		int length = lineStartOffsets.length;
		int newSize = length + (length >> 1);

		lineStartOffsets = Arrays.copyOf(lineStartOffsets, newSize);
	}

	@Override
	public int read()
		throws IOException
	{
		int result = super.read();
		track(result);
		return result;
	}

	@Override
	public int read(char[] cbuf, int off, int len)
		throws IOException
	{
		int result = super.read(cbuf, off, len);
		track(cbuf, off, result);
		return result;
	}

	private void track(int c)
	{
		if(c == -1) return;

		if(c == '\r' || c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085')
		{
			if(c == '\n' && lastWasCarriageReturn)
			{
				// \n following \r is still the same break, update the previous index
				lineStartOffsets[line] = offset + 1;
			}
			else
			{
				if(c == '\r')
				{
					lastWasCarriageReturn = true;
				}

				line++;
				ensureCapacity(line);
				lineStartOffsets[line] = offset + 1;
			}
		}

		offset++;
	}

	private void track(char[] cbuf, int off, int len)
	{
		for(int i=off, n=off+len; i<n; i++)
		{
			track(cbuf[i]);
		}
	}

	/**
	 * Resolve the {@link MutableTextLocation} of a character offset.
	 *
	 * @param offset
	 * @return
	 */
	public TextLocation offsetToLocation(int offset)
	{
		TextLocation loc = new MutableTextLocation(0, 0);
		offsetToLocation(offset, loc);
		return loc;
	}

	/**
	 * Resolve the {@link MutableTextLocation} of a character offset and store it in the given target.
	 *
	 * @param offset
	 * @param target
	 */
	public void offsetToLocation(int offset, TextLocation target)
	{
		int line = Arrays.binarySearch(lineStartOffsets, 0, this.line + 1, offset);
		int column = 0;
		if(line < 0)
		{
			// The offset was not exactly the start of a line, so figure out the nearest line and then the column
			line = -(line + 1) - 1;
			column = offset - lineStartOffsets[line];
		}

		target.moveTo(line, column);
	}
}
