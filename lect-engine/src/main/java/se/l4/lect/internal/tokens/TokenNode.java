package se.l4.lect.internal.tokens;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import se.l4.lect.tokens.Token;
import se.l4.lect.tokens.TokenType;

/**
 * Node that checks that an individual token and its properties matches.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenNode
	extends MatcherNode
{
	private final TokenType type;
	private final Map<String, Object> properties;

	public TokenNode(TokenType type, Map<String, Object> properties)
	{
		this.type = type;
		this.properties = properties;
	}

	@Override
	boolean match(Token token, Consumer<MatcherNode> branchReceiver)
	{
		if(token == TokenMatcherImpl.START || token == TokenMatcherImpl.END)
		{
			return false;
		}

		if(type != null && token.getType() != type)
		{
			return false;
		}

		if(properties != null)
		{
			for(Map.Entry<String, Object> e : properties.entrySet())
			{
				if("text".equals(e.getKey()))
				{
					// Check the text of the token
					if(! isEqual(token.getText(), e.getValue()))
					{
						return false;
					}
				}
				else
				{
					// TODO: Check the actual property
				}
			}
		}

		branchReceiver.accept(next);

		return true;
	}

	private boolean isEqual(Object value, Object expected)
	{
		if(expected instanceof Pattern)
		{
			if(value instanceof CharSequence)
			{
				return ((Pattern) expected).matcher((CharSequence) value).matches();
			}
			else
			{
				return false;
			}
		}
		else
		{
			return Objects.equals(value, expected);
		}
	}

	@Override
	public String toString()
	{
		return "Token[" + type + "," + properties + "]";
	}
}
