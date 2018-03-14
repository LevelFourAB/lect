package se.l4.lect.location;

/**
 * Mutable {@link TextLocation} for use in cases where line and column is important.
 *
 * @author Andreas Holstenson
 *
 */
public class MutableTextOffsetLocation
	implements TextOffsetLocation
{
	private int offset;
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
	public MutableTextOffsetLocation(int offset, int line, int column)
	{
		this.offset = offset;
		this.line = line;
		this.column = column;
	}

	@Override
	public int get()
	{
		return offset;
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
	public MutableTextOffsetLocation moveTo(int offset, int line, int column)
	{
		this.offset = offset;
		this.line = line;
		this.column = column;
		return this;
	}

	@Override
	public MutableTextOffsetLocation moveTo(int line, int column)
	{
		throw new UnsupportedOperationException();
	}

	public MutableTextOffsetLocation copyFrom(MutableTextOffsetLocation location)
	{
		this.offset = location.offset;
		this.line = location.line;
		this.column = location.column;
		return this;
	}

	@Override
	public MutableTextOffsetLocation copy()
	{
		return new MutableTextOffsetLocation(offset, line, column);
	}

	@Override
	public MutableTextOffsetLocation moveTextIndex(int amount)
	{
		this.offset += amount;
		this.column += amount;
		return this;
	}

	@Override
	public MutableTextOffsetLocation moveTextIndex(CharSequence sequence)
	{
		return moveTextIndex(sequence, 0, sequence.length());
	}

	@Override
	public MutableTextOffsetLocation moveTextIndex(CharSequence sequence, int offset, int length)
	{
		for(int i=offset, n=offset + length; i<n; i++)
		{
			char c = sequence.charAt(i);
			if(c == '\r')
			{
				this.offset++;
				line++;
				column = 0;
				if(i + 1 < n && sequence.charAt(i + 1) == '\n')
				{
					// Consume \n after \r
					i++;
					this.offset++;
					continue;
				}
			}
			else if(c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085')
			{
				this.offset++;
				line++;
				column = 0;
			}
			else
			{
				this.offset++;
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
		if(!(obj instanceof TextLocation))
			return false;
		TextLocation other = (TextLocation) obj;
		if(column != other.getColumn())
			return false;
		if(line != other.getLine())
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return line + ":" + column + "(@" + offset + ')';
	}
}