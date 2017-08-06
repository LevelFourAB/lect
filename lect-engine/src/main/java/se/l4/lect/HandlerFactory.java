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
	 * @return
	 */
	Handler create(Encounter<? extends Collector> encounter);
}
