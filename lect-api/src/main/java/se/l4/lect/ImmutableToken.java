package se.l4.lect;

import java.util.Map;

public class ImmutableToken
	implements Token
{
	private final TokenType type;
	private final String text;
	private final Location start;
	private final Location end;
	private final Map<String, Object> properties;

	public ImmutableToken(TokenType type, Location start, Location end, CharSequence text, Map<String, Object> properties)
	{
		this.type = type;
		this.properties = properties;
		this.start = start.copy();
		this.end = end.copy();
		this.text = text.toString();
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

	@Override
	public <T> T get(TokenProperty<T> property)
	{
		if(properties == null)
		{
			return null;
		}
		return property.cast(properties.get(property.getId()));
	}

	@Override
	public boolean has(TokenProperty<?> property)
	{
		return properties.containsKey(property.getId());
	}
}
