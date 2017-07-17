package se.l4.lect.internal.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

public class NegateNode
	extends MatcherNode
{
	private final MatcherNode node;

	public NegateNode(MatcherNode node)
	{
		this.node = node;
	}

	@Override
	boolean match(Token token, Consumer<MatcherNode> branchReceiver)
	{
		Sub sub = new Sub();
		return sub.match(token, branchReceiver);
	}

	@Override
	public String toString()
	{
		return "Negate[" + node + "]";
	}

	private class Sub
		extends MatcherNode
	{
		private List<MatcherNode> branches;
		private MatcherNode[] tempBranches;

		public Sub()
		{
			branches = new ArrayList<>();
			branches.add(node);
		}

		@Override
		boolean match(Token token, Consumer<MatcherNode> branchReceiver)
		{
			// Create or resize tempBranches if needed
			if(tempBranches == null || tempBranches.length < branches.size())
			{
				tempBranches = new MatcherNode[Math.max(4, branches.size() * 2)];
			}

			// Copy branches into tempBranches to prepare for receiving next branches
			branches.toArray(tempBranches);

			// Match all of the current branches
			branches.clear();

			boolean somethingMatched = false;
			for(MatcherNode n : tempBranches)
			{
				if(n == null) break;

				somethingMatched |= n.match(token, node -> {
					if(node != null)
					{
						branches.add(node);
					}
				});
			}

			if(somethingMatched)
			{
				// Need to keep on going down the branches
				branchReceiver.accept(this);
			}
			else
			{
				// Nothing matched
				branchReceiver.accept(NegateNode.this.next);
			}

			return true;
		}

		@Override
		public String toString()
		{
			return "Negate[Sub, " + node + "]";
		}
	}
}
