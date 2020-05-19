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


### Abbreviation

`Abbreviation` `AbbreviationBlock`

```````````````````````````````` example Abbreviation: 1
text with abbr embedded

*[abbr]: abbreviation

.
<p>text with <abbr title="abbreviation">abbr</abbr> embedded</p>
.
 MdFile:[0, 48, "text  … with abbr embedded\n\n*[abbr]: abbreviation\n\n"]
   MdParagraphImpl:[0, 24, "text  … with abbr embedded\n"]
     MdTextBlockImpl:[0, 24, "text  … with abbr embedded\n"]
       TEXT:[0, 10, "text with "]
       MdAbbreviatedTextImpl:[10, 14, "abbr"]
         MdAbbreviationRefIdImpl:[10, 14, "abbr"]
           ABBREVIATED_TEXT:[10, 14, "abbr"]
       TEXT:[14, 23, " embedded"]
       EOL:[23, 24, "\n"]
   MdBlankLineImpl:[24, 25, "\n"]
     BLANK_LINE:[24, 25, "\n"]
   MdAbbreviationImpl:[25, 47, "*[abb … r]: abbreviation\n"]
     ABBREVIATION_OPEN:[25, 27, "*["]
     MdAbbreviationIdImpl:[27, 31, "abbr"]
       ABBREVIATION_SHORT_TEXT:[27, 31, "abbr"]
     ABBREVIATION_CLOSE:[31, 33, "]:"]
     WHITESPACE:[33, 34, " "]
     MdAbbreviationTextImpl:[34, 46, "abbre … viation"]
       ABBREVIATION_EXPANDED_TEXT:[34, 46, "abbre … viation"]
     EOL:[46, 47, "\n"]
   MdBlankLineImpl:[47, 48, "\n"]
     BLANK_LINE:[47, 48, "\n"]
````````````````````````````````


```````````````````````````````` example Abbreviation: 2
*[abbr]: abbreviation

text with abbr embedded
.
<p>text with <abbr title="abbreviation">abbr</abbr> embedded</p>
.
 MdFile:[0, 46, "*[abb … r]: abbreviation\n\ntext with abbr embedded"]
   MdAbbreviationImpl:[0, 22, "*[abb … r]: abbreviation\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     WHITESPACE:[8, 9, " "]
     MdAbbreviationTextImpl:[9, 21, "abbre … viation"]
       ABBREVIATION_EXPANDED_TEXT:[9, 21, "abbre … viation"]
     EOL:[21, 22, "\n"]
   MdBlankLineImpl:[22, 23, "\n"]
     BLANK_LINE:[22, 23, "\n"]
   MdParagraphImpl:[23, 46, "text  … with abbr embedded"]
     MdTextBlockImpl:[23, 46, "text  … with abbr embedded"]
       TEXT:[23, 33, "text with "]
       MdAbbreviatedTextImpl:[33, 37, "abbr"]
         MdAbbreviationRefIdImpl:[33, 37, "abbr"]
           ABBREVIATED_TEXT:[33, 37, "abbr"]
       TEXT:[37, 46, " embedded"]
````````````````````````````````


```````````````````````````````` example Abbreviation: 3
*[Abbr]:Abbreviation
.
.
 MdFile:[0, 20, "*[Abb … r]:Abbreviation"]
   MdAbbreviationImpl:[0, 20, "*[Abb … r]:Abbreviation"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "Abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "Abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     MdAbbreviationTextImpl:[8, 20, "Abbre … viation"]
       ABBREVIATION_EXPANDED_TEXT:[8, 20, "Abbre … viation"]
````````````````````````````````


```````````````````````````````` example Abbreviation: 4
*[Abbr]:Abbreviation

This has an Abbr embedded in it.
.
<p>This has an <abbr title="Abbreviation">Abbr</abbr> embedded in it.</p>
.
 MdFile:[0, 54, "*[Abb … r]:Abbreviation\n\nThis has an Abbr embedded in it."]
   MdAbbreviationImpl:[0, 21, "*[Abb … r]:Abbreviation\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "Abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "Abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     MdAbbreviationTextImpl:[8, 20, "Abbre … viation"]
       ABBREVIATION_EXPANDED_TEXT:[8, 20, "Abbre … viation"]
     EOL:[20, 21, "\n"]
   MdBlankLineImpl:[21, 22, "\n"]
     BLANK_LINE:[21, 22, "\n"]
   MdParagraphImpl:[22, 54, "This  … has an Abbr embedded in it."]
     MdTextBlockImpl:[22, 54, "This  … has an Abbr embedded in it."]
       TEXT:[22, 34, "This  … has an "]
       MdAbbreviatedTextImpl:[34, 38, "Abbr"]
         MdAbbreviationRefIdImpl:[34, 38, "Abbr"]
           ABBREVIATED_TEXT:[34, 38, "Abbr"]
       TEXT:[38, 54, " embe … dded in it."]
````````````````````````````````


No inline processing in expansion text.

```````````````````````````````` example Abbreviation: 5
*[Abbr]: Abbreviation has *emphasis*, **bold** or `code`

This has an Abbr embedded in it.
.
<p>This has an <abbr title="Abbreviation has *emphasis*, **bold** or `code`">Abbr</abbr> embedded in it.</p>
.
 MdFile:[0, 90, "*[Abb … r]: Abbreviation has *emphasis*, **bold** or `code`\n\nThis has an Abbr embedded in it."]
   MdAbbreviationImpl:[0, 57, "*[Abb … r]: Abbreviation has *emphasis*, **bold** or `code`\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "Abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "Abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     WHITESPACE:[8, 9, " "]
     MdAbbreviationTextImpl:[9, 56, "Abbre … viation has *emphasis*, **bold** or `code`"]
       ABBREVIATION_EXPANDED_TEXT:[9, 56, "Abbre … viation has *emphasis*, **bold** or `code`"]
     EOL:[56, 57, "\n"]
   MdBlankLineImpl:[57, 58, "\n"]
     BLANK_LINE:[57, 58, "\n"]
   MdParagraphImpl:[58, 90, "This  … has an Abbr embedded in it."]
     MdTextBlockImpl:[58, 90, "This  … has an Abbr embedded in it."]
       TEXT:[58, 70, "This  … has an "]
       MdAbbreviatedTextImpl:[70, 74, "Abbr"]
         MdAbbreviationRefIdImpl:[70, 74, "Abbr"]
           ABBREVIATED_TEXT:[70, 74, "Abbr"]
       TEXT:[74, 90, " embe … dded in it."]
````````````````````````````````


```````````````````````````````` example Abbreviation: 6
*[Abbr]: Abbreviation 1
*[Abbre]: Abbreviation 2
.
.
 MdFile:[0, 48, "*[Abb … r]: Abbreviation 1\n*[Abbre]: Abbreviation 2"]
   MdAbbreviationImpl:[0, 24, "*[Abb … r]: Abbreviation 1\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "Abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "Abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     WHITESPACE:[8, 9, " "]
     MdAbbreviationTextImpl:[9, 23, "Abbre … viation 1"]
       ABBREVIATION_EXPANDED_TEXT:[9, 23, "Abbre … viation 1"]
     EOL:[23, 24, "\n"]
   MdAbbreviationImpl:[24, 48, "*[Abb … re]: Abbreviation 2"]
     ABBREVIATION_OPEN:[24, 26, "*["]
     MdAbbreviationIdImpl:[26, 31, "Abbre"]
       ABBREVIATION_SHORT_TEXT:[26, 31, "Abbre"]
     ABBREVIATION_CLOSE:[31, 33, "]:"]
     WHITESPACE:[33, 34, " "]
     MdAbbreviationTextImpl:[34, 48, "Abbre … viation 2"]
       ABBREVIATION_EXPANDED_TEXT:[34, 48, "Abbre … viation 2"]
````````````````````````````````


```````````````````````````````` example Abbreviation: 7
*[Abbr]: Abbreviation 1
*[Abbre]: Abbreviation 2

This has an Abbre embedded in it.
And this has another Abbr embedded in it.
.
<p>This has an <abbr title="Abbreviation 2">Abbre</abbr> embedded in it.
And this has another <abbr title="Abbreviation 1">Abbr</abbr> embedded in it.</p>
.
 MdFile:[0, 125, "*[Abb … r]: Abbreviation 1\n*[Abbre]: Abbreviation 2\n\nThis has an Abbre embedded in it.\nAnd this has another Abbr embedded in it."]
   MdAbbreviationImpl:[0, 24, "*[Abb … r]: Abbreviation 1\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "Abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "Abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     WHITESPACE:[8, 9, " "]
     MdAbbreviationTextImpl:[9, 23, "Abbre … viation 1"]
       ABBREVIATION_EXPANDED_TEXT:[9, 23, "Abbre … viation 1"]
     EOL:[23, 24, "\n"]
   MdAbbreviationImpl:[24, 49, "*[Abb … re]: Abbreviation 2\n"]
     ABBREVIATION_OPEN:[24, 26, "*["]
     MdAbbreviationIdImpl:[26, 31, "Abbre"]
       ABBREVIATION_SHORT_TEXT:[26, 31, "Abbre"]
     ABBREVIATION_CLOSE:[31, 33, "]:"]
     WHITESPACE:[33, 34, " "]
     MdAbbreviationTextImpl:[34, 48, "Abbre … viation 2"]
       ABBREVIATION_EXPANDED_TEXT:[34, 48, "Abbre … viation 2"]
     EOL:[48, 49, "\n"]
   MdBlankLineImpl:[49, 50, "\n"]
     BLANK_LINE:[49, 50, "\n"]
   MdParagraphImpl:[50, 125, "This  … has an Abbre embedded in it.\nAnd this has another Abbr embedded in it."]
     MdTextBlockImpl:[50, 125, "This  … has an Abbre embedded in it.\nAnd this has another Abbr embedded in it."]
       TEXT:[50, 62, "This  … has an "]
       MdAbbreviatedTextImpl:[62, 67, "Abbre"]
         MdAbbreviationRefIdImpl:[62, 67, "Abbre"]
           ABBREVIATED_TEXT:[62, 67, "Abbre"]
       TEXT:[67, 83, " embe … dded in it."]
       EOL:[83, 84, "\n"]
       TEXT:[84, 105, "And t … his has another "]
       MdAbbreviatedTextImpl:[105, 109, "Abbr"]
         MdAbbreviationRefIdImpl:[105, 109, "Abbr"]
           ABBREVIATED_TEXT:[105, 109, "Abbr"]
       TEXT:[109, 125, " embe … dded in it."]
````````````````````````````````


```````````````````````````````` example Abbreviation: 8
*[U.S.A.]: United States of America
*[US of A]: United States of America

U.S.A. is an abbreviation and so is US of A, an abbreviation.
.
<p><abbr title="United States of America">U.S.A.</abbr> is an abbreviation and so is <abbr title="United States of America">US of A</abbr>, an abbreviation.</p>
.
 MdFile:[0, 135, "*[U.S … .A.]: United States of America\n*[US of A]: United States of America\n\nU.S.A. is an abbreviation and so is US of A, an abbreviation."]
   MdAbbreviationImpl:[0, 36, "*[U.S … .A.]: United States of America\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 8, "U.S.A."]
       ABBREVIATION_SHORT_TEXT:[2, 8, "U.S.A."]
     ABBREVIATION_CLOSE:[8, 10, "]:"]
     WHITESPACE:[10, 11, " "]
     MdAbbreviationTextImpl:[11, 35, "Unite … d States of America"]
       ABBREVIATION_EXPANDED_TEXT:[11, 35, "Unite … d States of America"]
     EOL:[35, 36, "\n"]
   MdAbbreviationImpl:[36, 73, "*[US  … of A]: United States of America\n"]
     ABBREVIATION_OPEN:[36, 38, "*["]
     MdAbbreviationIdImpl:[38, 45, "US of A"]
       ABBREVIATION_SHORT_TEXT:[38, 45, "US of A"]
     ABBREVIATION_CLOSE:[45, 47, "]:"]
     WHITESPACE:[47, 48, " "]
     MdAbbreviationTextImpl:[48, 72, "Unite … d States of America"]
       ABBREVIATION_EXPANDED_TEXT:[48, 72, "Unite … d States of America"]
     EOL:[72, 73, "\n"]
   MdBlankLineImpl:[73, 74, "\n"]
     BLANK_LINE:[73, 74, "\n"]
   MdParagraphImpl:[74, 135, "U.S.A … . is an abbreviation and so is US of A, an abbreviation."]
     MdTextBlockImpl:[74, 135, "U.S.A … . is an abbreviation and so is US of A, an abbreviation."]
       MdAbbreviatedTextImpl:[74, 80, "U.S.A."]
         MdAbbreviationRefIdImpl:[74, 80, "U.S.A."]
           ABBREVIATED_TEXT:[74, 80, "U.S.A."]
       TEXT:[80, 110, " is a … n abbreviation and so is "]
       MdAbbreviatedTextImpl:[110, 117, "US of A"]
         MdAbbreviationRefIdImpl:[110, 117, "US of A"]
           ABBREVIATED_TEXT:[110, 117, "US of A"]
       TEXT:[117, 135, ", an  … abbreviation."]
````````````````````````````````


```````````````````````````````` example Abbreviation: 9
*[US]: United States
*[U.S.A.]: United States of America
*[US of A]: United States of America

U.S.A., US of A, and US are all abbreviations.
.
<p><abbr title="United States of America">U.S.A.</abbr>, <abbr title="United States of America">US of A</abbr>, and <abbr title="United States">US</abbr> are all abbreviations.</p>
.
 MdFile:[0, 141, "*[US] … : United States\n*[U.S.A.]: United States of America\n*[US of A]: United States of America\n\nU.S.A., US of A, and US are all abbreviations."]
   MdAbbreviationImpl:[0, 21, "*[US] … : United States\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 4, "US"]
       ABBREVIATION_SHORT_TEXT:[2, 4, "US"]
     ABBREVIATION_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdAbbreviationTextImpl:[7, 20, "Unite … d States"]
       ABBREVIATION_EXPANDED_TEXT:[7, 20, "Unite … d States"]
     EOL:[20, 21, "\n"]
   MdAbbreviationImpl:[21, 57, "*[U.S … .A.]: United States of America\n"]
     ABBREVIATION_OPEN:[21, 23, "*["]
     MdAbbreviationIdImpl:[23, 29, "U.S.A."]
       ABBREVIATION_SHORT_TEXT:[23, 29, "U.S.A."]
     ABBREVIATION_CLOSE:[29, 31, "]:"]
     WHITESPACE:[31, 32, " "]
     MdAbbreviationTextImpl:[32, 56, "Unite … d States of America"]
       ABBREVIATION_EXPANDED_TEXT:[32, 56, "Unite … d States of America"]
     EOL:[56, 57, "\n"]
   MdAbbreviationImpl:[57, 94, "*[US  … of A]: United States of America\n"]
     ABBREVIATION_OPEN:[57, 59, "*["]
     MdAbbreviationIdImpl:[59, 66, "US of A"]
       ABBREVIATION_SHORT_TEXT:[59, 66, "US of A"]
     ABBREVIATION_CLOSE:[66, 68, "]:"]
     WHITESPACE:[68, 69, " "]
     MdAbbreviationTextImpl:[69, 93, "Unite … d States of America"]
       ABBREVIATION_EXPANDED_TEXT:[69, 93, "Unite … d States of America"]
     EOL:[93, 94, "\n"]
   MdBlankLineImpl:[94, 95, "\n"]
     BLANK_LINE:[94, 95, "\n"]
   MdParagraphImpl:[95, 141, "U.S.A … ., US of A, and US are all abbreviations."]
     MdTextBlockImpl:[95, 141, "U.S.A … ., US of A, and US are all abbreviations."]
       MdAbbreviatedTextImpl:[95, 101, "U.S.A."]
         MdAbbreviationRefIdImpl:[95, 101, "U.S.A."]
           ABBREVIATED_TEXT:[95, 101, "U.S.A."]
       TEXT:[101, 103, ", "]
       MdAbbreviatedTextImpl:[103, 110, "US of A"]
         MdAbbreviationRefIdImpl:[103, 110, "US of A"]
           ABBREVIATED_TEXT:[103, 110, "US of A"]
       TEXT:[110, 116, ", and "]
       MdAbbreviatedTextImpl:[116, 118, "US"]
         MdAbbreviationRefIdImpl:[116, 118, "US"]
           ABBREVIATED_TEXT:[116, 118, "US"]
       TEXT:[118, 141, " are  … all abbreviations."]
````````````````````````````````


```````````````````````````````` example Abbreviation: 10
*[Abbr]: Abbreviation
[Abbr]: http://test.com

This is an Abbr and this is not [Abbr].

.
<p>This is an <abbr title="Abbreviation">Abbr</abbr> and this is not <a href="http://test.com">Abbr</a>.</p>
.
 MdFile:[0, 88, "*[Abb … r]: Abbreviation\n[Abbr]: http://test.com\n\nThis is an Abbr and this is not [Abbr].\n\n"]
   MdAbbreviationImpl:[0, 22, "*[Abb … r]: Abbreviation\n"]
     ABBREVIATION_OPEN:[0, 2, "*["]
     MdAbbreviationIdImpl:[2, 6, "Abbr"]
       ABBREVIATION_SHORT_TEXT:[2, 6, "Abbr"]
     ABBREVIATION_CLOSE:[6, 8, "]:"]
     WHITESPACE:[8, 9, " "]
     MdAbbreviationTextImpl:[9, 21, "Abbre … viation"]
       ABBREVIATION_EXPANDED_TEXT:[9, 21, "Abbre … viation"]
     EOL:[21, 22, "\n"]
   MdReferenceImpl:[22, 46, "[Abbr … ]: http://test.com\n"]
     REFERENCE_TEXT_OPEN:[22, 23, "["]
     MdReferenceIdentifierImpl:[23, 27, "Abbr"]
       REFERENCE_TEXT_LEAF:[23, 27, "Abbr"]
     REFERENCE_TEXT_CLOSE:[27, 29, "]:"]
     WHITESPACE:[29, 30, " "]
     MdReferenceLinkRefImpl:[30, 45, "http: … //test.com"]
       REFERENCE_LINK_REF:[30, 45, "http: … //test.com"]
     EOL:[45, 46, "\n"]
   MdBlankLineImpl:[46, 47, "\n"]
     BLANK_LINE:[46, 47, "\n"]
   MdParagraphImpl:[47, 87, "This  … is an Abbr and this is not [Abbr].\n"]
     MdTextBlockImpl:[47, 87, "This  … is an Abbr and this is not [Abbr].\n"]
       TEXT:[47, 58, "This  … is an "]
       MdAbbreviatedTextImpl:[58, 62, "Abbr"]
         MdAbbreviationRefIdImpl:[58, 62, "Abbr"]
           ABBREVIATED_TEXT:[58, 62, "Abbr"]
       TEXT:[62, 79, " and  … this is not "]
       MdReferenceLinkImpl:[79, 85, "[Abbr]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[79, 80, "["]
         MdReferenceLinkReferenceImpl:[80, 84, "Abbr"]
           REFERENCE_LINK_REFERENCE_LEAF:[80, 84, "Abbr"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[84, 85, "]"]
       TEXT:[85, 86, "."]
       EOL:[86, 87, "\n"]
   MdBlankLineImpl:[87, 88, "\n"]
     BLANK_LINE:[87, 88, "\n"]
````````````````````````````````


An abbreviation that is not on the first line is just text.

```````````````````````````````` example Abbreviation: 11
Paragraph with second line having a reference
*[test]: test abbreviation

.
<p>Paragraph with second line having a reference</p>
.
 MdFile:[0, 74, "Parag … raph with second line having a reference\n*[test]: test abbreviation\n\n"]
   MdParagraphImpl:[0, 46, "Parag … raph with second line having a reference\n"]
     MdTextBlockImpl:[0, 46, "Parag … raph with second line having a reference\n"]
       TEXT:[0, 45, "Parag … raph with second line having a reference"]
       EOL:[45, 46, "\n"]
   MdAbbreviationImpl:[46, 73, "*[tes … t]: test abbreviation\n"]
     ABBREVIATION_OPEN:[46, 48, "*["]
     MdAbbreviationIdImpl:[48, 52, "test"]
       ABBREVIATION_SHORT_TEXT:[48, 52, "test"]
     ABBREVIATION_CLOSE:[52, 54, "]:"]
     WHITESPACE:[54, 55, " "]
     MdAbbreviationTextImpl:[55, 72, "test  … abbreviation"]
       ABBREVIATION_EXPANDED_TEXT:[55, 72, "test  … abbreviation"]
     EOL:[72, 73, "\n"]
   MdBlankLineImpl:[73, 74, "\n"]
     BLANK_LINE:[73, 74, "\n"]
````````````````````````````````


References loose special characters when abbreviation extension is included

```````````````````````````````` example(Abbreviation: 12) options(no-abbr)
* Fix: [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]
    
[#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]: https://github.com/vsch/flexmark-java/issues/326

.
<ul>
  <li>Fix: <a href="https://github.com/vsch/flexmark-java/issues/326">#326, flexmark-html-parser - multiple &lt;code&gt; inside &lt;pre&gt; bug</a></li>
</ul>
.
 MdFile:[0, 199, "* Fix … : [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n    \n[#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]: https://github.com/vsch/flexmark-java/issues/326\n\n"]
   MdUnorderedListImpl:[0, 75, "* Fix … : [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n"]
     MdUnorderedListItemImpl:[0, 75, "* Fix … : [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
       MdTextBlockImpl:[2, 75, "Fix:  … [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n"]
         TEXT:[2, 7, "Fix: "]
         MdReferenceLinkImpl:[7, 74, "[#326 … , flexmark-html-parser - multiple \<code\> inside \<pre\> bug]"]
           REFERENCE_LINK_REFERENCE_OPEN2:[7, 8, "["]
           MdReferenceLinkReferenceImpl:[8, 73, "#326, …  flexmark-html-parser - multiple \<code\> inside \<pre\> bug"]
             REFERENCE_LINK_REFERENCE_LEAF:[8, 46, "#326, …  flexmark-html-parser - multiple "]
             MdInlineSpecialTextImpl:[46, 48, "\<"]
               SPECIAL_TEXT_MARKER:[46, 47, "\"]
               SPECIAL_TEXT:[47, 48, "<"]
             REFERENCE_LINK_REFERENCE_LEAF:[48, 52, "code"]
             MdInlineSpecialTextImpl:[52, 54, "\>"]
               SPECIAL_TEXT_MARKER:[52, 53, "\"]
               SPECIAL_TEXT:[53, 54, ">"]
             REFERENCE_LINK_REFERENCE_LEAF:[54, 62, " inside "]
             MdInlineSpecialTextImpl:[62, 64, "\<"]
               SPECIAL_TEXT_MARKER:[62, 63, "\"]
               SPECIAL_TEXT:[63, 64, "<"]
             REFERENCE_LINK_REFERENCE_LEAF:[64, 67, "pre"]
             MdInlineSpecialTextImpl:[67, 69, "\>"]
               SPECIAL_TEXT_MARKER:[67, 68, "\"]
               SPECIAL_TEXT:[68, 69, ">"]
             REFERENCE_LINK_REFERENCE_LEAF:[69, 73, " bug"]
           REFERENCE_LINK_REFERENCE_CLOSE2:[73, 74, "]"]
         EOL:[74, 75, "\n"]
   MdBlankLineImpl:[75, 80, "    \n"]
     BLANK_LINE:[75, 80, "    \n"]
   MdReferenceImpl:[80, 198, "[#326 … , flexmark-html-parser - multiple \<code\> inside \<pre\> bug]: https://github.com/vsch/flexmark-java/issues/326\n"]
     REFERENCE_TEXT_OPEN:[80, 81, "["]
     MdReferenceIdentifierImpl:[81, 146, "#326, …  flexmark-html-parser - multiple \<code\> inside \<pre\> bug"]
       REFERENCE_TEXT_LEAF:[81, 146, "#326, …  flexmark-html-parser - multiple \<code\> inside \<pre\> bug"]
     REFERENCE_TEXT_CLOSE:[146, 148, "]:"]
     WHITESPACE:[148, 149, " "]
     MdReferenceLinkRefImpl:[149, 197, "https … ://github.com/vsch/flexmark-java/issues/326"]
       REFERENCE_LINK_REF:[149, 197, "https … ://github.com/vsch/flexmark-java/issues/326"]
     EOL:[197, 198, "\n"]
   MdBlankLineImpl:[198, 199, "\n"]
     BLANK_LINE:[198, 199, "\n"]
````````````````````````````````


References loose special characters when abbreviation extension is included

```````````````````````````````` example Abbreviation: 13
* Fix: [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]
    
[#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]: https://github.com/vsch/flexmark-java/issues/326

.
<ul>
  <li>Fix: <a href="https://github.com/vsch/flexmark-java/issues/326">#326, flexmark-html-parser - multiple &lt;code&gt; inside &lt;pre&gt; bug</a></li>
</ul>
.
 MdFile:[0, 199, "* Fix … : [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n    \n[#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]: https://github.com/vsch/flexmark-java/issues/326\n\n"]
   MdUnorderedListImpl:[0, 75, "* Fix … : [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n"]
     MdUnorderedListItemImpl:[0, 75, "* Fix … : [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
       MdTextBlockImpl:[2, 75, "Fix:  … [#326, flexmark-html-parser - multiple \<code\> inside \<pre\> bug]\n"]
         TEXT:[2, 7, "Fix: "]
         MdReferenceLinkImpl:[7, 74, "[#326 … , flexmark-html-parser - multiple \<code\> inside \<pre\> bug]"]
           REFERENCE_LINK_REFERENCE_OPEN2:[7, 8, "["]
           MdReferenceLinkReferenceImpl:[8, 73, "#326, …  flexmark-html-parser - multiple \<code\> inside \<pre\> bug"]
             REFERENCE_LINK_REFERENCE_LEAF:[8, 46, "#326, …  flexmark-html-parser - multiple "]
             MdInlineSpecialTextImpl:[46, 48, "\<"]
               SPECIAL_TEXT_MARKER:[46, 47, "\"]
               SPECIAL_TEXT:[47, 48, "<"]
             REFERENCE_LINK_REFERENCE_LEAF:[48, 52, "code"]
             MdInlineSpecialTextImpl:[52, 54, "\>"]
               SPECIAL_TEXT_MARKER:[52, 53, "\"]
               SPECIAL_TEXT:[53, 54, ">"]
             REFERENCE_LINK_REFERENCE_LEAF:[54, 62, " inside "]
             MdInlineSpecialTextImpl:[62, 64, "\<"]
               SPECIAL_TEXT_MARKER:[62, 63, "\"]
               SPECIAL_TEXT:[63, 64, "<"]
             REFERENCE_LINK_REFERENCE_LEAF:[64, 67, "pre"]
             MdInlineSpecialTextImpl:[67, 69, "\>"]
               SPECIAL_TEXT_MARKER:[67, 68, "\"]
               SPECIAL_TEXT:[68, 69, ">"]
             REFERENCE_LINK_REFERENCE_LEAF:[69, 73, " bug"]
           REFERENCE_LINK_REFERENCE_CLOSE2:[73, 74, "]"]
         EOL:[74, 75, "\n"]
   MdBlankLineImpl:[75, 80, "    \n"]
     BLANK_LINE:[75, 80, "    \n"]
   MdReferenceImpl:[80, 198, "[#326 … , flexmark-html-parser - multiple \<code\> inside \<pre\> bug]: https://github.com/vsch/flexmark-java/issues/326\n"]
     REFERENCE_TEXT_OPEN:[80, 81, "["]
     MdReferenceIdentifierImpl:[81, 146, "#326, …  flexmark-html-parser - multiple \<code\> inside \<pre\> bug"]
       REFERENCE_TEXT_LEAF:[81, 146, "#326, …  flexmark-html-parser - multiple \<code\> inside \<pre\> bug"]
     REFERENCE_TEXT_CLOSE:[146, 148, "]:"]
     WHITESPACE:[148, 149, " "]
     MdReferenceLinkRefImpl:[149, 197, "https … ://github.com/vsch/flexmark-java/issues/326"]
       REFERENCE_LINK_REF:[149, 197, "https … ://github.com/vsch/flexmark-java/issues/326"]
     EOL:[197, 198, "\n"]
   MdBlankLineImpl:[198, 199, "\n"]
     BLANK_LINE:[198, 199, "\n"]
````````````````````````````````


### AnchorLink

`AnchorLink`

```````````````````````````````` example AnchorLink: 1
Setext Heading 1
===
Setext Heading 2
---
# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6
.
<h1><a href="#setext-heading-1" id="setext-heading-1"></a>Setext Heading 1</h1>
<h2><a href="#setext-heading-2" id="setext-heading-2"></a>Setext Heading 2</h2>
<h1><a href="#heading-1" id="heading-1"></a>Heading 1</h1>
<h2><a href="#heading-2" id="heading-2"></a>Heading 2</h2>
<h3><a href="#heading-3" id="heading-3"></a>Heading 3</h3>
<h4><a href="#heading-4" id="heading-4"></a>Heading 4</h4>
<h5><a href="#heading-5" id="heading-5"></a>Heading 5</h5>
<h6><a href="#heading-6" id="heading-6"></a>Heading 6</h6>
.
 MdFile:[0, 128, "Setex … t Heading 1\n===\nSetext Heading 2\n---\n# Heading 1\n## Heading 2\n### Heading 3\n#### Heading 4\n##### Heading 5\n###### Heading 6"]
   MdSetextHeaderImpl:[0, 21, "Setex … t Heading 1\n===\n"]
     MdHeaderTextImpl:[0, 16, "Setex … t Heading 1"]
       HEADER_TEXT:[0, 16, "Setex … t Heading 1"]
     EOL:[16, 17, "\n"]
     HEADER_SETEXT_MARKER:[17, 20, "==="]
     EOL:[20, 21, "\n"]
   MdSetextHeaderImpl:[21, 42, "Setex … t Heading 2\n---\n"]
     MdHeaderTextImpl:[21, 37, "Setex … t Heading 2"]
       HEADER_TEXT:[21, 37, "Setex … t Heading 2"]
     EOL:[37, 38, "\n"]
     HEADER_SETEXT_MARKER:[38, 41, "---"]
     EOL:[41, 42, "\n"]
   MdAtxHeaderImpl:[42, 54, "# Hea … ding 1\n"]
     HEADER_ATX_MARKER:[42, 43, "#"]
     WHITESPACE:[43, 44, " "]
     MdHeaderTextImpl:[44, 53, "Heading 1"]
       HEADER_TEXT:[44, 53, "Heading 1"]
     EOL:[53, 54, "\n"]
   MdAtxHeaderImpl:[54, 67, "## He … ading 2\n"]
     HEADER_ATX_MARKER:[54, 56, "##"]
     WHITESPACE:[56, 57, " "]
     MdHeaderTextImpl:[57, 66, "Heading 2"]
       HEADER_TEXT:[57, 66, "Heading 2"]
     EOL:[66, 67, "\n"]
   MdAtxHeaderImpl:[67, 81, "### H … eading 3\n"]
     HEADER_ATX_MARKER:[67, 70, "###"]
     WHITESPACE:[70, 71, " "]
     MdHeaderTextImpl:[71, 80, "Heading 3"]
       HEADER_TEXT:[71, 80, "Heading 3"]
     EOL:[80, 81, "\n"]
   MdAtxHeaderImpl:[81, 96, "####  … Heading 4\n"]
     HEADER_ATX_MARKER:[81, 85, "####"]
     WHITESPACE:[85, 86, " "]
     MdHeaderTextImpl:[86, 95, "Heading 4"]
       HEADER_TEXT:[86, 95, "Heading 4"]
     EOL:[95, 96, "\n"]
   MdAtxHeaderImpl:[96, 112, "##### …  Heading 5\n"]
     HEADER_ATX_MARKER:[96, 101, "#####"]
     WHITESPACE:[101, 102, " "]
     MdHeaderTextImpl:[102, 111, "Heading 5"]
       HEADER_TEXT:[102, 111, "Heading 5"]
     EOL:[111, 112, "\n"]
   MdAtxHeaderImpl:[112, 128, "##### … # Heading 6"]
     HEADER_ATX_MARKER:[112, 118, "######"]
     WHITESPACE:[118, 119, " "]
     MdHeaderTextImpl:[119, 128, "Heading 6"]
       HEADER_TEXT:[119, 128, "Heading 6"]
````````````````````````````````


### AutoLink

`AutoLink`

Wraped autolink

```````````````````````````````` example AutoLink: 1
text <http://autolink.com> embedded

.
<p>text <a href="http://autolink.com">http://autolink.com</a> embedded</p>
.
 MdFile:[0, 37, "text  … <http://autolink.com> embedded\n\n"]
   MdParagraphImpl:[0, 36, "text  … <http://autolink.com> embedded\n"]
     MdTextBlockImpl:[0, 36, "text  … <http://autolink.com> embedded\n"]
       TEXT:[0, 5, "text "]
       MdAutoLinkImpl:[5, 26, "<http … ://autolink.com>"]
         AUTO_LINK_OPEN:[5, 6, "<"]
         MdAutoLinkRefImpl:[6, 25, "http: … //autolink.com"]
           AUTO_LINK_REF:[6, 25, "http: … //autolink.com"]
         AUTO_LINK_CLOSE:[25, 26, ">"]
       TEXT:[26, 35, " embedded"]
       EOL:[35, 36, "\n"]
   MdBlankLineImpl:[36, 37, "\n"]
     BLANK_LINE:[36, 37, "\n"]
````````````````````````````````


Plain autolink

```````````````````````````````` example AutoLink: 2
text http://autolink.com embedded

.
<p>text <a href="http://autolink.com">http://autolink.com</a> embedded</p>
.
 MdFile:[0, 35, "text  … http://autolink.com embedded\n\n"]
   MdParagraphImpl:[0, 34, "text  … http://autolink.com embedded\n"]
     MdTextBlockImpl:[0, 34, "text  … http://autolink.com embedded\n"]
       TEXT:[0, 5, "text "]
       MdAutoLinkImpl:[5, 24, "http: … //autolink.com"]
         MdAutoLinkRefImpl:[5, 24, "http: … //autolink.com"]
           AUTO_LINK_REF:[5, 24, "http: … //autolink.com"]
       TEXT:[24, 33, " embedded"]
       EOL:[33, 34, "\n"]
   MdBlankLineImpl:[34, 35, "\n"]
     BLANK_LINE:[34, 35, "\n"]
````````````````````````````````


### BlockQuote

`BlockQuote`

Lazy continuation, no prefix

```````````````````````````````` example BlockQuote: 1
> block quote
with lazy continuation
>
.
<blockquote>
  <p>block quote
  with lazy continuation</p>
</blockquote>
.
 MdFile:[0, 38, "> blo … ck quote\nwith lazy continuation\n>"]
   MdBlockQuoteImpl:[0, 38, "> blo … ck quote\nwith lazy continuation\n>"]
     BLOCK_QUOTE_MARKER:[0, 2, "> "]
     MdParagraphImpl:[2, 37, "block …  quote\nwith lazy continuation\n"]
       MdTextBlockImpl:[2, 37, "block …  quote\nwith lazy continuation\n"]
         TEXT:[2, 13, "block …  quote"]
         EOL:[13, 14, "\n"]
         TEXT:[14, 36, "with  … lazy continuation"]
         EOL:[36, 37, "\n"]
     BLOCK_QUOTE_WHITESPACE:[37, 38, ">"]
````````````````````````````````


Lazy continuation, with prefix

```````````````````````````````` example BlockQuote: 2
> block quote
> with lazy continuation
>
.
<blockquote>
  <p>block quote
  with lazy continuation</p>
</blockquote>
.
 MdFile:[0, 40, "> blo … ck quote\n> with lazy continuation\n>"]
   MdBlockQuoteImpl:[0, 40, "> blo … ck quote\n> with lazy continuation\n>"]
     BLOCK_QUOTE_MARKER:[0, 2, "> "]
     MdParagraphImpl:[2, 39, "block …  quote\n> with lazy continuation\n"]
       MdTextBlockImpl:[2, 39, "block …  quote\n> with lazy continuation\n"]
         TEXT:[2, 13, "block …  quote"]
         EOL:[13, 14, "\n"]
         BLOCK_QUOTE_WHITESPACE:[14, 16, "> "]
         TEXT:[16, 38, "with  … lazy continuation"]
         EOL:[38, 39, "\n"]
     BLOCK_QUOTE_WHITESPACE:[39, 40, ">"]
````````````````````````````````


Nested, Lazy continuation, no prefix

```````````````````````````````` example BlockQuote: 3
>> block quote
with lazy continuation
>>
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 40, ">> bl … ock quote\nwith lazy continuation\n>>"]
   MdBlockQuoteImpl:[0, 40, ">> bl … ock quote\nwith lazy continuation\n>>"]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 40, "> blo … ck quote\nwith lazy continuation\n>>"]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 38, "block …  quote\nwith lazy continuation\n"]
         MdTextBlockImpl:[3, 38, "block …  quote\nwith lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           TEXT:[15, 37, "with  … lazy continuation"]
           EOL:[37, 38, "\n"]
       BLOCK_QUOTE_WHITESPACE:[38, 40, ">>"]
````````````````````````````````


Nested, Lazy continuation, with prefix

```````````````````````````````` example BlockQuote: 4
>> block quote
>> with lazy continuation
>>
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 43, ">> bl … ock quote\n>> with lazy continuation\n>>"]
   MdBlockQuoteImpl:[0, 43, ">> bl … ock quote\n>> with lazy continuation\n>>"]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 43, "> blo … ck quote\n>> with lazy continuation\n>>"]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 41, "block …  quote\n>> with lazy continuation\n"]
         MdTextBlockImpl:[3, 41, "block …  quote\n>> with lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           BLOCK_QUOTE_WHITESPACE:[15, 18, ">> "]
           TEXT:[18, 40, "with  … lazy continuation"]
           EOL:[40, 41, "\n"]
       BLOCK_QUOTE_WHITESPACE:[41, 43, ">>"]
````````````````````````````````


Nested, Lazy continuation less, with prefix

```````````````````````````````` example BlockQuote: 5
>> block quote
> with lazy continuation
>> 
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 43, ">> bl … ock quote\n> with lazy continuation\n>> "]
   MdBlockQuoteImpl:[0, 43, ">> bl … ock quote\n> with lazy continuation\n>> "]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 43, "> blo … ck quote\n> with lazy continuation\n>> "]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 40, "block …  quote\n> with lazy continuation\n"]
         MdTextBlockImpl:[3, 40, "block …  quote\n> with lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           BLOCK_QUOTE_WHITESPACE:[15, 17, "> "]
           TEXT:[17, 39, "with  … lazy continuation"]
           EOL:[39, 40, "\n"]
       BLOCK_QUOTE_WHITESPACE:[40, 43, ">> "]
````````````````````````````````


Nested, Lazy continuation more, with prefix

```````````````````````````````` example BlockQuote: 6
>> block quote
>> with lazy continuation
>> 
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 44, ">> bl … ock quote\n>> with lazy continuation\n>> "]
   MdBlockQuoteImpl:[0, 44, ">> bl … ock quote\n>> with lazy continuation\n>> "]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 44, "> blo … ck quote\n>> with lazy continuation\n>> "]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 41, "block …  quote\n>> with lazy continuation\n"]
         MdTextBlockImpl:[3, 41, "block …  quote\n>> with lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           BLOCK_QUOTE_WHITESPACE:[15, 18, ">> "]
           TEXT:[18, 40, "with  … lazy continuation"]
           EOL:[40, 41, "\n"]
       BLOCK_QUOTE_WHITESPACE:[41, 44, ">> "]
````````````````````````````````


Nested, Lazy continuation less, with prefix

```````````````````````````````` example BlockQuote: 7
>> block quote
with lazy continuation
>>
>> more text
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
    <p>more text</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 53, ">> bl … ock quote\nwith lazy continuation\n>>\n>> more text"]
   MdBlockQuoteImpl:[0, 53, ">> bl … ock quote\nwith lazy continuation\n>>\n>> more text"]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 53, "> blo … ck quote\nwith lazy continuation\n>>\n>> more text"]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 38, "block …  quote\nwith lazy continuation\n"]
         MdTextBlockImpl:[3, 38, "block …  quote\nwith lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           TEXT:[15, 37, "with  … lazy continuation"]
           EOL:[37, 38, "\n"]
       BLOCK_QUOTE_WHITESPACE:[38, 40, ">>"]
       MdBlankLineImpl:[40, 41, "\n"]
         BLANK_LINE:[40, 41, "\n"]
       BLOCK_QUOTE_WHITESPACE:[41, 44, ">> "]
       MdParagraphImpl:[44, 53, "more text"]
         MdTextBlockImpl:[44, 53, "more text"]
           TEXT:[44, 53, "more text"]
````````````````````````````````


Nested, Lazy continuation, with prefix

```````````````````````````````` example BlockQuote: 8
>> block quote
>> with lazy continuation
>>
>> more text
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
    <p>more text</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 56, ">> bl … ock quote\n>> with lazy continuation\n>>\n>> more text"]
   MdBlockQuoteImpl:[0, 56, ">> bl … ock quote\n>> with lazy continuation\n>>\n>> more text"]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 56, "> blo … ck quote\n>> with lazy continuation\n>>\n>> more text"]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 41, "block …  quote\n>> with lazy continuation\n"]
         MdTextBlockImpl:[3, 41, "block …  quote\n>> with lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           BLOCK_QUOTE_WHITESPACE:[15, 18, ">> "]
           TEXT:[18, 40, "with  … lazy continuation"]
           EOL:[40, 41, "\n"]
       BLOCK_QUOTE_WHITESPACE:[41, 43, ">>"]
       MdBlankLineImpl:[43, 44, "\n"]
         BLANK_LINE:[43, 44, "\n"]
       BLOCK_QUOTE_WHITESPACE:[44, 47, ">> "]
       MdParagraphImpl:[47, 56, "more text"]
         MdTextBlockImpl:[47, 56, "more text"]
           TEXT:[47, 56, "more text"]
````````````````````````````````


Nested, Lazy continuation less, with prefix

```````````````````````````````` example BlockQuote: 9
>> block quote
> with lazy continuation
>>
>> more text
.
<blockquote>
  <blockquote>
    <p>block quote
    with lazy continuation</p>
    <p>more text</p>
  </blockquote>
</blockquote>
.
 MdFile:[0, 55, ">> bl … ock quote\n> with lazy continuation\n>>\n>> more text"]
   MdBlockQuoteImpl:[0, 55, ">> bl … ock quote\n> with lazy continuation\n>>\n>> more text"]
     BLOCK_QUOTE_MARKER:[0, 1, ">"]
     MdBlockQuoteImpl:[1, 55, "> blo … ck quote\n> with lazy continuation\n>>\n>> more text"]
       BLOCK_QUOTE_MARKER:[1, 3, "> "]
       MdParagraphImpl:[3, 40, "block …  quote\n> with lazy continuation\n"]
         MdTextBlockImpl:[3, 40, "block …  quote\n> with lazy continuation\n"]
           TEXT:[3, 14, "block …  quote"]
           EOL:[14, 15, "\n"]
           BLOCK_QUOTE_WHITESPACE:[15, 17, "> "]
           TEXT:[17, 39, "with  … lazy continuation"]
           EOL:[39, 40, "\n"]
       BLOCK_QUOTE_WHITESPACE:[40, 42, ">>"]
       MdBlankLineImpl:[42, 43, "\n"]
         BLANK_LINE:[42, 43, "\n"]
       BLOCK_QUOTE_WHITESPACE:[43, 46, ">> "]
       MdParagraphImpl:[46, 55, "more text"]
         MdTextBlockImpl:[46, 55, "more text"]
           TEXT:[46, 55, "more text"]
````````````````````````````````


### BulletList

`BulletList` `BulletListItem` `TaskListItem` `TaskListItemMarker`

empty

```````````````````````````````` example BulletList: 1
+ 

.
<ul>
  <li></li>
</ul>
.
 MdFile:[0, 4, "+ \n\n"]
   MdUnorderedListImpl:[0, 3, "+ \n"]
     MdUnorderedListItemImpl:[0, 3, "+ \n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "+ "]
       EOL:[2, 3, "\n"]
   MdBlankLineImpl:[3, 4, "\n"]
     BLANK_LINE:[3, 4, "\n"]
````````````````````````````````


empty

```````````````````````````````` example BulletList: 2
- [ ] 

.
<ul>
  <li class="task-list-item" task-offset="3"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;</li>
</ul>
.
 MdFile:[0, 8, "- [ ] \n\n"]
   MdUnorderedListImpl:[0, 5, "- [ ]"]
     MdUnorderedListItemImpl:[0, 5, "- [ ]"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       TASK_ITEM_MARKER:[2, 5, "[ ]"]
   EOL:[5, 7, " \n"]
   MdBlankLineImpl:[7, 8, "\n"]
     BLANK_LINE:[7, 8, "\n"]
````````````````````````````````


nested

```````````````````````````````` example BulletList: 3
- item 1
* item 2
    - item 2.1
+ item 3
.
<ul>
  <li>item 1</li>
  <li>item 2
    <ul>
      <li>item 2.1</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
 MdFile:[0, 41, "- ite … m 1\n* item 2\n    - item 2.1\n+ item 3"]
   MdUnorderedListImpl:[0, 41, "- ite … m 1\n* item 2\n    - item 2.1\n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 9, "item 1\n"]
         TEXT:[2, 8, "item 1"]
         EOL:[8, 9, "\n"]
     MdUnorderedListItemImpl:[9, 33, "* ite … m 2\n    - item 2.1\n"]
       BULLET_LIST_ITEM_MARKER:[9, 11, "* "]
       MdTextBlockImpl:[11, 18, "item 2\n"]
         TEXT:[11, 17, "item 2"]
         EOL:[17, 18, "\n"]
       WHITESPACE:[18, 22, "    "]
       MdUnorderedListImpl:[22, 33, "- ite … m 2.1\n"]
         MdUnorderedListItemImpl:[22, 33, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[22, 24, "- "]
           MdTextBlockImpl:[24, 33, "item 2.1\n"]
             TEXT:[24, 32, "item 2.1"]
             EOL:[32, 33, "\n"]
     MdUnorderedListItemImpl:[33, 41, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[33, 35, "+ "]
       MdTextBlockImpl:[35, 41, "item 3"]
         TEXT:[35, 41, "item 3"]
````````````````````````````````


nested some loose

```````````````````````````````` example BulletList: 4
- item 1

* item 2
    - item 2.1
+ item 3
.
<ul>
  <li>
    <p>item 1</p>
  </li>
  <li>item 2
    <ul>
      <li>item 2.1</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
 MdFile:[0, 42, "- ite … m 1\n\n* item 2\n    - item 2.1\n+ item 3"]
   MdUnorderedListImpl:[0, 42, "- ite … m 1\n\n* item 2\n    - item 2.1\n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdParagraphImpl:[2, 9, "item 1\n"]
         MdTextBlockImpl:[2, 9, "item 1\n"]
           TEXT:[2, 8, "item 1"]
           EOL:[8, 9, "\n"]
     MdBlankLineImpl:[9, 10, "\n"]
       BLANK_LINE:[9, 10, "\n"]
     MdUnorderedListItemImpl:[10, 34, "* ite … m 2\n    - item 2.1\n"]
       BULLET_LIST_ITEM_MARKER:[10, 12, "* "]
       MdTextBlockImpl:[12, 19, "item 2\n"]
         TEXT:[12, 18, "item 2"]
         EOL:[18, 19, "\n"]
       WHITESPACE:[19, 23, "    "]
       MdUnorderedListImpl:[23, 34, "- ite … m 2.1\n"]
         MdUnorderedListItemImpl:[23, 34, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[23, 25, "- "]
           MdTextBlockImpl:[25, 34, "item 2.1\n"]
             TEXT:[25, 33, "item 2.1"]
             EOL:[33, 34, "\n"]
     MdUnorderedListItemImpl:[34, 42, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[34, 36, "+ "]
       MdTextBlockImpl:[36, 42, "item 3"]
         TEXT:[36, 42, "item 3"]
````````````````````````````````


nested loose

```````````````````````````````` example BulletList: 5
- item 1

* item 2
    - item 2.1
    
+ item 3
.
<ul>
  <li>
    <p>item 1</p>
  </li>
  <li>
    <p>item 2</p>
    <ul>
      <li>item 2.1</li>
    </ul>
  </li>
  <li>
    <p>item 3</p>
  </li>
</ul>
.
 MdFile:[0, 47, "- ite … m 1\n\n* item 2\n    - item 2.1\n    \n+ item 3"]
   MdUnorderedListImpl:[0, 47, "- ite … m 1\n\n* item 2\n    - item 2.1\n    \n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdParagraphImpl:[2, 9, "item 1\n"]
         MdTextBlockImpl:[2, 9, "item 1\n"]
           TEXT:[2, 8, "item 1"]
           EOL:[8, 9, "\n"]
     MdBlankLineImpl:[9, 10, "\n"]
       BLANK_LINE:[9, 10, "\n"]
     MdUnorderedListItemImpl:[10, 34, "* ite … m 2\n    - item 2.1\n"]
       BULLET_LIST_ITEM_MARKER:[10, 12, "* "]
       MdParagraphImpl:[12, 19, "item 2\n"]
         MdTextBlockImpl:[12, 19, "item 2\n"]
           TEXT:[12, 18, "item 2"]
           EOL:[18, 19, "\n"]
       WHITESPACE:[19, 23, "    "]
       MdUnorderedListImpl:[23, 34, "- ite … m 2.1\n"]
         MdUnorderedListItemImpl:[23, 34, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[23, 25, "- "]
           MdTextBlockImpl:[25, 34, "item 2.1\n"]
             TEXT:[25, 33, "item 2.1"]
             EOL:[33, 34, "\n"]
     MdBlankLineImpl:[34, 39, "    \n"]
       BLANK_LINE:[34, 39, "    \n"]
     MdUnorderedListItemImpl:[39, 47, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[39, 41, "+ "]
       MdParagraphImpl:[41, 47, "item 3"]
         MdTextBlockImpl:[41, 47, "item 3"]
           TEXT:[41, 47, "item 3"]
````````````````````````````````


tight nested loose

```````````````````````````````` example BulletList: 6
- item 1
* item 2

    - item 2.1
    
    - item 2.1
+ item 3
.
<ul>
  <li>item 1</li>
  <li>item 2
    <ul>
      <li>
        <p>item 2.1</p>
      </li>
      <li>
        <p>item 2.1</p>
      </li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
 MdFile:[0, 62, "- ite … m 1\n* item 2\n\n    - item 2.1\n    \n    - item 2.1\n+ item 3"]
   MdUnorderedListImpl:[0, 62, "- ite … m 1\n* item 2\n\n    - item 2.1\n    \n    - item 2.1\n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 9, "item 1\n"]
         TEXT:[2, 8, "item 1"]
         EOL:[8, 9, "\n"]
     MdUnorderedListItemImpl:[9, 54, "* ite … m 2\n\n    - item 2.1\n    \n    - item 2.1\n"]
       BULLET_LIST_ITEM_MARKER:[9, 11, "* "]
       MdTextBlockImpl:[11, 18, "item 2\n"]
         TEXT:[11, 17, "item 2"]
         EOL:[17, 18, "\n"]
       MdBlankLineImpl:[18, 19, "\n"]
         BLANK_LINE:[18, 19, "\n"]
       WHITESPACE:[19, 23, "    "]
       MdUnorderedListImpl:[23, 54, "- ite … m 2.1\n    \n    - item 2.1\n"]
         MdUnorderedListItemImpl:[23, 34, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[23, 25, "- "]
           MdParagraphImpl:[25, 34, "item 2.1\n"]
             MdTextBlockImpl:[25, 34, "item 2.1\n"]
               TEXT:[25, 33, "item 2.1"]
               EOL:[33, 34, "\n"]
         MdBlankLineImpl:[34, 39, "    \n"]
           BLANK_LINE:[34, 39, "    \n"]
         WHITESPACE:[39, 43, "    "]
         MdUnorderedListItemImpl:[43, 54, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[43, 45, "- "]
           MdParagraphImpl:[45, 54, "item 2.1\n"]
             MdTextBlockImpl:[45, 54, "item 2.1\n"]
               TEXT:[45, 53, "item 2.1"]
               EOL:[53, 54, "\n"]
     MdUnorderedListItemImpl:[54, 62, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[54, 56, "+ "]
       MdTextBlockImpl:[56, 62, "item 3"]
         TEXT:[56, 62, "item 3"]
````````````````````````````````


nested task item

```````````````````````````````` example BulletList: 7
- item 1
* item 2
    - [ ] item 2.1
    - item 2.2
+ item 3
.
<ul>
  <li>item 1</li>
  <li>item 2
    <ul>
      <li class="task-list-item" task-offset="25"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;item 2.1</li>
      <li>item 2.2</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
 MdFile:[0, 60, "- ite … m 1\n* item 2\n    - [ ] item 2.1\n    - item 2.2\n+ item 3"]
   MdUnorderedListImpl:[0, 60, "- ite … m 1\n* item 2\n    - [ ] item 2.1\n    - item 2.2\n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 9, "item 1\n"]
         TEXT:[2, 8, "item 1"]
         EOL:[8, 9, "\n"]
     MdUnorderedListItemImpl:[9, 52, "* ite … m 2\n    - [ ] item 2.1\n    - item 2.2\n"]
       BULLET_LIST_ITEM_MARKER:[9, 11, "* "]
       MdTextBlockImpl:[11, 18, "item 2\n"]
         TEXT:[11, 17, "item 2"]
         EOL:[17, 18, "\n"]
       WHITESPACE:[18, 22, "    "]
       MdUnorderedListImpl:[22, 52, "- [ ] …  item 2.1\n    - item 2.2\n"]
         MdUnorderedListItemImpl:[22, 37, "- [ ] …  item 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[22, 24, "- "]
           TASK_ITEM_MARKER:[24, 28, "[ ] "]
           MdTextBlockImpl:[28, 37, "item 2.1\n"]
             TEXT:[28, 36, "item 2.1"]
             EOL:[36, 37, "\n"]
         WHITESPACE:[37, 41, "    "]
         MdUnorderedListItemImpl:[41, 52, "- ite … m 2.2\n"]
           BULLET_LIST_ITEM_MARKER:[41, 43, "- "]
           MdTextBlockImpl:[43, 52, "item 2.2\n"]
             TEXT:[43, 51, "item 2.2"]
             EOL:[51, 52, "\n"]
     MdUnorderedListItemImpl:[52, 60, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[52, 54, "+ "]
       MdTextBlockImpl:[54, 60, "item 3"]
         TEXT:[54, 60, "item 3"]
````````````````````````````````


task item, nested

```````````````````````````````` example BulletList: 8
- item 1
* item 2
* [ ] item 2
    - item 2.1
    - item 2.2
+ item 3
.
<ul>
  <li>item 1</li>
  <li>item 2</li>
  <li class="task-list-item" task-offset="21"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;item 2
    <ul>
      <li>item 2.1</li>
      <li>item 2.2</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
 MdFile:[0, 69, "- ite … m 1\n* item 2\n* [ ] item 2\n    - item 2.1\n    - item 2.2\n+ item 3"]
   MdUnorderedListImpl:[0, 69, "- ite … m 1\n* item 2\n* [ ] item 2\n    - item 2.1\n    - item 2.2\n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 9, "item 1\n"]
         TEXT:[2, 8, "item 1"]
         EOL:[8, 9, "\n"]
     MdUnorderedListItemImpl:[9, 18, "* item 2\n"]
       BULLET_LIST_ITEM_MARKER:[9, 11, "* "]
       MdTextBlockImpl:[11, 18, "item 2\n"]
         TEXT:[11, 17, "item 2"]
         EOL:[17, 18, "\n"]
     MdUnorderedListItemImpl:[18, 61, "* [ ] …  item 2\n    - item 2.1\n    - item 2.2\n"]
       BULLET_LIST_ITEM_MARKER:[18, 20, "* "]
       TASK_ITEM_MARKER:[20, 24, "[ ] "]
       MdTextBlockImpl:[24, 31, "item 2\n"]
         TEXT:[24, 30, "item 2"]
         EOL:[30, 31, "\n"]
       WHITESPACE:[31, 35, "    "]
       MdUnorderedListImpl:[35, 61, "- ite … m 2.1\n    - item 2.2\n"]
         MdUnorderedListItemImpl:[35, 46, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[35, 37, "- "]
           MdTextBlockImpl:[37, 46, "item 2.1\n"]
             TEXT:[37, 45, "item 2.1"]
             EOL:[45, 46, "\n"]
         WHITESPACE:[46, 50, "    "]
         MdUnorderedListItemImpl:[50, 61, "- ite … m 2.2\n"]
           BULLET_LIST_ITEM_MARKER:[50, 52, "- "]
           MdTextBlockImpl:[52, 61, "item 2.2\n"]
             TEXT:[52, 60, "item 2.2"]
             EOL:[60, 61, "\n"]
     MdUnorderedListItemImpl:[61, 69, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[61, 63, "+ "]
       MdTextBlockImpl:[63, 69, "item 3"]
         TEXT:[63, 69, "item 3"]
````````````````````````````````


task item, nested task item

```````````````````````````````` example BulletList: 9
- item 1
* item 2
* [ ] item 2
    - item 2.1
    - [ ] item 2.2
+ item 3
.
<ul>
  <li>item 1</li>
  <li>item 2</li>
  <li class="task-list-item" task-offset="21"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;item 2
    <ul>
      <li>item 2.1</li>
      <li class="task-list-item" task-offset="53"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;item 2.2</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
 MdFile:[0, 73, "- ite … m 1\n* item 2\n* [ ] item 2\n    - item 2.1\n    - [ ] item 2.2\n+ item 3"]
   MdUnorderedListImpl:[0, 73, "- ite … m 1\n* item 2\n* [ ] item 2\n    - item 2.1\n    - [ ] item 2.2\n+ item 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 9, "item 1\n"]
         TEXT:[2, 8, "item 1"]
         EOL:[8, 9, "\n"]
     MdUnorderedListItemImpl:[9, 18, "* item 2\n"]
       BULLET_LIST_ITEM_MARKER:[9, 11, "* "]
       MdTextBlockImpl:[11, 18, "item 2\n"]
         TEXT:[11, 17, "item 2"]
         EOL:[17, 18, "\n"]
     MdUnorderedListItemImpl:[18, 65, "* [ ] …  item 2\n    - item 2.1\n    - [ ] item 2.2\n"]
       BULLET_LIST_ITEM_MARKER:[18, 20, "* "]
       TASK_ITEM_MARKER:[20, 24, "[ ] "]
       MdTextBlockImpl:[24, 31, "item 2\n"]
         TEXT:[24, 30, "item 2"]
         EOL:[30, 31, "\n"]
       WHITESPACE:[31, 35, "    "]
       MdUnorderedListImpl:[35, 65, "- ite … m 2.1\n    - [ ] item 2.2\n"]
         MdUnorderedListItemImpl:[35, 46, "- ite … m 2.1\n"]
           BULLET_LIST_ITEM_MARKER:[35, 37, "- "]
           MdTextBlockImpl:[37, 46, "item 2.1\n"]
             TEXT:[37, 45, "item 2.1"]
             EOL:[45, 46, "\n"]
         WHITESPACE:[46, 50, "    "]
         MdUnorderedListItemImpl:[50, 65, "- [ ] …  item 2.2\n"]
           BULLET_LIST_ITEM_MARKER:[50, 52, "- "]
           TASK_ITEM_MARKER:[52, 56, "[ ] "]
           MdTextBlockImpl:[56, 65, "item 2.2\n"]
             TEXT:[56, 64, "item 2.2"]
             EOL:[64, 65, "\n"]
     MdUnorderedListItemImpl:[65, 73, "+ item 3"]
       BULLET_LIST_ITEM_MARKER:[65, 67, "+ "]
       MdTextBlockImpl:[67, 73, "item 3"]
         TEXT:[67, 73, "item 3"]
````````````````````````````````


A bullet list after an ordered list

```````````````````````````````` example BulletList: 10
2. item 1
1. item 2
5. [ ] tem 3

- item 1
- item 2
- [ ] item 3
.
<ol>
  <li>item 1</li>
  <li>item 2</li>
  <li class="task-list-item" task-offset="24">
    <p><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;tem 3</p>
  </li>
  <li>item 1</li>
  <li>item 2</li>
  <li class="task-list-item" task-offset="55"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;item 3</li>
</ol>
.
 MdFile:[0, 64, "2. it … em 1\n1. item 2\n5. [ ] tem 3\n\n- item 1\n- item 2\n- [ ] item 3"]
   MdOrderedListImpl:[0, 64, "2. it … em 1\n1. item 2\n5. [ ] tem 3\n\n- item 1\n- item 2\n- [ ] item 3"]
     MdOrderedListItemImpl:[0, 10, "2. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "2. "]
       MdTextBlockImpl:[3, 10, "item 1\n"]
         TEXT:[3, 9, "item 1"]
         EOL:[9, 10, "\n"]
     MdOrderedListItemImpl:[10, 20, "1. item 2\n"]
       ORDERED_LIST_ITEM_MARKER:[10, 13, "1. "]
       MdTextBlockImpl:[13, 20, "item 2\n"]
         TEXT:[13, 19, "item 2"]
         EOL:[19, 20, "\n"]
     MdOrderedListItemImpl:[20, 33, "5. [  … ] tem 3\n"]
       ORDERED_LIST_ITEM_MARKER:[20, 23, "5. "]
       TASK_ITEM_MARKER:[23, 27, "[ ] "]
       MdParagraphImpl:[27, 33, "tem 3\n"]
         MdTextBlockImpl:[27, 33, "tem 3\n"]
           TEXT:[27, 32, "tem 3"]
           EOL:[32, 33, "\n"]
     MdBlankLineImpl:[33, 34, "\n"]
       BLANK_LINE:[33, 34, "\n"]
     MdUnorderedListItemImpl:[34, 43, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[34, 36, "- "]
       MdTextBlockImpl:[36, 43, "item 1\n"]
         TEXT:[36, 42, "item 1"]
         EOL:[42, 43, "\n"]
     MdUnorderedListItemImpl:[43, 52, "- item 2\n"]
       BULLET_LIST_ITEM_MARKER:[43, 45, "- "]
       MdTextBlockImpl:[45, 52, "item 2\n"]
         TEXT:[45, 51, "item 2"]
         EOL:[51, 52, "\n"]
     MdUnorderedListItemImpl:[52, 64, "- [ ] …  item 3"]
       BULLET_LIST_ITEM_MARKER:[52, 54, "- "]
       TASK_ITEM_MARKER:[54, 58, "[ ] "]
       MdTextBlockImpl:[58, 64, "item 3"]
         TEXT:[58, 64, "item 3"]
````````````````````````````````


Bullet items must have a blank line before them when preceded by paragraph but should not append
following child paragraph

```````````````````````````````` example BulletList: 11
- item 1 paragraph
    * sublist
- item 2 paragraph

    paragraph
.
<ul>
  <li>item 1 paragraph
    <ul>
      <li>sublist</li>
    </ul>
  </li>
  <li>item 2 paragraph
    <p>paragraph</p>
  </li>
</ul>
.
 MdFile:[0, 66, "- ite … m 1 paragraph\n    * sublist\n- item 2 paragraph\n\n    paragraph"]
   MdUnorderedListImpl:[0, 66, "- ite … m 1 paragraph\n    * sublist\n- item 2 paragraph\n\n    paragraph"]
     MdUnorderedListItemImpl:[0, 33, "- ite … m 1 paragraph\n    * sublist\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 19, "item  … 1 paragraph\n"]
         TEXT:[2, 18, "item  … 1 paragraph"]
         EOL:[18, 19, "\n"]
       WHITESPACE:[19, 23, "    "]
       MdUnorderedListImpl:[23, 33, "* sublist\n"]
         MdUnorderedListItemImpl:[23, 33, "* sublist\n"]
           BULLET_LIST_ITEM_MARKER:[23, 25, "* "]
           MdTextBlockImpl:[25, 33, "sublist\n"]
             TEXT:[25, 32, "sublist"]
             EOL:[32, 33, "\n"]
     MdUnorderedListItemImpl:[33, 66, "- ite … m 2 paragraph\n\n    paragraph"]
       BULLET_LIST_ITEM_MARKER:[33, 35, "- "]
       MdTextBlockImpl:[35, 52, "item  … 2 paragraph\n"]
         TEXT:[35, 51, "item  … 2 paragraph"]
         EOL:[51, 52, "\n"]
       MdBlankLineImpl:[52, 53, "\n"]
         BLANK_LINE:[52, 53, "\n"]
       WHITESPACE:[53, 57, "    "]
       MdParagraphImpl:[57, 66, "paragraph"]
         MdTextBlockImpl:[57, 66, "paragraph"]
           TEXT:[57, 66, "paragraph"]
````````````````````````````````


Bullet items can have headings as children

```````````````````````````````` example BulletList: 12
- Some Lists
    
    # Test
.
<ul>
  <li>Some Lists
    <h1><a href="#test" id="test"></a>Test</h1>
  </li>
</ul>
.
 MdFile:[0, 28, "- Som … e Lists\n    \n    # Test"]
   MdUnorderedListImpl:[0, 28, "- Som … e Lists\n    \n    # Test"]
     MdUnorderedListItemImpl:[0, 28, "- Som … e Lists\n    \n    # Test"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 13, "Some  … Lists\n"]
         TEXT:[2, 12, "Some Lists"]
         EOL:[12, 13, "\n"]
       MdBlankLineImpl:[13, 18, "    \n"]
         BLANK_LINE:[13, 18, "    \n"]
       WHITESPACE:[18, 22, "    "]
       MdAtxHeaderImpl:[22, 28, "# Test"]
         HEADER_ATX_MARKER:[22, 23, "#"]
         WHITESPACE:[23, 24, " "]
         MdHeaderTextImpl:[24, 28, "Test"]
           HEADER_TEXT:[24, 28, "Test"]
````````````````````````````````


### Code

`Code`

Plain text with unterminated or empty code

```````````````````````````````` example Code: 1
First line
Second line ``
Last line
.
<p>First line
Second line ``
Last line</p>
.
 MdFile:[0, 35, "First …  line\nSecond line ``\nLast line"]
   MdParagraphImpl:[0, 35, "First …  line\nSecond line ``\nLast line"]
     MdTextBlockImpl:[0, 35, "First …  line\nSecond line ``\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 25, "Secon … d line ``"]
       EOL:[25, 26, "\n"]
       TEXT:[26, 35, "Last line"]
````````````````````````````````


Plain text with simple code

```````````````````````````````` example Code: 2
First line
Second line `code`
Last line
.
<p>First line
Second line <code>code</code>
Last line</p>
.
 MdFile:[0, 39, "First …  line\nSecond line `code`\nLast line"]
   MdParagraphImpl:[0, 39, "First …  line\nSecond line `code`\nLast line"]
     MdTextBlockImpl:[0, 39, "First …  line\nSecond line `code`\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 23, "Secon … d line "]
       MdInlineCodeImpl:[23, 29, "`code`"]
         CODE_MARKER:[23, 24, "`"]
         CODE_TEXT:[24, 28, "code"]
         CODE_MARKER:[28, 29, "`"]
       EOL:[29, 30, "\n"]
       TEXT:[30, 39, "Last line"]
````````````````````````````````


Plain text with code with embedded looking HTML comment

```````````````````````````````` example Code: 3
First line
Second line `<!--code-->`
Last line
.
<p>First line
Second line <code>&lt;!--code--&gt;</code>
Last line</p>
.
 MdFile:[0, 46, "First …  line\nSecond line `<!--code-->`\nLast line"]
   MdParagraphImpl:[0, 46, "First …  line\nSecond line `<!--code-->`\nLast line"]
     MdTextBlockImpl:[0, 46, "First …  line\nSecond line `<!--code-->`\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 23, "Secon … d line "]
       MdInlineCodeImpl:[23, 36, "`<!-- … code-->`"]
         CODE_MARKER:[23, 24, "`"]
         CODE_TEXT:[24, 35, "<!--c … ode-->"]
         CODE_MARKER:[35, 36, "`"]
       EOL:[36, 37, "\n"]
       TEXT:[37, 46, "Last line"]
````````````````````````````````


### Emoji

`Emoji`

valid emoji

```````````````````````````````` example Emoji: 1
:warning:
text :warning: embedded 
:warning: embedded 
text :warning: 
.
<p><img src="file:///Users/vlad/src/projects/idea-multimarkdown/build/idea-sandbox/plugins-test/idea-multimarkdown/emojis/warning.png" alt="emoji places:warning" class="emoji" />
text <img src="file:///Users/vlad/src/projects/idea-multimarkdown/build/idea-sandbox/plugins-test/idea-multimarkdown/emojis/warning.png" alt="emoji places:warning" class="emoji" /> embedded
<img src="file:///Users/vlad/src/projects/idea-multimarkdown/build/idea-sandbox/plugins-test/idea-multimarkdown/emojis/warning.png" alt="emoji places:warning" class="emoji" /> embedded
text <img src="file:///Users/vlad/src/projects/idea-multimarkdown/build/idea-sandbox/plugins-test/idea-multimarkdown/emojis/warning.png" alt="emoji places:warning" class="emoji" /></p>
.
 MdFile:[0, 70, ":warn … ing:\ntext :warning: embedded \n:warning: embedded \ntext :warning: "]
   MdParagraphImpl:[0, 70, ":warn … ing:\ntext :warning: embedded \n:warning: embedded \ntext :warning: "]
     MdTextBlockImpl:[0, 70, ":warn … ing:\ntext :warning: embedded \n:warning: embedded \ntext :warning: "]
       MdEmojiImpl:[0, 9, ":warning:"]
         EMOJI_MARKER:[0, 1, ":"]
         MdEmojiIdImpl:[1, 8, "warning"]
           EMOJI_ID:[1, 8, "warning"]
         EMOJI_MARKER:[8, 9, ":"]
       EOL:[9, 10, "\n"]
       TEXT:[10, 15, "text "]
       MdEmojiImpl:[15, 24, ":warning:"]
         EMOJI_MARKER:[15, 16, ":"]
         MdEmojiIdImpl:[16, 23, "warning"]
           EMOJI_ID:[16, 23, "warning"]
         EMOJI_MARKER:[23, 24, ":"]
       TEXT:[24, 33, " embedded"]
       EOL:[33, 35, " \n"]
       MdEmojiImpl:[35, 44, ":warning:"]
         EMOJI_MARKER:[35, 36, ":"]
         MdEmojiIdImpl:[36, 43, "warning"]
           EMOJI_ID:[36, 43, "warning"]
         EMOJI_MARKER:[43, 44, ":"]
       TEXT:[44, 53, " embedded"]
       EOL:[53, 55, " \n"]
       TEXT:[55, 60, "text "]
       MdEmojiImpl:[60, 69, ":warning:"]
         EMOJI_MARKER:[60, 61, ":"]
         MdEmojiIdImpl:[61, 68, "warning"]
           EMOJI_ID:[61, 68, "warning"]
         EMOJI_MARKER:[68, 69, ":"]
       WHITESPACE:[69, 70, " "]
````````````````````````````````


invalid emoji

```````````````````````````````` example Emoji: 2
:invalid:
text :invalid: embedded 
:invalid: embedded 
text :invalid: 
.
<p>:invalid:
text :invalid: embedded
:invalid: embedded
text :invalid:</p>
.
 MdFile:[0, 70, ":inva … lid:\ntext :invalid: embedded \n:invalid: embedded \ntext :invalid: "]
   MdParagraphImpl:[0, 70, ":inva … lid:\ntext :invalid: embedded \n:invalid: embedded \ntext :invalid: "]
     MdTextBlockImpl:[0, 70, ":inva … lid:\ntext :invalid: embedded \n:invalid: embedded \ntext :invalid: "]
       MdEmojiImpl:[0, 9, ":invalid:"]
         EMOJI_MARKER:[0, 1, ":"]
         MdEmojiIdImpl:[1, 8, "invalid"]
           EMOJI_ID:[1, 8, "invalid"]
         EMOJI_MARKER:[8, 9, ":"]
       EOL:[9, 10, "\n"]
       TEXT:[10, 15, "text "]
       MdEmojiImpl:[15, 24, ":invalid:"]
         EMOJI_MARKER:[15, 16, ":"]
         MdEmojiIdImpl:[16, 23, "invalid"]
           EMOJI_ID:[16, 23, "invalid"]
         EMOJI_MARKER:[23, 24, ":"]
       TEXT:[24, 33, " embedded"]
       EOL:[33, 35, " \n"]
       MdEmojiImpl:[35, 44, ":invalid:"]
         EMOJI_MARKER:[35, 36, ":"]
         MdEmojiIdImpl:[36, 43, "invalid"]
           EMOJI_ID:[36, 43, "invalid"]
         EMOJI_MARKER:[43, 44, ":"]
       TEXT:[44, 53, " embedded"]
       EOL:[53, 55, " \n"]
       TEXT:[55, 60, "text "]
       MdEmojiImpl:[60, 69, ":invalid:"]
         EMOJI_MARKER:[60, 61, ":"]
         MdEmojiIdImpl:[61, 68, "invalid"]
           EMOJI_ID:[61, 68, "invalid"]
         EMOJI_MARKER:[68, 69, ":"]
       WHITESPACE:[69, 70, " "]
````````````````````````````````


### Emphasis

`Emphasis`

underscore

```````````````````````````````` example Emphasis: 1
_italic_
text _italic_ embedded 
_italic_ embedded 
text _italic_ 
.
<p><em>italic</em>
text <em>italic</em> embedded
<em>italic</em> embedded
text <em>italic</em></p>
.
 MdFile:[0, 66, "_ital … ic_\ntext _italic_ embedded \n_italic_ embedded \ntext _italic_ "]
   MdParagraphImpl:[0, 66, "_ital … ic_\ntext _italic_ embedded \n_italic_ embedded \ntext _italic_ "]
     MdTextBlockImpl:[0, 66, "_ital … ic_\ntext _italic_ embedded \n_italic_ embedded \ntext _italic_ "]
       MdInlineItalicImpl:[0, 8, "_italic_"]
         ITALIC_MARKER:[0, 1, "_"]
         ITALIC_TEXT:[1, 7, "italic"]
         ITALIC_MARKER:[7, 8, "_"]
       EOL:[8, 9, "\n"]
       TEXT:[9, 14, "text "]
       MdInlineItalicImpl:[14, 22, "_italic_"]
         ITALIC_MARKER:[14, 15, "_"]
         ITALIC_TEXT:[15, 21, "italic"]
         ITALIC_MARKER:[21, 22, "_"]
       TEXT:[22, 31, " embedded"]
       EOL:[31, 33, " \n"]
       MdInlineItalicImpl:[33, 41, "_italic_"]
         ITALIC_MARKER:[33, 34, "_"]
         ITALIC_TEXT:[34, 40, "italic"]
         ITALIC_MARKER:[40, 41, "_"]
       TEXT:[41, 50, " embedded"]
       EOL:[50, 52, " \n"]
       TEXT:[52, 57, "text "]
       MdInlineItalicImpl:[57, 65, "_italic_"]
         ITALIC_MARKER:[57, 58, "_"]
         ITALIC_TEXT:[58, 64, "italic"]
         ITALIC_MARKER:[64, 65, "_"]
       WHITESPACE:[65, 66, " "]
````````````````````````````````


asterisk

```````````````````````````````` example Emphasis: 2
*italic*
text *italic* embedded 
*italic* embedded 
text *italic* 
.
<p><em>italic</em>
text <em>italic</em> embedded
<em>italic</em> embedded
text <em>italic</em></p>
.
 MdFile:[0, 66, "*ital … ic*\ntext *italic* embedded \n*italic* embedded \ntext *italic* "]
   MdParagraphImpl:[0, 66, "*ital … ic*\ntext *italic* embedded \n*italic* embedded \ntext *italic* "]
     MdTextBlockImpl:[0, 66, "*ital … ic*\ntext *italic* embedded \n*italic* embedded \ntext *italic* "]
       MdInlineItalicImpl:[0, 8, "*italic*"]
         ITALIC_MARKER:[0, 1, "*"]
         ITALIC_TEXT:[1, 7, "italic"]
         ITALIC_MARKER:[7, 8, "*"]
       EOL:[8, 9, "\n"]
       TEXT:[9, 14, "text "]
       MdInlineItalicImpl:[14, 22, "*italic*"]
         ITALIC_MARKER:[14, 15, "*"]
         ITALIC_TEXT:[15, 21, "italic"]
         ITALIC_MARKER:[21, 22, "*"]
       TEXT:[22, 31, " embedded"]
       EOL:[31, 33, " \n"]
       MdInlineItalicImpl:[33, 41, "*italic*"]
         ITALIC_MARKER:[33, 34, "*"]
         ITALIC_TEXT:[34, 40, "italic"]
         ITALIC_MARKER:[40, 41, "*"]
       TEXT:[41, 50, " embedded"]
       EOL:[50, 52, " \n"]
       TEXT:[52, 57, "text "]
       MdInlineItalicImpl:[57, 65, "*italic*"]
         ITALIC_MARKER:[57, 58, "*"]
         ITALIC_TEXT:[58, 64, "italic"]
         ITALIC_MARKER:[64, 65, "*"]
       WHITESPACE:[65, 66, " "]
````````````````````````````````


### EscapedCharacter

`EscapedCharacter`

```````````````````````````````` example EscapedCharacter: 1
\\  \* \~ \t \"
.
<p>\  * ~ \t &quot;</p>
.
 MdFile:[0, 15, "\\  \ … * \~ \t \\""]
   MdParagraphImpl:[0, 15, "\\  \ … * \~ \t \\""]
     MdTextBlockImpl:[0, 15, "\\  \ … * \~ \t \\""]
       MdInlineSpecialTextImpl:[0, 2, "\\"]
         SPECIAL_TEXT_MARKER:[0, 1, "\"]
         SPECIAL_TEXT:[1, 2, "\"]
       TEXT:[2, 4, "  "]
       MdInlineSpecialTextImpl:[4, 6, "\*"]
         SPECIAL_TEXT_MARKER:[4, 5, "\"]
         SPECIAL_TEXT:[5, 6, "*"]
       TEXT:[6, 7, " "]
       MdInlineSpecialTextImpl:[7, 9, "\~"]
         SPECIAL_TEXT_MARKER:[7, 8, "\"]
         SPECIAL_TEXT:[8, 9, "~"]
       TEXT:[9, 13, " \t "]
       MdInlineSpecialTextImpl:[13, 15, "\\""]
         SPECIAL_TEXT_MARKER:[13, 14, "\"]
         SPECIAL_TEXT:[14, 15, "\""]
````````````````````````````````


### FencedCodeBlock

`FencedCodeBlock`

empty, no info

```````````````````````````````` example FencedCodeBlock: 1
```

```
.
<pre><code>
</code></pre>
.
 MdFile:[0, 8, "```\n\n```"]
   MdVerbatimImpl:[0, 8, "```\n\n```"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 3]
     EOL:[3, 4, "\n"]
     MdVerbatimContentImpl:[4, 5, "\n"]
       VERBATIM_CONTENT:[4, 5, "\n"]
     VERBATIM_CLOSE:[5, 8, "```"]
````````````````````````````````


unterminated

```````````````````````````````` example FencedCodeBlock: 2
```
.
<pre><code></code></pre>
.
 MdFile:[0, 3, "```"]
   MdVerbatimImpl:[0, 3, "```"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 3]
````````````````````````````````


empty, no info, blank line follows

```````````````````````````````` example FencedCodeBlock: 3
```

```

.
<pre><code>
</code></pre>
.
 MdFile:[0, 10, "```\n\n```\n\n"]
   MdVerbatimImpl:[0, 9, "```\n\n```\n"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 3]
     EOL:[3, 4, "\n"]
     MdVerbatimContentImpl:[4, 5, "\n"]
       VERBATIM_CONTENT:[4, 5, "\n"]
     VERBATIM_CLOSE:[5, 8, "```"]
     EOL:[8, 9, "\n"]
   MdBlankLineImpl:[9, 10, "\n"]
     BLANK_LINE:[9, 10, "\n"]
````````````````````````````````


empty, info

```````````````````````````````` example FencedCodeBlock: 4
```info

```
.
<pre><code class="info">
</code></pre>
.
 MdFile:[0, 12, "```in … fo\n\n```"]
   MdVerbatimImpl:[0, 12, "```in … fo\n\n```"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 7, "info"]
       VERBATIM_LANG:[3, 7, "info"]
     EOL:[7, 8, "\n"]
     MdVerbatimContentImpl:[8, 9, "\n"]
       VERBATIM_CONTENT:[8, 9, "\n"]
     VERBATIM_CLOSE:[9, 12, "```"]
````````````````````````````````


empty, info, blank line follows

```````````````````````````````` example FencedCodeBlock: 5
```info

```

.
<pre><code class="info">
</code></pre>
.
 MdFile:[0, 14, "```in … fo\n\n```\n\n"]
   MdVerbatimImpl:[0, 13, "```in … fo\n\n```\n"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 7, "info"]
       VERBATIM_LANG:[3, 7, "info"]
     EOL:[7, 8, "\n"]
     MdVerbatimContentImpl:[8, 9, "\n"]
       VERBATIM_CONTENT:[8, 9, "\n"]
     VERBATIM_CLOSE:[9, 12, "```"]
     EOL:[12, 13, "\n"]
   MdBlankLineImpl:[13, 14, "\n"]
     BLANK_LINE:[13, 14, "\n"]
````````````````````````````````


non empty, no info, blank line follows

```````````````````````````````` example FencedCodeBlock: 6
```
some text
```

.
<pre><code>some text
</code></pre>
.
 MdFile:[0, 19, "```\ns … ome text\n```\n\n"]
   MdVerbatimImpl:[0, 18, "```\ns … ome text\n```\n"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 3]
     EOL:[3, 4, "\n"]
     MdVerbatimContentImpl:[4, 14, "some text\n"]
       VERBATIM_CONTENT:[4, 14, "some text\n"]
     VERBATIM_CLOSE:[14, 17, "```"]
     EOL:[17, 18, "\n"]
   MdBlankLineImpl:[18, 19, "\n"]
     BLANK_LINE:[18, 19, "\n"]
````````````````````````````````


non empty, info

```````````````````````````````` example FencedCodeBlock: 7
```info
some text
```
.
<pre><code class="info">some text
</code></pre>
.
 MdFile:[0, 21, "```in … fo\nsome text\n```"]
   MdVerbatimImpl:[0, 21, "```in … fo\nsome text\n```"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 7, "info"]
       VERBATIM_LANG:[3, 7, "info"]
     EOL:[7, 8, "\n"]
     MdVerbatimContentImpl:[8, 18, "some text\n"]
       VERBATIM_CONTENT:[8, 18, "some text\n"]
     VERBATIM_CLOSE:[18, 21, "```"]
````````````````````````````````


non empty, info, blank line follows

```````````````````````````````` example FencedCodeBlock: 8
```info
some text
```

.
<pre><code class="info">some text
</code></pre>
.
 MdFile:[0, 23, "```in … fo\nsome text\n```\n\n"]
   MdVerbatimImpl:[0, 22, "```in … fo\nsome text\n```\n"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 7, "info"]
       VERBATIM_LANG:[3, 7, "info"]
     EOL:[7, 8, "\n"]
     MdVerbatimContentImpl:[8, 18, "some text\n"]
       VERBATIM_CONTENT:[8, 18, "some text\n"]
     VERBATIM_CLOSE:[18, 21, "```"]
     EOL:[21, 22, "\n"]
   MdBlankLineImpl:[22, 23, "\n"]
     BLANK_LINE:[22, 23, "\n"]
````````````````````````````````


non empty, info, blank line follows, unmatched

```````````````````````````````` example FencedCodeBlock: 9
```info
some text
~~~

.
<pre><code class="info">some text
</code></pre>
.
 MdFile:[0, 23, "```in … fo\nsome text\n~~~\n\n"]
   MdVerbatimImpl:[0, 22, "```in … fo\nsome text\n~~~\n"]
     VERBATIM_OPEN:[0, 3, "```"]
     MdVerbatimLanguageImpl:[3, 7, "info"]
       VERBATIM_LANG:[3, 7, "info"]
     EOL:[7, 8, "\n"]
     MdVerbatimContentImpl:[8, 18, "some text\n"]
       VERBATIM_CONTENT:[8, 18, "some text\n"]
     VERBATIM_CLOSE:[18, 21, "~~~"]
     EOL:[21, 22, "\n"]
   MdBlankLineImpl:[22, 23, "\n"]
     BLANK_LINE:[22, 23, "\n"]
````````````````````````````````


### HardLineBreak

`HardLineBreak`

minimal

```````````````````````````````` example HardLineBreak: 1
text with hard line break  
more text
.
<p>text with hard line break<br />
more text</p>
.
 MdFile:[0, 37, "text  … with hard line break  \nmore text"]
   MdParagraphImpl:[0, 37, "text  … with hard line break  \nmore text"]
     MdTextBlockImpl:[0, 37, "text  … with hard line break  \nmore text"]
       TEXT:[0, 25, "text  … with hard line break"]
       LINE_BREAK_SPACES:[25, 27, "  "]
       EOL:[27, 28, "\n"]
       TEXT:[28, 37, "more text"]
````````````````````````````````


non minimal

```````````````````````````````` example HardLineBreak: 2
text with hard line break   
more text
.
<p>text with hard line break<br />
more text</p>
.
 MdFile:[0, 38, "text  … with hard line break   \nmore text"]
   MdParagraphImpl:[0, 38, "text  … with hard line break   \nmore text"]
     MdTextBlockImpl:[0, 38, "text  … with hard line break   \nmore text"]
       TEXT:[0, 25, "text  … with hard line break"]
       LINE_BREAK_SPACES:[25, 28, "   "]
       EOL:[28, 29, "\n"]
       TEXT:[29, 38, "more text"]
````````````````````````````````


### Heading

`Heading`

Setext 1

```````````````````````````````` example Heading: 1
Heading 1
===
.
<h1><a href="#heading-1" id="heading-1"></a>Heading 1</h1>
.
 MdFile:[0, 13, "Headi … ng 1\n==="]
   MdSetextHeaderImpl:[0, 13, "Headi … ng 1\n==="]
     MdHeaderTextImpl:[0, 9, "Heading 1"]
       HEADER_TEXT:[0, 9, "Heading 1"]
     EOL:[9, 10, "\n"]
     HEADER_SETEXT_MARKER:[10, 13, "==="]
````````````````````````````````


Setext 2

```````````````````````````````` example Heading: 2
Heading 2
---
.
<h2><a href="#heading-2" id="heading-2"></a>Heading 2</h2>
.
 MdFile:[0, 13, "Headi … ng 2\n---"]
   MdSetextHeaderImpl:[0, 13, "Headi … ng 2\n---"]
     MdHeaderTextImpl:[0, 9, "Heading 2"]
       HEADER_TEXT:[0, 9, "Heading 2"]
     EOL:[9, 10, "\n"]
     HEADER_SETEXT_MARKER:[10, 13, "---"]
````````````````````````````````


Setext 1 with inlines

```````````````````````````````` example Heading: 3
Heading 1 **bold** _italic_ `code` 
==================================
.
<h1><a href="#heading-1-bold-italic-code" id="heading-1-bold-italic-code"></a>Heading 1 <strong>bold</strong> <em>italic</em> <code>code</code></h1>
.
 MdFile:[0, 70, "Headi … ng 1 **bold** _italic_ `code` \n=================================="]
   MdSetextHeaderImpl:[0, 70, "Headi … ng 1 **bold** _italic_ `code` \n=================================="]
     MdHeaderTextImpl:[0, 34, "Headi … ng 1 **bold** _italic_ `code`"]
       HEADER_TEXT:[0, 10, "Heading 1 "]
       MdInlineBoldImpl:[10, 18, "**bold**"]
         HEADER_TEXT_BOLD_MARKER:[10, 12, "**"]
         HEADER_TEXT_BOLD_TEXT:[12, 16, "bold"]
         HEADER_TEXT_BOLD_MARKER:[16, 18, "**"]
       HEADER_TEXT:[18, 19, " "]
       MdInlineItalicImpl:[19, 27, "_italic_"]
         HEADER_TEXT_ITALIC_MARKER:[19, 20, "_"]
         HEADER_TEXT_ITALIC_TEXT:[20, 26, "italic"]
         HEADER_TEXT_ITALIC_MARKER:[26, 27, "_"]
       HEADER_TEXT:[27, 28, " "]
       MdInlineCodeImpl:[28, 34, "`code`"]
         HEADER_TEXT_CODE_MARKER:[28, 29, "`"]
         HEADER_TEXT_CODE_TEXT:[29, 33, "code"]
         HEADER_TEXT_CODE_MARKER:[33, 34, "`"]
     WHITESPACE:[34, 35, " "]
     EOL:[35, 36, "\n"]
     HEADER_SETEXT_MARKER:[36, 70, "===== … ============================="]
````````````````````````````````


Setext 2 with inliines

```````````````````````````````` example Heading: 4
Heading 2 **bold** _italic_ `code` 
----------------------------------
.
<h2><a href="#heading-2-bold-italic-code" id="heading-2-bold-italic-code"></a>Heading 2 <strong>bold</strong> <em>italic</em> <code>code</code></h2>
.
 MdFile:[0, 70, "Headi … ng 2 **bold** _italic_ `code` \n----------------------------------"]
   MdSetextHeaderImpl:[0, 70, "Headi … ng 2 **bold** _italic_ `code` \n----------------------------------"]
     MdHeaderTextImpl:[0, 34, "Headi … ng 2 **bold** _italic_ `code`"]
       HEADER_TEXT:[0, 10, "Heading 2 "]
       MdInlineBoldImpl:[10, 18, "**bold**"]
         HEADER_TEXT_BOLD_MARKER:[10, 12, "**"]
         HEADER_TEXT_BOLD_TEXT:[12, 16, "bold"]
         HEADER_TEXT_BOLD_MARKER:[16, 18, "**"]
       HEADER_TEXT:[18, 19, " "]
       MdInlineItalicImpl:[19, 27, "_italic_"]
         HEADER_TEXT_ITALIC_MARKER:[19, 20, "_"]
         HEADER_TEXT_ITALIC_TEXT:[20, 26, "italic"]
         HEADER_TEXT_ITALIC_MARKER:[26, 27, "_"]
       HEADER_TEXT:[27, 28, " "]
       MdInlineCodeImpl:[28, 34, "`code`"]
         HEADER_TEXT_CODE_MARKER:[28, 29, "`"]
         HEADER_TEXT_CODE_TEXT:[29, 33, "code"]
         HEADER_TEXT_CODE_MARKER:[33, 34, "`"]
     WHITESPACE:[34, 35, " "]
     EOL:[35, 36, "\n"]
     HEADER_SETEXT_MARKER:[36, 70, "----- … -----------------------------"]
````````````````````````````````


Atx 1

```````````````````````````````` example Heading: 5
# Heading 1
# Heading 1 Tail #
.
<h1><a href="#heading-1" id="heading-1"></a>Heading 1</h1>
<h1><a href="#heading-1-tail" id="heading-1-tail"></a>Heading 1 Tail</h1>
.
 MdFile:[0, 30, "# Hea … ding 1\n# Heading 1 Tail #"]
   MdAtxHeaderImpl:[0, 12, "# Hea … ding 1\n"]
     HEADER_ATX_MARKER:[0, 1, "#"]
     WHITESPACE:[1, 2, " "]
     MdHeaderTextImpl:[2, 11, "Heading 1"]
       HEADER_TEXT:[2, 11, "Heading 1"]
     EOL:[11, 12, "\n"]
   MdAtxHeaderImpl:[12, 30, "# Hea … ding 1 Tail #"]
     HEADER_ATX_MARKER:[12, 13, "#"]
     WHITESPACE:[13, 14, " "]
     MdHeaderTextImpl:[14, 28, "Headi … ng 1 Tail"]
       HEADER_TEXT:[14, 28, "Headi … ng 1 Tail"]
     WHITESPACE:[28, 29, " "]
     ATX_HEADER:[29, 30, "#"]
````````````````````````````````


Atx 1 with inlines

```````````````````````````````` example Heading: 6
# Heading 1 **bold** _italic_ `code`
.
<h1><a href="#heading-1-bold-italic-code" id="heading-1-bold-italic-code"></a>Heading 1 <strong>bold</strong> <em>italic</em> <code>code</code></h1>
.
 MdFile:[0, 36, "# Hea … ding 1 **bold** _italic_ `code`"]
   MdAtxHeaderImpl:[0, 36, "# Hea … ding 1 **bold** _italic_ `code`"]
     HEADER_ATX_MARKER:[0, 1, "#"]
     WHITESPACE:[1, 2, " "]
     MdHeaderTextImpl:[2, 36, "Headi … ng 1 **bold** _italic_ `code`"]
       HEADER_TEXT:[2, 12, "Heading 1 "]
       MdInlineBoldImpl:[12, 20, "**bold**"]
         HEADER_TEXT_BOLD_MARKER:[12, 14, "**"]
         HEADER_TEXT_BOLD_TEXT:[14, 18, "bold"]
         HEADER_TEXT_BOLD_MARKER:[18, 20, "**"]
       HEADER_TEXT:[20, 21, " "]
       MdInlineItalicImpl:[21, 29, "_italic_"]
         HEADER_TEXT_ITALIC_MARKER:[21, 22, "_"]
         HEADER_TEXT_ITALIC_TEXT:[22, 28, "italic"]
         HEADER_TEXT_ITALIC_MARKER:[28, 29, "_"]
       HEADER_TEXT:[29, 30, " "]
       MdInlineCodeImpl:[30, 36, "`code`"]
         HEADER_TEXT_CODE_MARKER:[30, 31, "`"]
         HEADER_TEXT_CODE_TEXT:[31, 35, "code"]
         HEADER_TEXT_CODE_MARKER:[35, 36, "`"]
````````````````````````````````


Atx 2

```````````````````````````````` example Heading: 7
## Heading 2
## Heading 2 Tail #
.
<h2><a href="#heading-2" id="heading-2"></a>Heading 2</h2>
<h2><a href="#heading-2-tail" id="heading-2-tail"></a>Heading 2 Tail</h2>
.
 MdFile:[0, 32, "## He … ading 2\n## Heading 2 Tail #"]
   MdAtxHeaderImpl:[0, 13, "## He … ading 2\n"]
     HEADER_ATX_MARKER:[0, 2, "##"]
     WHITESPACE:[2, 3, " "]
     MdHeaderTextImpl:[3, 12, "Heading 2"]
       HEADER_TEXT:[3, 12, "Heading 2"]
     EOL:[12, 13, "\n"]
   MdAtxHeaderImpl:[13, 32, "## He … ading 2 Tail #"]
     HEADER_ATX_MARKER:[13, 15, "##"]
     WHITESPACE:[15, 16, " "]
     MdHeaderTextImpl:[16, 30, "Headi … ng 2 Tail"]
       HEADER_TEXT:[16, 30, "Headi … ng 2 Tail"]
     WHITESPACE:[30, 31, " "]
     ATX_HEADER:[31, 32, "#"]
````````````````````````````````


Atx 3

```````````````````````````````` example Heading: 8
### Heading 3
### Heading 3 Tail #
.
<h3><a href="#heading-3" id="heading-3"></a>Heading 3</h3>
<h3><a href="#heading-3-tail" id="heading-3-tail"></a>Heading 3 Tail</h3>
.
 MdFile:[0, 34, "### H … eading 3\n### Heading 3 Tail #"]
   MdAtxHeaderImpl:[0, 14, "### H … eading 3\n"]
     HEADER_ATX_MARKER:[0, 3, "###"]
     WHITESPACE:[3, 4, " "]
     MdHeaderTextImpl:[4, 13, "Heading 3"]
       HEADER_TEXT:[4, 13, "Heading 3"]
     EOL:[13, 14, "\n"]
   MdAtxHeaderImpl:[14, 34, "### H … eading 3 Tail #"]
     HEADER_ATX_MARKER:[14, 17, "###"]
     WHITESPACE:[17, 18, " "]
     MdHeaderTextImpl:[18, 32, "Headi … ng 3 Tail"]
       HEADER_TEXT:[18, 32, "Headi … ng 3 Tail"]
     WHITESPACE:[32, 33, " "]
     ATX_HEADER:[33, 34, "#"]
````````````````````````````````


Atx 4

```````````````````````````````` example Heading: 9
#### Heading 4
#### Heading 4 Tail #
.
<h4><a href="#heading-4" id="heading-4"></a>Heading 4</h4>
<h4><a href="#heading-4-tail" id="heading-4-tail"></a>Heading 4 Tail</h4>
.
 MdFile:[0, 36, "####  … Heading 4\n#### Heading 4 Tail #"]
   MdAtxHeaderImpl:[0, 15, "####  … Heading 4\n"]
     HEADER_ATX_MARKER:[0, 4, "####"]
     WHITESPACE:[4, 5, " "]
     MdHeaderTextImpl:[5, 14, "Heading 4"]
       HEADER_TEXT:[5, 14, "Heading 4"]
     EOL:[14, 15, "\n"]
   MdAtxHeaderImpl:[15, 36, "####  … Heading 4 Tail #"]
     HEADER_ATX_MARKER:[15, 19, "####"]
     WHITESPACE:[19, 20, " "]
     MdHeaderTextImpl:[20, 34, "Headi … ng 4 Tail"]
       HEADER_TEXT:[20, 34, "Headi … ng 4 Tail"]
     WHITESPACE:[34, 35, " "]
     ATX_HEADER:[35, 36, "#"]
````````````````````````````````


Atx 5

```````````````````````````````` example Heading: 10
##### Heading 5
##### Heading 5 Tail #
.
<h5><a href="#heading-5" id="heading-5"></a>Heading 5</h5>
<h5><a href="#heading-5-tail" id="heading-5-tail"></a>Heading 5 Tail</h5>
.
 MdFile:[0, 38, "##### …  Heading 5\n##### Heading 5 Tail #"]
   MdAtxHeaderImpl:[0, 16, "##### …  Heading 5\n"]
     HEADER_ATX_MARKER:[0, 5, "#####"]
     WHITESPACE:[5, 6, " "]
     MdHeaderTextImpl:[6, 15, "Heading 5"]
       HEADER_TEXT:[6, 15, "Heading 5"]
     EOL:[15, 16, "\n"]
   MdAtxHeaderImpl:[16, 38, "##### …  Heading 5 Tail #"]
     HEADER_ATX_MARKER:[16, 21, "#####"]
     WHITESPACE:[21, 22, " "]
     MdHeaderTextImpl:[22, 36, "Headi … ng 5 Tail"]
       HEADER_TEXT:[22, 36, "Headi … ng 5 Tail"]
     WHITESPACE:[36, 37, " "]
     ATX_HEADER:[37, 38, "#"]
````````````````````````````````


Atx 6

```````````````````````````````` example Heading: 11
###### Heading 6
###### Heading 6 Tail #
.
<h6><a href="#heading-6" id="heading-6"></a>Heading 6</h6>
<h6><a href="#heading-6-tail" id="heading-6-tail"></a>Heading 6 Tail</h6>
.
 MdFile:[0, 40, "##### … # Heading 6\n###### Heading 6 Tail #"]
   MdAtxHeaderImpl:[0, 17, "##### … # Heading 6\n"]
     HEADER_ATX_MARKER:[0, 6, "######"]
     WHITESPACE:[6, 7, " "]
     MdHeaderTextImpl:[7, 16, "Heading 6"]
       HEADER_TEXT:[7, 16, "Heading 6"]
     EOL:[16, 17, "\n"]
   MdAtxHeaderImpl:[17, 40, "##### … # Heading 6 Tail #"]
     HEADER_ATX_MARKER:[17, 23, "######"]
     WHITESPACE:[23, 24, " "]
     MdHeaderTextImpl:[24, 38, "Headi … ng 6 Tail"]
       HEADER_TEXT:[24, 38, "Headi … ng 6 Tail"]
     WHITESPACE:[38, 39, " "]
     ATX_HEADER:[39, 40, "#"]
````````````````````````````````


### HtmlBlock

`HtmlBlock`

Html Blocks

```````````````````````````````` example HtmlBlock: 1
<div>
  <ul>
    <li>item</li>
  </ul>
</div>
.
<div>
  <ul>
    <li>item</li>
  </ul>
</div>
.
 HtmlFileImpl:[0, 45, "<div> … \n  <ul>\n    <li>item</li>\n  </ul>\n</div>"]
   HtmlDocumentImpl:[0, 45, "<div> … \n  <ul>\n    <li>item</li>\n  </ul>\n</div>"]
     XmlPrologImpl:[0, 0]
     HtmlTagImpl:[0, 45, "<div> … \n  <ul>\n    <li>item</li>\n  </ul>\n</div>"]
       XML_START_TAG_START:[0, 1, "<"]
       XML_NAME:[1, 4, "div"]
       XML_TAG_END:[4, 5, ">"]
       XmlTextImpl:[5, 8, "\n  "]
         WHITE_SPACE:[5, 8, "\n  "]
       HtmlTagImpl:[8, 38, "<ul>\n …     <li>item</li>\n  </ul>"]
         XML_START_TAG_START:[8, 9, "<"]
         XML_NAME:[9, 11, "ul"]
         XML_TAG_END:[11, 12, ">"]
         XmlTextImpl:[12, 17, "\n    "]
           WHITE_SPACE:[12, 17, "\n    "]
         HtmlTagImpl:[17, 30, "<li>i … tem</li>"]
           XML_START_TAG_START:[17, 18, "<"]
           XML_NAME:[18, 20, "li"]
           XML_TAG_END:[20, 21, ">"]
           XmlTextImpl:[21, 25, "item"]
             XML_DATA_CHARACTERS:[21, 25, "item"]
           XML_END_TAG_START:[25, 27, "</"]
           XML_NAME:[27, 29, "li"]
           XML_TAG_END:[29, 30, ">"]
         XmlTextImpl:[30, 33, "\n  "]
           WHITE_SPACE:[30, 33, "\n  "]
         XML_END_TAG_START:[33, 35, "</"]
         XML_NAME:[35, 37, "ul"]
         XML_TAG_END:[37, 38, ">"]
       XmlTextImpl:[38, 39, "\n"]
         WHITE_SPACE:[38, 39, "\n"]
       XML_END_TAG_START:[39, 41, "</"]
       XML_NAME:[41, 44, "div"]
       XML_TAG_END:[44, 45, ">"]
````````````````````````````````


### HtmlCommentBlock

`HtmlCommentBlock`

empty

```````````````````````````````` example HtmlCommentBlock: 1
<!---->
.
<!---->
.
 MdFile:[0, 7, "<!---->"]
   MdBlockCommentImpl:[0, 7, "<!---->"]
     BLOCK_COMMENT_OPEN:[0, 4, "<!--"]
     BLOCK_COMMENT_CLOSE:[4, 7, "-->"]
````````````````````````````````


non-empty, no whitespace

```````````````````````````````` example HtmlCommentBlock: 2
<!--test-->
.
<!--test-->
.
 MdFile:[0, 11, "<!--t … est-->"]
   MdBlockCommentImpl:[0, 11, "<!--t … est-->"]
     BLOCK_COMMENT_OPEN:[0, 4, "<!--"]
     BLOCK_COMMENT_TEXT:[4, 8, "test"]
     BLOCK_COMMENT_CLOSE:[8, 11, "-->"]
````````````````````````````````


non-empty whitespace

```````````````````````````````` example HtmlCommentBlock: 3
<!-- test -->
.
<!-- test -->
.
 MdFile:[0, 13, "<!--  … test -->"]
   MdBlockCommentImpl:[0, 13, "<!--  … test -->"]
     BLOCK_COMMENT_OPEN:[0, 4, "<!--"]
     BLOCK_COMMENT_TEXT:[4, 10, " test "]
     BLOCK_COMMENT_CLOSE:[10, 13, "-->"]
````````````````````````````````


### HtmlEntity

`HtmlEntity`

named

```````````````````````````````` example HtmlEntity: 1
&nbsp;
.
<p>&nbsp;</p>
.
 MdFile:[0, 6, "&nbsp;"]
   MdParagraphImpl:[0, 6, "&nbsp;"]
     MdTextBlockImpl:[0, 6, "&nbsp;"]
       HTML_ENTITY:[0, 6, "&nbsp;"]
````````````````````````````````


numbered

```````````````````````````````` example HtmlEntity: 2
&#10;
.
<p>&#10;</p>
.
 MdFile:[0, 5, "&#10;"]
   MdParagraphImpl:[0, 5, "&#10;"]
     MdTextBlockImpl:[0, 5, "&#10;"]
       HTML_ENTITY:[0, 5, "&#10;"]
````````````````````````````````


named embedded

```````````````````````````````` example HtmlEntity: 3
text with &nbsp; embedded
.
<p>text with &nbsp; embedded</p>
.
 MdFile:[0, 25, "text  … with &nbsp; embedded"]
   MdParagraphImpl:[0, 25, "text  … with &nbsp; embedded"]
     MdTextBlockImpl:[0, 25, "text  … with &nbsp; embedded"]
       TEXT:[0, 10, "text with "]
       HTML_ENTITY:[10, 16, "&nbsp;"]
       TEXT:[16, 25, " embedded"]
````````````````````````````````


numbered embedded

```````````````````````````````` example HtmlEntity: 4
text with &#10; embedded
.
<p>text with &#10; embedded</p>
.
 MdFile:[0, 24, "text  … with &#10; embedded"]
   MdParagraphImpl:[0, 24, "text  … with &#10; embedded"]
     MdTextBlockImpl:[0, 24, "text  … with &#10; embedded"]
       TEXT:[0, 10, "text with "]
       HTML_ENTITY:[10, 15, "&#10;"]
       TEXT:[15, 24, " embedded"]
````````````````````````````````


named embedded, no whitespace

```````````````````````````````` example HtmlEntity: 5
text with&nbsp;embedded
.
<p>text with&nbsp;embedded</p>
.
 MdFile:[0, 23, "text  … with&nbsp;embedded"]
   MdParagraphImpl:[0, 23, "text  … with&nbsp;embedded"]
     MdTextBlockImpl:[0, 23, "text  … with&nbsp;embedded"]
       TEXT:[0, 9, "text with"]
       HTML_ENTITY:[9, 15, "&nbsp;"]
       TEXT:[15, 23, "embedded"]
````````````````````````````````


numbered embedded, no whitespace

```````````````````````````````` example HtmlEntity: 6
text with&#10;embedded
.
<p>text with&#10;embedded</p>
.
 MdFile:[0, 22, "text  … with&#10;embedded"]
   MdParagraphImpl:[0, 22, "text  … with&#10;embedded"]
     MdTextBlockImpl:[0, 22, "text  … with&#10;embedded"]
       TEXT:[0, 9, "text with"]
       HTML_ENTITY:[9, 14, "&#10;"]
       TEXT:[14, 22, "embedded"]
````````````````````````````````


### HtmlInline

`HtmlInline`

empty

```````````````````````````````` example HtmlInline: 1
<span></span>
.
<p><span></span></p>
.
 MdFile:[0, 13, "<span … ></span>"]
   MdParagraphImpl:[0, 13, "<span … ></span>"]
     MdTextBlockImpl:[0, 13, "<span … ></span>"]
       INLINE_HTML:[0, 13, "<span … ></span>"]
````````````````````````````````


non-empty

```````````````````````````````` example HtmlInline: 2
<span>span</span>
.
<p><span>span</span></p>
.
 MdFile:[0, 17, "<span … >span</span>"]
   MdParagraphImpl:[0, 17, "<span … >span</span>"]
     MdTextBlockImpl:[0, 17, "<span … >span</span>"]
       INLINE_HTML:[0, 6, "<span>"]
       TEXT:[6, 10, "span"]
       INLINE_HTML:[10, 17, "</span>"]
````````````````````````````````


empty leading

```````````````````````````````` example HtmlInline: 3
<span></span> embedded
.
<p><span></span> embedded</p>
.
 MdFile:[0, 22, "<span … ></span> embedded"]
   MdParagraphImpl:[0, 22, "<span … ></span> embedded"]
     MdTextBlockImpl:[0, 22, "<span … ></span> embedded"]
       INLINE_HTML:[0, 13, "<span … ></span>"]
       TEXT:[13, 22, " embedded"]
````````````````````````````````


non-empty leading

```````````````````````````````` example HtmlInline: 4
<span>span</span> embedded
.
<p><span>span</span> embedded</p>
.
 MdFile:[0, 26, "<span … >span</span> embedded"]
   MdParagraphImpl:[0, 26, "<span … >span</span> embedded"]
     MdTextBlockImpl:[0, 26, "<span … >span</span> embedded"]
       INLINE_HTML:[0, 6, "<span>"]
       TEXT:[6, 10, "span"]
       INLINE_HTML:[10, 17, "</span>"]
       TEXT:[17, 26, " embedded"]
````````````````````````````````


empty embedded

```````````````````````````````` example HtmlInline: 5
text with <span></span> embedded
.
<p>text with <span></span> embedded</p>
.
 MdFile:[0, 32, "text  … with <span></span> embedded"]
   MdParagraphImpl:[0, 32, "text  … with <span></span> embedded"]
     MdTextBlockImpl:[0, 32, "text  … with <span></span> embedded"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 23, "<span … ></span>"]
       TEXT:[23, 32, " embedded"]
````````````````````````````````


non-empty embedded

```````````````````````````````` example HtmlInline: 6
text with <span>span</span> embedded
.
<p>text with <span>span</span> embedded</p>
.
 MdFile:[0, 36, "text  … with <span>span</span> embedded"]
   MdParagraphImpl:[0, 36, "text  … with <span>span</span> embedded"]
     MdTextBlockImpl:[0, 36, "text  … with <span>span</span> embedded"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 16, "<span>"]
       TEXT:[16, 20, "span"]
       INLINE_HTML:[20, 27, "</span>"]
       TEXT:[27, 36, " embedded"]
````````````````````````````````


empty trailing

```````````````````````````````` example HtmlInline: 7
text with <span></span>
.
<p>text with <span></span></p>
.
 MdFile:[0, 23, "text  … with <span></span>"]
   MdParagraphImpl:[0, 23, "text  … with <span></span>"]
     MdTextBlockImpl:[0, 23, "text  … with <span></span>"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 23, "<span … ></span>"]
````````````````````````````````


non-empty trailing

```````````````````````````````` example HtmlInline: 8
text with <span>span</span>
.
<p>text with <span>span</span></p>
.
 MdFile:[0, 27, "text  … with <span>span</span>"]
   MdParagraphImpl:[0, 27, "text  … with <span>span</span>"]
     MdTextBlockImpl:[0, 27, "text  … with <span>span</span>"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 16, "<span>"]
       TEXT:[16, 20, "span"]
       INLINE_HTML:[20, 27, "</span>"]
````````````````````````````````


no ref anchor ids

```````````````````````````````` example HtmlInline: 9
text with <a id="test"></a>
.
<p>text with <a id="test"></a></p>
.
 MdFile:[0, 27, "text  … with <a id=\"test\"></a>"]
   MdParagraphImpl:[0, 27, "text  … with <a id=\"test\"></a>"]
     MdTextBlockImpl:[0, 27, "text  … with <a id=\"test\"></a>"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 27, "<a id … =\"test\"></a>"]
````````````````````````````````


ref anchor id

```````````````````````````````` example(HtmlInline: 10) options(parse-ref-anchors)
text with <a id="test"></a>
.
<p>text with <a id="test"></a></p>
.
 MdFile:[0, 27, "text  … with <a id=\"test\"></a>"]
   MdParagraphImpl:[0, 27, "text  … with <a id=\"test\"></a>"]
     MdTextBlockImpl:[0, 27, "text  … with <a id=\"test\"></a>"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 27, "<a id … =\"test\"></a>"]
````````````````````````````````


ref anchor id

```````````````````````````````` example(HtmlInline: 11) options(parse-ref-anchors)
text with <a id="test"></a> more text
.
<p>text with <a id="test"></a> more text</p>
.
 MdFile:[0, 37, "text  … with <a id=\"test\"></a> more text"]
   MdParagraphImpl:[0, 37, "text  … with <a id=\"test\"></a> more text"]
     MdTextBlockImpl:[0, 37, "text  … with <a id=\"test\"></a> more text"]
       TEXT:[0, 10, "text with "]
       INLINE_HTML:[10, 27, "<a id … =\"test\"></a>"]
       TEXT:[27, 37, " more text"]
````````````````````````````````


### HtmlInlineComment

`HtmlInlineComment`

Plain text with empty HTML comment

```````````````````````````````` example HtmlInlineComment: 1
First line
Second line <!---->
Last line
.
<p>First line
Second line <!---->
Last line</p>
.
 MdFile:[0, 40, "First …  line\nSecond line <!---->\nLast line"]
   MdParagraphImpl:[0, 40, "First …  line\nSecond line <!---->\nLast line"]
     MdTextBlockImpl:[0, 40, "First …  line\nSecond line <!---->\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 23, "Secon … d line "]
       MdInlineCommentImpl:[23, 30, "<!---->"]
         COMMENT_OPEN:[23, 27, "<!--"]
         COMMENT_CLOSE:[27, 30, "-->"]
       EOL:[30, 31, "\n"]
       TEXT:[31, 40, "Last line"]
````````````````````````````````


Html Inline with comment

```````````````````````````````` example HtmlInlineComment: 2
text <div><!-- HTML Comment --></div> some more text
.
<p>text <div><!-- HTML Comment --></div> some more text</p>
.
 MdFile:[0, 52, "text  … <div><!-- HTML Comment --></div> some more text"]
   MdParagraphImpl:[0, 52, "text  … <div><!-- HTML Comment --></div> some more text"]
     MdTextBlockImpl:[0, 52, "text  … <div><!-- HTML Comment --></div> some more text"]
       TEXT:[0, 5, "text "]
       INLINE_HTML:[5, 10, "<div>"]
       MdInlineCommentImpl:[10, 31, "<!--  … HTML Comment -->"]
         COMMENT_OPEN:[10, 14, "<!--"]
         COMMENT_TEXT:[14, 28, " HTML …  Comment "]
         COMMENT_CLOSE:[28, 31, "-->"]
       INLINE_HTML:[31, 37, "</div>"]
       TEXT:[37, 52, " some …  more text"]
````````````````````````````````


Plain text with simple HTML comment

```````````````````````````````` example HtmlInlineComment: 3
First line
Second line <!--simple-->
Last line
.
<p>First line
Second line <!--simple-->
Last line</p>
.
 MdFile:[0, 46, "First …  line\nSecond line <!--simple-->\nLast line"]
   MdParagraphImpl:[0, 46, "First …  line\nSecond line <!--simple-->\nLast line"]
     MdTextBlockImpl:[0, 46, "First …  line\nSecond line <!--simple-->\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 23, "Secon … d line "]
       MdInlineCommentImpl:[23, 36, "<!--s … imple-->"]
         COMMENT_OPEN:[23, 27, "<!--"]
         COMMENT_TEXT:[27, 33, "simple"]
         COMMENT_CLOSE:[33, 36, "-->"]
       EOL:[36, 37, "\n"]
       TEXT:[37, 46, "Last line"]
````````````````````````````````


Plain text with unterminated HTML comment

```````````````````````````````` example(HtmlInlineComment: 4) options(no-smarts)
First line
Second line <!--simple
Last line
.
<p>First line
Second line &lt;!--simple
Last line</p>
.
 MdFile:[0, 43, "First …  line\nSecond line <!--simple\nLast line"]
   MdParagraphImpl:[0, 43, "First …  line\nSecond line <!--simple\nLast line"]
     MdTextBlockImpl:[0, 43, "First …  line\nSecond line <!--simple\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 33, "Secon … d line <!--simple"]
       EOL:[33, 34, "\n"]
       TEXT:[34, 43, "Last line"]
````````````````````````````````


Plain text with HTML comment with embedded looking code

```````````````````````````````` example HtmlInlineComment: 5
First line
Second line <!--`code`-->
Last line
.
<p>First line
Second line <!--`code`-->
Last line</p>
.
 MdFile:[0, 46, "First …  line\nSecond line <!--`code`-->\nLast line"]
   MdParagraphImpl:[0, 46, "First …  line\nSecond line <!--`code`-->\nLast line"]
     MdTextBlockImpl:[0, 46, "First …  line\nSecond line <!--`code`-->\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 23, "Secon … d line "]
       MdInlineCommentImpl:[23, 36, "<!--` … code`-->"]
         COMMENT_OPEN:[23, 27, "<!--"]
         COMMENT_TEXT:[27, 33, "`code`"]
         COMMENT_CLOSE:[33, 36, "-->"]
       EOL:[36, 37, "\n"]
       TEXT:[37, 46, "Last line"]
````````````````````````````````


### HtmlInnerBlock

`HtmlInnerBlock` `HtmlInnerBlockComment`

Html Blocks

```````````````````````````````` example HtmlInnerBlock: 1
<div>
    <!-- HTML Comment -->
</div>
.
<div>
    <!-- HTML Comment -->
</div>
.
 HtmlFileImpl:[0, 38, "<div> … \n    <!-- HTML Comment -->\n</div>"]
   HtmlDocumentImpl:[0, 38, "<div> … \n    <!-- HTML Comment -->\n</div>"]
     XmlPrologImpl:[0, 0]
     HtmlTagImpl:[0, 38, "<div> … \n    <!-- HTML Comment -->\n</div>"]
       XML_START_TAG_START:[0, 1, "<"]
       XML_NAME:[1, 4, "div"]
       XML_TAG_END:[4, 5, ">"]
       XmlTextImpl:[5, 32, "\n     … <!-- HTML Comment -->\n"]
         WHITE_SPACE:[5, 10, "\n    "]
         XmlCommentImpl:[10, 31, "<!--  … HTML Comment -->"]
           XML_COMMENT_START:[10, 14, "<!--"]
           XML_COMMENT_CHARACTERS:[14, 28, " HTML …  Comment "]
           XML_COMMENT_END:[28, 31, "-->"]
         WHITE_SPACE:[31, 32, "\n"]
       XML_END_TAG_START:[32, 34, "</"]
       XML_NAME:[34, 37, "div"]
       XML_TAG_END:[37, 38, ">"]
````````````````````````````````


### Image

`Image`

plain

```````````````````````````````` example Image: 1
![alt](/url) 
.
<p><img src="/url" alt="alt" /></p>
.
 MdFile:[0, 13, "![alt … ](/url) "]
   MdParagraphImpl:[0, 13, "![alt … ](/url) "]
     MdTextBlockImpl:[0, 13, "![alt … ](/url) "]
       MdImageLinkImpl:[0, 12, "![alt … ](/url)"]
         IMAGE_LINK_REF_TEXT_OPEN:[0, 2, "!["]
         MdImageLinkTextImpl:[2, 5, "alt"]
           TEXT:[2, 5, "alt"]
         IMAGE_LINK_REF_TEXT_CLOSE:[5, 6, "]"]
         IMAGE_LINK_REF_OPEN:[6, 7, "("]
         MdImageLinkRefImpl:[7, 11, "/url"]
           IMAGE_LINK_REF:[7, 11, "/url"]
         IMAGE_LINK_REF_CLOSE:[11, 12, ")"]
       WHITESPACE:[12, 13, " "]
````````````````````````````````


embedded

```````````````````````````````` example Image: 2
text with ![alt](/url) embedded 
.
<p>text with <img src="/url" alt="alt" /> embedded</p>
.
 MdFile:[0, 32, "text  … with ![alt](/url) embedded "]
   MdParagraphImpl:[0, 32, "text  … with ![alt](/url) embedded "]
     MdTextBlockImpl:[0, 32, "text  … with ![alt](/url) embedded "]
       TEXT:[0, 10, "text with "]
       MdImageLinkImpl:[10, 22, "![alt … ](/url)"]
         IMAGE_LINK_REF_TEXT_OPEN:[10, 12, "!["]
         MdImageLinkTextImpl:[12, 15, "alt"]
           TEXT:[12, 15, "alt"]
         IMAGE_LINK_REF_TEXT_CLOSE:[15, 16, "]"]
         IMAGE_LINK_REF_OPEN:[16, 17, "("]
         MdImageLinkRefImpl:[17, 21, "/url"]
           IMAGE_LINK_REF:[17, 21, "/url"]
         IMAGE_LINK_REF_CLOSE:[21, 22, ")"]
       TEXT:[22, 31, " embedded"]
       WHITESPACE:[31, 32, " "]
````````````````````````````````


multi-line

```````````````````````````````` example Image: 3
text with ![alt](/url?
multi-line
content
)
embedded.
.
<p>text with <img src="/url?multi-line%0Acontent%0A" alt="alt" />
embedded.</p>
.
 MdFile:[0, 53, "text  … with ![alt](/url?\nmulti-line\ncontent\n)\nembedded."]
   MdParagraphImpl:[0, 53, "text  … with ![alt](/url?\nmulti-line\ncontent\n)\nembedded."]
     MdTextBlockImpl:[0, 53, "text  … with ![alt](/url?\nmulti-line\ncontent\n)\nembedded."]
       TEXT:[0, 10, "text with "]
       MdImageLinkImpl:[10, 43, "![alt … ](/url?\nmulti-line\ncontent\n)"]
         IMAGE_LINK_REF_TEXT_OPEN:[10, 12, "!["]
         MdImageLinkTextImpl:[12, 15, "alt"]
           TEXT:[12, 15, "alt"]
         IMAGE_LINK_REF_TEXT_CLOSE:[15, 16, "]"]
         IMAGE_LINK_REF_OPEN:[16, 17, "("]
         MdImageLinkRefImpl:[17, 23, "/url?\n"]
           IMAGE_LINK_REF:[17, 23, "/url?\n"]
         MdImageMultiLineUrlContentImpl:[23, 41, "multi … -line\ncontent"]
           IMAGE_URL_CONTENT:[23, 41, "multi … -line\ncontent"]
         EOL:[41, 42, "\n"]
         IMAGE_LINK_REF_CLOSE:[42, 43, ")"]
       EOL:[43, 44, "\n"]
       TEXT:[44, 53, "embedded."]
````````````````````````````````


multi-line with escape chars

```````````````````````````````` example Image: 4
text with ![alt](/url?
multi-line \\
content
)
embedded.
.
<p>text with <img src="/url?multi-line%20%5C%5C%0Acontent%0A" alt="alt" />
embedded.</p>
.
 MdFile:[0, 56, "text  … with ![alt](/url?\nmulti-line \\\ncontent\n)\nembedded."]
   MdParagraphImpl:[0, 56, "text  … with ![alt](/url?\nmulti-line \\\ncontent\n)\nembedded."]
     MdTextBlockImpl:[0, 56, "text  … with ![alt](/url?\nmulti-line \\\ncontent\n)\nembedded."]
       TEXT:[0, 10, "text with "]
       MdImageLinkImpl:[10, 46, "![alt … ](/url?\nmulti-line \\\ncontent\n)"]
         IMAGE_LINK_REF_TEXT_OPEN:[10, 12, "!["]
         MdImageLinkTextImpl:[12, 15, "alt"]
           TEXT:[12, 15, "alt"]
         IMAGE_LINK_REF_TEXT_CLOSE:[15, 16, "]"]
         IMAGE_LINK_REF_OPEN:[16, 17, "("]
         MdImageLinkRefImpl:[17, 23, "/url?\n"]
           IMAGE_LINK_REF:[17, 23, "/url?\n"]
         MdImageMultiLineUrlContentImpl:[23, 44, "multi … -line \\\ncontent"]
           IMAGE_URL_CONTENT:[23, 44, "multi … -line \\\ncontent"]
         EOL:[44, 45, "\n"]
         IMAGE_LINK_REF_CLOSE:[45, 46, ")"]
       EOL:[46, 47, "\n"]
       TEXT:[47, 56, "embedded."]
````````````````````````````````


### ImageRef

`ImageRef`

basic

```````````````````````````````` example ImageRef: 1
[ref]: /url

![ref]
.
<p><img src="/url" alt="ref" /></p>
.
 MdFile:[0, 19, "[ref] … : /url\n\n![ref]"]
   MdReferenceImpl:[0, 12, "[ref] … : /url\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 4, "ref"]
       REFERENCE_TEXT_LEAF:[1, 4, "ref"]
     REFERENCE_TEXT_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdReferenceLinkRefImpl:[7, 11, "/url"]
       REFERENCE_LINK_REF:[7, 11, "/url"]
     EOL:[11, 12, "\n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   MdParagraphImpl:[13, 19, "![ref]"]
     MdTextBlockImpl:[13, 19, "![ref]"]
       MdReferenceImageImpl:[13, 19, "![ref]"]
         REFERENCE_IMAGE_REFERENCE_OPEN2:[13, 15, "!["]
         MdReferenceImageReferenceImpl:[15, 18, "ref"]
           REFERENCE_IMAGE_REFERENCE_LEAF:[15, 18, "ref"]
         REFERENCE_IMAGE_REFERENCE_CLOSE2:[18, 19, "]"]
````````````````````````````````


undefined

```````````````````````````````` example ImageRef: 2
[ref2]: /url2

![ref]
.
<p>![ref]</p>
.
 MdFile:[0, 21, "[ref2 … ]: /url2\n\n![ref]"]
   MdReferenceImpl:[0, 14, "[ref2 … ]: /url2\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 5, "ref2"]
       REFERENCE_TEXT_LEAF:[1, 5, "ref2"]
     REFERENCE_TEXT_CLOSE:[5, 7, "]:"]
     WHITESPACE:[7, 8, " "]
     MdReferenceLinkRefImpl:[8, 13, "/url2"]
       REFERENCE_LINK_REF:[8, 13, "/url2"]
     EOL:[13, 14, "\n"]
   MdBlankLineImpl:[14, 15, "\n"]
     BLANK_LINE:[14, 15, "\n"]
   MdParagraphImpl:[15, 21, "![ref]"]
     MdTextBlockImpl:[15, 21, "![ref]"]
       MdReferenceImageImpl:[15, 21, "![ref]"]
         REFERENCE_IMAGE_REFERENCE_OPEN2:[15, 17, "!["]
         MdReferenceImageReferenceImpl:[17, 20, "ref"]
           REFERENCE_IMAGE_REFERENCE_LEAF:[17, 20, "ref"]
         REFERENCE_IMAGE_REFERENCE_CLOSE2:[20, 21, "]"]
````````````````````````````````


duplicate

```````````````````````````````` example ImageRef: 3
[ref]: /url1
[ref]: /url2

![ref]
.
<p><img src="/url1" alt="ref" /></p>
.
 MdFile:[0, 33, "[ref] … : /url1\n[ref]: /url2\n\n![ref]"]
   MdReferenceImpl:[0, 13, "[ref] … : /url1\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 4, "ref"]
       REFERENCE_TEXT_LEAF:[1, 4, "ref"]
     REFERENCE_TEXT_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdReferenceLinkRefImpl:[7, 12, "/url1"]
       REFERENCE_LINK_REF:[7, 12, "/url1"]
     EOL:[12, 13, "\n"]
   MdReferenceImpl:[13, 26, "[ref] … : /url2\n"]
     REFERENCE_TEXT_OPEN:[13, 14, "["]
     MdReferenceIdentifierImpl:[14, 17, "ref"]
       REFERENCE_TEXT_LEAF:[14, 17, "ref"]
     REFERENCE_TEXT_CLOSE:[17, 19, "]:"]
     WHITESPACE:[19, 20, " "]
     MdReferenceLinkRefImpl:[20, 25, "/url2"]
       REFERENCE_LINK_REF:[20, 25, "/url2"]
     EOL:[25, 26, "\n"]
   MdBlankLineImpl:[26, 27, "\n"]
     BLANK_LINE:[26, 27, "\n"]
   MdParagraphImpl:[27, 33, "![ref]"]
     MdTextBlockImpl:[27, 33, "![ref]"]
       MdReferenceImageImpl:[27, 33, "![ref]"]
         REFERENCE_IMAGE_REFERENCE_OPEN2:[27, 29, "!["]
         MdReferenceImageReferenceImpl:[29, 32, "ref"]
           REFERENCE_IMAGE_REFERENCE_LEAF:[29, 32, "ref"]
         REFERENCE_IMAGE_REFERENCE_CLOSE2:[32, 33, "]"]
````````````````````````````````


dummy ref

```````````````````````````````` example ImageRef: 4
[ref]: /url1

![ref][]
.
<p><img src="/url1" alt="ref" /></p>
.
 MdFile:[0, 22, "[ref] … : /url1\n\n![ref][]"]
   MdReferenceImpl:[0, 13, "[ref] … : /url1\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 4, "ref"]
       REFERENCE_TEXT_LEAF:[1, 4, "ref"]
     REFERENCE_TEXT_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdReferenceLinkRefImpl:[7, 12, "/url1"]
       REFERENCE_LINK_REF:[7, 12, "/url1"]
     EOL:[12, 13, "\n"]
   MdBlankLineImpl:[13, 14, "\n"]
     BLANK_LINE:[13, 14, "\n"]
   MdParagraphImpl:[14, 22, "![ref][]"]
     MdTextBlockImpl:[14, 22, "![ref][]"]
       MdReferenceImageImpl:[14, 22, "![ref][]"]
         REFERENCE_IMAGE_REFERENCE_OPEN2:[14, 16, "!["]
         MdReferenceImageReferenceImpl:[16, 19, "ref"]
           REFERENCE_IMAGE_REFERENCE_LEAF:[16, 19, "ref"]
         REFERENCE_IMAGE_REFERENCE_CLOSE2:[19, 20, "]"]
         DUMMY_REFERENCE:[20, 22, "[]"]
````````````````````````````````


### IndentedCodeBlock

`IndentedCodeBlock`

basic

```````````````````````````````` example IndentedCodeBlock: 1
    code
.
<pre><code>code
</code></pre>
.
 MdFile:[0, 8, "    code"]
   MdVerbatimImpl:[0, 8, "    code"]
     MdVerbatimContentImpl:[0, 8, "    code"]
       VERBATIM_CONTENT:[0, 8, "    code"]
````````````````````````````````


multi line

```````````````````````````````` example IndentedCodeBlock: 2
    code
        more code
.
<pre><code>code
    more code
</code></pre>
.
 MdFile:[0, 26, "    c … ode\n        more code"]
   MdVerbatimImpl:[0, 26, "    c … ode\n        more code"]
     MdVerbatimContentImpl:[0, 26, "    c … ode\n        more code"]
       VERBATIM_CONTENT:[0, 26, "    c … ode\n        more code"]
````````````````````````````````


multi line, blanks

```````````````````````````````` example IndentedCodeBlock: 3
    code
    
        more code
.
<pre><code>code

    more code
</code></pre>
.
 MdFile:[0, 31, "    c … ode\n    \n        more code"]
   MdVerbatimImpl:[0, 31, "    c … ode\n    \n        more code"]
     MdVerbatimContentImpl:[0, 31, "    c … ode\n    \n        more code"]
       VERBATIM_CONTENT:[0, 31, "    c … ode\n    \n        more code"]
````````````````````````````````


tabbed

```````````````````````````````` example IndentedCodeBlock: 4
→code
.
<pre><code>code
</code></pre>
.
 MdFile:[0, 5, "\u2192code"]
   MdVerbatimImpl:[0, 5, "\u2192code"]
     MdVerbatimContentImpl:[0, 5, "\u2192code"]
       VERBATIM_CONTENT:[0, 5, "\u2192code"]
````````````````````````````````


multi line

```````````````````````````````` example IndentedCodeBlock: 5
→code
→→more code
.
<pre><code>code
→more code
</code></pre>
.
 MdFile:[0, 17, "\u2192code … \n\u2192\u2192more code"]
   MdVerbatimImpl:[0, 17, "\u2192code … \n\u2192\u2192more code"]
     MdVerbatimContentImpl:[0, 17, "\u2192code … \n\u2192\u2192more code"]
       VERBATIM_CONTENT:[0, 17, "\u2192code … \n\u2192\u2192more code"]
````````````````````````````````


multi line, blanks

```````````````````````````````` example IndentedCodeBlock: 6
→code

→→more code
.
<pre><code>code

→more code
</code></pre>
.
 MdFile:[0, 18, "\u2192code … \n\n\u2192\u2192more code"]
   MdVerbatimImpl:[0, 18, "\u2192code … \n\n\u2192\u2192more code"]
     MdVerbatimContentImpl:[0, 18, "\u2192code … \n\n\u2192\u2192more code"]
       VERBATIM_CONTENT:[0, 18, "\u2192code … \n\n\u2192\u2192more code"]
````````````````````````````````


trailing blank lines

```````````````````````````````` example IndentedCodeBlock: 7
    code
    more code
    
    
.
<pre><code>code
more code
</code></pre>
.
 MdFile:[0, 33, "    c … ode\n    more code\n    \n    \n"]
   MdVerbatimImpl:[0, 23, "    c … ode\n    more code\n"]
     MdVerbatimContentImpl:[0, 23, "    c … ode\n    more code\n"]
       VERBATIM_CONTENT:[0, 23, "    c … ode\n    more code\n"]
   MdBlankLineImpl:[23, 28, "    \n"]
     BLANK_LINE:[23, 28, "    \n"]
   MdBlankLineImpl:[28, 33, "    \n"]
     BLANK_LINE:[28, 33, "    \n"]
````````````````````````````````


### JekyllFrontMatterBlock

`JekyllFrontMatterBlock` FlexmarkFrontMatter

Jekyll front matter

```````````````````````````````` example JekyllFrontMatterBlock: 1
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
---

.
.
 MdFile:[0, 150, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n---\n\n"]
   MdJekyllFrontMatterBlockImpl:[0, 148, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n---"]
     JEKYLL_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     JEKYLL_FRONT_MATTER_BLOCK:[4, 145, "title … : SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n"]
     JEKYLL_FRONT_MATTER_CLOSE:[145, 148, "---"]
   EOL:[148, 149, "\n"]
   MdBlankLineImpl:[149, 150, "\n"]
     BLANK_LINE:[149, 150, "\n"]
````````````````````````````````


### Jekyll Include Tag

```````````````````````````````` example Jekyll Include Tag: 1
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
---

{% include "includedFile" %}
.
.
 MdFile:[0, 178, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n---\n\n{% include \"includedFile\" %}"]
   MdJekyllFrontMatterBlockImpl:[0, 148, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n---"]
     JEKYLL_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     JEKYLL_FRONT_MATTER_BLOCK:[4, 145, "title … : SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n"]
     JEKYLL_FRONT_MATTER_CLOSE:[145, 148, "---"]
   EOL:[148, 149, "\n"]
   MdBlankLineImpl:[149, 150, "\n"]
     BLANK_LINE:[149, 150, "\n"]
   MdJekyllIncludeImpl:[150, 178, "{% in … clude \"includedFile\" %}"]
     JEKYLL_TAG_OPEN:[150, 152, "{%"]
     WHITESPACE:[152, 153, " "]
     JEKYLL_TAG_NAME:[153, 160, "include"]
     WHITESPACE:[160, 161, " "]
     JEKYLL_LINKREF_OPEN:[161, 162, "\""]
     MdJekyllIncludeLinkRefImpl:[162, 174, "inclu … dedFile"]
       JEKYLL_TAG_PARAMETERS:[162, 174, "inclu … dedFile"]
     JEKYLL_LINKREF_CLOSE:[174, 175, "\""]
     WHITESPACE:[175, 176, " "]
     JEKYLL_TAG_CLOSE:[176, 178, "%}"]
````````````````````````````````


no flexmark front matter

```````````````````````````````` example Jekyll Include Tag: 2
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

.
<hr />
.
 MdFile:[0, 155, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n...\n\n---\n\n"]
   MdJekyllFrontMatterBlockImpl:[0, 148, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n..."]
     JEKYLL_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     JEKYLL_FRONT_MATTER_BLOCK:[4, 145, "title … : SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n"]
     JEKYLL_FRONT_MATTER_CLOSE:[145, 148, "..."]
   EOL:[148, 149, "\n"]
   MdBlankLineImpl:[149, 150, "\n"]
     BLANK_LINE:[149, 150, "\n"]
   MdHRuleImpl:[150, 154, "---\n"]
     HRULE_TEXT:[150, 154, "---\n"]
   EOL:[154, 155, "\n"]
````````````````````````````````


### Link

`Link`

basic

```````````````````````````````` example Link: 1
[text](/url) 
.
<p><a href="/url">text</a></p>
.
 MdFile:[0, 13, "[text … ](/url) "]
   MdParagraphImpl:[0, 13, "[text … ](/url) "]
     MdTextBlockImpl:[0, 13, "[text … ](/url) "]
       MdExplicitLinkImpl:[0, 12, "[text … ](/url)"]
         LINK_REF_TEXT_OPEN:[0, 1, "["]
         MdLinkTextImpl:[1, 5, "text"]
           TEXT:[1, 5, "text"]
         LINK_REF_TEXT_CLOSE:[5, 6, "]"]
         LINK_REF_OPEN:[6, 7, "("]
         MdLinkRefImpl:[7, 11, "/url"]
           LINK_REF:[7, 11, "/url"]
         LINK_REF_CLOSE:[11, 12, ")"]
       WHITESPACE:[12, 13, " "]
````````````````````````````````


basic with intellij completion location

```````````````````````````````` example Link: 2
[text](/url⎮) 
.
<p>[text](/url⎮)</p>
.
 MdFile:[0, 14, "[text … ](/url%1f) "]
   MdParagraphImpl:[0, 14, "[text … ](/url%1f) "]
     MdTextBlockImpl:[0, 14, "[text … ](/url%1f) "]
       MdExplicitLinkImpl:[0, 13, "[text … ](/url%1f)"]
         LINK_REF_TEXT_OPEN:[0, 1, "["]
         MdLinkTextImpl:[1, 5, "text"]
           TEXT:[1, 5, "text"]
         LINK_REF_TEXT_CLOSE:[5, 6, "]"]
         LINK_REF_OPEN:[6, 7, "("]
         MdLinkRefImpl:[7, 12, "/url%1f"]
           LINK_REF:[7, 12, "/url%1f"]
         LINK_REF_CLOSE:[12, 13, ")"]
       WHITESPACE:[13, 14, " "]
````````````````````````````````


emphasis

```````````````````````````````` example Link: 3
[**text**](/url) 
.
<p><a href="/url"><strong>text</strong></a></p>
.
 MdFile:[0, 17, "[**te … xt**](/url) "]
   MdParagraphImpl:[0, 17, "[**te … xt**](/url) "]
     MdTextBlockImpl:[0, 17, "[**te … xt**](/url) "]
       MdExplicitLinkImpl:[0, 16, "[**te … xt**](/url)"]
         LINK_REF_TEXT_OPEN:[0, 1, "["]
         MdLinkTextImpl:[1, 9, "**text**"]
           MdInlineBoldImpl:[1, 9, "**text**"]
             BOLD_MARKER:[1, 3, "**"]
             BOLD_TEXT:[3, 7, "text"]
             BOLD_MARKER:[7, 9, "**"]
         LINK_REF_TEXT_CLOSE:[9, 10, "]"]
         LINK_REF_OPEN:[10, 11, "("]
         MdLinkRefImpl:[11, 15, "/url"]
           LINK_REF:[11, 15, "/url"]
         LINK_REF_CLOSE:[15, 16, ")"]
       WHITESPACE:[16, 17, " "]
````````````````````````````````


code

```````````````````````````````` example Link: 4
[`text`](/url) 
.
<p><a href="/url"><code>text</code></a></p>
.
 MdFile:[0, 15, "[`tex … t`](/url) "]
   MdParagraphImpl:[0, 15, "[`tex … t`](/url) "]
     MdTextBlockImpl:[0, 15, "[`tex … t`](/url) "]
       MdExplicitLinkImpl:[0, 14, "[`tex … t`](/url)"]
         LINK_REF_TEXT_OPEN:[0, 1, "["]
         MdLinkTextImpl:[1, 7, "`text`"]
           MdInlineCodeImpl:[1, 7, "`text`"]
             CODE_MARKER:[1, 2, "`"]
             CODE_TEXT:[2, 6, "text"]
             CODE_MARKER:[6, 7, "`"]
         LINK_REF_TEXT_CLOSE:[7, 8, "]"]
         LINK_REF_OPEN:[8, 9, "("]
         MdLinkRefImpl:[9, 13, "/url"]
           LINK_REF:[9, 13, "/url"]
         LINK_REF_CLOSE:[13, 14, ")"]
       WHITESPACE:[14, 15, " "]
````````````````````````````````


image

```````````````````````````````` example Link: 5
[![alt](/url2)](/url) 
.
<p><a href="/url"><img src="/url2" alt="alt" /></a></p>
.
 MdFile:[0, 22, "[![al … t](/url2)](/url) "]
   MdParagraphImpl:[0, 22, "[![al … t](/url2)](/url) "]
     MdTextBlockImpl:[0, 22, "[![al … t](/url2)](/url) "]
       MdExplicitLinkImpl:[0, 21, "[![al … t](/url2)](/url)"]
         LINK_REF_TEXT_OPEN:[0, 1, "["]
         MdLinkTextImpl:[1, 14, "![alt … ](/url2)"]
           MdImageLinkImpl:[1, 14, "![alt … ](/url2)"]
             IMAGE_LINK_REF_TEXT_OPEN:[1, 3, "!["]
             MdImageLinkTextImpl:[3, 6, "alt"]
               TEXT:[3, 6, "alt"]
             IMAGE_LINK_REF_TEXT_CLOSE:[6, 7, "]"]
             IMAGE_LINK_REF_OPEN:[7, 8, "("]
             MdImageLinkRefImpl:[8, 13, "/url2"]
               IMAGE_LINK_REF:[8, 13, "/url2"]
             IMAGE_LINK_REF_CLOSE:[13, 14, ")"]
         LINK_REF_TEXT_CLOSE:[14, 15, "]"]
         LINK_REF_OPEN:[15, 16, "("]
         MdLinkRefImpl:[16, 20, "/url"]
           LINK_REF:[16, 20, "/url"]
         LINK_REF_CLOSE:[20, 21, ")"]
       WHITESPACE:[21, 22, " "]
````````````````````````````````


basic embedded

```````````````````````````````` example Link: 6
text with [text](/url) embedded 
.
<p>text with <a href="/url">text</a> embedded</p>
.
 MdFile:[0, 32, "text  … with [text](/url) embedded "]
   MdParagraphImpl:[0, 32, "text  … with [text](/url) embedded "]
     MdTextBlockImpl:[0, 32, "text  … with [text](/url) embedded "]
       TEXT:[0, 10, "text with "]
       MdExplicitLinkImpl:[10, 22, "[text … ](/url)"]
         LINK_REF_TEXT_OPEN:[10, 11, "["]
         MdLinkTextImpl:[11, 15, "text"]
           TEXT:[11, 15, "text"]
         LINK_REF_TEXT_CLOSE:[15, 16, "]"]
         LINK_REF_OPEN:[16, 17, "("]
         MdLinkRefImpl:[17, 21, "/url"]
           LINK_REF:[17, 21, "/url"]
         LINK_REF_CLOSE:[21, 22, ")"]
       TEXT:[22, 31, " embedded"]
       WHITESPACE:[31, 32, " "]
````````````````````````````````


header embedded

```````````````````````````````` example Link: 7
# Heading [text](/url) 
.
<h1><a href="#heading-text" id="heading-text"></a>Heading <a href="/url">text</a></h1>
.
 MdFile:[0, 23, "# Hea … ding [text](/url) "]
   MdAtxHeaderImpl:[0, 22, "# Hea … ding [text](/url)"]
     HEADER_ATX_MARKER:[0, 1, "#"]
     WHITESPACE:[1, 2, " "]
     MdHeaderTextImpl:[2, 22, "Headi … ng [text](/url)"]
       HEADER_TEXT:[2, 10, "Heading "]
       MdExplicitLinkImpl:[10, 22, "[text … ](/url)"]
         LINK_REF_TEXT_OPEN:[10, 11, "["]
         MdLinkTextImpl:[11, 15, "text"]
           HEADER_TEXT:[11, 15, "text"]
         LINK_REF_TEXT_CLOSE:[15, 16, "]"]
         LINK_REF_OPEN:[16, 17, "("]
         MdLinkRefImpl:[17, 21, "/url"]
           LINK_REF:[17, 21, "/url"]
         LINK_REF_CLOSE:[21, 22, ")"]
   WHITESPACE:[22, 23, " "]
````````````````````````````````


header embedded

```````````````````````````````` example Link: 8
Heading [text](/url) 
---
.
<h2><a href="#heading-text" id="heading-text"></a>Heading <a href="/url">text</a></h2>
.
 MdFile:[0, 25, "Headi … ng [text](/url) \n---"]
   MdSetextHeaderImpl:[0, 25, "Headi … ng [text](/url) \n---"]
     MdHeaderTextImpl:[0, 20, "Headi … ng [text](/url)"]
       HEADER_TEXT:[0, 8, "Heading "]
       MdExplicitLinkImpl:[8, 20, "[text … ](/url)"]
         LINK_REF_TEXT_OPEN:[8, 9, "["]
         MdLinkTextImpl:[9, 13, "text"]
           HEADER_TEXT:[9, 13, "text"]
         LINK_REF_TEXT_CLOSE:[13, 14, "]"]
         LINK_REF_OPEN:[14, 15, "("]
         MdLinkRefImpl:[15, 19, "/url"]
           LINK_REF:[15, 19, "/url"]
         LINK_REF_CLOSE:[19, 20, ")"]
     WHITESPACE:[20, 21, " "]
     EOL:[21, 22, "\n"]
     HEADER_SETEXT_MARKER:[22, 25, "---"]
````````````````````````````````


header image embedded

```````````````````````````````` example Link: 9
# Heading [![alt](/url2)](/url) 
.
<h1><a href="#heading-alt" id="heading-alt"></a>Heading <a href="/url"><img src="/url2" alt="alt" /></a></h1>
.
 MdFile:[0, 32, "# Hea … ding [![alt](/url2)](/url) "]
   MdAtxHeaderImpl:[0, 31, "# Hea … ding [![alt](/url2)](/url)"]
     HEADER_ATX_MARKER:[0, 1, "#"]
     WHITESPACE:[1, 2, " "]
     MdHeaderTextImpl:[2, 31, "Headi … ng [![alt](/url2)](/url)"]
       HEADER_TEXT:[2, 10, "Heading "]
       MdExplicitLinkImpl:[10, 31, "[![al … t](/url2)](/url)"]
         LINK_REF_TEXT_OPEN:[10, 11, "["]
         MdLinkTextImpl:[11, 24, "![alt … ](/url2)"]
           MdImageLinkImpl:[11, 24, "![alt … ](/url2)"]
             IMAGE_LINK_REF_TEXT_OPEN:[11, 13, "!["]
             MdImageLinkTextImpl:[13, 16, "alt"]
               HEADER_TEXT:[13, 16, "alt"]
             IMAGE_LINK_REF_TEXT_CLOSE:[16, 17, "]"]
             IMAGE_LINK_REF_OPEN:[17, 18, "("]
             MdImageLinkRefImpl:[18, 23, "/url2"]
               IMAGE_LINK_REF:[18, 23, "/url2"]
             IMAGE_LINK_REF_CLOSE:[23, 24, ")"]
         LINK_REF_TEXT_CLOSE:[24, 25, "]"]
         LINK_REF_OPEN:[25, 26, "("]
         MdLinkRefImpl:[26, 30, "/url"]
           LINK_REF:[26, 30, "/url"]
         LINK_REF_CLOSE:[30, 31, ")"]
   WHITESPACE:[31, 32, " "]
````````````````````````````````


header image embedded

```````````````````````````````` example Link: 10
Heading [![alt](/url2)](/url) 
---
.
<h2><a href="#heading-alt" id="heading-alt"></a>Heading <a href="/url"><img src="/url2" alt="alt" /></a></h2>
.
 MdFile:[0, 34, "Headi … ng [![alt](/url2)](/url) \n---"]
   MdSetextHeaderImpl:[0, 34, "Headi … ng [![alt](/url2)](/url) \n---"]
     MdHeaderTextImpl:[0, 29, "Headi … ng [![alt](/url2)](/url)"]
       HEADER_TEXT:[0, 8, "Heading "]
       MdExplicitLinkImpl:[8, 29, "[![al … t](/url2)](/url)"]
         LINK_REF_TEXT_OPEN:[8, 9, "["]
         MdLinkTextImpl:[9, 22, "![alt … ](/url2)"]
           MdImageLinkImpl:[9, 22, "![alt … ](/url2)"]
             IMAGE_LINK_REF_TEXT_OPEN:[9, 11, "!["]
             MdImageLinkTextImpl:[11, 14, "alt"]
               HEADER_TEXT:[11, 14, "alt"]
             IMAGE_LINK_REF_TEXT_CLOSE:[14, 15, "]"]
             IMAGE_LINK_REF_OPEN:[15, 16, "("]
             MdImageLinkRefImpl:[16, 21, "/url2"]
               IMAGE_LINK_REF:[16, 21, "/url2"]
             IMAGE_LINK_REF_CLOSE:[21, 22, ")"]
         LINK_REF_TEXT_CLOSE:[22, 23, "]"]
         LINK_REF_OPEN:[23, 24, "("]
         MdLinkRefImpl:[24, 28, "/url"]
           LINK_REF:[24, 28, "/url"]
         LINK_REF_CLOSE:[28, 29, ")"]
     WHITESPACE:[29, 30, " "]
     EOL:[30, 31, "\n"]
     HEADER_SETEXT_MARKER:[31, 34, "---"]
````````````````````````````````


### LinkRef

`LinkRef`

basic

```````````````````````````````` example LinkRef: 1
[ref]: /url

[ref]
.
<p><a href="/url">ref</a></p>
.
 MdFile:[0, 18, "[ref] … : /url\n\n[ref]"]
   MdReferenceImpl:[0, 12, "[ref] … : /url\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 4, "ref"]
       REFERENCE_TEXT_LEAF:[1, 4, "ref"]
     REFERENCE_TEXT_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdReferenceLinkRefImpl:[7, 11, "/url"]
       REFERENCE_LINK_REF:[7, 11, "/url"]
     EOL:[11, 12, "\n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   MdParagraphImpl:[13, 18, "[ref]"]
     MdTextBlockImpl:[13, 18, "[ref]"]
       MdReferenceLinkImpl:[13, 18, "[ref]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[13, 14, "["]
         MdReferenceLinkReferenceImpl:[14, 17, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[14, 17, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[17, 18, "]"]
````````````````````````````````


undefined

```````````````````````````````` example LinkRef: 2
[ref2]: /url2

[ref]
.
<p>[ref]</p>
.
 MdFile:[0, 20, "[ref2 … ]: /url2\n\n[ref]"]
   MdReferenceImpl:[0, 14, "[ref2 … ]: /url2\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 5, "ref2"]
       REFERENCE_TEXT_LEAF:[1, 5, "ref2"]
     REFERENCE_TEXT_CLOSE:[5, 7, "]:"]
     WHITESPACE:[7, 8, " "]
     MdReferenceLinkRefImpl:[8, 13, "/url2"]
       REFERENCE_LINK_REF:[8, 13, "/url2"]
     EOL:[13, 14, "\n"]
   MdBlankLineImpl:[14, 15, "\n"]
     BLANK_LINE:[14, 15, "\n"]
   MdParagraphImpl:[15, 20, "[ref]"]
     MdTextBlockImpl:[15, 20, "[ref]"]
       MdReferenceLinkImpl:[15, 20, "[ref]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[15, 16, "["]
         MdReferenceLinkReferenceImpl:[16, 19, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[16, 19, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[19, 20, "]"]
````````````````````````````````


duplicate

```````````````````````````````` example LinkRef: 3
[ref]: /url1
[ref]: /url2

[ref]
.
<p><a href="/url1">ref</a></p>
.
 MdFile:[0, 32, "[ref] … : /url1\n[ref]: /url2\n\n[ref]"]
   MdReferenceImpl:[0, 13, "[ref] … : /url1\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 4, "ref"]
       REFERENCE_TEXT_LEAF:[1, 4, "ref"]
     REFERENCE_TEXT_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdReferenceLinkRefImpl:[7, 12, "/url1"]
       REFERENCE_LINK_REF:[7, 12, "/url1"]
     EOL:[12, 13, "\n"]
   MdReferenceImpl:[13, 26, "[ref] … : /url2\n"]
     REFERENCE_TEXT_OPEN:[13, 14, "["]
     MdReferenceIdentifierImpl:[14, 17, "ref"]
       REFERENCE_TEXT_LEAF:[14, 17, "ref"]
     REFERENCE_TEXT_CLOSE:[17, 19, "]:"]
     WHITESPACE:[19, 20, " "]
     MdReferenceLinkRefImpl:[20, 25, "/url2"]
       REFERENCE_LINK_REF:[20, 25, "/url2"]
     EOL:[25, 26, "\n"]
   MdBlankLineImpl:[26, 27, "\n"]
     BLANK_LINE:[26, 27, "\n"]
   MdParagraphImpl:[27, 32, "[ref]"]
     MdTextBlockImpl:[27, 32, "[ref]"]
       MdReferenceLinkImpl:[27, 32, "[ref]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[27, 28, "["]
         MdReferenceLinkReferenceImpl:[28, 31, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[28, 31, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[31, 32, "]"]
````````````````````````````````


dummy ref

```````````````````````````````` example LinkRef: 4
[ref]: /url1

[ref][]
.
<p><a href="/url1">ref</a></p>
.
 MdFile:[0, 21, "[ref] … : /url1\n\n[ref][]"]
   MdReferenceImpl:[0, 13, "[ref] … : /url1\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 4, "ref"]
       REFERENCE_TEXT_LEAF:[1, 4, "ref"]
     REFERENCE_TEXT_CLOSE:[4, 6, "]:"]
     WHITESPACE:[6, 7, " "]
     MdReferenceLinkRefImpl:[7, 12, "/url1"]
       REFERENCE_LINK_REF:[7, 12, "/url1"]
     EOL:[12, 13, "\n"]
   MdBlankLineImpl:[13, 14, "\n"]
     BLANK_LINE:[13, 14, "\n"]
   MdParagraphImpl:[14, 21, "[ref][]"]
     MdTextBlockImpl:[14, 21, "[ref][]"]
       MdReferenceLinkImpl:[14, 21, "[ref][]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[14, 15, "["]
         MdReferenceLinkReferenceImpl:[15, 18, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[15, 18, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[18, 19, "]"]
         DUMMY_REFERENCE:[19, 21, "[]"]
````````````````````````````````


### MailLink

`MailLink`

basic

```````````````````````````````` example MailLink: 1
name@url.dom
.
<p><a href="mailto:name@url.dom">name@url.dom</a></p>
.
 MdFile:[0, 12, "name@ … url.dom"]
   MdParagraphImpl:[0, 12, "name@ … url.dom"]
     MdTextBlockImpl:[0, 12, "name@ … url.dom"]
       MAIL_LINK:[0, 12, "name@ … url.dom"]
````````````````````````````````


basic leading

```````````````````````````````` example MailLink: 2
name@url.dom embedded
.
<p><a href="mailto:name@url.dom">name@url.dom</a> embedded</p>
.
 MdFile:[0, 21, "name@ … url.dom embedded"]
   MdParagraphImpl:[0, 21, "name@ … url.dom embedded"]
     MdTextBlockImpl:[0, 21, "name@ … url.dom embedded"]
       MAIL_LINK:[0, 12, "name@ … url.dom"]
       TEXT:[12, 21, " embedded"]
````````````````````````````````


basic embedded

```````````````````````````````` example MailLink: 3
text with name@url.dom embedded
.
<p>text with <a href="mailto:name@url.dom">name@url.dom</a> embedded</p>
.
 MdFile:[0, 31, "text  … with name@url.dom embedded"]
   MdParagraphImpl:[0, 31, "text  … with name@url.dom embedded"]
     MdTextBlockImpl:[0, 31, "text  … with name@url.dom embedded"]
       TEXT:[0, 10, "text with "]
       MAIL_LINK:[10, 22, "name@ … url.dom"]
       TEXT:[22, 31, " embedded"]
````````````````````````````````


basic trailing

```````````````````````````````` example MailLink: 4
text with name@url.dom
.
<p>text with <a href="mailto:name@url.dom">name@url.dom</a></p>
.
 MdFile:[0, 22, "text  … with name@url.dom"]
   MdParagraphImpl:[0, 22, "text  … with name@url.dom"]
     MdTextBlockImpl:[0, 22, "text  … with name@url.dom"]
       TEXT:[0, 10, "text with "]
       MAIL_LINK:[10, 22, "name@ … url.dom"]
````````````````````````````````


### OrderedList

`OrderedList` `OrderedListItem`

empty

```````````````````````````````` example OrderedList: 1
1. 

.
<ol>
  <li></li>
</ol>
.
 MdFile:[0, 5, "1. \n\n"]
   MdOrderedListImpl:[0, 4, "1. \n"]
     MdOrderedListItemImpl:[0, 4, "1. \n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "1. "]
       EOL:[3, 4, "\n"]
   MdBlankLineImpl:[4, 5, "\n"]
     BLANK_LINE:[4, 5, "\n"]
````````````````````````````````


empty task list

```````````````````````````````` example OrderedList: 2
1. [ ]

.
<ol>
  <li>[ ]</li>
</ol>
.
 MdFile:[0, 8, "1. [ ]\n\n"]
   MdOrderedListImpl:[0, 6, "1. [ ]"]
     MdOrderedListItemImpl:[0, 6, "1. [ ]"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "1. "]
       TASK_ITEM_MARKER:[3, 6, "[ ]"]
   EOL:[6, 7, "\n"]
   MdBlankLineImpl:[7, 8, "\n"]
     BLANK_LINE:[7, 8, "\n"]
````````````````````````````````


nested

```````````````````````````````` example OrderedList: 3
4. item 1
3. item 2
    2. item 2.1
1. item 3
.
<ol>
  <li>item 1</li>
  <li>item 2
    <ol>
      <li>item 2.1</li>
    </ol>
  </li>
  <li>item 3</li>
</ol>
.
 MdFile:[0, 45, "4. it … em 1\n3. item 2\n    2. item 2.1\n1. item 3"]
   MdOrderedListImpl:[0, 45, "4. it … em 1\n3. item 2\n    2. item 2.1\n1. item 3"]
     MdOrderedListItemImpl:[0, 10, "4. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "4. "]
       MdTextBlockImpl:[3, 10, "item 1\n"]
         TEXT:[3, 9, "item 1"]
         EOL:[9, 10, "\n"]
     MdOrderedListItemImpl:[10, 36, "3. it … em 2\n    2. item 2.1\n"]
       ORDERED_LIST_ITEM_MARKER:[10, 13, "3. "]
       MdTextBlockImpl:[13, 20, "item 2\n"]
         TEXT:[13, 19, "item 2"]
         EOL:[19, 20, "\n"]
       WHITESPACE:[20, 24, "    "]
       MdOrderedListImpl:[24, 36, "2. it … em 2.1\n"]
         MdOrderedListItemImpl:[24, 36, "2. it … em 2.1\n"]
           ORDERED_LIST_ITEM_MARKER:[24, 27, "2. "]
           MdTextBlockImpl:[27, 36, "item 2.1\n"]
             TEXT:[27, 35, "item 2.1"]
             EOL:[35, 36, "\n"]
     MdOrderedListItemImpl:[36, 45, "1. item 3"]
       ORDERED_LIST_ITEM_MARKER:[36, 39, "1. "]
       MdTextBlockImpl:[39, 45, "item 3"]
         TEXT:[39, 45, "item 3"]
````````````````````````````````


nested some loose

```````````````````````````````` example OrderedList: 4
4. item 1

3. item 2
    2. item 2.1
1. item 3
.
<ol>
  <li>
    <p>item 1</p>
  </li>
  <li>item 2
    <ol>
      <li>item 2.1</li>
    </ol>
  </li>
  <li>item 3</li>
</ol>
.
 MdFile:[0, 46, "4. it … em 1\n\n3. item 2\n    2. item 2.1\n1. item 3"]
   MdOrderedListImpl:[0, 46, "4. it … em 1\n\n3. item 2\n    2. item 2.1\n1. item 3"]
     MdOrderedListItemImpl:[0, 10, "4. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "4. "]
       MdParagraphImpl:[3, 10, "item 1\n"]
         MdTextBlockImpl:[3, 10, "item 1\n"]
           TEXT:[3, 9, "item 1"]
           EOL:[9, 10, "\n"]
     MdBlankLineImpl:[10, 11, "\n"]
       BLANK_LINE:[10, 11, "\n"]
     MdOrderedListItemImpl:[11, 37, "3. it … em 2\n    2. item 2.1\n"]
       ORDERED_LIST_ITEM_MARKER:[11, 14, "3. "]
       MdTextBlockImpl:[14, 21, "item 2\n"]
         TEXT:[14, 20, "item 2"]
         EOL:[20, 21, "\n"]
       WHITESPACE:[21, 25, "    "]
       MdOrderedListImpl:[25, 37, "2. it … em 2.1\n"]
         MdOrderedListItemImpl:[25, 37, "2. it … em 2.1\n"]
           ORDERED_LIST_ITEM_MARKER:[25, 28, "2. "]
           MdTextBlockImpl:[28, 37, "item 2.1\n"]
             TEXT:[28, 36, "item 2.1"]
             EOL:[36, 37, "\n"]
     MdOrderedListItemImpl:[37, 46, "1. item 3"]
       ORDERED_LIST_ITEM_MARKER:[37, 40, "1. "]
       MdTextBlockImpl:[40, 46, "item 3"]
         TEXT:[40, 46, "item 3"]
````````````````````````````````


nested loose

```````````````````````````````` example OrderedList: 5
4. item 1

3. item 2
    2. item 2.1
    
1. item 3
.
<ol>
  <li>
    <p>item 1</p>
  </li>
  <li>
    <p>item 2</p>
    <ol>
      <li>item 2.1</li>
    </ol>
  </li>
  <li>
    <p>item 3</p>
  </li>
</ol>
.
 MdFile:[0, 51, "4. it … em 1\n\n3. item 2\n    2. item 2.1\n    \n1. item 3"]
   MdOrderedListImpl:[0, 51, "4. it … em 1\n\n3. item 2\n    2. item 2.1\n    \n1. item 3"]
     MdOrderedListItemImpl:[0, 10, "4. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "4. "]
       MdParagraphImpl:[3, 10, "item 1\n"]
         MdTextBlockImpl:[3, 10, "item 1\n"]
           TEXT:[3, 9, "item 1"]
           EOL:[9, 10, "\n"]
     MdBlankLineImpl:[10, 11, "\n"]
       BLANK_LINE:[10, 11, "\n"]
     MdOrderedListItemImpl:[11, 37, "3. it … em 2\n    2. item 2.1\n"]
       ORDERED_LIST_ITEM_MARKER:[11, 14, "3. "]
       MdParagraphImpl:[14, 21, "item 2\n"]
         MdTextBlockImpl:[14, 21, "item 2\n"]
           TEXT:[14, 20, "item 2"]
           EOL:[20, 21, "\n"]
       WHITESPACE:[21, 25, "    "]
       MdOrderedListImpl:[25, 37, "2. it … em 2.1\n"]
         MdOrderedListItemImpl:[25, 37, "2. it … em 2.1\n"]
           ORDERED_LIST_ITEM_MARKER:[25, 28, "2. "]
           MdTextBlockImpl:[28, 37, "item 2.1\n"]
             TEXT:[28, 36, "item 2.1"]
             EOL:[36, 37, "\n"]
     MdBlankLineImpl:[37, 42, "    \n"]
       BLANK_LINE:[37, 42, "    \n"]
     MdOrderedListItemImpl:[42, 51, "1. item 3"]
       ORDERED_LIST_ITEM_MARKER:[42, 45, "1. "]
       MdParagraphImpl:[45, 51, "item 3"]
         MdTextBlockImpl:[45, 51, "item 3"]
           TEXT:[45, 51, "item 3"]
````````````````````````````````


tight nested loose

```````````````````````````````` example OrderedList: 6
4. item 1
3. item 2

    2. item 2.1
    
    3. item 2.1
1. item 3
.
<ol>
  <li>item 1</li>
  <li>item 2
    <ol>
      <li>
        <p>item 2.1</p>
      </li>
      <li>
        <p>item 2.1</p>
      </li>
    </ol>
  </li>
  <li>item 3</li>
</ol>
.
 MdFile:[0, 67, "4. it … em 1\n3. item 2\n\n    2. item 2.1\n    \n    3. item 2.1\n1. item 3"]
   MdOrderedListImpl:[0, 67, "4. it … em 1\n3. item 2\n\n    2. item 2.1\n    \n    3. item 2.1\n1. item 3"]
     MdOrderedListItemImpl:[0, 10, "4. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "4. "]
       MdTextBlockImpl:[3, 10, "item 1\n"]
         TEXT:[3, 9, "item 1"]
         EOL:[9, 10, "\n"]
     MdOrderedListItemImpl:[10, 58, "3. it … em 2\n\n    2. item 2.1\n    \n    3. item 2.1\n"]
       ORDERED_LIST_ITEM_MARKER:[10, 13, "3. "]
       MdTextBlockImpl:[13, 20, "item 2\n"]
         TEXT:[13, 19, "item 2"]
         EOL:[19, 20, "\n"]
       MdBlankLineImpl:[20, 21, "\n"]
         BLANK_LINE:[20, 21, "\n"]
       WHITESPACE:[21, 25, "    "]
       MdOrderedListImpl:[25, 58, "2. it … em 2.1\n    \n    3. item 2.1\n"]
         MdOrderedListItemImpl:[25, 37, "2. it … em 2.1\n"]
           ORDERED_LIST_ITEM_MARKER:[25, 28, "2. "]
           MdParagraphImpl:[28, 37, "item 2.1\n"]
             MdTextBlockImpl:[28, 37, "item 2.1\n"]
               TEXT:[28, 36, "item 2.1"]
               EOL:[36, 37, "\n"]
         MdBlankLineImpl:[37, 42, "    \n"]
           BLANK_LINE:[37, 42, "    \n"]
         WHITESPACE:[42, 46, "    "]
         MdOrderedListItemImpl:[46, 58, "3. it … em 2.1\n"]
           ORDERED_LIST_ITEM_MARKER:[46, 49, "3. "]
           MdParagraphImpl:[49, 58, "item 2.1\n"]
             MdTextBlockImpl:[49, 58, "item 2.1\n"]
               TEXT:[49, 57, "item 2.1"]
               EOL:[57, 58, "\n"]
     MdOrderedListItemImpl:[58, 67, "1. item 3"]
       ORDERED_LIST_ITEM_MARKER:[58, 61, "1. "]
       MdTextBlockImpl:[61, 67, "item 3"]
         TEXT:[61, 67, "item 3"]
````````````````````````````````


An ordered list after bullet list

```````````````````````````````` example OrderedList: 7
- item 1
- item 2
- [ ] item 3

2. item 1
1. item 2
5. [ ] tem 3
.
<ul>
  <li>item 1</li>
  <li>item 2</li>
  <li class="task-list-item" task-offset="21">
    <p><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;item 3</p>
  </li>
  <li>item 1</li>
  <li>item 2</li>
  <li class="task-list-item" task-offset="56"><input type="checkbox" class="task-list-item-checkbox" disabled="disabled" readonly="readonly" />&nbsp;tem 3</li>
</ul>
.
 MdFile:[0, 64, "- ite … m 1\n- item 2\n- [ ] item 3\n\n2. item 1\n1. item 2\n5. [ ] tem 3"]
   MdUnorderedListImpl:[0, 64, "- ite … m 1\n- item 2\n- [ ] item 3\n\n2. item 1\n1. item 2\n5. [ ] tem 3"]
     MdUnorderedListItemImpl:[0, 9, "- item 1\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "- "]
       MdTextBlockImpl:[2, 9, "item 1\n"]
         TEXT:[2, 8, "item 1"]
         EOL:[8, 9, "\n"]
     MdUnorderedListItemImpl:[9, 18, "- item 2\n"]
       BULLET_LIST_ITEM_MARKER:[9, 11, "- "]
       MdTextBlockImpl:[11, 18, "item 2\n"]
         TEXT:[11, 17, "item 2"]
         EOL:[17, 18, "\n"]
     MdUnorderedListItemImpl:[18, 31, "- [ ] …  item 3\n"]
       BULLET_LIST_ITEM_MARKER:[18, 20, "- "]
       TASK_ITEM_MARKER:[20, 24, "[ ] "]
       MdParagraphImpl:[24, 31, "item 3\n"]
         MdTextBlockImpl:[24, 31, "item 3\n"]
           TEXT:[24, 30, "item 3"]
           EOL:[30, 31, "\n"]
     MdBlankLineImpl:[31, 32, "\n"]
       BLANK_LINE:[31, 32, "\n"]
     MdOrderedListItemImpl:[32, 42, "2. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[32, 35, "2. "]
       MdTextBlockImpl:[35, 42, "item 1\n"]
         TEXT:[35, 41, "item 1"]
         EOL:[41, 42, "\n"]
     MdOrderedListItemImpl:[42, 52, "1. item 2\n"]
       ORDERED_LIST_ITEM_MARKER:[42, 45, "1. "]
       MdTextBlockImpl:[45, 52, "item 2\n"]
         TEXT:[45, 51, "item 2"]
         EOL:[51, 52, "\n"]
     MdOrderedListItemImpl:[52, 64, "5. [  … ] tem 3"]
       ORDERED_LIST_ITEM_MARKER:[52, 55, "5. "]
       TASK_ITEM_MARKER:[55, 59, "[ ] "]
       MdTextBlockImpl:[59, 64, "tem 3"]
         TEXT:[59, 64, "tem 3"]
````````````````````````````````


no parens delimiter

```````````````````````````````` example OrderedList: 8
2. item 1
1. item 2

3) item 3
.
<ol>
  <li>item 1</li>
  <li>item 2</li>
</ol>
<p>3) item 3</p>
.
 MdFile:[0, 30, "2. it … em 1\n1. item 2\n\n3) item 3"]
   MdOrderedListImpl:[0, 20, "2. it … em 1\n1. item 2\n"]
     MdOrderedListItemImpl:[0, 10, "2. item 1\n"]
       ORDERED_LIST_ITEM_MARKER:[0, 3, "2. "]
       MdTextBlockImpl:[3, 10, "item 1\n"]
         TEXT:[3, 9, "item 1"]
         EOL:[9, 10, "\n"]
     MdOrderedListItemImpl:[10, 20, "1. item 2\n"]
       ORDERED_LIST_ITEM_MARKER:[10, 13, "1. "]
       MdTextBlockImpl:[13, 20, "item 2\n"]
         TEXT:[13, 19, "item 2"]
         EOL:[19, 20, "\n"]
   MdBlankLineImpl:[20, 21, "\n"]
     BLANK_LINE:[20, 21, "\n"]
   MdParagraphImpl:[21, 30, "3) item 3"]
     MdTextBlockImpl:[21, 30, "3) item 3"]
       TEXT:[21, 30, "3) item 3"]
````````````````````````````````


### Paragraph

`Paragraph` `Text` `TextBase`

Plain text should return the whole input

```````````````````````````````` example Paragraph: 1
First line
Second line
Last line
.
<p>First line
Second line
Last line</p>
.
 MdFile:[0, 32, "First …  line\nSecond line\nLast line"]
   MdParagraphImpl:[0, 32, "First …  line\nSecond line\nLast line"]
     MdTextBlockImpl:[0, 32, "First …  line\nSecond line\nLast line"]
       TEXT:[0, 10, "First line"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 22, "Secon … d line"]
       EOL:[22, 23, "\n"]
       TEXT:[23, 32, "Last line"]
````````````````````````````````


Item text in a tight list should have no para wrapper.

```````````````````````````````` example Paragraph: 2
* first item 
* first item 
.
<ul>
  <li>first item</li>
  <li>first item</li>
</ul>
.
 MdFile:[0, 27, "* fir … st item \n* first item "]
   MdUnorderedListImpl:[0, 27, "* fir … st item \n* first item "]
     MdUnorderedListItemImpl:[0, 14, "* fir … st item \n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
       MdTextBlockImpl:[2, 14, "first …  item \n"]
         TEXT:[2, 12, "first item"]
         EOL:[12, 14, " \n"]
     MdUnorderedListItemImpl:[14, 27, "* fir … st item "]
       BULLET_LIST_ITEM_MARKER:[14, 16, "* "]
       MdTextBlockImpl:[16, 27, "first …  item "]
         TEXT:[16, 26, "first item"]
         WHITESPACE:[26, 27, " "]
````````````````````````````````


Paragraphs Following the item text should have paragraph wrappers

```````````````````````````````` example Paragraph: 3
* first item 
    
    Para wrapped
    
    Para wrapped
* first item 
.
<ul>
  <li>first item
    <p>Para wrapped</p>
    <p>Para wrapped</p>
  </li>
  <li>first item</li>
</ul>
.
 MdFile:[0, 71, "* fir … st item \n    \n    Para wrapped\n    \n    Para wrapped\n* first item "]
   MdUnorderedListImpl:[0, 71, "* fir … st item \n    \n    Para wrapped\n    \n    Para wrapped\n* first item "]
     MdUnorderedListItemImpl:[0, 58, "* fir … st item \n    \n    Para wrapped\n    \n    Para wrapped\n"]
       BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
       MdTextBlockImpl:[2, 14, "first …  item \n"]
         TEXT:[2, 12, "first item"]
         EOL:[12, 14, " \n"]
       MdBlankLineImpl:[14, 19, "    \n"]
         BLANK_LINE:[14, 19, "    \n"]
       WHITESPACE:[19, 23, "    "]
       MdParagraphImpl:[23, 36, "Para  … wrapped\n"]
         MdTextBlockImpl:[23, 36, "Para  … wrapped\n"]
           TEXT:[23, 35, "Para  … wrapped"]
           EOL:[35, 36, "\n"]
       MdBlankLineImpl:[36, 41, "    \n"]
         BLANK_LINE:[36, 41, "    \n"]
       WHITESPACE:[41, 45, "    "]
       MdParagraphImpl:[45, 58, "Para  … wrapped\n"]
         MdTextBlockImpl:[45, 58, "Para  … wrapped\n"]
           TEXT:[45, 57, "Para  … wrapped"]
           EOL:[57, 58, "\n"]
     MdUnorderedListItemImpl:[58, 71, "* fir … st item "]
       BULLET_LIST_ITEM_MARKER:[58, 60, "* "]
       MdTextBlockImpl:[60, 71, "first …  item "]
         TEXT:[60, 70, "first item"]
         WHITESPACE:[70, 71, " "]
````````````````````````````````


### Reference

`Reference`

```````````````````````````````` example Reference: 1
[url1]: /url1
[url2]: /url2
.
.
 MdFile:[0, 27, "[url1 … ]: /url1\n[url2]: /url2"]
   MdReferenceImpl:[0, 14, "[url1 … ]: /url1\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 5, "url1"]
       REFERENCE_TEXT_LEAF:[1, 5, "url1"]
     REFERENCE_TEXT_CLOSE:[5, 7, "]:"]
     WHITESPACE:[7, 8, " "]
     MdReferenceLinkRefImpl:[8, 13, "/url1"]
       REFERENCE_LINK_REF:[8, 13, "/url1"]
     EOL:[13, 14, "\n"]
   MdReferenceImpl:[14, 27, "[url2 … ]: /url2"]
     REFERENCE_TEXT_OPEN:[14, 15, "["]
     MdReferenceIdentifierImpl:[15, 19, "url2"]
       REFERENCE_TEXT_LEAF:[15, 19, "url2"]
     REFERENCE_TEXT_CLOSE:[19, 21, "]:"]
     WHITESPACE:[21, 22, " "]
     MdReferenceLinkRefImpl:[22, 27, "/url2"]
       REFERENCE_LINK_REF:[22, 27, "/url2"]
````````````````````````````````


Footnote looking references with footnotes disabled

```````````````````````````````` example(Reference: 2) options(no-footnotes)
[^url1]: /url1
[^url2]: /url2
.
.
 MdFile:[0, 29, "[^url … 1]: /url1\n[^url2]: /url2"]
   MdReferenceImpl:[0, 15, "[^url … 1]: /url1\n"]
     REFERENCE_TEXT_OPEN:[0, 1, "["]
     MdReferenceIdentifierImpl:[1, 6, "^url1"]
       REFERENCE_TEXT_LEAF:[1, 6, "^url1"]
     REFERENCE_TEXT_CLOSE:[6, 8, "]:"]
     WHITESPACE:[8, 9, " "]
     MdReferenceLinkRefImpl:[9, 14, "/url1"]
       REFERENCE_LINK_REF:[9, 14, "/url1"]
     EOL:[14, 15, "\n"]
   MdReferenceImpl:[15, 29, "[^url … 2]: /url2"]
     REFERENCE_TEXT_OPEN:[15, 16, "["]
     MdReferenceIdentifierImpl:[16, 21, "^url2"]
       REFERENCE_TEXT_LEAF:[16, 21, "^url2"]
     REFERENCE_TEXT_CLOSE:[21, 23, "]:"]
     WHITESPACE:[23, 24, " "]
     MdReferenceLinkRefImpl:[24, 29, "/url2"]
       REFERENCE_LINK_REF:[24, 29, "/url2"]
````````````````````````````````


Footnote looking references with footnotes disabled

```````````````````````````````` example(Reference: 3) options(no-footnotes)
this is a footnote[^]. And this is another footnote[^A].

this is an undefined footnote [^undef]

[^]: undefined

.
<p>this is a footnote<a href="undefined">^</a>. And this is another footnote[^A].</p>
<p>this is an undefined footnote [^undef]</p>
.
 MdFile:[0, 114, "this  … is a footnote[^]. And this is another footnote[^A].\n\nthis is an undefined footnote [^undef]\n\n[^]: undefined\n\n"]
   MdParagraphImpl:[0, 57, "this  … is a footnote[^]. And this is another footnote[^A].\n"]
     MdTextBlockImpl:[0, 57, "this  … is a footnote[^]. And this is another footnote[^A].\n"]
       TEXT:[0, 18, "this  … is a footnote"]
       MdReferenceLinkImpl:[18, 21, "[^]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[18, 19, "["]
         MdReferenceLinkReferenceImpl:[19, 20, "^"]
           REFERENCE_LINK_REFERENCE_LEAF:[19, 20, "^"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[20, 21, "]"]
       TEXT:[21, 51, ". And …  this is another footnote"]
       MdReferenceLinkImpl:[51, 55, "[^A]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[51, 52, "["]
         MdReferenceLinkReferenceImpl:[52, 54, "^A"]
           REFERENCE_LINK_REFERENCE_LEAF:[52, 54, "^A"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[54, 55, "]"]
       TEXT:[55, 56, "."]
       EOL:[56, 57, "\n"]
   MdBlankLineImpl:[57, 58, "\n"]
     BLANK_LINE:[57, 58, "\n"]
   MdParagraphImpl:[58, 97, "this  … is an undefined footnote [^undef]\n"]
     MdTextBlockImpl:[58, 97, "this  … is an undefined footnote [^undef]\n"]
       TEXT:[58, 88, "this  … is an undefined footnote "]
       MdReferenceLinkImpl:[88, 96, "[^undef]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[88, 89, "["]
         MdReferenceLinkReferenceImpl:[89, 95, "^undef"]
           REFERENCE_LINK_REFERENCE_LEAF:[89, 95, "^undef"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[95, 96, "]"]
       EOL:[96, 97, "\n"]
   MdBlankLineImpl:[97, 98, "\n"]
     BLANK_LINE:[97, 98, "\n"]
   MdReferenceImpl:[98, 113, "[^]:  … undefined\n"]
     REFERENCE_TEXT_OPEN:[98, 99, "["]
     MdReferenceIdentifierImpl:[99, 100, "^"]
       REFERENCE_TEXT_LEAF:[99, 100, "^"]
     REFERENCE_TEXT_CLOSE:[100, 102, "]:"]
     WHITESPACE:[102, 103, " "]
     MdReferenceLinkRefImpl:[103, 112, "undefined"]
       REFERENCE_LINK_REF:[103, 112, "undefined"]
     EOL:[112, 113, "\n"]
   MdBlankLineImpl:[113, 114, "\n"]
     BLANK_LINE:[113, 114, "\n"]
````````````````````````````````


### SoftLineBreak

`SoftLineBreak`

```````````````````````````````` example SoftLineBreak: 1
line 1
line 2
line 3
.
<p>line 1
line 2
line 3</p>
.
 MdFile:[0, 20, "line  … 1\nline 2\nline 3"]
   MdParagraphImpl:[0, 20, "line  … 1\nline 2\nline 3"]
     MdTextBlockImpl:[0, 20, "line  … 1\nline 2\nline 3"]
       TEXT:[0, 6, "line 1"]
       EOL:[6, 7, "\n"]
       TEXT:[7, 13, "line 2"]
       EOL:[13, 14, "\n"]
       TEXT:[14, 20, "line 3"]
````````````````````````````````


### Strikethrough

`Strikethrough`

basic

```````````````````````````````` example Strikethrough: 1
~italic~
text ~italic~ embedded 
~italic~ embedded 
text ~italic~ 
.
<p>~italic~
text ~italic~ embedded
~italic~ embedded
text ~italic~</p>
.
 MdFile:[0, 66, "~ital … ic~\ntext ~italic~ embedded \n~italic~ embedded \ntext ~italic~ "]
   MdParagraphImpl:[0, 66, "~ital … ic~\ntext ~italic~ embedded \n~italic~ embedded \ntext ~italic~ "]
     MdTextBlockImpl:[0, 66, "~ital … ic~\ntext ~italic~ embedded \n~italic~ embedded \ntext ~italic~ "]
       TEXT:[0, 8, "~italic~"]
       EOL:[8, 9, "\n"]
       TEXT:[9, 31, "text  … ~italic~ embedded"]
       EOL:[31, 33, " \n"]
       TEXT:[33, 50, "~ital … ic~ embedded"]
       EOL:[50, 52, " \n"]
       TEXT:[52, 65, "text  … ~italic~"]
       WHITESPACE:[65, 66, " "]
````````````````````````````````


### StrongEmphasis

`StrongEmphasis`

basic

```````````````````````````````` example StrongEmphasis: 1
**italic**
text **italic** embedded 
**italic** embedded 
text **italic** 
.
<p><strong>italic</strong>
text <strong>italic</strong> embedded
<strong>italic</strong> embedded
text <strong>italic</strong></p>
.
 MdFile:[0, 74, "**ita … lic**\ntext **italic** embedded \n**italic** embedded \ntext **italic** "]
   MdParagraphImpl:[0, 74, "**ita … lic**\ntext **italic** embedded \n**italic** embedded \ntext **italic** "]
     MdTextBlockImpl:[0, 74, "**ita … lic**\ntext **italic** embedded \n**italic** embedded \ntext **italic** "]
       MdInlineBoldImpl:[0, 10, "**italic**"]
         BOLD_MARKER:[0, 2, "**"]
         BOLD_TEXT:[2, 8, "italic"]
         BOLD_MARKER:[8, 10, "**"]
       EOL:[10, 11, "\n"]
       TEXT:[11, 16, "text "]
       MdInlineBoldImpl:[16, 26, "**italic**"]
         BOLD_MARKER:[16, 18, "**"]
         BOLD_TEXT:[18, 24, "italic"]
         BOLD_MARKER:[24, 26, "**"]
       TEXT:[26, 35, " embedded"]
       EOL:[35, 37, " \n"]
       MdInlineBoldImpl:[37, 47, "**italic**"]
         BOLD_MARKER:[37, 39, "**"]
         BOLD_TEXT:[39, 45, "italic"]
         BOLD_MARKER:[45, 47, "**"]
       TEXT:[47, 56, " embedded"]
       EOL:[56, 58, " \n"]
       TEXT:[58, 63, "text "]
       MdInlineBoldImpl:[63, 73, "**italic**"]
         BOLD_MARKER:[63, 65, "**"]
         BOLD_TEXT:[65, 71, "italic"]
         BOLD_MARKER:[71, 73, "**"]
       WHITESPACE:[73, 74, " "]
````````````````````````````````


### TableBlock

`TableBlock` `TableBody` `TableCaption` `TableCell` `TableHead` `TableRow` `TableSeparator`

```````````````````````````````` example TableBlock: 1
Abc|Def
---|---
1|2
table, you are over
.
<table>
  <thead>
    <tr><th>Abc</th><th>Def</th></tr>
  </thead>
  <tbody>
    <tr><td>1</td><td>2</td></tr>
  </tbody>
</table>
<p>table, you are over</p>
.
 MdFile:[0, 39, "Abc|D … ef\n---|---\n1|2\ntable, you are over"]
   MdTableImpl:[0, 20, "Abc|D … ef\n---|---\n1|2\n"]
     MdTableHeaderImpl:[0, 8, "Abc|Def\n"]
       MdTableRowImpl:[0, 8, "Abc|Def\n"]
         MdTableCellImpl:[0, 3, "Abc"]
           TABLE_HDR_CELL_RODD_CODD:[0, 3, "Abc"]
         TABLE_HDR_ROW_ODD:[3, 4, "|"]
         MdTableCellImpl:[4, 7, "Def"]
           TABLE_HDR_CELL_RODD_CEVEN:[4, 7, "Def"]
         EOL:[7, 8, "\n"]
     MdTableSeparatorImpl:[8, 16, "---|---\n"]
       MdTableCellImpl:[8, 11, "---"]
         TABLE_SEP_COLUMN_ODD:[8, 11, "---"]
       TABLE_SEP_ROW_ODD:[11, 12, "|"]
       MdTableCellImpl:[12, 15, "---"]
         TABLE_SEP_COLUMN_EVEN:[12, 15, "---"]
       EOL:[15, 16, "\n"]
     MdTableBodyImpl:[16, 20, "1|2\n"]
       MdTableRowImpl:[16, 20, "1|2\n"]
         MdTableCellImpl:[16, 17, "1"]
           TABLE_CELL_RODD_CODD:[16, 17, "1"]
         TABLE_ROW_ODD:[17, 18, "|"]
         MdTableCellImpl:[18, 19, "2"]
           TABLE_CELL_RODD_CEVEN:[18, 19, "2"]
         EOL:[19, 20, "\n"]
   MdParagraphImpl:[20, 39, "table … , you are over"]
     MdTextBlockImpl:[20, 39, "table … , you are over"]
       TEXT:[20, 39, "table … , you are over"]
````````````````````````````````


inlines should be processed

```````````````````````````````` example TableBlock: 2
|**Abc** **test** |_Def_ _Def_
---|---
[ref]|`code` `code`
table, you are over

[ref]: /url
.
<table>
  <thead>
    <tr><th><strong>Abc</strong> <strong>test</strong> </th><th><em>Def</em> <em>Def</em></th></tr>
  </thead>
  <tbody>
    <tr><td><a href="/url">ref</a></td><td><code>code</code> <code>code</code></td></tr>
  </tbody>
</table>
<p>table, you are over</p>
.
 MdFile:[0, 91, "|**Ab … c** **test** |_Def_ _Def_\n---|---\n[ref]|`code` `code`\ntable, you are over\n\n[ref]: /url"]
   MdTableImpl:[0, 59, "|**Ab … c** **test** |_Def_ _Def_\n---|---\n[ref]|`code` `code`\n"]
     MdTableHeaderImpl:[0, 31, "|**Ab … c** **test** |_Def_ _Def_\n"]
       MdTableRowImpl:[0, 31, "|**Ab … c** **test** |_Def_ _Def_\n"]
         TABLE_HDR_ROW_ODD:[0, 1, "|"]
         MdTableCellImpl:[1, 18, "**Abc … ** **test** "]
           MdInlineBoldImpl:[1, 8, "**Abc**"]
             TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER:[1, 3, "**"]
             TABLE_HDR_CELL_RODD_CODD_BOLD_TEXT:[3, 6, "Abc"]
             TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER:[6, 8, "**"]
           TABLE_HDR_CELL_RODD_CODD:[8, 9, " "]
           MdInlineBoldImpl:[9, 17, "**test**"]
             TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER:[9, 11, "**"]
             TABLE_HDR_CELL_RODD_CODD_BOLD_TEXT:[11, 15, "test"]
             TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER:[15, 17, "**"]
           TABLE_HDR_CELL_RODD_CODD:[17, 18, " "]
         TABLE_HDR_ROW_ODD:[18, 19, "|"]
         MdTableCellImpl:[19, 30, "_Def_ …  _Def_"]
           MdInlineItalicImpl:[19, 24, "_Def_"]
             TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER:[19, 20, "_"]
             TABLE_HDR_CELL_RODD_CEVEN_ITALIC_TEXT:[20, 23, "Def"]
             TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER:[23, 24, "_"]
           TABLE_HDR_CELL_RODD_CEVEN:[24, 25, " "]
           MdInlineItalicImpl:[25, 30, "_Def_"]
             TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER:[25, 26, "_"]
             TABLE_HDR_CELL_RODD_CEVEN_ITALIC_TEXT:[26, 29, "Def"]
             TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER:[29, 30, "_"]
         EOL:[30, 31, "\n"]
     MdTableSeparatorImpl:[31, 39, "---|---\n"]
       MdTableCellImpl:[31, 34, "---"]
         TABLE_SEP_COLUMN_ODD:[31, 34, "---"]
       TABLE_SEP_ROW_ODD:[34, 35, "|"]
       MdTableCellImpl:[35, 38, "---"]
         TABLE_SEP_COLUMN_EVEN:[35, 38, "---"]
       EOL:[38, 39, "\n"]
     MdTableBodyImpl:[39, 59, "[ref] … |`code` `code`\n"]
       MdTableRowImpl:[39, 59, "[ref] … |`code` `code`\n"]
         MdTableCellImpl:[39, 44, "[ref]"]
           MdReferenceLinkImpl:[39, 44, "[ref]"]
             TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_OPEN2:[39, 40, "["]
             MdReferenceLinkReferenceImpl:[40, 43, "ref"]
               REFERENCE_LINK_REFERENCE_LEAF:[40, 43, "ref"]
             TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_CLOSE2:[43, 44, "]"]
         TABLE_ROW_ODD:[44, 45, "|"]
         MdTableCellImpl:[45, 58, "`code … ` `code`"]
           MdInlineCodeImpl:[45, 51, "`code`"]
             CODE_MARKER:[45, 46, "`"]
             TABLE_CELL_RODD_CEVEN_CODE_TEXT:[46, 50, "code"]
             CODE_MARKER:[50, 51, "`"]
           TABLE_CELL_RODD_CEVEN:[51, 52, " "]
           MdInlineCodeImpl:[52, 58, "`code`"]
             CODE_MARKER:[52, 53, "`"]
             TABLE_CELL_RODD_CEVEN_CODE_TEXT:[53, 57, "code"]
             CODE_MARKER:[57, 58, "`"]
         EOL:[58, 59, "\n"]
   MdParagraphImpl:[59, 79, "table … , you are over\n"]
     MdTextBlockImpl:[59, 79, "table … , you are over\n"]
       TEXT:[59, 78, "table … , you are over"]
       EOL:[78, 79, "\n"]
   MdBlankLineImpl:[79, 80, "\n"]
     BLANK_LINE:[79, 80, "\n"]
   MdReferenceImpl:[80, 91, "[ref] … : /url"]
     REFERENCE_TEXT_OPEN:[80, 81, "["]
     MdReferenceIdentifierImpl:[81, 84, "ref"]
       REFERENCE_TEXT_LEAF:[81, 84, "ref"]
     REFERENCE_TEXT_CLOSE:[84, 86, "]:"]
     WHITESPACE:[86, 87, " "]
     MdReferenceLinkRefImpl:[87, 91, "/url"]
       REFERENCE_LINK_REF:[87, 91, "/url"]
````````````````````````````````


Column spans are created with repeated | pipes one for each additional column to span

```````````````````````````````` example TableBlock: 3
|Abc|Def
|---|---|
| span ||
.
<table>
  <thead>
    <tr><th>Abc</th><th>Def</th></tr>
  </thead>
  <tbody>
    <tr><td colspan="2"> span </td></tr>
  </tbody>
</table>
.
 MdFile:[0, 28, "|Abc| … Def\n|---|---|\n| span ||"]
   MdTableImpl:[0, 28, "|Abc| … Def\n|---|---|\n| span ||"]
     MdTableHeaderImpl:[0, 9, "|Abc|Def\n"]
       MdTableRowImpl:[0, 9, "|Abc|Def\n"]
         TABLE_HDR_ROW_ODD:[0, 1, "|"]
         MdTableCellImpl:[1, 4, "Abc"]
           TABLE_HDR_CELL_RODD_CODD:[1, 4, "Abc"]
         TABLE_HDR_ROW_ODD:[4, 5, "|"]
         MdTableCellImpl:[5, 8, "Def"]
           TABLE_HDR_CELL_RODD_CEVEN:[5, 8, "Def"]
         EOL:[8, 9, "\n"]
     MdTableSeparatorImpl:[9, 19, "|---|---|\n"]
       TABLE_SEP_ROW_ODD:[9, 10, "|"]
       MdTableCellImpl:[10, 13, "---"]
         TABLE_SEP_COLUMN_ODD:[10, 13, "---"]
       TABLE_SEP_ROW_ODD:[13, 14, "|"]
       MdTableCellImpl:[14, 17, "---"]
         TABLE_SEP_COLUMN_EVEN:[14, 17, "---"]
       TABLE_SEP_ROW_ODD:[17, 18, "|"]
       EOL:[18, 19, "\n"]
     MdTableBodyImpl:[19, 28, "| span ||"]
       MdTableRowImpl:[19, 28, "| span ||"]
         TABLE_ROW_ODD:[19, 20, "|"]
         MdTableCellImpl:[20, 26, " span "]
           TABLE_CELL_RODD_CODD:[20, 26, " span "]
         TABLE_ROW_ODD:[26, 28, "||"]
````````````````````````````````


Now we try varying the header lines and make sure we get the right output

```````````````````````````````` example TableBlock: 4
|Abc|Def
|Hij|Lmn
|---|---|
| span ||
.
<table>
  <thead>
    <tr><th>Abc</th><th>Def</th></tr>
    <tr><th>Hij</th><th>Lmn</th></tr>
  </thead>
  <tbody>
    <tr><td colspan="2"> span </td></tr>
  </tbody>
</table>
.
 MdFile:[0, 37, "|Abc| … Def\n|Hij|Lmn\n|---|---|\n| span ||"]
   MdTableImpl:[0, 37, "|Abc| … Def\n|Hij|Lmn\n|---|---|\n| span ||"]
     MdTableHeaderImpl:[0, 18, "|Abc| … Def\n|Hij|Lmn\n"]
       MdTableRowImpl:[0, 9, "|Abc|Def\n"]
         TABLE_HDR_ROW_ODD:[0, 1, "|"]
         MdTableCellImpl:[1, 4, "Abc"]
           TABLE_HDR_CELL_RODD_CODD:[1, 4, "Abc"]
         TABLE_HDR_ROW_ODD:[4, 5, "|"]
         MdTableCellImpl:[5, 8, "Def"]
           TABLE_HDR_CELL_RODD_CEVEN:[5, 8, "Def"]
         EOL:[8, 9, "\n"]
       MdTableRowImpl:[9, 18, "|Hij|Lmn\n"]
         TABLE_HDR_ROW_EVEN:[9, 10, "|"]
         MdTableCellImpl:[10, 13, "Hij"]
           TABLE_HDR_CELL_REVEN_CODD:[10, 13, "Hij"]
         TABLE_HDR_ROW_EVEN:[13, 14, "|"]
         MdTableCellImpl:[14, 17, "Lmn"]
           TABLE_HDR_CELL_REVEN_CEVEN:[14, 17, "Lmn"]
         EOL:[17, 18, "\n"]
     MdTableSeparatorImpl:[18, 28, "|---|---|\n"]
       TABLE_SEP_ROW_ODD:[18, 19, "|"]
       MdTableCellImpl:[19, 22, "---"]
         TABLE_SEP_COLUMN_ODD:[19, 22, "---"]
       TABLE_SEP_ROW_ODD:[22, 23, "|"]
       MdTableCellImpl:[23, 26, "---"]
         TABLE_SEP_COLUMN_EVEN:[23, 26, "---"]
       TABLE_SEP_ROW_ODD:[26, 27, "|"]
       EOL:[27, 28, "\n"]
     MdTableBodyImpl:[28, 37, "| span ||"]
       MdTableRowImpl:[28, 37, "| span ||"]
         TABLE_ROW_ODD:[28, 29, "|"]
         MdTableCellImpl:[29, 35, " span "]
           TABLE_CELL_RODD_CODD:[29, 35, " span "]
         TABLE_ROW_ODD:[35, 37, "||"]
````````````````````````````````


No header lines

```````````````````````````````` example TableBlock: 5
|---|---|
| col1 | col2|
.
<table>
  <thead></thead>
  <tbody>
    <tr><td> col1 </td><td> col2</td></tr>
  </tbody>
</table>
.
 MdFile:[0, 24, "|---| … ---|\n| col1 | col2|"]
   MdTableImpl:[0, 24, "|---| … ---|\n| col1 | col2|"]
     MdTableHeaderImpl:[0, 0]
     MdTableSeparatorImpl:[0, 10, "|---|---|\n"]
       TABLE_SEP_ROW_ODD:[0, 1, "|"]
       MdTableCellImpl:[1, 4, "---"]
         TABLE_SEP_COLUMN_ODD:[1, 4, "---"]
       TABLE_SEP_ROW_ODD:[4, 5, "|"]
       MdTableCellImpl:[5, 8, "---"]
         TABLE_SEP_COLUMN_EVEN:[5, 8, "---"]
       TABLE_SEP_ROW_ODD:[8, 9, "|"]
       EOL:[9, 10, "\n"]
     MdTableBodyImpl:[10, 24, "| col … 1 | col2|"]
       MdTableRowImpl:[10, 24, "| col … 1 | col2|"]
         TABLE_ROW_ODD:[10, 11, "|"]
         MdTableCellImpl:[11, 17, " col1 "]
           TABLE_CELL_RODD_CODD:[11, 17, " col1 "]
         TABLE_ROW_ODD:[17, 18, "|"]
         MdTableCellImpl:[18, 23, " col2"]
           TABLE_CELL_RODD_CEVEN:[18, 23, " col2"]
         TABLE_ROW_ODD:[23, 24, "|"]
````````````````````````````````


No body lines

```````````````````````````````` example TableBlock: 6
| col1 | col2|
|---|---|
.
<table>
  <thead>
    <tr><th> col1 </th><th> col2</th></tr>
  </thead>
  <tbody></tbody>
</table>
.
 MdFile:[0, 24, "| col … 1 | col2|\n|---|---|"]
   MdTableImpl:[0, 24, "| col … 1 | col2|\n|---|---|"]
     MdTableHeaderImpl:[0, 15, "| col … 1 | col2|\n"]
       MdTableRowImpl:[0, 15, "| col … 1 | col2|\n"]
         TABLE_HDR_ROW_ODD:[0, 1, "|"]
         MdTableCellImpl:[1, 7, " col1 "]
           TABLE_HDR_CELL_RODD_CODD:[1, 7, " col1 "]
         TABLE_HDR_ROW_ODD:[7, 8, "|"]
         MdTableCellImpl:[8, 13, " col2"]
           TABLE_HDR_CELL_RODD_CEVEN:[8, 13, " col2"]
         TABLE_HDR_ROW_ODD:[13, 14, "|"]
         EOL:[14, 15, "\n"]
     MdTableSeparatorImpl:[15, 24, "|---|---|"]
       TABLE_SEP_ROW_ODD:[15, 16, "|"]
       MdTableCellImpl:[16, 19, "---"]
         TABLE_SEP_COLUMN_ODD:[16, 19, "---"]
       TABLE_SEP_ROW_ODD:[19, 20, "|"]
       MdTableCellImpl:[20, 23, "---"]
         TABLE_SEP_COLUMN_EVEN:[20, 23, "---"]
       TABLE_SEP_ROW_ODD:[23, 24, "|"]
     MdTableBodyImpl:[24, 24]
````````````````````````````````


multiple tables parsed correctly

```````````````````````````````` example TableBlock: 7
not a table, followed by a table

| col1 | col2|
|---|---|

| col1 | col2|
|---|---|
| data1 | data2|

not a table, followed by a table

| col11 | col12|
| col21 | col22|
|---|---|
| data1 | data2|

.
<p>not a table, followed by a table</p>
<table>
  <thead>
    <tr><th> col1 </th><th> col2</th></tr>
  </thead>
  <tbody></tbody>
</table>
<table>
  <thead>
    <tr><th> col1 </th><th> col2</th></tr>
  </thead>
  <tbody>
    <tr><td> data1 </td><td> data2</td></tr>
  </tbody>
</table>
<p>not a table, followed by a table</p>
<table>
  <thead>
    <tr><th> col11 </th><th> col12</th></tr>
    <tr><th> col21 </th><th> col22</th></tr>
  </thead>
  <tbody>
    <tr><td> data1 </td><td> data2</td></tr>
  </tbody>
</table>
.
 MdFile:[0, 199, "not a …  table, followed by a table\n\n| col1 | col2|\n|---|---|\n\n| col1 | col2|\n|---|---|\n| data1 | data2|\n\nnot a table, followed by a table\n\n| col11 | col12|\n| col21 | col22|\n|---|---|\n| data1 | data2|\n\n"]
   MdParagraphImpl:[0, 33, "not a …  table, followed by a table\n"]
     MdTextBlockImpl:[0, 33, "not a …  table, followed by a table\n"]
       TEXT:[0, 32, "not a …  table, followed by a table"]
       EOL:[32, 33, "\n"]
   MdBlankLineImpl:[33, 34, "\n"]
     BLANK_LINE:[33, 34, "\n"]
   MdTableImpl:[34, 59, "| col … 1 | col2|\n|---|---|\n"]
     MdTableHeaderImpl:[34, 49, "| col … 1 | col2|\n"]
       MdTableRowImpl:[34, 49, "| col … 1 | col2|\n"]
         TABLE_HDR_ROW_ODD:[34, 35, "|"]
         MdTableCellImpl:[35, 41, " col1 "]
           TABLE_HDR_CELL_RODD_CODD:[35, 41, " col1 "]
         TABLE_HDR_ROW_ODD:[41, 42, "|"]
         MdTableCellImpl:[42, 47, " col2"]
           TABLE_HDR_CELL_RODD_CEVEN:[42, 47, " col2"]
         TABLE_HDR_ROW_ODD:[47, 48, "|"]
         EOL:[48, 49, "\n"]
     MdTableSeparatorImpl:[49, 59, "|---|---|\n"]
       TABLE_SEP_ROW_ODD:[49, 50, "|"]
       MdTableCellImpl:[50, 53, "---"]
         TABLE_SEP_COLUMN_ODD:[50, 53, "---"]
       TABLE_SEP_ROW_ODD:[53, 54, "|"]
       MdTableCellImpl:[54, 57, "---"]
         TABLE_SEP_COLUMN_EVEN:[54, 57, "---"]
       TABLE_SEP_ROW_ODD:[57, 58, "|"]
       EOL:[58, 59, "\n"]
     MdTableBodyImpl:[59, 59]
   MdBlankLineImpl:[59, 60, "\n"]
     BLANK_LINE:[59, 60, "\n"]
   MdTableImpl:[60, 102, "| col … 1 | col2|\n|---|---|\n| data1 | data2|\n"]
     MdTableHeaderImpl:[60, 75, "| col … 1 | col2|\n"]
       MdTableRowImpl:[60, 75, "| col … 1 | col2|\n"]
         TABLE_HDR_ROW_ODD:[60, 61, "|"]
         MdTableCellImpl:[61, 67, " col1 "]
           TABLE_HDR_CELL_RODD_CODD:[61, 67, " col1 "]
         TABLE_HDR_ROW_ODD:[67, 68, "|"]
         MdTableCellImpl:[68, 73, " col2"]
           TABLE_HDR_CELL_RODD_CEVEN:[68, 73, " col2"]
         TABLE_HDR_ROW_ODD:[73, 74, "|"]
         EOL:[74, 75, "\n"]
     MdTableSeparatorImpl:[75, 85, "|---|---|\n"]
       TABLE_SEP_ROW_ODD:[75, 76, "|"]
       MdTableCellImpl:[76, 79, "---"]
         TABLE_SEP_COLUMN_ODD:[76, 79, "---"]
       TABLE_SEP_ROW_ODD:[79, 80, "|"]
       MdTableCellImpl:[80, 83, "---"]
         TABLE_SEP_COLUMN_EVEN:[80, 83, "---"]
       TABLE_SEP_ROW_ODD:[83, 84, "|"]
       EOL:[84, 85, "\n"]
     MdTableBodyImpl:[85, 102, "| dat … a1 | data2|\n"]
       MdTableRowImpl:[85, 102, "| dat … a1 | data2|\n"]
         TABLE_ROW_ODD:[85, 86, "|"]
         MdTableCellImpl:[86, 93, " data1 "]
           TABLE_CELL_RODD_CODD:[86, 93, " data1 "]
         TABLE_ROW_ODD:[93, 94, "|"]
         MdTableCellImpl:[94, 100, " data2"]
           TABLE_CELL_RODD_CEVEN:[94, 100, " data2"]
         TABLE_ROW_ODD:[100, 101, "|"]
         EOL:[101, 102, "\n"]
   MdBlankLineImpl:[102, 103, "\n"]
     BLANK_LINE:[102, 103, "\n"]
   MdParagraphImpl:[103, 136, "not a …  table, followed by a table\n"]
     MdTextBlockImpl:[103, 136, "not a …  table, followed by a table\n"]
       TEXT:[103, 135, "not a …  table, followed by a table"]
       EOL:[135, 136, "\n"]
   MdBlankLineImpl:[136, 137, "\n"]
     BLANK_LINE:[136, 137, "\n"]
   MdTableImpl:[137, 198, "| col … 11 | col12|\n| col21 | col22|\n|---|---|\n| data1 | data2|\n"]
     MdTableHeaderImpl:[137, 171, "| col … 11 | col12|\n| col21 | col22|\n"]
       MdTableRowImpl:[137, 154, "| col … 11 | col12|\n"]
         TABLE_HDR_ROW_ODD:[137, 138, "|"]
         MdTableCellImpl:[138, 145, " col11 "]
           TABLE_HDR_CELL_RODD_CODD:[138, 145, " col11 "]
         TABLE_HDR_ROW_ODD:[145, 146, "|"]
         MdTableCellImpl:[146, 152, " col12"]
           TABLE_HDR_CELL_RODD_CEVEN:[146, 152, " col12"]
         TABLE_HDR_ROW_ODD:[152, 153, "|"]
         EOL:[153, 154, "\n"]
       MdTableRowImpl:[154, 171, "| col … 21 | col22|\n"]
         TABLE_HDR_ROW_EVEN:[154, 155, "|"]
         MdTableCellImpl:[155, 162, " col21 "]
           TABLE_HDR_CELL_REVEN_CODD:[155, 162, " col21 "]
         TABLE_HDR_ROW_EVEN:[162, 163, "|"]
         MdTableCellImpl:[163, 169, " col22"]
           TABLE_HDR_CELL_REVEN_CEVEN:[163, 169, " col22"]
         TABLE_HDR_ROW_EVEN:[169, 170, "|"]
         EOL:[170, 171, "\n"]
     MdTableSeparatorImpl:[171, 181, "|---|---|\n"]
       TABLE_SEP_ROW_ODD:[171, 172, "|"]
       MdTableCellImpl:[172, 175, "---"]
         TABLE_SEP_COLUMN_ODD:[172, 175, "---"]
       TABLE_SEP_ROW_ODD:[175, 176, "|"]
       MdTableCellImpl:[176, 179, "---"]
         TABLE_SEP_COLUMN_EVEN:[176, 179, "---"]
       TABLE_SEP_ROW_ODD:[179, 180, "|"]
       EOL:[180, 181, "\n"]
     MdTableBodyImpl:[181, 198, "| dat … a1 | data2|\n"]
       MdTableRowImpl:[181, 198, "| dat … a1 | data2|\n"]
         TABLE_ROW_ODD:[181, 182, "|"]
         MdTableCellImpl:[182, 189, " data1 "]
           TABLE_CELL_RODD_CODD:[182, 189, " data1 "]
         TABLE_ROW_ODD:[189, 190, "|"]
         MdTableCellImpl:[190, 196, " data2"]
           TABLE_CELL_RODD_CEVEN:[190, 196, " data2"]
         TABLE_ROW_ODD:[196, 197, "|"]
         EOL:[197, 198, "\n"]
   MdBlankLineImpl:[198, 199, "\n"]
     BLANK_LINE:[198, 199, "\n"]
````````````````````````````````


multi row/column

```````````````````````````````` example TableBlock: 8
| col11 | col12| col13|
| col21 | col22| col23|
| col31 | col32| col33|
|---|---|---|
| data11 | data12| data13|
| data21 | data22| data23|
| data31 | data32| data33|

.
<table>
  <thead>
    <tr><th> col11 </th><th> col12</th><th> col13</th></tr>
    <tr><th> col21 </th><th> col22</th><th> col23</th></tr>
    <tr><th> col31 </th><th> col32</th><th> col33</th></tr>
  </thead>
  <tbody>
    <tr><td> data11 </td><td> data12</td><td> data13</td></tr>
    <tr><td> data21 </td><td> data22</td><td> data23</td></tr>
    <tr><td> data31 </td><td> data32</td><td> data33</td></tr>
  </tbody>
</table>
.
 MdFile:[0, 168, "| col … 11 | col12| col13|\n| col21 | col22| col23|\n| col31 | col32| col33|\n|---|---|---|\n| data11 | data12| data13|\n| data21 | data22| data23|\n| data31 | data32| data33|\n\n"]
   MdTableImpl:[0, 167, "| col … 11 | col12| col13|\n| col21 | col22| col23|\n| col31 | col32| col33|\n|---|---|---|\n| data11 | data12| data13|\n| data21 | data22| data23|\n| data31 | data32| data33|\n"]
     MdTableHeaderImpl:[0, 72, "| col … 11 | col12| col13|\n| col21 | col22| col23|\n| col31 | col32| col33|\n"]
       MdTableRowImpl:[0, 24, "| col … 11 | col12| col13|\n"]
         TABLE_HDR_ROW_ODD:[0, 1, "|"]
         MdTableCellImpl:[1, 8, " col11 "]
           TABLE_HDR_CELL_RODD_CODD:[1, 8, " col11 "]
         TABLE_HDR_ROW_ODD:[8, 9, "|"]
         MdTableCellImpl:[9, 15, " col12"]
           TABLE_HDR_CELL_RODD_CEVEN:[9, 15, " col12"]
         TABLE_HDR_ROW_ODD:[15, 16, "|"]
         MdTableCellImpl:[16, 22, " col13"]
           TABLE_HDR_CELL_RODD_CODD:[16, 22, " col13"]
         TABLE_HDR_ROW_ODD:[22, 23, "|"]
         EOL:[23, 24, "\n"]
       MdTableRowImpl:[24, 48, "| col … 21 | col22| col23|\n"]
         TABLE_HDR_ROW_EVEN:[24, 25, "|"]
         MdTableCellImpl:[25, 32, " col21 "]
           TABLE_HDR_CELL_REVEN_CODD:[25, 32, " col21 "]
         TABLE_HDR_ROW_EVEN:[32, 33, "|"]
         MdTableCellImpl:[33, 39, " col22"]
           TABLE_HDR_CELL_REVEN_CEVEN:[33, 39, " col22"]
         TABLE_HDR_ROW_EVEN:[39, 40, "|"]
         MdTableCellImpl:[40, 46, " col23"]
           TABLE_HDR_CELL_REVEN_CODD:[40, 46, " col23"]
         TABLE_HDR_ROW_EVEN:[46, 47, "|"]
         EOL:[47, 48, "\n"]
       MdTableRowImpl:[48, 72, "| col … 31 | col32| col33|\n"]
         TABLE_HDR_ROW_ODD:[48, 49, "|"]
         MdTableCellImpl:[49, 56, " col31 "]
           TABLE_HDR_CELL_RODD_CODD:[49, 56, " col31 "]
         TABLE_HDR_ROW_ODD:[56, 57, "|"]
         MdTableCellImpl:[57, 63, " col32"]
           TABLE_HDR_CELL_RODD_CEVEN:[57, 63, " col32"]
         TABLE_HDR_ROW_ODD:[63, 64, "|"]
         MdTableCellImpl:[64, 70, " col33"]
           TABLE_HDR_CELL_RODD_CODD:[64, 70, " col33"]
         TABLE_HDR_ROW_ODD:[70, 71, "|"]
         EOL:[71, 72, "\n"]
     MdTableSeparatorImpl:[72, 86, "|---| … ---|---|\n"]
       TABLE_SEP_ROW_ODD:[72, 73, "|"]
       MdTableCellImpl:[73, 76, "---"]
         TABLE_SEP_COLUMN_ODD:[73, 76, "---"]
       TABLE_SEP_ROW_ODD:[76, 77, "|"]
       MdTableCellImpl:[77, 80, "---"]
         TABLE_SEP_COLUMN_EVEN:[77, 80, "---"]
       TABLE_SEP_ROW_ODD:[80, 81, "|"]
       MdTableCellImpl:[81, 84, "---"]
         TABLE_SEP_COLUMN_ODD:[81, 84, "---"]
       TABLE_SEP_ROW_ODD:[84, 85, "|"]
       EOL:[85, 86, "\n"]
     MdTableBodyImpl:[86, 167, "| dat … a11 | data12| data13|\n| data21 | data22| data23|\n| data31 | data32| data33|\n"]
       MdTableRowImpl:[86, 113, "| dat … a11 | data12| data13|\n"]
         TABLE_ROW_ODD:[86, 87, "|"]
         MdTableCellImpl:[87, 95, " data11 "]
           TABLE_CELL_RODD_CODD:[87, 95, " data11 "]
         TABLE_ROW_ODD:[95, 96, "|"]
         MdTableCellImpl:[96, 103, " data12"]
           TABLE_CELL_RODD_CEVEN:[96, 103, " data12"]
         TABLE_ROW_ODD:[103, 104, "|"]
         MdTableCellImpl:[104, 111, " data13"]
           TABLE_CELL_RODD_CODD:[104, 111, " data13"]
         TABLE_ROW_ODD:[111, 112, "|"]
         EOL:[112, 113, "\n"]
       MdTableRowImpl:[113, 140, "| dat … a21 | data22| data23|\n"]
         TABLE_ROW_EVEN:[113, 114, "|"]
         MdTableCellImpl:[114, 122, " data21 "]
           TABLE_CELL_REVEN_CODD:[114, 122, " data21 "]
         TABLE_ROW_EVEN:[122, 123, "|"]
         MdTableCellImpl:[123, 130, " data22"]
           TABLE_CELL_REVEN_CEVEN:[123, 130, " data22"]
         TABLE_ROW_EVEN:[130, 131, "|"]
         MdTableCellImpl:[131, 138, " data23"]
           TABLE_CELL_REVEN_CODD:[131, 138, " data23"]
         TABLE_ROW_EVEN:[138, 139, "|"]
         EOL:[139, 140, "\n"]
       MdTableRowImpl:[140, 167, "| dat … a31 | data32| data33|\n"]
         TABLE_ROW_ODD:[140, 141, "|"]
         MdTableCellImpl:[141, 149, " data31 "]
           TABLE_CELL_RODD_CODD:[141, 149, " data31 "]
         TABLE_ROW_ODD:[149, 150, "|"]
         MdTableCellImpl:[150, 157, " data32"]
           TABLE_CELL_RODD_CEVEN:[150, 157, " data32"]
         TABLE_ROW_ODD:[157, 158, "|"]
         MdTableCellImpl:[158, 165, " data33"]
           TABLE_CELL_RODD_CODD:[158, 165, " data33"]
         TABLE_ROW_ODD:[165, 166, "|"]
         EOL:[166, 167, "\n"]
   MdBlankLineImpl:[167, 168, "\n"]
     BLANK_LINE:[167, 168, "\n"]
````````````````````````````````


real life table

```````````````````````````````` example TableBlock: 9
| Feature                                                                                                                 | Basic | Enhanced |
|:------------------------------------------------------------------------------------------------------------------------|:-----:|:--------:|
| Works with builds 143.2370 or newer, product version IDEA 15.0.6                                                        |   X   |    X     |
| Preview Tab so you can see what the rendered markdown will look like on GitHub.                                         |   X   |    X     |
| Syntax highlighting                                                                                                     |   X   |    X     |
| Table syntax highlighting stripes rows and columns                                                                      |   X   |    X     |
| Support for Default and Darcula color schemes for preview tab                                                           |   X   |    X     |
| Warning and Error Annotations to help you validate wiki link errors                                                     |   X   |    X     |
| Link address completion for wiki links                                                                                  |   X   |    X     |
| Quick Fixes for detected wiki link errors                                                                               |   X   |    X     |
| GFM Task list extension `* [ ]` open task item and `* [x]` completed task item                                          |   X   |    X     |
| Line markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  |   X   |    X     |
| Markdown extensions configuration to customize markdown dialects                                                        |   X   |    X     |
| GitHub wiki support makes maintaining GitHub wiki pages easier.                                                         |   X   |    X     |
| GitHub compatible id generation for headers so you can validate your anchor references                                  |   X   |    X     |
| Swing and JavaFX WebView based preview.                                                                                 |   X   |    X     |
| Supports **JavaFX with JetBrains JRE on OS X**                                                                          |   X   |    X     |
| Supports Highlight JS in WebView preview                                                                                |   X   |    X     |
| **Multi-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**                                        |   X   |    X     |
| Live Templates for common markdown elements                                                                             |   X   |    X     |
| **Enhanced Version Benefits**                                                                                           |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   |       |    X     |
| **As you type automation**                                                                                              |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing `#` marker                                                        |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       |       |    X     |
| **Code Completions**                                                                                                    |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after `issues/` link address and in text                             |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                |       |    X     |
| **Intention Actions**                                                                                                   |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           |       |    X     |
| **Refactoring**                                                                                                         |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    |       |    X     |
| &nbsp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         |       |    X     |
.
<table>
  <thead>
    <tr><th align="left"> Feature                                                                                                                 </th><th align="center"> Basic </th><th align="center"> Enhanced </th></tr>
  </thead>
  <tbody>
    <tr><td align="left"> Works with builds 143.2370 or newer, product version IDEA 15.0.6                                                        </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Preview Tab so you can see what the rendered markdown will look like on GitHub.                                         </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Syntax highlighting                                                                                                     </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Table syntax highlighting stripes rows and columns                                                                      </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Support for Default and Darcula color schemes for preview tab                                                           </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Warning and Error Annotations to help you validate wiki link errors                                                     </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Link address completion for wiki links                                                                                  </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Quick Fixes for detected wiki link errors                                                                               </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> GFM Task list extension <code>* [ ]</code> open task item and <code>* [x]</code> completed task item                                          </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Line markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Markdown extensions configuration to customize markdown dialects                                                        </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> GitHub wiki support makes maintaining GitHub wiki pages easier.                                                         </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> GitHub compatible id generation for headers so you can validate your anchor references                                  </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Swing and JavaFX WebView based preview.                                                                                 </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Supports <strong>JavaFX with JetBrains JRE on OS X</strong>                                                                          </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Supports Highlight JS in WebView preview                                                                                </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> <strong>Multi-line Image URLs for embedding [gravizo.com] UML diagrams into markdown</strong>                                        </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> Live Templates for common markdown elements                                                                             </td><td align="center">   X   </td><td align="center">    X     </td></tr>
    <tr><td align="left"> <strong>Enhanced Version Benefits</strong>                                                                                           </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> <strong>As you type automation</strong>                                                                                              </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing <code>#</code> marker                                                        </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> <strong>Code Completions</strong>                                                                                                    </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Absolute link address completions using <a href="https://">https://</a> and <a href="file://">file://</a> formats                            </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after <code>issues/</code> link address and in text                             </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> <strong>Intention Actions</strong>                                                                                                   </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Change between relative and absolute <a href="https://">https://</a> link addresses via intention action               </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> <strong>Refactoring</strong>                                                                                                         </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Refactoring for /, <a href="https://">https://</a> and <a href="file://">file://</a> absolute link addresses to project files                </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    </td><td align="center">       </td><td align="center">    X     </td></tr>
    <tr><td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         </td><td align="center">       </td><td align="center">    X     </td></tr>
  </tbody>
</table>
.
 MdFile:[0, 10152, "| Fea … ture                                                                                                                 | Basic | Enhanced |\n|:------------------------------------------------------------------------------------------------------------------------|:-----:|:--------:|\n| Works with builds 143.2370 or newer, product version IDEA 15.0.6                                                        |   X   |    X     |\n| Preview Tab so you can see what the rendered markdown will look like on GitHub.                                         |   X   |    X     |\n| Syntax highlighting                                                                                                     |   X   |    X     |\n| Table syntax highlighting stripes rows and columns                                                                      |   X   |    X     |\n| Support for Default and Darcula color schemes for preview tab                                                           |   X   |    X     |\n| Warning and Error Annotations to help you validate wiki link errors                                                     |   X   |    X     |\n| Link address completion for wiki links                                                                                  |   X   |    X     |\n| Quick Fixes for detected wiki link errors                                                                               |   X   |    X     |\n| GFM Task list extension `* [ ]` open task item and `* [x]` completed task item                                          |   X   |    X     |\n| Line markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  |   X   |    X     |\n| Markdown extensions configuration to customize markdown dialects                                                        |   X   |    X     |\n| GitHub wiki support makes maintaining GitHub wiki pages easier.                                                         |   X   |    X     |\n| GitHub compatible id generation for headers so you can validate your anchor references                                  |   X   |    X     |\n| Swing and JavaFX WebView based preview.                                                                                 |   X   |    X     |\n| Supports **JavaFX with JetBrains JRE on OS X**                                                                          |   X   |    X     |\n| Supports Highlight JS in WebView preview                                                                                |   X   |    X     |\n| **Multi-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**                                        |   X   |    X     |\n| Live Templates for common markdown elements                                                                             |   X   |    X     |\n| **Enhanced Version Benefits**                                                                                           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   |       |    X     |\n| **As you type automation**                                                                                              |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing `#` marker                                                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       |       |    X     |\n| **Code Completions**                                                                                                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after `issues/` link address and in text                             |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                |       |    X     |\n| **Intention Actions**                                                                                                   |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           |       |    X     |\n| **Refactoring**                                                                                                         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         |       |    X     |"]
   MdTableImpl:[0, 10152, "| Fea … ture                                                                                                                 | Basic | Enhanced |\n|:------------------------------------------------------------------------------------------------------------------------|:-----:|:--------:|\n| Works with builds 143.2370 or newer, product version IDEA 15.0.6                                                        |   X   |    X     |\n| Preview Tab so you can see what the rendered markdown will look like on GitHub.                                         |   X   |    X     |\n| Syntax highlighting                                                                                                     |   X   |    X     |\n| Table syntax highlighting stripes rows and columns                                                                      |   X   |    X     |\n| Support for Default and Darcula color schemes for preview tab                                                           |   X   |    X     |\n| Warning and Error Annotations to help you validate wiki link errors                                                     |   X   |    X     |\n| Link address completion for wiki links                                                                                  |   X   |    X     |\n| Quick Fixes for detected wiki link errors                                                                               |   X   |    X     |\n| GFM Task list extension `* [ ]` open task item and `* [x]` completed task item                                          |   X   |    X     |\n| Line markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  |   X   |    X     |\n| Markdown extensions configuration to customize markdown dialects                                                        |   X   |    X     |\n| GitHub wiki support makes maintaining GitHub wiki pages easier.                                                         |   X   |    X     |\n| GitHub compatible id generation for headers so you can validate your anchor references                                  |   X   |    X     |\n| Swing and JavaFX WebView based preview.                                                                                 |   X   |    X     |\n| Supports **JavaFX with JetBrains JRE on OS X**                                                                          |   X   |    X     |\n| Supports Highlight JS in WebView preview                                                                                |   X   |    X     |\n| **Multi-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**                                        |   X   |    X     |\n| Live Templates for common markdown elements                                                                             |   X   |    X     |\n| **Enhanced Version Benefits**                                                                                           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   |       |    X     |\n| **As you type automation**                                                                                              |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing `#` marker                                                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       |       |    X     |\n| **Code Completions**                                                                                                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after `issues/` link address and in text                             |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                |       |    X     |\n| **Intention Actions**                                                                                                   |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           |       |    X     |\n| **Refactoring**                                                                                                         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         |       |    X     |"]
     MdTableHeaderImpl:[0, 143, "| Fea … ture                                                                                                                 | Basic | Enhanced |\n"]
       MdTableRowImpl:[0, 143, "| Fea … ture                                                                                                                 | Basic | Enhanced |\n"]
         TABLE_HDR_ROW_ODD:[0, 1, "|"]
         MdTableCellImpl:[1, 122, " Feat … ure                                                                                                                 "]
           TABLE_HDR_CELL_RODD_CODD:[1, 122, " Feat … ure                                                                                                                 "]
         TABLE_HDR_ROW_ODD:[122, 123, "|"]
         MdTableCellImpl:[123, 130, " Basic "]
           TABLE_HDR_CELL_RODD_CEVEN:[123, 130, " Basic "]
         TABLE_HDR_ROW_ODD:[130, 131, "|"]
         MdTableCellImpl:[131, 141, " Enhanced "]
           TABLE_HDR_CELL_RODD_CODD:[131, 141, " Enhanced "]
         TABLE_HDR_ROW_ODD:[141, 142, "|"]
         EOL:[142, 143, "\n"]
     MdTableSeparatorImpl:[143, 286, "|:--- … ---------------------------------------------------------------------------------------------------------------------|:-----:|:--------:|\n"]
       TABLE_SEP_ROW_ODD:[143, 144, "|"]
       MdTableCellImpl:[144, 265, ":---- … --------------------------------------------------------------------------------------------------------------------"]
         TABLE_SEP_COLUMN_ODD:[144, 265, ":---- … --------------------------------------------------------------------------------------------------------------------"]
       TABLE_SEP_ROW_ODD:[265, 266, "|"]
       MdTableCellImpl:[266, 273, ":-----:"]
         TABLE_SEP_COLUMN_EVEN:[266, 273, ":-----:"]
       TABLE_SEP_ROW_ODD:[273, 274, "|"]
       MdTableCellImpl:[274, 284, ":--------:"]
         TABLE_SEP_COLUMN_ODD:[274, 284, ":--------:"]
       TABLE_SEP_ROW_ODD:[284, 285, "|"]
       EOL:[285, 286, "\n"]
     MdTableBodyImpl:[286, 10152, "| Wor … ks with builds 143.2370 or newer, product version IDEA 15.0.6                                                        |   X   |    X     |\n| Preview Tab so you can see what the rendered markdown will look like on GitHub.                                         |   X   |    X     |\n| Syntax highlighting                                                                                                     |   X   |    X     |\n| Table syntax highlighting stripes rows and columns                                                                      |   X   |    X     |\n| Support for Default and Darcula color schemes for preview tab                                                           |   X   |    X     |\n| Warning and Error Annotations to help you validate wiki link errors                                                     |   X   |    X     |\n| Link address completion for wiki links                                                                                  |   X   |    X     |\n| Quick Fixes for detected wiki link errors                                                                               |   X   |    X     |\n| GFM Task list extension `* [ ]` open task item and `* [x]` completed task item                                          |   X   |    X     |\n| Line markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  |   X   |    X     |\n| Markdown extensions configuration to customize markdown dialects                                                        |   X   |    X     |\n| GitHub wiki support makes maintaining GitHub wiki pages easier.                                                         |   X   |    X     |\n| GitHub compatible id generation for headers so you can validate your anchor references                                  |   X   |    X     |\n| Swing and JavaFX WebView based preview.                                                                                 |   X   |    X     |\n| Supports **JavaFX with JetBrains JRE on OS X**                                                                          |   X   |    X     |\n| Supports Highlight JS in WebView preview                                                                                |   X   |    X     |\n| **Multi-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**                                        |   X   |    X     |\n| Live Templates for common markdown elements                                                                             |   X   |    X     |\n| **Enhanced Version Benefits**                                                                                           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   |       |    X     |\n| **As you type automation**                                                                                              |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing `#` marker                                                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       |       |    X     |\n| **Code Completions**                                                                                                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after `issues/` link address and in text                             |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                |       |    X     |\n| **Intention Actions**                                                                                                   |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           |       |    X     |\n| **Refactoring**                                                                                                         |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    |       |    X     |\n| &nbsp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         |       |    X     |"]
       MdTableRowImpl:[286, 429, "| Wor … ks with builds 143.2370 or newer, product version IDEA 15.0.6                                                        |   X   |    X     |\n"]
         TABLE_ROW_ODD:[286, 287, "|"]
         MdTableCellImpl:[287, 408, " Work … s with builds 143.2370 or newer, product version IDEA 15.0.6                                                        "]
           TABLE_CELL_RODD_CODD:[287, 408, " Work … s with builds 143.2370 or newer, product version IDEA 15.0.6                                                        "]
         TABLE_ROW_ODD:[408, 409, "|"]
         MdTableCellImpl:[409, 416, "   X   "]
           TABLE_CELL_RODD_CEVEN:[409, 416, "   X   "]
         TABLE_ROW_ODD:[416, 417, "|"]
         MdTableCellImpl:[417, 427, "    X     "]
           TABLE_CELL_RODD_CODD:[417, 427, "    X     "]
         TABLE_ROW_ODD:[427, 428, "|"]
         EOL:[428, 429, "\n"]
       MdTableRowImpl:[429, 572, "| Pre … view Tab so you can see what the rendered markdown will look like on GitHub.                                         |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[429, 430, "|"]
         MdTableCellImpl:[430, 551, " Prev … iew Tab so you can see what the rendered markdown will look like on GitHub.                                         "]
           TABLE_CELL_REVEN_CODD:[430, 551, " Prev … iew Tab so you can see what the rendered markdown will look like on GitHub.                                         "]
         TABLE_ROW_EVEN:[551, 552, "|"]
         MdTableCellImpl:[552, 559, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[552, 559, "   X   "]
         TABLE_ROW_EVEN:[559, 560, "|"]
         MdTableCellImpl:[560, 570, "    X     "]
           TABLE_CELL_REVEN_CODD:[560, 570, "    X     "]
         TABLE_ROW_EVEN:[570, 571, "|"]
         EOL:[571, 572, "\n"]
       MdTableRowImpl:[572, 715, "| Syn … tax highlighting                                                                                                     |   X   |    X     |\n"]
         TABLE_ROW_ODD:[572, 573, "|"]
         MdTableCellImpl:[573, 694, " Synt … ax highlighting                                                                                                     "]
           TABLE_CELL_RODD_CODD:[573, 694, " Synt … ax highlighting                                                                                                     "]
         TABLE_ROW_ODD:[694, 695, "|"]
         MdTableCellImpl:[695, 702, "   X   "]
           TABLE_CELL_RODD_CEVEN:[695, 702, "   X   "]
         TABLE_ROW_ODD:[702, 703, "|"]
         MdTableCellImpl:[703, 713, "    X     "]
           TABLE_CELL_RODD_CODD:[703, 713, "    X     "]
         TABLE_ROW_ODD:[713, 714, "|"]
         EOL:[714, 715, "\n"]
       MdTableRowImpl:[715, 858, "| Tab … le syntax highlighting stripes rows and columns                                                                      |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[715, 716, "|"]
         MdTableCellImpl:[716, 837, " Tabl … e syntax highlighting stripes rows and columns                                                                      "]
           TABLE_CELL_REVEN_CODD:[716, 837, " Tabl … e syntax highlighting stripes rows and columns                                                                      "]
         TABLE_ROW_EVEN:[837, 838, "|"]
         MdTableCellImpl:[838, 845, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[838, 845, "   X   "]
         TABLE_ROW_EVEN:[845, 846, "|"]
         MdTableCellImpl:[846, 856, "    X     "]
           TABLE_CELL_REVEN_CODD:[846, 856, "    X     "]
         TABLE_ROW_EVEN:[856, 857, "|"]
         EOL:[857, 858, "\n"]
       MdTableRowImpl:[858, 1001, "| Sup … port for Default and Darcula color schemes for preview tab                                                           |   X   |    X     |\n"]
         TABLE_ROW_ODD:[858, 859, "|"]
         MdTableCellImpl:[859, 980, " Supp … ort for Default and Darcula color schemes for preview tab                                                           "]
           TABLE_CELL_RODD_CODD:[859, 980, " Supp … ort for Default and Darcula color schemes for preview tab                                                           "]
         TABLE_ROW_ODD:[980, 981, "|"]
         MdTableCellImpl:[981, 988, "   X   "]
           TABLE_CELL_RODD_CEVEN:[981, 988, "   X   "]
         TABLE_ROW_ODD:[988, 989, "|"]
         MdTableCellImpl:[989, 999, "    X     "]
           TABLE_CELL_RODD_CODD:[989, 999, "    X     "]
         TABLE_ROW_ODD:[999, 1000, "|"]
         EOL:[1000, 1001, "\n"]
       MdTableRowImpl:[1001, 1144, "| War … ning and Error Annotations to help you validate wiki link errors                                                     |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[1001, 1002, "|"]
         MdTableCellImpl:[1002, 1123, " Warn … ing and Error Annotations to help you validate wiki link errors                                                     "]
           TABLE_CELL_REVEN_CODD:[1002, 1123, " Warn … ing and Error Annotations to help you validate wiki link errors                                                     "]
         TABLE_ROW_EVEN:[1123, 1124, "|"]
         MdTableCellImpl:[1124, 1131, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[1124, 1131, "   X   "]
         TABLE_ROW_EVEN:[1131, 1132, "|"]
         MdTableCellImpl:[1132, 1142, "    X     "]
           TABLE_CELL_REVEN_CODD:[1132, 1142, "    X     "]
         TABLE_ROW_EVEN:[1142, 1143, "|"]
         EOL:[1143, 1144, "\n"]
       MdTableRowImpl:[1144, 1287, "| Lin … k address completion for wiki links                                                                                  |   X   |    X     |\n"]
         TABLE_ROW_ODD:[1144, 1145, "|"]
         MdTableCellImpl:[1145, 1266, " Link …  address completion for wiki links                                                                                  "]
           TABLE_CELL_RODD_CODD:[1145, 1266, " Link …  address completion for wiki links                                                                                  "]
         TABLE_ROW_ODD:[1266, 1267, "|"]
         MdTableCellImpl:[1267, 1274, "   X   "]
           TABLE_CELL_RODD_CEVEN:[1267, 1274, "   X   "]
         TABLE_ROW_ODD:[1274, 1275, "|"]
         MdTableCellImpl:[1275, 1285, "    X     "]
           TABLE_CELL_RODD_CODD:[1275, 1285, "    X     "]
         TABLE_ROW_ODD:[1285, 1286, "|"]
         EOL:[1286, 1287, "\n"]
       MdTableRowImpl:[1287, 1430, "| Qui … ck Fixes for detected wiki link errors                                                                               |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[1287, 1288, "|"]
         MdTableCellImpl:[1288, 1409, " Quic … k Fixes for detected wiki link errors                                                                               "]
           TABLE_CELL_REVEN_CODD:[1288, 1409, " Quic … k Fixes for detected wiki link errors                                                                               "]
         TABLE_ROW_EVEN:[1409, 1410, "|"]
         MdTableCellImpl:[1410, 1417, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[1410, 1417, "   X   "]
         TABLE_ROW_EVEN:[1417, 1418, "|"]
         MdTableCellImpl:[1418, 1428, "    X     "]
           TABLE_CELL_REVEN_CODD:[1418, 1428, "    X     "]
         TABLE_ROW_EVEN:[1428, 1429, "|"]
         EOL:[1429, 1430, "\n"]
       MdTableRowImpl:[1430, 1573, "| GFM …  Task list extension `* [ ]` open task item and `* [x]` completed task item                                          |   X   |    X     |\n"]
         TABLE_ROW_ODD:[1430, 1431, "|"]
         MdTableCellImpl:[1431, 1552, " GFM  … Task list extension `* [ ]` open task item and `* [x]` completed task item                                          "]
           TABLE_CELL_RODD_CODD:[1431, 1456, " GFM  … Task list extension "]
           MdInlineCodeImpl:[1456, 1463, "`* [ ]`"]
             CODE_MARKER:[1456, 1457, "`"]
             TABLE_CELL_RODD_CODD_CODE_TEXT:[1457, 1462, "* [ ]"]
             CODE_MARKER:[1462, 1463, "`"]
           TABLE_CELL_RODD_CODD:[1463, 1483, " open …  task item and "]
           MdInlineCodeImpl:[1483, 1490, "`* [x]`"]
             CODE_MARKER:[1483, 1484, "`"]
             TABLE_CELL_RODD_CODD_CODE_TEXT:[1484, 1489, "* [x]"]
             CODE_MARKER:[1489, 1490, "`"]
           TABLE_CELL_RODD_CODD:[1490, 1552, " comp … leted task item                                          "]
         TABLE_ROW_ODD:[1552, 1553, "|"]
         MdTableCellImpl:[1553, 1560, "   X   "]
           TABLE_CELL_RODD_CEVEN:[1553, 1560, "   X   "]
         TABLE_ROW_ODD:[1560, 1561, "|"]
         MdTableCellImpl:[1561, 1571, "    X     "]
           TABLE_CELL_RODD_CODD:[1561, 1571, "    X     "]
         TABLE_ROW_ODD:[1571, 1572, "|"]
         EOL:[1572, 1573, "\n"]
       MdTableRowImpl:[1573, 1716, "| Lin … e markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[1573, 1574, "|"]
         MdTableCellImpl:[1574, 1695, " Line …  markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  "]
           TABLE_CELL_REVEN_CODD:[1574, 1695, " Line …  markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  "]
         TABLE_ROW_EVEN:[1695, 1696, "|"]
         MdTableCellImpl:[1696, 1703, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[1696, 1703, "   X   "]
         TABLE_ROW_EVEN:[1703, 1704, "|"]
         MdTableCellImpl:[1704, 1714, "    X     "]
           TABLE_CELL_REVEN_CODD:[1704, 1714, "    X     "]
         TABLE_ROW_EVEN:[1714, 1715, "|"]
         EOL:[1715, 1716, "\n"]
       MdTableRowImpl:[1716, 1859, "| Mar … kdown extensions configuration to customize markdown dialects                                                        |   X   |    X     |\n"]
         TABLE_ROW_ODD:[1716, 1717, "|"]
         MdTableCellImpl:[1717, 1838, " Mark … down extensions configuration to customize markdown dialects                                                        "]
           TABLE_CELL_RODD_CODD:[1717, 1838, " Mark … down extensions configuration to customize markdown dialects                                                        "]
         TABLE_ROW_ODD:[1838, 1839, "|"]
         MdTableCellImpl:[1839, 1846, "   X   "]
           TABLE_CELL_RODD_CEVEN:[1839, 1846, "   X   "]
         TABLE_ROW_ODD:[1846, 1847, "|"]
         MdTableCellImpl:[1847, 1857, "    X     "]
           TABLE_CELL_RODD_CODD:[1847, 1857, "    X     "]
         TABLE_ROW_ODD:[1857, 1858, "|"]
         EOL:[1858, 1859, "\n"]
       MdTableRowImpl:[1859, 2002, "| Git … Hub wiki support makes maintaining GitHub wiki pages easier.                                                         |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[1859, 1860, "|"]
         MdTableCellImpl:[1860, 1981, " GitH … ub wiki support makes maintaining GitHub wiki pages easier.                                                         "]
           TABLE_CELL_REVEN_CODD:[1860, 1981, " GitH … ub wiki support makes maintaining GitHub wiki pages easier.                                                         "]
         TABLE_ROW_EVEN:[1981, 1982, "|"]
         MdTableCellImpl:[1982, 1989, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[1982, 1989, "   X   "]
         TABLE_ROW_EVEN:[1989, 1990, "|"]
         MdTableCellImpl:[1990, 2000, "    X     "]
           TABLE_CELL_REVEN_CODD:[1990, 2000, "    X     "]
         TABLE_ROW_EVEN:[2000, 2001, "|"]
         EOL:[2001, 2002, "\n"]
       MdTableRowImpl:[2002, 2145, "| Git … Hub compatible id generation for headers so you can validate your anchor references                                  |   X   |    X     |\n"]
         TABLE_ROW_ODD:[2002, 2003, "|"]
         MdTableCellImpl:[2003, 2124, " GitH … ub compatible id generation for headers so you can validate your anchor references                                  "]
           TABLE_CELL_RODD_CODD:[2003, 2124, " GitH … ub compatible id generation for headers so you can validate your anchor references                                  "]
         TABLE_ROW_ODD:[2124, 2125, "|"]
         MdTableCellImpl:[2125, 2132, "   X   "]
           TABLE_CELL_RODD_CEVEN:[2125, 2132, "   X   "]
         TABLE_ROW_ODD:[2132, 2133, "|"]
         MdTableCellImpl:[2133, 2143, "    X     "]
           TABLE_CELL_RODD_CODD:[2133, 2143, "    X     "]
         TABLE_ROW_ODD:[2143, 2144, "|"]
         EOL:[2144, 2145, "\n"]
       MdTableRowImpl:[2145, 2288, "| Swi … ng and JavaFX WebView based preview.                                                                                 |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[2145, 2146, "|"]
         MdTableCellImpl:[2146, 2267, " Swin … g and JavaFX WebView based preview.                                                                                 "]
           TABLE_CELL_REVEN_CODD:[2146, 2267, " Swin … g and JavaFX WebView based preview.                                                                                 "]
         TABLE_ROW_EVEN:[2267, 2268, "|"]
         MdTableCellImpl:[2268, 2275, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[2268, 2275, "   X   "]
         TABLE_ROW_EVEN:[2275, 2276, "|"]
         MdTableCellImpl:[2276, 2286, "    X     "]
           TABLE_CELL_REVEN_CODD:[2276, 2286, "    X     "]
         TABLE_ROW_EVEN:[2286, 2287, "|"]
         EOL:[2287, 2288, "\n"]
       MdTableRowImpl:[2288, 2431, "| Sup … ports **JavaFX with JetBrains JRE on OS X**                                                                          |   X   |    X     |\n"]
         TABLE_ROW_ODD:[2288, 2289, "|"]
         MdTableCellImpl:[2289, 2410, " Supp … orts **JavaFX with JetBrains JRE on OS X**                                                                          "]
           TABLE_CELL_RODD_CODD:[2289, 2299, " Supports "]
           MdInlineBoldImpl:[2299, 2336, "**Jav … aFX with JetBrains JRE on OS X**"]
             TABLE_CELL_RODD_CODD_BOLD_MARKER:[2299, 2301, "**"]
             TABLE_CELL_RODD_CODD_BOLD_TEXT:[2301, 2334, "JavaF … X with JetBrains JRE on OS X"]
             TABLE_CELL_RODD_CODD_BOLD_MARKER:[2334, 2336, "**"]
           TABLE_CELL_RODD_CODD:[2336, 2410, "      …                                                                      "]
         TABLE_ROW_ODD:[2410, 2411, "|"]
         MdTableCellImpl:[2411, 2418, "   X   "]
           TABLE_CELL_RODD_CEVEN:[2411, 2418, "   X   "]
         TABLE_ROW_ODD:[2418, 2419, "|"]
         MdTableCellImpl:[2419, 2429, "    X     "]
           TABLE_CELL_RODD_CODD:[2419, 2429, "    X     "]
         TABLE_ROW_ODD:[2429, 2430, "|"]
         EOL:[2430, 2431, "\n"]
       MdTableRowImpl:[2431, 2574, "| Sup … ports Highlight JS in WebView preview                                                                                |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[2431, 2432, "|"]
         MdTableCellImpl:[2432, 2553, " Supp … orts Highlight JS in WebView preview                                                                                "]
           TABLE_CELL_REVEN_CODD:[2432, 2553, " Supp … orts Highlight JS in WebView preview                                                                                "]
         TABLE_ROW_EVEN:[2553, 2554, "|"]
         MdTableCellImpl:[2554, 2561, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[2554, 2561, "   X   "]
         TABLE_ROW_EVEN:[2561, 2562, "|"]
         MdTableCellImpl:[2562, 2572, "    X     "]
           TABLE_CELL_REVEN_CODD:[2562, 2572, "    X     "]
         TABLE_ROW_EVEN:[2572, 2573, "|"]
         EOL:[2573, 2574, "\n"]
       MdTableRowImpl:[2574, 2717, "| **M … ulti-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**                                        |   X   |    X     |\n"]
         TABLE_ROW_ODD:[2574, 2575, "|"]
         MdTableCellImpl:[2575, 2696, " **Mu … lti-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**                                        "]
           TABLE_CELL_RODD_CODD:[2575, 2576, " "]
           MdInlineBoldImpl:[2576, 2656, "**Mul … ti-line Image URLs for embedding [gravizo.com] UML diagrams into markdown**"]
             TABLE_CELL_RODD_CODD_BOLD_MARKER:[2576, 2578, "**"]
             TABLE_CELL_RODD_CODD_BOLD_TEXT:[2578, 2614, "Multi … -line Image URLs for embedding "]
             MdReferenceLinkImpl:[2614, 2627, "[grav … izo.com]"]
               TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_OPEN2:[2614, 2615, "["]
               MdReferenceLinkReferenceImpl:[2615, 2626, "gravi … zo.com"]
                 REFERENCE_LINK_REFERENCE_LEAF:[2615, 2626, "gravi … zo.com"]
               TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_CLOSE2:[2626, 2627, "]"]
             TABLE_CELL_RODD_CODD_BOLD_TEXT:[2627, 2654, " UML  … diagrams into markdown"]
             TABLE_CELL_RODD_CODD_BOLD_MARKER:[2654, 2656, "**"]
           TABLE_CELL_RODD_CODD:[2656, 2696, "      …                                    "]
         TABLE_ROW_ODD:[2696, 2697, "|"]
         MdTableCellImpl:[2697, 2704, "   X   "]
           TABLE_CELL_RODD_CEVEN:[2697, 2704, "   X   "]
         TABLE_ROW_ODD:[2704, 2705, "|"]
         MdTableCellImpl:[2705, 2715, "    X     "]
           TABLE_CELL_RODD_CODD:[2705, 2715, "    X     "]
         TABLE_ROW_ODD:[2715, 2716, "|"]
         EOL:[2716, 2717, "\n"]
       MdTableRowImpl:[2717, 2860, "| Liv … e Templates for common markdown elements                                                                             |   X   |    X     |\n"]
         TABLE_ROW_EVEN:[2717, 2718, "|"]
         MdTableCellImpl:[2718, 2839, " Live …  Templates for common markdown elements                                                                             "]
           TABLE_CELL_REVEN_CODD:[2718, 2839, " Live …  Templates for common markdown elements                                                                             "]
         TABLE_ROW_EVEN:[2839, 2840, "|"]
         MdTableCellImpl:[2840, 2847, "   X   "]
           TABLE_CELL_REVEN_CEVEN:[2840, 2847, "   X   "]
         TABLE_ROW_EVEN:[2847, 2848, "|"]
         MdTableCellImpl:[2848, 2858, "    X     "]
           TABLE_CELL_REVEN_CODD:[2848, 2858, "    X     "]
         TABLE_ROW_EVEN:[2858, 2859, "|"]
         EOL:[2859, 2860, "\n"]
       MdTableRowImpl:[2860, 3003, "| **E … nhanced Version Benefits**                                                                                           |       |    X     |\n"]
         TABLE_ROW_ODD:[2860, 2861, "|"]
         MdTableCellImpl:[2861, 2982, " **En … hanced Version Benefits**                                                                                           "]
           TABLE_CELL_RODD_CODD:[2861, 2862, " "]
           MdInlineBoldImpl:[2862, 2891, "**Enh … anced Version Benefits**"]
             TABLE_CELL_RODD_CODD_BOLD_MARKER:[2862, 2864, "**"]
             TABLE_CELL_RODD_CODD_BOLD_TEXT:[2864, 2889, "Enhan … ced Version Benefits"]
             TABLE_CELL_RODD_CODD_BOLD_MARKER:[2889, 2891, "**"]
           TABLE_CELL_RODD_CODD:[2891, 2982, "      …                                                                                       "]
         TABLE_ROW_ODD:[2982, 2983, "|"]
         MdTableCellImpl:[2983, 2990, "       "]
           TABLE_CELL_RODD_CEVEN:[2983, 2990, "       "]
         TABLE_ROW_ODD:[2990, 2991, "|"]
         MdTableCellImpl:[2991, 3001, "    X     "]
           TABLE_CELL_RODD_CODD:[2991, 3001, "    X     "]
         TABLE_ROW_ODD:[3001, 3002, "|"]
         EOL:[3002, 3003, "\n"]
       MdTableRowImpl:[3003, 3146, "| &nb … sp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    |       |    X     |\n"]
         TABLE_ROW_EVEN:[3003, 3004, "|"]
         MdTableCellImpl:[3004, 3125, " &nbs … p;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    "]
           TABLE_CELL_REVEN_CODD:[3004, 3005, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[3005, 3029, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[3029, 3125, "Split …  Editor with Preview or HTML Text modes to view both source and preview                    "]
         TABLE_ROW_EVEN:[3125, 3126, "|"]
         MdTableCellImpl:[3126, 3133, "       "]
           TABLE_CELL_REVEN_CEVEN:[3126, 3133, "       "]
         TABLE_ROW_EVEN:[3133, 3134, "|"]
         MdTableCellImpl:[3134, 3144, "    X     "]
           TABLE_CELL_REVEN_CODD:[3134, 3144, "    X     "]
         TABLE_ROW_EVEN:[3144, 3145, "|"]
         EOL:[3145, 3146, "\n"]
       MdTableRowImpl:[3146, 3289, "| &nb … sp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  |       |    X     |\n"]
         TABLE_ROW_ODD:[3146, 3147, "|"]
         MdTableCellImpl:[3147, 3268, " &nbs … p;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  "]
           TABLE_CELL_RODD_CODD:[3147, 3148, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[3148, 3172, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[3172, 3268, "Toolb … ar for fast access to frequent operations                                                  "]
         TABLE_ROW_ODD:[3268, 3269, "|"]
         MdTableCellImpl:[3269, 3276, "       "]
           TABLE_CELL_RODD_CEVEN:[3269, 3276, "       "]
         TABLE_ROW_ODD:[3276, 3277, "|"]
         MdTableCellImpl:[3277, 3287, "    X     "]
           TABLE_CELL_RODD_CODD:[3277, 3287, "    X     "]
         TABLE_ROW_ODD:[3287, 3288, "|"]
         EOL:[3288, 3289, "\n"]
       MdTableRowImpl:[3289, 3432, "| &nb … sp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       |       |    X     |\n"]
         TABLE_ROW_EVEN:[3289, 3290, "|"]
         MdTableCellImpl:[3290, 3411, " &nbs … p;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       "]
           TABLE_CELL_REVEN_CODD:[3290, 3291, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[3291, 3315, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[3315, 3411, "Langu … age Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       "]
         TABLE_ROW_EVEN:[3411, 3412, "|"]
         MdTableCellImpl:[3412, 3419, "       "]
           TABLE_CELL_REVEN_CEVEN:[3412, 3419, "       "]
         TABLE_ROW_EVEN:[3419, 3420, "|"]
         MdTableCellImpl:[3420, 3430, "    X     "]
           TABLE_CELL_REVEN_CODD:[3420, 3430, "    X     "]
         TABLE_ROW_EVEN:[3430, 3431, "|"]
         EOL:[3431, 3432, "\n"]
       MdTableRowImpl:[3432, 3575, "| &nb … sp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               |       |    X     |\n"]
         TABLE_ROW_ODD:[3432, 3433, "|"]
         MdTableCellImpl:[3433, 3554, " &nbs … p;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               "]
           TABLE_CELL_RODD_CODD:[3433, 3434, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[3434, 3458, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[3458, 3554, "Code  … completions, refactoring, annotations and quick fixes to let you work faster               "]
         TABLE_ROW_ODD:[3554, 3555, "|"]
         MdTableCellImpl:[3555, 3562, "       "]
           TABLE_CELL_RODD_CEVEN:[3555, 3562, "       "]
         TABLE_ROW_ODD:[3562, 3563, "|"]
         MdTableCellImpl:[3563, 3573, "    X     "]
           TABLE_CELL_RODD_CODD:[3563, 3573, "    X     "]
         TABLE_ROW_ODD:[3573, 3574, "|"]
         EOL:[3574, 3575, "\n"]
       MdTableRowImpl:[3575, 3718, "| &nb … sp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       |       |    X     |\n"]
         TABLE_ROW_EVEN:[3575, 3576, "|"]
         MdTableCellImpl:[3576, 3697, " &nbs … p;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       "]
           TABLE_CELL_REVEN_CODD:[3576, 3577, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[3577, 3601, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[3601, 3697, "Navig … ation support with Line markers, Find usages, Go To Declaration for rapid navigation       "]
         TABLE_ROW_EVEN:[3697, 3698, "|"]
         MdTableCellImpl:[3698, 3705, "       "]
           TABLE_CELL_REVEN_CEVEN:[3698, 3705, "       "]
         TABLE_ROW_EVEN:[3705, 3706, "|"]
         MdTableCellImpl:[3706, 3716, "    X     "]
           TABLE_CELL_REVEN_CODD:[3706, 3716, "    X     "]
         TABLE_ROW_EVEN:[3716, 3717, "|"]
         EOL:[3717, 3718, "\n"]
       MdTableRowImpl:[3718, 3861, "| &nb … sp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              |       |    X     |\n"]
         TABLE_ROW_ODD:[3718, 3719, "|"]
         MdTableCellImpl:[3719, 3840, " &nbs … p;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              "]
           TABLE_CELL_RODD_CODD:[3719, 3720, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[3720, 3744, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[3744, 3840, "Inspe … ctions to help you validate links, anchor refs, footnote refs                              "]
         TABLE_ROW_ODD:[3840, 3841, "|"]
         MdTableCellImpl:[3841, 3848, "       "]
           TABLE_CELL_RODD_CEVEN:[3841, 3848, "       "]
         TABLE_ROW_ODD:[3848, 3849, "|"]
         MdTableCellImpl:[3849, 3859, "    X     "]
           TABLE_CELL_RODD_CODD:[3849, 3859, "    X     "]
         TABLE_ROW_ODD:[3859, 3860, "|"]
         EOL:[3860, 3861, "\n"]
       MdTableRowImpl:[3861, 4004, "| &nb … sp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         |       |    X     |\n"]
         TABLE_ROW_EVEN:[3861, 3862, "|"]
         MdTableCellImpl:[3862, 3983, " &nbs … p;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         "]
           TABLE_CELL_REVEN_CODD:[3862, 3863, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[3863, 3887, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[3887, 3983, "Compl … ete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         "]
         TABLE_ROW_EVEN:[3983, 3984, "|"]
         MdTableCellImpl:[3984, 3991, "       "]
           TABLE_CELL_REVEN_CEVEN:[3984, 3991, "       "]
         TABLE_ROW_EVEN:[3991, 3992, "|"]
         MdTableCellImpl:[3992, 4002, "    X     "]
           TABLE_CELL_REVEN_CODD:[3992, 4002, "    X     "]
         TABLE_ROW_EVEN:[4002, 4003, "|"]
         EOL:[4003, 4004, "\n"]
       MdTableRowImpl:[4004, 4147, "| &nb … sp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           |       |    X     |\n"]
         TABLE_ROW_ODD:[4004, 4005, "|"]
         MdTableCellImpl:[4005, 4126, " &nbs … p;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           "]
           TABLE_CELL_RODD_CODD:[4005, 4006, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[4006, 4030, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[4030, 4126, "Jekyl … l front matter recognition in markdown documents                                           "]
         TABLE_ROW_ODD:[4126, 4127, "|"]
         MdTableCellImpl:[4127, 4134, "       "]
           TABLE_CELL_RODD_CEVEN:[4127, 4134, "       "]
         TABLE_ROW_ODD:[4134, 4135, "|"]
         MdTableCellImpl:[4135, 4145, "    X     "]
           TABLE_CELL_RODD_CODD:[4135, 4145, "    X     "]
         TABLE_ROW_ODD:[4145, 4146, "|"]
         EOL:[4146, 4147, "\n"]
       MdTableRowImpl:[4147, 4290, "| &nb … sp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    |       |    X     |\n"]
         TABLE_ROW_EVEN:[4147, 4148, "|"]
         MdTableCellImpl:[4148, 4269, " &nbs … p;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    "]
           TABLE_CELL_REVEN_CODD:[4148, 4149, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[4149, 4173, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[4173, 4209, "Emoji …  text to icon conversion using "]
           MdReferenceLinkImpl:[4209, 4228, "[Emoj … i Cheat Sheet]"]
             TABLE_CELL_REVEN_CODD_REFERENCE_LINK_REFERENCE_OPEN2:[4209, 4210, "["]
             MdReferenceLinkReferenceImpl:[4210, 4227, "Emoji …  Cheat Sheet"]
               REFERENCE_LINK_REFERENCE_LEAF:[4210, 4227, "Emoji …  Cheat Sheet"]
             TABLE_CELL_REVEN_CODD_REFERENCE_LINK_REFERENCE_CLOSE2:[4227, 4228, "]"]
           TABLE_CELL_REVEN_CODD:[4228, 4269, " or G … itHub emoji URLs                    "]
         TABLE_ROW_EVEN:[4269, 4270, "|"]
         MdTableCellImpl:[4270, 4277, "       "]
           TABLE_CELL_REVEN_CEVEN:[4270, 4277, "       "]
         TABLE_ROW_EVEN:[4277, 4278, "|"]
         MdTableCellImpl:[4278, 4288, "    X     "]
           TABLE_CELL_REVEN_CODD:[4278, 4288, "    X     "]
         TABLE_ROW_EVEN:[4288, 4289, "|"]
         EOL:[4289, 4290, "\n"]
       MdTableRowImpl:[4290, 4433, "| &nb … sp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       |       |    X     |\n"]
         TABLE_ROW_ODD:[4290, 4291, "|"]
         MdTableCellImpl:[4291, 4412, " &nbs … p;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       "]
           TABLE_CELL_RODD_CODD:[4291, 4292, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[4292, 4316, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[4316, 4412, "Wrap  … on typing and table formatting with column alignment                                       "]
         TABLE_ROW_ODD:[4412, 4413, "|"]
         MdTableCellImpl:[4413, 4420, "       "]
           TABLE_CELL_RODD_CEVEN:[4413, 4420, "       "]
         TABLE_ROW_ODD:[4420, 4421, "|"]
         MdTableCellImpl:[4421, 4431, "    X     "]
           TABLE_CELL_RODD_CODD:[4421, 4431, "    X     "]
         TABLE_ROW_ODD:[4431, 4432, "|"]
         EOL:[4432, 4433, "\n"]
       MdTableRowImpl:[4433, 4576, "| &nb … sp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  |       |    X     |\n"]
         TABLE_ROW_EVEN:[4433, 4434, "|"]
         MdTableCellImpl:[4434, 4555, " &nbs … p;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  "]
           TABLE_CELL_REVEN_CODD:[4434, 4435, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[4435, 4459, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[4459, 4555, "Chara … cter display width used for wrapping and table formatting                                  "]
         TABLE_ROW_EVEN:[4555, 4556, "|"]
         MdTableCellImpl:[4556, 4563, "       "]
           TABLE_CELL_REVEN_CEVEN:[4556, 4563, "       "]
         TABLE_ROW_EVEN:[4563, 4564, "|"]
         MdTableCellImpl:[4564, 4574, "    X     "]
           TABLE_CELL_REVEN_CODD:[4564, 4574, "    X     "]
         TABLE_ROW_EVEN:[4574, 4575, "|"]
         EOL:[4575, 4576, "\n"]
       MdTableRowImpl:[4576, 4719, "| &nb … sp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           |       |    X     |\n"]
         TABLE_ROW_ODD:[4576, 4577, "|"]
         MdTableCellImpl:[4577, 4698, " &nbs … p;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           "]
           TABLE_CELL_RODD_CODD:[4577, 4578, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[4578, 4602, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[4602, 4698, "Struc … ture view for Abbreviations, Headers, Tables, Footnotes, References and Document           "]
         TABLE_ROW_ODD:[4698, 4699, "|"]
         MdTableCellImpl:[4699, 4706, "       "]
           TABLE_CELL_RODD_CEVEN:[4699, 4706, "       "]
         TABLE_ROW_ODD:[4706, 4707, "|"]
         MdTableCellImpl:[4707, 4717, "    X     "]
           TABLE_CELL_RODD_CODD:[4707, 4717, "    X     "]
         TABLE_ROW_ODD:[4717, 4718, "|"]
         EOL:[4718, 4719, "\n"]
       MdTableRowImpl:[4719, 4862, "| &nb … sp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            |       |    X     |\n"]
         TABLE_ROW_EVEN:[4719, 4720, "|"]
         MdTableCellImpl:[4720, 4841, " &nbs … p;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            "]
           TABLE_CELL_REVEN_CODD:[4720, 4721, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[4721, 4745, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[4745, 4841, "Docum … ent formatting with text wrapping, list renumbering, aranging of elements, etc.            "]
         TABLE_ROW_EVEN:[4841, 4842, "|"]
         MdTableCellImpl:[4842, 4849, "       "]
           TABLE_CELL_REVEN_CEVEN:[4842, 4849, "       "]
         TABLE_ROW_EVEN:[4849, 4850, "|"]
         MdTableCellImpl:[4850, 4860, "    X     "]
           TABLE_CELL_REVEN_CODD:[4850, 4860, "    X     "]
         TABLE_ROW_EVEN:[4860, 4861, "|"]
         EOL:[4861, 4862, "\n"]
       MdTableRowImpl:[4862, 5005, "| &nb … sp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   |       |    X     |\n"]
         TABLE_ROW_ODD:[4862, 4863, "|"]
         MdTableCellImpl:[4863, 4984, " &nbs … p;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   "]
           TABLE_CELL_RODD_CODD:[4863, 4864, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[4864, 4888, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[4888, 4984, "Table …  of Contents generation for any markdown parser, with many style options                   "]
         TABLE_ROW_ODD:[4984, 4985, "|"]
         MdTableCellImpl:[4985, 4992, "       "]
           TABLE_CELL_RODD_CEVEN:[4985, 4992, "       "]
         TABLE_ROW_ODD:[4992, 4993, "|"]
         MdTableCellImpl:[4993, 5003, "    X     "]
           TABLE_CELL_RODD_CODD:[4993, 5003, "    X     "]
         TABLE_ROW_ODD:[5003, 5004, "|"]
         EOL:[5004, 5005, "\n"]
       MdTableRowImpl:[5005, 5148, "| **A … s you type automation**                                                                                              |       |    X     |\n"]
         TABLE_ROW_EVEN:[5005, 5006, "|"]
         MdTableCellImpl:[5006, 5127, " **As …  you type automation**                                                                                              "]
           TABLE_CELL_REVEN_CODD:[5006, 5007, " "]
           MdInlineBoldImpl:[5007, 5033, "**As  … you type automation**"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[5007, 5009, "**"]
             TABLE_CELL_REVEN_CODD_BOLD_TEXT:[5009, 5031, "As yo … u type automation"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[5031, 5033, "**"]
           TABLE_CELL_REVEN_CODD:[5033, 5127, "      …                                                                                          "]
         TABLE_ROW_EVEN:[5127, 5128, "|"]
         MdTableCellImpl:[5128, 5135, "       "]
           TABLE_CELL_REVEN_CEVEN:[5128, 5135, "       "]
         TABLE_ROW_EVEN:[5135, 5136, "|"]
         MdTableCellImpl:[5136, 5146, "    X     "]
           TABLE_CELL_REVEN_CODD:[5136, 5146, "    X     "]
         TABLE_ROW_EVEN:[5146, 5147, "|"]
         EOL:[5147, 5148, "\n"]
       MdTableRowImpl:[5148, 5291, "| &nb … sp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    |       |    X     |\n"]
         TABLE_ROW_ODD:[5148, 5149, "|"]
         MdTableCellImpl:[5149, 5270, " &nbs … p;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    "]
           TABLE_CELL_RODD_CODD:[5149, 5150, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[5150, 5174, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[5174, 5270, "Doubl … e of bold/emphasis markers and remove inserted ones if a space is typed                    "]
         TABLE_ROW_ODD:[5270, 5271, "|"]
         MdTableCellImpl:[5271, 5278, "       "]
           TABLE_CELL_RODD_CEVEN:[5271, 5278, "       "]
         TABLE_ROW_ODD:[5278, 5279, "|"]
         MdTableCellImpl:[5279, 5289, "    X     "]
           TABLE_CELL_RODD_CODD:[5279, 5289, "    X     "]
         TABLE_ROW_ODD:[5289, 5290, "|"]
         EOL:[5290, 5291, "\n"]
       MdTableRowImpl:[5291, 5434, "| &nb … sp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     |       |    X     |\n"]
         TABLE_ROW_EVEN:[5291, 5292, "|"]
         MdTableCellImpl:[5292, 5413, " &nbs … p;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     "]
           TABLE_CELL_REVEN_CODD:[5292, 5293, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[5293, 5317, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[5317, 5413, "Wrap  … text blocks to margins and indentation                                                     "]
         TABLE_ROW_EVEN:[5413, 5414, "|"]
         MdTableCellImpl:[5414, 5421, "       "]
           TABLE_CELL_REVEN_CEVEN:[5414, 5421, "       "]
         TABLE_ROW_EVEN:[5421, 5422, "|"]
         MdTableCellImpl:[5422, 5432, "    X     "]
           TABLE_CELL_REVEN_CODD:[5422, 5432, "    X     "]
         TABLE_ROW_EVEN:[5432, 5433, "|"]
         EOL:[5433, 5434, "\n"]
       MdTableRowImpl:[5434, 5577, "| &nb … sp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing `#` marker                                                        |       |    X     |\n"]
         TABLE_ROW_ODD:[5434, 5435, "|"]
         MdTableCellImpl:[5435, 5556, " &nbs … p;&nbsp;&nbsp;&nbsp;ATX headers to match trailing `#` marker                                                        "]
           TABLE_CELL_RODD_CODD:[5435, 5436, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[5436, 5460, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[5460, 5490, "ATX h … eaders to match trailing "]
           MdInlineCodeImpl:[5490, 5493, "`#`"]
             CODE_MARKER:[5490, 5491, "`"]
             TABLE_CELL_RODD_CODD_CODE_TEXT:[5491, 5492, "#"]
             CODE_MARKER:[5492, 5493, "`"]
           TABLE_CELL_RODD_CODD:[5493, 5556, " mark … er                                                        "]
         TABLE_ROW_ODD:[5556, 5557, "|"]
         MdTableCellImpl:[5557, 5564, "       "]
           TABLE_CELL_RODD_CEVEN:[5557, 5564, "       "]
         TABLE_ROW_ODD:[5564, 5565, "|"]
         MdTableCellImpl:[5565, 5575, "    X     "]
           TABLE_CELL_RODD_CODD:[5565, 5575, "    X     "]
         TABLE_ROW_ODD:[5575, 5576, "|"]
         EOL:[5576, 5577, "\n"]
       MdTableRowImpl:[5577, 5720, "| &nb … sp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   |       |    X     |\n"]
         TABLE_ROW_EVEN:[5577, 5578, "|"]
         MdTableCellImpl:[5578, 5699, " &nbs … p;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   "]
           TABLE_CELL_REVEN_CODD:[5578, 5579, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[5579, 5603, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[5603, 5699, "Setex … t headers to match marker length to text                                                   "]
         TABLE_ROW_EVEN:[5699, 5700, "|"]
         MdTableCellImpl:[5700, 5707, "       "]
           TABLE_CELL_REVEN_CEVEN:[5700, 5707, "       "]
         TABLE_ROW_EVEN:[5707, 5708, "|"]
         MdTableCellImpl:[5708, 5718, "    X     "]
           TABLE_CELL_REVEN_CODD:[5708, 5718, "    X     "]
         TABLE_ROW_EVEN:[5718, 5719, "|"]
         EOL:[5719, 5720, "\n"]
       MdTableRowImpl:[5720, 5863, "| &nb … sp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        |       |    X     |\n"]
         TABLE_ROW_ODD:[5720, 5721, "|"]
         MdTableCellImpl:[5721, 5842, " &nbs … p;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        "]
           TABLE_CELL_RODD_CODD:[5721, 5722, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[5722, 5746, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[5746, 5842, "Forma … t tables to pad column width, column alignment and spanning columns                        "]
         TABLE_ROW_ODD:[5842, 5843, "|"]
         MdTableCellImpl:[5843, 5850, "       "]
           TABLE_CELL_RODD_CEVEN:[5843, 5850, "       "]
         TABLE_ROW_ODD:[5850, 5851, "|"]
         MdTableCellImpl:[5851, 5861, "    X     "]
           TABLE_CELL_RODD_CODD:[5851, 5861, "    X     "]
         TABLE_ROW_ODD:[5861, 5862, "|"]
         EOL:[5862, 5863, "\n"]
       MdTableRowImpl:[5863, 6006, "| &nb … sp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 |       |    X     |\n"]
         TABLE_ROW_EVEN:[5863, 5864, "|"]
         MdTableCellImpl:[5864, 5985, " &nbs … p;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 "]
           TABLE_CELL_REVEN_CODD:[5864, 5865, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[5865, 5889, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[5889, 5920, "Auto  … insert empty table row on "]
           TABLE_CELL_REVEN_CODD_INLINE_HTML:[5920, 5925, "<kbd>"]
           TABLE_CELL_REVEN_CODD:[5925, 5930, "ENTER"]
           TABLE_CELL_REVEN_CODD_INLINE_HTML:[5930, 5936, "</kbd>"]
           TABLE_CELL_REVEN_CODD:[5936, 5985, "      …                                             "]
         TABLE_ROW_EVEN:[5985, 5986, "|"]
         MdTableCellImpl:[5986, 5993, "       "]
           TABLE_CELL_REVEN_CEVEN:[5986, 5993, "       "]
         TABLE_ROW_EVEN:[5993, 5994, "|"]
         MdTableCellImpl:[5994, 6004, "    X     "]
           TABLE_CELL_REVEN_CODD:[5994, 6004, "    X     "]
         TABLE_ROW_EVEN:[6004, 6005, "|"]
         EOL:[6005, 6006, "\n"]
       MdTableRowImpl:[6006, 6149, "| &nb … sp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      |       |    X     |\n"]
         TABLE_ROW_ODD:[6006, 6007, "|"]
         MdTableCellImpl:[6007, 6128, " &nbs … p;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      "]
           TABLE_CELL_RODD_CODD:[6007, 6008, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[6008, 6032, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[6032, 6070, "Auto  … delete empty table row/column on "]
           TABLE_CELL_RODD_CODD_INLINE_HTML:[6070, 6075, "<kbd>"]
           TABLE_CELL_RODD_CODD:[6075, 6084, "BACKSPACE"]
           TABLE_CELL_RODD_CODD_INLINE_HTML:[6084, 6090, "</kbd>"]
           TABLE_CELL_RODD_CODD:[6090, 6128, "      …                                  "]
         TABLE_ROW_ODD:[6128, 6129, "|"]
         MdTableCellImpl:[6129, 6136, "       "]
           TABLE_CELL_RODD_CEVEN:[6129, 6136, "       "]
         TABLE_ROW_ODD:[6136, 6137, "|"]
         MdTableCellImpl:[6137, 6147, "    X     "]
           TABLE_CELL_RODD_CODD:[6137, 6147, "    X     "]
         TABLE_ROW_ODD:[6147, 6148, "|"]
         EOL:[6148, 6149, "\n"]
       MdTableRowImpl:[6149, 6292, "| &nb … sp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          |       |    X     |\n"]
         TABLE_ROW_EVEN:[6149, 6150, "|"]
         MdTableCellImpl:[6150, 6271, " &nbs … p;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          "]
           TABLE_CELL_REVEN_CODD:[6150, 6151, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[6151, 6175, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[6175, 6271, "Auto  … insert table column when typing before first column or after last column of table          "]
         TABLE_ROW_EVEN:[6271, 6272, "|"]
         MdTableCellImpl:[6272, 6279, "       "]
           TABLE_CELL_REVEN_CEVEN:[6272, 6279, "       "]
         TABLE_ROW_EVEN:[6279, 6280, "|"]
         MdTableCellImpl:[6280, 6290, "    X     "]
           TABLE_CELL_REVEN_CODD:[6280, 6290, "    X     "]
         TABLE_ROW_EVEN:[6290, 6291, "|"]
         EOL:[6291, 6292, "\n"]
       MdTableRowImpl:[6292, 6435, "| &nb … sp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  |       |    X     |\n"]
         TABLE_ROW_ODD:[6292, 6293, "|"]
         MdTableCellImpl:[6293, 6414, " &nbs … p;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  "]
           TABLE_CELL_RODD_CODD:[6293, 6294, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[6294, 6318, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[6318, 6414, "Actio … ns to insert: table, row or column; delete: row or column                                  "]
         TABLE_ROW_ODD:[6414, 6415, "|"]
         MdTableCellImpl:[6415, 6422, "       "]
           TABLE_CELL_RODD_CEVEN:[6415, 6422, "       "]
         TABLE_ROW_ODD:[6422, 6423, "|"]
         MdTableCellImpl:[6423, 6433, "    X     "]
           TABLE_CELL_RODD_CODD:[6423, 6433, "    X     "]
         TABLE_ROW_ODD:[6433, 6434, "|"]
         EOL:[6434, 6435, "\n"]
       MdTableRowImpl:[6435, 6578, "| &nb … sp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       |       |    X     |\n"]
         TABLE_ROW_EVEN:[6435, 6436, "|"]
         MdTableCellImpl:[6436, 6557, " &nbs … p;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       "]
           TABLE_CELL_REVEN_CODD:[6436, 6437, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[6437, 6461, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[6461, 6486, "Auto  … insert list item on "]
           TABLE_CELL_REVEN_CODD_INLINE_HTML:[6486, 6491, "<kbd>"]
           TABLE_CELL_REVEN_CODD:[6491, 6496, "ENTER"]
           TABLE_CELL_REVEN_CODD_INLINE_HTML:[6496, 6502, "</kbd>"]
           TABLE_CELL_REVEN_CODD:[6502, 6557, "      …                                                   "]
         TABLE_ROW_EVEN:[6557, 6558, "|"]
         MdTableCellImpl:[6558, 6565, "       "]
           TABLE_CELL_REVEN_CEVEN:[6558, 6565, "       "]
         TABLE_ROW_EVEN:[6565, 6566, "|"]
         MdTableCellImpl:[6566, 6576, "    X     "]
           TABLE_CELL_REVEN_CODD:[6566, 6576, "    X     "]
         TABLE_ROW_EVEN:[6576, 6577, "|"]
         EOL:[6577, 6578, "\n"]
       MdTableRowImpl:[6578, 6721, "| &nb … sp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 |       |    X     |\n"]
         TABLE_ROW_ODD:[6578, 6579, "|"]
         MdTableCellImpl:[6579, 6700, " &nbs … p;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 "]
           TABLE_CELL_RODD_CODD:[6579, 6580, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[6580, 6604, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[6604, 6635, "Auto  … delete empty list item on "]
           TABLE_CELL_RODD_CODD_INLINE_HTML:[6635, 6640, "<kbd>"]
           TABLE_CELL_RODD_CODD:[6640, 6645, "ENTER"]
           TABLE_CELL_RODD_CODD_INLINE_HTML:[6645, 6651, "</kbd>"]
           TABLE_CELL_RODD_CODD:[6651, 6700, "      …                                             "]
         TABLE_ROW_ODD:[6700, 6701, "|"]
         MdTableCellImpl:[6701, 6708, "       "]
           TABLE_CELL_RODD_CEVEN:[6701, 6708, "       "]
         TABLE_ROW_ODD:[6708, 6709, "|"]
         MdTableCellImpl:[6709, 6719, "    X     "]
           TABLE_CELL_RODD_CODD:[6709, 6719, "    X     "]
         TABLE_ROW_ODD:[6719, 6720, "|"]
         EOL:[6720, 6721, "\n"]
       MdTableRowImpl:[6721, 6864, "| &nb … sp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             |       |    X     |\n"]
         TABLE_ROW_EVEN:[6721, 6722, "|"]
         MdTableCellImpl:[6722, 6843, " &nbs … p;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             "]
           TABLE_CELL_REVEN_CODD:[6722, 6723, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[6723, 6747, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[6747, 6778, "Auto  … delete empty list item on "]
           TABLE_CELL_REVEN_CODD_INLINE_HTML:[6778, 6783, "<kbd>"]
           TABLE_CELL_REVEN_CODD:[6783, 6792, "BACKSPACE"]
           TABLE_CELL_REVEN_CODD_INLINE_HTML:[6792, 6798, "</kbd>"]
           TABLE_CELL_REVEN_CODD:[6798, 6843, "      …                                         "]
         TABLE_ROW_EVEN:[6843, 6844, "|"]
         MdTableCellImpl:[6844, 6851, "       "]
           TABLE_CELL_REVEN_CEVEN:[6844, 6851, "       "]
         TABLE_ROW_EVEN:[6851, 6852, "|"]
         MdTableCellImpl:[6852, 6862, "    X     "]
           TABLE_CELL_REVEN_CODD:[6852, 6862, "    X     "]
         TABLE_ROW_EVEN:[6862, 6863, "|"]
         EOL:[6863, 6864, "\n"]
       MdTableRowImpl:[6864, 7007, "| &nb … sp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       |       |    X     |\n"]
         TABLE_ROW_ODD:[6864, 6865, "|"]
         MdTableCellImpl:[6865, 6986, " &nbs … p;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       "]
           TABLE_CELL_RODD_CODD:[6865, 6866, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[6866, 6890, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[6890, 6986, "Inden … t or un-indent list item toolbar buttons and actions                                       "]
         TABLE_ROW_ODD:[6986, 6987, "|"]
         MdTableCellImpl:[6987, 6994, "       "]
           TABLE_CELL_RODD_CEVEN:[6987, 6994, "       "]
         TABLE_ROW_ODD:[6994, 6995, "|"]
         MdTableCellImpl:[6995, 7005, "    X     "]
           TABLE_CELL_RODD_CODD:[6995, 7005, "    X     "]
         TABLE_ROW_ODD:[7005, 7006, "|"]
         EOL:[7006, 7007, "\n"]
       MdTableRowImpl:[7007, 7150, "| **C … ode Completions**                                                                                                    |       |    X     |\n"]
         TABLE_ROW_EVEN:[7007, 7008, "|"]
         MdTableCellImpl:[7008, 7129, " **Co … de Completions**                                                                                                    "]
           TABLE_CELL_REVEN_CODD:[7008, 7009, " "]
           MdInlineBoldImpl:[7009, 7029, "**Cod … e Completions**"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[7009, 7011, "**"]
             TABLE_CELL_REVEN_CODD_BOLD_TEXT:[7011, 7027, "Code  … Completions"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[7027, 7029, "**"]
           TABLE_CELL_REVEN_CODD:[7029, 7129, "      …                                                                                                "]
         TABLE_ROW_EVEN:[7129, 7130, "|"]
         MdTableCellImpl:[7130, 7137, "       "]
           TABLE_CELL_REVEN_CEVEN:[7130, 7137, "       "]
         TABLE_ROW_EVEN:[7137, 7138, "|"]
         MdTableCellImpl:[7138, 7148, "    X     "]
           TABLE_CELL_REVEN_CODD:[7138, 7148, "    X     "]
         TABLE_ROW_EVEN:[7148, 7149, "|"]
         EOL:[7149, 7150, "\n"]
       MdTableRowImpl:[7150, 7293, "| &nb … sp;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            |       |    X     |\n"]
         TABLE_ROW_ODD:[7150, 7151, "|"]
         MdTableCellImpl:[7151, 7272, " &nbs … p;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            "]
           TABLE_CELL_RODD_CODD:[7151, 7152, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[7152, 7176, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[7176, 7216, "Absol … ute link address completions using "]
           MdAutoLinkImpl:[7216, 7224, "https://"]
             MdAutoLinkRefImpl:[7216, 7224, "https://"]
               AUTO_LINK_REF:[7216, 7224, "https://"]
           TABLE_CELL_RODD_CODD:[7224, 7229, " and "]
           MdAutoLinkImpl:[7229, 7236, "file://"]
             MdAutoLinkRefImpl:[7229, 7236, "file://"]
               AUTO_LINK_REF:[7229, 7236, "file://"]
           TABLE_CELL_RODD_CODD:[7236, 7272, " form … ats                            "]
         TABLE_ROW_ODD:[7272, 7273, "|"]
         MdTableCellImpl:[7273, 7280, "       "]
           TABLE_CELL_RODD_CEVEN:[7273, 7280, "       "]
         TABLE_ROW_ODD:[7280, 7281, "|"]
         MdTableCellImpl:[7281, 7291, "    X     "]
           TABLE_CELL_RODD_CODD:[7281, 7291, "    X     "]
         TABLE_ROW_ODD:[7291, 7292, "|"]
         EOL:[7292, 7293, "\n"]
       MdTableRowImpl:[7293, 7436, "| &nb … sp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  |       |    X     |\n"]
         TABLE_ROW_EVEN:[7293, 7294, "|"]
         MdTableCellImpl:[7294, 7415, " &nbs … p;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  "]
           TABLE_CELL_REVEN_CODD:[7294, 7295, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[7295, 7319, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[7319, 7415, "Expli … cit and Image links are GitHub wiki aware                                                  "]
         TABLE_ROW_EVEN:[7415, 7416, "|"]
         MdTableCellImpl:[7416, 7423, "       "]
           TABLE_CELL_REVEN_CEVEN:[7416, 7423, "       "]
         TABLE_ROW_EVEN:[7423, 7424, "|"]
         MdTableCellImpl:[7424, 7434, "    X     "]
           TABLE_CELL_REVEN_CODD:[7424, 7434, "    X     "]
         TABLE_ROW_EVEN:[7434, 7435, "|"]
         EOL:[7435, 7436, "\n"]
       MdTableRowImpl:[7436, 7579, "| &nb … sp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after `issues/` link address and in text                             |       |    X     |\n"]
         TABLE_ROW_ODD:[7436, 7437, "|"]
         MdTableCellImpl:[7437, 7558, " &nbs … p;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after `issues/` link address and in text                             "]
           TABLE_CELL_RODD_CODD:[7437, 7438, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[7438, 7462, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[7462, 7495, "GitHu … b Issue # Completions after "]
           MdInlineCodeImpl:[7495, 7504, "`issues/`"]
             CODE_MARKER:[7495, 7496, "`"]
             TABLE_CELL_RODD_CODD_CODE_TEXT:[7496, 7503, "issues/"]
             CODE_MARKER:[7503, 7504, "`"]
           TABLE_CELL_RODD_CODD:[7504, 7558, " link …  address and in text                             "]
         TABLE_ROW_ODD:[7558, 7559, "|"]
         MdTableCellImpl:[7559, 7566, "       "]
           TABLE_CELL_RODD_CEVEN:[7559, 7566, "       "]
         TABLE_ROW_ODD:[7566, 7567, "|"]
         MdTableCellImpl:[7567, 7577, "    X     "]
           TABLE_CELL_RODD_CODD:[7567, 7577, "    X     "]
         TABLE_ROW_ODD:[7577, 7578, "|"]
         EOL:[7578, 7579, "\n"]
       MdTableRowImpl:[7579, 7722, "| &nb … sp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 |       |    X     |\n"]
         TABLE_ROW_EVEN:[7579, 7580, "|"]
         MdTableCellImpl:[7580, 7701, " &nbs … p;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 "]
           TABLE_CELL_REVEN_CODD:[7580, 7581, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[7581, 7605, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[7605, 7701, "GitHu … b special links: Issues, Pull requests, Graphs, and Pulse.                                 "]
         TABLE_ROW_EVEN:[7701, 7702, "|"]
         MdTableCellImpl:[7702, 7709, "       "]
           TABLE_CELL_REVEN_CEVEN:[7702, 7709, "       "]
         TABLE_ROW_EVEN:[7709, 7710, "|"]
         MdTableCellImpl:[7710, 7720, "    X     "]
           TABLE_CELL_REVEN_CODD:[7710, 7720, "    X     "]
         TABLE_ROW_EVEN:[7720, 7721, "|"]
         EOL:[7721, 7722, "\n"]
       MdTableRowImpl:[7722, 7865, "| &nb … sp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 |       |    X     |\n"]
         TABLE_ROW_ODD:[7722, 7723, "|"]
         MdTableCellImpl:[7723, 7844, " &nbs … p;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 "]
           TABLE_CELL_RODD_CODD:[7723, 7724, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[7724, 7748, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[7748, 7844, "Link  … address completions for non-markdown files                                                 "]
         TABLE_ROW_ODD:[7844, 7845, "|"]
         MdTableCellImpl:[7845, 7852, "       "]
           TABLE_CELL_RODD_CEVEN:[7845, 7852, "       "]
         TABLE_ROW_ODD:[7852, 7853, "|"]
         MdTableCellImpl:[7853, 7863, "    X     "]
           TABLE_CELL_RODD_CODD:[7853, 7863, "    X     "]
         TABLE_ROW_ODD:[7863, 7864, "|"]
         EOL:[7864, 7865, "\n"]
       MdTableRowImpl:[7865, 8008, "| &nb … sp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 |       |    X     |\n"]
         TABLE_ROW_EVEN:[7865, 7866, "|"]
         MdTableCellImpl:[7866, 7987, " &nbs … p;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 "]
           TABLE_CELL_REVEN_CODD:[7866, 7867, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[7867, 7891, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[7891, 7987, "Emoji …  text shortcuts completion                                                                 "]
         TABLE_ROW_EVEN:[7987, 7988, "|"]
         MdTableCellImpl:[7988, 7995, "       "]
           TABLE_CELL_REVEN_CEVEN:[7988, 7995, "       "]
         TABLE_ROW_EVEN:[7995, 7996, "|"]
         MdTableCellImpl:[7996, 8006, "    X     "]
           TABLE_CELL_REVEN_CODD:[7996, 8006, "    X     "]
         TABLE_ROW_EVEN:[8006, 8007, "|"]
         EOL:[8007, 8008, "\n"]
       MdTableRowImpl:[8008, 8151, "| &nb … sp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                |       |    X     |\n"]
         TABLE_ROW_ODD:[8008, 8009, "|"]
         MdTableCellImpl:[8009, 8130, " &nbs … p;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                "]
           TABLE_CELL_RODD_CODD:[8009, 8010, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[8010, 8034, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[8034, 8130, "Java  … class, field and method completions in inline code elements                                "]
         TABLE_ROW_ODD:[8130, 8131, "|"]
         MdTableCellImpl:[8131, 8138, "       "]
           TABLE_CELL_RODD_CEVEN:[8131, 8138, "       "]
         TABLE_ROW_ODD:[8138, 8139, "|"]
         MdTableCellImpl:[8139, 8149, "    X     "]
           TABLE_CELL_RODD_CODD:[8139, 8149, "    X     "]
         TABLE_ROW_ODD:[8149, 8150, "|"]
         EOL:[8150, 8151, "\n"]
       MdTableRowImpl:[8151, 8294, "| **I … ntention Actions**                                                                                                   |       |    X     |\n"]
         TABLE_ROW_EVEN:[8151, 8152, "|"]
         MdTableCellImpl:[8152, 8273, " **In … tention Actions**                                                                                                   "]
           TABLE_CELL_REVEN_CODD:[8152, 8153, " "]
           MdInlineBoldImpl:[8153, 8174, "**Int … ention Actions**"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[8153, 8155, "**"]
             TABLE_CELL_REVEN_CODD_BOLD_TEXT:[8155, 8172, "Inten … tion Actions"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[8172, 8174, "**"]
           TABLE_CELL_REVEN_CODD:[8174, 8273, "      …                                                                                               "]
         TABLE_ROW_EVEN:[8273, 8274, "|"]
         MdTableCellImpl:[8274, 8281, "       "]
           TABLE_CELL_REVEN_CEVEN:[8274, 8281, "       "]
         TABLE_ROW_EVEN:[8281, 8282, "|"]
         MdTableCellImpl:[8282, 8292, "    X     "]
           TABLE_CELL_REVEN_CODD:[8282, 8292, "    X     "]
         TABLE_ROW_EVEN:[8292, 8293, "|"]
         EOL:[8293, 8294, "\n"]
       MdTableRowImpl:[8294, 8437, "| &nb … sp;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               |       |    X     |\n"]
         TABLE_ROW_ODD:[8294, 8295, "|"]
         MdTableCellImpl:[8295, 8416, " &nbs … p;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               "]
           TABLE_CELL_RODD_CODD:[8295, 8296, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[8296, 8320, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[8320, 8357, "Chang … e between relative and absolute "]
           MdAutoLinkImpl:[8357, 8365, "https://"]
             MdAutoLinkRefImpl:[8357, 8365, "https://"]
               AUTO_LINK_REF:[8357, 8365, "https://"]
           TABLE_CELL_RODD_CODD:[8365, 8416, " link …  addresses via intention action               "]
         TABLE_ROW_ODD:[8416, 8417, "|"]
         MdTableCellImpl:[8417, 8424, "       "]
           TABLE_CELL_RODD_CEVEN:[8417, 8424, "       "]
         TABLE_ROW_ODD:[8424, 8425, "|"]
         MdTableCellImpl:[8425, 8435, "    X     "]
           TABLE_CELL_RODD_CODD:[8425, 8435, "    X     "]
         TABLE_ROW_ODD:[8435, 8436, "|"]
         EOL:[8436, 8437, "\n"]
       MdTableRowImpl:[8437, 8580, "| &nb … sp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     |       |    X     |\n"]
         TABLE_ROW_EVEN:[8437, 8438, "|"]
         MdTableCellImpl:[8438, 8559, " &nbs … p;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     "]
           TABLE_CELL_REVEN_CODD:[8438, 8439, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[8439, 8463, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[8463, 8559, "Chang … e between wiki links and explicit link                                                     "]
         TABLE_ROW_EVEN:[8559, 8560, "|"]
         MdTableCellImpl:[8560, 8567, "       "]
           TABLE_CELL_REVEN_CEVEN:[8560, 8567, "       "]
         TABLE_ROW_EVEN:[8567, 8568, "|"]
         MdTableCellImpl:[8568, 8578, "    X     "]
           TABLE_CELL_REVEN_CODD:[8568, 8578, "    X     "]
         TABLE_ROW_EVEN:[8578, 8579, "|"]
         EOL:[8579, 8580, "\n"]
       MdTableRowImpl:[8580, 8723, "| &nb … sp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        |       |    X     |\n"]
         TABLE_ROW_ODD:[8580, 8581, "|"]
         MdTableCellImpl:[8581, 8702, " &nbs … p;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        "]
           TABLE_CELL_RODD_CODD:[8581, 8582, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[8582, 8606, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[8606, 8702, "Inten … tions for links, wiki links, references and headers                                        "]
         TABLE_ROW_ODD:[8702, 8703, "|"]
         MdTableCellImpl:[8703, 8710, "       "]
           TABLE_CELL_RODD_CEVEN:[8703, 8710, "       "]
         TABLE_ROW_ODD:[8710, 8711, "|"]
         MdTableCellImpl:[8711, 8721, "    X     "]
           TABLE_CELL_RODD_CODD:[8711, 8721, "    X     "]
         TABLE_ROW_ODD:[8721, 8722, "|"]
         EOL:[8722, 8723, "\n"]
       MdTableRowImpl:[8723, 8866, "| &nb … sp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         |       |    X     |\n"]
         TABLE_ROW_EVEN:[8723, 8724, "|"]
         MdTableCellImpl:[8724, 8845, " &nbs … p;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         "]
           TABLE_CELL_REVEN_CODD:[8724, 8725, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[8725, 8749, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[8749, 8845, "Inten … tion to format Setext Header marker to match marker length to text                         "]
         TABLE_ROW_EVEN:[8845, 8846, "|"]
         MdTableCellImpl:[8846, 8853, "       "]
           TABLE_CELL_REVEN_CEVEN:[8846, 8853, "       "]
         TABLE_ROW_EVEN:[8853, 8854, "|"]
         MdTableCellImpl:[8854, 8864, "    X     "]
           TABLE_CELL_REVEN_CODD:[8854, 8864, "    X     "]
         TABLE_ROW_EVEN:[8864, 8865, "|"]
         EOL:[8865, 8866, "\n"]
       MdTableRowImpl:[8866, 9009, "| &nb … sp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      |       |    X     |\n"]
         TABLE_ROW_ODD:[8866, 8867, "|"]
         MdTableCellImpl:[8867, 8988, " &nbs … p;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      "]
           TABLE_CELL_RODD_CODD:[8867, 8868, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[8868, 8892, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[8892, 8988, "Inten … tion to swap Setext/Atx header format                                                      "]
         TABLE_ROW_ODD:[8988, 8989, "|"]
         MdTableCellImpl:[8989, 8996, "       "]
           TABLE_CELL_RODD_CEVEN:[8989, 8996, "       "]
         TABLE_ROW_ODD:[8996, 8997, "|"]
         MdTableCellImpl:[8997, 9007, "    X     "]
           TABLE_CELL_RODD_CODD:[8997, 9007, "    X     "]
         TABLE_ROW_ODD:[9007, 9008, "|"]
         EOL:[9008, 9009, "\n"]
       MdTableRowImpl:[9009, 9152, "| &nb … sp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    |       |    X     |\n"]
         TABLE_ROW_EVEN:[9009, 9010, "|"]
         MdTableCellImpl:[9010, 9131, " &nbs … p;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    "]
           TABLE_CELL_REVEN_CODD:[9010, 9011, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[9011, 9035, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[9035, 9131, "Updat … e table of contents quick fix intention                                                    "]
         TABLE_ROW_EVEN:[9131, 9132, "|"]
         MdTableCellImpl:[9132, 9139, "       "]
           TABLE_CELL_REVEN_CEVEN:[9132, 9139, "       "]
         TABLE_ROW_EVEN:[9139, 9140, "|"]
         MdTableCellImpl:[9140, 9150, "    X     "]
           TABLE_CELL_REVEN_CODD:[9140, 9150, "    X     "]
         TABLE_ROW_EVEN:[9150, 9151, "|"]
         EOL:[9151, 9152, "\n"]
       MdTableRowImpl:[9152, 9295, "| &nb … sp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           |       |    X     |\n"]
         TABLE_ROW_ODD:[9152, 9153, "|"]
         MdTableCellImpl:[9153, 9274, " &nbs … p;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           "]
           TABLE_CELL_RODD_CODD:[9153, 9154, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[9154, 9178, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[9178, 9274, "Inten … tion to edit Table of Contents style options dialog with preview                           "]
         TABLE_ROW_ODD:[9274, 9275, "|"]
         MdTableCellImpl:[9275, 9282, "       "]
           TABLE_CELL_RODD_CEVEN:[9275, 9282, "       "]
         TABLE_ROW_ODD:[9282, 9283, "|"]
         MdTableCellImpl:[9283, 9293, "    X     "]
           TABLE_CELL_RODD_CODD:[9283, 9293, "    X     "]
         TABLE_ROW_ODD:[9293, 9294, "|"]
         EOL:[9294, 9295, "\n"]
       MdTableRowImpl:[9295, 9438, "| **R … efactoring**                                                                                                         |       |    X     |\n"]
         TABLE_ROW_EVEN:[9295, 9296, "|"]
         MdTableCellImpl:[9296, 9417, " **Re … factoring**                                                                                                         "]
           TABLE_CELL_REVEN_CODD:[9296, 9297, " "]
           MdInlineBoldImpl:[9297, 9312, "**Ref … actoring**"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[9297, 9299, "**"]
             TABLE_CELL_REVEN_CODD_BOLD_TEXT:[9299, 9310, "Refac … toring"]
             TABLE_CELL_REVEN_CODD_BOLD_MARKER:[9310, 9312, "**"]
           TABLE_CELL_REVEN_CODD:[9312, 9417, "      …                                                                                                     "]
         TABLE_ROW_EVEN:[9417, 9418, "|"]
         MdTableCellImpl:[9418, 9425, "       "]
           TABLE_CELL_REVEN_CEVEN:[9418, 9425, "       "]
         TABLE_ROW_EVEN:[9425, 9426, "|"]
         MdTableCellImpl:[9426, 9436, "    X     "]
           TABLE_CELL_REVEN_CODD:[9426, 9436, "    X     "]
         TABLE_ROW_EVEN:[9436, 9437, "|"]
         EOL:[9437, 9438, "\n"]
       MdTableRowImpl:[9438, 9581, "| &nb … sp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki |       |    X     |\n"]
         TABLE_ROW_ODD:[9438, 9439, "|"]
         MdTableCellImpl:[9439, 9560, " &nbs … p;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki "]
           TABLE_CELL_RODD_CODD:[9439, 9440, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[9440, 9464, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[9464, 9560, "Autom … atic change from wiki link to explicit link when link target file is moved out of the wiki "]
         TABLE_ROW_ODD:[9560, 9561, "|"]
         MdTableCellImpl:[9561, 9568, "       "]
           TABLE_CELL_RODD_CEVEN:[9561, 9568, "       "]
         TABLE_ROW_ODD:[9568, 9569, "|"]
         MdTableCellImpl:[9569, 9579, "    X     "]
           TABLE_CELL_RODD_CODD:[9569, 9579, "    X     "]
         TABLE_ROW_ODD:[9579, 9580, "|"]
         EOL:[9580, 9581, "\n"]
       MdTableRowImpl:[9581, 9724, "| &nb … sp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            |       |    X     |\n"]
         TABLE_ROW_EVEN:[9581, 9582, "|"]
         MdTableCellImpl:[9582, 9703, " &nbs … p;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            "]
           TABLE_CELL_REVEN_CODD:[9582, 9583, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[9583, 9607, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[9607, 9703, "File  … move refactoring of contained links. This completes the refactoring feature set            "]
         TABLE_ROW_EVEN:[9703, 9704, "|"]
         MdTableCellImpl:[9704, 9711, "       "]
           TABLE_CELL_REVEN_CEVEN:[9704, 9711, "       "]
         TABLE_ROW_EVEN:[9711, 9712, "|"]
         MdTableCellImpl:[9712, 9722, "    X     "]
           TABLE_CELL_REVEN_CODD:[9712, 9722, "    X     "]
         TABLE_ROW_EVEN:[9722, 9723, "|"]
         EOL:[9723, 9724, "\n"]
       MdTableRowImpl:[9724, 9867, "| &nb … sp;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                |       |    X     |\n"]
         TABLE_ROW_ODD:[9724, 9725, "|"]
         MdTableCellImpl:[9725, 9846, " &nbs … p;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                "]
           TABLE_CELL_RODD_CODD:[9725, 9726, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[9726, 9750, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[9750, 9769, "Refac … toring for /, "]
           MdAutoLinkImpl:[9769, 9777, "https://"]
             MdAutoLinkRefImpl:[9769, 9777, "https://"]
               AUTO_LINK_REF:[9769, 9777, "https://"]
           TABLE_CELL_RODD_CODD:[9777, 9782, " and "]
           MdAutoLinkImpl:[9782, 9789, "file://"]
             MdAutoLinkRefImpl:[9782, 9789, "file://"]
               AUTO_LINK_REF:[9782, 9789, "file://"]
           TABLE_CELL_RODD_CODD:[9789, 9846, " abso … lute link addresses to project files                "]
         TABLE_ROW_ODD:[9846, 9847, "|"]
         MdTableCellImpl:[9847, 9854, "       "]
           TABLE_CELL_RODD_CEVEN:[9847, 9854, "       "]
         TABLE_ROW_ODD:[9854, 9855, "|"]
         MdTableCellImpl:[9855, 9865, "    X     "]
           TABLE_CELL_RODD_CODD:[9855, 9865, "    X     "]
         TABLE_ROW_ODD:[9865, 9866, "|"]
         EOL:[9866, 9867, "\n"]
       MdTableRowImpl:[9867, 10010, "| &nb … sp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    |       |    X     |\n"]
         TABLE_ROW_EVEN:[9867, 9868, "|"]
         MdTableCellImpl:[9868, 9989, " &nbs … p;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    "]
           TABLE_CELL_REVEN_CODD:[9868, 9869, " "]
           TABLE_CELL_REVEN_CODD_HTML_ENTITY:[9869, 9893, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_REVEN_CODD:[9893, 9989, "Refac … toring of header text with update to referencing anchor link references                    "]
         TABLE_ROW_EVEN:[9989, 9990, "|"]
         MdTableCellImpl:[9990, 9997, "       "]
           TABLE_CELL_REVEN_CEVEN:[9990, 9997, "       "]
         TABLE_ROW_EVEN:[9997, 9998, "|"]
         MdTableCellImpl:[9998, 10008, "    X     "]
           TABLE_CELL_REVEN_CODD:[9998, 10008, "    X     "]
         TABLE_ROW_EVEN:[10008, 10009, "|"]
         EOL:[10009, 10010, "\n"]
       MdTableRowImpl:[10010, 10152, "| &nb … sp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         |       |    X     |"]
         TABLE_ROW_ODD:[10010, 10011, "|"]
         MdTableCellImpl:[10011, 10132, " &nbs … p;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         "]
           TABLE_CELL_RODD_CODD:[10011, 10012, " "]
           TABLE_CELL_RODD_CODD_HTML_ENTITY:[10012, 10036, "&nbsp … ;&nbsp;&nbsp;&nbsp;"]
           TABLE_CELL_RODD_CODD:[10036, 10132, "Ancho … r link reference refactoring with update to referenced header text                         "]
         TABLE_ROW_ODD:[10132, 10133, "|"]
         MdTableCellImpl:[10133, 10140, "       "]
           TABLE_CELL_RODD_CEVEN:[10133, 10140, "       "]
         TABLE_ROW_ODD:[10140, 10141, "|"]
         MdTableCellImpl:[10141, 10151, "    X     "]
           TABLE_CELL_RODD_CODD:[10141, 10151, "    X     "]
         TABLE_ROW_ODD:[10151, 10152, "|"]
````````````````````````````````


### ThematicBreak

`ThematicBreak`

```````````````````````````````` example ThematicBreak: 1
.
.
 MdFile:[0, 0]
````````````````````````````````


### TypographicQuotes

`TypographicQuotes`

basic quotes

```````````````````````````````` example TypographicQuotes: 1
Sample "double" 'single' <<angle>> "l'ordre" 'l'ordre'
.
<p>Sample &ldquo;double&rdquo; &lsquo;single&rsquo; &laquo;angle&raquo; &ldquo;l&rsquo;ordre&rdquo; &lsquo;l&rsquo;ordre&rsquo;</p>
.
 MdFile:[0, 54, "Sampl … e \"double\" 'single' <<angle>> \"l'ordre\" 'l'ordre'"]
   MdParagraphImpl:[0, 54, "Sampl … e \"double\" 'single' <<angle>> \"l'ordre\" 'l'ordre'"]
     MdTextBlockImpl:[0, 54, "Sampl … e \"double\" 'single' <<angle>> \"l'ordre\" 'l'ordre'"]
       TEXT:[0, 7, "Sample "]
       MdInlineQuotedImpl:[7, 15, "\"double\""]
         QUOTE_MARKER:[7, 8, "\""]
         QUOTED_TEXT:[8, 14, "double"]
         QUOTE_MARKER:[14, 15, "\""]
       TEXT:[15, 16, " "]
       MdInlineQuotedImpl:[16, 24, "'single'"]
         QUOTE_MARKER:[16, 17, "'"]
         QUOTED_TEXT:[17, 23, "single"]
         QUOTE_MARKER:[23, 24, "'"]
       TEXT:[24, 25, " "]
       MdInlineQuotedImpl:[25, 34, "<<angle>>"]
         QUOTE_MARKER:[25, 27, "<<"]
         QUOTED_TEXT:[27, 32, "angle"]
         QUOTE_MARKER:[32, 34, ">>"]
       TEXT:[34, 35, " "]
       MdInlineQuotedImpl:[35, 44, "\"l'ordre\""]
         QUOTE_MARKER:[35, 36, "\""]
         QUOTED_TEXT:[36, 37, "l"]
         QUOTED_TEXT_SMARTS:[37, 38, "'"]
         QUOTED_TEXT:[38, 43, "ordre"]
         QUOTE_MARKER:[43, 44, "\""]
       TEXT:[44, 45, " "]
       MdInlineQuotedImpl:[45, 54, "'l'ordre'"]
         QUOTE_MARKER:[45, 46, "'"]
         QUOTED_TEXT:[46, 47, "l"]
         QUOTED_TEXT_SMARTS:[47, 48, "'"]
         QUOTED_TEXT:[48, 53, "ordre"]
         QUOTE_MARKER:[53, 54, "'"]
````````````````````````````````


escaped quotes

```````````````````````````````` example TypographicQuotes: 2
Sample \"double\" \'single\' \<<angle\>> \"l\'ordre\" \'l\'ordre\'
.
<p>Sample &quot;double&quot; 'single' &lt;&lt;angle&gt;&gt; &quot;l'ordre&quot; 'l'ordre'</p>
.
 MdFile:[0, 66, "Sampl … e \\"double\\" \'single\' \<<angle\>> \\"l\'ordre\\" \'l\'ordre\'"]
   MdParagraphImpl:[0, 66, "Sampl … e \\"double\\" \'single\' \<<angle\>> \\"l\'ordre\\" \'l\'ordre\'"]
     MdTextBlockImpl:[0, 66, "Sampl … e \\"double\\" \'single\' \<<angle\>> \\"l\'ordre\\" \'l\'ordre\'"]
       TEXT:[0, 7, "Sample "]
       MdInlineSpecialTextImpl:[7, 9, "\\""]
         SPECIAL_TEXT_MARKER:[7, 8, "\"]
         SPECIAL_TEXT:[8, 9, "\""]
       TEXT:[9, 15, "double"]
       MdInlineSpecialTextImpl:[15, 17, "\\""]
         SPECIAL_TEXT_MARKER:[15, 16, "\"]
         SPECIAL_TEXT:[16, 17, "\""]
       TEXT:[17, 18, " "]
       MdInlineSpecialTextImpl:[18, 20, "\'"]
         SPECIAL_TEXT_MARKER:[18, 19, "\"]
         SPECIAL_TEXT:[19, 20, "'"]
       TEXT:[20, 26, "single"]
       MdInlineSpecialTextImpl:[26, 28, "\'"]
         SPECIAL_TEXT_MARKER:[26, 27, "\"]
         SPECIAL_TEXT:[27, 28, "'"]
       TEXT:[28, 29, " "]
       MdInlineSpecialTextImpl:[29, 31, "\<"]
         SPECIAL_TEXT_MARKER:[29, 30, "\"]
         SPECIAL_TEXT:[30, 31, "<"]
       TEXT:[31, 37, "<angle"]
       MdInlineSpecialTextImpl:[37, 39, "\>"]
         SPECIAL_TEXT_MARKER:[37, 38, "\"]
         SPECIAL_TEXT:[38, 39, ">"]
       TEXT:[39, 41, "> "]
       MdInlineSpecialTextImpl:[41, 43, "\\""]
         SPECIAL_TEXT_MARKER:[41, 42, "\"]
         SPECIAL_TEXT:[42, 43, "\""]
       TEXT:[43, 44, "l"]
       MdInlineSpecialTextImpl:[44, 46, "\'"]
         SPECIAL_TEXT_MARKER:[44, 45, "\"]
         SPECIAL_TEXT:[45, 46, "'"]
       TEXT:[46, 51, "ordre"]
       MdInlineSpecialTextImpl:[51, 53, "\\""]
         SPECIAL_TEXT_MARKER:[51, 52, "\"]
         SPECIAL_TEXT:[52, 53, "\""]
       TEXT:[53, 54, " "]
       MdInlineSpecialTextImpl:[54, 56, "\'"]
         SPECIAL_TEXT_MARKER:[54, 55, "\"]
         SPECIAL_TEXT:[55, 56, "'"]
       TEXT:[56, 57, "l"]
       MdInlineSpecialTextImpl:[57, 59, "\'"]
         SPECIAL_TEXT_MARKER:[57, 58, "\"]
         SPECIAL_TEXT:[58, 59, "'"]
       TEXT:[59, 64, "ordre"]
       MdInlineSpecialTextImpl:[64, 66, "\'"]
         SPECIAL_TEXT_MARKER:[64, 65, "\"]
         SPECIAL_TEXT:[65, 66, "'"]
````````````````````````````````


### TypographicSmarts

`TypographicSmarts`

basic

```````````````````````````````` example TypographicSmarts: 1
Sample with l'existence, from 1...2 and so on. . . 

en--dash and em---dash
.
<p>Sample with l&rsquo;existence, from 1&hellip;2 and so on&hellip;</p>
<p>en&ndash;dash and em&mdash;dash</p>
.
 MdFile:[0, 75, "Sampl … e with l'existence, from 1...2 and so on. . . \n\nen--dash and em---dash"]
   MdParagraphImpl:[0, 52, "Sampl … e with l'existence, from 1...2 and so on. . . \n"]
     MdTextBlockImpl:[0, 52, "Sampl … e with l'existence, from 1...2 and so on. . . \n"]
       TEXT:[0, 13, "Sampl … e with l"]
       SMARTS:[13, 14, "'"]
       TEXT:[14, 31, "exist … ence, from 1"]
       SMARTS:[31, 34, "..."]
       TEXT:[34, 45, "2 and …  so on"]
       SMARTS:[45, 50, ". . ."]
       EOL:[50, 52, " \n"]
   MdBlankLineImpl:[52, 53, "\n"]
     BLANK_LINE:[52, 53, "\n"]
   MdParagraphImpl:[53, 75, "en--d … ash and em---dash"]
     MdTextBlockImpl:[53, 75, "en--d … ash and em---dash"]
       TEXT:[53, 55, "en"]
       SMARTS:[55, 57, "--"]
       TEXT:[57, 68, "dash  … and em"]
       SMARTS:[68, 71, "---"]
       TEXT:[71, 75, "dash"]
````````````````````````````````


escaped smarts

```````````````````````````````` example TypographicSmarts: 2
Sample with l\'existence, from 1\...2 and so on\. . . 

en\--dash and em\---dash
.
<p>Sample with l'existence, from 1...2 and so on. . .</p>
<p>en--dash and em-&ndash;dash</p>
.
 MdFile:[0, 80, "Sampl … e with l\'existence, from 1\...2 and so on\. . . \n\nen\--dash and em\---dash"]
   MdParagraphImpl:[0, 55, "Sampl … e with l\'existence, from 1\...2 and so on\. . . \n"]
     MdTextBlockImpl:[0, 55, "Sampl … e with l\'existence, from 1\...2 and so on\. . . \n"]
       TEXT:[0, 13, "Sampl … e with l"]
       MdInlineSpecialTextImpl:[13, 15, "\'"]
         SPECIAL_TEXT_MARKER:[13, 14, "\"]
         SPECIAL_TEXT:[14, 15, "'"]
       TEXT:[15, 32, "exist … ence, from 1"]
       MdInlineSpecialTextImpl:[32, 34, "\."]
         SPECIAL_TEXT_MARKER:[32, 33, "\"]
         SPECIAL_TEXT:[33, 34, "."]
       TEXT:[34, 47, "..2 a … nd so on"]
       MdInlineSpecialTextImpl:[47, 49, "\."]
         SPECIAL_TEXT_MARKER:[47, 48, "\"]
         SPECIAL_TEXT:[48, 49, "."]
       TEXT:[49, 53, " . ."]
       EOL:[53, 55, " \n"]
   MdBlankLineImpl:[55, 56, "\n"]
     BLANK_LINE:[55, 56, "\n"]
   MdParagraphImpl:[56, 80, "en\-- … dash and em\---dash"]
     MdTextBlockImpl:[56, 80, "en\-- … dash and em\---dash"]
       TEXT:[56, 58, "en"]
       MdInlineSpecialTextImpl:[58, 60, "\-"]
         SPECIAL_TEXT_MARKER:[58, 59, "\"]
         SPECIAL_TEXT:[59, 60, "-"]
       TEXT:[60, 72, "-dash …  and em"]
       MdInlineSpecialTextImpl:[72, 74, "\-"]
         SPECIAL_TEXT_MARKER:[72, 73, "\"]
         SPECIAL_TEXT:[73, 74, "-"]
       SMARTS:[74, 76, "--"]
       TEXT:[76, 80, "dash"]
````````````````````````````````


### WikiLink

`WikiLink`

no spaces between brackets

```````````````````````````````` example WikiLink: 1
[ [not wiki link]]
.
<p>[ [not wiki link]]</p>
.
 MdFile:[0, 18, "[ [no … t wiki link]]"]
   MdParagraphImpl:[0, 18, "[ [no … t wiki link]]"]
     MdTextBlockImpl:[0, 18, "[ [no … t wiki link]]"]
       TEXT:[0, 2, "[ "]
       MdReferenceLinkImpl:[2, 17, "[not  … wiki link]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[2, 3, "["]
         MdReferenceLinkReferenceImpl:[3, 16, "not w … iki link"]
           REFERENCE_LINK_REFERENCE_LEAF:[3, 16, "not w … iki link"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[16, 17, "]"]
       TEXT:[17, 18, "]"]
````````````````````````````````


no spaces between brackets

```````````````````````````````` example WikiLink: 2
[[not wiki link] ]
.
<p>[[not wiki link] ]</p>
.
 MdFile:[0, 18, "[[not …  wiki link] ]"]
   MdParagraphImpl:[0, 18, "[[not …  wiki link] ]"]
     MdTextBlockImpl:[0, 18, "[[not …  wiki link] ]"]
       TEXT:[0, 1, "["]
       MdReferenceLinkImpl:[1, 16, "[not  … wiki link]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[1, 2, "["]
         MdReferenceLinkReferenceImpl:[2, 15, "not w … iki link"]
           REFERENCE_LINK_REFERENCE_LEAF:[2, 15, "not w … iki link"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[15, 16, "]"]
       TEXT:[16, 18, " ]"]
````````````````````````````````


simple wiki link

```````````````````````````````` example WikiLink: 3
[[wiki link]]
.
<p><a href="wiki link">wiki link</a></p>
.
 MdFile:[0, 13, "[[wik … i link]]"]
   MdParagraphImpl:[0, 13, "[[wik … i link]]"]
     MdTextBlockImpl:[0, 13, "[[wik … i link]]"]
       MdWikiLinkImpl:[0, 13, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_CLOSE:[11, 13, "]]"]
````````````````````````````````


wiki link with text

```````````````````````````````` example WikiLink: 4
[[wiki text|wiki link]]
.
<p><a href="wiki link">wiki text</a></p>
.
 MdFile:[0, 23, "[[wik … i text|wiki link]]"]
   MdParagraphImpl:[0, 23, "[[wik … i text|wiki link]]"]
     MdTextBlockImpl:[0, 23, "[[wik … i text|wiki link]]"]
       MdWikiLinkImpl:[0, 23, "[[wik … i text|wiki link]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkTextImpl:[2, 11, "wiki text"]
           WIKI_LINK_TEXT:[2, 11, "wiki text"]
         WIKI_LINK_SEPARATOR:[11, 12, "|"]
         MdWikiLinkRefImpl:[12, 21, "wiki link"]
           WIKI_LINK_REF:[12, 21, "wiki link"]
         WIKI_LINK_CLOSE:[21, 23, "]]"]
````````````````````````````````


simple wiki link with ! before

```````````````````````````````` example WikiLink: 5
![[wiki link]]
.
<p>!<a href="wiki link">wiki link</a></p>
.
 MdFile:[0, 14, "![[wi … ki link]]"]
   MdParagraphImpl:[0, 14, "![[wi … ki link]]"]
     MdTextBlockImpl:[0, 14, "![[wi … ki link]]"]
       TEXT:[0, 1, "!"]
       MdWikiLinkImpl:[1, 14, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[1, 3, "[["]
         MdWikiLinkRefImpl:[3, 12, "wiki link"]
           WIKI_LINK_REF:[3, 12, "wiki link"]
         WIKI_LINK_CLOSE:[12, 14, "]]"]
````````````````````````````````


wiki link with text with ! before

```````````````````````````````` example WikiLink: 6
![[wiki text|wiki link]]
.
<p>!<a href="wiki link">wiki text</a></p>
.
 MdFile:[0, 24, "![[wi … ki text|wiki link]]"]
   MdParagraphImpl:[0, 24, "![[wi … ki text|wiki link]]"]
     MdTextBlockImpl:[0, 24, "![[wi … ki text|wiki link]]"]
       TEXT:[0, 1, "!"]
       MdWikiLinkImpl:[1, 24, "[[wik … i text|wiki link]]"]
         WIKI_LINK_OPEN:[1, 3, "[["]
         MdWikiLinkTextImpl:[3, 12, "wiki text"]
           WIKI_LINK_TEXT:[3, 12, "wiki text"]
         WIKI_LINK_SEPARATOR:[12, 13, "|"]
         MdWikiLinkRefImpl:[13, 22, "wiki link"]
           WIKI_LINK_REF:[13, 22, "wiki link"]
         WIKI_LINK_CLOSE:[22, 24, "]]"]
````````````````````````````````


reference following will be a reference, even if not defined

```````````````````````````````` example WikiLink: 7
[[wiki link]][ref]
.
<p><a href="wiki link">wiki link</a>[ref]</p>
.
 MdFile:[0, 18, "[[wik … i link]][ref]"]
   MdParagraphImpl:[0, 18, "[[wik … i link]][ref]"]
     MdTextBlockImpl:[0, 18, "[[wik … i link]][ref]"]
       MdWikiLinkImpl:[0, 13, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_CLOSE:[11, 13, "]]"]
       MdReferenceLinkImpl:[13, 18, "[ref]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[13, 14, "["]
         MdReferenceLinkReferenceImpl:[14, 17, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[14, 17, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[17, 18, "]"]
````````````````````````````````


reference following will be a reference

```````````````````````````````` example WikiLink: 8
[[wiki link]][ref]

[ref]: /url
.
<p><a href="wiki link">wiki link</a><a href="/url">ref</a></p>
.
 MdFile:[0, 31, "[[wik … i link]][ref]\n\n[ref]: /url"]
   MdParagraphImpl:[0, 19, "[[wik … i link]][ref]\n"]
     MdTextBlockImpl:[0, 19, "[[wik … i link]][ref]\n"]
       MdWikiLinkImpl:[0, 13, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_CLOSE:[11, 13, "]]"]
       MdReferenceLinkImpl:[13, 18, "[ref]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[13, 14, "["]
         MdReferenceLinkReferenceImpl:[14, 17, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[14, 17, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[17, 18, "]"]
       EOL:[18, 19, "\n"]
   MdBlankLineImpl:[19, 20, "\n"]
     BLANK_LINE:[19, 20, "\n"]
   MdReferenceImpl:[20, 31, "[ref] … : /url"]
     REFERENCE_TEXT_OPEN:[20, 21, "["]
     MdReferenceIdentifierImpl:[21, 24, "ref"]
       REFERENCE_TEXT_LEAF:[21, 24, "ref"]
     REFERENCE_TEXT_CLOSE:[24, 26, "]:"]
     WHITESPACE:[26, 27, " "]
     MdReferenceLinkRefImpl:[27, 31, "/url"]
       REFERENCE_LINK_REF:[27, 31, "/url"]
````````````````````````````````


dummy reference following will be an empty reference

```````````````````````````````` example WikiLink: 9
[[wiki link]][]
.
<p><a href="wiki link">wiki link</a>[]</p>
.
 MdFile:[0, 15, "[[wik … i link]][]"]
   MdParagraphImpl:[0, 15, "[[wik … i link]][]"]
     MdTextBlockImpl:[0, 15, "[[wik … i link]][]"]
       MdWikiLinkImpl:[0, 13, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_CLOSE:[11, 13, "]]"]
       MdReferenceLinkImpl:[13, 15, "[]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[13, 14, "["]
         MdReferenceLinkReferenceImpl:[14, 14]
         REFERENCE_LINK_REFERENCE_CLOSE2:[14, 15, "]"]
````````````````````````````````


reference inside is not a wiki link but a link ref with brackets around it

```````````````````````````````` example WikiLink: 10
[[not wiki link][ref]]
.
<p>[[not wiki link][ref]]</p>
.
 MdFile:[0, 22, "[[not …  wiki link][ref]]"]
   MdParagraphImpl:[0, 22, "[[not …  wiki link][ref]]"]
     MdTextBlockImpl:[0, 22, "[[not …  wiki link][ref]]"]
       TEXT:[0, 1, "["]
       MdReferenceLinkImpl:[1, 21, "[not  … wiki link][ref]"]
         REFERENCE_LINK_TEXT_OPEN:[1, 2, "["]
         MdReferenceLinkTextImpl:[2, 15, "not w … iki link"]
           TEXT:[2, 15, "not w … iki link"]
         REFERENCE_LINK_TEXT_CLOSE:[15, 16, "]"]
         REFERENCE_LINK_REFERENCE_OPEN:[16, 17, "["]
         MdReferenceLinkReferenceImpl:[17, 20, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[17, 20, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE:[20, 21, "]"]
       TEXT:[21, 22, "]"]
````````````````````````````````


dummy reference inside is not a wiki link but a link ref with brackets around it

```````````````````````````````` example WikiLink: 11
[[not wiki link][]]
.
<p>[[not wiki link][]]</p>
.
 MdFile:[0, 19, "[[not …  wiki link][]]"]
   MdParagraphImpl:[0, 19, "[[not …  wiki link][]]"]
     MdTextBlockImpl:[0, 19, "[[not …  wiki link][]]"]
       TEXT:[0, 1, "["]
       MdReferenceLinkImpl:[1, 18, "[not  … wiki link][]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[1, 2, "["]
         MdReferenceLinkReferenceImpl:[2, 15, "not w … iki link"]
           REFERENCE_LINK_REFERENCE_LEAF:[2, 15, "not w … iki link"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[15, 16, "]"]
         DUMMY_REFERENCE:[16, 18, "[]"]
       TEXT:[18, 19, "]"]
````````````````````````````````


```````````````````````````````` example WikiLink: 12
[[wiki link]] [^link][ref] [[^wiki link]]
.
<p><a href="wiki link">wiki link</a> [^link][ref] <a href="^wiki link">^wiki link</a></p>
.
 MdFile:[0, 41, "[[wik … i link]] [^link][ref] [[^wiki link]]"]
   MdParagraphImpl:[0, 41, "[[wik … i link]] [^link][ref] [[^wiki link]]"]
     MdTextBlockImpl:[0, 41, "[[wik … i link]] [^link][ref] [[^wiki link]]"]
       MdWikiLinkImpl:[0, 13, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_CLOSE:[11, 13, "]]"]
       TEXT:[13, 14, " "]
       MdReferenceLinkImpl:[14, 26, "[^lin … k][ref]"]
         REFERENCE_LINK_TEXT_OPEN:[14, 15, "["]
         MdReferenceLinkTextImpl:[15, 20, "^link"]
           TEXT:[15, 20, "^link"]
         REFERENCE_LINK_TEXT_CLOSE:[20, 21, "]"]
         REFERENCE_LINK_REFERENCE_OPEN:[21, 22, "["]
         MdReferenceLinkReferenceImpl:[22, 25, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[22, 25, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE:[25, 26, "]"]
       TEXT:[26, 27, " "]
       MdWikiLinkImpl:[27, 41, "[[^wi … ki link]]"]
         WIKI_LINK_OPEN:[27, 29, "[["]
         MdWikiLinkRefImpl:[29, 39, "^wiki link"]
           WIKI_LINK_REF:[29, 39, "^wiki link"]
         WIKI_LINK_CLOSE:[39, 41, "]]"]
````````````````````````````````


Exclamation before is just text

```````````````````````````````` example WikiLink: 13
![[wiki link]] [^link][ref] [[^wiki link]] [[wiki]][ref]
.
<p>!<a href="wiki link">wiki link</a> [^link][ref] <a href="^wiki link">^wiki link</a> <a href="wiki">wiki</a>[ref]</p>
.
 MdFile:[0, 56, "![[wi … ki link]] [^link][ref] [[^wiki link]] [[wiki]][ref]"]
   MdParagraphImpl:[0, 56, "![[wi … ki link]] [^link][ref] [[^wiki link]] [[wiki]][ref]"]
     MdTextBlockImpl:[0, 56, "![[wi … ki link]] [^link][ref] [[^wiki link]] [[wiki]][ref]"]
       TEXT:[0, 1, "!"]
       MdWikiLinkImpl:[1, 14, "[[wik … i link]]"]
         WIKI_LINK_OPEN:[1, 3, "[["]
         MdWikiLinkRefImpl:[3, 12, "wiki link"]
           WIKI_LINK_REF:[3, 12, "wiki link"]
         WIKI_LINK_CLOSE:[12, 14, "]]"]
       TEXT:[14, 15, " "]
       MdReferenceLinkImpl:[15, 27, "[^lin … k][ref]"]
         REFERENCE_LINK_TEXT_OPEN:[15, 16, "["]
         MdReferenceLinkTextImpl:[16, 21, "^link"]
           TEXT:[16, 21, "^link"]
         REFERENCE_LINK_TEXT_CLOSE:[21, 22, "]"]
         REFERENCE_LINK_REFERENCE_OPEN:[22, 23, "["]
         MdReferenceLinkReferenceImpl:[23, 26, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[23, 26, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE:[26, 27, "]"]
       TEXT:[27, 28, " "]
       MdWikiLinkImpl:[28, 42, "[[^wi … ki link]]"]
         WIKI_LINK_OPEN:[28, 30, "[["]
         MdWikiLinkRefImpl:[30, 40, "^wiki link"]
           WIKI_LINK_REF:[30, 40, "^wiki link"]
         WIKI_LINK_CLOSE:[40, 42, "]]"]
       TEXT:[42, 43, " "]
       MdWikiLinkImpl:[43, 51, "[[wiki]]"]
         WIKI_LINK_OPEN:[43, 45, "[["]
         MdWikiLinkRefImpl:[45, 49, "wiki"]
           WIKI_LINK_REF:[45, 49, "wiki"]
         WIKI_LINK_CLOSE:[49, 51, "]]"]
       MdReferenceLinkImpl:[51, 56, "[ref]"]
         REFERENCE_LINK_REFERENCE_OPEN2:[51, 52, "["]
         MdReferenceLinkReferenceImpl:[52, 55, "ref"]
           REFERENCE_LINK_REFERENCE_LEAF:[52, 55, "ref"]
         REFERENCE_LINK_REFERENCE_CLOSE2:[55, 56, "]"]
````````````````````````````````


With empty anchor ref

```````````````````````````````` example WikiLink: 14
[[wiki link#]] 
.
<p><a href="wiki link#">wiki link</a></p>
.
 MdFile:[0, 15, "[[wik … i link#]] "]
   MdParagraphImpl:[0, 15, "[[wik … i link#]] "]
     MdTextBlockImpl:[0, 15, "[[wik … i link#]] "]
       MdWikiLinkImpl:[0, 14, "[[wik … i link#]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_REF_ANCHOR_MARKER:[11, 12, "#"]
         MdWikiLinkAnchorImpl:[12, 12]
         WIKI_LINK_CLOSE:[12, 14, "]]"]
       WHITESPACE:[14, 15, " "]
````````````````````````````````


With Anchor ref

```````````````````````````````` example WikiLink: 15
[[wiki link#anchor-ref]] 
.
<p><a href="wiki link#anchor-ref">wiki link</a></p>
.
 MdFile:[0, 25, "[[wik … i link#anchor-ref]] "]
   MdParagraphImpl:[0, 25, "[[wik … i link#anchor-ref]] "]
     MdTextBlockImpl:[0, 25, "[[wik … i link#anchor-ref]] "]
       MdWikiLinkImpl:[0, 24, "[[wik … i link#anchor-ref]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkRefImpl:[2, 11, "wiki link"]
           WIKI_LINK_REF:[2, 11, "wiki link"]
         WIKI_LINK_REF_ANCHOR_MARKER:[11, 12, "#"]
         MdWikiLinkAnchorImpl:[12, 22, "anchor-ref"]
           WIKI_LINK_REF_ANCHOR:[12, 22, "anchor-ref"]
         WIKI_LINK_CLOSE:[22, 24, "]]"]
       WHITESPACE:[24, 25, " "]
````````````````````````````````


With text, empty anchor ref

```````````````````````````````` example WikiLink: 16
[[wiki text|wiki link#]] 
.
<p><a href="wiki link#">wiki text</a></p>
.
 MdFile:[0, 25, "[[wik … i text|wiki link#]] "]
   MdParagraphImpl:[0, 25, "[[wik … i text|wiki link#]] "]
     MdTextBlockImpl:[0, 25, "[[wik … i text|wiki link#]] "]
       MdWikiLinkImpl:[0, 24, "[[wik … i text|wiki link#]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkTextImpl:[2, 11, "wiki text"]
           WIKI_LINK_TEXT:[2, 11, "wiki text"]
         WIKI_LINK_SEPARATOR:[11, 12, "|"]
         MdWikiLinkRefImpl:[12, 21, "wiki link"]
           WIKI_LINK_REF:[12, 21, "wiki link"]
         WIKI_LINK_REF_ANCHOR_MARKER:[21, 22, "#"]
         MdWikiLinkAnchorImpl:[22, 22]
         WIKI_LINK_CLOSE:[22, 24, "]]"]
       WHITESPACE:[24, 25, " "]
````````````````````````````````


With text, anchor ref

```````````````````````````````` example WikiLink: 17
[[wiki text|wiki link#anchor-ref]] 
.
<p><a href="wiki link#anchor-ref">wiki text</a></p>
.
 MdFile:[0, 35, "[[wik … i text|wiki link#anchor-ref]] "]
   MdParagraphImpl:[0, 35, "[[wik … i text|wiki link#anchor-ref]] "]
     MdTextBlockImpl:[0, 35, "[[wik … i text|wiki link#anchor-ref]] "]
       MdWikiLinkImpl:[0, 34, "[[wik … i text|wiki link#anchor-ref]]"]
         WIKI_LINK_OPEN:[0, 2, "[["]
         MdWikiLinkTextImpl:[2, 11, "wiki text"]
           WIKI_LINK_TEXT:[2, 11, "wiki text"]
         WIKI_LINK_SEPARATOR:[11, 12, "|"]
         MdWikiLinkRefImpl:[12, 21, "wiki link"]
           WIKI_LINK_REF:[12, 21, "wiki link"]
         WIKI_LINK_REF_ANCHOR_MARKER:[21, 22, "#"]
         MdWikiLinkAnchorImpl:[22, 32, "anchor-ref"]
           WIKI_LINK_REF_ANCHOR:[22, 32, "anchor-ref"]
         WIKI_LINK_CLOSE:[32, 34, "]]"]
       WHITESPACE:[34, 35, " "]
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


