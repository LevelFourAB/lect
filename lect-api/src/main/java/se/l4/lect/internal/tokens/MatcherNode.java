package se.l4.lect.internal.tokens;

import java.util.List;
import java.util.function.Consumer;

import se.l4.lect.tokens.Token;

public abstract class MatcherNode
{
	protected MatcherNode next;

	public MatcherNode()
	{
	}

	public void setNext(MatcherNode next)
	{
		this.next = next;
	}

	public MatcherNode getTail()
	{
		MatcherNode tail = this;
		while(tail.next != null)
		{
			tail = tail.next;
		}
		return tail;
	}

	abstract boolean match(Token token, Consumer<MatcherNode> branchReceiver);

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
