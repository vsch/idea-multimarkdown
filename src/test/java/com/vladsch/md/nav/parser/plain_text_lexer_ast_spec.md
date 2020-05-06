---
title: MarkdownPlainTextLexer Spec
author:
version:
date: '2016-07-09'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## MarkdownPlainTextLexer

Used for parsing markdown text for spellchecking and string search.

Only generates TEXT, COMMENT_FOR_TODO and CODE_TEXT lexemes.

Plain text should return the whole input

```````````````````````````````` example MarkdownPlainTextLexer: 1
.
.
Root[0, 0]
````````````````````````````````


Plain text should return the whole input

```````````````````````````````` example MarkdownPlainTextLexer: 2
First line
Second line
Last line
.
.
Root[0, 32]
  TEXT[0, 32]
````````````````````````````````


Plain text with empty HTML comment

```````````````````````````````` example MarkdownPlainTextLexer: 3
First line
Second line <!---->
Last line
.
.
Root[0, 40]
  TEXT[0, 23]
  COMMENT_OPEN[23, 27]
  COMMENT_CLOSE[27, 30]
  TEXT[30, 40]
````````````````````````````````


Plain text with simple HTML comment

```````````````````````````````` example MarkdownPlainTextLexer: 4
First line
Second line <!--simple-->
Last line
.
.
Root[0, 46]
  TEXT[0, 23]
  COMMENT_OPEN[23, 27]
  COMMENT_TEXT[27, 33]
  COMMENT_CLOSE[33, 36]
  TEXT[36, 46]
````````````````````````````````


Plain text with unterminated HTML comment

```````````````````````````````` example MarkdownPlainTextLexer: 5
First line
Second line <!--simple
Last line
.
.
Root[0, 43]
  TEXT[0, 23]
  COMMENT_OPEN[23, 27]
  COMMENT_TEXT[27, 43]
````````````````````````````````


Plain text with unterminated or empty code

```````````````````````````````` example MarkdownPlainTextLexer: 6
First line
Second line ``
Last line
.
.
Root[0, 35]
  TEXT[0, 35]
````````````````````````````````


Plain text with simple code

```````````````````````````````` example MarkdownPlainTextLexer: 7
First line
Second line `code`
Last line
.
.
Root[0, 39]
  TEXT[0, 39]
````````````````````````````````


Plain text with HTML comment with embedded looking code

```````````````````````````````` example MarkdownPlainTextLexer: 8
First line
Second line <!--`code`-->
Last line
.
.
Root[0, 46]
  TEXT[0, 23]
  COMMENT_OPEN[23, 27]
  COMMENT_TEXT[27, 33]
  COMMENT_CLOSE[33, 36]
  TEXT[36, 46]
````````````````````````````````


Plain text with code with embedded looking HTML comment

```````````````````````````````` example MarkdownPlainTextLexer: 9
First line
Second line `<!--code-->`
Last line
.
.
Root[0, 46]
  TEXT[0, 24]
  COMMENT_OPEN[24, 28]
  COMMENT_TEXT[28, 32]
  COMMENT_CLOSE[32, 35]
  TEXT[35, 46]
````````````````````````````````


complex

```````````````````````````````` example MarkdownPlainTextLexer: 10
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

```java
class dummy {
    String test = "SimTocContents";
}
```

## SimToc  

The Sim TOC tag has the following format: `[TOC style]: # "Title"` and includes all following
lines until a blank line.

Lines after the TOC tag are added to the `SimTocContent` child node of the SimToc block.

The intention for this tag is to have the `SimTocContent` updated to reflect the content of the
document.
.
.
Root[0, 549]
  TEXT[0, 549]
````````````````````````````````


