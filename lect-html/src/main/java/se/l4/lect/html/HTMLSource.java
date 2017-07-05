package se.l4.lect.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import nu.validator.htmlparser.sax.HtmlParser;
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

	/**
	 * Peek into the given reader to try to figure out if this is a full HTML document.
	 *
	 * @param r
	 * @return
	 * @throws IOException
	 */
	private boolean isDocument(BufferedReader r)
		throws IOException
	{
		int i = 0;
		int c;
		while((c = r.read()) != -1)
		{
			// Always read whitespace at the beginning
			if(Character.isWhitespace(c)) continue;

			// Read more than 100 chars of whitespace, treat this as a fragment
			if(i++ > 100) return false;

			// Break if start of tag
			if(c == '<') break;

			// All other chars indicate it's not a fragment
			return false;
		}

		c = r.read();
		if(c == 'h')
		{
			return r.read() == 't' && r.read() == 'm' && r.read() == 'l';
		}
		else if(c == '!')
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void parse(SourceEncounter encounter)
		throws IOException
	{
		HtmlParser parser = new HtmlParser();
		try
		{
			Handler handler = new Handler(encounter);
			parser.setContentHandler(handler);
			parser.setLexicalHandler(handler);

			BufferedReader buffered = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader, 1024);
			buffered.mark(1024);
			boolean isFullDoc = isDocument(buffered);
			buffered.reset();

			if(isFullDoc)
			{
				parser.parse(new InputSource(buffered));
			}
			else
			{
				parser.parseFragment(new InputSource(buffered), "");
			}

			handler.done();
		}
		catch(SAXException e)
		{
			throw new IOException("Unable to parse HTML; " + e.getMessage(), e);
		}
	}

	private enum State
	{
		UNKNOWN,

		IGNORE,

		EXPLICIT,

		IMPLICIT
	}

	private class Handler
		extends DefaultHandler
		implements LexicalHandler
	{
		private final SourceEncounter encounter;

		private Locator locator;

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

			start = new TextLocation(0, 0, -1);
			end = new TextLocation(0, 0, -1);

			encounter.location(start);

			stateHistory = new State[16];
			stateHistory[0] = state;
		}

		private void pushState()
		{
			if(depth >= stateHistory.length)
			{
				stateHistory = Arrays.copyOf(stateHistory, stateHistory.length + 16);
			}
			stateHistory[depth] = state;
		}

		@Override
		public void setDocumentLocator(Locator locator)
		{
			this.locator = locator;
		}

		private void updateStart()
		{
			if(locator.getLineNumber() < 0) return;

			start.moveTo(locator.getLineNumber() - 1, locator.getColumnNumber() - 1, -1);
			encounter.location(start);
		}

		private void updateEnd()
		{
			if(locator.getLineNumber() < 0) return;

			end.moveTo(locator.getLineNumber() - 1, locator.getColumnNumber() - 1, -1);
		}

		private void startParagraph()
		{
			if(! encounter.inParagraph())
			{
				encounter.startParagraph();
			}
		}

		private void endParagraph()
		{
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

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException
		{
			depth++;
			switch(state)
			{
				case UNKNOWN:
				case IMPLICIT:
					switch(localName)
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
							updateStart();
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

							startParagraph();

							// Update the end location and emit a line break
							updateEnd();
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
				case EXPLICIT:
					// Nothing special to do for other states
			}

			// TODO: If attributes should be kept push them onto a pending paragraph list

			// Make sure the state is recorded
			pushState();

			// Update the start location with our location
			updateStart();
		}

		@Override
		public void endElement(String uri, String localName, String qName)
			throws SAXException
		{
			depth--;

			// Restore the state
			State oldState = state;
			state = stateHistory[depth];

			boolean isSingle = false;
			switch(localName)
			{
				case "br":
				case "hr":
					isSingle = true;
					break;
			}

			if(! isSingle && state != oldState && (oldState == State.EXPLICIT || oldState == State.IMPLICIT))
			{
				// Switched from explicit or implicit paragraph
				updateEnd();
				endParagraph();
			}

			updateStart();

			// TODO: Flush pending paragraphs
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
		{
			updateStart();
		}

		@Override
		public void characters(char[] ch, int start, int length)
			throws SAXException
		{
			if(state == State.UNKNOWN)
			{
				startParagraph();
				state = State.IMPLICIT;
				pushState();
			}

			// TODO: White-space shouldn't be collapsed at all times

			StringBuilder cleaned = new StringBuilder(length);
			for(int i=start, n=start+length; i<n; i++)
			{
				char c = ch[i];
				if(Character.isWhitespace(c))
				{
					if(c == '\u00a0' || c == '\u202f' || c == '\ufeff')
					{
						// These are non-breaking spaces, push them onto the result
						lastWasSpace = true;
						cleaned.append(' ');
					}
					else
					{
						// Normal space, skip if last was a space
						if(lastWasSpace) continue;

						lastWasSpace = true;
						cleaned.append(' ');
					}
				}
				else
				{
					cleaned.append(c);
				}
			}

			updateEnd();
			encounter.text(cleaned, end);
			updateStart();
		}

		@Override
		public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
				throws SAXException
		{
		}

		@Override
		public void startEntity(String name) throws SAXException
		{
		}

		@Override
		public void endEntity(String name) throws SAXException
		{
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException
		{
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException
		{
		}

		@Override
		public void startDTD(String name, String publicId, String systemId) throws SAXException
		{
		}

		@Override
		public void endDTD() throws SAXException
		{
		}

		@Override
		public void startCDATA() throws SAXException
		{
			updateStart();
		}

		@Override
		public void endCDATA() throws SAXException
		{
			updateStart();
		}

		@Override
		public void comment(char[] ch, int start, int length) throws SAXException
		{
			updateStart();
		}
	}
}
