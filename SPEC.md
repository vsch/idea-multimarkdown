Specification for an Interactive Markdown Parser
================================================

This is a work in progress. It will be used to implement new Grammar-Kit based parsers
and plug-in features.

Comments and suggestions will be greatly appreciated.

References:

[John Gruber's Markdown]: http://daringfireball.net/projects/markdown/syntax
[Adam Pritchard's Markdown Cheatsheet]: https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet
[GitHub Flavored Markdown]: https://help.github.com/articles/github-flavored-markdown/
[Pandoc’s markdown]: http://pandoc.org/demo/example9/pandocs-markdown.html/
[CommonMark Spec]: http://spec.commonmark.org/0.21/

[John Gruber's Markdown][]
[Adam Pritchard's Markdown Cheatsheet][]
[GitHub Flavored Markdown][]
[Pandoc’s markdown][]
[CommonMark Spec][]


Background
----------

Markdown parsers differ in the details of how they process the source and convert it to HTML. Additionally, in their attempt to convert anything to HTML they not only become more complex but also create difficulty in controlling the resulting output between dialects. This is especially true in the case of nested lists with interleaved paragraphs and code blocks. In some cases their handling of indents and blank lines make it difficult or impossible to get the desired hierarchy without resorting to HTML comments or raw HTML and in others you need to resort to trial and error indentation.

The goal is to simplify generating non-ambiguous markdown source without trial and error indentations or raw HTML. Additionally, the goal is to make the plug-in eliminate edge cases by generating warnings or errors instead of silently ignoring or handling them with assumptions that may not apply. In all cases an edge case should be flagged and a suggestion be made to change the source so that the intent is clear, with syntax highlighting communicating how the source element will be interpreted to remove uncertainty.

The syntax used by this parser is aimed at making the intentions clear to allow automatic formatting with intention pop-ups to suggest changes in the source to reflect those intentions, including alternative interpretations. Where it is not possible to create consistent format then user selection of the target parser dialect will be required to disambiguate the intent.

The original syntax provided by [John Gruber's Markdown][] was ambiguous in some places and too flexible in parts of its syntax. However, having the parser in an interactive environment creates opportunities that are not practical in a command line or back-end parsers. As an IntelliJ IDEA plug-in it is painless to the user to apply the suggested changes by having intentions offered to make the changes automatically.

Handling all flavours of popular markdown dialects is a desirable feature and will be treated as the target goal. The initial implementation will aim at [John Gruber's Markdown][], [GitHub Flavored Markdown][] with [Pandoc’s markdown][] as a long term goal.

This specification is taking the trade-off between complexity with flexible syntax and simplicity with stricter syntax in favour of the latter over the former. Syntax will be made more stringent to reduce ambiguity of intent and make it more portable between implementations. In practice most changes in syntax require extra blank lines between non-homogeneous content or a non-optional space after a directive where it was optional. For the price of an extra blank line or a space you gain a consistent philosophical approach that is easy to understand.

The spirit of simplicity, clarity, intuitiveness and ease of manipulation of plain text source stated in the original specification by John Gruber is a primary goal that must be preserved. Trying to create something consistent with inconsistent input is not worth purusing, especially if it adds ambiguity, conversion difficult to control or predict. Simplicity and predictability are desired features. The aim is to keep it concise, clear and concrete. In case of ambiguous input to generate a warning or an error. Where possible generate the output as if the most common intent was applied to the source, in others generate the output as the selected parser dialect.

In this specification all elements consist of homogeneous blocks that are separated by at least a single blank line. The only exception is list items which can be placed in the same block to support the duality of lists: compact and loose. Not being able to end a list with a double blank line, instead of having to resort to an HTML comment to mark the break, is counter intuitive, but any changes would make it incompatible with existing parsers. However, the mixing of loose and compact format for lists in this specification will be flagged as a warning because the resulting lists will be inconsistently spaced and is usually not the desired intent. Determining when a list item is compact or loose has to follow the selected parser's dialect even when other dialects, like [Pandoc's markdown], make more sense. Mixed loose and compact items in the same list will be flagged with a warning.

All lines in a block are considered to be a logical continuation the first line of the block, regardless of their indentation. If your intent differs then put the text into its own block by adding a blank line above it. This means that headers, tables, code blocks, block-quotes and lists must begin and end with blank lines, consistently. Except of course at the begining of a file where the preceding blank line is implied.

Requiring the user to surround headers with blank lines and changing the specification for Setext headers to require at least three `-` or `=` characters is not a hardship and is consistent with code fencing requiring at least three backticks. It also makes the source easier on the eyes and should be a standard practice even when not required by the specification and is already part of some parsers.

The syntax changes also apply to block-quotes. According to what is inferred in the original specification, these are supposed to be quotations of existing text, pre-formatted and inserted for reference, as per e-mail block-quotes practice. The intent is to be able to take a section of text: copy, paste and prefix every line with the `> ` block-quote lead in. The result should be no more and no less than having thus prefixed text acquire a solid line, running along the left margin. Preserving headers, paragraphs, lists, code blocks, block-quotes and their formatting as they were. There is no lazy continuation, additional indents, etc. All lines of the block-quote must be prefixed, consistently, with `> `. The inconsistent requirement that block-quotes do not contain link definitions is handled by offering to move the reference definitions to the start or end of the file for parser dialects that have this unnecessary limitation.

Similarly, taking a section of text with possible headers, block-quotes, lists, code blocks, etc. and adding it as a paragraph in a list item should be as easy as copy, paste then indent the inserted text for the destination list item. The result should be predictable and consistent. Any block that looks like it belongs to a list item should be unambiguously formatted and modified to be so in practice.

The goal of this syntax is to make the creation of complex hierarchies a simple recursive process with intuitive and non-ambiguous results. If something is ambiguous it must be flagged as a warning or error so that it is addressed by the user who is the final arbiter of the intent and should not be left to the parser implementation's best guess.


Compatibility Goals
-------------------

The goal is to make sure that when you create a document using the new syntax the rendering on the selected parser or parsers is the same as in the plug-in, if the new parser generates no errors or warnings.

In some cases it will not be possible to generate output identical to old parsers. For example, with this specification it is not possible in the same list to mix compact or loose list items without warnings. You either get a warning or put a blank line between all the list items. In either case you always get a loose list for all the items. This is not a limitation but a feature to ensure consistent formatting of lists as standard.


Future Developments
-------------------

Since this markdown parser is geared towards the IntelliJ IDE, future development would include the ability to automatically update the source file to make it more consistent with the generated HTML. For example the paragraphs can be word-wrapped, Setext header underline matched to header text length, lists properly hang-indented, ordered list items can be renumbered to reflect the correct sequence and right justified. Extra blank lines can be added or removed for consistency. Loose list format consistently applied across all items, tables columns space padded to reflect the column sizes and alignment. All these features are intended make the plain text file reflect the look of the generated HTML as much as possible without affecting the output.

It may also be possible to create extensions to Markdown that are implemented in the plug-in during editing: such as numbered headers, alternate numbering schemes for ordered lists, automatic insertions of a table of contents, etc. These would not violate compatibility with existing implementations because these changes would modify the source text, making it agnostic to the final parser that processes it into HTML. If the target parser can handle it leave the source unmodified, if it can't then let the plug-in update the source.


Parser Goals
------------

Initial implementation would use recursion to simplify experimentation and verification of the concept in real-life application.. After this the parser would be implemented, preferably with Grammar-Kit, to create a PSI compatible AST and integrated into a plug-in.

1. The parsing should consist of scanning the text to split the stream into logical lines, with line type based on the line content.

2. The resulting blocks that consist of lines marked with a block-quote prefix will have their prefix removed and reparsed as if they were each a top level markdown source file, with the contents used as child blocks of the block-quote.

3. Blocks that are recognized as a starting a list item block will open a list context and start to process the list items, line by line and include processing of subsequent blocks until a double blank line is found or a block whose indentation is less than the current list item, or until a list item block of a different type than the current list item is reached. i.e. ol list item ends a ul list and vice versa.

    Each list item that encounters another block that will be its child block, based on its indentation being >= list's nesting level, will reduce that block's indentation level by one, and process it as if it was a top level markup source file. The results will be included as child blocks of this list item.

4. All other blocks will be processed to determine their type and in-line mark-up. This includes blocks that start with an HTML block type tag, as opposed to inline type tags. The block HTML tags will have to be parsed to continue the block until the first tag is properly closed. Detecting when an invalid HTML block ends will have to be determined heuristically.

5. At this point consistency of link references with link definitions can be checked for duplicates and missing definitions. Link references resolved, etc.

6. HTML or other format generated from the AST created by the parser.


Specification
=============

In all cases warnings and errors will be presented to the user via the IntelliJ IDEA mechanism of intentions and error highlighting. In the following paragraphs displaying a warning or error should be interpreted as error highlighting and intention pop-up. Only internal errors and warnings will be logged to the logger.


Escaping Characters
-------------------

A backslash appearing before one of the following characters will cause that character to be processed as verbatim text. A backslash followed by anything else will be treated as verbatim text. This was not part of the original specification but is consistent with markdown philosophy of not recognizable markdown sequences being treated as verbatim text. This is the same as the original Markdown specification:

\\   backslash
\`   backtick
\*   asterisk
\_   underscore
\{\}  curly braces
\[\]  square brackets
\(\)  parentheses
\#   hash mark
\+   plus sign
\-   minus sign (hyphen)
\.   dot
\!   exclamation mark


Non-Blank Characters
--------------------

Any character that is not a space or a tab.


Blank Lines
-----------

Any lines which contain no characters or contains only spaces and tabs.


Indents
-------

All indents are leading spaces on a line and consist of multiples of 4 spaces. Partial spaces sequences are truncated: 0-3 spaces is indent(0), 4-7 is indent(1), etc.

All tabs are treated as being set on a 4 character boundary. Tabs may or may not be expanded to spaces but in all cases will be logically treated as if they were expanded. Since indentation spaces are stripped out this has not effect on the generated HTML.

Indents are used to determine the hierarchical location of the block in a list item, or whether it is a code block.

Only the indent of the first line of the block is significant for all blocks but list blocks, which process the indent of each line of the block for possible sub-list items and next list items.


Blocks
------

A block is a sequence of consecutive lines which are not blank lines. Extra blank lines between blocks do not affect the generated output.

All blocks are expected to consist of a group of lines of the same type which can be determined by the first line of the block and at most by first two lines of the block for Setext header blocks and tables. Any inconsistencies will be treated as a missing blank line between the inconsistent parts of the block and logically inserted and a warning generated to inform the user of the fact.

If hard-wraps option is turned off then all the lines in the block are treated as a single logical line of text. Otherwise, every line in the block is suffixed with a `<br>` to preserve line breaks in the output, a la GitHub.

The indentation level of a block is determined by its first line. Indentation of the rest of the lines is not used and is ignored except when processing list blocks.

The following are equivalent:

   als;dkfj;a lslas; fj;lsafdj ;lsafdj l;sdfj sd;afj sdal;fjlsdfj
 alf jasfld;j asl;dfj sal;dfj lsa;dfj l;sadf ls;adf jls
a s;fjasl;f ls;adfj lsa;dfj lsd;afj sdl;afj asfj

als;dkfj;a lslas; fj;lsafdj ;lsafdj l;sdfj sd;afj sdal;fjlsdfj
    alf jasfld;j asl;dfj sal;dfj lsa;dfj l;sadf ls;adf jls
a s;fjasl;f ls;adfj lsa;dfj lsd;afj sdl;afj asfj

als;dkfj;a lslas; fj;lsafdj ;lsafdj l;sdfj sd;afj sdal;fjlsdfj
  alf jasfld;j asl;dfj sal;dfj lsa;dfj l;sadf ls;adf jls
                    a s;fjasl;f ls;adfj lsa;dfj lsd;afj sdl;afj asfj

It is not possible to create mixed content list items without resorting to loose list format for all the items in the list. This is not considered a limitation because real life mixed content lists should be consistently double spaced or they are visually unbalanced. Compact lists should not contain loose sub-lists for the same reason.

A block-quote prefix must appear as a prefix on all the lines in the block-quote block. Any block which does not have this prefix on all the lines will be split into several blocks with a warning that the source is missing blank lines or that the leading `>` in the `> ` sequence should be escaped if it is not intended to mark a block-quote.


Inline Formatting
-----------------

All inline formatting directives will be terminated in the current paragraph block or the beginning of the next list item in the case of list item blocks, whichever of these comes first. An error will be generated that an inline formatting directive was not closed. These sequences will be automatically closed in the reverse order in which they were opened. An intent to escape or close these sequences will be presented to the user.


### Emphasis

A * or _ is used as an inline marker if it is followed by a non-blank character. The span is terminated by the same character which is preceded by a non-blank character, otherwise these characters will be treated as verbatim text.

With a single * or _ marking an italic sequence and ** or __ marking a bold sequence. Converted to `<em>` and `<strong>` tags in the generated HTML.

The _ is recognized as italic directive only if enabled in the options. Otherwise it is treated as a regular character to allow for variable names with leading and embedded underscored without needing the underscores to be escaped.

Depending on the targer parser setting, encountering an _ that could be interpreted as an emphasis directive will generate a warning and suggestion to escape the underscore.


### Code Spans/Verbatim Text

Same as markdown. With one or more \` backticks used to mark the start and end of a span of code or verbatim text. The closing sequence has to have the same number of backticks as the opening sequence. A leading or trailing space in the enclosed text is trimmed from the output.


### Links

Same as markdown standard, with GitHub and Wiki link styles as selectable options. With the constraint that no spaces can separate the `[]` and `()` or the `[]` and `[]` parts of the link.

The inconsistent requirement that block-quotes do not contain link definitions will be handled by offering to move the reference definitions to the start or end of the file for parser dialects that have this limitation.

<!-- TODO:add details from standard spec and GitHub and Wiki extensions -->


### Images

Same as links but with a preceding `!` character without any spaces between the `!` and the `[` of the link.


### HTML


Non-block HTML tags will be parsed and handled as inline directives. Specifically, they will be automatically closed if not explicitly done so in the source and the element is not opened with <... /> tag and requires a closing tag.

inline element tags:
a, abbr, acronym, b, bdo, big, br, button, cite, code, dfn, em, i, img, input, kbd, label, map, object, q, samp, script, select, small, span, strong, sub, sup textarea tt var

elements which are forbidden to have a closing tag:
br, img, input


Headers
-------

Headers are blocks of text. Being blocks they are always surrounded by blank lines.

Headers consist of two types: Setext and atx.

Setext headers use at least three `=` or `-` characters on the second line of the block and contain nothing but these characters, if more than three are used.

Atx headers are specified with a sequence of `#`, 1 to 6 character long followed by one or more spaces which are not optional. The number of # determines the heading tag `<h1>` to `<h6>` to be used for the generated HTML. Any trailing #'s will be removed from the generated text.

Any extra lines in header blocks will generate a warning and the extra lines processed as if they were preceded by a blank line.

Formatting will extend the length of the second line to the length of the title or 3 characters, which ever is the minimum.


Block Quotes
------------

All block quotes start with `> ` and each additional block quote level adds another set of `> `. Note that the single space between the `>` and the following text or next level block-quote is part of the block-quote prefix, the rest of the spaces, if any, are considered part of the indentation level for the rest of the text on this line. This is to follow in the spirit of the original specification stating that block quotes use the e-mail strategy for quoting text and most editors easily add block quoting level by prefixing the lines with `> `. Therefore no lazy continuation is considered for block-quotes. All lines must have the prefix to be part of the block-quote.

If a line does not have a block-quote prefix then an error is generated and the text treated as if the line had the block-quote prefix. The converse, of a line with a block-quote prefix appearing in a non-block-quote block, will be treated as if the `>` was escaped with a warning to that effect.

The easiest way to treat block quotes is to look at the contained text as being created without the block-quote prefix and then add the block-quote prefix to every line in the block-quote. Which is the intended way of creating block quotes.

Blank lines between block-quotes will be treated as if they had a blank line followed by an HTML comment line with `<!-- start new block quote -->` on it so that the two block-quotes are not merged into one. An alternate intent will be offered to remove the spurious blank line or to add the extra level of block-quote prefix to it.

The following:

```
> line one
> > nested
>
> > second nested
> test
```

which generates:

> line one
> > nested
>
> > second nested
> test

will be parsed as if it was:

```
> line one
> > nested
>
> <!-- start new block quote -->
> > second nested
> test
```

to generate, which is more likely the intent based on the prefixes in the source:

> line one
> > nested
>
> <!-- start new block quote -->
> > second nested
> test


Lists
-----

Lists are blocks or consecutive blocks of text with a list prefix directive. A new list is always started on the first line of the block.

Consecutive list blocks will be treated as part of the same list if and only if their list type is the same as the current list or their indentation is equal to or greater than the nesting level of the current list or one of its parents.

A single block can have multiple list items, including nested list items if the lines begin with a list item directive.

An unordered list item starts with a single - + or * followed by one or more spaces.

An ordered list starts with a number followed by a period. A block that would begin a list and starts with an item of more than two digits will generate a warning and be processed if it was not a list block. This is to prevent starting accidental lists where a number and a period start the block.

If a new list item has the same nesting level as the current list but is of a different type then a warning is generated and a new list is started as if it was preceded by a blank line.

A list which consists of multiple blocks will have all its items' first line text wrapped in `<p>` tags and a warning will be generated that not all items in the list have consistent line spacing. There is no way to generate a single list with compact and loose list items.

A compact list that contains a loose sub-list may also be flagged with a warning based on the options.

This is different from the original specification which did not address the use of compact and loose list items in the same list. However, this mixed case results by accident when you can start with a compact list and add formatting that causes a single item in the list to become loose. The alternative is to manually insert blank lines around every item in the list, when you notice that it is inconsistent.

A block will be associated with one of the currently open lists based on its indentation level and list directive prefix.


1. list asdfasdfsadf safdsaf
      - item
      - item
1. list asdfasdfsadf safdsaf
1. list asdfasdfsadf safdsaf
1. list asdfasdfsadf safdsaf

To get two consecutive lists of the same type, instead of a single double spaced one, it is necessary to leave insert an HTML comment line between the lists, otherwise they will be merged into one list with a loose list item formatting:


The following:

```
1. list 1 item 1
2. list 1 item 2
3. list 1 item 3

4. list 1 item 1
5. list 1 item 2
6. list 1 item 3
```

will generate a single list, with spacing varying between parsers:

1. list 1 item 1
2. list 1 item 2
3. list 1 item 3

4. list 1 item 1
5. list 1 item 2
6. list 1 item 3

It will be changed to:

```
1. list 1 item 1
2. list 1 item 2
3. list 1 item 3

<!-- end list -->

4. list 1 item 1
5. list 1 item 2
6. list 1 item 3
```

to generate two lists, which is closer to the formatted intent:

1. list 1 item 1
2. list 1 item 2
3. list 1 item 3

<!-- end list -->

4. list 1 item 1
5. list 1 item 2
6. list 1 item 3


Indented blocks that follow a list item will be associated with the previous list item whose nesting level matches the indentation level of the line being considered. This makes it easier to create multi-paragraph list items that themselves contain sub-lists, paragraphs and code blocks.

In the case where the last block in an item list could be a paragraph for a list item or a code block of the parent's list item the block will be treated as a non-code block with an intent pop up to change this to a fenced code block unindented so that it becomes part of the parent list's item.

```
 1. List 1 Item 1
    - sub list item 1
        sub list item 1 continuation line

            sub list item 1 code block 1

        sub list item 1 paragraph

            sub list item 1 code block 2

    - sub list item 2

    List 1 Item 1 paragraph

        List 1 Item 1 code block

    List 1 Item 1 paragraph or main code block


```

could be interpreted as:

 1. List 1 Item 1
    - sub list item 1
        sub list item 1 continuation line

            sub list item 1 code block 1

        sub list item 1 paragraph

            sub list item 1 code block 2

    - sub list item 2

    List 1 Item 1 paragraph

        List 1 Item 1 code block

    List 1 Item 1 paragraph or main code block

or alternately:

 1. List 1 Item 1
    - sub list item 1
        sub list item 1 continuation line

            sub list item 1 code block 1

      sub list item 1 paragraph

            sub list item 1 code block 2

    - sub list item 2

    List 1 Item 1 paragraph

        List 1 Item 1 code block

```
List 1 Item 1 paragraph or main code block
```


Tables
------

Tables are contained in a single block with the first line defining the table header, the second being the line separator identifying this block as a table block.

The rest of the lines being the table body. The leading and trailing `|` characters on the line are optional. As are the `:` column alignment indicators on the second line and the spaces around the `|` column separators. The leading and trailing spaces of the column text will be trimmed from the output.

The sequence of `-` and `:`  for each column in the second line must be at least three characters long.

The column text will be processed for inline markdown directives.

The number of columns in each line should be the same any differences will be compensated by appending empty columns and generating a warning to inform the user of that fact.

column 1 | column 2 | column 3 | ... | column N
:---|:-------:|---------:| ---- | -------
column 1 | column 2 | column 3 | ... | column N
column 1 | column 2 | column 3 | ... | column N
column 1 | column 2 | column 3 | ... | column N
column 1 | column 2 | column 3 | ... | column N

Any line in the table block that does not contain a column separator `|` will be treated as if it has a blank line preceding it and is the first line of the next block. A warning will be generated to inform the user.


Horizontal Rule
---------------

Three or more hyphens, asterisks or underscores with or without spaces in between, by themselves on line without any other characters. Preceded and followed by a blank line.

Indentation and block-quotes affect the placement of the horizontal rule in the proper hierarchy.


Code Blocks
-----------

A code block is a block of lines indented by 4 or more spaces. The leading 4 spaces of the indent are removed from all the lines in the output text and the resulting lines wrapped in `<pre><code>` and `<pre><code>` tags including any blank lines until a non-code block is encountered.

    for (i in range)
    {
        if (isOdd(i))
        {
            printf("%i is odd\n",i);
        }
        else
        {
            printf("%i is even\n",i);
        }

    }

Fenced Code Blocks
------------------

Optionally a code block can use the fenced code directive consisting of three or more `` ` `` on the first line, followed by optional source language directive for the enclosed block and the same number or greater `` ` `` on a line used to terminate the fenced block.

`````c
for (i in range)
{
    if (isOdd(i))
    {
        printf("%i is odd\n",i);
    }
    else
    {
        printf("%i is even\n",i);
    }

}
`````

