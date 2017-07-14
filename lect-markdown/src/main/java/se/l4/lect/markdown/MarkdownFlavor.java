package se.l4.lect.markdown;

import java.util.Arrays;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * The flavor of markdown to use.
 *
 * @author Andreas Holstenson
 *
 */
public enum MarkdownFlavor
	implements CustomMarkdownFlavor
{
	/**
	 * Original flavor.
	 */
	MARKDOWN,

	/**
	 * CommonMark.
	 */
	COMMONMARK,

	/**
	 * Github Flavored Markdown.
	 */
	GITHUB;

	@Override
	public void applyMarkdownOptions(MutableDataHolder options)
	{
		switch(this)
		{
			case MARKDOWN:
				options.setFrom(ParserEmulationProfile.MARKDOWN);
				break;
			case COMMONMARK:
				options.setFrom(ParserEmulationProfile.COMMONMARK);
				break;
			case GITHUB:
				options.setFrom(ParserEmulationProfile.COMMONMARK)
					.set(TablesExtension.COLUMN_SPANS, false)
					.set(TablesExtension.APPEND_MISSING_COLUMNS, true)
					.set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
					.set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
					.set(Parser.EXTENSIONS, Arrays.asList(
						StrikethroughSubscriptExtension.create(),
						TablesExtension.create(),
						TaskListExtension.create()
					));
				break;
		}
	}
}