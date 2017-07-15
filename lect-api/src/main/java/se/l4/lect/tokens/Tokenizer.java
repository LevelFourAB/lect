package se.l4.lect.tokens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Tokenizer used to tokenize input into {@link Token}s.
 *
 * @author Andreas Holstenson
 *
 */
public interface Tokenizer
	extends Iterator<Token>
{
	/**
	 * Get if any more tokens are available.
	 *
	 * @return
	 */
	@Override
	boolean hasNext();

	/**
	 * Get the next available token.
	 *
	 * @return
	 */
	@Override
	Token next();

	/**
	 * Stream these tokens.
	 *
	 * @return
	 */
	default Stream<Token> stream()
	{
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(this, Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL),
			false
		);
	}

	/**
	 * Get the tokens as a {@link List}.
	 *
	 * @return
	 */
	default List<Token> toList()
	{
		List<Token> result = new ArrayList<>();
		while(hasNext())
		{
			result.add(next().copy());
		}
		return result;
	}

	/**
	 * Get these tokens as an array.
	 *
	 * @return
	 */
	default Token[] toArray()
	{
		List<Token> list = toList();
		return list.toArray(new Token[list.size()]);
	}
}
