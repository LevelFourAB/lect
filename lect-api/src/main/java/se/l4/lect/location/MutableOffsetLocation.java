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
	public Location moveTextIndex(int amount)
	{
		this.index += amount;
		return this;
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

}
