package se.l4.lect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import se.l4.lect.handlers.DefaultHandler;
import se.l4.lect.text.PlainTextSource;
import se.l4.lect.tokens.Token;

public class PipelineTest
{
	@Test
	public void test()
		throws IOException
	{
		PipelineTestCollector result = Pipeline.over(PlainTextSource.forString("Hello world!"))
			.collector(new PipelineTestCollectorImpl())
			.language(ICULanguage.forLocale(Locale.ENGLISH))
			.with(WordCountHandler::new)
			.run();

		assertThat(result, notNullValue());
		assertThat(result.get(), is(2));
	}

	@Test
	public void testExampleFromReadme()
		throws IOException
	{
		Source source = PlainTextSource.forString("Simple plain text");

		AtomicInteger wordCount = Pipeline.over(source)
			.language(ICULanguage.forLocale(Locale.ENGLISH))
			.collector(new AtomicInteger())
			.with(encounter -> new DefaultHandler() {
				private int count = 0;

				@Override
				public void word(Token token) {
					count++;
				}

				@Override
				public void done() {
					encounter.collector().set(count);
				}
			})
			.run();

		System.out.println(wordCount + " words");
	}
}
