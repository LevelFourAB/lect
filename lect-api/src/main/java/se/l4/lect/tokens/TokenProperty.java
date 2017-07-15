package se.l4.lect.tokens;

import se.l4.lect.Handler;
import se.l4.lect.LanguageParser;

/**
 * Extended language data for a {@link Token} that a {@link LanguageParser} or {@link Handler} may enhance a token
 * with. Most of these are made available for use {@link TokenType#WORD words}, but it is up to the parser or handler
 * to decide when they are used.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenProperty<T>
{
	/**
	 * Normalized form of the token.
	 */
	public static final TokenProperty<String> NORMALIZED = define("normalized", String.class);

	/**
	 * Lemma of the token.
	 */
	public static final TokenProperty<String> LEMMA = define("lemma", String.class);

	/**
	 * The part of speech the token is.
	 */
	public static final TokenProperty<String> POS = define("pos", String.class);

	/**
	 * Extended type for use with {@link TokenType#SPECIAL}.
	 */
	public static final TokenProperty<String> EXTENDED_TYPE = define("extendedType", String.class);

	private final String id;
	private final Class<T> dataType;

	private TokenProperty(String id, Class<T> dataType)
	{
		this.id = id;
		this.dataType = dataType;
	}

	/**
	 * Define some data that can be attacted to a {@link Token}.
	 *
	 * @param id
	 * @param dataType
	 * @return
	 */
	public static <T> TokenProperty<T> define(String id, Class<T> dataType)
	{
		return new TokenProperty<>(id, dataType);
	}

	public String getId()
	{
		return id;
	}

	/**
	 * Cast an object to the data type of this token.
	 *
	 * @param data
	 * @return
	 */
	public T cast(Object data)
	{
		return dataType.cast(data);
	}
}
