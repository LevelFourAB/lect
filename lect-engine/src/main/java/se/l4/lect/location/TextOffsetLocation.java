package se.l4.lect.location;

/**
 * Location representing an offset and a text location in the source.
 *
 * @author Andreas Holstenson
 *
 */
public interface TextOffsetLocation
	extends TextLocation, OffsetLocation
{
	/**
	 * Copy this location.
	 */
	TextOffsetLocation copy();

	/**
	 * Move the offset, line and column of this location, returning either
	 * itself if mutable or a copy if this instance is immutable.
	 * 
	 * @param offset
	 * @param line
	 * @param column
	 * @return
	 *   new instance if immutable or self if mutable
	 */
	TextOffsetLocation moveTo(int offset, int line, int column);

	/**
	 * Move the text index according to the given sequence of characters. 
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @return
	 */
	TextOffsetLocation moveTextIndex(CharSequence sequence);

	/**
	 * Move the text index according to the given sequence of characters.
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @param offset
	 * @param length
	 * @return
	 */
	TextOffsetLocation moveTextIndex(CharSequence sequence, int offset, int length);

}