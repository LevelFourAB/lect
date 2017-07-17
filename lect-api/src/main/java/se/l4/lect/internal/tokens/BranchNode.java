package se.l4.lect.internal.tokens;

import java.util.List;
import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

public class BranchNode
	extends MatcherNode
{
	private final MatcherNode[] branches;

	public BranchNode(List<MatcherNode> branches)
	{
		this.branches = branches.toArray(new MatcherNode[branches.size()]);
	}

	@Override
	public void setNext(MatcherNode next)
	{
		for(MatcherNode node : branches)
		{
			node.getTail().setNext(next);
		}
	}

	@Override
	boolean match(Token token, Consumer<MatcherNode> branchReceiver)
	{
		boolean matched = true;
		for(MatcherNode node : branches)
		{
			if(! node.match(token, branchReceiver))
			{
				matched = false;
			}
		}

		return matched;
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		b.append('[');

		for(MatcherNode node : branches)
		{
			if(b.length() > 1) b.append(' ');

			b.append(node.toString());
			node = node.next;
		}

		b.append(']');
		return b.toString();
	}
}
