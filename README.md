# Lect

Lect is a syntax tree and pipeline for natural language analysis that can be
created from different formats such as plain text and HTML.

```java
Source source = PlainTextSource.fromString("Simple plain text");

Pipeline.over(source)
  .language(ICULanguage.forLocale(Locale.ENGLISH))
  .with(WordCountHandler::new)
  .run(obj -> System.out.println("Got " + obj);
```

## Languages

Languages are supported via the interface `LanguageParser` which is responsible
for turning text into sentences and tokens (words, symbols and whitespace).
A parser implemented using ICU4J is available that uses `BreakIterator` to split
things.
 