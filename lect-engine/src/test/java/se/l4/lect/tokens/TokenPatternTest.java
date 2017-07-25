package se.l4.lect.tokens;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link TokenPattern}.
 *
 * @author Andreas Holstenson
 *
 */
public class TokenPatternTest
{
	@Test
	public void testSingleToken()
	{
		TokenPattern pattern = TokenPattern.compile("word");
		TokenMatcher matcher = pattern.matcher();

		if(! matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSingleTokenMiss()
	{
		TokenPattern pattern = TokenPattern.compile("word");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher did not match");
		}
		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSingleTokenResetsAfterMatch()
	{
		TokenPattern pattern = TokenPattern.compile("word");
		TokenMatcher matcher = pattern.matcher();

		if(! matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher did not match");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}
		assertThat(matcher.isMatch(), is(false));
	}

	@Test
	public void testSingleTokenViaString()
	{
		TokenPattern pattern = TokenPattern.compile("'cookie'");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSingleTokenViaRegex()
	{
		TokenPattern pattern = TokenPattern.compile("/cookie/i");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("Cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSingleTokenWithTextString1()
	{
		TokenPattern pattern = TokenPattern.compile("word='cookie'");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSingleTokenWithTextString2()
	{
		TokenPattern pattern = TokenPattern.compile("word,text='cookie'");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSingleTokenWithTextRegex()
	{
		TokenPattern pattern = TokenPattern.compile("word,text=/[Cc]ookie/");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD).setText("Cookie")))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testRootSequence()
	{
		TokenPattern pattern = TokenPattern.compile("word symbol");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testRootSequenceReset()
	{
		TokenPattern pattern = TokenPattern.compile("word symbol");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSubSequence()
	{
		TokenPattern pattern = TokenPattern.compile("word (symbol symbol)");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSubSequence2()
	{
		TokenPattern pattern = TokenPattern.compile("word (symbol symbol) word");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testSubSequence3()
	{
		TokenPattern pattern = TokenPattern.compile("any (symbol=$ symbol,normalized=test)");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL).setText("$")))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL).set(TokenProperty.NORMALIZED, "test")))
		{
			throw new AssertionError("Matcher did not match");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL).set(TokenProperty.NORMALIZED, "test")))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		assertThat(matcher.isMatch(), is(false));
	}

	@Test
	public void testBranch()
	{
		TokenPattern pattern = TokenPattern.compile("[word symbol] unkn");
		TokenMatcher matcher = pattern.matcher();

		// First branch with word
		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.UNKNOWN)))
		{
			throw new AssertionError("Matcher did not match");
		}

		// Second with word
		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.UNKNOWN)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testNegateSingleToken()
	{
		TokenPattern pattern = TokenPattern.compile("!word");
		TokenMatcher matcher = pattern.matcher();

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		assertThat(matcher.isMatch(), is(true));

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		assertThat(matcher.isMatch(), is(false));
	}

	@Test
	public void testNegateSequence()
	{
		TokenPattern pattern = TokenPattern.compile("!(symbol word) special");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		// TODO: This will always match as it resets the matcher
		matcher.add(MutableToken.ofType(TokenType.SPECIAL));

		matcher.reset();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Shouldn't have matched");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SPECIAL)))
		{
			throw new AssertionError("End did not match");
		}

		assertThat(matcher.isMatch(), is(true));
	}

	@Test
	public void testOptionalSingleToken()
	{
		TokenPattern pattern = TokenPattern.compile("word?");
		TokenMatcher matcher = pattern.matcher();

		if(! matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher did not match");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}
	}

	@Test
	public void testOptionalTwoTokens()
	{
		TokenPattern pattern = TokenPattern.compile("word? symbol");
		TokenMatcher matcher = pattern.matcher();

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		matcher.reset();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}
	}

	//@Test
	public void testOptionalTwoTokensReverseOrder()
	{
		TokenPattern pattern = TokenPattern.compile("symbol word?");
		TokenMatcher matcher = pattern.matcher();

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher did not match at optional token");
		}
	}

	@Test
	public void testOptionalSequence()
	{
		TokenPattern pattern = TokenPattern.compile("symbol (word word)? symbol");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}

		matcher.reset();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched after single word");
		}

		matcher.reset();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}
	}

	@Test
	public void testRepeatingSequence1()
	{
		TokenPattern pattern = TokenPattern.compile("symbol (word word)+ symbol");
		TokenMatcher matcher = pattern.matcher();

		// Symbol + symbol - no match
		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}
	}

	@Test
	public void testRepeatingSequence2()
	{
		TokenPattern pattern = TokenPattern.compile("symbol (word word)+ symbol");
		TokenMatcher matcher = pattern.matcher();

		// Symbol + word + word + symbol - match
		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}
	}

	@Test
	public void testRepeatingSequence3()
	{
		TokenPattern pattern = TokenPattern.compile("symbol (word word)+ symbol");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(! matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher did not match");
		}
	}

	@Test
	public void testRepeatingSequence4()
	{
		TokenPattern pattern = TokenPattern.compile("symbol (word word)+ symbol");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched against nothing");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matcher matched at optional token");
		}
	}

	@Test
	public void testEndOfSequence()
	{
		TokenPattern pattern = TokenPattern.compile("symbol $");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.SYMBOL)))
		{
			throw new AssertionError("Matched to early");
		}

		if(! matcher.end())
		{
			throw new AssertionError("Did not match end of sequence");
		}
	}

	@Test
	public void testEndOfSequenceNoMatch()
	{
		TokenPattern pattern = TokenPattern.compile("symbol $");
		TokenMatcher matcher = pattern.matcher();

		if(matcher.add(MutableToken.ofType(TokenType.WORD)))
		{
			throw new AssertionError("Matched for wrong token");
		}

		if(matcher.end())
		{
			throw new AssertionError("Invalid sequence");
		}
	}
}
