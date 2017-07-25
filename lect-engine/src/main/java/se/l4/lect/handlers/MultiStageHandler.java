package se.l4.lect.handlers;

import java.util.Collection;

import se.l4.lect.Handler;
import se.l4.lect.TextSource;

/**
 * Extension to {@link Handler} for those handlers that need to run on {@link TextSource} more than once.
 *
 * @author Andreas Holstenson
 *
 */
public interface MultiStageHandler
	extends Handler
{
	/**
	 * Get if there any more stages needed.
	 *
	 * @return
	 */
	boolean hasMoreStages();

	/**
	 * Create a handler that runs the given stages in order.
	 *
	 * @param stages
	 * @return
	 */
	public static Handler withStages(Handler... stages)
	{
		return new SimpleMultiStageHandler(stages);
	}

	/**
	 * Create a handler that runs the given stages in order.
	 *
	 * @param stages
	 * @return
	 */
	public static Handler withStages(Collection<Handler> stages)
	{
		return new SimpleMultiStageHandler(stages);
	}
}
