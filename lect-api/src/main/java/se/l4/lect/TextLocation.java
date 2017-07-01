package se.l4.lect;

/**
 * {@link Location} suitable for use with plain text formats.
 *
 * @author Andreas Holstenson
 *
 */
public class TextLocation
	implements Location
{
	private final int line;
	private final int column;

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
