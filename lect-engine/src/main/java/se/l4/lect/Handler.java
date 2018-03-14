package se.l4.lect;

import se.l4.lect.location.Location;
import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.TokenProperty;

/**
 * Handler that receives streaming events when processing a Lect syntax tree.
 *
 * @author Andreas Holstenson
 *
 */
public interface Handler
{
	/**
	 * Processing is starting.
	 */
	void start();

	/**
	 * A new paragraph has been started.
	 * 
	 * @param location
	 *   the location of the paragraph. Locations are shared, the user should
	 *   copy the location if it needs to be used outside the current method.
	 */
	void startParagraph(Location location);

	/**
	 * A paragraph has ended.
	 * 
	 * @param location
	 *   the location of the paragraph end. Locations are shared, the user
	 *   should copy the location if it needs to be used outside the current
	 *   method.
	 */
	void endParagraph(Location location);

	/**
	 * The start of a sentence has been found.
	 * 
	 * @param location
	 *   the location of the sentence. Locations are shared, the user should
	 *   copy the location if it needs to be used outside the current method.
	 */
	void startSentence(Location location);

	/**
	 * The end of a sentence has been found.
	 * @param location
	 *   the location of the sentence end. Locations are shared, the user
	 *   should copy the location if it needs to be used outside the current
	 *   method.
	 */
	void endSentence(Location location);

	/**
	 * An attribute has been set by the source.
	 *
	 * @param location
	 *   the location where the property is started. Locations are shared, the
	 *   user should copy the location if it needs to be used outside the
	 *   current method.
	 * @param property
	 *   the property to set
	 * @param value
	 *   the value of the property
	 */
	void startAttribute(Location location, TokenProperty<?> property, Object value);

	/**
	 * An attribute has been cleared.
	 *
	 * @param location
	 *   the location where the property is ended. Locations are shared, the
	 *   user should copy the location if it needs to be used outside the
	 *   current method.
	 * @param property
	 */
	void endAttribute(Location location, TokenProperty<?> property);

	/**
	 * Token has been found, can either be whitespace, symbol or word.
	 *
	 * @param token
	 *   the token that has been found. Tokens are shared and should be copied
	 *   via {@link Token#copy()} if they need to be used outside of the current
	 *   method.
	 */
	void token(Token token);

	/**
	 * Indicates that there are no more events to process.
	 */
	void done();
}
