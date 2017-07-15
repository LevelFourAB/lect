package se.l4.lect;

import se.l4.lect.location.Location;

/**
 * Handler that receives streaming events when processing a Lect syntax tree.
 *
 * @author Andreas Holstenson
 *
 */
public interface Handler
{
	/**
	 * A new paragraph has been started.
	 */
	void startParagraph(Location location);

	/**
	 * A paragraph has ended.
	 */
	void endParagraph(Location location);

	/**
	 * The start of a sentence has been found.
	 */
	void startSentence(Location location);

	/**
	 * The end of a sentence has been found.
	 */
	void endSentence(Location location);

	/**
	 * Token has been found, can either be whitespace, symbol or word.
	 *
	 * @param token
	 */
	void token(Token token);

	/**
	 * Indicates that there are no more events to process.
	 */
	void done();
}
