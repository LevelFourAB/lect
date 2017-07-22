package se.l4.lect.internal.tokens;

import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

/**
 * Node used to match the start and end of input. Only ever matches if it receives the exact instance requested.
 *
 * @author Andreas Holstenson
 *
 */
public class ExactTokenNode
	extends MatcherNode
{
	private final Token token;

	public ExactTokenNode(Token token)
	{
		this.token = token;
	}

	@Override
	boolean match(Token token, Consumer<MatcherNode> branchReceiver)
	{
		if(token == this.token)
		{
			branchReceiver.accept(next);
			return true;
		}

		return false;
	}
}
