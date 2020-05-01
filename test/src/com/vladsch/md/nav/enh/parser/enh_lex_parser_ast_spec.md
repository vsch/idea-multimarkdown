---
title: Lex Parser Spec
author:
version:
date: '2016-07-09'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

# Lex Parser Spec

Combined lexer/parser form markdown

empty text

```````````````````````````````` example Lex Parser Spec: 1
.
.
 MdFile:[0, 0]
````````````````````````````````


### Issue xxx-04

nested lists should have correct offsets

```````````````````````````````` example Issue xxx-04: 1
* 
  * list item
.
<ul>
  <li></li>
  <li>list item</li>
</ul>
.
 MdFile:[0, 16, "* \n   … * list item"]
   MdUnorderedListImpl:[0, 16, "* \n   … * list item"]
     MdUnorderedListItemImpl:[0, 3, "* \n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
       EOL:[2, 3, "\n"]
     WHITESPACE:[3, 5, "  "]
     MdUnorderedListItemImpl:[5, 16, "* lis … t item"]
       BULLET_LIST_ITEM_MARKER:[5, 7, "* "]
       MdTextBlockImpl:[7, 16, "list item"]
         TEXT:[7, 16, "list item"]
````````````````````````````````


nested lists should have correct offsets

```````````````````````````````` example Issue xxx-04: 2
* * list item
.
<ul>
  <li>
    <ul>
      <li>list item</li>
    </ul>
  </li>
</ul>
.
 MdFile:[0, 13, "* * l … ist item"]
   MdUnorderedListImpl:[0, 13, "* * l … ist item"]
     MdUnorderedListItemImpl:[0, 13, "* * l … ist item"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
       MdUnorderedListImpl:[2, 13, "* lis … t item"]
         MdUnorderedListItemImpl:[2, 13, "* lis … t item"]
           BULLET_LIST_ITEM_MARKER:[2, 4, "* "]
           MdTextBlockImpl:[4, 13, "list item"]
             TEXT:[4, 13, "list item"]
````````````````````````````````


lists with block quotes

```````````````````````````````` example Issue xxx-04: 3
> 1. item 1
> 2. item 2
> 3. item 3
> 4. item 4
.
<blockquote>
  <ol>
    <li>item 1</li>
    <li>item 2</li>
    <li>item 3</li>
    <li>item 4</li>
  </ol>
</blockquote>
.
 MdFile:[0, 47, "> 1.  … item 1\n> 2. item 2\n> 3. item 3\n> 4. item 4"]
   MdBlockQuoteImpl:[0, 47, "> 1.  … item 1\n> 2. item 2\n> 3. item 3\n> 4. item 4"]
     BLOCK_QUOTE_MARKER:[0, 2, "> "]
     MdOrderedListImpl:[2, 47, "1. it … em 1\n> 2. item 2\n> 3. item 3\n> 4. item 4"]
       MdOrderedListItemImpl:[2, 12, "1. item 1\n"]
         ORDERED_LIST_ITEM_MARKER:[2, 5, "1. "]
         MdTextBlockImpl:[5, 12, "item 1\n"]
           TEXT:[5, 11, "item 1"]
           EOL:[11, 12, "\n"]
       BLOCK_QUOTE_WHITESPACE:[12, 14, "> "]
       MdOrderedListItemImpl:[14, 24, "2. item 2\n"]
         ORDERED_LIST_ITEM_MARKER:[14, 17, "2. "]
         MdTextBlockImpl:[17, 24, "item 2\n"]
           TEXT:[17, 23, "item 2"]
           EOL:[23, 24, "\n"]
       BLOCK_QUOTE_WHITESPACE:[24, 26, "> "]
       MdOrderedListItemImpl:[26, 36, "3. item 3\n"]
         ORDERED_LIST_ITEM_MARKER:[26, 29, "3. "]
         MdTextBlockImpl:[29, 36, "item 3\n"]
           TEXT:[29, 35, "item 3"]
           EOL:[35, 36, "\n"]
       BLOCK_QUOTE_WHITESPACE:[36, 38, "> "]
       MdOrderedListItemImpl:[38, 47, "4. item 4"]
         ORDERED_LIST_ITEM_MARKER:[38, 41, "4. "]
         MdTextBlockImpl:[41, 47, "item 4"]
           TEXT:[41, 47, "item 4"]
````````````````````````````````


## GitHub Issue Marker

```````````````````````````````` example GitHub Issue Marker: 1
issue # 
.
<p>issue #</p>
.
 MdFile:[0, 8, "issue # "]
   MdParagraphImpl:[0, 8, "issue # "]
     MdTextBlockImpl:[0, 8, "issue # "]
       TEXT:[0, 7, "issue #"]
       WHITESPACE:[7, 8, " "]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 2
*issue #* 
.
<p><em>issue #</em></p>
.
 MdFile:[0, 10, "*issue #* "]
   MdParagraphImpl:[0, 10, "*issue #* "]
     MdTextBlockImpl:[0, 10, "*issue #* "]
       MdInlineItalicImpl:[0, 9, "*issue #*"]
         ITALIC_MARKER:[0, 1, "*"]
         ITALIC_TEXT:[1, 8, "issue #"]
         ITALIC_MARKER:[8, 9, "*"]
       WHITESPACE:[9, 10, " "]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 3
**issue #** 
.
<p><strong>issue #</strong></p>
.
 MdFile:[0, 12, "**iss … ue #** "]
   MdParagraphImpl:[0, 12, "**iss … ue #** "]
     MdTextBlockImpl:[0, 12, "**iss … ue #** "]
       MdInlineBoldImpl:[0, 11, "**iss … ue #**"]
         BOLD_MARKER:[0, 2, "**"]
         BOLD_TEXT:[2, 9, "issue #"]
         BOLD_MARKER:[9, 11, "**"]
       WHITESPACE:[11, 12, " "]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 4
**_issue #_**
.
<p><strong><em>issue #</em></strong></p>
.
 MdFile:[0, 13, "**_is … sue #_**"]
   MdParagraphImpl:[0, 13, "**_is … sue #_**"]
     MdTextBlockImpl:[0, 13, "**_is … sue #_**"]
       MdInlineBoldImpl:[0, 13, "**_is … sue #_**"]
         BOLD_MARKER:[0, 2, "**"]
         MdInlineItalicImpl:[2, 11, "_issue #_"]
           ITALIC_MARKER:[2, 3, "_"]
           BOLD_TEXT_ITALIC_TEXT:[3, 10, "issue #"]
           ITALIC_MARKER:[10, 11, "_"]
         BOLD_MARKER:[11, 13, "**"]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 5
~~_**issue #**_~~
.
<p><del><em><strong>issue #</strong></em></del></p>
.
 MdFile:[0, 17, "~~_** … issue #**_~~"]
   MdParagraphImpl:[0, 17, "~~_** … issue #**_~~"]
     MdTextBlockImpl:[0, 17, "~~_** … issue #**_~~"]
       MdInlineStrikethroughImpl:[0, 17, "~~_** … issue #**_~~"]
         STRIKETHROUGH_MARKER:[0, 2, "~~"]
         MdInlineItalicImpl:[2, 15, "_**is … sue #**_"]
           ITALIC_MARKER:[2, 3, "_"]
           MdInlineBoldImpl:[3, 14, "**iss … ue #**"]
             BOLD_MARKER:[3, 5, "**"]
             STRIKETHROUGH_TEXT_BOLD_TEXT_ITALIC_TEXT:[5, 12, "issue #"]
             BOLD_MARKER:[12, 14, "**"]
           ITALIC_MARKER:[14, 15, "_"]
         STRIKETHROUGH_MARKER:[15, 17, "~~"]
````````````````````````````````


