package se.l4.lect.internal.tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import se.l4.lect.CharPredicate;
import se.l4.lect.tokens.TextTokenization;
import se.l4.lect.tokens.TokenPattern;
import se.l4.lect.tokens.TokenType;

/**
 * Parser for {@link TokenPattern}.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenPatternParser
{
	private final String source;
	private int offset;

	public TokenPatternParser(String source)
	{
		this.source = source;
	}

	protected void consumeWhitespace()
	{
		if(offset == -1)
		{
			// Everything already read
			return;
		}
		offset = TextTokenization.findNonWhitespace(source, offset);
	}

	protected String consumeUntil(CharPredicate predicate)
	{
		int current = offset;
		offset = TextTokenization.find(source, offset, predicate);
		return source.substring(current, offset == -1 ? source.length() : offset);
	}

	protected void consume(char c)
	{
		if(source.charAt(offset) != c)
		{
			throw raiseError("Expected to find the character " + c);
		}
		offset++;
		if(offset == source.length())
		{
			offset = -1;
		}
	}

	private String readString()
	{
		consume('\'');
		StringBuilder builder = new StringBuilder();
		for(int n=source.length(); offset<n; offset++)
		{
			char c = source.charAt(offset);
			if(c == '\\')
			{
				// This is an escape, so read next char
				if(offset + 1 < n)
				{
					char c2 = source.charAt(offset + 1);
					switch(c2)
					{
						case 'n':
							builder.append('\n');
							break;
						case 'r':
							builder.append('\r');
							break;
						case 't':
							builder.append('\t');
							break;
						case '\\':
							builder.append('\\');
							break;
						case '\'':
							builder.append('\'');
							break;
						default:
							throw raiseError("Unknown escape \\" + c2);
					}
				}
				else
				{
					throw raiseError("Started escape without following it with escaped char");
				}
			}
			else if(c == '\'')
			{
				// Done reading, found the last quote
				break;
			}
			else
			{
				builder.append(c);
			}
		}

		consume('\'');
		return builder.toString();
	}

	private Pattern readRegex()
	{
		consume('/');
		StringBuilder builder = new StringBuilder();
		for(int n=source.length(); offset<n; offset++)
		{
			char c = source.charAt(offset);
			if(c == '\\')
			{
				// This is an escape, so read next char
				if(offset + 1 < n)
				{
					char c2 = source.charAt(offset + 1);
					if(c2 == '/')
					{
						builder.append('/');
					}
					else
					{
						builder.append('\\');
						builder.append(c);
					}
				}
				else
				{
					throw raiseError("Started escape without following it with escaped char");
				}
			}
			else if(c == '/')
			{
				// Done reading, found the end
				break;
			}
			else
			{
				builder.append(c);
			}
		}

		consume('/');

		int flags = 0;
		if(peek() == 'i')
		{
			consume('i');
			flags = Pattern.CASE_INSENSITIVE;
		}
		return Pattern.compile(builder.toString(), flags);
	}

	private RuntimeException raiseError(String msg)
	{
		return new RuntimeException(msg);
	}

	private char peek()
	{
		if(offset == -1) return '\u0000';
		return source.charAt(offset);
	}

	public MatcherNode parse()
	{
		List<MatcherNode> nodes = parseSeveral('\u0000');
		if(nodes.isEmpty())
		{
			throw new RuntimeException("Pattern does not match anything");
		}

		if(offset != -1)
		{
			throw raiseError("Could not parse all of the expression");
		}

		return MatcherNode.createSequence(nodes);
	}

	private List<MatcherNode> parseSeveral(char returnChar)
	{
		consumeWhitespace();

		List<MatcherNode> result = new ArrayList<>();
		while(offset != -1)
		{
			char c = peek();
			if(c == ')' || c == ']')
			{
				if(c == returnChar)
				{
					consume(returnChar);
					return result;
				}
				else if(returnChar == '\u0000')
				{
					throw raiseError("Found unbalanced character " + c);
				}
				else
				{
					throw raiseError("Expected to find " + returnChar + " but found unbalanced character " + c);
				}
			}
			else
			{
				result.add(parseSingle());
			}

			consumeWhitespace();
		}

		return result;
	}

	private MatcherNode parseSingle()
	{
		MatcherNode node = parseSingleNoCounts();

		// Parse out the range of the node
		switch(peek())
		{
			case '?':
				consume('?');
				node = new RangeNode(node, 0, 1);
				break;
			case '*':
				consume('*');
				node = new RangeNode(node, 0, Integer.MAX_VALUE);
				break;
			case '+':
				consume('+');
				node = new RangeNode(node, 1, Integer.MAX_VALUE);
				break;
		}

		return node;
	}

	private MatcherNode parseSingleNoCounts()
	{
		char c = peek();
		switch(c)
		{
			case '!':
				consume('!');
				return new NegateNode(parseSingleNoCounts());
			case '(':
				return parseSequence();
			case '[':
				return parseBranch();
			case '\'':
				return quickToken(readString());
			case '/':
				return quickToken(readRegex());
			default:
				return parseTokenWithType();
		}
	}

	private MatcherNode quickToken(Object value)
	{
		Map<String, Object> properties = new HashMap<>();
		properties.put("text", value);
		return new TokenNode(null, properties);
	}

	private MatcherNode parseTokenWithType()
	{
		String token = consumeUntil(c -> c == '=' || isBoundary(c));
		TokenType type;
		switch(token)
		{
			case "word":
				type = TokenType.WORD;
				break;
			case "ws":
			case "whitespace":
				type = TokenType.WHITESPACE;
			case "special":
				type = TokenType.SPECIAL;
				break;
			case "unkn":
			case "unknown":
				type = TokenType.UNKNOWN;
				break;
			case "symbol":
				type = TokenType.SYMBOL;
				break;
			case "any":
				type = null;
				break;
			default:
				throw raiseError("Expected type of token, found `" + token + "`");
		}

		Map<String, Object> properties = null;
		if(peek() == '=')
		{
			consume('=');

			Object value = parseValue();

			properties = new HashMap<>();
			properties.put("text", value);
		}

		while(peek() == ',')
		{
			// This token has an argument, parse it
			consume(',');
			String name = consumeUntil(this::isBoundary);

			Object value = null;
			if(peek() == '=')
			{
				consume('=');

				value = parseValue();
			}

			if(properties == null)
			{
				properties = new HashMap<>();
			}
			properties.put(name, value);
		}

		return new TokenNode(type, properties);
	}

	private boolean isBoundary(char c)
	{
		return c == ',' || c == '=' || c == ']' || c == ')' || c == '?' || c == '*' || c == '+' || c == '{' || Character.isWhitespace(c);
	}

	private Object parseValue()
	{
		Object value;
		switch(peek())
		{
			case '\'':
				// String value
				value = readString();
				break;
			case '/':
				value = readRegex();
				break;
			default:
				value = consumeUntil(c -> c == ',' || c == ']' || c == ')' || Character.isWhitespace(c));
				break;
		}
		return value;
	}

	private MatcherNode parseSequence()
	{
		consume('(');
		List<MatcherNode> nodes = parseSeveral(')');
		if(nodes.isEmpty())
		{
			throw raiseError("Group in pattern was empty");
		}
		return MatcherNode.createSequence(nodes);
	}


	private MatcherNode parseBranch()
	{
		consume('[');
		List<MatcherNode> nodes = parseSeveral(']');
		if(nodes.isEmpty())
		{
			throw raiseError("Branch in pattern was empty");
		}
		return new BranchNode(nodes);
	}
}
