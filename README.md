# Lect

Lect is a pipeline for natural language analysis that can be created from and
executed on different formats such as plain text, HTML and Markdown. Lect
parses the original format into paragraphs, sentences and words while keeping
track of the location in the source.

Lect can be used to build things such as spell and grammar checking,
entity tagging, keyword extraction, summarization algorithms and many other
applications that require robust text handling.

```java
Source source = PlainTextSource.forString("Simple plain text");

AtomicInteger wordCount = Pipeline.over(source)
  .language(ICULanguage.forLocale(Locale.ENGLISH))
  .collector(new AtomicInteger())
  .with(encounter -> new DefaultHandler() {
    private int count = 0;
    
    public void word(Token token) {
      count++;
    }
    
    public void done() {
      encounter.collector().set(count);
    }
  })
  .run();

System.out.println(wordCount + " words");
```

## Paragraphs, sentences and tokens

Three things are currently tracked in a source starting with paragraphs. The
paragraphs in Lect are used to group text content that is logically connected
instead of visually connected. For a format such as HTML or Markdown this
means that explicit paragraphs, headings and list items are all turned into
paragraphs. Handlers receive paragraph boundaries via the `startParagraph`
and `endParagraph` methods.

When a paragraph has been found the text in the paragraph is run through a
`LanguageParser` to turn it into sentences and tokens. Sentence boundaries are
passed to handlers via `startSentence` and `endSentence`.

Tokens are the individual parts that make up the actual content. Most of the
tokens are emitted for sentences, but white-space tokens can be found between
sentences and paragraphs.

Four types of tokens exists and map white-space, words, symbols and special.

* White-space is anything that matches space in the source, within our outside
sentences.
* Words are anything that could be a word in the language specified.
* Symbols are individual symbols, such as punctuation.
* Special tokens are things such as URLs, e-mails and phone numbers.

## Languages

Languages are supported via the interface `LanguageParser` which is responsible
for turning text into sentences and tokens (words, symbols and whitespace).
A parser implemented using ICU4J is available that uses `BreakIterator` to split
things into tokens. This parser is suitable for some uses, such as spell
checking but is not recommended for more advanced NLP tasks.

```java
LanguageFactory lang = ICULanguage.forLocale(Locale.ENGLISH);
```

`TokenizingLanguage` is available for use with two types of tokenizers, one that
splits a paragraph into sentences and one that splits a sentence into tokens:

```java
LanguageFactory lang = TokenizingLanguage.create(Locale.ENGLISH,
  SentenceTestTokenizer::new,
  WhitespaceTokenizer::new
);
```

## Tokenizers

Tokenizers are objects responsible for tokenizing input, such as strings,
into tokens. In Lect they are a interesting mostly when implementing a
`LanguageParser`. The `TokeningLanguage` class makes implementing the parsing a two
step process, first implement a tokenizer that splits text into sentences
and secondly a tokenizer that splits sentences into tokens.

A good starting point for custom tokenizers is `OffsetTokenizer` which helps with
creating tokenizers that use `OffsetLocation` for location tracking.

## Token matching

Lect includes utilities for matching patterns of tokens. `TokenPattern` can be
used to compile and match a sequence of tokens. Matching is usually done
streaming so it can be used with handlers:

```java
TokenPattern pattern = TokenPattern.compile("symbol='$' word");
TokenMatcher matcher = pattern.matcher();

if(matcher.add(token)) {
  // The token matched
}
```

```java
// Match any token
TokenPattern.compile("any");
// Match a word
TokenPattern.compile("word");
// Match against token.getText()
TokenPattern.compile("word='Test'");
// Shortcut to match the text of any type of token
TokenPattern.compile("'Test'");
// Match against TokenProperty.NORMALIZED
TokenPattern.compile("word,normalized='test'");
// Match word followed by symbol
TokenPattern.compile("word symbol")
// Match against regular expression
TokenPattern.compile("word=/test/i");
// Shortcut to match via regex for any type of token
TokenPattern.compile("/test/i");
// Use parenthesis to create an optional group of Mrs + period
TokenPattern.compile("?word,normalized='mrs' symbol,text='.',continuation)? word");
// Use brackets to create an OR between tokens or groups
TokenPattern.compile("[word,normalized='mrs' word,normalized='mr'] symbol,text='.',continuation?");
```