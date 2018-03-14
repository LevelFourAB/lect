package se.l4.lect;

/**
 * Interface for creating instances of a {@link Handler}.
 *
 * @author Andreas Holstenson
 *
 * @param <Collector>
 */
public interface HandlerFactory<Collector>
{
	/**
	 * Create a handler for the given encounter.
	 *
	 * @param encounter
	 *   the encounter for which to create a handler
	 * @return
	 *   created handler
	 */
	Handler create(Encounter<? extends Collector> encounter);
}
