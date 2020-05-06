---
title: Format Document Action Spec Test
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

# Format Document

:information_source: Default action for tests is `format-element, `
defined in
[FormatDocumentActionSpecTest.java: Line 65](FormatDocumentActionSpecTest.java#L65-L65)

* [ ] Add old GitHub lists
* [ ] Add fixed spacing option
* [ ] Add format options for lists

## HTML Blocks

```````````````````````````````` example HTML Blocks: 1
line 1

  <img src="i.jpg">

line 2
.
⦙line 1

  <img src="i.jpg">

line 2
````````````````````````````````


```````````````````````````````` example HTML Blocks: 2
line 1

<img src="i.jpg">

line 2
.
⦙line 1

<img src="i.jpg">

line 2
````````````````````````````````


## Headings

```````````````````````````````` example Headings: 1
# ⦙Heading 1 #

.
# ⦙Heading 1 #

````````````````````````````````


### In List

```````````````````````````````` example Headings - In List: 1
* list item

  # ⦙Heading 1

.
* list item

  # ⦙Heading 1

````````````````````````````````


```````````````````````````````` example Headings - In List: 2
* list item

  # ⦙Heading 1 #

.
* list item

  # ⦙Heading 1 #

````````````````````````````````


### Trailing Marker

```````````````````````````````` example(Headings - Trailing Marker: 1) options(style-heading-preference-as-is, style-atx-header-trailing-marker-equalize)
## ⦙Heading 2

.
## ⦙Heading 2

````````````````````````````````


```````````````````````````````` example(Headings - Trailing Marker: 2) options(style-heading-preference-as-is, style-atx-header-trailing-marker-equalize)
## ⦙Heading 2 #

.
## ⦙Heading 2 #

````````````````````````````````


## List Items

### Unordered

* [ ] Add: Bullet Item Marker type formatting
  * [ ] ANY
  * [ ] DASH
  * [ ] ASTERISK
  * [ ] PLUS
* [ ] Add fixed spacing option

```````````````````````````````` example List Items - Unordered: 1
*⦙ text
.
*⦙ text
````````````````````````````````


```````````````````````````````` example List Items - Unordered: 2
* list item with text alignment at the text edge so it all lines up with the bullet hanging indent.
.
⦙* list item with text alignment at the text edge so it all lines up with
  the bullet hanging indent.
````````````````````````````````


```````````````````````````````` example List Items - Unordered: 3
1. aasdf lasdfj sadlfj asdfl; as;lfdj als;fdj l;asdfj al;sdfj ;lasdfj ;lasdfj l;asjd fl;asdjf ;lasjkdf ;lasdjf ;adlsj fasld;fj 
   
   alsdfj;alsf jals;dfj asdl;fj as;dlfj ;alsj df;lasdjf l;asdkfj ;lasjkdf l;asdfj l;asdj adfsl;j 
.
⦙1. aasdf lasdfj sadlfj asdfl; as;lfdj als;fdj l;asdfj al;sdfj ;lasdfj
   ;lasdfj l;asjd fl;asdjf ;lasjkdf ;lasdjf ;adlsj fasld;fj
   
   alsdfj;alsf jals;dfj asdl;fj as;dlfj ;alsj df;lasdjf l;asdkfj ;lasjkdf l;asdfj l;asdj adfsl;j 
````````````````````````````````


```````````````````````````````` example List Items - Unordered: 4
* > block quote
1. > block quote  
.
⦙* > block quote
1. > block quote  
````````````````````````````````


### Task Items

```````````````````````````````` example(List Items - Task Items: 1) options(style-bullet-list-item-marker-asterisk, style-task-list-item-placement-complete-nested-to-non-task, margin[72])
- [x] Was complete low Fix: nasty bug introducing typing delay with preview enabled.
.
⦙- [x] Was complete low Fix: nasty bug introducing typing delay with
      preview enabled.
````````````````````````````````


```````````````````````````````` example(List Items - Task Items: 2) options(style-bullet-list-item-marker-asterisk, style-task-list-item-placement-complete-to-non-task, margin[72])
- [x] Was complete low Fix: nasty bug introducing typing delay with preview enabled.
.
⦙- [x] Was complete low Fix: nasty bug introducing typing delay with
      preview enabled.
````````````````````````````````


Format element should not absorb blank line.

```````````````````````````````` example(List Items - Task Items: 3) options(format-element, style-list-spacing-as-is)
* Item 1
    
  paragraph child item
* ⦙Fix: Move preview settings which are Monitor specific (Grey scale font smoothing) to Application settings. Review others that may
      need moving to IDE shared settings from project settings.

* Item 2

.
* Item 1
    
  paragraph child item
* ⦙Fix: Move preview settings which are Monitor specific (Grey scale font
  smoothing) to Application settings. Review others that may need moving
  to IDE shared settings from project settings.

* Item 2

````````````````````````````````


### Ordered

* [ ] Add `)` marker
* [ ] Add fixed spacing option

```````````````````````````````` example List Items - Ordered: 1
1.⦙ text
.
1.⦙ text
````````````````````````````````


Should not add blank line after last child of list item.

```````````````````````````````` example(List Items - Ordered: 2) options(style-list-spacing-as-is)
1. List item with paragraph child item without blank line after is a tight list.

   ⦙Paragraph child item.
2. Following list item.
3. Another list item.

.
1. List item with paragraph child item without blank line after is a tight list.

   ⦙Paragraph child item.
2. Following list item.
3. Another list item.

````````````````````````````````


```````````````````````````````` example(List Items - Ordered: 3) options(style-list-spacing-tight)
1. List item with paragraph child item without blank line after is a tight list.

   ⦙Paragraph child item.
2. Following list item.
3. Another list item.

.
1. List item with paragraph child item without blank line after is a tight list.

   ⦙Paragraph child item.
2. Following list item.
3. Another list item.

````````````````````````````````


```````````````````````````````` example(List Items - Ordered: 4) options(style-list-spacing-loosen)
1. List item with paragraph child item without blank line after is a tight list.

   ⦙Paragraph child item.
2. Following list item.
3. Another list item.

.
1. List item with paragraph child item without blank line after is a tight list.

   ⦙Paragraph child item.
2. Following list item.
3. Another list item.

````````````````````````````````


## Paragraph

```````````````````````````````` example(Paragraph: 1) options(margin[72])
⦙マルチバイ トマルチバイト マルチバイト マルチ バイトマル チバイ トマルチバイト マルチバイ トマル チバイト マルチバイト マルチバイト マルチバイト マルチバイト
.
⦙マルチバイ トマルチバイト マルチバイト マルチ バイトマル チバイ トマルチバイト マルチバイ トマル チバイト マルチバイト マルチバイト
マルチバイト マルチバイト
````````````````````````````````


```````````````````````````````` example(Paragraph: 2) options(margin[72], style-no-use-actual-char-width)
⦙マルチバイ トマルチバイト マルチバイト マルチ バイトマル チバイ トマルチバイト マルチバイ トマル チバイト マルチバイト マルチバイト マルチバイト マルチバイト
.
⦙マルチバイ トマルチバイト マルチバイト マルチ バイトマル チバイ トマルチバイト マルチバイ トマル チバイト マルチバイト マルチバイト
マルチバイト マルチバイト
````````````````````````````````


```````````````````````````````` example(Paragraph: 3) options(margin[72])
⦙异常情 况 的 错误 信息, mixed chars width wrapping mode test 供显 示给用户 异常情况的 错 误信息 供显 示给 用户
异常情 况的错误 信息，供显示给用户 异常情况的错 误信息，供显 示给用户  异常情况 的错误信息，供显示给用户
异常 情况的 错 误 信息，供显 示给 用户
.
⦙异常情 况 的 错误 信息, mixed chars width wrapping mode test 供显 示给用户 异常情况的 错 误信息
供显 示给 用户 异常情 况的错误 信息，供显示给用户 异常情况的错 误信息，供显 示给用户 异常情况 的错误信息，供显示给用户 异常 情况的
错 误 信息，供显 示给 用户
````````````````````````````````


```````````````````````````````` example(Paragraph: 4) options(margin[72], style-no-use-actual-char-width)
⦙异常情 况 的 错误 信息, mixed chars width wrapping mode test 供显 示给用户 异常情况的 错 误信息 供显 示给 用户
异常情 况的错误 信息，供显示给用户 异常情况的错 误信息，供显 示给用户  异常情况 的错误信息，供显示给用户
异常 情况的 错 误 信息，供显 示给 用户
.
⦙异常情 况 的 错误 信息, mixed chars width wrapping mode test 供显 示给用户 异常情况的 错 误信息
供显 示给 用户 异常情 况的错误 信息，供显示给用户 异常情况的错 误信息，供显 示给用户 异常情况 的错误信息，供显示给用户 异常 情况的
错 误 信息，供显 示给 用户
````````````````````````````````


## Edge Cases

* [ ] Break up into individual parts and keep full for the last test

### xxx-01

the markdown is doubled instead of formatting on format element

```````````````````````````````` example(Edge Cases - xxx-01: 1) options(format-element, margin[96])
![](\<AUTHOR_SIGNATURE\>){max-width=6cm max-height=2.4cm}  
**Johner Institut GmbH**, {.tab} **\<CUSTOMER\>**  
\<AUTHOR_NAME\>⦙ {.tab} Name:  
Project Consultant, {.tab} Position:  
<OFFICE_CITY>, \<BID_DATE\> {.tab} Place, Date: ____________________, _______________
{.Signatures}
    
.
![](\<AUTHOR_SIGNATURE\>){max-width=6cm max-height=2.4cm}  
**Johner Institut GmbH**, {.tab} **\<CUSTOMER\>**  
\<AUTHOR_NAME\>⦙ {.tab} Name:  
Project Consultant, {.tab} Position:  
<OFFICE_CITY>, \<BID_DATE\> {.tab} Place, Date: ____________________, _______________
{.Signatures}
    
````````````````````````````````


