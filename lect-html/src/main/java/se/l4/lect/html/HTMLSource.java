package se.l4.lect.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.RowColumnVector;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import se.l4.commons.io.Bytes;
import se.l4.commons.io.IOSupplier;
import se.l4.lect.TextSource;
import se.l4.lect.TextSourceEncounter;
import se.l4.lect.location.MutableTextOffsetLocation;

/**
 * {@link TextSource} that parses HTML.
 *
 * @author Andreas Holstenson
 *
 */
public class HTMLSource
	implements TextSource
{
	static
	{
		Config.ConvertNonBreakingSpaces = false;
	}

	private final IOSupplier<Reader> supplier;
	private final Set<String> activeAttributes;

	private HTMLSource(IOSupplier<Reader> supplier)
	{
		this.supplier = supplier;
		this.activeAttributes = new HashSet<>();
	}

	/**
	 * Create a new source for the given {@link Reader}.
	 *
	 * @param reader
	 * @return
	 */
	public static HTMLSource forReader(Reader reader)
	{
		return forReader(() -> reader);
	}

	/**
	 * Create a new source that will use the given {@link IoSupplier} to open a
	 * {@link Reader}.
	 *
	 * @param supplier
	 * @return
	 */
	public static HTMLSource forReader(IOSupplier<Reader> supplier)
	{
		return new HTMLSource(supplier);
	}

	/**
	 * Create a new source for the given {@link InputStream} using the
	 * specified character set to decode it.
	 *
	 * @param stream
	 * @param charset
	 * @return
	 */
	public static HTMLSource forStream(InputStream stream, Charset charset)
	{
		return forReader(new InputStreamReader(stream, charset));
	}

	/**
	 * Create a new source that uses the given {@link IoSupplier} to resolve
	 * the text content. The given {@link Charset} will be used to decode the
	 * stream.
	 *
	 * @param stream
	 * @param charset
	 * @return
	 */
	public static HTMLSource forStream(IOSupplier<InputStream> stream, Charset charset)
	{
		return forReader(() -> new InputStreamReader(stream.get(), charset));
	}

	/**
	 * Create a new source for the given {@link String}.
	 *
	 * @param text
	 * @return
	 */
	public static HTMLSource forString(String text)
	{
		return forReader(new StringReader(text));
	}

	/**
	 * Create a new source using the given {@link Bytes} to resolve the text
	 * content. The given {@link Charset} will be used to decode the content.
	 *
	 * @param bytes
	 * @param charset
	 * @return
	 */
	public static HTMLSource forBytes(Bytes bytes, Charset charset)
	{
		return forStream(bytes::asInputStream, charset);
	}

	/**
	 * Activate parsing of the standard attributes into paragraphs. This will
	 * handle alt, title and aria-label attributes as paragraphs.
	 *
	 * @return
	 *   this instance
	 */
	public HTMLSource withStandardAttributes()
	{
		return withAttributes(
			"title",
			"alt",
			"aria-label",
			"aria-valuetext",
			"label",
			"value",
			"summary"
		);
	}

	/**
	 * Activate parsing of some attributes.
	 *
	 * @param attrs
	 *   attributes to activate
	 * @return
	 *   this instance
	 */
	public HTMLSource withAttributes(String... attrs)
	{
		for(String attr : attrs)
		{
			activeAttributes.add(attr);
		}

		return this;
	}

	@Override
	public void parse(TextSourceEncounter encounter)
		throws IOException
	{
		new Handler(activeAttributes, encounter).parse();
	}

	private enum State
	{
		UNKNOWN,

		IGNORE,

		EXPLICIT,

		IMPLICIT
	}

	private class Handler
	{
		private final Set<String> activeAttributes;
		private final TextSourceEncounter encounter;
		private net.htmlparser.jericho.Source source;

		private int depth;

		private State state;
		private State[] stateHistory;
		private boolean lastWasSpace;

		private MutableTextOffsetLocation start;
		private MutableTextOffsetLocation end;

		private final List<Attribute> attributes;

		public Handler(Set<String> activeAttributes, TextSourceEncounter encounter)
		{
			this.activeAttributes = activeAttributes;
			this.encounter = encounter;

			state = State.UNKNOWN;

			start = new MutableTextOffsetLocation(0, 0, 0);
			end = new MutableTextOffsetLocation(0, 0, 0);

			encounter.location(start);

			stateHistory = new State[16];
			stateHistory[0] = state;

			attributes = new ArrayList<>();
		}

		public void parse()
			throws IOException
		{
			try(Reader reader = supplier.get())
			{
				source = new net.htmlparser.jericho.Source(reader);
				parse0();
			}
		}

		private void parse0()
			throws IOException
		{
			Iterator<Segment> it = source.getNodeIterator();
			StringBuilder buffer = new StringBuilder();
			while(it.hasNext())
			{
				Segment segment = it.next();
				if(segment instanceof StartTag)
				{
					// Handle all the attributes on the start tag
					handleAttributes((StartTag) segment);

					depth++;
					String name = ((StartTag) segment).getName();
					switch(state)
					{
						case UNKNOWN:
						case IMPLICIT:
							switch(name)
							{
								case "p":
								case "h1":
								case "h2":
								case "h3":
								case "h4":
								case "h5":
								case "h6":
									// Start of an explicit paragraph
									state = State.EXPLICIT;
									updateStart(segment);
									startParagraph();
									lastWasSpace = true;
									break;
								case "script":
								case "style":
								case "svg":
								case "math":
								case "del":
									// Tags that we should ignore
									state = State.IGNORE;
									break;
								case "br":
									// Breaks create a paragraph
									state = State.IMPLICIT;

									updateStart(segment);
									startParagraph();

									// Update the end location and emit a line break
									updateEnd(segment);
									encounter.location(start);
									encounter.text("\n", end);
									break;
								case "hr":
									// HR should end implicit paragraph
									if(state == State.IMPLICIT && encounter.inParagraph())
									{
										state = State.UNKNOWN;
										endParagraph();
									}
							}
							break;
						case IGNORE:
							// Nothing special to do for ignore
							break;
						case EXPLICIT:
							// Explicit <br> creates a line break
							if("br".equals(name))
							{
								updateStart(segment);
								startParagraph();

								// Update the end location and emit a line break
								updateEnd(segment);
								encounter.location(start);
								encounter.text("\n", end);

								lastWasSpace = true;
							}
					}

					pushState();
				}
				else if(segment instanceof EndTag)
				{
					depth--;

					// Protect against docs that have end tags without start tags
					if(depth < 0) depth = 0;

					// Restore the state
					State oldState = state;
					state = stateHistory[depth];

					String name = ((EndTag) segment).getName();

					boolean isSingle = false;
					switch(name)
					{
						case "br":
						case "hr":
							isSingle = true;
							break;
					}

					if(! isSingle && state != oldState && (oldState == State.EXPLICIT || oldState == State.IMPLICIT))
					{
						// Switched from explicit or implicit paragraph
						updateStart(segment);
						updateEnd(segment);
						endParagraph();
					}

					lastWasSpace = false;
				}
				else if(segment instanceof CharacterReference)
				{
					if(state == State.UNKNOWN)
					{
						startParagraph();
						state = State.IMPLICIT;
						pushState();
					}

					// TODO: Collapse white-space
					CharacterReference ref = (CharacterReference) segment;
					updateStart(ref);
					updateEnd(ref);

					buffer.setLength(0);
					ref.appendCharTo(buffer);
					encounter.location(this.start);
					encounter.text(buffer, end);
				}
				else
				{
					updateStart(segment);
					this.end.copyFrom(this.start);

					buffer.setLength(0);
					CharSequence chars = source.subSequence(segment.getBegin(), segment.getEnd());
					for(int i=0, n=chars.length(); i<n; i++)
					{
						char c = chars.charAt(i);
						if(c == '\u0020' || c == '\u0009' || c == '\n' || c == '\u000c' || c == '\r')
						{
							if(! lastWasSpace && buffer.length() > 0)
							{
								encounter.location(this.start);
								encounter.text(buffer, end);
								buffer.setLength(0);
								this.start.copyFrom(end);
							}

							// Collapse into a single space character
							if(c == '\r')
							{
								this.end.moveTo(this.end.get() + 1, this.end.getLine() + 1, 0);

								// Carriage return needs to consume any following \n and move the location
								if(i + 1 < n && chars.charAt(i+1) == '\n')
								{
									// Consume \n after \r
									i++;
									this.end.moveTextIndex(1);
								}
							}
							else if(c == '\n')
							{
								this.end.moveTo(this.end.get() + 1, this.end.getLine() + 1, 0);
							}
							else
							{
								this.end.moveTextIndex(1);
							}

							if(! lastWasSpace)
							{
								encounter.location(this.start);
								encounter.text(" ", this.end);
								lastWasSpace = true;
							}
						}
						else
						{
							if(state == State.UNKNOWN)
							{
								startParagraph();
								state = State.IMPLICIT;
								pushState();
							}

							if(lastWasSpace)
							{
								// Set the start location to be the end of the white-space
								this.start.copyFrom(this.end);
								encounter.location(this.start);
								lastWasSpace = false;
							}

							this.end.moveTextIndex(1);
							buffer.append(c);
						}
					}

					if(buffer.length() > 0)
					{
						encounter.location(this.start);
						encounter.text(buffer, end);
					}

					// Set the start location so the end of paragraph is correct for implicit paragraphs
					this.start.copyFrom(end);
				}
			}

			done();
		}

		private void pushState()
		{
			if(depth >= stateHistory.length)
			{
				stateHistory = Arrays.copyOf(stateHistory, stateHistory.length + 16);
			}
			stateHistory[depth] = state;
		}

		private void startParagraph()
		{
			if(! encounter.inParagraph())
			{
				// Flush all of the found attributes
				flushAttributes();

				encounter.location(start);
				encounter.startParagraph();
			}
		}

		private void endParagraph()
		{
			encounter.location(start);
			encounter.endParagraph();
			lastWasSpace = false;
		}

		private void done()
		{
			if(encounter.inParagraph())
			{
				endParagraph();
			}

			// Flush all of the found attributes
			flushAttributes();

			encounter.done();
		}

		private void updateStart(Segment s)
		{
			RowColumnVector rv = source.getRowColumnVector(s.getBegin());
			start.moveTo(s.getBegin(), rv.getRow() - 1, rv.getColumn() - 1);
		}

		private void updateEnd(Segment s)
		{
			RowColumnVector rv = source.getRowColumnVector(s.getEnd());
			end.moveTo(s.getEnd(), rv.getRow() - 1, rv.getColumn() - 1);
		}

		/**
		 * Handle the attributes on the start tag. Will go through and copy
		 * the tags for flushing later.
		 */
		private void handleAttributes(StartTag s)
		{
			Attributes attrs = s.getAttributes();
			if(attrs == null) return;

			for(Attribute attr : attrs)
			{
				if(activeAttributes.contains(attr.getName()))
				{
					attributes.add(attr);
				}
			}
		}

		/**
		 * Flush all of the attributes in their own paragraphs.
		 */
		private void flushAttributes()
		{
			if(attributes.isEmpty()) return;

			MutableTextOffsetLocation startTemp = start.copy();
			MutableTextOffsetLocation endTemp = end.copy();

			for(Attribute attr : attributes)
			{
				// Create a mini-paragraph for the attribute
				updateStart(attr);
				encounter.location(start);
				encounter.startParagraph();

				// Extract the value
				Segment value = attr.getValueSegment();

				// Set the start location of the attribute value
				updateStart(value);
				encounter.location(start);

				// Fetch the end location and add the text value
				updateEnd(value);
				encounter.text(attr.getValue(), end);

				// End the mini-paragraph
				updateEnd(attr);
				encounter.location(end);
				encounter.endParagraph();
			}

			attributes.clear();

			start.copyFrom(startTemp);
			end.copyFrom(endTemp);
		}

	}
}
