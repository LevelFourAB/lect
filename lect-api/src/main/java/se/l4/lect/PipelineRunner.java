package se.l4.lect;

import java.io.IOException;
import java.util.function.Function;

/**
 * {@link PipelineAssembly Assembly} for a {@link Pipeline} to run a set of handlers over a single {@link Source}.
 *
 * @author Andreas Holstenson
 *
 */
public class PipelineRunner<Collector>
	implements PipelineAssembly<Collector, PipelineRunner<Collector>>
{
	private final Source source;
	private final PipelineBuilder<Collector> builder;
	private Collector collector;

	public PipelineRunner(Source source)
	{
		this.source = source;

		builder = Pipeline.newBuilder();
	}

	public <NewCollector> PipelineRunner<NewCollector> collector(NewCollector collector)
	{
		PipelineRunner<NewCollector> castedSelf = (PipelineRunner) this;
		castedSelf.collector = collector;
		return castedSelf;
	}

	@Override
	public PipelineRunner<Collector> language(LanguageFactory factory)
	{
		builder.language(factory);
		return this;
	}

	@Override
	public PipelineRunner<Collector> with(Handler handler)
	{
		builder.with(handler);
		return this;
	}

	@Override
	public PipelineRunner<Collector> with(Function<Encounter<Collector>, Handler> handler)
	{
		builder.with(handler);
		return this;
	}

	public Collector run()
		throws IOException
	{
		builder.build().run(source, collector);
		return collector;
	}
}
