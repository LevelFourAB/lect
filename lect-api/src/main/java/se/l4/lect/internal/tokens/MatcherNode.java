package se.l4.lect.internal.tokens;

import java.util.List;
import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

/**
 * Abstract base class for anything used with a {@link TokenPatternImpl} for matching via {@link TokenMatcherImpl}.
 *
 * @author Andreas Holstenson
 *
 */
public abstract class MatcherNode
{
	protected MatcherNode next;

	public MatcherNode()
	{
	}

	/**
	 * Set the node to evaluate after this one.
	 *
	 * @param next
	 */
	public void setNext(MatcherNode next)
	{
		this.next = next;
	}

	/**
	 * Get the tail of this node.
	 *
	 * @return
	 */
	public MatcherNode getTail()
	{
		MatcherNode tail = this;
		while(tail.next != null)
		{
			tail = tail.next;
		}
		return tail;
	}

	/**
	 * Match the node against the given token. Any branch that should be evaluated should be given to the branch
	 * receiver.
	 *
	 * @param token
	 *   the token to match
	 * @param branchReceiver
	 *   receiver for any branches that this node wants to evaluate after itself
	 * @return
	 *   if this node matched
	 */
	abstract boolean match(Token token, Consumer<MatcherNode> branchReceiver);

	/**
	 * Create a {@link SequenceNode} for the given nodes.
	 *
	 * @param nodes
	 * @return
	 */
	public static MatcherNode createSequence(List<MatcherNode> nodes)
	{
		MatcherNode root = null;
		MatcherNode parent = null;
		for(MatcherNode node : nodes)
		{
			if(parent != null)
			{
				parent.setNext(node);
			}
			else
			{
				root = node;
			}

			parent = node;
		}
		return new SequenceNode(root);
	}
}
