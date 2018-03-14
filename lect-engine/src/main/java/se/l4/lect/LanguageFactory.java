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
	 *   encounter that should be used to emit sentence and token information
	 * @return
	 *   instance of {@link LanguageParser}
	 */
	LanguageParser create(LanguageEncounter encounter);
}
