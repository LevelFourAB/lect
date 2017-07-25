package se.l4.lect;

import se.l4.lect.handlers.DefaultHandler;
import se.l4.lect.tokens.Token;

/**
 * Simple {@link Handler} that counts the number of words found.
 *
 * @author Andreas Holstenson
 *
 */
public class WordCountHandler
	extends DefaultHandler
{
	private final Encounter<? extends PipelineTestCollector> encounter;
	private int words;

	public WordCountHandler(Encounter<? extends PipelineTestCollector> encounter)
	{
		this.encounter = encounter;
	}

	@Override
	protected void word(Token token)
	{
		words++;
	}

	@Override
	public void done()
	{
		encounter.collector().set(words);
	}
}
