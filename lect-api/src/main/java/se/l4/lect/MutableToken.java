package se.l4.lect;

public class MutableToken
	implements Token
{
	private TokenType type;
	private String text;
	private Location start;
	private Location end;

	public void update(TokenType type, Location start, Location end, String text)
	{
		this.type = type;
		this.text = text;
		this.start = start;
		this.end = end;
	}

	@Override
	public TokenType getType()
	{
		return type;
	}

	public void setType(TokenType type)
	{
		this.type = type;
	}

	@Override
	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@Override
	public Location getStart()
	{
		return start;
	}

	public void setStart(Location start)
	{
		this.start = start;
	}

	@Override
	public Location getEnd()
	{
		return end;
	}

	public void setEnd(Location end)
	{
		this.end = end;
	}

	@Override
	public Token copy()
	{
		return new ImmutableToken(type, start, end, text);
	}

	@Override
	public String toString()
	{
		return type + ": " + text + " (" + start + "-" + end + ")";
	}
}
