package se.l4.lect.location;

/**
 * Location representing an offset in the source.
 *
 * @author Andreas Holstenson
 *
 */
public interface OffsetLocation
	extends Location
{
	/**
	 * Get the offset.
	 *
	 * @return
	 */
	int get();

	/**
	 * Create a copy of this location.
	 *
	 * @return
	 */
	@Override
	OffsetLocation copy();
}
