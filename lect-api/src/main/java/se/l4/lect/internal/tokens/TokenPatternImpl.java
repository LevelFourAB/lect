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
	private MatcherNode node;

	public TokenPatternImpl(MatcherNode node)
	{
		this.node = node;
	}

	@Override
	public TokenMatcher matcher()
	{
		return new TokenMatcherImpl(node);
	}


	public static TokenPattern compile(String source)
	{
		MatcherNode node = new TokenPatternParser(source).parse();
		return new TokenPatternImpl(node);
	}
}
