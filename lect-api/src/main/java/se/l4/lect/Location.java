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
}
