package se.l4.lect;

/**
 * Abstract implementation of {@link LanguageParser} that allows an implementation to handle a single
 * {@link CharSequence} without worrying about mapping to the correct location or original text in the source.
 *
 * @author Andreas Holstenson
 *
 */
public abstract class ChunkedLanguageParser
	implements LanguageParser
{
	protected final LanguageEncounter encounter;
	private final MutableToken token;

	private int lastOffset;
	private Node head;

	private Location start;
	private Location end;

	private StringBuilder sourceScratch;
	private StringBuilder scratch;

	public ChunkedLanguageParser(LanguageEncounter encounter)
	{
		this.encounter = encounter;

		head = new Node(null, null, null);
		token = new MutableToken();

		scratch = new StringBuilder();
		sourceScratch = new StringBuilder();
	}

	@Override
	public void text(String text, Location start, Location end)
	{
		Node node = new Node(text, start.copy(), end.copy());
		node.appendTo(head);
	}

	public void text(String text, String original, Location start)
	{
		text(text, start, start.copy().moveTextIndex(original.length()));
	}

	@Override
	public void flush()
	{
		lastOffset = 0;

		int offset = 0;
		Node node = head.next;
		StringBuilder builder = new StringBuilder();
		while(node != head)
		{
			node.offsetStart = offset;
			node.offsetEnd = offset + node.text.length();
			builder.append(node.text);

			offset = node.offsetEnd;
			node = node.next;
		}

		handleChunk(builder);
	}

	private String consume(int offset, Token.Type eventType)
	{
		// Sanity check that we never go backwards
		if(offset < lastOffset) throw new AssertionError("Going backwards in processing of paragraph-level content, language implementation probably broken");

		// Update the start location if there are more nodes
		if(head.next != head)
		{
			start = head.next.start;
			if(head.next.consumed > 0)
			{
				start = start.copy().moveTextIndex(head.next.consumed);
			}
		}

		// If we are the same offset nothing needs to be flushed
		if(offset == lastOffset)
		{
			return null;
		}

		sourceScratch.setLength(0);
		scratch.setLength(0);

		Node node = head.next;
		while(node != head)
		{
			// If this node starts at or after the given offset we shouldn't remove it
			if(node.offsetStart > offset) break;

			if(node.offsetEnd >= offset)
			{
				// This node contains the offset so it's the last node to process
				if(eventType != null)
				{
					scratch.append(node.text.substring(node.consumed, offset - node.offsetStart));
				}

				node.consumed = offset - node.offsetStart;
				if(node.consumed == node.text.length())
				{
					// Consumed all of the node, remove it
					end = node.end;
					head.next = node.next;
				}
				else
				{
					// Only consumed part of the node, guess the end of the node
					// TODO: This should smooth out over the entire length and not just save the difference for the last token found
					end = node.start.copy().moveTextIndex(node.consumed);
				}

				break;
			}
			else
			{
				// This entire node should get consumed
				if(eventType != null)
				{
					scratch.append(node.text.substring(node.consumed, node.text.length()));
				}

				node.consumed = node.text.length();

				// Remove this node and process the next nod
				head.next = node.next;
				node = node.next;
			}
		}

		lastOffset = offset;

		String source = sourceScratch.toString();
		if(eventType != null)
		{
			token.update(eventType, start, end, scratch.toString());
			encounter.location(start);
			encounter.token(token);
		}

		start = end;

		return source;
	}

	private void emitWhitespace(int offset)
	{
		consume(offset, Token.Type.WHITESPACE);
	}

	/**
	 * Start a sentence at the given offset.
	 *
	 * @param offset
	 */
	protected void startSentence(int offset)
	{
		emitWhitespace(offset);
		encounter.location(start);
		encounter.startSentence();
	}

	/**
	 * End a sentence at the given offset.
	 *
	 * @param offset
	 */
	protected void endSentence(int offset)
	{
		emitWhitespace(offset);
		encounter.location(start);
		encounter.endSentence();
	}

	protected void emitToken(int offset, Token.Type type, String value)
	{
		emitWhitespace(offset);

		consume(offset + value.length(), type);
	}

	/**
	 * Handle the given sequence of characters. This method should go through the sequence and in order emit events
	 * via {@link #startSentence(int)}, {@link #emitToken(int, se.l4.lect.Token.Type, String)} and
	 * {@link #endSentence(int)}.
	 *
	 * @param sequence
	 */
	protected abstract void handleChunk(CharSequence sequence);

	private static class Node
	{
		private final Location start;
		private final Location end;
		private final String text;

		private int offsetStart;
		private int offsetEnd;
		private int consumed;
		private Node next;
		private Node prev;

		public Node(String text, Location start, Location end)
		{
			this.text = text;
			this.start = start;
			this.end = end;

			next = prev = this;
		}

		public void appendTo(Node head)
		{
			Node tail = head.prev;
			head.prev = this;
			tail.next = this;
			this.next = head;
			this.prev = tail;
		}
	}
}
