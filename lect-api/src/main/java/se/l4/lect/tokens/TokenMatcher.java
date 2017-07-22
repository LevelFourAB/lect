package se.l4.lect.tokens;

import se.l4.lect.Handler;

/**
 * Matcher created from a {@link TokenPattern} that will match the sequence the pattern describes. Matchers keep track
 * of their matching state and are designed for easy use together with {@link Handler}:
 *
 * <pre>
 * new DefaultHandler() {
 *   TokenMatcher matcher = pattern.matcher();
 *
 *   public void token(Token token) {
 *     if(matcher.add(token)) {
 *       // This will be true if the pattern is matched
 *     }
 *   }
 *
 *   public void sentenceStart(Location location) {
 *     // Reset matching when a new sentence is found
 *     matcher.reset();
 *   }
 * }
 * </pre>
 *
 * @author Andreas Holstenson
 *
 */
public interface TokenMatcher
{
	/**
	 * Add a token to this matcher returning whether a match has now been found. Tokens can be added whenever they
	 * are encountered and the matcher will automatically keep track where it is.
	 *
	 * @param token
	 * @return
	 *   if the pattern now matches
	 */
	boolean add(Token token);

	/**
	 * Indicate that this is the end of the current match sequence. This method should be called to properly match
	 * against {@code $} if used. In many cases this would be called at the end of a sentence by a {@link Handler}.
	 *
	 * @return
	 *   if the pattern now matches
	 */
	boolean end();

	/**
	 * Reset the matching, for when tokens are no longer logically connected, such as when new sentence has been
	 * started.
	 */
	void reset();

	/**
	 * Get if the matcher is currently matching.
	 *
	 * @return
	 */
	boolean isMatch();
}
