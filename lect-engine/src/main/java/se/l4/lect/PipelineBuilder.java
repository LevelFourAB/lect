package se.l4.lect;

/**
 * Builder for instances of {@link Pipeline}. Built pipelines are useful if you want to store pipelines for reuse
 * and run them on different {@link TextSource}s.
 *
 * @author Andreas Holstenson
 *
 */
public interface PipelineBuilder<Collector>
	extends PipelineAssembly<Collector, PipelineBuilder<Collector>>
{
	/**
	 * Build the instance of {@link Pipeline}.
	 *
	 * @return
	 */
	Pipeline<Collector> build();
}
