package se.l4.lect.internal.tokens;

import se.l4.lect.tokens.TokenMatcher;
import se.l4.lect.tokens.TokenPattern;

/**
 * Implementation of {@link TokenPattern}.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenPatternImpl
	implements TokenPattern
{
	private final int flags;
	private final MatcherNode node;

	public TokenPatternImpl(int flags, MatcherNode node)
	{
		this.flags = flags;
		this.node = node;
	}

	@Override
	public TokenMatcher matcher()
	{
		return new TokenMatcherImpl(flags, node);
	}


	public static TokenPattern compile(String source, int flags)
	{
		MatcherNode node = new TokenPatternParser(source).parse();
		return new TokenPatternImpl(flags, node);
	}
}
