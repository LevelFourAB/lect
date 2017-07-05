package se.l4.lect;

/**
 * Virtual representation of the location of something in a {@link Source}. This interface exists so that sources
 * may potentially include location info that are not just suitable for plain text.
 *
 * @author Andreas Holstenson
 *
 */
public interface Location
	extends Comparable<Location>
{
	/**
	 * Return a string representation of this location suitable for debugging output, such as in log messages.
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
	 * Create a version of this {@link Location} that has moved a number of positions in a text.
	 *
	 * @param amount
	 * @return
	 */
	Location moveTextIndex(int amount);

	/**
	 * Quickly create a new {@link TextLocation}.
	 *
	 * @param line
	 * @param column
	 * @return
	 */
	static TextLocation text(int line, int column)
	{
		return new TextLocation(line, column);
	}
}
