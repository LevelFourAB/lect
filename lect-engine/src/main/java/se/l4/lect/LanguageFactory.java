package se.l4.lect;

/**
 * Factory for creating instances of {@link LanguageParser}.
 *
 * @author Andreas Holstenson
 *
 */
@FunctionalInterface
public interface LanguageFactory
{
	/**
	 * Create a parser that will send its results to the given encounter.
	 *
	 * @param encounter
	 * @return
	 */
	LanguageParser create(LanguageEncounter encounter);
}
