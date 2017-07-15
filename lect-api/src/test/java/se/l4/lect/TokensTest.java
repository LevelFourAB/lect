package se.l4.lect;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.l4.lect.location.Location;

/**
 * Test for {@link MutableToken} and {@link ImmutableToken}.
 *
 * @author Andreas Holstenson
 *
 */
public class TokensTest
{
	@Test
	public void testMutableCreation()
	{
		MutableToken token = new MutableToken(TokenType.SYMBOL, Location.text(1, 0), Location.text(1, 4), "");

		assertThat(token.getType(), is(TokenType.SYMBOL));
		assertThat(token.getStart(), is(Location.text(1, 0)));
		assertThat(token.getEnd(), is(Location.text(1, 4)));
		assertThat(token.getText(), is(""));
	}

	@Test
	public void testMutableUpdate()
	{
		MutableToken token = new MutableToken();
		token.update(TokenType.SYMBOL, Location.text(1, 0), Location.text(1, 4), "");

		assertThat(token.getType(), is(TokenType.SYMBOL));
		assertThat(token.getStart(), is(Location.text(1, 0)));
		assertThat(token.getEnd(), is(Location.text(1, 4)));
		assertThat(token.getText(), is(""));
	}

	@Test
	public void testMutableProperties()
	{
		MutableToken token = new MutableToken(TokenType.SYMBOL, Location.text(1, 0), Location.text(1, 4), "");
		token.set(TokenProperty.LEMMA, "abc");

		assertThat(token.getType(), is(TokenType.SYMBOL));
		assertThat(token.getStart(), is(Location.text(1, 0)));
		assertThat(token.getEnd(), is(Location.text(1, 4)));
		assertThat(token.getText(), is(""));
		assertThat(token.get(TokenProperty.LEMMA), is("abc"));
	}

	@Test
	public void testMutableCopy()
	{
		MutableToken token = new MutableToken(TokenType.SYMBOL, Location.text(1, 0), Location.text(1, 4), "");
		token.set(TokenProperty.LEMMA, "abc");

		Token t2 = token.copy();

		assertThat(t2.getType(), is(TokenType.SYMBOL));
		assertThat(t2.getStart(), is(Location.text(1, 0)));
		assertThat(t2.getEnd(), is(Location.text(1, 4)));
		assertThat(t2.getText(), is(""));
		assertThat(t2.get(TokenProperty.LEMMA), is("abc"));
	}
}
