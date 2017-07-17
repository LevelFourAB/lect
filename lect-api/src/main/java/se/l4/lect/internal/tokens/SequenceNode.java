package se.l4.lect.internal.tokens;

import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

/**
 * Node that checks that an entire sequence of nodes matches.
 *
 * @author Andreas Holstenson
 *
 */
public class SequenceNode
	extends MatcherNode
{
	private MatcherNode root;

	public SequenceNode(MatcherNode root)
	{
		this.root = root;
	}

	@Override
	public void setNext(MatcherNode next)
	{
		root.getTail().setNext(next);
	}

	@Override
	boolean match(Token token, Consumer<MatcherNode> branchReceiver)
	{
		return root.match(token, branchReceiver);
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		b.append('(');

		MatcherNode node = root;
		while(node != null)
		{
			if(b.length() > 1) b.append(' ');

			b.append(node.toString());
			node = node.next;
		}

		b.append(')');
		return b.toString();
	}
}
