package se.l4.lect;

public class ImmutableToken
	implements Token
{
	private final TokenType type;
	private final String text;
	private final Location start;
	private final Location end;

	public ImmutableToken(TokenType type, Location start, Location end, String text)
	{
		this.type = type;
		this.start = start.copy();
		this.end = end.copy();
		this.text = text;
	}

	@Override
	public TokenType getType()
	{
		return type;
	}

	@Override
	public String getText()
	{
		return text;
	}

	@Override
	public Location getStart()
	{
		return start;
	}

	@Override
	public Location getEnd()
	{
		return end;
	}

	@Override
	public Token copy()
	{
		return this;
	}

}
