package se.l4.lect.internal;

import java.io.IOException;

import se.l4.lect.TextSource;
import se.l4.lect.TextSourceEncounter;

/**
 * {@link TextSource} that reads and emits nothing.
 *
 * @author Andreas Holstenson
 *
 */
public class EmptyTextSource
	implements TextSource
{
	public static final TextSource INSTANCE = new EmptyTextSource();

	private EmptyTextSource()
	{
	}

	@Override
	public void parse(TextSourceEncounter encounter)
		throws IOException
	{
		encounter.done();
	}
}
