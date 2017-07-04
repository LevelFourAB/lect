package se.l4.lect;

import se.l4.lect.handlers.DefaultHandler;

/**
 * Simple {@link Handler} that counts the number of words found.
 *
 * @author Andreas Holstenson
 *
 */
public class WordCountHandler
	extends DefaultHandler
{
	private final Encounter encounter;
	private int words;

	public WordCountHandler(Encounter encounter)
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
		encounter.add(words);
	}
}
