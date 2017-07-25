package se.l4.lect;

public class PipelineTestCollectorImpl
	implements PipelineTestCollector
{
	private Object result;

	@Override
	public void set(Object result)
	{
		this.result = result;
	}

	@Override
	public Object get()
	{
		return result;
	}
}
