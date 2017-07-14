package se.l4.lect.markdown;

import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Interface that can be used to implement a custom {@link MarkdownFlavor}. Applies options directly to Flexmark and
 * can set the parser behavior and extensions used.
 *
 * @author Andreas Holstenson
 *
 */
public interface CustomMarkdownFlavor
{
	/**
	 * Apply this flavor to the given option holder.
	 *
	 * @param options
	 */
	void applyMarkdownOptions(MutableDataHolder options);
}
