package se.l4.lect;

import java.util.LinkedList;
import java.util.Objects;

public class VerifyingLanguageEncounter
	implements LanguageEncounter
{
	private final LinkedList<Data> data;

	private Location location;

	public VerifyingLanguageEncounter()
	{
		data = new LinkedList<>();
	}

	@Override
	public void location(Location location)
	{
		this.location = location;
	}

	@Override
	public void startSentence()
	{
		data.add(new Data(Type.START_SENTENCE, location.copy()));
	}

	@Override
	public void endSentence()
	{
		data.add(new Data(Type.END_SENTENCE, location.copy()));
	}

	@Override
	public void token(Token token)
	{
		data.add(new Data(Type.TOKEN, token.copy()));
	}

	public void verifySentenceStart(Location location)
	{
		if(data.isEmpty()) throw new AssertionError("No more data, expected to find start of sentence");

		Data d = data.removeFirst();
		if(d.event != Type.START_SENTENCE)
		{
			throw new AssertionError("Expected start of sentence but got " + d.event);
		}

		if(location != null && ! location.equals(d.location))
		{
			throw new AssertionError("Locations do not match, expected " + location + " but got " + d.location);
		}
	}

	public void verifySentenceEnd(Location location)
	{
		if(data.isEmpty()) throw new AssertionError("No more data, expected to find end of sentence");

		Data d = data.removeFirst();
		if(d.event != Type.END_SENTENCE)
		{
			throw new AssertionError("Expected end of sentence but got " + d.event);
		}

		if(location != null && ! location.equals(d.location))
		{
			throw new AssertionError("Locations do not match, expected " + location + " but got " + d.location);
		}
	}

	public void verifyToken(Token.Type type, Location start, Location end, String text)
	{
		if(data.isEmpty()) throw new AssertionError("No more data, expected to find token " + type + " with text `" + text + "`");

		Data d = data.removeFirst();
		if(d.event != Type.TOKEN)
		{
			throw new AssertionError("Expected token but got " + d.event);
		}

		if(d.token.getType() != type)
		{
			throw new AssertionError("Expected " + type + " but got " + d.token.getType());
		}

		if(! Objects.equals(d.token.getText(), text))
		{
			throw new AssertionError("Expected text `" + text + "` but got `" + d.token.getText() + "`");
		}

		if(start != null && ! start.equals(d.token.getStart()))
		{
			throw new AssertionError("Start do not match, expected " + start + " but got " + d.token.getStart());
		}

		if(end != null && ! end.equals(d.token.getEnd()))
		{
			throw new AssertionError("End do not match, expected " + end + " but got " + d.token.getEnd());
		}
	}

	enum Type
	{
		START_SENTENCE,
		END_SENTENCE,
		TOKEN
	}

	class Data
	{
		private Type event;
		private Location location;
		private Token token;

		public Data(Type event, Location location)
		{
			this.event = event;
			this.location = location;
		}

		public Data(Type event, Token token)
		{
			this.event = event;
			this.token = token;
		}
	}
}
