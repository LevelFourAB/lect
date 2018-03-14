package se.l4.lect;

import java.util.Locale;

/**
 * Information provided to {@link Handler}s when they are part of a
 * {@link Pipeline}.
 *
 * @author Andreas Holstenson
 *
 */
public interface Encounter<Collector>
{
	/**
	 * Get the locale is being processed.
	 *
	 * @return
	 */
	Locale locale();

	/**
	 * Get the object used to collect results from this execution.
	 *
	 * @return
	 */
	Collector collector();
}
