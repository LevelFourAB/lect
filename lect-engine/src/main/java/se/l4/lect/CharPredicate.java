package se.l4.lect;

/**
 * Predicate for testing a character.
 *
 * @author Andreas Holstenson
 *
 */
@FunctionalInterface
public interface CharPredicate
{
	/**
	 * Apply this predicate to the given character.
	 *
	 * @param c
	 * @return
	 */
	boolean test(char c);
}
