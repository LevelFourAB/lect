package se.l4.lect.markdown;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.HtmlEntity;
import com.vladsch.flexmark.ast.HtmlInline;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.ListBlock;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.MailLink;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.WhiteSpace;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;
import com.vladsch.flexmark.ext.gfm.strikethrough.Subscript;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.html.Html5Entities;
import com.vladsch.flexmark.util.options.MutableDataSet;

import se.l4.lect.Source;
import se.l4.lect.SourceEncounter;
import se.l4.lect.TextLocation;
import se.l4.lect.text.OffsetTrackingReader;

/**
 * {@link Source} that parses Markdown.
 *
 * @author Andreas Holstenson
 *
 */
public class MarkdownSource
	implements Source
{
	private final Reader reader;
	private CustomMarkdownFlavor flavor;

	private MarkdownSource(Reader reader)
	{
		this.reader = reader;
		flavor = MarkdownFlavor.COMMONMARK;
	}

	public static MarkdownSource forReader(Reader reader)
	{
		return new MarkdownSource(reader);
	}

	public static MarkdownSource forString(String s)
	{
		return new MarkdownSource(new StringReader(s));
	}

	/**
	 * Set the flavor to use when parsing.
	 *
	 * @param flavor
	 * @return
	 */
	public MarkdownSource withFlavor(MarkdownFlavor flavor)
	{
		return withFlavor((CustomMarkdownFlavor) flavor);
	}

	/**
	 * Set the flavor to use when parsing.
	 *
	 * @param flavor
	 * @return
	 */
	public MarkdownSource withFlavor(CustomMarkdownFlavor flavor)
	{
		this.flavor = flavor;
		return this;
	}

	@Override
	public void parse(SourceEncounter encounter)
		throws IOException
	{
		new Handler(encounter).parse();
	}

	private class Handler
	{
		private final SourceEncounter encounter;
		private OffsetTrackingReader reader;

		private TextLocation start;
		private TextLocation end;

		public Handler(SourceEncounter encounter)
		{
			this.encounter = encounter;

			start = new TextLocation(0, 0);
			end = new TextLocation(0, 0);

			encounter.location(start);

			reader = new OffsetTrackingReader(MarkdownSource.this.reader);
		}

		public void parse()
			throws IOException
		{
			MutableDataSet options = new MutableDataSet();
			flavor.applyMarkdownOptions(options);

			Parser parser = Parser.builder(options).build();
			Document doc = parser.parseReader(reader);

			handleChildren(doc);

			encounter.done();
		}

		private void updateLocation(TextLocation target, int offset)
		{
			reader.offsetToLocation(offset, target);
		}

		private void updateAndSetStartLocation(Node node)
		{
			updateLocation(start, node.getStartOffset());
			encounter.location(start);
		}

		private void updateStartLocation(Node node)
		{
			updateLocation(start, node.getStartOffset());
		}

		private void updateAndSetEndLocation(Node node)
		{
			updateLocation(end, node.getEndOffset());
			encounter.location(end);
		}

		private void updateEndLocation(Node node)
		{
			updateLocation(end, node.getEndOffset());
		}

		private void handleNode(Node node)
		{
			if(node instanceof Paragraph || node instanceof Heading)
			{
				updateAndSetStartLocation(node);
				encounter.startParagraph();

				handleChildren(node);

				updateAndSetEndLocation(node);
				encounter.endParagraph();
			}
			else if(node instanceof BlockQuote || node instanceof ListBlock || node instanceof ListItem)
			{
				handleChildren(node);
			}
			else if(node instanceof WhiteSpace)
			{
				updateAndSetStartLocation(node);
				updateEndLocation(node);
				encounter.text(node.getChars(), end);
			}
			else if(encounter.inParagraph())
			{
				// Inline nodes that are only handled if we are currently in paragraph

				if(node instanceof Text || node instanceof MailLink)
				{
					updateAndSetStartLocation(node);
					updateEndLocation(node);
					encounter.text(node.getChars(), end);
				}
				else if(node instanceof Emphasis || node instanceof StrongEmphasis || node instanceof Code
						|| node instanceof Strikethrough || node instanceof Subscript)
				{
					handleChildren(node);
				}
				else if(node instanceof SoftLineBreak || node instanceof HardLineBreak)
				{
					updateAndSetStartLocation(node);
					updateEndLocation(node);
					encounter.text("\n", end);
				}
				else if(node instanceof HtmlEntity)
				{
					updateAndSetStartLocation(node);
					updateEndLocation(node);
					encounter.text(Html5Entities.entityToSequence(node.getChars()), end);
				}
				else if(node instanceof HtmlInline)
				{
					handleChildren(node);
				}
				else if(node instanceof AutoLink)
				{
					AutoLink link = (AutoLink) node;
					updateStartLocation(node);
					start.moveTextIndex(link.getOpeningMarker());
					encounter.location(start);

					// Instead of using end offset, move the text index using the text length
					end.copyFrom(start);
					end.moveTextIndex(link.getText());

					encounter.text(link.getText(), end);
				}
				else if(node instanceof Link)
				{
					Link link = (Link) node;
					updateStartLocation(node);
					start.moveTextIndex(link.getTextOpeningMarker());
					encounter.location(start);

					// Instead of using end offset, move the text index using the text length
					end.copyFrom(start);
					end.moveTextIndex(link.getText());

					encounter.text(link.getText(), end);
				}
			}
		}

		private void handleChildren(Node node)
		{
			for(Node n : node.getChildren())
			{
				handleNode(n);
			}
		}
	}
}
