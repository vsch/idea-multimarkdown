Markdown Syntax for Simpler Parsing and Greater Control
=======================================================

This is a work in progress and is not currently implemented or complete.

The goal is to simplify and disambiguate the markdown spec while increasing the complexity of hierarchies that can
be easily created without resorting to embedded HTML or trial and error indentations. Additionally, the goal
is to make the creation of parsers and HTML generators simple, testable and deterministic so that multi-language
implementation can generate identical output with improved speed and reduced resources, where possible.

The original syntax provided by [John Gruber](http://daringfireball.net/projects/markdown/syntax#header) was a good start but its
ambiguities and attempts at being too permissive in the syntax of input text made subsequent implementations diverge
on handling of blank lines, block-quotes and nested compound lists with embedded paragraphs and code blocks.

The [CommonMark Spec](http://spec.commonmark.org/0.21/) was considered but I found that it is way too complex and beginning to
to resemble the HTML spec in its complexity, which defeats the purpose of Markdown being simpler and easier to use and understand.
Implementing compliant parsers for this spec will not be easier than doing the same for HTML browsers. 'Nuff said.

Currently there are a lot of flavours of Markdown parsers and all differ in the details of how process the source and convert it to HTML.
Additionally, in their attempt to convert anything to HTML they not only become more complex but also create difficulty
in controlling the resulting output. This is especially true in the case of nested lists with interleaved paragraphs with code blocks.
In some cases making it impossible to get the desired hierarchy.

I feel that the spirit of simplicity, clarity and ease of manipulation of plain text markdown source stated in the original spec by
John Gruber should be primary and must be preserved. Trying to create something consistent with inconsistent input should be avoided
if it adds ambiguity or makes parsers more complex, conversion difficult to control or predict without giving significant benefits to
the user. Simplicity and predictability are features too. Keep it concise, clear and concrete. In the case of erroneous input generate an
error and predictable output consistent with fixing this error. (more on that later).

The biggest change is the expectation that all components of text consist of blocks that are separated by blank lines. The only exception
is list items which can be placed in the same block. This eliminates ambiguities associated with determining the placement of the line. It
also eliminates the need for the concept of lazy continuation because it is no longer relevant. All lines in a block are considered
to be continuation lines of the first line of the block, regardless of their indentation. You want something else? Put it into its own
block by adding a blank line above it.

Forcing the user to surround headers with blank lines is not a hardship because it makes the source easier on the eyes and should be
done to make the plain text file readable. As a bonus it eliminates ambiguity, simplifies the parser and makes the output
predictable. If the header block has more than two lines then it is an error and you can assume that the third line should be preceded
by a blank line.

The same applies to block-quotes. These are supposed to be quotations of existing text, already pre-formatted and inserted for reference.
The intent is to take a section of text: copy, paste and prefix every line with the `> ` block-quote lead in. The result should be
no more and no less than having thus prefixed text acquire a solid line, running along the left margin. Preserving headers, paragraphs,
lists, code blocks, block-quotes and their formatting as they were. Therefore there is no lazy continuation, additional indents, etc. All
lines of the block-quote must be prefixed with `> `.

Taking a section of text with possible headers, block-quotes, lists, code blocks and adding it as a paragraph of a list item
should be as easy as copy, paste and indent to the right level for the destination list item. The result should be predictable and
consistent. This is definitely not the case with today's parsers.

The goal of this syntax is to make the creation of complex hierarchies a simple recursive process and thus parsing to be the same.
Recursion with maximum stack depth equal to the maximum block quote and list nesting level, plus a constant. To make it secure the maximum
recursion depth can be limited to 25 making it safe and at the same time not a limit for real life documents and insignificant from
a resource demand point of view.


Compatibility Goals
-------------------

Compatibility with existing parsers would be limited to first level elements and nested lists using the new more stringent syntax.
In the case where list items contain composite hierarchies with multiple paragraphs and code blocks the maximum list item
depth level would be 2 since this is about all you can achieve predictably with most parsers used today.

Creating composite hierarchies deeper than that with plain markdown is not consistent or not even possible, so no attempt will be
made to emulate idiosyncrasies of the existing parsers when rendering of complex nested structures deeper than two levels or
their inconsistent handling of blank lines.

The goal is to make sure that when you create a document using the new syntax the rendering of the existing parsers is the same as
the new parser. The reverse is not the goal: taking the edge cases of an ambiguous input and expecting identical output by
the new parser. If the new syntax using existing parsers generates the same output as the new parser then the goal is met.

When you process an existing document with the new parser you should get a list of warnings and errors where the document has
glaring inconsistencies in the block contents which means that some blank lines should be added to separate single blocks into
multiple blocks.


Future Developments
-------------------

Since this markdown parser is geared towards the IntelliJ IDE, future development would include the ability to automatically update the
source file to make it more consistent with the generated HTML. For example the paragraphs can be word-wrapped, lists properly
hang-indented, ordered list items can be renumbered to reflect the correct sequence and right justified. Extra blank lines can be
added or removed for consistency. Double spacing of lists consistently applied across all items, tables formatted to reflect
the column sizes and alignment. All these features are intended make the plain text file reflect the look of the generated HTML
as much as possible without affecting the output.

In the future it may be possible to create extensions to Markdown that are implemented in the plug-in during editing: such as numbered
headers, alternate numbering schemes for ordered lists, automatic insertions of a table of contents, etc. These would not violate
compatibility with existing implementations because these changes would modify the source text, making it agnostic to the final
parser that processes it into HTML. However, these features would be easy to add to parsers so that they generate the same
HTML without having the source modified. If your parser can handle it you can leave the source unmodified, if it can't then
let the plug-in update the source.


Parsing Goals
-------------

Ideally, parsing should be a single pass, top down process with one line look ahead. The lines contained in resulting blocks would not
copy the file text but would instead reference its location, allowing quick access to the text without having to duplicate it and have
the reference to the original source location for free. This would also make it compatible with how the IntelliJ PSI elements create
references to the original file text.

Initial implementation would use recursion to simplify experimentation and verification of the concept in real-life applications. After
this the parser would be translated to create a PSI compatible AST and integrated into a plug-in.

1. The parsing should consist of scanning the text for top level blocks based on blank lines, keeping track of blocks that consistenly
   start with a block-quote prefix so they can be marked as block-quoted blocks.

2. The resulting blocks that consist of lines marked with a block-quote prefix will have their prefix removed and reparsed
   as if they were each a top level markdown source file, with the contents used as child blocks of the block-quote.

3. Blocks that are recognized as a starting a list item block will open a list context and start to process the list items,
   line by line and include processing of subsequent blocks until a double blank line is found or a block whose indentation is
   less than the current list item, or until a list item block of a different type than the current list item is reached.
   i.e. ol list item interrupts a ul list and vice versa.

   Each list item that encounters another block that will be its child block, based on its indentation being >= this items nesting level, will
   reduce that block's indentation level by one, and process it as if it was a top level markup source file. The results will be
   included as child blocks of this list item.

4. All other blocks will be processed in one pass to determine their type and inline mark-up. This includes blocks that start with an HTML
   block type tags, as opposed to inline type tags. The block HTML tags will be passed as is, without checking the HTML for validity. This
   simplifies parsing and will allow to break up the HTML into separate blocks, possibly interleaved with Markdown processed blocks,
   each of which is not valid HTML on its own but the total is valid HTML. This will allow raw HTML to include markdown blocks, adding
   flexibility that is currently missing from the spec.

5. At this point consistency of link references can be checked for duplicates and missing definitions. Table of contents can be generated, etc.

6. HTML or other format generated from the AST created by the parser.


Specification
=============

Escaping Characters
-------------------

A backslash appearing before one of the following characters will cause that character, and any identical characters following
it to be processed as simmple text. This means that `\*\*` and `\**` are equivalent and can be used to escape bold and italic sequences.
Escaping a backslash sequence will result in one less backslash than the sequence it escapes: `\\\\` will output `\\\` not `\\` as would be
expected if the backslash escaped a single character following it.

A backslash anywhere else will be treated as simple text. This is the same as the original Markdown spec:

\   backslash
`   backtick
*   asterisk
_   underscore
{}  curly braces
[]  square brackets
()  parentheses
#   hash mark
+   plus sign
-   minus sign (hyphen)
.   dot
!   exclamation mark


Indents and Block Quote Prefixes
--------------------------------

All indents are multiples of 4 spaces, so indent-0 is 0-3 spaces, indent-1 is 4-7 spaces, etc.

A sequence of `> ` is treated as a block quote prefix marker

Indents are used to determine the hierarchical location of the block in a list item, or whether it is a code block.


Non-Blank Characters
--------------------

Any character that is not a space or a tab character


Blank Lines
-----------

Any line which contains no characters or only spaces and tabs.


Blocks
------

A block is a sequence of consecutive non-blank lines. Blocks are separated by one or more blank lines.
Extra blank lines do not affect the spacing of generated output. The only exception is two or more blank lines used to
separate consecutive lists.

All blocks are expected to be consistently of a single type which can be determined by the first few non-blank characters
of the first line and by at most first two lines of the block for Setext header blocks. Any inconsistencies will be treated
as a missing blank line between the inconsistent parts of the block and logically inserted, while generating a warning.

If hard-wraps option is turned off then all the lines in the block are treated as a single logical line of text. Otherwise, every line in the block
is suffixed with a `<br>` to preserve line breaks in the output, a la GitHub.

The indentation level of a block is determined its first line. Indentation of the rest of the lines is not used and is ignored except when
processing list-item blocks, where it is used to determine whether the line is a new list item, the start of a new sub list, or a continuation line.

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

Definition of lazy continuation lines is no longer necessary since all lines in a block are treated as continuation lines regardless of
their indentation. List blocks must start on the first line of the block and process the rest of the lines based on the lead character
sequence and indentation is only used to determine the start of the next sub-list level or the next list item of the same list. Any line which
not a list item will be treated as a continuation line of the last list item, regardless of its indentation level.

It is not possible to create mixed content list items without resorting to paragraphs. This is not considered a limitation because
real life mixed content lists should be double space or they are impossible to read and follow the structure.

A block quote prefix must appear as a prefix on all the lines in the block. Any block which does not have this prefix on all the lines
will be split into several blocks with a warning that the source is missing blank lines or that the leading `>` in the `> ` sequence
should be escaped if it is not intended to mark a block-quote.


Inline Formatting
-----------------

A - * or _ is used as an inline marker if it is followed by a non-blank character. The span is terminated by the same character which is
preceded by a non-blank character or the end of the block. In the latter case a warning is to be generated that an inline format is
not terminated.

All inline formatting directives will be terminated in the current paragraph block. Any unclosed sequences will be automatically closed
before the closing paragraph tag, in the reverse order in which they were opened.

The _ is only recognized as italic directive only if enabled in the options. Otherwise it is treated as a regular character to allow for
variable names with leading and embedded underscored from starting an italic or bold text sequece.


Headers
-------

Headers are blocks of text. Being blocks they are always surrounded by blank lines.

Headers consist of two types: Setext and atx.

Setext headers use = or - as the first character of the second line of the block. Anything after the first non-blank

Atx headers are specified with a seuquence of #, 1 to 6 character long followed by one or more spaces. The number of # determines the
heading tag `<h1>` to `<h6>` used for the header. Trailing #'s will be removed from the generated heading text.


Block Quotes
------------

All block quotes start with `> ` with **no indentation** and each additional block quote level adds another set of `> `. Note that the
single space between the > and the following text or next level block quote is part of the block quote level,
the rest of the spaces, if any, are treated for determining the indentation level.  This is to follow in the spirit of the original
spec stating that block quotes use the e-mail strategy for quoting text and most editors easily add block quoting level
by prefixing the lines with `> `.

The easiest way to treat block quotes is to look at the contained text as being created without the block-quote prefix and then
add the block-quote prefix to every line in the block-quote.


Lists and List Items
--------------------

Lists are blocks or consecutive blocks. A new list is always started on the first line of the block.

Consecutive blocks will be treated as part of the same list if and only if their list type is the same as the current list.
Otherwise, a warning is generated and a new list is started by the first non-matching list item type if it was preceded by a
blank.

A single block can have multiple list items, including nested list items if the lines begin with a list item directive.

An unordered list item starts with a single - + or * followed by one or more spaces.

An ordered list starts with a number followed by a period. To prevent accidental lists where a number is followed by a period, but
is not a list item, the first list item in the list should have a single digit. The rest of the items can have any number of digits
and not be sequentially numbered. A list that begins with an item of more than one digit will generate an error and process the
item as if it contained a single digit.

A list which consists of multiple blocks will have all its items' first line text wrapped in `<p>` tags, causing the list
to be double spaced.

A block will be associated with one of the currently open list based on its indentation level and list directive prefix.

To get two consecutive lists of the same type, instead of a single double spaced one, it is necessary to leave two blank lines between list items as in:

1. list 1 item 1
1. list 1 item 2
1. list 1 item 3

1. list 1 itme 4 (causes all items to be double spaced)
1. list 1 itme 5
1. list 1 itme 6


2. list 2 item 1 (single spaced list)
2. list 2 item 2
2. list 2 item 3


3. list 3 itme 1 (single spaced list)
3. list 3 itme 2
3. list 3 itme 3


Indented blocks that follow a list item will be associated with the previous list item whose nesting level matches the indentation
level of the line being considered. This makes it easier to create multi-paragraph list items that themselves contain sub-lists.

1. List 1 Item 1
    - sub list item 1
        sub list item 1 continuation line

            sub list item 1 code block 1

        sub list item 1 paragraph

            sub list item 1 code block 2

    - sub list item 2

    List 1 Item 1 paragraph

        List 1 Item 1 code block


Tables
------

Tables are contained in a single block with the first line defining the table header, the second being the line
separator identifying this block as a table block.

The rest of the lines being the table body. The leading and trailing `|` characters on the line are optional. As are
the `:` column alignment indicators on the second line and the spaces around the `|` column separators. The leading
and trailing spaces of the column text will be trimmed from the output.

The number of columns in each line should be the same any differences will be compensated by adding empty columns and
a warning generated to inform the user of that fact.

column 1 | column 2 | column 3 | ... | column N
--------:|:-------:|---------:| ---- | -------
column 1 | column 2 | column 3 | ... | column N
column 1 | column 2 | column 3 | ... | column N
column 1 | column 2 | column 3 | ... | column N
column 1 | column 2 | column 3 | ... | column N

Any line in the table block that does not contain a column separator `|` will be treated as if it has a blank line preceding
it and is the first line of the next block.


Links
-----

Same as markdown standard, with GitHub and Wiki link styles as selectable options. With the constraint that no spaces
can separate the `[]` and `()` or the `[]` and `[]` parts of the link.


Images
------

Same as links but with a preceding `!` character without any spaces between it and the `[` of the link.


Code Spans
----------

Same as markdown. With one or two \` backticks used to mark the start and end of a span of code text.


Horizontal Rule
---------------

Three or more hyphens, asterisks or underscores with or without spaces in between, by themselves on line without any other characters.

This should be a block containing a single line. If this line is not preceded or followed by a blank line then a warning
is generated and the line is processed as if it was preceded and followed by a blank line.

Indentation and block-quotes affect the placement of the horizontal rule in the proper hierarchy.

1. List item

    paragraph

    - - -

    paragraph


Code Blocks
-----------

A code block is a block of lines whose indent exceeds the current list nesting level indent by 4 or more spaces. The common number of
leading spaces of all lines in the block is removed from all the lines so that the relative indentation between the lines will be preserved.
This means that a code block does not have to start with the least indented line to have the whole code block's indentation reflect the
original indentation structure, yet the extra leading spaces will be removed without having to manually un-indent the lines.

For example, the following two code blocks will match indents because the relative indentation of the least indented lines is the same:

         for (i in range)
         {
             if (isOdd(i))
             {
                 printf("%i is odd\n");
             }

             else
             {
                 printf("%i is even\n");
             }
         }

         +----------------------------------------+
         |for (i in range)                        |
         |{                                       |
         |    if (isOdd(i))                       |
         |    {                                   |
         |        printf("%i is odd\n");          |
         |    }                                   |
         +----------------------------------------+
         +----------------------------------------+
         |    else                                |
         |    {                                   |
         |        printf("%i is even\n");         |
         |    }                                   |
         |}                                       |
         +----------------------------------------+

To create code blocks that can include blank lines use the code fencing directive of `\`\`\``, optionally followed by the language
of the enclosed code, GitHub style.

```
         for (i in range)
         {
             if (isOdd(i))
             {
                 printf("%i is odd\n");
             }

             else
             {
                 printf("%i is even\n");
             }
         }
```

         +-----------------------------------------+
         | for (i in range)                        |
         | {                                       |
         |     if (isOdd(i))                       |
         |     {                                   |
         |         printf("%i is odd\n");          |
         |     }                                   |
         |     else                                |
         |     {                                   |
         |         printf("%i is even\n");         |
         |     }                                   |
         | }                                       |
         +-----------------------------------------+


Examples:

> First Level block-quote
>
> > Second Level block-quote
> >
> > 1. List (item 1)
> >     i. Nested-List
> > continuation of i.
> >
> >     ii. The above blank line causes the whole Nested-List to be double spaced.
> >         continuation line for item ii.
> >
> >             code block for item ii.
> >             code block continuation.
> >
> >         paragraph for item ii.
> >
> >     paragraph for item 1, since it is the last item indented to this level.
> > continuation for the previous line, making it part of the previous block.
> >
> >     ```
> >                 code block for item 1. since its code fencing directive is indented to its level
> >             code block
> >     ```
> >
> > 2. List (item 2)
> >
> > 3. List (item 3) the above blank line, after block quoting is removed, causes the whole List to be double spaced.
>
> > Second Level new block quote, the above line causes the second level block-quote to terminate, hence this is a new
> > (continuation line for above) second level block quote.
> >
> > 4. New List item, will become list item numbered 1.
>

