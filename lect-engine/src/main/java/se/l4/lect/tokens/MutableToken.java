package se.l4.lect.tokens;

import java.util.HashMap;
import java.util.Map;

import se.l4.lect.location.Location;

/**
 * Implementation of {@link Token} that is mutable. Mutable tokens can be
 * modified and are commonly used by {@link se.l4.lect.LanguageParser}s to 
 * reduce memory usage during parsing.
 * 
 * @author Andreas Holstenson
 */
public class MutableToken
	implements Token
{
	private TokenType type;
	private String text;
	private Location start;
	private Location end;

	private Map<String, Object> properties;

	/**
	 * Create a new empty token.
	 */
	public MutableToken()
	{
	}

	/**
	 * Create a new token with a given type, location and text.
	 * 
	 * @param type
	 *   the type of the token
	 * @param start
	 *   the start of the token
	 * @param end
	 *   the end of the token
	 * @param text
	 *   text of the sequence, will be turned into a {@link String} internally
	 */
	public MutableToken(TokenType type, Location start, Location end, CharSequence text)
	{
		this.update(type, start, end, text);
	}

	/**
	 * Update the data within the token.
	 * 
	 * @param type
	 *   the type of the token
	 * @param start
	 *   the start of the token
	 * @param end
	 *   the end of the token
	 * @param text
	 *   text of the sequence, will be turned into a {@link String} internally
	 */
	public void update(TokenType type, Location start, Location end, CharSequence text)
	{
		update(type, start, end, text, null);
	}

	/**
	 * Update the data within the token including properties.
	 * 
	 * @param type
	 *   the type of the token
	 * @param start
	 *   the start of the token
	 * @param end
	 *   the end of the token
	 * @param text
	 *   text of the sequence, will be turned into a {@link String} internally
	 * @param otherProperties
	 *   properties for the token
	 */
	public void update(TokenType type, Location start, Location end, CharSequence text, Map<String, Object> otherProperties)
	{
		this.type = type;
		this.text = text == null ? null : text.toString();
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
	public String getText()
	{
		return text;
	}

	public MutableToken setText(CharSequence text)
	{
		this.text = text.toString();
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

	/**
	 * Set a property for this token.
	 * 
	 * @param property
	 *   the property to set
	 * @param value
	 *   the value of the property
	 * @return
	 *   this instance
	 */
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
