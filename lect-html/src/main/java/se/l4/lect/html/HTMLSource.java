package se.l4.lect.html;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;

import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.RowColumnVector;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import se.l4.lect.Source;
import se.l4.lect.SourceEncounter;
import se.l4.lect.TextLocation;

/**
 * {@link Source} that parses HTML.
 *
 * @author Andreas Holstenson
 *
 */
public class HTMLSource
	implements Source
{
	static
	{
		Config.ConvertNonBreakingSpaces = false;
	}

	private final Reader reader;

	private HTMLSource(Reader reader)
	{
		this.reader = reader;
	}

	public static Source forReader(Reader reader)
	{
		return new HTMLSource(reader);
	}

	public static Source forString(String s)
	{
		return new HTMLSource(new StringReader(s));
	}

	@Override
	public void parse(SourceEncounter encounter)
		throws IOException
	{
		new Handler(encounter).parse();
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
		private final SourceEncounter encounter;
		private net.htmlparser.jericho.Source source;

		private int depth;

		private State state;
		private State[] stateHistory;
		private boolean lastWasSpace;

		private TextLocation start;
		private TextLocation end;

		public Handler(SourceEncounter encounter)
		{
			this.encounter = encounter;

			state = State.UNKNOWN;

			start = new TextLocation(0, 0);
			end = new TextLocation(0, 0);

			encounter.location(start);

			stateHistory = new State[16];
			stateHistory[0] = state;
		}

		public void parse()
			throws IOException
		{
			source = new net.htmlparser.jericho.Source(reader);
			Iterator<Segment> it = source.getNodeIterator();
			StringBuilder buffer = new StringBuilder();
			while(it.hasNext())
			{
				Segment segment = it.next();
				if(segment instanceof StartTag)
				{
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
					if(state == State.UNKNOWN)
					{
						updateStart(segment);
						startParagraph();
						state = State.IMPLICIT;
						pushState();
					}

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
								this.end.moveTo(this.end.getLine() + 1, 0);

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
								this.end.moveTo(this.end.getLine() + 1, 0);
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

			encounter.done();
		}

		private void updateStart(Segment s)
		{
			RowColumnVector rv = source.getRowColumnVector(s.getBegin());
			start.moveTo(rv.getRow() - 1, rv.getColumn() - 1);
		}

		private void updateEnd(Segment s)
		{
			RowColumnVector rv = source.getRowColumnVector(s.getEnd());
			end.moveTo(rv.getRow() - 1, rv.getColumn() - 1);
		}
	}
}
