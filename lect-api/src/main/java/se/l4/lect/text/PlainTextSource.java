package se.l4.lect.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import se.l4.lect.Source;
import se.l4.lect.SourceEncounter;
import se.l4.lect.TextLocation;

/**
 * Source that parses plain text into a syntax tree.
 *
 * @author Andreas Holstenson
 *
 */
public class PlainTextSource
	implements Source
{
	private final Readable readable;

	private PlainTextSource(Readable readable)
	{
		this.readable = readable;
	}

	/**
	 * Create a new source for the given {@link Reader}.
	 *
	 * @param reader
	 * @return
	 */
	public static Source forReader(Readable readable)
	{
		return new PlainTextSource(readable);
	}

	/**
	 * Create a new source for the given {@link InputStream} using the specified
	 * character set to decode it.
	 *
	 * @param stream
	 * @param charset
	 * @return
	 */
	public static Source forStream(InputStream stream, Charset charset)
	{
		return forReader(new InputStreamReader(stream, charset));
	}

	/**
	 * Create a new source for the given {@link String}.
	 *
	 * @param text
	 * @return
	 */
	public static Source forString(String text)
	{
		return forReader(new StringReader(text));
	}

	@Override
	public void parse(SourceEncounter encounter)
		throws IOException
	{
		new Parser(readable, encounter).parse();
	}

	private static boolean isWhitespace(StringBuilder builder)
	{
		for(int i=0, n=builder.length(); i<n; i++)
		{
			if(! Character.isWhitespace(builder.charAt(i)))
			{
				return false;
			}
		}

		return true;
	}

	private enum State
	{
		TEXT,
		LINE_BREAK,
		PENDING_PARAGRAPH
	}

	private static class Parser
	{
		private final Readable readable;
		private final SourceEncounter encounter;

		private int index;
		private int line;
		private int column;

		private TextLocation location;
		private TextLocation lastLocation;

		private State state;
		private StringBuilder builder;

		public Parser(Readable readable, SourceEncounter encounter)
		{
			this.readable = readable;
			this.encounter = encounter;

			state = State.LINE_BREAK;
			builder = new StringBuilder();
		}

		public void parse()
			throws IOException
		{
			boolean lastWasCarriageReturn = false;

			location = new TextLocation(0, 0, 0);
			lastLocation = new TextLocation(0, 0, 0);
			encounter.location(location);

			int length;
			CharBuffer buffer = CharBuffer.allocate(1024);
			while((length = readable.read(buffer)) != -1)
			{
				buffer.flip();
				for(int i=0; i<length; i++)
				{
					char c = buffer.charAt(i);
					if(c == '\r' || c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085')
					{
						// This some form of line break
						if(c == '\r')
						{
							lastWasCarriageReturn = true;
						}
						else if(c == '\n' && lastWasCarriageReturn)
						{
							// Newline after carriage return should just be added to the buffer
							lastWasCarriageReturn = false;
							index++;
							builder.append(c);
							continue;
						}
						else
						{
							lastWasCarriageReturn = false;
						}

						if(state == State.LINE_BREAK)
						{
							// Already in a line break, we are going to create a new paragraph
							state = State.PENDING_PARAGRAPH;
						}
						else
						{
							flush();
							state = State.LINE_BREAK;
						}

						// Set the current position
						line++;
						column = 0;
						index++;

						// Append to the buffer
						builder.append(c);
					}
					else if(Character.isWhitespace(c))
					{
						// This is some whitespace, always append to the buffer and do not switch state
						builder.append(c);
						column++;
						index++;
					}
					else
					{
						// Anything else is appended to the buffer while creating a paragraph if needed
						if(state != State.TEXT)
						{
							flush();
							state = State.TEXT;
						}

						if(! encounter.inParagraph())
						{
							encounter.startParagraph();
						}

						column++;
						index++;
						builder.append(c);
					}
				}
			}

			if(isWhitespace(builder))
			{
				// If all we have left is whitespace, end any paragraphs and flush
				flush();
			}
			else if(builder.length() > 0)
			{
				if(state != State.TEXT)
				{
					flush();
					state = State.TEXT;
				}

				if(! encounter.inParagraph())
				{
					encounter.startParagraph();
				}

				flush();

			}

			if(encounter.inParagraph())
			{
				encounter.endParagraph();
			}

			encounter.done();
		}

		private void flushLocation()
		{
			location.moveTo(line, column, index);
			lastLocation.copyFrom(location);
			encounter.location(location);
		}

		private void flush()
		{
			if(state == State.PENDING_PARAGRAPH && encounter.inParagraph())
			{
				/*
				 * Pending paragraphs need some special handling as the first newline should be part of the previous
				 * paragraph and anything else should be outside of the paragraph.
				 */
				int offset = 1;
				char c = builder.charAt(0);
				if(c == '\r' && builder.length() > 1 && builder.charAt(1) == '\n')
				{
					// Pair or carriage return + newline
					offset = 2;
				}

				String text = builder.substring(0, offset);
				TextLocation end = location.copy().moveTo(
					lastLocation.getLine() + 1,
					0,
					lastLocation.getIndex() + offset
				);
				encounter.text(text, end);

				// Update the location and end the paragraph
				location.moveTo(lastLocation.getLine() + 1, 0, lastLocation.getIndex() + offset);
				encounter.location(location);
				encounter.endParagraph();

				if(builder.length() > offset)
				{
					text = builder.substring(offset);

					encounter.text(text, lastLocation.copy().moveTextIndex(builder));
				}

				builder.setLength(0);
			}
			else if(builder.length() > 0)
			{
				String text = builder.toString();
				encounter.text(text, lastLocation.copy().moveTextIndex(builder));
				builder.setLength(0);
			}

			flushLocation();
		}
	}
}
