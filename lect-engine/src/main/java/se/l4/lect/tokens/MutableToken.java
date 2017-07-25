package se.l4.lect.tokens;

import java.util.HashMap;
import java.util.Map;

import se.l4.lect.location.Location;

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
		update(type, start, end, text, null);
	}

	public void update(TokenType type, Location start, Location end, CharSequence text, Map<String, Object> otherProperties)
	{
		this.type = type;
		this.text = text;
		this.start = start;
		this.end = end;

		if(properties != null)
		{
			properties.clear();
		}

		if(otherProperties != null)
		{
			if(properties == null)
			{
				properties = new HashMap<>();
			}

			properties.putAll(otherProperties);
		}
	}

	/**
	 * Copy data from another token into this one.
	 *
	 * @param other
	 */
	public void copyFrom(Token other)
	{
		this.type = other.getType();
		this.start = other.getStart();
		this.end = other.getEnd();
		this.text = other.getText();

		if(properties != null)
		{
			properties.clear();
		}

		Map<String, Object> otherProperties = other.getProperties();
		if(otherProperties != null)
		{
			if(properties == null)
			{
				properties = new HashMap<>();
			}

			properties.putAll(otherProperties);
		}
	}

	@Override
	public TokenType getType()
	{
		return type;
	}

	public MutableToken setType(TokenType type)
	{
		this.type = type;
		return this;
	}

	@Override
	public CharSequence getText()
	{
		return text;
	}

	public MutableToken setText(CharSequence text)
	{
		this.text = text;
		return this;
	}

	@Override
	public Location getStart()
	{
		return start;
	}

	public MutableToken setStart(Location start)
	{
		this.start = start;
		return this;
	}

	@Override
	public Location getEnd()
	{
		return end;
	}

	public MutableToken setEnd(Location end)
	{
		this.end = end;
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

	public <T> MutableToken set(TokenProperty<T> property, T value)
	{
		if(properties == null)
		{
			properties = new HashMap<>();
		}
		properties.put(property.getId(), value);

		return this;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		return properties;
	}

	@Override
	public Token copy()
	{
		return new ImmutableToken(type, start, end, text, properties == null ? null : new HashMap<>(properties));
	}

	@Override
	public String toString()
	{
		return type + ": " + text + " (" + start + "-" + end + ")" + (properties == null ? "" : " " + properties);
	}

	public static MutableToken ofType(TokenType type)
	{
		return new MutableToken(type, null, null, null);
	}
}
