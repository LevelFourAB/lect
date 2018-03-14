package se.l4.lect.location;

/**
 * {@link Location} for text, where line and column is the important thing to keep track of. This location uses
 * zero-indexed lines and columns.
 *
 * @author Andreas Holstenson
 *
 */
public interface TextLocation
	extends Location
{

	/**
	 * Get the line this location represents.
	 *
	 * @return
	 *   zero-indexed line
	 */
	int getLine();

	/**
	 * Get the column this location represents.
	 *
	 * @return
	 */
	int getColumn();

	/**
	 * Create a copy of this location.
	 */
	@Override
	TextLocation copy();

	/**
	 * Move the line and column of this location, returning either itself if 
	 * utable or a copy if this instance is immutable.
	 *
	 * @param line
	 * @param column
	 * @return
	 */
	TextLocation moveTo(int line, int column);

	/**
	 * Move the text index according to the given sequence of characters.
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @return
	 */
	TextLocation moveTextIndex(CharSequence sequence);

	/**
	 * Move the text index according to the given sequence of characters.
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @param offset
	 * @param length
	 * @return
	 */
	TextLocation moveTextIndex(CharSequence sequence, int offset, int length);

}