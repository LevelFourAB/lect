package se.l4.lect.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import se.l4.lect.Handler;

/**
 * {@link MultiStageHandler} that takes a collection of {@link Handler}s to run
 * after each other.
 *
 * @author Andreas Holstenson
 *
 */
public class SimpleMultiStageHandler
	extends DelegatingMultiStageHandler
{
	private final Iterator<Handler> stages;

	public SimpleMultiStageHandler(Collection<Handler> stages)
	{
		this.stages = stages.iterator();
	}

	public SimpleMultiStageHandler(Handler... handlers)
	{
		this(Arrays.asList(handlers));
	}

	@Override
	protected Handler next()
	{
		return stages.hasNext() ? stages.next() : null;
	}
}
