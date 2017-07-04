package se.l4.lect;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link PipelineAssembly Assembly} for a {@link Pipeline} to run a set of handlers over a single {@link Source}.
 *
 * @author Andreas Holstenson
 *
 */
public class PipelineRunner
	implements PipelineAssembly<PipelineRunner>
{
	private final Source source;
	private final PipelineBuilder builder;

	public PipelineRunner(Source source)
	{
		this.source = source;

		builder = Pipeline.newBuilder();
	}

	@Override
	public PipelineRunner language(Function<LanguageEncounter, LanguageParser> parserCreator)
	{
		builder.language(parserCreator);
		return this;
	}

	@Override
	public PipelineRunner with(Handler handler)
	{
		builder.with(handler);
		return this;
	}

	@Override
	public PipelineRunner with(Function<Encounter, Handler> handler)
	{
		builder.with(handler);
		return this;
	}

	public List<Object> run()
		throws IOException
	{
		return builder.build().run(source);
	}

	public void collect(Consumer<Object> collector)
		throws IOException
	{
		builder.build().run(source, collector);
	}
}
