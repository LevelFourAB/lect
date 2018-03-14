package se.l4.lect;

import se.l4.lect.location.Location;
import se.l4.lect.tokens.Token;

/**
 * Encounter used while using a {@link LanguageParser} to extract natural
 * language details. This encounter used to indicate sentences and tokens
 * within those sentences.
 *
 * @author Andreas Holstenson
 *
 */
public interface LanguageEncounter
{
	/**
	 * Start a new sentence.
	 * 
	 * @param location
	 *   the location at which the sentence starts
	 */
	void startSentence(Location location);

	/**
	 * End the current sentence.
	 * 
	 * @param location
	 *   the location at which the sentence ends
	 */
	void endSentence(Location location);

	/**
	 * A token has been encountered, either within a sentence or outside a
	 * sentence.
	 * 
	 * @param token
	 *   the token that has been extracted
	 */
	void token(Token token);
}
