package se.l4.lect.location;

/**
 * {@link OffsetLocation} that is mutable.
 *
 * @author Andreas Holstenson
 *
 */
public class MutableOffsetLocation
	implements OffsetLocation
{
	private int index;

	public MutableOffsetLocation()
	{
		this(0);
	}

	public MutableOffsetLocation(int index)
	{
		this.index = index;
	}

	@Override
	public int get()
	{
		return index;
	}

	public void set(int index)
	{
		this.index = index;
	}

	@Override
	public MutableOffsetLocation moveTextIndex(int amount)
	{
		this.index += amount;
		return this;
	}

	@Override
	public MutableOffsetLocation moveTextIndex(CharSequence sequence)
	{
		return moveTextIndex(sequence.length());
	}

	@Override
	public MutableOffsetLocation moveTextIndex(CharSequence sequence, int offset, int length)
	{
		return moveTextIndex(length);
	}

	@Override
	public OffsetLocation copy()
	{
		return new MutableOffsetLocation(index);
	}

	@Override
	public int compareTo(Location o)
	{
		if(! (o instanceof OffsetLocation))
		{
			throw new IllegalArgumentException("Trying to compare to incompatible location: " + o);
		}

		return Integer.compare(index, ((OffsetLocation) o).get());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof OffsetLocation))
			return false;
		OffsetLocation other = (OffsetLocation) obj;
		if(index != other.get())
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "@" + index;
	}
}
