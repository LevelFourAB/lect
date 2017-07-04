package se.l4.lect;

/**
 * Encounter used while using a {@link LanguageParser} to extract natural language details.
 *
 * @author Andreas Holstenson
 *
 */
public interface LanguageEncounter
{
	/**
	 * Set the location of the next event.
	 *
	 * @param location
	 */
	void location(Location location);

	/**
	 * Start a new sentence.
	 */
	void startSentence();

	/**
	 * End the current sentence.
	 */
	void endSentence();

	void token(Token token);
}
