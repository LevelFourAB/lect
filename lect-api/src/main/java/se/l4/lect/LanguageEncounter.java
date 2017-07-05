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
	 * Start a new sentence.
	 */
	void startSentence(Location location);

	/**
	 * End the current sentence.
	 */
	void endSentence(Location location);

	void token(Token token);
}
