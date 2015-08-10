Markdown Syntax for Grammar-Kit
===============================

This is a work in progress and is not currently implemented.

Currently there are a lot of flavours of Markdown parsers and all differ in the details of how they recognize elements and convert to HTML.

The original syntax provided by [John Gruber](http://daringfireball.net/projects/markdown/syntax#header) is a good start but some changes were
applied to extend and in some cases simplify parsing, in others to give more control when recognizing ambiguous elements.

This was done to allow a simpler parser and to make it easier for a human to predict the results.
The [CommonMark Spec](http://spec.commonmark.org/0.21/) was considered but I found it
to be too complex and almost as long as the HTML spec which defeats the purpose of Markdown being simpler and easier to use.

The simplified strategy uses more stringent specification for handling of lists, titles, code and block quotes to eliminate ambiguity and
to allow for easier control of nested list formatting with multiple paragraphs and code blocks in the list items.


Paragraph Blocks
----------------

A Paragraph is a block of un-indented lines. Paragraphs are separated by one or more blank lines. Blank lines are only used to delimit paragraphs
and do no affect the generated output.

If hand-wraps option is turned off then all the lines in the paragraph are treated as a single line of text. Otherwise, every line in the paragraph
is suffixed with a `<br>` to preserve line breaks in the output, a la GitHub.

Inline Formatting
-----------------

All inline formatting directives will be terminated in the current paragraph block. Any unclosed sequences will be automatically closed before the closing
paragraph tag, in the reverse order in which they were opened.

The _ is only recognized as italic directive only if enabled by the options. Otherwise it is treated as a regular character to allow for
variable names with leading and embedded underscored from starting an italic text sequece.

Escaping Characters
-------------------



Titles
------




Indents and Lazy continuation
-----------------------------

All indents are multiples of 4 spaces, so no indent is 0-3 spaces, 1 indent is 4-7 spaces, etc.

Lazy continuation lines are ones with no indent which is 0-3 spaces. Any other indentation is not considered lazy and a match is attempted to previous
list item nesting level indentations. Any indentation at or greater than the expected list item indentation is treated as a match since code blocks
are only recognized when preceeded by a blank line.

Block Quotes
------------

All block quotes start with `> ` with **no indentation** and each additional block quote level adds another set of `> `. Note that the
single space between the > and the following text or next level block quote is part of the block quote level,
the rest of the spaces, if any, are treated for indent determination.  This is to follow in the spirit of the original spec stating that block
quotes use the e-mail strategy for quoting text and most editors easily add block quoting level by prefixing the lines with `> `.
It also makes it easier to read and generate complex layout in block quotes by first creating the layout
without block quotes and then adding the necessary level of block quotes by repeatedly prefixing the lines with `> `.

A line that has a lower level of block quoting ends all the higher levels of block quotes. A blank line or one containing only spaces ends all block quote levels.

For elements nested inside block quotes the block quote prefix, which included a single space following the `>`, is before the indentation level
of the line is considered. The easiest way to treat block quotes is to look at the contained text as being created without the block quotes and then
add the block quotes by prefixing the line with `> ` for every block quote level.

Lists and List Items
--------------------

An unordered list item starts with a single - + or * followed by one or more spaces.

An ordered list starts with a number followed by a period. To prevent accidental lists where a number is followed by a period, but is not a list item
the first list item should have a single digit. The rest of the items can have any number of digits and not be sequentially numbered.

If any list item, after the first one that starts the list, is preceeded by a blank line or contains at least one extra paragraph,
will cause all the items in the list to be paragraph wrapped, ie. look double spaced.

So to have two consecutive lists, instead of a single double spaced one, it is necessary to leave two blank lines between list items as in:

- list 1 item 1
- list 1 item 2
- list 1 item 3

- list 1 itme 4 (causes all items to be double spaced)
- list 1 itme 5
- list 1 itme 6


- list 2 item 1
- list 2 item 2
- list 2 item 3


- list 3 itme 1
- list 3 itme 2
- list 3 itme 3


Lazy continuation lines are associated with the most recent list item in all cases.

Indented lines that follow a list item will be associated with the previous list item whose nesting level matches the indentation
level of the line being considered. This makes it easier to create multi-paragraph list ites that themselves contain sub-lists.

Code Blocks
-----------

Code block is a block of lines whose indent exceeds the current list nesting level indent by 4 or more spaces. The common number of
leading spaces of all lines in the block is removed from all the lines so that the indentation will be preserved. This means that a
code block does not have to start with the least indented line to have the whole code block's indentation reflect the snippet's
indentation structure.

Example:

> First Level
>
> > Second Level
> >
> > 1. List (item 1)
> >     i. Nested-List
> > lazy continuation of i.
> >
> >     ii. The above blank line causes the whole Nested-List to be double spaced.
> >         continuation line for item ii.
> >             code block for item ii.
> >             code block continuation.
> > lazy continuation line for item ii.
> >     continuation line for item 1, since it is indented to its level.
> > lazy continuation for the previous line, making it part of item 1.
> >                 code block for item 1. since it is the last open list item. Indented 3 spaces from the block
> >             code block continuation.
> > 2. List (item 2)
> >
> > 3. List (item 3) the above blank line, after block quoting is removed, causes the whole List to be double spaced.
>
> > Second Level new block quote, the above line causes the second level block quote to terminate, hence this is a new
> > (continuation line for above) second level block quote.
>

Any text starting at the

Titles:





