package se.l4.lect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import se.l4.lect.text.PlainTextSource;

public class PipelineTest
{
	@Test
	public void test()
		throws IOException
	{
		List<Object> result = Pipeline.over(PlainTextSource.forString("Hello world!"))
			.language(ICULanguage.forLocale(Locale.ENGLISH))
			.with(WordCountHandler::new)
			.run();

		assertThat(result, notNullValue());
		assertThat(result.size(), is(1));
		assertThat(result.get(0), is(2));
	}
}
