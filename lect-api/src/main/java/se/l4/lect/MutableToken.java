package se.l4.lect;

import java.util.HashMap;
import java.util.Map;

public class MutableToken
	implements Token
{
	private TokenType type;
	private CharSequence text;
	private Location start;
	private Location end;

	private Map<String, Object> properties;

	public MutableToken()
	{
	}

	public MutableToken(TokenType type, Location start, Location end, CharSequence text)
	{
		this.update(type, start, end, text);
	}

	public void update(TokenType type, Location start, Location end, CharSequence text)
	{
		this.type = type;
		this.text = text;
		this.start = start;
		this.end = end;

		if(properties != null)
		{
			properties.clear();
		}
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
	public CharSequence getText()
	{
		return text;
	}

	public void setText(CharSequence text)
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

	public <T> void set(TokenProperty<T> property, T value)
	{
		if(properties == null)
		{
			properties = new HashMap<>();
		}
		properties.put(property.getId(), value);
	}

	@Override
	public Token copy()
	{
		return new ImmutableToken(type, start, end, text, properties == null ? null : new HashMap<>(properties));
	}

	@Override
	public String toString()
	{
		return type + ": " + text + " (" + start + "-" + end + ")";
	}
}
