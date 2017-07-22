package se.l4.lect;

import java.util.Locale;

import se.l4.lect.location.Location;
import se.l4.lect.tokens.MutableToken;
import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.TokenType;

/**
 * {@link LanguageFactory} that just emits entire paragraphs as a {@link Token}.
 *
 * @author Andreas Holstenson
 *
 */
public class ParagraphLanguage
	implements LanguageFactory
{
	private static final LanguageFactory INSTANCE = new ParagraphLanguage();

	private ParagraphLanguage()
	{
	}

	public static LanguageFactory get()
	{
		return INSTANCE;
	}

	@Override
	public LanguageParser create(LanguageEncounter encounter)
	{
		return new LanguageParser()
		{
			private final StringBuilder text = new StringBuilder();
			private final MutableToken token = new MutableToken();
			private Location start;
			private Location end;

			@Override
			public void text(CharSequence text, Location start, Location end)
			{
				if(this.start == null)
				{
					this.start = start.copy();
				}

				this.end = end;
				this.text.append(text);
			}

			@Override
			public Locale locale()
			{
				return Locale.ENGLISH;
			}

			@Override
			public void flush()
			{
				if(this.start == null) return;

				token.update(TokenType.UNKNOWN, start, end, text);
				encounter.token(token);

				start = null;
				text.setLength(0);
			}
		};
	}

}
