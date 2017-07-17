package se.l4.lect.internal.tokens;

import java.util.ArrayList;
import java.util.List;

import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.TokenMatcher;

public class TokenMatcherImpl
	implements TokenMatcher
{
	private final MatcherNode root;
	private List<MatcherNode> branches;
	private MatcherNode[] tempBranches;
	private boolean matched;

	public TokenMatcherImpl(MatcherNode root)
	{
		this.root = root;
		branches = new ArrayList<>();
	}

	@Override
	public boolean add(Token token)
	{
		// Reset matched and add root to the active branches as it should always be matched
		matched = false;
		branches.add(root);

		// Create or resize tempBranches if needed
		if(tempBranches == null || tempBranches.length < branches.size())
		{
			tempBranches = new MatcherNode[Math.max(4, branches.size() * 2)];
		}

		// Copy branches into tempBranches to prepare for receiving next branches
		branches.toArray(tempBranches);

		// Match all of the current branches
		branches.clear();
		for(MatcherNode n : tempBranches)
		{
			if(n == null) break;

			n.match(token, node -> {
				if(node == null)
				{
					matched = true;
				}
				else
				{
					branches.add(node);
				}
			});
		}

		if(matched)
		{
			// The matcher should reset if it matched
			branches.clear();
		}

		return matched;
	}

	@Override
	public void reset()
	{
		branches.clear();
		matched = false;
	}

	@Override
	public boolean isMatch()
	{
		return matched;
	}

}
