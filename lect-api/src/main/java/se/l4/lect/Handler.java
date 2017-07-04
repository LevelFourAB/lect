package se.l4.lect;

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
	void startParagraph();

	/**
	 * A paragraph has ended.
	 */
	void endParagraph();

	/**
	 * The start of a sentence has been found.
	 */
	void startSentence();

	/**
	 * The end of a sentence has been found.
	 */
	void endSentence();

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
