package se.l4.lect;

/**
 * Collector used for collecting a single thing from a test.
 *
 * @author Andreas Holstenson
 *
 */
public interface PipelineTestCollector
{
	void set(Object result);

	Object get();
}
