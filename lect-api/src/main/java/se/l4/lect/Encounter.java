package se.l4.lect;

import java.util.Locale;

/**
 * Information provided to {@link Handler}s when they are part of a {@link Pipeline}.
 *
 * @author Andreas Holstenson
 *
 */
public interface Encounter
{
	/**
	 * Get the locale is being processed.
	 *
	 * @return
	 */
	Locale locale();

	/**
	 * Add something to the collected results.
	 *
	 * @param o
	 */
	void add(Object o);
}
