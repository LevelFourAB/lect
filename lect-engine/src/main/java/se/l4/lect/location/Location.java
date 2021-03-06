package se.l4.lect.location;

import se.l4.lect.TextSource;

/**
 * Virtual representation of the location of something in a {@link TextSource}.
 * This interface exists so that sources may potentially include location info
 * that are not just suitable for plain text.
 *
 * @author Andreas Holstenson
 *
 */
public interface Location
	extends Comparable<Location>
{
	/**
	 * Return a string representation of this location suitable for debugging
	 * output, such as in log messages.
	 *
	 * @return
	 */
	@Override
	String toString();

	/**
	 * Create a copy of this location.
	 *
	 * @return
	 */
	Location copy();

	/**
	 * Get if this location comes after another location.
	 *
	 * @param other
	 * @return
	 */
	default boolean isAfter(Location other)
	{
		return compareTo(other) > 0;
	}

	/**
	 * Get if this location is the same or after another location.
	 *
	 * @param other
	 * @return
	 */
	default boolean isSameOrAfter(Location other)
	{
		return compareTo(other) >= 0;
	}

	/**
	 * Get if this location comes before another location.
	 *
	 * @param other
	 * @return
	 */
	default boolean isBefore(Location other)
	{
		return compareTo(other) < 0;
	}

	/**
	 * Get if this location is the same or before another location.
	 *
	 * @param other
	 * @return
	 */
	default boolean isSameOrBefore(Location other)
	{
		return compareTo(other) <= 0;
	}

	/**
	 * Move the text index according to the given sequence of characters. 
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @return
	 */
	Location moveTextIndex(CharSequence sequence);

	/**
	 * Move the text index according to the given sequence of characters.
	 * Returns itself if mutable or a copy if the instance is immutable.
	 *
	 * @param sequence
	 * @param offset
	 * @param length
	 * @return
	 */
	Location moveTextIndex(CharSequence sequence, int offset, int length);

	/**
	 * Quickly create a new {@link MutableTextOffsetLocation}.
	 *
	 * @param line
	 * @param column
	 * @return
	 */
	static TextLocation text(int offset, int line, int column)
	{
		return new MutableTextOffsetLocation(offset, line, column);
	}

	/**
	 * Quickly create a new {@link MutableOffsetLocation}.
	 *
	 * @param index
	 * @return
	 */
	static OffsetLocation offset(int index)
	{
		return new MutableOffsetLocation(index);
	}
}
