package se.l4.lect.handlers;

import se.l4.lect.Handler;
import se.l4.lect.location.Location;
import se.l4.lect.tokens.Token;

/**
 * {@link MultiStageHandler} that delegates to a {@link Handler} for each stage.
 *
 * @author Andreas Holstenson
 *
 */
public abstract class DelegatingMultiStageHandler
	implements MultiStageHandler
{
	private Handler handler;

	protected abstract Handler next();

	@Override
	public void start()
	{
		if(handler == null)
		{
			handler = next();
		}
		handler.start();
	}

	@Override
	public void startParagraph(Location location)
	{
		handler.startParagraph(location);
	}

	@Override
	public void endParagraph(Location location)
	{
		handler.endParagraph(location);
	}

	@Override
	public void startSentence(Location location)
	{
		handler.startSentence(location);
	}

	@Override
	public void endSentence(Location location)
	{
		handler.endSentence(location);
	}

	@Override
	public void token(Token token)
	{
		handler.token(token);
	}

	@Override
	public void done()
	{
		handler.done();

		handler = next();
	}

	@Override
	public boolean hasMoreStages()
	{
		return handler != null;
	}

}
