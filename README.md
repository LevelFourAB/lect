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
