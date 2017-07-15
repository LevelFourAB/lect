package se.l4.lect.location;

/**
 * Mutable {@link TextLocation} for use in cases where line and column is important.
 *
 * @author Andreas Holstenson
 *
 */
public class MutableTextLocation
	implements Location, TextLocation
{
	private int line;
	private int column;

	/**
	 * Create a new location representing the given line and column.
	 *
	 * @param line
	 *   zero-indexed line
	 * @param column
	 *   zero-indexed column
	 */
	public MutableTextLocation(int line, int column)
	{
		this.line = line;
		this.column = column;
	}

	@Override
	public int getLine()
	{
		return line;
	}

	@Override
	public int getColumn()
	{
		return column;
	}

	@Override
	public MutableTextLocation moveTo(int line, int column)
	{
		this.line = line;
		this.column = column;
		return this;
	}

	public TextLocation copyFrom(MutableTextLocation location)
	{
		this.line = location.line;
		this.column = location.column;
		return this;
	}

	@Override
	public MutableTextLocation copy()
	{
		return new MutableTextLocation(line, column);
	}

	@Override
	public MutableTextLocation moveTextIndex(int amount)
	{
		this.column += amount;
		return this;
	}

	@Override
	public MutableTextLocation moveTextIndex(CharSequence sequence)
	{
		return moveTextIndex(sequence, 0, sequence.length());
	}

	@Override
	public MutableTextLocation moveTextIndex(CharSequence sequence, int offset, int length)
	{
		for(int i=offset, n=offset + length; i<n; i++)
		{
			char c = sequence.charAt(i);
			if(c == '\r')
			{
				line++;
				column = 0;
				if(i + 1 < n && sequence.charAt(i + 1) == '\n')
				{
					// Consume \n after \r
					i++;
					continue;
				}
			}
			else if(c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085')
			{
				line++;
				column = 0;
			}
			else
			{
				column++;
			}
		}

		return this;
	}

	@Override
	public int compareTo(Location o)
	{
		if(! (o instanceof TextLocation))
		{
			throw new IllegalArgumentException("Trying to compare to incompatible location: " + o);
		}

		int c = Integer.compare(line, ((TextLocation) o).getLine());
		if(c != 0) return c;

		return Integer.compare(column, ((TextLocation) o).getColumn());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + line;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		MutableTextLocation other = (MutableTextLocation) obj;
		if(column != other.column)
			return false;
		if(line != other.line)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return line + ":" + column;
	}
}