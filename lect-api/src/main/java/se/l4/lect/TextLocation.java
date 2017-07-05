package se.l4.lect;

/**
 * Mutable {@link Location} suitable for use with plain text formats.
 *
 * @author Andreas Holstenson
 *
 */
public class TextLocation
	implements Location
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
	public TextLocation(int line, int column)
	{
		this.line = line;
		this.column = column;
	}

	public int getLine()
	{
		return line;
	}

	public int getColumn()
	{
		return column;
	}

	public TextLocation moveTo(int line, int column)
	{
		this.line = line;
		this.column = column;
		return this;
	}

	public TextLocation copyFrom(TextLocation location)
	{
		this.line = location.line;
		this.column = location.column;
		return this;
	}

	@Override
	public TextLocation copy()
	{
		return new TextLocation(line, column);
	}

	@Override
	public TextLocation moveTextIndex(int amount)
	{
		this.column += amount;
		return this;
	}

	public TextLocation moveTextIndex(CharSequence sequence)
	{
		return moveTextIndex(sequence, 0, sequence.length());
	}

	public TextLocation moveTextIndex(CharSequence sequence, int offset, int length)
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

		int c = Integer.compare(line, ((TextLocation) o).line);
		if(c != 0) return c;

		return Integer.compare(column, ((TextLocation) o).column);
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
		TextLocation other = (TextLocation) obj;
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