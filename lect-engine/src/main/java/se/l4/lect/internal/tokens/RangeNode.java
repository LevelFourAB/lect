package se.l4.lect.internal.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

/**
 * Node that checks that another node matches a number of times.
 *
 * @author Andreas Holstenson
 *
 */
public class RangeNode
	extends MatcherNode
{
	private final MatcherNode node;
	private final int min;
	private final int max;

	public RangeNode(MatcherNode node, int min, int max)
	{
		this.node = node;
		this.min = min;
		this.max = max;
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
		return "Range[" + min + ".." + max + ", " + node + "]";
	}

	private class Sub
		extends MatcherNode
	{
		private List<MatcherNode> branches;
		private MatcherNode[] tempBranches;

		private int count;
		private boolean lastMatched;
		private boolean nextMatched;

		public Sub()
		{
			branches = new ArrayList<>();
			branches.add(node);

			if(min == 0 && RangeNode.this.next != null)
			{
				branches.add(RangeNode.this.next);
			}
		}

		@Override
		boolean match(Token token, Consumer<MatcherNode> branchReceiver)
		{
			lastMatched = false;

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
					if(node == null)
					{
						// This fully matched, increase count
						count++;
						lastMatched = true;

						nextMatched = n == RangeNode.this.next;
					}
					else
					{
						branches.add(node);
					}
				});
			}

			branchReceiver.accept(this);

			if(lastMatched)
			{
				if(count < max)
				{
					branches.add(node);
				}

				if(count >= min)
				{
					branches.add(RangeNode.this.next);

					if(nextMatched)
					{
						branchReceiver.accept(RangeNode.this.next.next);
					}
					else
					{
						branchReceiver.accept(RangeNode.this.next);
					}
				}
			}

			return somethingMatched;
		}

		@Override
		public String toString()
		{
			return "Range[" + min + ".." + max + " @" + count + ", " + node + "]";
		}
	}
}
