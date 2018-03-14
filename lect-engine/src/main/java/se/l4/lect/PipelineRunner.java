package se.l4.lect;

import java.io.IOException;

/**
 * {@link PipelineAssembly Assembly} for a {@link Pipeline} to run a set of
 * handlers over a single {@link TextSource}.
 *
 * @author Andreas Holstenson
 *
 */
public class PipelineRunner<Collector>
	implements PipelineAssembly<Collector, PipelineRunner<Collector>>
{
	private final TextSource source;
	private final PipelineBuilder<Collector> builder;
	private Collector collector;

	public PipelineRunner(TextSource source)
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
	public PipelineRunner<Collector> with(HandlerFactory<Collector> handler)
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
