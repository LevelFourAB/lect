package se.l4.lect.text;

import java.io.IOException;

import se.l4.lect.TextSource;
import se.l4.lect.TextSourceEncounter;
import se.l4.lect.location.MutableOffsetLocation;
import se.l4.lect.location.OffsetLocation;

/**
 * Source that treats a {@link String} as a single paragraph. Used when
 * working with text that is already split into paragraphs. This
 * {@link TextSource} emits {@link OffsetLocation}s.
 *
 * @author Andreas Holstenson
 *
 */
public class StringTextSource
	implements TextSource
{
	private final String text;

	private StringTextSource(String text)
	{
		this.text = text;
	}

	/**
	 * Create a new source for the given {@link String}.
	 *
	 * @param text
	 * @return
	 */
	public static TextSource forString(String text)
	{
		return new StringTextSource(text);
	}

	@Override
	public void parse(TextSourceEncounter encounter)
		throws IOException
	{
		MutableOffsetLocation location = new MutableOffsetLocation();
		encounter.location(location);
		encounter.startParagraph();

		encounter.text(text, new MutableOffsetLocation(text.length()));

		location.set(text.length());
		encounter.location(location);
		encounter.endParagraph();

		encounter.done();
	}
}
