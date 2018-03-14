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

	/**
	 * Create a version of this {@link Location} that has moved a number of
	 * positions in a text.
	 *
	 * @param amount
	 * @return
	 */
	OffsetLocation moveTextIndex(int amount);

	/**
	 * Move the text index according to the given sequence of characters. 
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @return
	 */
	OffsetLocation moveTextIndex(CharSequence sequence);

	/**
	 * Move the text index according to the given sequence of characters.
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @param offset
	 * @param length
	 * @return
	 */
	OffsetLocation moveTextIndex(CharSequence sequence, int offset, int length);

}
