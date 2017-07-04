package se.l4.lect;

/**
 * Builder for instances of {@link Pipeline}. Built pipelines are useful if you want to store pipelines for reuse
 * and run them on different {@link Source}s.
 *
 * @author Andreas Holstenson
 *
 */
public interface PipelineBuilder
	extends PipelineAssembly<PipelineBuilder>
{
	/**
	 * Build the instance of {@link Pipeline}.
	 *
	 * @return
	 */
	Pipeline build();
}
