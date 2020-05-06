---
title: Lex Parser Spec no block quote whitespace
author:
version:
date: '2016-07-09'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Lex Parser Spec

Combined lexer/parser form markdown

empty text

```````````````````````````````` example Lex Parser Spec: 1
.

.
FILE[0, 0]
  Leaf:WHITESPACE[0, 0]
````````````````````````````````


## Markdown elements

### Markdown elements - Abbreviation

`Abbreviation` `AbbreviationBlock`

```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 1
text with abbr embedded

*[abbr]: abbreviation

.
<p>text with <abbr title="abbreviation">abbr</abbr> embedded</p>
.
FILE[0, 48] chars:[0, 48, "text  … ion\n\n"]
  PARAGRAPH_BLOCK[0, 24] chars:[0, 24, "text  … dded\n"]
    TEXT_BLOCK[0, 24] chars:[0, 24, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      ABBREVIATED[10, 14] chars:[10, 14, "abbr"]
        ABBREVIATED_TEXT[10, 14] chars:[10, 14, "abbr"]
          Leaf:ABBREVIATED_TEXT[10, 14] chars:[10, 14, "abbr"]
      Leaf:TEXT[14, 23] chars:[14, 23, " embedded"]
      Leaf:EOL[23, 24] chars:[23, 24, "\n"]
  BLANK_LINE[24, 25] chars:[24, 25, "\n"]
    Leaf:BLANK_LINE[24, 25] chars:[24, 25, "\n"]
  ABBREVIATION[25, 47] chars:[25, 47, "*[abb … tion\n"]
    Leaf:ABBREVIATION_OPEN[25, 27] chars:[25, 27, "*["]
    ABBREVIATION_SHORT_TEXT[27, 31] chars:[27, 31, "abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[27, 31] chars:[27, 31, "abbr"]
    Leaf:ABBREVIATION_CLOSE[31, 33] chars:[31, 33, "]:"]
    Leaf:WHITESPACE[33, 34] chars:[33, 34, " "]
    ABBREVIATION_EXPANDED_TEXT[34, 46] chars:[34, 46, "abbre … ation"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[34, 46] chars:[34, 46, "abbre … ation"]
    Leaf:EOL[46, 47] chars:[46, 47, "\n"]
  BLANK_LINE[47, 48] chars:[47, 48, "\n"]
    Leaf:BLANK_LINE[47, 48] chars:[47, 48, "\n"]
````````````````````````````````


```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 2
*[abbr]: abbreviation

text with abbr embedded
.
<p>text with <abbr title="abbreviation">abbr</abbr> embedded</p>
.
FILE[0, 47] chars:[0, 47, "*[abb … dded\n"]
  ABBREVIATION[0, 22] chars:[0, 22, "*[abb … tion\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    Leaf:WHITESPACE[8, 9] chars:[8, 9, " "]
    ABBREVIATION_EXPANDED_TEXT[9, 21] chars:[9, 21, "abbre … ation"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[9, 21] chars:[9, 21, "abbre … ation"]
    Leaf:EOL[21, 22] chars:[21, 22, "\n"]
  BLANK_LINE[22, 23] chars:[22, 23, "\n"]
    Leaf:BLANK_LINE[22, 23] chars:[22, 23, "\n"]
  PARAGRAPH_BLOCK[23, 47] chars:[23, 47, "text  … dded\n"]
    TEXT_BLOCK[23, 47] chars:[23, 47, "text  … dded\n"]
      Leaf:TEXT[23, 33] chars:[23, 33, "text with "]
      ABBREVIATED[33, 37] chars:[33, 37, "abbr"]
        ABBREVIATED_TEXT[33, 37] chars:[33, 37, "abbr"]
          Leaf:ABBREVIATED_TEXT[33, 37] chars:[33, 37, "abbr"]
      Leaf:TEXT[37, 46] chars:[37, 46, " embedded"]
      Leaf:EOL[46, 47] chars:[46, 47, "\n"]
````````````````````````````````


```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 3
*[Abbr]:Abbreviation
.

.
FILE[0, 21] chars:[0, 21, "*[Abb … tion\n"]
  ABBREVIATION[0, 21] chars:[0, 21, "*[Abb … tion\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    ABBREVIATION_EXPANDED_TEXT[8, 20] chars:[8, 20, "Abbre … ation"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[8, 20] chars:[8, 20, "Abbre … ation"]
    Leaf:EOL[20, 21] chars:[20, 21, "\n"]
````````````````````````````````


```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 4
*[Abbr]:Abbreviation

This has an Abbr embedded in it.
.
<p>This has an <abbr title="Abbreviation">Abbr</abbr> embedded in it.</p>
.
FILE[0, 55] chars:[0, 55, "*[Abb …  it.\n"]
  ABBREVIATION[0, 21] chars:[0, 21, "*[Abb … tion\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    ABBREVIATION_EXPANDED_TEXT[8, 20] chars:[8, 20, "Abbre … ation"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[8, 20] chars:[8, 20, "Abbre … ation"]
    Leaf:EOL[20, 21] chars:[20, 21, "\n"]
  BLANK_LINE[21, 22] chars:[21, 22, "\n"]
    Leaf:BLANK_LINE[21, 22] chars:[21, 22, "\n"]
  PARAGRAPH_BLOCK[22, 55] chars:[22, 55, "This  …  it.\n"]
    TEXT_BLOCK[22, 55] chars:[22, 55, "This  …  it.\n"]
      Leaf:TEXT[22, 34] chars:[22, 34, "This  … s an "]
      ABBREVIATED[34, 38] chars:[34, 38, "Abbr"]
        ABBREVIATED_TEXT[34, 38] chars:[34, 38, "Abbr"]
          Leaf:ABBREVIATED_TEXT[34, 38] chars:[34, 38, "Abbr"]
      Leaf:TEXT[38, 54] chars:[38, 54, " embe … n it."]
      Leaf:EOL[54, 55] chars:[54, 55, "\n"]
````````````````````````````````


No inline processing in expansion text.

```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 5
*[Abbr]: Abbreviation has *emphasis*, **bold** or `code`

This has an Abbr embedded in it.
.
<p>This has an <abbr title="Abbreviation has *emphasis*, **bold** or `code`">Abbr</abbr> embedded in it.</p>
.
FILE[0, 91] chars:[0, 91, "*[Abb …  it.\n"]
  ABBREVIATION[0, 57] chars:[0, 57, "*[Abb … ode`\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    Leaf:WHITESPACE[8, 9] chars:[8, 9, " "]
    ABBREVIATION_EXPANDED_TEXT[9, 56] chars:[9, 56, "Abbre … code`"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[9, 56] chars:[9, 56, "Abbre … code`"]
    Leaf:EOL[56, 57] chars:[56, 57, "\n"]
  BLANK_LINE[57, 58] chars:[57, 58, "\n"]
    Leaf:BLANK_LINE[57, 58] chars:[57, 58, "\n"]
  PARAGRAPH_BLOCK[58, 91] chars:[58, 91, "This  …  it.\n"]
    TEXT_BLOCK[58, 91] chars:[58, 91, "This  …  it.\n"]
      Leaf:TEXT[58, 70] chars:[58, 70, "This  … s an "]
      ABBREVIATED[70, 74] chars:[70, 74, "Abbr"]
        ABBREVIATED_TEXT[70, 74] chars:[70, 74, "Abbr"]
          Leaf:ABBREVIATED_TEXT[70, 74] chars:[70, 74, "Abbr"]
      Leaf:TEXT[74, 90] chars:[74, 90, " embe … n it."]
      Leaf:EOL[90, 91] chars:[90, 91, "\n"]
````````````````````````````````


```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 6
*[Abbr]: Abbreviation 1
*[Abbre]: Abbreviation 2
.

.
FILE[0, 49] chars:[0, 49, "*[Abb … on 2\n"]
  ABBREVIATION[0, 24] chars:[0, 24, "*[Abb … on 1\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    Leaf:WHITESPACE[8, 9] chars:[8, 9, " "]
    ABBREVIATION_EXPANDED_TEXT[9, 23] chars:[9, 23, "Abbre … ion 1"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[9, 23] chars:[9, 23, "Abbre … ion 1"]
    Leaf:EOL[23, 24] chars:[23, 24, "\n"]
  ABBREVIATION[24, 49] chars:[24, 49, "*[Abb … on 2\n"]
    Leaf:ABBREVIATION_OPEN[24, 26] chars:[24, 26, "*["]
    ABBREVIATION_SHORT_TEXT[26, 31] chars:[26, 31, "Abbre"]
      Leaf:ABBREVIATION_SHORT_TEXT[26, 31] chars:[26, 31, "Abbre"]
    Leaf:ABBREVIATION_CLOSE[31, 33] chars:[31, 33, "]:"]
    Leaf:WHITESPACE[33, 34] chars:[33, 34, " "]
    ABBREVIATION_EXPANDED_TEXT[34, 48] chars:[34, 48, "Abbre … ion 2"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[34, 48] chars:[34, 48, "Abbre … ion 2"]
    Leaf:EOL[48, 49] chars:[48, 49, "\n"]
````````````````````````````````


```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 7
*[Abbr]: Abbreviation 1
*[Abbre]: Abbreviation 2

This has an Abbre embedded in it.
And this has another Abbr embedded in it.
.
<p>This has an <abbr title="Abbreviation 2">Abbre</abbr> embedded in it. And this has another <abbr title="Abbreviation 1">Abbr</abbr> embedded in it.</p>
.
FILE[0, 126] chars:[0, 126, "*[Abb …  it.\n"]
  ABBREVIATION[0, 24] chars:[0, 24, "*[Abb … on 1\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    Leaf:WHITESPACE[8, 9] chars:[8, 9, " "]
    ABBREVIATION_EXPANDED_TEXT[9, 23] chars:[9, 23, "Abbre … ion 1"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[9, 23] chars:[9, 23, "Abbre … ion 1"]
    Leaf:EOL[23, 24] chars:[23, 24, "\n"]
  ABBREVIATION[24, 49] chars:[24, 49, "*[Abb … on 2\n"]
    Leaf:ABBREVIATION_OPEN[24, 26] chars:[24, 26, "*["]
    ABBREVIATION_SHORT_TEXT[26, 31] chars:[26, 31, "Abbre"]
      Leaf:ABBREVIATION_SHORT_TEXT[26, 31] chars:[26, 31, "Abbre"]
    Leaf:ABBREVIATION_CLOSE[31, 33] chars:[31, 33, "]:"]
    Leaf:WHITESPACE[33, 34] chars:[33, 34, " "]
    ABBREVIATION_EXPANDED_TEXT[34, 48] chars:[34, 48, "Abbre … ion 2"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[34, 48] chars:[34, 48, "Abbre … ion 2"]
    Leaf:EOL[48, 49] chars:[48, 49, "\n"]
  BLANK_LINE[49, 50] chars:[49, 50, "\n"]
    Leaf:BLANK_LINE[49, 50] chars:[49, 50, "\n"]
  PARAGRAPH_BLOCK[50, 126] chars:[50, 126, "This  …  it.\n"]
    TEXT_BLOCK[50, 126] chars:[50, 126, "This  …  it.\n"]
      Leaf:TEXT[50, 62] chars:[50, 62, "This  … s an "]
      ABBREVIATED[62, 67] chars:[62, 67, "Abbre"]
        ABBREVIATED_TEXT[62, 67] chars:[62, 67, "Abbre"]
          Leaf:ABBREVIATED_TEXT[62, 67] chars:[62, 67, "Abbre"]
      Leaf:TEXT[67, 105] chars:[67, 105, " embe … ther "]
      ABBREVIATED[105, 109] chars:[105, 109, "Abbr"]
        ABBREVIATED_TEXT[105, 109] chars:[105, 109, "Abbr"]
          Leaf:ABBREVIATED_TEXT[105, 109] chars:[105, 109, "Abbr"]
      Leaf:TEXT[109, 125] chars:[109, 125, " embe … n it."]
      Leaf:EOL[125, 126] chars:[125, 126, "\n"]
````````````````````````````````


```````````````````````````````` example(Markdown elements - Markdown elements - Abbreviation: 8) options(pegdown-fail)
*[U.S.A.]: United States of America
*[US of A]: United States of America

U.S.A. is an abbreviation and so is US of A, an abbreviation.
.
<p><abbr title="United States of America">U.S.A.</abbr> is an abbreviation and so is <abbr title="United States of America">US of A</abbr>, an abbreviation.</p>
.
FILE[0, 135] chars:[0, 135, "*[U.S … tion."]
  ABBREVIATION[0, 36] chars:[0, 36, "*[U.S … rica\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 8] chars:[2, 8, "U.S.A."]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 8] chars:[2, 8, "U.S.A."]
    Leaf:ABBREVIATION_CLOSE[8, 10] chars:[8, 10, "]:"]
    Leaf:WHITESPACE[10, 11] chars:[10, 11, " "]
    ABBREVIATION_EXPANDED_TEXT[11, 35] chars:[11, 35, "Unite … erica"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[11, 35] chars:[11, 35, "Unite … erica"]
    Leaf:EOL[35, 36] chars:[35, 36, "\n"]
  ABBREVIATION[36, 73] chars:[36, 73, "*[US  … rica\n"]
    Leaf:ABBREVIATION_OPEN[36, 38] chars:[36, 38, "*["]
    ABBREVIATION_SHORT_TEXT[38, 45] chars:[38, 45, "US of A"]
      Leaf:ABBREVIATION_SHORT_TEXT[38, 45] chars:[38, 45, "US of A"]
    Leaf:ABBREVIATION_CLOSE[45, 47] chars:[45, 47, "]:"]
    Leaf:WHITESPACE[47, 48] chars:[47, 48, " "]
    ABBREVIATION_EXPANDED_TEXT[48, 72] chars:[48, 72, "Unite … erica"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[48, 72] chars:[48, 72, "Unite … erica"]
    Leaf:EOL[72, 73] chars:[72, 73, "\n"]
  BLANK_LINE[73, 74] chars:[73, 74, "\n"]
    Leaf:BLANK_LINE[73, 74] chars:[73, 74, "\n"]
  PARAGRAPH_BLOCK[74, 135] chars:[74, 135, "U.S.A … tion."]
    TEXT_BLOCK[74, 135] chars:[74, 135, "U.S.A … tion."]
      ABBREVIATED[74, 80] chars:[74, 80, "U.S.A."]
        ABBREVIATED_TEXT[74, 80] chars:[74, 80, "U.S.A."]
          Leaf:ABBREVIATED_TEXT[74, 80] chars:[74, 80, "U.S.A."]
      Leaf:TEXT[80, 110] chars:[80, 110, " is a … o is "]
      ABBREVIATED[110, 117] chars:[110, 117, "US of A"]
        ABBREVIATED_TEXT[110, 117] chars:[110, 117, "US of A"]
          Leaf:ABBREVIATED_TEXT[110, 117] chars:[110, 117, "US of A"]
      Leaf:TEXT[117, 135] chars:[117, 135, ", an  … tion."]
````````````````````````````````


```````````````````````````````` example(Markdown elements - Markdown elements - Abbreviation: 9) options(pegdown-fail)
*[US]: United States
*[U.S.A.]: United States of America
*[US of A]: United States of America

U.S.A., US of A, and US are all abbreviations.
.
<p><abbr title="United States of America">U.S.A.</abbr>, <abbr title="United States">US</abbr> of A, and <abbr title="United States">US</abbr> are all abbreviations.</p>
.
FILE[0, 141] chars:[0, 141, "*[US] … ions."]
  ABBREVIATION[0, 21] chars:[0, 21, "*[US] … ates\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 4] chars:[2, 4, "US"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 4] chars:[2, 4, "US"]
    Leaf:ABBREVIATION_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    ABBREVIATION_EXPANDED_TEXT[7, 20] chars:[7, 20, "Unite … tates"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[7, 20] chars:[7, 20, "Unite … tates"]
    Leaf:EOL[20, 21] chars:[20, 21, "\n"]
  ABBREVIATION[21, 57] chars:[21, 57, "*[U.S … rica\n"]
    Leaf:ABBREVIATION_OPEN[21, 23] chars:[21, 23, "*["]
    ABBREVIATION_SHORT_TEXT[23, 29] chars:[23, 29, "U.S.A."]
      Leaf:ABBREVIATION_SHORT_TEXT[23, 29] chars:[23, 29, "U.S.A."]
    Leaf:ABBREVIATION_CLOSE[29, 31] chars:[29, 31, "]:"]
    Leaf:WHITESPACE[31, 32] chars:[31, 32, " "]
    ABBREVIATION_EXPANDED_TEXT[32, 56] chars:[32, 56, "Unite … erica"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[32, 56] chars:[32, 56, "Unite … erica"]
    Leaf:EOL[56, 57] chars:[56, 57, "\n"]
  ABBREVIATION[57, 94] chars:[57, 94, "*[US  … rica\n"]
    Leaf:ABBREVIATION_OPEN[57, 59] chars:[57, 59, "*["]
    ABBREVIATION_SHORT_TEXT[59, 66] chars:[59, 66, "US of A"]
      Leaf:ABBREVIATION_SHORT_TEXT[59, 66] chars:[59, 66, "US of A"]
    Leaf:ABBREVIATION_CLOSE[66, 68] chars:[66, 68, "]:"]
    Leaf:WHITESPACE[68, 69] chars:[68, 69, " "]
    ABBREVIATION_EXPANDED_TEXT[69, 93] chars:[69, 93, "Unite … erica"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[69, 93] chars:[69, 93, "Unite … erica"]
    Leaf:EOL[93, 94] chars:[93, 94, "\n"]
  BLANK_LINE[94, 95] chars:[94, 95, "\n"]
    Leaf:BLANK_LINE[94, 95] chars:[94, 95, "\n"]
  PARAGRAPH_BLOCK[95, 141] chars:[95, 141, "U.S.A … ions."]
    TEXT_BLOCK[95, 141] chars:[95, 141, "U.S.A … ions."]
      ABBREVIATED[95, 101] chars:[95, 101, "U.S.A."]
        ABBREVIATED_TEXT[95, 101] chars:[95, 101, "U.S.A."]
          Leaf:ABBREVIATED_TEXT[95, 101] chars:[95, 101, "U.S.A."]
      Leaf:TEXT[101, 103] chars:[101, 103, ", "]
      ABBREVIATED[103, 110] chars:[103, 110, "US of A"]
        ABBREVIATED_TEXT[103, 110] chars:[103, 110, "US of A"]
          Leaf:ABBREVIATED_TEXT[103, 110] chars:[103, 110, "US of A"]
      Leaf:TEXT[110, 116] chars:[110, 116, ", and "]
      ABBREVIATED[116, 118] chars:[116, 118, "US"]
        ABBREVIATED_TEXT[116, 118] chars:[116, 118, "US"]
          Leaf:ABBREVIATED_TEXT[116, 118] chars:[116, 118, "US"]
      Leaf:TEXT[118, 141] chars:[118, 141, " are  … ions."]
````````````````````````````````


```````````````````````````````` example(Markdown elements - Markdown elements - Abbreviation: 10) options(pegdown-fail)
*[Abbr]: Abbreviation
[Abbr]: http://test.com

This is an Abbr and this is not [Abbr].

.
<p>This is an <abbr title="Abbreviation">Abbr</abbr> and this is not <a href="http://test.com">Abbr</a>.</p>
.
FILE[0, 88] chars:[0, 88, "*[Abb … r].\n\n"]
  ABBREVIATION[0, 22] chars:[0, 22, "*[Abb … tion\n"]
    Leaf:ABBREVIATION_OPEN[0, 2] chars:[0, 2, "*["]
    ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
      Leaf:ABBREVIATION_SHORT_TEXT[2, 6] chars:[2, 6, "Abbr"]
    Leaf:ABBREVIATION_CLOSE[6, 8] chars:[6, 8, "]:"]
    Leaf:WHITESPACE[8, 9] chars:[8, 9, " "]
    ABBREVIATION_EXPANDED_TEXT[9, 21] chars:[9, 21, "Abbre … ation"]
      Leaf:ABBREVIATION_EXPANDED_TEXT[9, 21] chars:[9, 21, "Abbre … ation"]
    Leaf:EOL[21, 22] chars:[21, 22, "\n"]
  REFERENCE[22, 46] chars:[22, 46, "[Abbr … .com\n"]
    Leaf:REFERENCE_TEXT_OPEN[22, 23] chars:[22, 23, "["]
    REFERENCE_TEXT[23, 27] chars:[23, 27, "Abbr"]
      Leaf:TEXT[23, 27] chars:[23, 27, "Abbr"]
    Leaf:REFERENCE_TEXT_CLOSE[27, 29] chars:[27, 29, "]:"]
    Leaf:WHITESPACE[29, 30] chars:[29, 30, " "]
    REFERENCE_LINK_REF[30, 45] chars:[30, 45, "http: … t.com"]
      Leaf:REFERENCE_LINK_REF[30, 45] chars:[30, 45, "http: … t.com"]
    Leaf:EOL[45, 46] chars:[45, 46, "\n"]
  BLANK_LINE[46, 47] chars:[46, 47, "\n"]
    Leaf:BLANK_LINE[46, 47] chars:[46, 47, "\n"]
  PARAGRAPH_BLOCK[47, 87] chars:[47, 87, "This  … br].\n"]
    TEXT_BLOCK[47, 87] chars:[47, 87, "This  … br].\n"]
      Leaf:TEXT[47, 58] chars:[47, 58, "This  … s an "]
      ABBREVIATED[58, 62] chars:[58, 62, "Abbr"]
        ABBREVIATED_TEXT[58, 62] chars:[58, 62, "Abbr"]
          Leaf:ABBREVIATED_TEXT[58, 62] chars:[58, 62, "Abbr"]
      Leaf:TEXT[62, 79] chars:[62, 79, " and  …  not "]
      REFERENCE_LINK[79, 85] chars:[79, 85, "[Abbr]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[79, 80] chars:[79, 80, "["]
        REFERENCE_LINK_REFERENCE[80, 84] chars:[80, 84, "Abbr"]
          Leaf:TEXT[80, 84] chars:[80, 84, "Abbr"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[84, 85] chars:[84, 85, "]"]
      Leaf:TEXT[85, 86] chars:[85, 86, "."]
      Leaf:EOL[86, 87] chars:[86, 87, "\n"]
  BLANK_LINE[87, 88] chars:[87, 88, "\n"]
    Leaf:BLANK_LINE[87, 88] chars:[87, 88, "\n"]
````````````````````````````````


An abbreviation that is not on the first line is just text.

```````````````````````````````` example Markdown elements - Markdown elements - Abbreviation: 11
Paragraph with second line having a reference
*[test]: test abbreviation

.
<p>Paragraph with second line having a reference *[test]: test abbreviation</p>
.
FILE[0, 74] chars:[0, 74, "Parag … ion\n\n"]
  PARAGRAPH_BLOCK[0, 73] chars:[0, 73, "Parag … tion\n"]
    TEXT_BLOCK[0, 73] chars:[0, 73, "Parag … tion\n"]
      Leaf:TEXT[0, 47] chars:[0, 47, "Parag … nce\n*"]
      REFERENCE_LINK[47, 53] chars:[47, 53, "[test]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[47, 48] chars:[47, 48, "["]
        REFERENCE_LINK_REFERENCE[48, 52] chars:[48, 52, "test"]
          Leaf:TEXT[48, 52] chars:[48, 52, "test"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[52, 53] chars:[52, 53, "]"]
      Leaf:TEXT[53, 72] chars:[53, 72, ": tes … ation"]
      Leaf:EOL[72, 73] chars:[72, 73, "\n"]
  BLANK_LINE[73, 74] chars:[73, 74, "\n"]
    Leaf:BLANK_LINE[73, 74] chars:[73, 74, "\n"]
````````````````````````````````


### Markdown elements - AnchorLink

`AnchorLink`

```````````````````````````````` example Markdown elements - Markdown elements - AnchorLink: 1
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
<h1 id="setext-heading-1"><a href="#setext-heading-1" name="setext-heading-1"></a>Setext Heading 1</h1>
<h2 id="setext-heading-2"><a href="#setext-heading-2" name="setext-heading-2"></a>Setext Heading 2</h2>
<h1 id="heading-1"><a href="#heading-1" name="heading-1"></a>Heading 1</h1>
<h2 id="heading-2"><a href="#heading-2" name="heading-2"></a>Heading 2</h2>
<h3 id="heading-3"><a href="#heading-3" name="heading-3"></a>Heading 3</h3>
<h4 id="heading-4"><a href="#heading-4" name="heading-4"></a>Heading 4</h4>
<h5 id="heading-5"><a href="#heading-5" name="heading-5"></a>Heading 5</h5>
<h6 id="heading-6"><a href="#heading-6" name="heading-6"></a>Heading 6</h6>
.
FILE[0, 129] chars:[0, 129, "Setex … ng 6\n"]
  SETEXT_HEADER[0, 21] chars:[0, 21, "Setex … \n===\n"]
    HEADER_TEXT[0, 16] chars:[0, 16, "Setex … ing 1"]
      Leaf:HEADER_TEXT[0, 16] chars:[0, 16, "Setex … ing 1"]
    Leaf:EOL[16, 17] chars:[16, 17, "\n"]
    Leaf:HEADER_SETEXT_MARKER[17, 20] chars:[17, 20, "==="]
    Leaf:EOL[20, 21] chars:[20, 21, "\n"]
  SETEXT_HEADER[21, 42] chars:[21, 42, "Setex … \n---\n"]
    HEADER_TEXT[21, 37] chars:[21, 37, "Setex … ing 2"]
      Leaf:HEADER_TEXT[21, 37] chars:[21, 37, "Setex … ing 2"]
    Leaf:EOL[37, 38] chars:[37, 38, "\n"]
    Leaf:HEADER_SETEXT_MARKER[38, 41] chars:[38, 41, "---"]
    Leaf:EOL[41, 42] chars:[41, 42, "\n"]
  ATX_HEADER[42, 54] chars:[42, 54, "# Hea … ng 1\n"]
    Leaf:HEADER_ATX_MARKER[42, 43] chars:[42, 43, "#"]
    Leaf:WHITESPACE[43, 44] chars:[43, 44, " "]
    HEADER_TEXT[44, 53] chars:[44, 53, "Heading 1"]
      Leaf:HEADER_TEXT[44, 53] chars:[44, 53, "Heading 1"]
    Leaf:EOL[53, 54] chars:[53, 54, "\n"]
  ATX_HEADER[54, 67] chars:[54, 67, "## He … ng 2\n"]
    Leaf:HEADER_ATX_MARKER[54, 56] chars:[54, 56, "##"]
    Leaf:WHITESPACE[56, 57] chars:[56, 57, " "]
    HEADER_TEXT[57, 66] chars:[57, 66, "Heading 2"]
      Leaf:HEADER_TEXT[57, 66] chars:[57, 66, "Heading 2"]
    Leaf:EOL[66, 67] chars:[66, 67, "\n"]
  ATX_HEADER[67, 81] chars:[67, 81, "### H … ng 3\n"]
    Leaf:HEADER_ATX_MARKER[67, 70] chars:[67, 70, "###"]
    Leaf:WHITESPACE[70, 71] chars:[70, 71, " "]
    HEADER_TEXT[71, 80] chars:[71, 80, "Heading 3"]
      Leaf:HEADER_TEXT[71, 80] chars:[71, 80, "Heading 3"]
    Leaf:EOL[80, 81] chars:[80, 81, "\n"]
  ATX_HEADER[81, 96] chars:[81, 96, "####  … ng 4\n"]
    Leaf:HEADER_ATX_MARKER[81, 85] chars:[81, 85, "####"]
    Leaf:WHITESPACE[85, 86] chars:[85, 86, " "]
    HEADER_TEXT[86, 95] chars:[86, 95, "Heading 4"]
      Leaf:HEADER_TEXT[86, 95] chars:[86, 95, "Heading 4"]
    Leaf:EOL[95, 96] chars:[95, 96, "\n"]
  ATX_HEADER[96, 112] chars:[96, 112, "##### … ng 5\n"]
    Leaf:HEADER_ATX_MARKER[96, 101] chars:[96, 101, "#####"]
    Leaf:WHITESPACE[101, 102] chars:[101, 102, " "]
    HEADER_TEXT[102, 111] chars:[102, 111, "Heading 5"]
      Leaf:HEADER_TEXT[102, 111] chars:[102, 111, "Heading 5"]
    Leaf:EOL[111, 112] chars:[111, 112, "\n"]
  ATX_HEADER[112, 129] chars:[112, 129, "##### … ng 6\n"]
    Leaf:HEADER_ATX_MARKER[112, 118] chars:[112, 118, "######"]
    Leaf:WHITESPACE[118, 119] chars:[118, 119, " "]
    HEADER_TEXT[119, 128] chars:[119, 128, "Heading 6"]
      Leaf:HEADER_TEXT[119, 128] chars:[119, 128, "Heading 6"]
    Leaf:EOL[128, 129] chars:[128, 129, "\n"]
````````````````````````````````


### Markdown elements - AutoLink

`AutoLink`

Wraped autolink

```````````````````````````````` example Markdown elements - Markdown elements - AutoLink: 1
text <http://autolink.com> embedded

.
<p>text <a href="http://autolink.com">http://autolink.com</a> embedded</p>
.
FILE[0, 37] chars:[0, 37, "text  … ded\n\n"]
  PARAGRAPH_BLOCK[0, 36] chars:[0, 36, "text  … dded\n"]
    TEXT_BLOCK[0, 36] chars:[0, 36, "text  … dded\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      AUTO_LINK[5, 26] chars:[5, 26, "<http … .com>"]
        Leaf:AUTO_LINK_OPEN[5, 6] chars:[5, 6, "<"]
        AUTO_LINK_REF[6, 25] chars:[6, 25, "http: … k.com"]
          Leaf:AUTO_LINK_REF[6, 25] chars:[6, 25, "http: … k.com"]
        Leaf:AUTO_LINK_CLOSE[25, 26] chars:[25, 26, ">"]
      Leaf:TEXT[26, 35] chars:[26, 35, " embedded"]
      Leaf:EOL[35, 36] chars:[35, 36, "\n"]
  BLANK_LINE[36, 37] chars:[36, 37, "\n"]
    Leaf:BLANK_LINE[36, 37] chars:[36, 37, "\n"]
````````````````````````````````


Plain autolink

```````````````````````````````` example Markdown elements - Markdown elements - AutoLink: 2
text http://autolink.com embedded

.
<p>text <a href="http://autolink.com">http://autolink.com</a> embedded</p>
.
FILE[0, 35] chars:[0, 35, "text  … ded\n\n"]
  PARAGRAPH_BLOCK[0, 34] chars:[0, 34, "text  … dded\n"]
    TEXT_BLOCK[0, 34] chars:[0, 34, "text  … dded\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      AUTO_LINK[5, 24] chars:[5, 24, "http: … k.com"]
        AUTO_LINK_REF[5, 24] chars:[5, 24, "http: … k.com"]
          Leaf:AUTO_LINK_REF[5, 24] chars:[5, 24, "http: … k.com"]
      Leaf:TEXT[24, 33] chars:[24, 33, " embedded"]
      Leaf:EOL[33, 34] chars:[33, 34, "\n"]
  BLANK_LINE[34, 35] chars:[34, 35, "\n"]
    Leaf:BLANK_LINE[34, 35] chars:[34, 35, "\n"]
````````````````````````````````


### Markdown elements - BlockQuote

`BlockQuote`

Lazy continuation, no prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 1
> block quote
with lazy continuation
>
.
<blockquote>
  <p>block quote with lazy continuation</p>
</blockquote>
.
FILE[0, 39] chars:[0, 39, "> blo … on\n>\n"]
  BLOCK_QUOTE[0, 39] chars:[0, 39, "> blo … on\n>\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 2] chars:[0, 2, "> "]
    PARAGRAPH_BLOCK[2, 37] chars:[2, 37, "block … tion\n"]
      TEXT_BLOCK[2, 37] chars:[2, 37, "block … tion\n"]
        Leaf:TEXT[2, 36] chars:[2, 36, "block … ation"]
        Leaf:EOL[36, 37] chars:[36, 37, "\n"]
    BLANK_LINE[37, 39] chars:[37, 39, ">\n"]
      Leaf:BLANK_LINE[37, 39] chars:[37, 39, ">\n"]
````````````````````````````````


Lazy continuation, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 2
> block quote
> with lazy continuation
>
.
<blockquote>
  <p>block quote with lazy continuation</p>
</blockquote>
.
FILE[0, 41] chars:[0, 41, "> blo … on\n>\n"]
  BLOCK_QUOTE[0, 41] chars:[0, 41, "> blo … on\n>\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 2] chars:[0, 2, "> "]
    PARAGRAPH_BLOCK[2, 39] chars:[2, 39, "block … tion\n"]
      TEXT_BLOCK[2, 39] chars:[2, 39, "block … tion\n"]
        Leaf:TEXT[2, 38] chars:[2, 38, "block … ation"]
        Leaf:EOL[38, 39] chars:[38, 39, "\n"]
    BLANK_LINE[39, 41] chars:[39, 41, ">\n"]
      Leaf:BLANK_LINE[39, 41] chars:[39, 41, ">\n"]
````````````````````````````````


Nested, Lazy continuation, no prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 3
>> block quote
with lazy continuation
>>
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
  </blockquote>
</blockquote>
.
FILE[0, 41] chars:[0, 41, ">> bl … n\n>>\n"]
  BLOCK_QUOTE[0, 41] chars:[0, 41, ">> bl … n\n>>\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 41] chars:[1, 41, "> blo … n\n>>\n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 38] chars:[3, 38, "block … tion\n"]
        TEXT_BLOCK[3, 38] chars:[3, 38, "block … tion\n"]
          Leaf:TEXT[3, 37] chars:[3, 37, "block … ation"]
          Leaf:EOL[37, 38] chars:[37, 38, "\n"]
      BLANK_LINE[38, 41] chars:[38, 41, ">>\n"]
        Leaf:BLANK_LINE[38, 41] chars:[38, 41, ">>\n"]
````````````````````````````````


Nested, Lazy continuation, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 4
>> block quote
>> with lazy continuation
>>
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
  </blockquote>
</blockquote>
.
FILE[0, 44] chars:[0, 44, ">> bl … n\n>>\n"]
  BLOCK_QUOTE[0, 44] chars:[0, 44, ">> bl … n\n>>\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 44] chars:[1, 44, "> blo … n\n>>\n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 41] chars:[3, 41, "block … tion\n"]
        TEXT_BLOCK[3, 41] chars:[3, 41, "block … tion\n"]
          Leaf:TEXT[3, 40] chars:[3, 40, "block … ation"]
          Leaf:EOL[40, 41] chars:[40, 41, "\n"]
      BLANK_LINE[41, 44] chars:[41, 44, ">>\n"]
        Leaf:BLANK_LINE[41, 44] chars:[41, 44, ">>\n"]
````````````````````````````````


Nested, Lazy continuation less, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 5
>> block quote
> with lazy continuation
>> 
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
  </blockquote>
</blockquote>
.
FILE[0, 44] chars:[0, 44, ">> bl … \n>> \n"]
  BLOCK_QUOTE[0, 44] chars:[0, 44, ">> bl … \n>> \n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 44] chars:[1, 44, "> blo … \n>> \n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 40] chars:[3, 40, "block … tion\n"]
        TEXT_BLOCK[3, 40] chars:[3, 40, "block … tion\n"]
          Leaf:TEXT[3, 39] chars:[3, 39, "block … ation"]
          Leaf:EOL[39, 40] chars:[39, 40, "\n"]
      BLANK_LINE[40, 44] chars:[40, 44, ">> \n"]
        Leaf:BLANK_LINE[40, 44] chars:[40, 44, ">> \n"]
````````````````````````````````


Nested, Lazy continuation more, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 6
>> block quote
>> with lazy continuation
>> 
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
  </blockquote>
</blockquote>
.
FILE[0, 45] chars:[0, 45, ">> bl … \n>> \n"]
  BLOCK_QUOTE[0, 45] chars:[0, 45, ">> bl … \n>> \n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 45] chars:[1, 45, "> blo … \n>> \n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 41] chars:[3, 41, "block … tion\n"]
        TEXT_BLOCK[3, 41] chars:[3, 41, "block … tion\n"]
          Leaf:TEXT[3, 40] chars:[3, 40, "block … ation"]
          Leaf:EOL[40, 41] chars:[40, 41, "\n"]
      BLANK_LINE[41, 45] chars:[41, 45, ">> \n"]
        Leaf:BLANK_LINE[41, 45] chars:[41, 45, ">> \n"]
````````````````````````````````


Nested, Lazy continuation less, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 7
>> block quote
with lazy continuation
>>
>> more text
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
    <p>more text</p>
  </blockquote>
</blockquote>
.
FILE[0, 54] chars:[0, 54, ">> bl … text\n"]
  BLOCK_QUOTE[0, 54] chars:[0, 54, ">> bl … text\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 54] chars:[1, 54, "> blo … text\n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 38] chars:[3, 38, "block … tion\n"]
        TEXT_BLOCK[3, 38] chars:[3, 38, "block … tion\n"]
          Leaf:TEXT[3, 37] chars:[3, 37, "block … ation"]
          Leaf:EOL[37, 38] chars:[37, 38, "\n"]
      BLANK_LINE[38, 41] chars:[38, 41, ">>\n"]
        Leaf:BLANK_LINE[38, 41] chars:[38, 41, ">>\n"]
      Leaf:WHITESPACE[41, 44] chars:[41, 44, ">> "]
      PARAGRAPH_BLOCK[44, 54] chars:[44, 54, "more text\n"]
        TEXT_BLOCK[44, 54] chars:[44, 54, "more text\n"]
          Leaf:TEXT[44, 53] chars:[44, 53, "more text"]
          Leaf:EOL[53, 54] chars:[53, 54, "\n"]
````````````````````````````````


Nested, Lazy continuation, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 8
>> block quote
>> with lazy continuation
>>
>> more text
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
    <p>more text</p>
  </blockquote>
</blockquote>
.
FILE[0, 57] chars:[0, 57, ">> bl … text\n"]
  BLOCK_QUOTE[0, 57] chars:[0, 57, ">> bl … text\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 57] chars:[1, 57, "> blo … text\n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 41] chars:[3, 41, "block … tion\n"]
        TEXT_BLOCK[3, 41] chars:[3, 41, "block … tion\n"]
          Leaf:TEXT[3, 40] chars:[3, 40, "block … ation"]
          Leaf:EOL[40, 41] chars:[40, 41, "\n"]
      BLANK_LINE[41, 44] chars:[41, 44, ">>\n"]
        Leaf:BLANK_LINE[41, 44] chars:[41, 44, ">>\n"]
      Leaf:WHITESPACE[44, 47] chars:[44, 47, ">> "]
      PARAGRAPH_BLOCK[47, 57] chars:[47, 57, "more text\n"]
        TEXT_BLOCK[47, 57] chars:[47, 57, "more text\n"]
          Leaf:TEXT[47, 56] chars:[47, 56, "more text"]
          Leaf:EOL[56, 57] chars:[56, 57, "\n"]
````````````````````````````````


Nested, Lazy continuation less, with prefix

```````````````````````````````` example Markdown elements - Markdown elements - BlockQuote: 9
>> block quote
> with lazy continuation
>>
>> more text
.
<blockquote>
  <blockquote>
    <p>block quote with lazy continuation</p>
    <p>more text</p>
  </blockquote>
</blockquote>
.
FILE[0, 56] chars:[0, 56, ">> bl … text\n"]
  BLOCK_QUOTE[0, 56] chars:[0, 56, ">> bl … text\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 1] chars:[0, 1, ">"]
    BLOCK_QUOTE[1, 56] chars:[1, 56, "> blo … text\n"]
      Leaf:BLOCK_QUOTE_MARKER[1, 3] chars:[1, 3, "> "]
      PARAGRAPH_BLOCK[3, 40] chars:[3, 40, "block … tion\n"]
        TEXT_BLOCK[3, 40] chars:[3, 40, "block … tion\n"]
          Leaf:TEXT[3, 39] chars:[3, 39, "block … ation"]
          Leaf:EOL[39, 40] chars:[39, 40, "\n"]
      BLANK_LINE[40, 43] chars:[40, 43, ">>\n"]
        Leaf:BLANK_LINE[40, 43] chars:[40, 43, ">>\n"]
      Leaf:WHITESPACE[43, 46] chars:[43, 46, ">> "]
      PARAGRAPH_BLOCK[46, 56] chars:[46, 56, "more text\n"]
        TEXT_BLOCK[46, 56] chars:[46, 56, "more text\n"]
          Leaf:TEXT[46, 55] chars:[46, 55, "more text"]
          Leaf:EOL[55, 56] chars:[55, 56, "\n"]
````````````````````````````````


### Markdown elements - BulletList

`BulletList` `BulletListItem` `TaskListItem` `TaskListItemMarker`

empty

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 1
+ 

.
<ul>
  <li></li>
</ul>
.
FILE[0, 4] chars:[0, 4, "+ \n\n"]
  BULLET_LIST[0, 3] chars:[0, 3, "+ \n"]
    BULLET_LIST_ITEM[0, 3] chars:[0, 3, "+ \n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "+ "]
      Leaf:EOL[2, 3] chars:[2, 3, "\n"]
  BLANK_LINE[3, 4] chars:[3, 4, "\n"]
    Leaf:BLANK_LINE[3, 4] chars:[3, 4, "\n"]
````````````````````````````````


empty

```````````````````````````````` example(Markdown elements - Markdown elements - BulletList: 2) options(pegdown-fail)
- [ ] 

.
<ul>
  <li></li>
</ul>
.
FILE[0, 8] chars:[0, 8, "- [ ] \n\n"]
  BULLET_LIST[0, 5] chars:[0, 5, "- [ ]"]
    BULLET_LIST_ITEM[0, 5] chars:[0, 5, "- [ ]"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      Leaf:TASK_ITEM_MARKER[2, 5] chars:[2, 5, "[ ]"]
  Leaf:EOL[5, 7] chars:[5, 7, " \n"]
  BLANK_LINE[7, 8] chars:[7, 8, "\n"]
    Leaf:BLANK_LINE[7, 8] chars:[7, 8, "\n"]
````````````````````````````````


nested

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 3
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
FILE[0, 42] chars:[0, 42, "- ite … em 3\n"]
  BULLET_LIST[0, 42] chars:[0, 42, "- ite … em 3\n"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BULLET_LIST_ITEM[9, 33] chars:[9, 33, "* ite …  2.1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[9, 11] chars:[9, 11, "* "]
      TEXT_BLOCK[11, 18] chars:[11, 18, "item 2\n"]
        Leaf:TEXT[11, 17] chars:[11, 17, "item 2"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
      Leaf:WHITESPACE[18, 22] chars:[18, 22, "    "]
      BULLET_LIST[22, 33] chars:[22, 33, "- ite …  2.1\n"]
        BULLET_LIST_ITEM[22, 33] chars:[22, 33, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[22, 24] chars:[22, 24, "- "]
          TEXT_BLOCK[24, 33] chars:[24, 33, "item 2.1\n"]
            Leaf:TEXT[24, 32] chars:[24, 32, "item 2.1"]
            Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    BULLET_LIST_ITEM[33, 42] chars:[33, 42, "+ item 3\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[33, 35] chars:[33, 35, "+ "]
      TEXT_BLOCK[35, 42] chars:[35, 42, "item 3\n"]
        Leaf:TEXT[35, 41] chars:[35, 41, "item 3"]
        Leaf:EOL[41, 42] chars:[41, 42, "\n"]
````````````````````````````````


nested some loose

```````````````````````````````` example(Markdown elements - Markdown elements - BulletList: 4) options(pegdown-fail)
- item 1

* item 2
    - item 2.1
+ item 3
.
<ul>
  <li>
  <p>item 1</p></li>
  <li>
    <p>item 2</p>
    <ul>
      <li>item 2.1</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
FILE[0, 42] chars:[0, 42, "- ite … tem 3"]
  BULLET_LIST[0, 42] chars:[0, 42, "- ite … tem 3"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      PARAGRAPH_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
          Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
          Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BLANK_LINE[9, 10] chars:[9, 10, "\n"]
      Leaf:BLANK_LINE[9, 10] chars:[9, 10, "\n"]
    BULLET_LIST_ITEM[10, 34] chars:[10, 34, "* ite …  2.1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[10, 12] chars:[10, 12, "* "]
      TEXT_BLOCK[12, 19] chars:[12, 19, "item 2\n"]
        Leaf:TEXT[12, 18] chars:[12, 18, "item 2"]
        Leaf:EOL[18, 19] chars:[18, 19, "\n"]
      Leaf:WHITESPACE[19, 23] chars:[19, 23, "    "]
      BULLET_LIST[23, 34] chars:[23, 34, "- ite …  2.1\n"]
        BULLET_LIST_ITEM[23, 34] chars:[23, 34, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[23, 25] chars:[23, 25, "- "]
          TEXT_BLOCK[25, 34] chars:[25, 34, "item 2.1\n"]
            Leaf:TEXT[25, 33] chars:[25, 33, "item 2.1"]
            Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    BULLET_LIST_ITEM[34, 42] chars:[34, 42, "+ item 3"]
      Leaf:BULLET_LIST_ITEM_MARKER[34, 36] chars:[34, 36, "+ "]
      TEXT_BLOCK[36, 42] chars:[36, 42, "item 3"]
        Leaf:TEXT[36, 42] chars:[36, 42, "item 3"]
````````````````````````````````


nested loose

```````````````````````````````` example(Markdown elements - Markdown elements - BulletList: 5) options(pegdown-fail)
- item 1

* item 2
    - item 2.1
    
+ item 3
.
<ul>
  <li>
  <p>item 1</p></li>
  <li>
    <p>item 2</p>
    <ul>
      <li>item 2.1</li>
    </ul>
  </li>
  <li>
  <p>item 3</p></li>
</ul>
.
FILE[0, 47] chars:[0, 47, "- ite … tem 3"]
  BULLET_LIST[0, 47] chars:[0, 47, "- ite … tem 3"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      PARAGRAPH_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
          Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
          Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BLANK_LINE[9, 10] chars:[9, 10, "\n"]
      Leaf:BLANK_LINE[9, 10] chars:[9, 10, "\n"]
    BULLET_LIST_ITEM[10, 34] chars:[10, 34, "* ite …  2.1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[10, 12] chars:[10, 12, "* "]
      PARAGRAPH_BLOCK[12, 19] chars:[12, 19, "item 2\n"]
        TEXT_BLOCK[12, 19] chars:[12, 19, "item 2\n"]
          Leaf:TEXT[12, 18] chars:[12, 18, "item 2"]
          Leaf:EOL[18, 19] chars:[18, 19, "\n"]
      Leaf:WHITESPACE[19, 23] chars:[19, 23, "    "]
      BULLET_LIST[23, 34] chars:[23, 34, "- ite …  2.1\n"]
        BULLET_LIST_ITEM[23, 34] chars:[23, 34, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[23, 25] chars:[23, 25, "- "]
          TEXT_BLOCK[25, 34] chars:[25, 34, "item 2.1\n"]
            Leaf:TEXT[25, 33] chars:[25, 33, "item 2.1"]
            Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    BLANK_LINE[34, 39] chars:[34, 39, "    \n"]
      Leaf:BLANK_LINE[34, 39] chars:[34, 39, "    \n"]
    BULLET_LIST_ITEM[39, 47] chars:[39, 47, "+ item 3"]
      Leaf:BULLET_LIST_ITEM_MARKER[39, 41] chars:[39, 41, "+ "]
      PARAGRAPH_BLOCK[41, 47] chars:[41, 47, "item 3"]
        TEXT_BLOCK[41, 47] chars:[41, 47, "item 3"]
          Leaf:TEXT[41, 47] chars:[41, 47, "item 3"]
````````````````````````````````


tight nested loose

```````````````````````````````` example(Markdown elements - Markdown elements - BulletList: 6) options(pegdown-fail)
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
      <p>item 2.1</p></li>
      <li>
      <p>item 2.1</p></li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
FILE[0, 62] chars:[0, 62, "- ite … tem 3"]
  BULLET_LIST[0, 62] chars:[0, 62, "- ite … tem 3"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BULLET_LIST_ITEM[9, 54] chars:[9, 54, "* ite …  2.1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[9, 11] chars:[9, 11, "* "]
      TEXT_BLOCK[11, 18] chars:[11, 18, "item 2\n"]
        Leaf:TEXT[11, 17] chars:[11, 17, "item 2"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
      BLANK_LINE[18, 19] chars:[18, 19, "\n"]
        Leaf:BLANK_LINE[18, 19] chars:[18, 19, "\n"]
      Leaf:WHITESPACE[19, 23] chars:[19, 23, "    "]
      BULLET_LIST[23, 54] chars:[23, 54, "- ite …  2.1\n"]
        BULLET_LIST_ITEM[23, 34] chars:[23, 34, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[23, 25] chars:[23, 25, "- "]
          PARAGRAPH_BLOCK[25, 34] chars:[25, 34, "item 2.1\n"]
            TEXT_BLOCK[25, 34] chars:[25, 34, "item 2.1\n"]
              Leaf:TEXT[25, 33] chars:[25, 33, "item 2.1"]
              Leaf:EOL[33, 34] chars:[33, 34, "\n"]
        BLANK_LINE[34, 39] chars:[34, 39, "    \n"]
          Leaf:BLANK_LINE[34, 39] chars:[34, 39, "    \n"]
        Leaf:WHITESPACE[39, 43] chars:[39, 43, "    "]
        BULLET_LIST_ITEM[43, 54] chars:[43, 54, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[43, 45] chars:[43, 45, "- "]
          PARAGRAPH_BLOCK[45, 54] chars:[45, 54, "item 2.1\n"]
            TEXT_BLOCK[45, 54] chars:[45, 54, "item 2.1\n"]
              Leaf:TEXT[45, 53] chars:[45, 53, "item 2.1"]
              Leaf:EOL[53, 54] chars:[53, 54, "\n"]
    BULLET_LIST_ITEM[54, 62] chars:[54, 62, "+ item 3"]
      Leaf:BULLET_LIST_ITEM_MARKER[54, 56] chars:[54, 56, "+ "]
      TEXT_BLOCK[56, 62] chars:[56, 62, "item 3"]
        Leaf:TEXT[56, 62] chars:[56, 62, "item 3"]
````````````````````````````````


nested task item

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 7
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
      <li class="task-list-item"><span class="taskitem">O</span>item 2.1</li>
      <li>item 2.2</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
FILE[0, 61] chars:[0, 61, "- ite … em 3\n"]
  BULLET_LIST[0, 61] chars:[0, 61, "- ite … em 3\n"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BULLET_LIST_ITEM[9, 52] chars:[9, 52, "* ite …  2.2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[9, 11] chars:[9, 11, "* "]
      TEXT_BLOCK[11, 18] chars:[11, 18, "item 2\n"]
        Leaf:TEXT[11, 17] chars:[11, 17, "item 2"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
      Leaf:WHITESPACE[18, 22] chars:[18, 22, "    "]
      BULLET_LIST[22, 52] chars:[22, 52, "- [ ] …  2.2\n"]
        BULLET_LIST_ITEM[22, 37] chars:[22, 37, "- [ ] …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[22, 24] chars:[22, 24, "- "]
          Leaf:TASK_ITEM_MARKER[24, 28] chars:[24, 28, "[ ] "]
          TEXT_BLOCK[28, 37] chars:[28, 37, "item 2.1\n"]
            Leaf:TEXT[28, 36] chars:[28, 36, "item 2.1"]
            Leaf:EOL[36, 37] chars:[36, 37, "\n"]
        Leaf:WHITESPACE[37, 41] chars:[37, 41, "    "]
        BULLET_LIST_ITEM[41, 52] chars:[41, 52, "- ite …  2.2\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[41, 43] chars:[41, 43, "- "]
          TEXT_BLOCK[43, 52] chars:[43, 52, "item 2.2\n"]
            Leaf:TEXT[43, 51] chars:[43, 51, "item 2.2"]
            Leaf:EOL[51, 52] chars:[51, 52, "\n"]
    BULLET_LIST_ITEM[52, 61] chars:[52, 61, "+ item 3\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[52, 54] chars:[52, 54, "+ "]
      TEXT_BLOCK[54, 61] chars:[54, 61, "item 3\n"]
        Leaf:TEXT[54, 60] chars:[54, 60, "item 3"]
        Leaf:EOL[60, 61] chars:[60, 61, "\n"]
````````````````````````````````


task item, nested

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 8
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
  <li class="task-list-item"><span class="taskitem">O</span>item 2
    <ul>
      <li>item 2.1</li>
      <li>item 2.2</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
FILE[0, 70] chars:[0, 70, "- ite … em 3\n"]
  BULLET_LIST[0, 70] chars:[0, 70, "- ite … em 3\n"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BULLET_LIST_ITEM[9, 18] chars:[9, 18, "* item 2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[9, 11] chars:[9, 11, "* "]
      TEXT_BLOCK[11, 18] chars:[11, 18, "item 2\n"]
        Leaf:TEXT[11, 17] chars:[11, 17, "item 2"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
    BULLET_LIST_ITEM[18, 61] chars:[18, 61, "* [ ] …  2.2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[18, 20] chars:[18, 20, "* "]
      Leaf:TASK_ITEM_MARKER[20, 24] chars:[20, 24, "[ ] "]
      TEXT_BLOCK[24, 31] chars:[24, 31, "item 2\n"]
        Leaf:TEXT[24, 30] chars:[24, 30, "item 2"]
        Leaf:EOL[30, 31] chars:[30, 31, "\n"]
      Leaf:WHITESPACE[31, 35] chars:[31, 35, "    "]
      BULLET_LIST[35, 61] chars:[35, 61, "- ite …  2.2\n"]
        BULLET_LIST_ITEM[35, 46] chars:[35, 46, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[35, 37] chars:[35, 37, "- "]
          TEXT_BLOCK[37, 46] chars:[37, 46, "item 2.1\n"]
            Leaf:TEXT[37, 45] chars:[37, 45, "item 2.1"]
            Leaf:EOL[45, 46] chars:[45, 46, "\n"]
        Leaf:WHITESPACE[46, 50] chars:[46, 50, "    "]
        BULLET_LIST_ITEM[50, 61] chars:[50, 61, "- ite …  2.2\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[50, 52] chars:[50, 52, "- "]
          TEXT_BLOCK[52, 61] chars:[52, 61, "item 2.2\n"]
            Leaf:TEXT[52, 60] chars:[52, 60, "item 2.2"]
            Leaf:EOL[60, 61] chars:[60, 61, "\n"]
    BULLET_LIST_ITEM[61, 70] chars:[61, 70, "+ item 3\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[61, 63] chars:[61, 63, "+ "]
      TEXT_BLOCK[63, 70] chars:[63, 70, "item 3\n"]
        Leaf:TEXT[63, 69] chars:[63, 69, "item 3"]
        Leaf:EOL[69, 70] chars:[69, 70, "\n"]
````````````````````````````````


task item, nested task item

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 9
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
  <li class="task-list-item"><span class="taskitem">O</span>item 2
    <ul>
      <li>item 2.1</li>
      <li class="task-list-item"><span class="taskitem">O</span>item 2.2</li>
    </ul>
  </li>
  <li>item 3</li>
</ul>
.
FILE[0, 74] chars:[0, 74, "- ite … em 3\n"]
  BULLET_LIST[0, 74] chars:[0, 74, "- ite … em 3\n"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BULLET_LIST_ITEM[9, 18] chars:[9, 18, "* item 2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[9, 11] chars:[9, 11, "* "]
      TEXT_BLOCK[11, 18] chars:[11, 18, "item 2\n"]
        Leaf:TEXT[11, 17] chars:[11, 17, "item 2"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
    BULLET_LIST_ITEM[18, 65] chars:[18, 65, "* [ ] …  2.2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[18, 20] chars:[18, 20, "* "]
      Leaf:TASK_ITEM_MARKER[20, 24] chars:[20, 24, "[ ] "]
      TEXT_BLOCK[24, 31] chars:[24, 31, "item 2\n"]
        Leaf:TEXT[24, 30] chars:[24, 30, "item 2"]
        Leaf:EOL[30, 31] chars:[30, 31, "\n"]
      Leaf:WHITESPACE[31, 35] chars:[31, 35, "    "]
      BULLET_LIST[35, 65] chars:[35, 65, "- ite …  2.2\n"]
        BULLET_LIST_ITEM[35, 46] chars:[35, 46, "- ite …  2.1\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[35, 37] chars:[35, 37, "- "]
          TEXT_BLOCK[37, 46] chars:[37, 46, "item 2.1\n"]
            Leaf:TEXT[37, 45] chars:[37, 45, "item 2.1"]
            Leaf:EOL[45, 46] chars:[45, 46, "\n"]
        Leaf:WHITESPACE[46, 50] chars:[46, 50, "    "]
        BULLET_LIST_ITEM[50, 65] chars:[50, 65, "- [ ] …  2.2\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[50, 52] chars:[50, 52, "- "]
          Leaf:TASK_ITEM_MARKER[52, 56] chars:[52, 56, "[ ] "]
          TEXT_BLOCK[56, 65] chars:[56, 65, "item 2.2\n"]
            Leaf:TEXT[56, 64] chars:[56, 64, "item 2.2"]
            Leaf:EOL[64, 65] chars:[64, 65, "\n"]
    BULLET_LIST_ITEM[65, 74] chars:[65, 74, "+ item 3\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[65, 67] chars:[65, 67, "+ "]
      TEXT_BLOCK[67, 74] chars:[67, 74, "item 3\n"]
        Leaf:TEXT[67, 73] chars:[67, 73, "item 3"]
        Leaf:EOL[73, 74] chars:[73, 74, "\n"]
````````````````````````````````


A bullet list after an ordered list

```````````````````````````````` example(Markdown elements - Markdown elements - BulletList: 10) options(pegdown-fail)
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
</ol>
<ul>
  <li>item 1</li>
  <li>item 2</li>
</ul>
.
FILE[0, 64] chars:[0, 64, "2. it … tem 3"]
  ORDERED_LIST[0, 64] chars:[0, 64, "2. it … tem 3"]
    ORDERED_LIST_ITEM[0, 10] chars:[0, 10, "2. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "2. "]
      TEXT_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
        Leaf:TEXT[3, 9] chars:[3, 9, "item 1"]
        Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    ORDERED_LIST_ITEM[10, 20] chars:[10, 20, "1. item 2\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[10, 13] chars:[10, 13, "1. "]
      TEXT_BLOCK[13, 20] chars:[13, 20, "item 2\n"]
        Leaf:TEXT[13, 19] chars:[13, 19, "item 2"]
        Leaf:EOL[19, 20] chars:[19, 20, "\n"]
    ORDERED_LIST_ITEM[20, 33] chars:[20, 33, "5. [  … em 3\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[20, 23] chars:[20, 23, "5. "]
      Leaf:TASK_ITEM_MARKER[23, 27] chars:[23, 27, "[ ] "]
      PARAGRAPH_BLOCK[27, 33] chars:[27, 33, "tem 3\n"]
        TEXT_BLOCK[27, 33] chars:[27, 33, "tem 3\n"]
          Leaf:TEXT[27, 32] chars:[27, 32, "tem 3"]
          Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    BLANK_LINE[33, 34] chars:[33, 34, "\n"]
      Leaf:BLANK_LINE[33, 34] chars:[33, 34, "\n"]
    BULLET_LIST_ITEM[34, 43] chars:[34, 43, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[34, 36] chars:[34, 36, "- "]
      TEXT_BLOCK[36, 43] chars:[36, 43, "item 1\n"]
        Leaf:TEXT[36, 42] chars:[36, 42, "item 1"]
        Leaf:EOL[42, 43] chars:[42, 43, "\n"]
    BULLET_LIST_ITEM[43, 52] chars:[43, 52, "- item 2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[43, 45] chars:[43, 45, "- "]
      TEXT_BLOCK[45, 52] chars:[45, 52, "item 2\n"]
        Leaf:TEXT[45, 51] chars:[45, 51, "item 2"]
        Leaf:EOL[51, 52] chars:[51, 52, "\n"]
    BULLET_LIST_ITEM[52, 64] chars:[52, 64, "- [ ] … tem 3"]
      Leaf:BULLET_LIST_ITEM_MARKER[52, 54] chars:[52, 54, "- "]
      Leaf:TASK_ITEM_MARKER[54, 58] chars:[54, 58, "[ ] "]
      TEXT_BLOCK[58, 64] chars:[58, 64, "item 3"]
        Leaf:TEXT[58, 64] chars:[58, 64, "item 3"]
````````````````````````````````


Bullet items must have a blank line before them when preceded by paragraph but should not append
following child paragraph

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 11
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
FILE[0, 67] chars:[0, 67, "- ite … raph\n"]
  BULLET_LIST[0, 67] chars:[0, 67, "- ite … raph\n"]
    BULLET_LIST_ITEM[0, 33] chars:[0, 33, "- ite … list\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 19] chars:[2, 19, "item  … raph\n"]
        Leaf:TEXT[2, 18] chars:[2, 18, "item  … graph"]
        Leaf:EOL[18, 19] chars:[18, 19, "\n"]
      Leaf:WHITESPACE[19, 23] chars:[19, 23, "    "]
      BULLET_LIST[23, 33] chars:[23, 33, "* sublist\n"]
        BULLET_LIST_ITEM[23, 33] chars:[23, 33, "* sublist\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[23, 25] chars:[23, 25, "* "]
          TEXT_BLOCK[25, 33] chars:[25, 33, "sublist\n"]
            Leaf:TEXT[25, 32] chars:[25, 32, "sublist"]
            Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    BULLET_LIST_ITEM[33, 67] chars:[33, 67, "- ite … raph\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[33, 35] chars:[33, 35, "- "]
      TEXT_BLOCK[35, 52] chars:[35, 52, "item  … raph\n"]
        Leaf:TEXT[35, 51] chars:[35, 51, "item  … graph"]
        Leaf:EOL[51, 52] chars:[51, 52, "\n"]
      BLANK_LINE[52, 53] chars:[52, 53, "\n"]
        Leaf:BLANK_LINE[52, 53] chars:[52, 53, "\n"]
      Leaf:WHITESPACE[53, 57] chars:[53, 57, "    "]
      PARAGRAPH_BLOCK[57, 67] chars:[57, 67, "paragraph\n"]
        TEXT_BLOCK[57, 67] chars:[57, 67, "paragraph\n"]
          Leaf:TEXT[57, 66] chars:[57, 66, "paragraph"]
          Leaf:EOL[66, 67] chars:[66, 67, "\n"]
````````````````````````````````


Bullet items can have headings as children

```````````````````````````````` example Markdown elements - Markdown elements - BulletList: 12
- Some Lists
    
    # Test
.
<ul>
  <li>Some Lists
    <h1 id="test"><a href="#test" name="test"></a>Test</h1>
  </li>
</ul>
.
FILE[0, 29] chars:[0, 29, "- Som … Test\n"]
  BULLET_LIST[0, 29] chars:[0, 29, "- Som … Test\n"]
    BULLET_LIST_ITEM[0, 29] chars:[0, 29, "- Som … Test\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 13] chars:[2, 13, "Some  … ists\n"]
        Leaf:TEXT[2, 12] chars:[2, 12, "Some Lists"]
        Leaf:EOL[12, 13] chars:[12, 13, "\n"]
      BLANK_LINE[13, 18] chars:[13, 18, "    \n"]
        Leaf:BLANK_LINE[13, 18] chars:[13, 18, "    \n"]
      Leaf:WHITESPACE[18, 22] chars:[18, 22, "    "]
      ATX_HEADER[22, 29] chars:[22, 29, "# Test\n"]
        Leaf:HEADER_ATX_MARKER[22, 23] chars:[22, 23, "#"]
        Leaf:WHITESPACE[23, 24] chars:[23, 24, " "]
        HEADER_TEXT[24, 28] chars:[24, 28, "Test"]
          Leaf:HEADER_TEXT[24, 28] chars:[24, 28, "Test"]
        Leaf:EOL[28, 29] chars:[28, 29, "\n"]
````````````````````````````````


### Markdown elements - Code

`Code`

Plain text with unterminated or empty code

```````````````````````````````` example Markdown elements - Markdown elements - Code: 1
First line
Second line ``
Last line
.
<p>First line Second line `` Last line</p>
.
FILE[0, 36] chars:[0, 36, "First … line\n"]
  PARAGRAPH_BLOCK[0, 36] chars:[0, 36, "First … line\n"]
    TEXT_BLOCK[0, 36] chars:[0, 36, "First … line\n"]
      Leaf:TEXT[0, 35] chars:[0, 35, "First …  line"]
      Leaf:EOL[35, 36] chars:[35, 36, "\n"]
````````````````````````````````


Plain text with simple code

```````````````````````````````` example Markdown elements - Markdown elements - Code: 2
First line
Second line `code`
Last line
.
<p>First line Second line <code>code</code> Last line</p>
.
FILE[0, 40] chars:[0, 40, "First … line\n"]
  PARAGRAPH_BLOCK[0, 40] chars:[0, 40, "First … line\n"]
    TEXT_BLOCK[0, 40] chars:[0, 40, "First … line\n"]
      Leaf:TEXT[0, 23] chars:[0, 23, "First … line "]
      CODE[23, 29] chars:[23, 29, "`code`"]
        Leaf:CODE_MARKER[23, 24] chars:[23, 24, "`"]
        Leaf:CODE_TEXT[24, 28] chars:[24, 28, "code"]
        Leaf:CODE_MARKER[28, 29] chars:[28, 29, "`"]
      Leaf:TEXT[29, 39] chars:[29, 39, "\nLast line"]
      Leaf:EOL[39, 40] chars:[39, 40, "\n"]
````````````````````````````````


Plain text with code with embedded looking HTML comment

```````````````````````````````` example Markdown elements - Markdown elements - Code: 3
First line
Second line `<!--code-->`
Last line
.
<p>First line Second line <code>&lt;!--code--&gt;</code> Last line</p>
.
FILE[0, 47] chars:[0, 47, "First … line\n"]
  PARAGRAPH_BLOCK[0, 47] chars:[0, 47, "First … line\n"]
    TEXT_BLOCK[0, 47] chars:[0, 47, "First … line\n"]
      Leaf:TEXT[0, 23] chars:[0, 23, "First … line "]
      CODE[23, 36] chars:[23, 36, "`<!-- … e-->`"]
        Leaf:CODE_MARKER[23, 24] chars:[23, 24, "`"]
        Leaf:CODE_TEXT[24, 35] chars:[24, 35, "<!--c … de-->"]
        Leaf:CODE_MARKER[35, 36] chars:[35, 36, "`"]
      Leaf:TEXT[36, 46] chars:[36, 46, "\nLast line"]
      Leaf:EOL[46, 47] chars:[46, 47, "\n"]
````````````````````````````````


### Markdown elements - DefinitionItem

`DefinitionList` `DefinitionTerm` `DefinitionItem`

No optional : at end

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 1
Definition Term
: definition item 
.
<dl>
  <dt>Definition Term</dt>
  <dd>definition item</dd>
</dl>
.
FILE[0, 35] chars:[0, 35, "Defin … tem \n"]
  DEFINITION_LIST[0, 35] chars:[0, 35, "Defin … tem \n"]
    DEFINITION_TERM_ELEMENT[0, 16] chars:[0, 16, "Defin … Term\n"]
      TEXT_BLOCK[0, 16] chars:[0, 16, "Defin … Term\n"]
        Leaf:DEFINITION_TERM[0, 15] chars:[0, 15, "Defin …  Term"]
        Leaf:DEFINITION_TERM[15, 16] chars:[15, 16, "\n"]
    DEFINITION[16, 35] chars:[16, 35, ": def … tem \n"]
      Leaf:DEFINITION_MARKER[16, 17] chars:[16, 17, ":"]
      Leaf:WHITESPACE[17, 18] chars:[17, 18, " "]
      TEXT_BLOCK[18, 35] chars:[18, 35, "defin … tem \n"]
        Leaf:TEXT[18, 34] chars:[18, 34, "defin … item "]
        Leaf:EOL[34, 35] chars:[34, 35, "\n"]
````````````````````````````````


```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 2
Definition Term
~ definition item 
.
<dl>
  <dt>Definition Term</dt>
  <dd>definition item</dd>
</dl>
.
FILE[0, 35] chars:[0, 35, "Defin … tem \n"]
  DEFINITION_LIST[0, 35] chars:[0, 35, "Defin … tem \n"]
    DEFINITION_TERM_ELEMENT[0, 16] chars:[0, 16, "Defin … Term\n"]
      TEXT_BLOCK[0, 16] chars:[0, 16, "Defin … Term\n"]
        Leaf:DEFINITION_TERM[0, 15] chars:[0, 15, "Defin …  Term"]
        Leaf:DEFINITION_TERM[15, 16] chars:[15, 16, "\n"]
    DEFINITION[16, 35] chars:[16, 35, "~ def … tem \n"]
      Leaf:DEFINITION_MARKER[16, 17] chars:[16, 17, "~"]
      Leaf:WHITESPACE[17, 18] chars:[17, 18, " "]
      TEXT_BLOCK[18, 35] chars:[18, 35, "defin … tem \n"]
        Leaf:TEXT[18, 34] chars:[18, 34, "defin … item "]
        Leaf:EOL[34, 35] chars:[34, 35, "\n"]
````````````````````````````````


A simple definition list:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 3
Term 1
:   Definition 1

Term 2
:   Definition 2

.
<dl>
  <dt>Term 1</dt>
  <dd>Definition 1</dd>
  <dt>Term 2</dt>
  <dd>Definition 2</dd>
</dl>
.
FILE[0, 50] chars:[0, 50, "Term  … n 2\n\n"]
  DEFINITION_LIST[0, 49] chars:[0, 49, "Term  … on 2\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    DEFINITION[7, 24] chars:[7, 24, ":   D … on 1\n"]
      Leaf:DEFINITION_MARKER[7, 8] chars:[7, 8, ":"]
      Leaf:WHITESPACE[8, 11] chars:[8, 11, "   "]
      TEXT_BLOCK[11, 24] chars:[11, 24, "Defin … on 1\n"]
        Leaf:TEXT[11, 23] chars:[11, 23, "Defin … ion 1"]
        Leaf:EOL[23, 24] chars:[23, 24, "\n"]
    BLANK_LINE[24, 25] chars:[24, 25, "\n"]
      Leaf:BLANK_LINE[24, 25] chars:[24, 25, "\n"]
    DEFINITION_TERM_ELEMENT[25, 32] chars:[25, 32, "Term 2\n"]
      TEXT_BLOCK[25, 32] chars:[25, 32, "Term 2\n"]
        Leaf:DEFINITION_TERM[25, 31] chars:[25, 31, "Term 2"]
        Leaf:DEFINITION_TERM[31, 32] chars:[31, 32, "\n"]
    DEFINITION[32, 49] chars:[32, 49, ":   D … on 2\n"]
      Leaf:DEFINITION_MARKER[32, 33] chars:[32, 33, ":"]
      Leaf:WHITESPACE[33, 36] chars:[33, 36, "   "]
      TEXT_BLOCK[36, 49] chars:[36, 49, "Defin … on 2\n"]
        Leaf:TEXT[36, 48] chars:[36, 48, "Defin … ion 2"]
        Leaf:EOL[48, 49] chars:[48, 49, "\n"]
  BLANK_LINE[49, 50] chars:[49, 50, "\n"]
    Leaf:BLANK_LINE[49, 50] chars:[49, 50, "\n"]
````````````````````````````````


With multiple terms:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 4
Term 1
Term 2
:   Definition 1

Term 3
Term 4
:   Definition 2

.
<dl>
  <dt>Term 1</dt>
  <dt>Term 2</dt>
  <dd>Definition 1</dd>
  <dt>Term 3</dt>
  <dt>Term 4</dt>
  <dd>Definition 2</dd>
</dl>
.
FILE[0, 64] chars:[0, 64, "Term  … n 2\n\n"]
  DEFINITION_LIST[0, 63] chars:[0, 63, "Term  … on 2\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    DEFINITION_TERM_ELEMENT[7, 14] chars:[7, 14, "Term 2\n"]
      TEXT_BLOCK[7, 14] chars:[7, 14, "Term 2\n"]
        Leaf:DEFINITION_TERM[7, 13] chars:[7, 13, "Term 2"]
        Leaf:DEFINITION_TERM[13, 14] chars:[13, 14, "\n"]
    DEFINITION[14, 31] chars:[14, 31, ":   D … on 1\n"]
      Leaf:DEFINITION_MARKER[14, 15] chars:[14, 15, ":"]
      Leaf:WHITESPACE[15, 18] chars:[15, 18, "   "]
      TEXT_BLOCK[18, 31] chars:[18, 31, "Defin … on 1\n"]
        Leaf:TEXT[18, 30] chars:[18, 30, "Defin … ion 1"]
        Leaf:EOL[30, 31] chars:[30, 31, "\n"]
    BLANK_LINE[31, 32] chars:[31, 32, "\n"]
      Leaf:BLANK_LINE[31, 32] chars:[31, 32, "\n"]
    DEFINITION_TERM_ELEMENT[32, 39] chars:[32, 39, "Term 3\n"]
      TEXT_BLOCK[32, 39] chars:[32, 39, "Term 3\n"]
        Leaf:DEFINITION_TERM[32, 38] chars:[32, 38, "Term 3"]
        Leaf:DEFINITION_TERM[38, 39] chars:[38, 39, "\n"]
    DEFINITION_TERM_ELEMENT[39, 46] chars:[39, 46, "Term 4\n"]
      TEXT_BLOCK[39, 46] chars:[39, 46, "Term 4\n"]
        Leaf:DEFINITION_TERM[39, 45] chars:[39, 45, "Term 4"]
        Leaf:DEFINITION_TERM[45, 46] chars:[45, 46, "\n"]
    DEFINITION[46, 63] chars:[46, 63, ":   D … on 2\n"]
      Leaf:DEFINITION_MARKER[46, 47] chars:[46, 47, ":"]
      Leaf:WHITESPACE[47, 50] chars:[47, 50, "   "]
      TEXT_BLOCK[50, 63] chars:[50, 63, "Defin … on 2\n"]
        Leaf:TEXT[50, 62] chars:[50, 62, "Defin … ion 2"]
        Leaf:EOL[62, 63] chars:[62, 63, "\n"]
  BLANK_LINE[63, 64] chars:[63, 64, "\n"]
    Leaf:BLANK_LINE[63, 64] chars:[63, 64, "\n"]
````````````````````````````````


With multiple definitions:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 5
Term 1
:   Definition 1
:   Definition 2

Term 2
:   Definition 3
:   Definition 4

.
<dl>
  <dt>Term 1</dt>
  <dd>Definition 1</dd>
  <dd>Definition 2</dd>
  <dt>Term 2</dt>
  <dd>Definition 3</dd>
  <dd>Definition 4</dd>
</dl>
.
FILE[0, 84] chars:[0, 84, "Term  … n 4\n\n"]
  DEFINITION_LIST[0, 83] chars:[0, 83, "Term  … on 4\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    DEFINITION[7, 24] chars:[7, 24, ":   D … on 1\n"]
      Leaf:DEFINITION_MARKER[7, 8] chars:[7, 8, ":"]
      Leaf:WHITESPACE[8, 11] chars:[8, 11, "   "]
      TEXT_BLOCK[11, 24] chars:[11, 24, "Defin … on 1\n"]
        Leaf:TEXT[11, 23] chars:[11, 23, "Defin … ion 1"]
        Leaf:EOL[23, 24] chars:[23, 24, "\n"]
    DEFINITION[24, 41] chars:[24, 41, ":   D … on 2\n"]
      Leaf:DEFINITION_MARKER[24, 25] chars:[24, 25, ":"]
      Leaf:WHITESPACE[25, 28] chars:[25, 28, "   "]
      TEXT_BLOCK[28, 41] chars:[28, 41, "Defin … on 2\n"]
        Leaf:TEXT[28, 40] chars:[28, 40, "Defin … ion 2"]
        Leaf:EOL[40, 41] chars:[40, 41, "\n"]
    BLANK_LINE[41, 42] chars:[41, 42, "\n"]
      Leaf:BLANK_LINE[41, 42] chars:[41, 42, "\n"]
    DEFINITION_TERM_ELEMENT[42, 49] chars:[42, 49, "Term 2\n"]
      TEXT_BLOCK[42, 49] chars:[42, 49, "Term 2\n"]
        Leaf:DEFINITION_TERM[42, 48] chars:[42, 48, "Term 2"]
        Leaf:DEFINITION_TERM[48, 49] chars:[48, 49, "\n"]
    DEFINITION[49, 66] chars:[49, 66, ":   D … on 3\n"]
      Leaf:DEFINITION_MARKER[49, 50] chars:[49, 50, ":"]
      Leaf:WHITESPACE[50, 53] chars:[50, 53, "   "]
      TEXT_BLOCK[53, 66] chars:[53, 66, "Defin … on 3\n"]
        Leaf:TEXT[53, 65] chars:[53, 65, "Defin … ion 3"]
        Leaf:EOL[65, 66] chars:[65, 66, "\n"]
    DEFINITION[66, 83] chars:[66, 83, ":   D … on 4\n"]
      Leaf:DEFINITION_MARKER[66, 67] chars:[66, 67, ":"]
      Leaf:WHITESPACE[67, 70] chars:[67, 70, "   "]
      TEXT_BLOCK[70, 83] chars:[70, 83, "Defin … on 4\n"]
        Leaf:TEXT[70, 82] chars:[70, 82, "Defin … ion 4"]
        Leaf:EOL[82, 83] chars:[82, 83, "\n"]
  BLANK_LINE[83, 84] chars:[83, 84, "\n"]
    Leaf:BLANK_LINE[83, 84] chars:[83, 84, "\n"]
````````````````````````````````


With multiple lines per definition:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 6
Term 1
:   Definition 1 line 1 ...
Definition 1 line 2
:   Definition 2 line 1 ...
Definition 2 line 2

Term 2
:   Definition 3 line 2 ...
    Definition 3 line 2
:   Definition 4 line 2 ...
    Definition 4 line 2

.
<dl>
  <dt>Term 1</dt>
  <dd>Definition 1 line 1 &hellip; Definition 1 line 2</dd>
  <dd>Definition 2 line 1 &hellip; Definition 2 line 2</dd>
  <dt>Term 2</dt>
  <dd>Definition 3 line 2 &hellip; Definition 3 line 2</dd>
  <dd>Definition 4 line 2 &hellip; Definition 4 line 2</dd>
</dl>
.
FILE[0, 216] chars:[0, 216, "Term  … e 2\n\n"]
  DEFINITION_LIST[0, 215] chars:[0, 215, "Term  … ne 2\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    DEFINITION[7, 55] chars:[7, 55, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[7, 8] chars:[7, 8, ":"]
      Leaf:WHITESPACE[8, 11] chars:[8, 11, "   "]
      TEXT_BLOCK[11, 55] chars:[11, 55, "Defin … ne 2\n"]
        Leaf:TEXT[11, 31] chars:[11, 31, "Defin … ne 1 "]
        Leaf:SMARTS[31, 34] chars:[31, 34, "..."]
        Leaf:TEXT[34, 54] chars:[34, 54, "\nDefi … ine 2"]
        Leaf:EOL[54, 55] chars:[54, 55, "\n"]
    DEFINITION[55, 103] chars:[55, 103, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[55, 56] chars:[55, 56, ":"]
      Leaf:WHITESPACE[56, 59] chars:[56, 59, "   "]
      TEXT_BLOCK[59, 103] chars:[59, 103, "Defin … ne 2\n"]
        Leaf:TEXT[59, 79] chars:[59, 79, "Defin … ne 1 "]
        Leaf:SMARTS[79, 82] chars:[79, 82, "..."]
        Leaf:TEXT[82, 102] chars:[82, 102, "\nDefi … ine 2"]
        Leaf:EOL[102, 103] chars:[102, 103, "\n"]
    BLANK_LINE[103, 104] chars:[103, 104, "\n"]
      Leaf:BLANK_LINE[103, 104] chars:[103, 104, "\n"]
    DEFINITION_TERM_ELEMENT[104, 111] chars:[104, 111, "Term 2\n"]
      TEXT_BLOCK[104, 111] chars:[104, 111, "Term 2\n"]
        Leaf:DEFINITION_TERM[104, 110] chars:[104, 110, "Term 2"]
        Leaf:DEFINITION_TERM[110, 111] chars:[110, 111, "\n"]
    DEFINITION[111, 163] chars:[111, 163, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[111, 112] chars:[111, 112, ":"]
      Leaf:WHITESPACE[112, 115] chars:[112, 115, "   "]
      TEXT_BLOCK[115, 163] chars:[115, 163, "Defin … ne 2\n"]
        Leaf:TEXT[115, 135] chars:[115, 135, "Defin … ne 2 "]
        Leaf:SMARTS[135, 138] chars:[135, 138, "..."]
        Leaf:TEXT[138, 162] chars:[138, 162, "\n     … ine 2"]
        Leaf:EOL[162, 163] chars:[162, 163, "\n"]
    DEFINITION[163, 215] chars:[163, 215, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[163, 164] chars:[163, 164, ":"]
      Leaf:WHITESPACE[164, 167] chars:[164, 167, "   "]
      TEXT_BLOCK[167, 215] chars:[167, 215, "Defin … ne 2\n"]
        Leaf:TEXT[167, 187] chars:[167, 187, "Defin … ne 2 "]
        Leaf:SMARTS[187, 190] chars:[187, 190, "..."]
        Leaf:TEXT[190, 214] chars:[190, 214, "\n     … ine 2"]
        Leaf:EOL[214, 215] chars:[214, 215, "\n"]
  BLANK_LINE[215, 216] chars:[215, 216, "\n"]
    Leaf:BLANK_LINE[215, 216] chars:[215, 216, "\n"]
````````````````````````````````


With paragraphs:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 7
Term 1

:   Definition 1 (paragraph)

Term 2

:   Definition 2 (paragraph)

.
<dl>
  <dt>Term 1</dt>
  <dd>
  <p>Definition 1 (paragraph)</p></dd>
  <dt>Term 2</dt>
  <dd>
  <p>Definition 2 (paragraph)</p></dd>
</dl>
.
FILE[0, 76] chars:[0, 76, "Term  … ph)\n\n"]
  DEFINITION_LIST[0, 75] chars:[0, 75, "Term  … aph)\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    DEFINITION[8, 37] chars:[8, 37, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[8, 9] chars:[8, 9, ":"]
      Leaf:WHITESPACE[9, 12] chars:[9, 12, "   "]
      PARAGRAPH_BLOCK[12, 37] chars:[12, 37, "Defin … aph)\n"]
        TEXT_BLOCK[12, 37] chars:[12, 37, "Defin … aph)\n"]
          Leaf:TEXT[12, 36] chars:[12, 36, "Defin … raph)"]
          Leaf:EOL[36, 37] chars:[36, 37, "\n"]
    BLANK_LINE[37, 38] chars:[37, 38, "\n"]
      Leaf:BLANK_LINE[37, 38] chars:[37, 38, "\n"]
    DEFINITION_TERM_ELEMENT[38, 45] chars:[38, 45, "Term 2\n"]
      TEXT_BLOCK[38, 45] chars:[38, 45, "Term 2\n"]
        Leaf:DEFINITION_TERM[38, 44] chars:[38, 44, "Term 2"]
        Leaf:DEFINITION_TERM[44, 45] chars:[44, 45, "\n"]
    Leaf:EOL[45, 46] chars:[45, 46, "\n"]
    DEFINITION[46, 75] chars:[46, 75, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[46, 47] chars:[46, 47, ":"]
      Leaf:WHITESPACE[47, 50] chars:[47, 50, "   "]
      PARAGRAPH_BLOCK[50, 75] chars:[50, 75, "Defin … aph)\n"]
        TEXT_BLOCK[50, 75] chars:[50, 75, "Defin … aph)\n"]
          Leaf:TEXT[50, 74] chars:[50, 74, "Defin … raph)"]
          Leaf:EOL[74, 75] chars:[74, 75, "\n"]
  BLANK_LINE[75, 76] chars:[75, 76, "\n"]
    Leaf:BLANK_LINE[75, 76] chars:[75, 76, "\n"]
````````````````````````````````


With multiple paragraphs:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 8
Term 1

:   Definition 1 paragraph 1 line 1 ...
&#2192;Definition 1 paragraph 1 line 2

    Definition 1 paragraph 2 line 1 ...
    Definition 1 paragraph 2 line 2

Term 2

:   Definition 1 paragraph 1 line 1 ...
Definition 1 paragraph 1 line 2 (lazy)

    Definition 1 paragraph 2 line 1 ...
Definition 1 paragraph 2 line 2 (lazy)

.
<dl>
  <dt>Term 1</dt>
  <dd>
    <p>Definition 1 paragraph 1 line 1 &hellip; Definition 1 paragraph 1 line 2</p>
    <p>Definition 1 paragraph 2 line 1 &hellip; Definition 1 paragraph 2 line 2</p>
  </dd>
  <dt>Term 2</dt>
  <dd>
    <p>Definition 1 paragraph 1 line 1 &hellip; Definition 1 paragraph 1 line 2 (lazy)</p>
    <p>Definition 1 paragraph 2 line 1 &hellip; Definition 1 paragraph 2 line 2 (lazy)</p>
  </dd>
</dl>
.
FILE[0, 327] chars:[0, 327, "Term  … zy)\n\n"]
  DEFINITION_LIST[0, 326] chars:[0, 326, "Term  … azy)\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    DEFINITION[8, 158] chars:[8, 158, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[8, 9] chars:[8, 9, ":"]
      Leaf:WHITESPACE[9, 12] chars:[9, 12, "   "]
      PARAGRAPH_BLOCK[12, 81] chars:[12, 81, "Defin … ne 2\n"]
        TEXT_BLOCK[12, 81] chars:[12, 81, "Defin … ne 2\n"]
          Leaf:TEXT[12, 44] chars:[12, 44, "Defin … ne 1 "]
          Leaf:SMARTS[44, 47] chars:[44, 47, "..."]
          Leaf:TEXT[47, 80] chars:[47, 80, "\n\u2192Def … ine 2"]
          Leaf:EOL[80, 81] chars:[80, 81, "\n"]
      BLANK_LINE[81, 82] chars:[81, 82, "\n"]
        Leaf:BLANK_LINE[81, 82] chars:[81, 82, "\n"]
      Leaf:WHITESPACE[82, 86] chars:[82, 86, "    "]
      PARAGRAPH_BLOCK[86, 158] chars:[86, 158, "Defin … ne 2\n"]
        TEXT_BLOCK[86, 158] chars:[86, 158, "Defin … ne 2\n"]
          Leaf:TEXT[86, 118] chars:[86, 118, "Defin … ne 1 "]
          Leaf:SMARTS[118, 121] chars:[118, 121, "..."]
          Leaf:TEXT[121, 157] chars:[121, 157, "\n     … ine 2"]
          Leaf:EOL[157, 158] chars:[157, 158, "\n"]
    BLANK_LINE[158, 159] chars:[158, 159, "\n"]
      Leaf:BLANK_LINE[158, 159] chars:[158, 159, "\n"]
    DEFINITION_TERM_ELEMENT[159, 166] chars:[159, 166, "Term 2\n"]
      TEXT_BLOCK[159, 166] chars:[159, 166, "Term 2\n"]
        Leaf:DEFINITION_TERM[159, 165] chars:[159, 165, "Term 2"]
        Leaf:DEFINITION_TERM[165, 166] chars:[165, 166, "\n"]
    Leaf:EOL[166, 167] chars:[166, 167, "\n"]
    DEFINITION[167, 326] chars:[167, 326, ":   D … azy)\n"]
      Leaf:DEFINITION_MARKER[167, 168] chars:[167, 168, ":"]
      Leaf:WHITESPACE[168, 171] chars:[168, 171, "   "]
      PARAGRAPH_BLOCK[171, 246] chars:[171, 246, "Defin … azy)\n"]
        TEXT_BLOCK[171, 246] chars:[171, 246, "Defin … azy)\n"]
          Leaf:TEXT[171, 203] chars:[171, 203, "Defin … ne 1 "]
          Leaf:SMARTS[203, 206] chars:[203, 206, "..."]
          Leaf:TEXT[206, 245] chars:[206, 245, "\nDefi … lazy)"]
          Leaf:EOL[245, 246] chars:[245, 246, "\n"]
      BLANK_LINE[246, 247] chars:[246, 247, "\n"]
        Leaf:BLANK_LINE[246, 247] chars:[246, 247, "\n"]
      Leaf:WHITESPACE[247, 251] chars:[247, 251, "    "]
      PARAGRAPH_BLOCK[251, 326] chars:[251, 326, "Defin … azy)\n"]
        TEXT_BLOCK[251, 326] chars:[251, 326, "Defin … azy)\n"]
          Leaf:TEXT[251, 283] chars:[251, 283, "Defin … ne 1 "]
          Leaf:SMARTS[283, 286] chars:[283, 286, "..."]
          Leaf:TEXT[286, 325] chars:[286, 325, "\nDefi … lazy)"]
          Leaf:EOL[325, 326] chars:[325, 326, "\n"]
  BLANK_LINE[326, 327] chars:[326, 327, "\n"]
    Leaf:BLANK_LINE[326, 327] chars:[326, 327, "\n"]
````````````````````````````````


A mix:

```````````````````````````````` example Markdown elements - Markdown elements - DefinitionItem: 9
Term 1
Term 2

:   Definition 1 paragraph 1 line 1 ...
Definition 1 paragraph 1 line 2 (lazy)
    
    Definition 1 paragraph 2 line 1 ...
    Definition 1 paragraph 2 line 2

:   Definition 2 paragraph 1 line 1 ...
Definition 2 paragraph 1 line 2 (lazy)

Term 3
:   Definition 3 (no paragraph)
:   Definition 4 (no paragraph)
:   Definition 5 line 1 ...
    Definition 5 line 2 (no paragraph)

:   Definition 6 paragraph 1 line 1 ...
Definition 6 paragraph 1 line 2
:   Definition 7 (no paragraph)
:   Definition 8 paragraph 1 line 1 (forced paragraph) ...
    Definition 8 paragraph 1 line 2
    
    Definition 8 paragraph 2 line 1
    
Term 4
:   Definition 9 paragraph 1 line 1 (forced paragraph) ...
    Definition 9 paragraph 1 line 2
    
    Definition 9 paragraph 2 line 1
:   Definition 10 (no paragraph)
.
<dl>
  <dt>Term 1</dt>
  <dt>Term 2</dt>
  <dd>
    <p>Definition 1 paragraph 1 line 1 &hellip; Definition 1 paragraph 1 line 2 (lazy)</p>
    <p>Definition 1 paragraph 2 line 1 &hellip; Definition 1 paragraph 2 line 2</p>
  </dd>
  <dd>
  <p>Definition 2 paragraph 1 line 1 &hellip; Definition 2 paragraph 1 line 2 (lazy)</p></dd>
  <dt>Term 3</dt>
  <dd>Definition 3 (no paragraph)</dd>
  <dd>Definition 4 (no paragraph)</dd>
  <dd>Definition 5 line 1 &hellip; Definition 5 line 2 (no paragraph)</dd>
  <dd>
  <p>Definition 6 paragraph 1 line 1 &hellip; Definition 6 paragraph 1 line 2</p></dd>
  <dd>Definition 7 (no paragraph)</dd>
  <dd>Definition 8 paragraph 1 line 1 (forced paragraph) &hellip; Definition 8 paragraph 1 line 2
    <p>Definition 8 paragraph 2 line 1</p>
  </dd>
  <dt>Term 4</dt>
  <dd>Definition 9 paragraph 1 line 1 (forced paragraph) &hellip; Definition 9 paragraph 1 line 2
    <p>Definition 9 paragraph 2 line 1</p>
  </dd>
  <dd>Definition 10 (no paragraph)</dd>
</dl>
.
FILE[0, 816] chars:[0, 816, "Term  … aph)\n"]
  DEFINITION_LIST[0, 816] chars:[0, 816, "Term  … aph)\n"]
    DEFINITION_TERM_ELEMENT[0, 7] chars:[0, 7, "Term 1\n"]
      TEXT_BLOCK[0, 7] chars:[0, 7, "Term 1\n"]
        Leaf:DEFINITION_TERM[0, 6] chars:[0, 6, "Term 1"]
        Leaf:DEFINITION_TERM[6, 7] chars:[6, 7, "\n"]
    DEFINITION_TERM_ELEMENT[7, 14] chars:[7, 14, "Term 2\n"]
      TEXT_BLOCK[7, 14] chars:[7, 14, "Term 2\n"]
        Leaf:DEFINITION_TERM[7, 13] chars:[7, 13, "Term 2"]
        Leaf:DEFINITION_TERM[13, 14] chars:[13, 14, "\n"]
    Leaf:EOL[14, 15] chars:[14, 15, "\n"]
    DEFINITION[15, 175] chars:[15, 175, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[15, 16] chars:[15, 16, ":"]
      Leaf:WHITESPACE[16, 19] chars:[16, 19, "   "]
      PARAGRAPH_BLOCK[19, 94] chars:[19, 94, "Defin … azy)\n"]
        TEXT_BLOCK[19, 94] chars:[19, 94, "Defin … azy)\n"]
          Leaf:TEXT[19, 51] chars:[19, 51, "Defin … ne 1 "]
          Leaf:SMARTS[51, 54] chars:[51, 54, "..."]
          Leaf:TEXT[54, 93] chars:[54, 93, "\nDefi … lazy)"]
          Leaf:EOL[93, 94] chars:[93, 94, "\n"]
      BLANK_LINE[94, 99] chars:[94, 99, "    \n"]
        Leaf:BLANK_LINE[94, 99] chars:[94, 99, "    \n"]
      Leaf:WHITESPACE[99, 103] chars:[99, 103, "    "]
      PARAGRAPH_BLOCK[103, 175] chars:[103, 175, "Defin … ne 2\n"]
        TEXT_BLOCK[103, 175] chars:[103, 175, "Defin … ne 2\n"]
          Leaf:TEXT[103, 135] chars:[103, 135, "Defin … ne 1 "]
          Leaf:SMARTS[135, 138] chars:[135, 138, "..."]
          Leaf:TEXT[138, 174] chars:[138, 174, "\n     … ine 2"]
          Leaf:EOL[174, 175] chars:[174, 175, "\n"]
    BLANK_LINE[175, 176] chars:[175, 176, "\n"]
      Leaf:BLANK_LINE[175, 176] chars:[175, 176, "\n"]
    DEFINITION[176, 255] chars:[176, 255, ":   D … azy)\n"]
      Leaf:DEFINITION_MARKER[176, 177] chars:[176, 177, ":"]
      Leaf:WHITESPACE[177, 180] chars:[177, 180, "   "]
      PARAGRAPH_BLOCK[180, 255] chars:[180, 255, "Defin … azy)\n"]
        TEXT_BLOCK[180, 255] chars:[180, 255, "Defin … azy)\n"]
          Leaf:TEXT[180, 212] chars:[180, 212, "Defin … ne 1 "]
          Leaf:SMARTS[212, 215] chars:[212, 215, "..."]
          Leaf:TEXT[215, 254] chars:[215, 254, "\nDefi … lazy)"]
          Leaf:EOL[254, 255] chars:[254, 255, "\n"]
    BLANK_LINE[255, 256] chars:[255, 256, "\n"]
      Leaf:BLANK_LINE[255, 256] chars:[255, 256, "\n"]
    DEFINITION_TERM_ELEMENT[256, 263] chars:[256, 263, "Term 3\n"]
      TEXT_BLOCK[256, 263] chars:[256, 263, "Term 3\n"]
        Leaf:DEFINITION_TERM[256, 262] chars:[256, 262, "Term 3"]
        Leaf:DEFINITION_TERM[262, 263] chars:[262, 263, "\n"]
    DEFINITION[263, 295] chars:[263, 295, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[263, 264] chars:[263, 264, ":"]
      Leaf:WHITESPACE[264, 267] chars:[264, 267, "   "]
      TEXT_BLOCK[267, 295] chars:[267, 295, "Defin … aph)\n"]
        Leaf:TEXT[267, 294] chars:[267, 294, "Defin … raph)"]
        Leaf:EOL[294, 295] chars:[294, 295, "\n"]
    DEFINITION[295, 327] chars:[295, 327, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[295, 296] chars:[295, 296, ":"]
      Leaf:WHITESPACE[296, 299] chars:[296, 299, "   "]
      TEXT_BLOCK[299, 327] chars:[299, 327, "Defin … aph)\n"]
        Leaf:TEXT[299, 326] chars:[299, 326, "Defin … raph)"]
        Leaf:EOL[326, 327] chars:[326, 327, "\n"]
    DEFINITION[327, 394] chars:[327, 394, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[327, 328] chars:[327, 328, ":"]
      Leaf:WHITESPACE[328, 331] chars:[328, 331, "   "]
      TEXT_BLOCK[331, 394] chars:[331, 394, "Defin … aph)\n"]
        Leaf:TEXT[331, 351] chars:[331, 351, "Defin … ne 1 "]
        Leaf:SMARTS[351, 354] chars:[351, 354, "..."]
        Leaf:TEXT[354, 393] chars:[354, 393, "\n     … raph)"]
        Leaf:EOL[393, 394] chars:[393, 394, "\n"]
    BLANK_LINE[394, 395] chars:[394, 395, "\n"]
      Leaf:BLANK_LINE[394, 395] chars:[394, 395, "\n"]
    DEFINITION[395, 467] chars:[395, 467, ":   D … ne 2\n"]
      Leaf:DEFINITION_MARKER[395, 396] chars:[395, 396, ":"]
      Leaf:WHITESPACE[396, 399] chars:[396, 399, "   "]
      PARAGRAPH_BLOCK[399, 467] chars:[399, 467, "Defin … ne 2\n"]
        TEXT_BLOCK[399, 467] chars:[399, 467, "Defin … ne 2\n"]
          Leaf:TEXT[399, 431] chars:[399, 431, "Defin … ne 1 "]
          Leaf:SMARTS[431, 434] chars:[431, 434, "..."]
          Leaf:TEXT[434, 466] chars:[434, 466, "\nDefi … ine 2"]
          Leaf:EOL[466, 467] chars:[466, 467, "\n"]
    DEFINITION[467, 499] chars:[467, 499, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[467, 468] chars:[467, 468, ":"]
      Leaf:WHITESPACE[468, 471] chars:[468, 471, "   "]
      TEXT_BLOCK[471, 499] chars:[471, 499, "Defin … aph)\n"]
        Leaf:TEXT[471, 498] chars:[471, 498, "Defin … raph)"]
        Leaf:EOL[498, 499] chars:[498, 499, "\n"]
    DEFINITION[499, 635] chars:[499, 635, ":   D … ne 1\n"]
      Leaf:DEFINITION_MARKER[499, 500] chars:[499, 500, ":"]
      Leaf:WHITESPACE[500, 503] chars:[500, 503, "   "]
      TEXT_BLOCK[503, 594] chars:[503, 594, "Defin … ne 2\n"]
        Leaf:TEXT[503, 554] chars:[503, 554, "Defin … aph) "]
        Leaf:SMARTS[554, 557] chars:[554, 557, "..."]
        Leaf:TEXT[557, 593] chars:[557, 593, "\n     … ine 2"]
        Leaf:EOL[593, 594] chars:[593, 594, "\n"]
      BLANK_LINE[594, 599] chars:[594, 599, "    \n"]
        Leaf:BLANK_LINE[594, 599] chars:[594, 599, "    \n"]
      Leaf:WHITESPACE[599, 603] chars:[599, 603, "    "]
      PARAGRAPH_BLOCK[603, 635] chars:[603, 635, "Defin … ne 1\n"]
        TEXT_BLOCK[603, 635] chars:[603, 635, "Defin … ne 1\n"]
          Leaf:TEXT[603, 634] chars:[603, 634, "Defin … ine 1"]
          Leaf:EOL[634, 635] chars:[634, 635, "\n"]
    BLANK_LINE[635, 640] chars:[635, 640, "    \n"]
      Leaf:BLANK_LINE[635, 640] chars:[635, 640, "    \n"]
    DEFINITION_TERM_ELEMENT[640, 647] chars:[640, 647, "Term 4\n"]
      TEXT_BLOCK[640, 647] chars:[640, 647, "Term 4\n"]
        Leaf:DEFINITION_TERM[640, 646] chars:[640, 646, "Term 4"]
        Leaf:DEFINITION_TERM[646, 647] chars:[646, 647, "\n"]
    DEFINITION[647, 783] chars:[647, 783, ":   D … ne 1\n"]
      Leaf:DEFINITION_MARKER[647, 648] chars:[647, 648, ":"]
      Leaf:WHITESPACE[648, 651] chars:[648, 651, "   "]
      TEXT_BLOCK[651, 742] chars:[651, 742, "Defin … ne 2\n"]
        Leaf:TEXT[651, 702] chars:[651, 702, "Defin … aph) "]
        Leaf:SMARTS[702, 705] chars:[702, 705, "..."]
        Leaf:TEXT[705, 741] chars:[705, 741, "\n     … ine 2"]
        Leaf:EOL[741, 742] chars:[741, 742, "\n"]
      BLANK_LINE[742, 747] chars:[742, 747, "    \n"]
        Leaf:BLANK_LINE[742, 747] chars:[742, 747, "    \n"]
      Leaf:WHITESPACE[747, 751] chars:[747, 751, "    "]
      PARAGRAPH_BLOCK[751, 783] chars:[751, 783, "Defin … ne 1\n"]
        TEXT_BLOCK[751, 783] chars:[751, 783, "Defin … ne 1\n"]
          Leaf:TEXT[751, 782] chars:[751, 782, "Defin … ine 1"]
          Leaf:EOL[782, 783] chars:[782, 783, "\n"]
    DEFINITION[783, 816] chars:[783, 816, ":   D … aph)\n"]
      Leaf:DEFINITION_MARKER[783, 784] chars:[783, 784, ":"]
      Leaf:WHITESPACE[784, 787] chars:[784, 787, "   "]
      TEXT_BLOCK[787, 816] chars:[787, 816, "Defin … aph)\n"]
        Leaf:TEXT[787, 815] chars:[787, 815, "Defin … raph)"]
        Leaf:EOL[815, 816] chars:[815, 816, "\n"]
````````````````````````````````


### Markdown elements - Emoji

`Emoji`

valid emoji

```````````````````````````````` example Markdown elements - Markdown elements - Emoji: 1
:warning:
text :warning: embedded 
:warning: embedded 
text :warning: 
.
<p><img title=":warning:" alt=":warning:" src="file:/emojis/warning.png" height="20" width="20" align="absmiddle"> text <img title=":warning:" alt=":warning:" src="file:/emojis/warning.png" height="20" width="20" align="absmiddle"> embedded <img title=":warning:" alt=":warning:" src="file:/emojis/warning.png" height="20" width="20" align="absmiddle"> embedded text <img title=":warning:" alt=":warning:" src="file:/emojis/warning.png" height="20" width="20" align="absmiddle"> </p>
.
FILE[0, 71] chars:[0, 71, ":warn … ng: \n"]
  PARAGRAPH_BLOCK[0, 71] chars:[0, 71, ":warn … ng: \n"]
    TEXT_BLOCK[0, 71] chars:[0, 71, ":warn … ng: \n"]
      EMOJI[0, 9] chars:[0, 9, ":warning:"]
        Leaf:EMOJI_MARKER[0, 1] chars:[0, 1, ":"]
        EMOJI_ID[1, 8] chars:[1, 8, "warning"]
          Leaf:EMOJI_ID[1, 8] chars:[1, 8, "warning"]
        Leaf:EMOJI_MARKER[8, 9] chars:[8, 9, ":"]
      Leaf:TEXT[9, 15] chars:[9, 15, "\ntext "]
      EMOJI[15, 24] chars:[15, 24, ":warning:"]
        Leaf:EMOJI_MARKER[15, 16] chars:[15, 16, ":"]
        EMOJI_ID[16, 23] chars:[16, 23, "warning"]
          Leaf:EMOJI_ID[16, 23] chars:[16, 23, "warning"]
        Leaf:EMOJI_MARKER[23, 24] chars:[23, 24, ":"]
      Leaf:TEXT[24, 35] chars:[24, 35, " embe … ded \n"]
      EMOJI[35, 44] chars:[35, 44, ":warning:"]
        Leaf:EMOJI_MARKER[35, 36] chars:[35, 36, ":"]
        EMOJI_ID[36, 43] chars:[36, 43, "warning"]
          Leaf:EMOJI_ID[36, 43] chars:[36, 43, "warning"]
        Leaf:EMOJI_MARKER[43, 44] chars:[43, 44, ":"]
      Leaf:TEXT[44, 60] chars:[44, 60, " embe … text "]
      EMOJI[60, 69] chars:[60, 69, ":warning:"]
        Leaf:EMOJI_MARKER[60, 61] chars:[60, 61, ":"]
        EMOJI_ID[61, 68] chars:[61, 68, "warning"]
          Leaf:EMOJI_ID[61, 68] chars:[61, 68, "warning"]
        Leaf:EMOJI_MARKER[68, 69] chars:[68, 69, ":"]
      Leaf:TEXT[69, 70] chars:[69, 70, " "]
      Leaf:EOL[70, 71] chars:[70, 71, "\n"]
````````````````````````````````


invalid emoji

```````````````````````````````` example Markdown elements - Markdown elements - Emoji: 2
:invalid:
text :invalid: embedded 
:invalid: embedded 
text :invalid: 
.
<p>:invalid: text :invalid: embedded :invalid: embedded text :invalid: </p>
.
FILE[0, 71] chars:[0, 71, ":inva … id: \n"]
  PARAGRAPH_BLOCK[0, 71] chars:[0, 71, ":inva … id: \n"]
    TEXT_BLOCK[0, 71] chars:[0, 71, ":inva … id: \n"]
      EMOJI[0, 9] chars:[0, 9, ":invalid:"]
        Leaf:EMOJI_MARKER[0, 1] chars:[0, 1, ":"]
        EMOJI_ID[1, 8] chars:[1, 8, "invalid"]
          Leaf:EMOJI_ID[1, 8] chars:[1, 8, "invalid"]
        Leaf:EMOJI_MARKER[8, 9] chars:[8, 9, ":"]
      Leaf:TEXT[9, 15] chars:[9, 15, "\ntext "]
      EMOJI[15, 24] chars:[15, 24, ":invalid:"]
        Leaf:EMOJI_MARKER[15, 16] chars:[15, 16, ":"]
        EMOJI_ID[16, 23] chars:[16, 23, "invalid"]
          Leaf:EMOJI_ID[16, 23] chars:[16, 23, "invalid"]
        Leaf:EMOJI_MARKER[23, 24] chars:[23, 24, ":"]
      Leaf:TEXT[24, 35] chars:[24, 35, " embe … ded \n"]
      EMOJI[35, 44] chars:[35, 44, ":invalid:"]
        Leaf:EMOJI_MARKER[35, 36] chars:[35, 36, ":"]
        EMOJI_ID[36, 43] chars:[36, 43, "invalid"]
          Leaf:EMOJI_ID[36, 43] chars:[36, 43, "invalid"]
        Leaf:EMOJI_MARKER[43, 44] chars:[43, 44, ":"]
      Leaf:TEXT[44, 60] chars:[44, 60, " embe … text "]
      EMOJI[60, 69] chars:[60, 69, ":invalid:"]
        Leaf:EMOJI_MARKER[60, 61] chars:[60, 61, ":"]
        EMOJI_ID[61, 68] chars:[61, 68, "invalid"]
          Leaf:EMOJI_ID[61, 68] chars:[61, 68, "invalid"]
        Leaf:EMOJI_MARKER[68, 69] chars:[68, 69, ":"]
      Leaf:TEXT[69, 70] chars:[69, 70, " "]
      Leaf:EOL[70, 71] chars:[70, 71, "\n"]
````````````````````````````````


### Markdown elements - Emphasis

`Emphasis`

underscore

```````````````````````````````` example Markdown elements - Markdown elements - Emphasis: 1
_italic_
text _italic_ embedded 
_italic_ embedded 
text _italic_ 
.
<p><em>italic</em> text <em>italic</em> embedded <em>italic</em> embedded text <em>italic</em> </p>
.
FILE[0, 67] chars:[0, 67, "_ital … ic_ \n"]
  PARAGRAPH_BLOCK[0, 67] chars:[0, 67, "_ital … ic_ \n"]
    TEXT_BLOCK[0, 67] chars:[0, 67, "_ital … ic_ \n"]
      ITALIC[0, 8] chars:[0, 8, "_italic_"]
        Leaf:ITALIC_MARKER[0, 1] chars:[0, 1, "_"]
        Leaf:ITALIC_TEXT[1, 7] chars:[1, 7, "italic"]
        Leaf:ITALIC_MARKER[7, 8] chars:[7, 8, "_"]
      Leaf:TEXT[8, 14] chars:[8, 14, "\ntext "]
      ITALIC[14, 22] chars:[14, 22, "_italic_"]
        Leaf:ITALIC_MARKER[14, 15] chars:[14, 15, "_"]
        Leaf:ITALIC_TEXT[15, 21] chars:[15, 21, "italic"]
        Leaf:ITALIC_MARKER[21, 22] chars:[21, 22, "_"]
      Leaf:TEXT[22, 33] chars:[22, 33, " embe … ded \n"]
      ITALIC[33, 41] chars:[33, 41, "_italic_"]
        Leaf:ITALIC_MARKER[33, 34] chars:[33, 34, "_"]
        Leaf:ITALIC_TEXT[34, 40] chars:[34, 40, "italic"]
        Leaf:ITALIC_MARKER[40, 41] chars:[40, 41, "_"]
      Leaf:TEXT[41, 57] chars:[41, 57, " embe … text "]
      ITALIC[57, 65] chars:[57, 65, "_italic_"]
        Leaf:ITALIC_MARKER[57, 58] chars:[57, 58, "_"]
        Leaf:ITALIC_TEXT[58, 64] chars:[58, 64, "italic"]
        Leaf:ITALIC_MARKER[64, 65] chars:[64, 65, "_"]
      Leaf:TEXT[65, 66] chars:[65, 66, " "]
      Leaf:EOL[66, 67] chars:[66, 67, "\n"]
````````````````````````````````


asterisk

```````````````````````````````` example Markdown elements - Markdown elements - Emphasis: 2
*italic*
text *italic* embedded 
*italic* embedded 
text *italic* 
.
<p><em>italic</em> text <em>italic</em> embedded <em>italic</em> embedded text <em>italic</em> </p>
.
FILE[0, 67] chars:[0, 67, "*ital … ic* \n"]
  PARAGRAPH_BLOCK[0, 67] chars:[0, 67, "*ital … ic* \n"]
    TEXT_BLOCK[0, 67] chars:[0, 67, "*ital … ic* \n"]
      ITALIC[0, 8] chars:[0, 8, "*italic*"]
        Leaf:ITALIC_MARKER[0, 1] chars:[0, 1, "*"]
        Leaf:ITALIC_TEXT[1, 7] chars:[1, 7, "italic"]
        Leaf:ITALIC_MARKER[7, 8] chars:[7, 8, "*"]
      Leaf:TEXT[8, 14] chars:[8, 14, "\ntext "]
      ITALIC[14, 22] chars:[14, 22, "*italic*"]
        Leaf:ITALIC_MARKER[14, 15] chars:[14, 15, "*"]
        Leaf:ITALIC_TEXT[15, 21] chars:[15, 21, "italic"]
        Leaf:ITALIC_MARKER[21, 22] chars:[21, 22, "*"]
      Leaf:TEXT[22, 33] chars:[22, 33, " embe … ded \n"]
      ITALIC[33, 41] chars:[33, 41, "*italic*"]
        Leaf:ITALIC_MARKER[33, 34] chars:[33, 34, "*"]
        Leaf:ITALIC_TEXT[34, 40] chars:[34, 40, "italic"]
        Leaf:ITALIC_MARKER[40, 41] chars:[40, 41, "*"]
      Leaf:TEXT[41, 57] chars:[41, 57, " embe … text "]
      ITALIC[57, 65] chars:[57, 65, "*italic*"]
        Leaf:ITALIC_MARKER[57, 58] chars:[57, 58, "*"]
        Leaf:ITALIC_TEXT[58, 64] chars:[58, 64, "italic"]
        Leaf:ITALIC_MARKER[64, 65] chars:[64, 65, "*"]
      Leaf:TEXT[65, 66] chars:[65, 66, " "]
      Leaf:EOL[66, 67] chars:[66, 67, "\n"]
````````````````````````````````


### Markdown elements - EscapedCharacter

`EscapedCharacter`

```````````````````````````````` example(Markdown elements - Markdown elements - EscapedCharacter: 1) options(pegdown-fail)
\\  \* \~ \t \"
.
<p>\  * ~ \t \"</p>
.
FILE[0, 15] chars:[0, 15, "\\  \ … \t \\""]
  PARAGRAPH_BLOCK[0, 15] chars:[0, 15, "\\  \ … \t \\""]
    TEXT_BLOCK[0, 15] chars:[0, 15, "\\  \ … \t \\""]
      SPECIAL[0, 2] chars:[0, 2, "\\"]
        Leaf:SPECIAL_TEXT_MARKER[0, 1] chars:[0, 1, "\"]
        Leaf:SPECIAL_TEXT[1, 2] chars:[1, 2, "\"]
      Leaf:TEXT[2, 4] chars:[2, 4, "  "]
      SPECIAL[4, 6] chars:[4, 6, "\*"]
        Leaf:SPECIAL_TEXT_MARKER[4, 5] chars:[4, 5, "\"]
        Leaf:SPECIAL_TEXT[5, 6] chars:[5, 6, "*"]
      Leaf:TEXT[6, 7] chars:[6, 7, " "]
      SPECIAL[7, 9] chars:[7, 9, "\~"]
        Leaf:SPECIAL_TEXT_MARKER[7, 8] chars:[7, 8, "\"]
        Leaf:SPECIAL_TEXT[8, 9] chars:[8, 9, "~"]
      Leaf:TEXT[9, 13] chars:[9, 13, " \t "]
      SPECIAL[13, 15] chars:[13, 15, "\\""]
        Leaf:SPECIAL_TEXT_MARKER[13, 14] chars:[13, 14, "\"]
        Leaf:SPECIAL_TEXT[14, 15] chars:[14, 15, "\""]
````````````````````````````````


### Markdown elements - FencedCodeBlock

`FencedCodeBlock`

empty, no info

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 1
```

```
.
<pre><code><br/></code></pre>
.
FILE[0, 9] chars:[0, 9, "```\n\n```\n"]
  VERBATIM[0, 9] chars:[0, 9, "```\n\n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 3]
    Leaf:EOL[3, 4] chars:[3, 4, "\n"]
    VERBATIM_CONTENT[4, 5] chars:[4, 5, "\n"]
      Leaf:VERBATIM_CONTENT[4, 5] chars:[4, 5, "\n"]
    Leaf:VERBATIM_CLOSE[5, 8] chars:[5, 8, "```"]
    Leaf:EOL[8, 9] chars:[8, 9, "\n"]
````````````````````````````````


unterminated

```````````````````````````````` example(Markdown elements - Markdown elements - FencedCodeBlock: 2) options(pegdown-fail)
```
.
<pre><code><br/></code></pre>
.
FILE[0, 3] chars:[0, 3, "```"]
  VERBATIM[0, 3] chars:[0, 3, "```"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
````````````````````````````````


empty, no info, blank line follows

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 3
```

```

.
<pre><code><br/></code></pre>
.
FILE[0, 10] chars:[0, 10, "```\n\n```\n\n"]
  VERBATIM[0, 9] chars:[0, 9, "```\n\n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 3]
    Leaf:EOL[3, 4] chars:[3, 4, "\n"]
    VERBATIM_CONTENT[4, 5] chars:[4, 5, "\n"]
      Leaf:VERBATIM_CONTENT[4, 5] chars:[4, 5, "\n"]
    Leaf:VERBATIM_CLOSE[5, 8] chars:[5, 8, "```"]
    Leaf:EOL[8, 9] chars:[8, 9, "\n"]
  BLANK_LINE[9, 10] chars:[9, 10, "\n"]
    Leaf:BLANK_LINE[9, 10] chars:[9, 10, "\n"]
````````````````````````````````


empty, info

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 4
```info

```
.
<pre><code class="info"><br/></code></pre>
.
FILE[0, 13] chars:[0, 13, "```in … \n```\n"]
  VERBATIM[0, 13] chars:[0, 13, "```in … \n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
      Leaf:VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    VERBATIM_CONTENT[8, 9] chars:[8, 9, "\n"]
      Leaf:VERBATIM_CONTENT[8, 9] chars:[8, 9, "\n"]
    Leaf:VERBATIM_CLOSE[9, 12] chars:[9, 12, "```"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
````````````````````````````````


empty, info, blank line follows

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 5
```info

```

.
<pre><code class="info"><br/></code></pre>
.
FILE[0, 14] chars:[0, 14, "```in … ```\n\n"]
  VERBATIM[0, 13] chars:[0, 13, "```in … \n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
      Leaf:VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    VERBATIM_CONTENT[8, 9] chars:[8, 9, "\n"]
      Leaf:VERBATIM_CONTENT[8, 9] chars:[8, 9, "\n"]
    Leaf:VERBATIM_CLOSE[9, 12] chars:[9, 12, "```"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
  BLANK_LINE[13, 14] chars:[13, 14, "\n"]
    Leaf:BLANK_LINE[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


non empty, no info, blank line follows

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 6
```
some text
```

.
<pre><code>some text
</code></pre>
.
FILE[0, 19] chars:[0, 19, "```\ns … ```\n\n"]
  VERBATIM[0, 18] chars:[0, 18, "```\ns … \n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 3]
    Leaf:EOL[3, 4] chars:[3, 4, "\n"]
    VERBATIM_CONTENT[4, 14] chars:[4, 14, "some text\n"]
      Leaf:VERBATIM_CONTENT[4, 14] chars:[4, 14, "some text\n"]
    Leaf:VERBATIM_CLOSE[14, 17] chars:[14, 17, "```"]
    Leaf:EOL[17, 18] chars:[17, 18, "\n"]
  BLANK_LINE[18, 19] chars:[18, 19, "\n"]
    Leaf:BLANK_LINE[18, 19] chars:[18, 19, "\n"]
````````````````````````````````


non empty, info

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 7
```info
some text
```
.
<pre><code class="info">some text
</code></pre>
.
FILE[0, 22] chars:[0, 22, "```in … \n```\n"]
  VERBATIM[0, 22] chars:[0, 22, "```in … \n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
      Leaf:VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    VERBATIM_CONTENT[8, 18] chars:[8, 18, "some text\n"]
      Leaf:VERBATIM_CONTENT[8, 18] chars:[8, 18, "some text\n"]
    Leaf:VERBATIM_CLOSE[18, 21] chars:[18, 21, "```"]
    Leaf:EOL[21, 22] chars:[21, 22, "\n"]
````````````````````````````````


non empty, info, blank line follows

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 8
```info
some text
```

.
<pre><code class="info">some text
</code></pre>
.
FILE[0, 23] chars:[0, 23, "```in … ```\n\n"]
  VERBATIM[0, 22] chars:[0, 22, "```in … \n```\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
      Leaf:VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    VERBATIM_CONTENT[8, 18] chars:[8, 18, "some text\n"]
      Leaf:VERBATIM_CONTENT[8, 18] chars:[8, 18, "some text\n"]
    Leaf:VERBATIM_CLOSE[18, 21] chars:[18, 21, "```"]
    Leaf:EOL[21, 22] chars:[21, 22, "\n"]
  BLANK_LINE[22, 23] chars:[22, 23, "\n"]
    Leaf:BLANK_LINE[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


non empty, info, blank line follows, unmatched

```````````````````````````````` example Markdown elements - Markdown elements - FencedCodeBlock: 9
```info
some text
~~~

.
<pre><code class="info">some text
</code></pre>
.
FILE[0, 23] chars:[0, 23, "```in … ~~~\n\n"]
  VERBATIM[0, 22] chars:[0, 22, "```in … \n~~~\n"]
    Leaf:VERBATIM_OPEN[0, 3] chars:[0, 3, "```"]
    VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
      Leaf:VERBATIM_LANG[3, 7] chars:[3, 7, "info"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    VERBATIM_CONTENT[8, 18] chars:[8, 18, "some text\n"]
      Leaf:VERBATIM_CONTENT[8, 18] chars:[8, 18, "some text\n"]
    Leaf:VERBATIM_CLOSE[18, 21] chars:[18, 21, "~~~"]
    Leaf:EOL[21, 22] chars:[21, 22, "\n"]
  BLANK_LINE[22, 23] chars:[22, 23, "\n"]
    Leaf:BLANK_LINE[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


### Markdown elements - Footnote

`FootnoteBlock` `Footnote`

basic

```````````````````````````````` example Markdown elements - Markdown elements - Footnote: 1
text [^footnote] embedded.

[^footnote]: footnote text
with continuation

.
<p>text <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup> embedded.</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p>footnote text with continuation<a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 74] chars:[0, 74, "text  … ion\n\n"]
  PARAGRAPH_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
    TEXT_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      FOOTNOTE_REF[5, 16] chars:[5, 16, "[^foo … note]"]
        Leaf:FOOTNOTE_REF_OPEN[5, 7] chars:[5, 7, "[^"]
        FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
          Leaf:FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
        Leaf:FOOTNOTE_REF_CLOSE[15, 16] chars:[15, 16, "]"]
      Leaf:TEXT[16, 26] chars:[16, 26, " embedded."]
      Leaf:EOL[26, 27] chars:[26, 27, "\n"]
  BLANK_LINE[27, 28] chars:[27, 28, "\n"]
    Leaf:BLANK_LINE[27, 28] chars:[27, 28, "\n"]
  FOOTNOTE[28, 73] chars:[28, 73, "[^foo … tion\n"]
    Leaf:FOOTNOTE_OPEN[28, 30] chars:[28, 30, "[^"]
    FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
      Leaf:FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
    Leaf:FOOTNOTE_CLOSE[38, 40] chars:[38, 40, "]:"]
    Leaf:WHITESPACE[40, 41] chars:[40, 41, " "]
    FOOTNOTE_TEXT[41, 73] chars:[41, 73, "footn … tion\n"]
      PARAGRAPH_BLOCK[41, 73] chars:[41, 73, "footn … tion\n"]
        TEXT_BLOCK[41, 73] chars:[41, 73, "footn … tion\n"]
          Leaf:TEXT[41, 72] chars:[41, 72, "footn … ation"]
          Leaf:EOL[72, 73] chars:[72, 73, "\n"]
  BLANK_LINE[73, 74] chars:[73, 74, "\n"]
    Leaf:BLANK_LINE[73, 74] chars:[73, 74, "\n"]
````````````````````````````````


undefined

```````````````````````````````` example Markdown elements - Markdown elements - Footnote: 2
text [^undefined] embedded.

[^footnote]: footnote text
with continuation

.
<p>text <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup> embedded.</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p><a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 75] chars:[0, 75, "text  … ion\n\n"]
  PARAGRAPH_BLOCK[0, 28] chars:[0, 28, "text  … ded.\n"]
    TEXT_BLOCK[0, 28] chars:[0, 28, "text  … ded.\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      FOOTNOTE_REF[5, 17] chars:[5, 17, "[^und … ined]"]
        Leaf:FOOTNOTE_REF_OPEN[5, 7] chars:[5, 7, "[^"]
        FOOTNOTE_REF_ID[7, 16] chars:[7, 16, "undefined"]
          Leaf:FOOTNOTE_REF_ID[7, 16] chars:[7, 16, "undefined"]
        Leaf:FOOTNOTE_REF_CLOSE[16, 17] chars:[16, 17, "]"]
      Leaf:TEXT[17, 27] chars:[17, 27, " embedded."]
      Leaf:EOL[27, 28] chars:[27, 28, "\n"]
  BLANK_LINE[28, 29] chars:[28, 29, "\n"]
    Leaf:BLANK_LINE[28, 29] chars:[28, 29, "\n"]
  FOOTNOTE[29, 74] chars:[29, 74, "[^foo … tion\n"]
    Leaf:FOOTNOTE_OPEN[29, 31] chars:[29, 31, "[^"]
    FOOTNOTE_ID[31, 39] chars:[31, 39, "footnote"]
      Leaf:FOOTNOTE_ID[31, 39] chars:[31, 39, "footnote"]
    Leaf:FOOTNOTE_CLOSE[39, 41] chars:[39, 41, "]:"]
    Leaf:WHITESPACE[41, 42] chars:[41, 42, " "]
    FOOTNOTE_TEXT[42, 74] chars:[42, 74, "footn … tion\n"]
      PARAGRAPH_BLOCK[42, 74] chars:[42, 74, "footn … tion\n"]
        TEXT_BLOCK[42, 74] chars:[42, 74, "footn … tion\n"]
          Leaf:TEXT[42, 73] chars:[42, 73, "footn … ation"]
          Leaf:EOL[73, 74] chars:[73, 74, "\n"]
  BLANK_LINE[74, 75] chars:[74, 75, "\n"]
    Leaf:BLANK_LINE[74, 75] chars:[74, 75, "\n"]
````````````````````````````````


duplicated

```````````````````````````````` example Markdown elements - Markdown elements - Footnote: 3
text [^footnote] embedded.

[^footnote]: footnote text
with continuation

[^footnote]: duplicated footnote text
with continuation

.
<p>text <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup> embedded.</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p>duplicated footnote text with continuation<a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 131] chars:[0, 131, "text  … ion\n\n"]
  PARAGRAPH_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
    TEXT_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      FOOTNOTE_REF[5, 16] chars:[5, 16, "[^foo … note]"]
        Leaf:FOOTNOTE_REF_OPEN[5, 7] chars:[5, 7, "[^"]
        FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
          Leaf:FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
        Leaf:FOOTNOTE_REF_CLOSE[15, 16] chars:[15, 16, "]"]
      Leaf:TEXT[16, 26] chars:[16, 26, " embedded."]
      Leaf:EOL[26, 27] chars:[26, 27, "\n"]
  BLANK_LINE[27, 28] chars:[27, 28, "\n"]
    Leaf:BLANK_LINE[27, 28] chars:[27, 28, "\n"]
  FOOTNOTE[28, 73] chars:[28, 73, "[^foo … tion\n"]
    Leaf:FOOTNOTE_OPEN[28, 30] chars:[28, 30, "[^"]
    FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
      Leaf:FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
    Leaf:FOOTNOTE_CLOSE[38, 40] chars:[38, 40, "]:"]
    Leaf:WHITESPACE[40, 41] chars:[40, 41, " "]
    FOOTNOTE_TEXT[41, 73] chars:[41, 73, "footn … tion\n"]
      PARAGRAPH_BLOCK[41, 73] chars:[41, 73, "footn … tion\n"]
        TEXT_BLOCK[41, 73] chars:[41, 73, "footn … tion\n"]
          Leaf:TEXT[41, 72] chars:[41, 72, "footn … ation"]
          Leaf:EOL[72, 73] chars:[72, 73, "\n"]
  BLANK_LINE[73, 74] chars:[73, 74, "\n"]
    Leaf:BLANK_LINE[73, 74] chars:[73, 74, "\n"]
  FOOTNOTE[74, 130] chars:[74, 130, "[^foo … tion\n"]
    Leaf:FOOTNOTE_OPEN[74, 76] chars:[74, 76, "[^"]
    FOOTNOTE_ID[76, 84] chars:[76, 84, "footnote"]
      Leaf:FOOTNOTE_ID[76, 84] chars:[76, 84, "footnote"]
    Leaf:FOOTNOTE_CLOSE[84, 86] chars:[84, 86, "]:"]
    Leaf:WHITESPACE[86, 87] chars:[86, 87, " "]
    FOOTNOTE_TEXT[87, 130] chars:[87, 130, "dupli … tion\n"]
      PARAGRAPH_BLOCK[87, 130] chars:[87, 130, "dupli … tion\n"]
        TEXT_BLOCK[87, 130] chars:[87, 130, "dupli … tion\n"]
          Leaf:TEXT[87, 129] chars:[87, 129, "dupli … ation"]
          Leaf:EOL[129, 130] chars:[129, 130, "\n"]
  BLANK_LINE[130, 131] chars:[130, 131, "\n"]
    Leaf:BLANK_LINE[130, 131] chars:[130, 131, "\n"]
````````````````````````````````


nested

```````````````````````````````` example Markdown elements - Markdown elements - Footnote: 4
text [^footnote] embedded.

[^footnote]: footnote text with [^another] embedded footnote
with continuation

[^another]: footnote text
with continuation

.
<p>text <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup> embedded.</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p>footnote text with <sup id="fnref-2"><a href="#fn-2" class="footnote-ref">2</a></sup> embedded footnote with continuation<a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
<li id="fn-2"><p><a href="#fnref-2" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 153] chars:[0, 153, "text  … ion\n\n"]
  PARAGRAPH_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
    TEXT_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      FOOTNOTE_REF[5, 16] chars:[5, 16, "[^foo … note]"]
        Leaf:FOOTNOTE_REF_OPEN[5, 7] chars:[5, 7, "[^"]
        FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
          Leaf:FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
        Leaf:FOOTNOTE_REF_CLOSE[15, 16] chars:[15, 16, "]"]
      Leaf:TEXT[16, 26] chars:[16, 26, " embedded."]
      Leaf:EOL[26, 27] chars:[26, 27, "\n"]
  BLANK_LINE[27, 28] chars:[27, 28, "\n"]
    Leaf:BLANK_LINE[27, 28] chars:[27, 28, "\n"]
  FOOTNOTE[28, 107] chars:[28, 107, "[^foo … tion\n"]
    Leaf:FOOTNOTE_OPEN[28, 30] chars:[28, 30, "[^"]
    FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
      Leaf:FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
    Leaf:FOOTNOTE_CLOSE[38, 40] chars:[38, 40, "]:"]
    Leaf:WHITESPACE[40, 41] chars:[40, 41, " "]
    FOOTNOTE_TEXT[41, 107] chars:[41, 107, "footn … tion\n"]
      PARAGRAPH_BLOCK[41, 107] chars:[41, 107, "footn … tion\n"]
        TEXT_BLOCK[41, 107] chars:[41, 107, "footn … tion\n"]
          Leaf:TEXT[41, 60] chars:[41, 60, "footn … with "]
          FOOTNOTE_REF[60, 70] chars:[60, 70, "[^another]"]
            Leaf:FOOTNOTE_REF_OPEN[60, 62] chars:[60, 62, "[^"]
            FOOTNOTE_REF_ID[62, 69] chars:[62, 69, "another"]
              Leaf:FOOTNOTE_REF_ID[62, 69] chars:[62, 69, "another"]
            Leaf:FOOTNOTE_REF_CLOSE[69, 70] chars:[69, 70, "]"]
          Leaf:TEXT[70, 106] chars:[70, 106, " embe … ation"]
          Leaf:EOL[106, 107] chars:[106, 107, "\n"]
  BLANK_LINE[107, 108] chars:[107, 108, "\n"]
    Leaf:BLANK_LINE[107, 108] chars:[107, 108, "\n"]
  FOOTNOTE[108, 152] chars:[108, 152, "[^ano … tion\n"]
    Leaf:FOOTNOTE_OPEN[108, 110] chars:[108, 110, "[^"]
    FOOTNOTE_ID[110, 117] chars:[110, 117, "another"]
      Leaf:FOOTNOTE_ID[110, 117] chars:[110, 117, "another"]
    Leaf:FOOTNOTE_CLOSE[117, 119] chars:[117, 119, "]:"]
    Leaf:WHITESPACE[119, 120] chars:[119, 120, " "]
    FOOTNOTE_TEXT[120, 152] chars:[120, 152, "footn … tion\n"]
      PARAGRAPH_BLOCK[120, 152] chars:[120, 152, "footn … tion\n"]
        TEXT_BLOCK[120, 152] chars:[120, 152, "footn … tion\n"]
          Leaf:TEXT[120, 151] chars:[120, 151, "footn … ation"]
          Leaf:EOL[151, 152] chars:[151, 152, "\n"]
  BLANK_LINE[152, 153] chars:[152, 153, "\n"]
    Leaf:BLANK_LINE[152, 153] chars:[152, 153, "\n"]
````````````````````````````````


circular

```````````````````````````````` example Markdown elements - Markdown elements - Footnote: 5
text [^footnote] embedded.

[^footnote]: footnote text with [^another] embedded footnote
with continuation

[^another]: footnote text with [^another] embedded footnote
with continuation

.
<p>text <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup> embedded.</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p>footnote text with <sup id="fnref-2"><a href="#fn-2" class="footnote-ref">2</a></sup> embedded footnote with continuation<a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
<li id="fn-2"><p><a href="#fnref-2" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 187] chars:[0, 187, "text  … ion\n\n"]
  PARAGRAPH_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
    TEXT_BLOCK[0, 27] chars:[0, 27, "text  … ded.\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      FOOTNOTE_REF[5, 16] chars:[5, 16, "[^foo … note]"]
        Leaf:FOOTNOTE_REF_OPEN[5, 7] chars:[5, 7, "[^"]
        FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
          Leaf:FOOTNOTE_REF_ID[7, 15] chars:[7, 15, "footnote"]
        Leaf:FOOTNOTE_REF_CLOSE[15, 16] chars:[15, 16, "]"]
      Leaf:TEXT[16, 26] chars:[16, 26, " embedded."]
      Leaf:EOL[26, 27] chars:[26, 27, "\n"]
  BLANK_LINE[27, 28] chars:[27, 28, "\n"]
    Leaf:BLANK_LINE[27, 28] chars:[27, 28, "\n"]
  FOOTNOTE[28, 107] chars:[28, 107, "[^foo … tion\n"]
    Leaf:FOOTNOTE_OPEN[28, 30] chars:[28, 30, "[^"]
    FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
      Leaf:FOOTNOTE_ID[30, 38] chars:[30, 38, "footnote"]
    Leaf:FOOTNOTE_CLOSE[38, 40] chars:[38, 40, "]:"]
    Leaf:WHITESPACE[40, 41] chars:[40, 41, " "]
    FOOTNOTE_TEXT[41, 107] chars:[41, 107, "footn … tion\n"]
      PARAGRAPH_BLOCK[41, 107] chars:[41, 107, "footn … tion\n"]
        TEXT_BLOCK[41, 107] chars:[41, 107, "footn … tion\n"]
          Leaf:TEXT[41, 60] chars:[41, 60, "footn … with "]
          FOOTNOTE_REF[60, 70] chars:[60, 70, "[^another]"]
            Leaf:FOOTNOTE_REF_OPEN[60, 62] chars:[60, 62, "[^"]
            FOOTNOTE_REF_ID[62, 69] chars:[62, 69, "another"]
              Leaf:FOOTNOTE_REF_ID[62, 69] chars:[62, 69, "another"]
            Leaf:FOOTNOTE_REF_CLOSE[69, 70] chars:[69, 70, "]"]
          Leaf:TEXT[70, 106] chars:[70, 106, " embe … ation"]
          Leaf:EOL[106, 107] chars:[106, 107, "\n"]
  BLANK_LINE[107, 108] chars:[107, 108, "\n"]
    Leaf:BLANK_LINE[107, 108] chars:[107, 108, "\n"]
  FOOTNOTE[108, 186] chars:[108, 186, "[^ano … tion\n"]
    Leaf:FOOTNOTE_OPEN[108, 110] chars:[108, 110, "[^"]
    FOOTNOTE_ID[110, 117] chars:[110, 117, "another"]
      Leaf:FOOTNOTE_ID[110, 117] chars:[110, 117, "another"]
    Leaf:FOOTNOTE_CLOSE[117, 119] chars:[117, 119, "]:"]
    Leaf:WHITESPACE[119, 120] chars:[119, 120, " "]
    FOOTNOTE_TEXT[120, 186] chars:[120, 186, "footn … tion\n"]
      PARAGRAPH_BLOCK[120, 186] chars:[120, 186, "footn … tion\n"]
        TEXT_BLOCK[120, 186] chars:[120, 186, "footn … tion\n"]
          Leaf:TEXT[120, 139] chars:[120, 139, "footn … with "]
          FOOTNOTE_REF[139, 149] chars:[139, 149, "[^another]"]
            Leaf:FOOTNOTE_REF_OPEN[139, 141] chars:[139, 141, "[^"]
            FOOTNOTE_REF_ID[141, 148] chars:[141, 148, "another"]
              Leaf:FOOTNOTE_REF_ID[141, 148] chars:[141, 148, "another"]
            Leaf:FOOTNOTE_REF_CLOSE[148, 149] chars:[148, 149, "]"]
          Leaf:TEXT[149, 185] chars:[149, 185, " embe … ation"]
          Leaf:EOL[185, 186] chars:[185, 186, "\n"]
  BLANK_LINE[186, 187] chars:[186, 187, "\n"]
    Leaf:BLANK_LINE[186, 187] chars:[186, 187, "\n"]
````````````````````````````````


### Markdown elements - HardLineBreak

`HardLineBreak`

minimal

```````````````````````````````` example Markdown elements - Markdown elements - HardLineBreak: 1
text with hard line break  
more text
.
<p>text with hard line break<br/>more text</p>
.
FILE[0, 38] chars:[0, 38, "text  … text\n"]
  PARAGRAPH_BLOCK[0, 38] chars:[0, 38, "text  … text\n"]
    TEXT_BLOCK[0, 38] chars:[0, 38, "text  … text\n"]
      Leaf:TEXT[0, 25] chars:[0, 25, "text  … break"]
      Leaf:LINE_BREAK_SPACES[25, 27] chars:[25, 27, "  "]
      Leaf:EOL[27, 28] chars:[27, 28, "\n"]
      Leaf:TEXT[28, 37] chars:[28, 37, "more text"]
      Leaf:EOL[37, 38] chars:[37, 38, "\n"]
````````````````````````````````


non minimal

```````````````````````````````` example Markdown elements - Markdown elements - HardLineBreak: 2
text with hard line break   
more text
.
<p>text with hard line break<br/>more text</p>
.
FILE[0, 39] chars:[0, 39, "text  … text\n"]
  PARAGRAPH_BLOCK[0, 39] chars:[0, 39, "text  … text\n"]
    TEXT_BLOCK[0, 39] chars:[0, 39, "text  … text\n"]
      Leaf:TEXT[0, 25] chars:[0, 25, "text  … break"]
      Leaf:LINE_BREAK_SPACES[25, 28] chars:[25, 28, "   "]
      Leaf:EOL[28, 29] chars:[28, 29, "\n"]
      Leaf:TEXT[29, 38] chars:[29, 38, "more text"]
      Leaf:EOL[38, 39] chars:[38, 39, "\n"]
````````````````````````````````


### Markdown elements - Heading

`Heading`

Setext 1

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 1
Heading 1
===
.
<h1 id="heading-1"><a href="#heading-1" name="heading-1"></a>Heading 1</h1>
.
FILE[0, 14] chars:[0, 14, "Headi … \n===\n"]
  SETEXT_HEADER[0, 14] chars:[0, 14, "Headi … \n===\n"]
    HEADER_TEXT[0, 9] chars:[0, 9, "Heading 1"]
      Leaf:HEADER_TEXT[0, 9] chars:[0, 9, "Heading 1"]
    Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    Leaf:HEADER_SETEXT_MARKER[10, 13] chars:[10, 13, "==="]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


Setext 2

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 2
Heading 2
---
.
<h2 id="heading-2"><a href="#heading-2" name="heading-2"></a>Heading 2</h2>
.
FILE[0, 14] chars:[0, 14, "Headi … \n---\n"]
  SETEXT_HEADER[0, 14] chars:[0, 14, "Headi … \n---\n"]
    HEADER_TEXT[0, 9] chars:[0, 9, "Heading 2"]
      Leaf:HEADER_TEXT[0, 9] chars:[0, 9, "Heading 2"]
    Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    Leaf:HEADER_SETEXT_MARKER[10, 13] chars:[10, 13, "---"]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


Setext 1 with inlines

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 3
Heading 1 **bold** _italic_ `code` 
==================================
.
<h1 id="heading-1"><a href="#heading-1" name="heading-1"></a>Heading 1</h1>
.
FILE[0, 71] chars:[0, 71, "Headi … ====\n"]
  SETEXT_HEADER[0, 71] chars:[0, 71, "Headi … ====\n"]
    HEADER_TEXT[0, 34] chars:[0, 34, "Headi … code`"]
      Leaf:HEADER_TEXT[0, 10] chars:[0, 10, "Heading 1 "]
      BOLD[10, 18] chars:[10, 18, "**bold**"]
        Leaf:HEADER_TEXT_BOLD_MARKER[10, 12] chars:[10, 12, "**"]
        Leaf:HEADER_TEXT_BOLD_TEXT[12, 16] chars:[12, 16, "bold"]
        Leaf:HEADER_TEXT_BOLD_MARKER[16, 18] chars:[16, 18, "**"]
      Leaf:HEADER_TEXT[18, 19] chars:[18, 19, " "]
      ITALIC[19, 27] chars:[19, 27, "_italic_"]
        Leaf:HEADER_TEXT_ITALIC_MARKER[19, 20] chars:[19, 20, "_"]
        Leaf:HEADER_TEXT_ITALIC_TEXT[20, 26] chars:[20, 26, "italic"]
        Leaf:HEADER_TEXT_ITALIC_MARKER[26, 27] chars:[26, 27, "_"]
      Leaf:HEADER_TEXT[27, 28] chars:[27, 28, " "]
      CODE[28, 34] chars:[28, 34, "`code`"]
        Leaf:HEADER_TEXT_CODE_MARKER[28, 29] chars:[28, 29, "`"]
        Leaf:HEADER_TEXT_CODE_TEXT[29, 33] chars:[29, 33, "code"]
        Leaf:HEADER_TEXT_CODE_MARKER[33, 34] chars:[33, 34, "`"]
    Leaf:WHITESPACE[34, 35] chars:[34, 35, " "]
    Leaf:EOL[35, 36] chars:[35, 36, "\n"]
    Leaf:HEADER_SETEXT_MARKER[36, 70] chars:[36, 70, "===== … ====="]
    Leaf:EOL[70, 71] chars:[70, 71, "\n"]
````````````````````````````````


Setext 2 with inliines

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 4
Heading 2 **bold** _italic_ `code` 
----------------------------------
.
<h2 id="heading-2"><a href="#heading-2" name="heading-2"></a>Heading 2</h2>
.
FILE[0, 71] chars:[0, 71, "Headi … ----\n"]
  SETEXT_HEADER[0, 71] chars:[0, 71, "Headi … ----\n"]
    HEADER_TEXT[0, 34] chars:[0, 34, "Headi … code`"]
      Leaf:HEADER_TEXT[0, 10] chars:[0, 10, "Heading 2 "]
      BOLD[10, 18] chars:[10, 18, "**bold**"]
        Leaf:HEADER_TEXT_BOLD_MARKER[10, 12] chars:[10, 12, "**"]
        Leaf:HEADER_TEXT_BOLD_TEXT[12, 16] chars:[12, 16, "bold"]
        Leaf:HEADER_TEXT_BOLD_MARKER[16, 18] chars:[16, 18, "**"]
      Leaf:HEADER_TEXT[18, 19] chars:[18, 19, " "]
      ITALIC[19, 27] chars:[19, 27, "_italic_"]
        Leaf:HEADER_TEXT_ITALIC_MARKER[19, 20] chars:[19, 20, "_"]
        Leaf:HEADER_TEXT_ITALIC_TEXT[20, 26] chars:[20, 26, "italic"]
        Leaf:HEADER_TEXT_ITALIC_MARKER[26, 27] chars:[26, 27, "_"]
      Leaf:HEADER_TEXT[27, 28] chars:[27, 28, " "]
      CODE[28, 34] chars:[28, 34, "`code`"]
        Leaf:HEADER_TEXT_CODE_MARKER[28, 29] chars:[28, 29, "`"]
        Leaf:HEADER_TEXT_CODE_TEXT[29, 33] chars:[29, 33, "code"]
        Leaf:HEADER_TEXT_CODE_MARKER[33, 34] chars:[33, 34, "`"]
    Leaf:WHITESPACE[34, 35] chars:[34, 35, " "]
    Leaf:EOL[35, 36] chars:[35, 36, "\n"]
    Leaf:HEADER_SETEXT_MARKER[36, 70] chars:[36, 70, "----- … -----"]
    Leaf:EOL[70, 71] chars:[70, 71, "\n"]
````````````````````````````````


Atx 1

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 5
# Heading 1
# Heading 1 Tail #
.
<h1 id="heading-1"><a href="#heading-1" name="heading-1"></a>Heading 1</h1>
<h1 id="heading-1-tail"><a href="#heading-1-tail" name="heading-1-tail"></a>Heading 1 Tail</h1>
.
FILE[0, 31] chars:[0, 31, "# Hea … il #\n"]
  ATX_HEADER[0, 12] chars:[0, 12, "# Hea … ng 1\n"]
    Leaf:HEADER_ATX_MARKER[0, 1] chars:[0, 1, "#"]
    Leaf:WHITESPACE[1, 2] chars:[1, 2, " "]
    HEADER_TEXT[2, 11] chars:[2, 11, "Heading 1"]
      Leaf:HEADER_TEXT[2, 11] chars:[2, 11, "Heading 1"]
    Leaf:EOL[11, 12] chars:[11, 12, "\n"]
  ATX_HEADER[12, 31] chars:[12, 31, "# Hea … il #\n"]
    Leaf:HEADER_ATX_MARKER[12, 13] chars:[12, 13, "#"]
    Leaf:WHITESPACE[13, 14] chars:[13, 14, " "]
    HEADER_TEXT[14, 28] chars:[14, 28, "Headi …  Tail"]
      Leaf:HEADER_TEXT[14, 28] chars:[14, 28, "Headi …  Tail"]
    Leaf:WHITESPACE[28, 29] chars:[28, 29, " "]
    Leaf:ATX_HEADER[29, 30] chars:[29, 30, "#"]
    Leaf:EOL[30, 31] chars:[30, 31, "\n"]
````````````````````````````````


Atx 1 with inlines

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 6
# Heading 1 **bold** _italic_ `code`
.
<h1 id="heading-1"><a href="#heading-1" name="heading-1"></a>Heading 1</h1>
.
FILE[0, 37] chars:[0, 37, "# Hea … ode`\n"]
  ATX_HEADER[0, 37] chars:[0, 37, "# Hea … ode`\n"]
    Leaf:HEADER_ATX_MARKER[0, 1] chars:[0, 1, "#"]
    Leaf:WHITESPACE[1, 2] chars:[1, 2, " "]
    HEADER_TEXT[2, 36] chars:[2, 36, "Headi … code`"]
      Leaf:HEADER_TEXT[2, 12] chars:[2, 12, "Heading 1 "]
      BOLD[12, 20] chars:[12, 20, "**bold**"]
        Leaf:HEADER_TEXT_BOLD_MARKER[12, 14] chars:[12, 14, "**"]
        Leaf:HEADER_TEXT_BOLD_TEXT[14, 18] chars:[14, 18, "bold"]
        Leaf:HEADER_TEXT_BOLD_MARKER[18, 20] chars:[18, 20, "**"]
      Leaf:HEADER_TEXT[20, 21] chars:[20, 21, " "]
      ITALIC[21, 29] chars:[21, 29, "_italic_"]
        Leaf:HEADER_TEXT_ITALIC_MARKER[21, 22] chars:[21, 22, "_"]
        Leaf:HEADER_TEXT_ITALIC_TEXT[22, 28] chars:[22, 28, "italic"]
        Leaf:HEADER_TEXT_ITALIC_MARKER[28, 29] chars:[28, 29, "_"]
      Leaf:HEADER_TEXT[29, 30] chars:[29, 30, " "]
      CODE[30, 36] chars:[30, 36, "`code`"]
        Leaf:HEADER_TEXT_CODE_MARKER[30, 31] chars:[30, 31, "`"]
        Leaf:HEADER_TEXT_CODE_TEXT[31, 35] chars:[31, 35, "code"]
        Leaf:HEADER_TEXT_CODE_MARKER[35, 36] chars:[35, 36, "`"]
    Leaf:EOL[36, 37] chars:[36, 37, "\n"]
````````````````````````````````


Atx 2

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 7
## Heading 2
## Heading 2 Tail #
.
<h2 id="heading-2"><a href="#heading-2" name="heading-2"></a>Heading 2</h2>
<h2 id="heading-2-tail"><a href="#heading-2-tail" name="heading-2-tail"></a>Heading 2 Tail</h2>
.
FILE[0, 33] chars:[0, 33, "## He … il #\n"]
  ATX_HEADER[0, 13] chars:[0, 13, "## He … ng 2\n"]
    Leaf:HEADER_ATX_MARKER[0, 2] chars:[0, 2, "##"]
    Leaf:WHITESPACE[2, 3] chars:[2, 3, " "]
    HEADER_TEXT[3, 12] chars:[3, 12, "Heading 2"]
      Leaf:HEADER_TEXT[3, 12] chars:[3, 12, "Heading 2"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
  ATX_HEADER[13, 33] chars:[13, 33, "## He … il #\n"]
    Leaf:HEADER_ATX_MARKER[13, 15] chars:[13, 15, "##"]
    Leaf:WHITESPACE[15, 16] chars:[15, 16, " "]
    HEADER_TEXT[16, 30] chars:[16, 30, "Headi …  Tail"]
      Leaf:HEADER_TEXT[16, 30] chars:[16, 30, "Headi …  Tail"]
    Leaf:WHITESPACE[30, 31] chars:[30, 31, " "]
    Leaf:ATX_HEADER[31, 32] chars:[31, 32, "#"]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


Atx 3

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 8
### Heading 3
### Heading 3 Tail #
.
<h3 id="heading-3"><a href="#heading-3" name="heading-3"></a>Heading 3</h3>
<h3 id="heading-3-tail"><a href="#heading-3-tail" name="heading-3-tail"></a>Heading 3 Tail</h3>
.
FILE[0, 35] chars:[0, 35, "### H … il #\n"]
  ATX_HEADER[0, 14] chars:[0, 14, "### H … ng 3\n"]
    Leaf:HEADER_ATX_MARKER[0, 3] chars:[0, 3, "###"]
    Leaf:WHITESPACE[3, 4] chars:[3, 4, " "]
    HEADER_TEXT[4, 13] chars:[4, 13, "Heading 3"]
      Leaf:HEADER_TEXT[4, 13] chars:[4, 13, "Heading 3"]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
  ATX_HEADER[14, 35] chars:[14, 35, "### H … il #\n"]
    Leaf:HEADER_ATX_MARKER[14, 17] chars:[14, 17, "###"]
    Leaf:WHITESPACE[17, 18] chars:[17, 18, " "]
    HEADER_TEXT[18, 32] chars:[18, 32, "Headi …  Tail"]
      Leaf:HEADER_TEXT[18, 32] chars:[18, 32, "Headi …  Tail"]
    Leaf:WHITESPACE[32, 33] chars:[32, 33, " "]
    Leaf:ATX_HEADER[33, 34] chars:[33, 34, "#"]
    Leaf:EOL[34, 35] chars:[34, 35, "\n"]
````````````````````````````````


Atx 4

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 9
#### Heading 4
#### Heading 4 Tail #
.
<h4 id="heading-4"><a href="#heading-4" name="heading-4"></a>Heading 4</h4>
<h4 id="heading-4-tail"><a href="#heading-4-tail" name="heading-4-tail"></a>Heading 4 Tail</h4>
.
FILE[0, 37] chars:[0, 37, "####  … il #\n"]
  ATX_HEADER[0, 15] chars:[0, 15, "####  … ng 4\n"]
    Leaf:HEADER_ATX_MARKER[0, 4] chars:[0, 4, "####"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    HEADER_TEXT[5, 14] chars:[5, 14, "Heading 4"]
      Leaf:HEADER_TEXT[5, 14] chars:[5, 14, "Heading 4"]
    Leaf:EOL[14, 15] chars:[14, 15, "\n"]
  ATX_HEADER[15, 37] chars:[15, 37, "####  … il #\n"]
    Leaf:HEADER_ATX_MARKER[15, 19] chars:[15, 19, "####"]
    Leaf:WHITESPACE[19, 20] chars:[19, 20, " "]
    HEADER_TEXT[20, 34] chars:[20, 34, "Headi …  Tail"]
      Leaf:HEADER_TEXT[20, 34] chars:[20, 34, "Headi …  Tail"]
    Leaf:WHITESPACE[34, 35] chars:[34, 35, " "]
    Leaf:ATX_HEADER[35, 36] chars:[35, 36, "#"]
    Leaf:EOL[36, 37] chars:[36, 37, "\n"]
````````````````````````````````


Atx 5

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 10
##### Heading 5
##### Heading 5 Tail #
.
<h5 id="heading-5"><a href="#heading-5" name="heading-5"></a>Heading 5</h5>
<h5 id="heading-5-tail"><a href="#heading-5-tail" name="heading-5-tail"></a>Heading 5 Tail</h5>
.
FILE[0, 39] chars:[0, 39, "##### … il #\n"]
  ATX_HEADER[0, 16] chars:[0, 16, "##### … ng 5\n"]
    Leaf:HEADER_ATX_MARKER[0, 5] chars:[0, 5, "#####"]
    Leaf:WHITESPACE[5, 6] chars:[5, 6, " "]
    HEADER_TEXT[6, 15] chars:[6, 15, "Heading 5"]
      Leaf:HEADER_TEXT[6, 15] chars:[6, 15, "Heading 5"]
    Leaf:EOL[15, 16] chars:[15, 16, "\n"]
  ATX_HEADER[16, 39] chars:[16, 39, "##### … il #\n"]
    Leaf:HEADER_ATX_MARKER[16, 21] chars:[16, 21, "#####"]
    Leaf:WHITESPACE[21, 22] chars:[21, 22, " "]
    HEADER_TEXT[22, 36] chars:[22, 36, "Headi …  Tail"]
      Leaf:HEADER_TEXT[22, 36] chars:[22, 36, "Headi …  Tail"]
    Leaf:WHITESPACE[36, 37] chars:[36, 37, " "]
    Leaf:ATX_HEADER[37, 38] chars:[37, 38, "#"]
    Leaf:EOL[38, 39] chars:[38, 39, "\n"]
````````````````````````````````


Atx 6

```````````````````````````````` example Markdown elements - Markdown elements - Heading: 11
###### Heading 6
###### Heading 6 Tail #
.
<h6 id="heading-6"><a href="#heading-6" name="heading-6"></a>Heading 6</h6>
<h6 id="heading-6-tail"><a href="#heading-6-tail" name="heading-6-tail"></a>Heading 6 Tail</h6>
.
FILE[0, 41] chars:[0, 41, "##### … il #\n"]
  ATX_HEADER[0, 17] chars:[0, 17, "##### … ng 6\n"]
    Leaf:HEADER_ATX_MARKER[0, 6] chars:[0, 6, "######"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    HEADER_TEXT[7, 16] chars:[7, 16, "Heading 6"]
      Leaf:HEADER_TEXT[7, 16] chars:[7, 16, "Heading 6"]
    Leaf:EOL[16, 17] chars:[16, 17, "\n"]
  ATX_HEADER[17, 41] chars:[17, 41, "##### … il #\n"]
    Leaf:HEADER_ATX_MARKER[17, 23] chars:[17, 23, "######"]
    Leaf:WHITESPACE[23, 24] chars:[23, 24, " "]
    HEADER_TEXT[24, 38] chars:[24, 38, "Headi …  Tail"]
      Leaf:HEADER_TEXT[24, 38] chars:[24, 38, "Headi …  Tail"]
    Leaf:WHITESPACE[38, 39] chars:[38, 39, " "]
    Leaf:ATX_HEADER[39, 40] chars:[39, 40, "#"]
    Leaf:EOL[40, 41] chars:[40, 41, "\n"]
````````````````````````````````


### Markdown elements - HtmlAnchor

`HtmlAnchor`

empty

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 1) options(flexmark-ignore)
<a id="id"></a>
.
<a id="id"></a>
.
FILE[0, 16] chars:[0, 16, "<a id … </a>\n"]
  HTML_BLOCK[0, 16] chars:[0, 16, "<a id … </a>\n"]
    ANCHOR[0, 11] chars:[0, 11, "<a id … \"id\">"]
      Leaf:ANCHOR_MARK[0, 7] chars:[0, 7, "<a id=\""]
      ANCHOR_ID[7, 9] chars:[7, 9, "id"]
        Leaf:ANCHOR_ID[7, 9] chars:[7, 9, "id"]
      Leaf:ANCHOR_MARK[9, 11] chars:[9, 11, "\">"]
    Leaf:HTML_BLOCK[11, 15] chars:[11, 15, "</a>"]
    Leaf:EOL[15, 16] chars:[15, 16, "\n"]
````````````````````````````````


non-empty

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 2) options(flexmark-ignore)
<a id="id">text</a>
.
<a id="id">text</a>
.
FILE[0, 20] chars:[0, 20, "<a id … </a>\n"]
  HTML_BLOCK[0, 20] chars:[0, 20, "<a id … </a>\n"]
    ANCHOR[0, 11] chars:[0, 11, "<a id … \"id\">"]
      Leaf:ANCHOR_MARK[0, 7] chars:[0, 7, "<a id=\""]
      ANCHOR_ID[7, 9] chars:[7, 9, "id"]
        Leaf:ANCHOR_ID[7, 9] chars:[7, 9, "id"]
      Leaf:ANCHOR_MARK[9, 11] chars:[9, 11, "\">"]
    Leaf:HTML_BLOCK[11, 19] chars:[11, 19, "text</a>"]
    Leaf:EOL[19, 20] chars:[19, 20, "\n"]
````````````````````````````````


empty leading

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 3) options(flexmark-ignore)
<a id="id"></a> embedded
.
<p><a id="id"></a> embedded</p>
.
FILE[0, 25] chars:[0, 25, "<a id … dded\n"]
  PARAGRAPH_BLOCK[0, 25] chars:[0, 25, "<a id … dded\n"]
    TEXT_BLOCK[0, 25] chars:[0, 25, "<a id … dded\n"]
      ANCHOR[0, 11] chars:[0, 11, "<a id … \"id\">"]
        Leaf:ANCHOR_MARK[0, 7] chars:[0, 7, "<a id=\""]
        ANCHOR_ID[7, 9] chars:[7, 9, "id"]
          Leaf:ANCHOR_ID[7, 9] chars:[7, 9, "id"]
        Leaf:ANCHOR_MARK[9, 11] chars:[9, 11, "\">"]
      Leaf:INLINE_HTML[11, 15] chars:[11, 15, "</a>"]
      Leaf:TEXT[15, 24] chars:[15, 24, " embedded"]
      Leaf:EOL[24, 25] chars:[24, 25, "\n"]
````````````````````````````````


non-empty leading

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 4) options(flexmark-ignore)
text with <a id="id">text</a> embedded
.
<p>text with <a id="id">text</a> embedded</p>
.
FILE[0, 39] chars:[0, 39, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 39] chars:[0, 39, "text  … dded\n"]
    TEXT_BLOCK[0, 39] chars:[0, 39, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      ANCHOR[10, 21] chars:[10, 21, "<a id … \"id\">"]
        Leaf:ANCHOR_MARK[10, 17] chars:[10, 17, "<a id=\""]
        ANCHOR_ID[17, 19] chars:[17, 19, "id"]
          Leaf:ANCHOR_ID[17, 19] chars:[17, 19, "id"]
        Leaf:ANCHOR_MARK[19, 21] chars:[19, 21, "\">"]
      Leaf:TEXT[21, 25] chars:[21, 25, "text"]
      Leaf:INLINE_HTML[25, 29] chars:[25, 29, "</a>"]
      Leaf:TEXT[29, 38] chars:[29, 38, " embedded"]
      Leaf:EOL[38, 39] chars:[38, 39, "\n"]
````````````````````````````````


empty embedded

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 5) options(flexmark-ignore)
text with <a id="id"></a> embedded
.
<p>text with <a id="id"></a> embedded</p>
.
FILE[0, 35] chars:[0, 35, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 35] chars:[0, 35, "text  … dded\n"]
    TEXT_BLOCK[0, 35] chars:[0, 35, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      ANCHOR[10, 21] chars:[10, 21, "<a id … \"id\">"]
        Leaf:ANCHOR_MARK[10, 17] chars:[10, 17, "<a id=\""]
        ANCHOR_ID[17, 19] chars:[17, 19, "id"]
          Leaf:ANCHOR_ID[17, 19] chars:[17, 19, "id"]
        Leaf:ANCHOR_MARK[19, 21] chars:[19, 21, "\">"]
      Leaf:INLINE_HTML[21, 25] chars:[21, 25, "</a>"]
      Leaf:TEXT[25, 34] chars:[25, 34, " embedded"]
      Leaf:EOL[34, 35] chars:[34, 35, "\n"]
````````````````````````````````


non-empty embedded

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 6) options(flexmark-ignore)
text with <a id="id">text</a> embedded
.
<p>text with <a id="id">text</a> embedded</p>
.
FILE[0, 39] chars:[0, 39, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 39] chars:[0, 39, "text  … dded\n"]
    TEXT_BLOCK[0, 39] chars:[0, 39, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      ANCHOR[10, 21] chars:[10, 21, "<a id … \"id\">"]
        Leaf:ANCHOR_MARK[10, 17] chars:[10, 17, "<a id=\""]
        ANCHOR_ID[17, 19] chars:[17, 19, "id"]
          Leaf:ANCHOR_ID[17, 19] chars:[17, 19, "id"]
        Leaf:ANCHOR_MARK[19, 21] chars:[19, 21, "\">"]
      Leaf:TEXT[21, 25] chars:[21, 25, "text"]
      Leaf:INLINE_HTML[25, 29] chars:[25, 29, "</a>"]
      Leaf:TEXT[29, 38] chars:[29, 38, " embedded"]
      Leaf:EOL[38, 39] chars:[38, 39, "\n"]
````````````````````````````````


empty trailing

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 7) options(flexmark-ignore)
text with <a id="id"></a>
.
<p>text with <a id="id"></a></p>
.
FILE[0, 26] chars:[0, 26, "text  … </a>\n"]
  PARAGRAPH_BLOCK[0, 26] chars:[0, 26, "text  … </a>\n"]
    TEXT_BLOCK[0, 26] chars:[0, 26, "text  … </a>\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      ANCHOR[10, 21] chars:[10, 21, "<a id … \"id\">"]
        Leaf:ANCHOR_MARK[10, 17] chars:[10, 17, "<a id=\""]
        ANCHOR_ID[17, 19] chars:[17, 19, "id"]
          Leaf:ANCHOR_ID[17, 19] chars:[17, 19, "id"]
        Leaf:ANCHOR_MARK[19, 21] chars:[19, 21, "\">"]
      Leaf:INLINE_HTML[21, 25] chars:[21, 25, "</a>"]
      Leaf:EOL[25, 26] chars:[25, 26, "\n"]
````````````````````````````````


non-empty trailing

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlAnchor: 8) options(flexmark-ignore)
text with <a id="id">text</a>
.
<p>text with <a id="id">text</a></p>
.
FILE[0, 30] chars:[0, 30, "text  … </a>\n"]
  PARAGRAPH_BLOCK[0, 30] chars:[0, 30, "text  … </a>\n"]
    TEXT_BLOCK[0, 30] chars:[0, 30, "text  … </a>\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      ANCHOR[10, 21] chars:[10, 21, "<a id … \"id\">"]
        Leaf:ANCHOR_MARK[10, 17] chars:[10, 17, "<a id=\""]
        ANCHOR_ID[17, 19] chars:[17, 19, "id"]
          Leaf:ANCHOR_ID[17, 19] chars:[17, 19, "id"]
        Leaf:ANCHOR_MARK[19, 21] chars:[19, 21, "\">"]
      Leaf:TEXT[21, 25] chars:[21, 25, "text"]
      Leaf:INLINE_HTML[25, 29] chars:[25, 29, "</a>"]
      Leaf:EOL[29, 30] chars:[29, 30, "\n"]
````````````````````````````````


### Markdown elements - HtmlBlock

`HtmlBlock`

Html Blocks

```````````````````````````````` example Markdown elements - Markdown elements - HtmlBlock: 1
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
FILE[0, 46] chars:[0, 46, "<div> … div>\n"]
  HTML_BLOCK[0, 46] chars:[0, 46, "<div> … div>\n"]
    Leaf:HTML_BLOCK[0, 45] chars:[0, 45, "<div> … /div>"]
    Leaf:EOL[45, 46] chars:[45, 46, "\n"]
````````````````````````````````


### Markdown elements - HtmlCommentBlock

`HtmlCommentBlock`

empty

```````````````````````````````` example Markdown elements - Markdown elements - HtmlCommentBlock: 1
<!---->
.
<!---->
.
FILE[0, 8] chars:[0, 8, "<!---->\n"]
  HTML_BLOCK[0, 8] chars:[0, 8, "<!---->\n"]
    Leaf:COMMENT[0, 7] chars:[0, 7, "<!---->"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
````````````````````````````````


non-empty, no whitespace

```````````````````````````````` example Markdown elements - Markdown elements - HtmlCommentBlock: 2
<!--test-->
.
<!--test-->
.
FILE[0, 12] chars:[0, 12, "<!--t … t-->\n"]
  HTML_BLOCK[0, 12] chars:[0, 12, "<!--t … t-->\n"]
    Leaf:COMMENT[0, 11] chars:[0, 11, "<!--t … st-->"]
    Leaf:EOL[11, 12] chars:[11, 12, "\n"]
````````````````````````````````


non-empty whitespace

```````````````````````````````` example Markdown elements - Markdown elements - HtmlCommentBlock: 3
<!-- test -->
.
<!-- test -->
.
FILE[0, 14] chars:[0, 14, "<!--  …  -->\n"]
  HTML_BLOCK[0, 14] chars:[0, 14, "<!--  …  -->\n"]
    Leaf:COMMENT[0, 13] chars:[0, 13, "<!--  … t -->"]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


### Markdown elements - HtmlEntity

`HtmlEntity`

named

```````````````````````````````` example Markdown elements - Markdown elements - HtmlEntity: 1
&nbsp;
.
<p>&nbsp;</p>
.
FILE[0, 7] chars:[0, 7, "&nbsp;\n"]
  PARAGRAPH_BLOCK[0, 7] chars:[0, 7, "&nbsp;\n"]
    TEXT_BLOCK[0, 7] chars:[0, 7, "&nbsp;\n"]
      Leaf:HTML_ENTITY[0, 6] chars:[0, 6, "&nbsp;"]
      Leaf:EOL[6, 7] chars:[6, 7, "\n"]
````````````````````````````````


numbered

```````````````````````````````` example Markdown elements - Markdown elements - HtmlEntity: 2
&#10;
.
<p>&#10;</p>
.
FILE[0, 6] chars:[0, 6, "&#10;\n"]
  PARAGRAPH_BLOCK[0, 6] chars:[0, 6, "&#10;\n"]
    TEXT_BLOCK[0, 6] chars:[0, 6, "&#10;\n"]
      Leaf:HTML_ENTITY[0, 5] chars:[0, 5, "&#10;"]
      Leaf:EOL[5, 6] chars:[5, 6, "\n"]
````````````````````````````````


named embedded

```````````````````````````````` example Markdown elements - Markdown elements - HtmlEntity: 3
text with &nbsp; embedded
.
<p>text with &nbsp; embedded</p>
.
FILE[0, 26] chars:[0, 26, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 26] chars:[0, 26, "text  … dded\n"]
    TEXT_BLOCK[0, 26] chars:[0, 26, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:HTML_ENTITY[10, 16] chars:[10, 16, "&nbsp;"]
      Leaf:TEXT[16, 25] chars:[16, 25, " embedded"]
      Leaf:EOL[25, 26] chars:[25, 26, "\n"]
````````````````````````````````


numbered embedded

```````````````````````````````` example Markdown elements - Markdown elements - HtmlEntity: 4
text with &#10; embedded
.
<p>text with &#10; embedded</p>
.
FILE[0, 25] chars:[0, 25, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 25] chars:[0, 25, "text  … dded\n"]
    TEXT_BLOCK[0, 25] chars:[0, 25, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:HTML_ENTITY[10, 15] chars:[10, 15, "&#10;"]
      Leaf:TEXT[15, 24] chars:[15, 24, " embedded"]
      Leaf:EOL[24, 25] chars:[24, 25, "\n"]
````````````````````````````````


named embedded, no whitespace

```````````````````````````````` example Markdown elements - Markdown elements - HtmlEntity: 5
text with&nbsp;embedded
.
<p>text with&nbsp;embedded</p>
.
FILE[0, 24] chars:[0, 24, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 24] chars:[0, 24, "text  … dded\n"]
    TEXT_BLOCK[0, 24] chars:[0, 24, "text  … dded\n"]
      Leaf:TEXT[0, 9] chars:[0, 9, "text with"]
      Leaf:HTML_ENTITY[9, 15] chars:[9, 15, "&nbsp;"]
      Leaf:TEXT[15, 23] chars:[15, 23, "embedded"]
      Leaf:EOL[23, 24] chars:[23, 24, "\n"]
````````````````````````````````


numbered embedded, no whitespace

```````````````````````````````` example Markdown elements - Markdown elements - HtmlEntity: 6
text with&#10;embedded
.
<p>text with&#10;embedded</p>
.
FILE[0, 23] chars:[0, 23, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 23] chars:[0, 23, "text  … dded\n"]
    TEXT_BLOCK[0, 23] chars:[0, 23, "text  … dded\n"]
      Leaf:TEXT[0, 9] chars:[0, 9, "text with"]
      Leaf:HTML_ENTITY[9, 14] chars:[9, 14, "&#10;"]
      Leaf:TEXT[14, 22] chars:[14, 22, "embedded"]
      Leaf:EOL[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


### Markdown elements - HtmlInline

`HtmlInline`

empty

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlInline: 1) options(pegdown-fail)
<span></span>
.
<span></span>
.
FILE[0, 13] chars:[0, 13, "<span … span>"]
  PARAGRAPH_BLOCK[0, 13] chars:[0, 13, "<span … span>"]
    TEXT_BLOCK[0, 13] chars:[0, 13, "<span … span>"]
      Leaf:INLINE_HTML[0, 13] chars:[0, 13, "<span … span>"]
````````````````````````````````


non-empty

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlInline: 2) options(pegdown-fail)
<span>span</span>
.
<span>span</span>
.
FILE[0, 17] chars:[0, 17, "<span … span>"]
  PARAGRAPH_BLOCK[0, 17] chars:[0, 17, "<span … span>"]
    TEXT_BLOCK[0, 17] chars:[0, 17, "<span … span>"]
      Leaf:INLINE_HTML[0, 6] chars:[0, 6, "<span>"]
      Leaf:TEXT[6, 10] chars:[6, 10, "span"]
      Leaf:INLINE_HTML[10, 17] chars:[10, 17, "</span>"]
````````````````````````````````


empty leading

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInline: 3
<span></span> embedded
.
<p><span></span> embedded</p>
.
FILE[0, 23] chars:[0, 23, "<span … dded\n"]
  PARAGRAPH_BLOCK[0, 23] chars:[0, 23, "<span … dded\n"]
    TEXT_BLOCK[0, 23] chars:[0, 23, "<span … dded\n"]
      Leaf:INLINE_HTML[0, 13] chars:[0, 13, "<span … span>"]
      Leaf:TEXT[13, 22] chars:[13, 22, " embedded"]
      Leaf:EOL[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


non-empty leading

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInline: 4
<span>span</span> embedded
.
<p><span>span</span> embedded</p>
.
FILE[0, 27] chars:[0, 27, "<span … dded\n"]
  PARAGRAPH_BLOCK[0, 27] chars:[0, 27, "<span … dded\n"]
    TEXT_BLOCK[0, 27] chars:[0, 27, "<span … dded\n"]
      Leaf:INLINE_HTML[0, 6] chars:[0, 6, "<span>"]
      Leaf:TEXT[6, 10] chars:[6, 10, "span"]
      Leaf:INLINE_HTML[10, 17] chars:[10, 17, "</span>"]
      Leaf:TEXT[17, 26] chars:[17, 26, " embedded"]
      Leaf:EOL[26, 27] chars:[26, 27, "\n"]
````````````````````````````````


empty embedded

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInline: 5
text with <span></span> embedded
.
<p>text with <span></span> embedded</p>
.
FILE[0, 33] chars:[0, 33, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 33] chars:[0, 33, "text  … dded\n"]
    TEXT_BLOCK[0, 33] chars:[0, 33, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:INLINE_HTML[10, 23] chars:[10, 23, "<span … span>"]
      Leaf:TEXT[23, 32] chars:[23, 32, " embedded"]
      Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


non-empty embedded

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInline: 6
text with <span>span</span> embedded
.
<p>text with <span>span</span> embedded</p>
.
FILE[0, 37] chars:[0, 37, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 37] chars:[0, 37, "text  … dded\n"]
    TEXT_BLOCK[0, 37] chars:[0, 37, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:INLINE_HTML[10, 16] chars:[10, 16, "<span>"]
      Leaf:TEXT[16, 20] chars:[16, 20, "span"]
      Leaf:INLINE_HTML[20, 27] chars:[20, 27, "</span>"]
      Leaf:TEXT[27, 36] chars:[27, 36, " embedded"]
      Leaf:EOL[36, 37] chars:[36, 37, "\n"]
````````````````````````````````


empty trailing

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInline: 7
text with <span></span>
.
<p>text with <span></span></p>
.
FILE[0, 24] chars:[0, 24, "text  … pan>\n"]
  PARAGRAPH_BLOCK[0, 24] chars:[0, 24, "text  … pan>\n"]
    TEXT_BLOCK[0, 24] chars:[0, 24, "text  … pan>\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:INLINE_HTML[10, 23] chars:[10, 23, "<span … span>"]
      Leaf:EOL[23, 24] chars:[23, 24, "\n"]
````````````````````````````````


non-empty trailing

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInline: 8
text with <span>span</span>
.
<p>text with <span>span</span></p>
.
FILE[0, 28] chars:[0, 28, "text  … pan>\n"]
  PARAGRAPH_BLOCK[0, 28] chars:[0, 28, "text  … pan>\n"]
    TEXT_BLOCK[0, 28] chars:[0, 28, "text  … pan>\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:INLINE_HTML[10, 16] chars:[10, 16, "<span>"]
      Leaf:TEXT[16, 20] chars:[16, 20, "span"]
      Leaf:INLINE_HTML[20, 27] chars:[20, 27, "</span>"]
      Leaf:EOL[27, 28] chars:[27, 28, "\n"]
````````````````````````````````


### Markdown elements - HtmlInlineComment

`HtmlInlineComment`

Plain text with empty HTML comment

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInlineComment: 1
First line
Second line <!---->
Last line
.
<p>First line Second line <!----> Last line</p>
.
FILE[0, 41] chars:[0, 41, "First … line\n"]
  PARAGRAPH_BLOCK[0, 41] chars:[0, 41, "First … line\n"]
    TEXT_BLOCK[0, 41] chars:[0, 41, "First … line\n"]
      Leaf:TEXT[0, 23] chars:[0, 23, "First … line "]
      Leaf:COMMENT[23, 30] chars:[23, 30, "<!---->"]
      Leaf:TEXT[30, 40] chars:[30, 40, "\nLast line"]
      Leaf:EOL[40, 41] chars:[40, 41, "\n"]
````````````````````````````````


Html Inline with comment

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInlineComment: 2
text <div><!-- HTML Comment --></div> some more text
.
<p>text <div><!-- HTML Comment --></div> some more text</p>
.
FILE[0, 53] chars:[0, 53, "text  … text\n"]
  PARAGRAPH_BLOCK[0, 53] chars:[0, 53, "text  … text\n"]
    TEXT_BLOCK[0, 53] chars:[0, 53, "text  … text\n"]
      Leaf:TEXT[0, 5] chars:[0, 5, "text "]
      Leaf:INLINE_HTML[5, 10] chars:[5, 10, "<div>"]
      Leaf:COMMENT[10, 31] chars:[10, 31, "<!--  … t -->"]
      Leaf:INLINE_HTML[31, 37] chars:[31, 37, "</div>"]
      Leaf:TEXT[37, 52] chars:[37, 52, " some …  text"]
      Leaf:EOL[52, 53] chars:[52, 53, "\n"]
````````````````````````````````


Plain text with simple HTML comment

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInlineComment: 3
First line
Second line <!--simple-->
Last line
.
<p>First line Second line <!--simple--> Last line</p>
.
FILE[0, 47] chars:[0, 47, "First … line\n"]
  PARAGRAPH_BLOCK[0, 47] chars:[0, 47, "First … line\n"]
    TEXT_BLOCK[0, 47] chars:[0, 47, "First … line\n"]
      Leaf:TEXT[0, 23] chars:[0, 23, "First … line "]
      Leaf:COMMENT[23, 36] chars:[23, 36, "<!--s … le-->"]
      Leaf:TEXT[36, 46] chars:[36, 46, "\nLast line"]
      Leaf:EOL[46, 47] chars:[46, 47, "\n"]
````````````````````````````````


Plain text with unterminated HTML comment

```````````````````````````````` example(Markdown elements - Markdown elements - HtmlInlineComment: 4) options(no-smarts)
First line
Second line <!--simple
Last line
.
<p>First line Second line &lt;!--simple Last line</p>
.
FILE[0, 43] chars:[0, 43, "First …  line"]
  PARAGRAPH_BLOCK[0, 43] chars:[0, 43, "First …  line"]
    TEXT_BLOCK[0, 43] chars:[0, 43, "First …  line"]
      Leaf:TEXT[0, 43] chars:[0, 43, "First …  line"]
````````````````````````````````


Plain text with HTML comment with embedded looking code

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInlineComment: 5
First line
Second line <!--`code`-->
Last line
.
<p>First line Second line <!--`code`--> Last line</p>
.
FILE[0, 47] chars:[0, 47, "First … line\n"]
  PARAGRAPH_BLOCK[0, 47] chars:[0, 47, "First … line\n"]
    TEXT_BLOCK[0, 47] chars:[0, 47, "First … line\n"]
      Leaf:TEXT[0, 23] chars:[0, 23, "First … line "]
      Leaf:COMMENT[23, 36] chars:[23, 36, "<!--` … e`-->"]
      Leaf:TEXT[36, 46] chars:[36, 46, "\nLast line"]
      Leaf:EOL[46, 47] chars:[46, 47, "\n"]
````````````````````````````````


### Markdown elements - HtmlInnerBlock

`HtmlInnerBlock` `HtmlInnerBlockComment`

Html Blocks

```````````````````````````````` example Markdown elements - Markdown elements - HtmlInnerBlock: 1
<div>
    <!-- HTML Comment -->
</div>
.
<div>
    <!-- HTML Comment -->
</div>
.
FILE[0, 39] chars:[0, 39, "<div> … div>\n"]
  HTML_BLOCK[0, 39] chars:[0, 39, "<div> … div>\n"]
    Leaf:HTML_BLOCK[0, 10] chars:[0, 10, "<div>\n    "]
    Leaf:COMMENT[10, 31] chars:[10, 31, "<!--  … t -->"]
    Leaf:HTML_BLOCK[31, 38] chars:[31, 38, "\n</div>"]
    Leaf:EOL[38, 39] chars:[38, 39, "\n"]
````````````````````````````````


### Markdown elements - Image

`Image`

plain

```````````````````````````````` example Markdown elements - Markdown elements - Image: 1
![alt](/url) 
.
<p><img src="/url" alt="alt" /> </p>
.
FILE[0, 14] chars:[0, 14, "![alt … rl) \n"]
  PARAGRAPH_BLOCK[0, 14] chars:[0, 14, "![alt … rl) \n"]
    TEXT_BLOCK[0, 14] chars:[0, 14, "![alt … rl) \n"]
      IMAGE[0, 12] chars:[0, 12, "![alt … /url)"]
        Leaf:IMAGE_LINK_REF_TEXT_OPEN[0, 2] chars:[0, 2, "!["]
        IMAGE_LINK_TEXT[2, 5] chars:[2, 5, "alt"]
          Leaf:TEXT[2, 5] chars:[2, 5, "alt"]
        Leaf:IMAGE_LINK_REF_TEXT_CLOSE[5, 6] chars:[5, 6, "]"]
        Leaf:IMAGE_LINK_REF_OPEN[6, 7] chars:[6, 7, "("]
        IMAGE_LINK_REF[7, 11] chars:[7, 11, "/url"]
          Leaf:IMAGE_LINK_REF[7, 11] chars:[7, 11, "/url"]
        Leaf:IMAGE_LINK_REF_CLOSE[11, 12] chars:[11, 12, ")"]
      Leaf:TEXT[12, 13] chars:[12, 13, " "]
      Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


embedded

```````````````````````````````` example Markdown elements - Markdown elements - Image: 2
text with ![alt](/url) embedded 
.
<p>text with <img src="/url" alt="alt" /> embedded </p>
.
FILE[0, 33] chars:[0, 33, "text  … ded \n"]
  PARAGRAPH_BLOCK[0, 33] chars:[0, 33, "text  … ded \n"]
    TEXT_BLOCK[0, 33] chars:[0, 33, "text  … ded \n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      IMAGE[10, 22] chars:[10, 22, "![alt … /url)"]
        Leaf:IMAGE_LINK_REF_TEXT_OPEN[10, 12] chars:[10, 12, "!["]
        IMAGE_LINK_TEXT[12, 15] chars:[12, 15, "alt"]
          Leaf:TEXT[12, 15] chars:[12, 15, "alt"]
        Leaf:IMAGE_LINK_REF_TEXT_CLOSE[15, 16] chars:[15, 16, "]"]
        Leaf:IMAGE_LINK_REF_OPEN[16, 17] chars:[16, 17, "("]
        IMAGE_LINK_REF[17, 21] chars:[17, 21, "/url"]
          Leaf:IMAGE_LINK_REF[17, 21] chars:[17, 21, "/url"]
        Leaf:IMAGE_LINK_REF_CLOSE[21, 22] chars:[21, 22, ")"]
      Leaf:TEXT[22, 32] chars:[22, 32, " embedded "]
      Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


multi-line

```````````````````````````````` example Markdown elements - Markdown elements - Image: 3
text with ![alt](/url?
multi-line
content
)
embedded.
.
<p>text with <img src="/url?%0Amulti-line%0Acontent" alt="alt" /> embedded.</p>
.
FILE[0, 54] chars:[0, 54, "text  … ded.\n"]
  PARAGRAPH_BLOCK[0, 54] chars:[0, 54, "text  … ded.\n"]
    TEXT_BLOCK[0, 54] chars:[0, 54, "text  … ded.\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      IMAGE[10, 43] chars:[10, 43, "![alt … ent\n)"]
        Leaf:IMAGE_LINK_REF_TEXT_OPEN[10, 12] chars:[10, 12, "!["]
        IMAGE_LINK_TEXT[12, 15] chars:[12, 15, "alt"]
          Leaf:TEXT[12, 15] chars:[12, 15, "alt"]
        Leaf:IMAGE_LINK_REF_TEXT_CLOSE[15, 16] chars:[15, 16, "]"]
        Leaf:IMAGE_LINK_REF_OPEN[16, 17] chars:[16, 17, "("]
        IMAGE_LINK_REF[17, 23] chars:[17, 23, "/url?\n"]
          Leaf:IMAGE_LINK_REF[17, 23] chars:[17, 23, "/url?\n"]
        IMAGE_URL_CONTENT[23, 41] chars:[23, 41, "multi … ntent"]
          Leaf:IMAGE_URL_CONTENT[23, 41] chars:[23, 41, "multi … ntent"]
        Leaf:EOL[41, 42] chars:[41, 42, "\n"]
        Leaf:IMAGE_LINK_REF_CLOSE[42, 43] chars:[42, 43, ")"]
      Leaf:TEXT[43, 53] chars:[43, 53, "\nembedded."]
      Leaf:EOL[53, 54] chars:[53, 54, "\n"]
````````````````````````````````


multi-line with escape chars

```````````````````````````````` example Markdown elements - Markdown elements - Image: 4
text with ![alt](/url?
multi-line \\
content
)
embedded.
.
<p>text with <img src="/url?%0Amulti-line%20%5C%5C%0Acontent" alt="alt" /> embedded.</p>
.
FILE[0, 57] chars:[0, 57, "text  … ded.\n"]
  PARAGRAPH_BLOCK[0, 57] chars:[0, 57, "text  … ded.\n"]
    TEXT_BLOCK[0, 57] chars:[0, 57, "text  … ded.\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      IMAGE[10, 46] chars:[10, 46, "![alt … ent\n)"]
        Leaf:IMAGE_LINK_REF_TEXT_OPEN[10, 12] chars:[10, 12, "!["]
        IMAGE_LINK_TEXT[12, 15] chars:[12, 15, "alt"]
          Leaf:TEXT[12, 15] chars:[12, 15, "alt"]
        Leaf:IMAGE_LINK_REF_TEXT_CLOSE[15, 16] chars:[15, 16, "]"]
        Leaf:IMAGE_LINK_REF_OPEN[16, 17] chars:[16, 17, "("]
        IMAGE_LINK_REF[17, 23] chars:[17, 23, "/url?\n"]
          Leaf:IMAGE_LINK_REF[17, 23] chars:[17, 23, "/url?\n"]
        IMAGE_URL_CONTENT[23, 44] chars:[23, 44, "multi … ntent"]
          Leaf:IMAGE_URL_CONTENT[23, 44] chars:[23, 44, "multi … ntent"]
        Leaf:EOL[44, 45] chars:[44, 45, "\n"]
        Leaf:IMAGE_LINK_REF_CLOSE[45, 46] chars:[45, 46, ")"]
      Leaf:TEXT[46, 56] chars:[46, 56, "\nembedded."]
      Leaf:EOL[56, 57] chars:[56, 57, "\n"]
````````````````````````````````


### Markdown elements - ImageRef

`ImageRef`

basic

```````````````````````````````` example Markdown elements - Markdown elements - ImageRef: 1
[ref]: /url

![ref]
.
<p><img src="/url" alt="ref" /></p>
.
FILE[0, 20] chars:[0, 20, "[ref] … ref]\n"]
  REFERENCE[0, 12] chars:[0, 12, "[ref] … /url\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 4] chars:[1, 4, "ref"]
      Leaf:TEXT[1, 4] chars:[1, 4, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    REFERENCE_LINK_REF[7, 11] chars:[7, 11, "/url"]
      Leaf:REFERENCE_LINK_REF[7, 11] chars:[7, 11, "/url"]
    Leaf:EOL[11, 12] chars:[11, 12, "\n"]
  BLANK_LINE[12, 13] chars:[12, 13, "\n"]
    Leaf:BLANK_LINE[12, 13] chars:[12, 13, "\n"]
  PARAGRAPH_BLOCK[13, 20] chars:[13, 20, "![ref]\n"]
    TEXT_BLOCK[13, 20] chars:[13, 20, "![ref]\n"]
      REFERENCE_IMAGE[13, 19] chars:[13, 19, "![ref]"]
        Leaf:REFERENCE_IMAGE_REFERENCE_OPEN2[13, 15] chars:[13, 15, "!["]
        REFERENCE_IMAGE_REFERENCE[15, 18] chars:[15, 18, "ref"]
          Leaf:TEXT[15, 18] chars:[15, 18, "ref"]
        Leaf:REFERENCE_IMAGE_REFERENCE_CLOSE2[18, 19] chars:[18, 19, "]"]
      Leaf:EOL[19, 20] chars:[19, 20, "\n"]
````````````````````````````````


undefined

```````````````````````````````` example Markdown elements - Markdown elements - ImageRef: 2
[ref2]: /url2

![ref]
.
<p>![ref]</p>
.
FILE[0, 22] chars:[0, 22, "[ref2 … ref]\n"]
  REFERENCE[0, 14] chars:[0, 14, "[ref2 … url2\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 5] chars:[1, 5, "ref2"]
      Leaf:TEXT[1, 5] chars:[1, 5, "ref2"]
    Leaf:REFERENCE_TEXT_CLOSE[5, 7] chars:[5, 7, "]:"]
    Leaf:WHITESPACE[7, 8] chars:[7, 8, " "]
    REFERENCE_LINK_REF[8, 13] chars:[8, 13, "/url2"]
      Leaf:REFERENCE_LINK_REF[8, 13] chars:[8, 13, "/url2"]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
  BLANK_LINE[14, 15] chars:[14, 15, "\n"]
    Leaf:BLANK_LINE[14, 15] chars:[14, 15, "\n"]
  PARAGRAPH_BLOCK[15, 22] chars:[15, 22, "![ref]\n"]
    TEXT_BLOCK[15, 22] chars:[15, 22, "![ref]\n"]
      REFERENCE_IMAGE[15, 21] chars:[15, 21, "![ref]"]
        Leaf:REFERENCE_IMAGE_REFERENCE_OPEN2[15, 17] chars:[15, 17, "!["]
        REFERENCE_IMAGE_REFERENCE[17, 20] chars:[17, 20, "ref"]
          Leaf:TEXT[17, 20] chars:[17, 20, "ref"]
        Leaf:REFERENCE_IMAGE_REFERENCE_CLOSE2[20, 21] chars:[20, 21, "]"]
      Leaf:EOL[21, 22] chars:[21, 22, "\n"]
````````````````````````````````


duplicate

```````````````````````````````` example Markdown elements - Markdown elements - ImageRef: 3
[ref]: /url1
[ref]: /url2

![ref]
.
<p><img src="/url2" alt="ref" /></p>
.
FILE[0, 34] chars:[0, 34, "[ref] … ref]\n"]
  REFERENCE[0, 13] chars:[0, 13, "[ref] … url1\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 4] chars:[1, 4, "ref"]
      Leaf:TEXT[1, 4] chars:[1, 4, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
      Leaf:REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
  REFERENCE[13, 26] chars:[13, 26, "[ref] … url2\n"]
    Leaf:REFERENCE_TEXT_OPEN[13, 14] chars:[13, 14, "["]
    REFERENCE_TEXT[14, 17] chars:[14, 17, "ref"]
      Leaf:TEXT[14, 17] chars:[14, 17, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[17, 19] chars:[17, 19, "]:"]
    Leaf:WHITESPACE[19, 20] chars:[19, 20, " "]
    REFERENCE_LINK_REF[20, 25] chars:[20, 25, "/url2"]
      Leaf:REFERENCE_LINK_REF[20, 25] chars:[20, 25, "/url2"]
    Leaf:EOL[25, 26] chars:[25, 26, "\n"]
  BLANK_LINE[26, 27] chars:[26, 27, "\n"]
    Leaf:BLANK_LINE[26, 27] chars:[26, 27, "\n"]
  PARAGRAPH_BLOCK[27, 34] chars:[27, 34, "![ref]\n"]
    TEXT_BLOCK[27, 34] chars:[27, 34, "![ref]\n"]
      REFERENCE_IMAGE[27, 33] chars:[27, 33, "![ref]"]
        Leaf:REFERENCE_IMAGE_REFERENCE_OPEN2[27, 29] chars:[27, 29, "!["]
        REFERENCE_IMAGE_REFERENCE[29, 32] chars:[29, 32, "ref"]
          Leaf:TEXT[29, 32] chars:[29, 32, "ref"]
        Leaf:REFERENCE_IMAGE_REFERENCE_CLOSE2[32, 33] chars:[32, 33, "]"]
      Leaf:EOL[33, 34] chars:[33, 34, "\n"]
````````````````````````````````


dummy ref

```````````````````````````````` example Markdown elements - Markdown elements - ImageRef: 4
[ref]: /url1

![ref][]
.
<p><img src="/url1" alt="ref" /></p>
.
FILE[0, 23] chars:[0, 23, "[ref] … f][]\n"]
  REFERENCE[0, 13] chars:[0, 13, "[ref] … url1\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 4] chars:[1, 4, "ref"]
      Leaf:TEXT[1, 4] chars:[1, 4, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
      Leaf:REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
  BLANK_LINE[13, 14] chars:[13, 14, "\n"]
    Leaf:BLANK_LINE[13, 14] chars:[13, 14, "\n"]
  PARAGRAPH_BLOCK[14, 23] chars:[14, 23, "![ref][]\n"]
    TEXT_BLOCK[14, 23] chars:[14, 23, "![ref][]\n"]
      REFERENCE_IMAGE[14, 22] chars:[14, 22, "![ref][]"]
        Leaf:REFERENCE_IMAGE_REFERENCE_OPEN2[14, 16] chars:[14, 16, "!["]
        REFERENCE_IMAGE_REFERENCE[16, 19] chars:[16, 19, "ref"]
          Leaf:TEXT[16, 19] chars:[16, 19, "ref"]
        Leaf:REFERENCE_IMAGE_REFERENCE_CLOSE2[19, 20] chars:[19, 20, "]"]
        Leaf:DUMMY_REFERENCE[20, 22] chars:[20, 22, "[]"]
      Leaf:EOL[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


### Markdown elements - IndentedCodeBlock

`IndentedCodeBlock`

basic

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 1
    code
.
<pre><code>code
</code></pre>
.
FILE[0, 9] chars:[0, 9, "    code\n"]
  VERBATIM[0, 9] chars:[0, 9, "    code\n"]
    VERBATIM_CONTENT[0, 9] chars:[0, 9, "    code\n"]
      Leaf:VERBATIM_CONTENT[0, 9] chars:[0, 9, "    code\n"]
````````````````````````````````


multi line

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 2
    code
        more code
.
<pre><code>code
    more code
</code></pre>
.
FILE[0, 27] chars:[0, 27, "    c … code\n"]
  VERBATIM[0, 27] chars:[0, 27, "    c … code\n"]
    VERBATIM_CONTENT[0, 27] chars:[0, 27, "    c … code\n"]
      Leaf:VERBATIM_CONTENT[0, 27] chars:[0, 27, "    c … code\n"]
````````````````````````````````


multi line, blanks

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 3
    code
    
        more code
.
<pre><code>code

    more code
</code></pre>
.
FILE[0, 32] chars:[0, 32, "    c … code\n"]
  VERBATIM[0, 32] chars:[0, 32, "    c … code\n"]
    VERBATIM_CONTENT[0, 32] chars:[0, 32, "    c … code\n"]
      Leaf:VERBATIM_CONTENT[0, 32] chars:[0, 32, "    c … code\n"]
````````````````````````````````


tabbed

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 4
&#2192;code
.
<pre><code>code
</code></pre>
.
FILE[0, 6] chars:[0, 6, "\tcode\n"]
  VERBATIM[0, 6] chars:[0, 6, "\tcode\n"]
    VERBATIM_CONTENT[0, 6] chars:[0, 6, "\tcode\n"]
      Leaf:VERBATIM_CONTENT[0, 6] chars:[0, 6, "\tcode\n"]
````````````````````````````````


multi line

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 5
&#2192;code
&#2192;&#2192;more code
.
<pre><code>code
    more code
</code></pre>
.
FILE[0, 18] chars:[0, 18, "\u2192code … code\n"]
  VERBATIM[0, 18] chars:[0, 18, "\u2192code … code\n"]
    VERBATIM_CONTENT[0, 18] chars:[0, 18, "\u2192code … code\n"]
      Leaf:VERBATIM_CONTENT[0, 18] chars:[0, 18, "\u2192code … code\n"]
````````````````````````````````


multi line, blanks

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 6
&#2192;code

&#2192;&#2192;more code
.
<pre><code>code

    more code
</code></pre>
.
FILE[0, 19] chars:[0, 19, "\u2192code … code\n"]
  VERBATIM[0, 19] chars:[0, 19, "\u2192code … code\n"]
    VERBATIM_CONTENT[0, 19] chars:[0, 19, "\u2192code … code\n"]
      Leaf:VERBATIM_CONTENT[0, 19] chars:[0, 19, "\u2192code … code\n"]
````````````````````````````````


trailing blank lines

```````````````````````````````` example Markdown elements - Markdown elements - IndentedCodeBlock: 7
    code
    more code
    
    
.
<pre><code>code
more code
</code></pre>
.
FILE[0, 33] chars:[0, 33, "    c …     \n"]
  VERBATIM[0, 23] chars:[0, 23, "    c … code\n"]
    VERBATIM_CONTENT[0, 23] chars:[0, 23, "    c … code\n"]
      Leaf:VERBATIM_CONTENT[0, 23] chars:[0, 23, "    c … code\n"]
  BLANK_LINE[23, 28] chars:[23, 28, "    \n"]
    Leaf:BLANK_LINE[23, 28] chars:[23, 28, "    \n"]
  BLANK_LINE[28, 33] chars:[28, 33, "    \n"]
    Leaf:BLANK_LINE[28, 33] chars:[28, 33, "    \n"]
````````````````````````````````


### Markdown elements - JekyllFrontMatterBlock

`JekyllFrontMatterBlock` FlexmarkFrontMatter

Jekyll front matter

```````````````````````````````` example Markdown elements - Markdown elements - JekyllFrontMatterBlock: 1
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
---

.

.
FILE[0, 150] chars:[0, 150, "---\nt … ---\n\n"]
  JEKYLL_FRONT_MATTER_BLOCK_ELEM[0, 148] chars:[0, 148, "---\nt … '\n---"]
    Leaf:JEKYLL_FRONT_MATTER_OPEN[0, 3] chars:[0, 3, "---"]
    Leaf:EOL[3, 4] chars:[3, 4, "\n"]
    Leaf:JEKYLL_FRONT_MATTER_BLOCK[4, 145] chars:[4, 145, "title … 0/)'\n"]
    Leaf:JEKYLL_FRONT_MATTER_CLOSE[145, 148] chars:[145, 148, "---"]
  Leaf:EOL[148, 149] chars:[148, 149, "\n"]
  BLANK_LINE[149, 150] chars:[149, 150, "\n"]
    Leaf:BLANK_LINE[149, 150] chars:[149, 150, "\n"]
````````````````````````````````


flexmark front matter

```````````````````````````````` example Markdown elements - Markdown elements - JekyllFrontMatterBlock: 2
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

.
<hr/>
.
FILE[0, 155] chars:[0, 155, "---\nt … ---\n\n"]
  FLEXMARK_FRONT_MATTER_BLOCK_ELEM[0, 148] chars:[0, 148, "---\nt … '\n..."]
    Leaf:FLEXMARK_FRONT_MATTER_OPEN[0, 3] chars:[0, 3, "---"]
    Leaf:EOL[3, 4] chars:[3, 4, "\n"]
    Leaf:FLEXMARK_FRONT_MATTER_BLOCK[4, 145] chars:[4, 145, "title … 0/)'\n"]
    Leaf:FLEXMARK_FRONT_MATTER_CLOSE[145, 148] chars:[145, 148, "..."]
  Leaf:EOL[148, 149] chars:[148, 149, "\n"]
  BLANK_LINE[149, 150] chars:[149, 150, "\n"]
    Leaf:BLANK_LINE[149, 150] chars:[149, 150, "\n"]
  HRULE[150, 154] chars:[150, 154, "---\n"]
    Leaf:HRULE_TEXT[150, 154] chars:[150, 154, "---\n"]
  Leaf:EOL[154, 155] chars:[154, 155, "\n"]
````````````````````````````````


### Markdown elements - Link

`Link`

basic

```````````````````````````````` example Markdown elements - Markdown elements - Link: 1
[text](/url) 
.
<p><a href="/url">text</a> </p>
.
FILE[0, 14] chars:[0, 14, "[text … rl) \n"]
  PARAGRAPH_BLOCK[0, 14] chars:[0, 14, "[text … rl) \n"]
    TEXT_BLOCK[0, 14] chars:[0, 14, "[text … rl) \n"]
      EXPLICIT_LINK[0, 12] chars:[0, 12, "[text … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[0, 1] chars:[0, 1, "["]
        LINK_REF_TEXT[1, 5] chars:[1, 5, "text"]
          Leaf:TEXT[1, 5] chars:[1, 5, "text"]
        Leaf:LINK_REF_TEXT_CLOSE[5, 6] chars:[5, 6, "]"]
        Leaf:LINK_REF_OPEN[6, 7] chars:[6, 7, "("]
        LINK_REF[7, 11] chars:[7, 11, "/url"]
          Leaf:LINK_REF[7, 11] chars:[7, 11, "/url"]
        Leaf:LINK_REF_CLOSE[11, 12] chars:[11, 12, ")"]
      Leaf:TEXT[12, 13] chars:[12, 13, " "]
      Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


basic with intellij completion location

```````````````````````````````` example Markdown elements - Markdown elements - Link: 2
[text](/url&#23ae;) 
.
<p><a href="/url">text</a> </p>
.
FILE[0, 15] chars:[0, 15, "[text … l%1f) \n"]
  PARAGRAPH_BLOCK[0, 15] chars:[0, 15, "[text … l%1f) \n"]
    TEXT_BLOCK[0, 15] chars:[0, 15, "[text … l%1f) \n"]
      EXPLICIT_LINK[0, 13] chars:[0, 13, "[text … url%1f)"]
        Leaf:LINK_REF_TEXT_OPEN[0, 1] chars:[0, 1, "["]
        LINK_REF_TEXT[1, 5] chars:[1, 5, "text"]
          Leaf:TEXT[1, 5] chars:[1, 5, "text"]
        Leaf:LINK_REF_TEXT_CLOSE[5, 6] chars:[5, 6, "]"]
        Leaf:LINK_REF_OPEN[6, 7] chars:[6, 7, "("]
        LINK_REF[7, 12] chars:[7, 12, "/url%1f"]
          Leaf:LINK_REF[7, 12] chars:[7, 12, "/url%1f"]
        Leaf:LINK_REF_CLOSE[12, 13] chars:[12, 13, ")"]
      Leaf:TEXT[13, 14] chars:[13, 14, " "]
      Leaf:EOL[14, 15] chars:[14, 15, "\n"]
````````````````````````````````


emphasis

```````````````````````````````` example Markdown elements - Markdown elements - Link: 3
[**text**](/url) 
.
<p><a href="/url"><strong>text</strong></a> </p>
.
FILE[0, 18] chars:[0, 18, "[**te … rl) \n"]
  PARAGRAPH_BLOCK[0, 18] chars:[0, 18, "[**te … rl) \n"]
    TEXT_BLOCK[0, 18] chars:[0, 18, "[**te … rl) \n"]
      EXPLICIT_LINK[0, 16] chars:[0, 16, "[**te … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[0, 1] chars:[0, 1, "["]
        LINK_REF_TEXT[1, 9] chars:[1, 9, "**text**"]
          BOLD[1, 9] chars:[1, 9, "**text**"]
            Leaf:BOLD_MARKER[1, 3] chars:[1, 3, "**"]
            Leaf:BOLD_TEXT[3, 7] chars:[3, 7, "text"]
            Leaf:BOLD_MARKER[7, 9] chars:[7, 9, "**"]
        Leaf:LINK_REF_TEXT_CLOSE[9, 10] chars:[9, 10, "]"]
        Leaf:LINK_REF_OPEN[10, 11] chars:[10, 11, "("]
        LINK_REF[11, 15] chars:[11, 15, "/url"]
          Leaf:LINK_REF[11, 15] chars:[11, 15, "/url"]
        Leaf:LINK_REF_CLOSE[15, 16] chars:[15, 16, ")"]
      Leaf:TEXT[16, 17] chars:[16, 17, " "]
      Leaf:EOL[17, 18] chars:[17, 18, "\n"]
````````````````````````````````


code

```````````````````````````````` example Markdown elements - Markdown elements - Link: 4
[`text`](/url) 
.
<p><a href="/url"><code>text</code></a> </p>
.
FILE[0, 16] chars:[0, 16, "[`tex … rl) \n"]
  PARAGRAPH_BLOCK[0, 16] chars:[0, 16, "[`tex … rl) \n"]
    TEXT_BLOCK[0, 16] chars:[0, 16, "[`tex … rl) \n"]
      EXPLICIT_LINK[0, 14] chars:[0, 14, "[`tex … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[0, 1] chars:[0, 1, "["]
        LINK_REF_TEXT[1, 7] chars:[1, 7, "`text`"]
          CODE[1, 7] chars:[1, 7, "`text`"]
            Leaf:CODE_MARKER[1, 2] chars:[1, 2, "`"]
            Leaf:CODE_TEXT[2, 6] chars:[2, 6, "text"]
            Leaf:CODE_MARKER[6, 7] chars:[6, 7, "`"]
        Leaf:LINK_REF_TEXT_CLOSE[7, 8] chars:[7, 8, "]"]
        Leaf:LINK_REF_OPEN[8, 9] chars:[8, 9, "("]
        LINK_REF[9, 13] chars:[9, 13, "/url"]
          Leaf:LINK_REF[9, 13] chars:[9, 13, "/url"]
        Leaf:LINK_REF_CLOSE[13, 14] chars:[13, 14, ")"]
      Leaf:TEXT[14, 15] chars:[14, 15, " "]
      Leaf:EOL[15, 16] chars:[15, 16, "\n"]
````````````````````````````````


image

```````````````````````````````` example Markdown elements - Markdown elements - Link: 5
[![alt](/url2)](/url) 
.
<p><a href="/url"><img src="/url2" alt="alt" /></a> </p>
.
FILE[0, 23] chars:[0, 23, "[![al … rl) \n"]
  PARAGRAPH_BLOCK[0, 23] chars:[0, 23, "[![al … rl) \n"]
    TEXT_BLOCK[0, 23] chars:[0, 23, "[![al … rl) \n"]
      EXPLICIT_LINK[0, 21] chars:[0, 21, "[![al … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[0, 1] chars:[0, 1, "["]
        LINK_REF_TEXT[1, 14] chars:[1, 14, "![alt … url2)"]
          IMAGE[1, 14] chars:[1, 14, "![alt … url2)"]
            Leaf:IMAGE_LINK_REF_TEXT_OPEN[1, 3] chars:[1, 3, "!["]
            IMAGE_LINK_TEXT[3, 6] chars:[3, 6, "alt"]
              Leaf:TEXT[3, 6] chars:[3, 6, "alt"]
            Leaf:IMAGE_LINK_REF_TEXT_CLOSE[6, 7] chars:[6, 7, "]"]
            Leaf:IMAGE_LINK_REF_OPEN[7, 8] chars:[7, 8, "("]
            IMAGE_LINK_REF[8, 13] chars:[8, 13, "/url2"]
              Leaf:IMAGE_LINK_REF[8, 13] chars:[8, 13, "/url2"]
            Leaf:IMAGE_LINK_REF_CLOSE[13, 14] chars:[13, 14, ")"]
        Leaf:LINK_REF_TEXT_CLOSE[14, 15] chars:[14, 15, "]"]
        Leaf:LINK_REF_OPEN[15, 16] chars:[15, 16, "("]
        LINK_REF[16, 20] chars:[16, 20, "/url"]
          Leaf:LINK_REF[16, 20] chars:[16, 20, "/url"]
        Leaf:LINK_REF_CLOSE[20, 21] chars:[20, 21, ")"]
      Leaf:TEXT[21, 22] chars:[21, 22, " "]
      Leaf:EOL[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


basic embedded

```````````````````````````````` example Markdown elements - Markdown elements - Link: 6
text with [text](/url) embedded 
.
<p>text with <a href="/url">text</a> embedded </p>
.
FILE[0, 33] chars:[0, 33, "text  … ded \n"]
  PARAGRAPH_BLOCK[0, 33] chars:[0, 33, "text  … ded \n"]
    TEXT_BLOCK[0, 33] chars:[0, 33, "text  … ded \n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      EXPLICIT_LINK[10, 22] chars:[10, 22, "[text … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[10, 11] chars:[10, 11, "["]
        LINK_REF_TEXT[11, 15] chars:[11, 15, "text"]
          Leaf:TEXT[11, 15] chars:[11, 15, "text"]
        Leaf:LINK_REF_TEXT_CLOSE[15, 16] chars:[15, 16, "]"]
        Leaf:LINK_REF_OPEN[16, 17] chars:[16, 17, "("]
        LINK_REF[17, 21] chars:[17, 21, "/url"]
          Leaf:LINK_REF[17, 21] chars:[17, 21, "/url"]
        Leaf:LINK_REF_CLOSE[21, 22] chars:[21, 22, ")"]
      Leaf:TEXT[22, 32] chars:[22, 32, " embedded "]
      Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


header embedded

```````````````````````````````` example Markdown elements - Markdown elements - Link: 7
# Heading [text](/url) 
.
<h1 id="heading-text"><a href="#heading-text" name="heading-text"></a>Heading <a href="/url">text</a></h1>
.
FILE[0, 24] chars:[0, 24, "# Hea … rl) \n"]
  ATX_HEADER[0, 24] chars:[0, 24, "# Hea … rl) \n"]
    Leaf:HEADER_ATX_MARKER[0, 1] chars:[0, 1, "#"]
    Leaf:WHITESPACE[1, 2] chars:[1, 2, " "]
    HEADER_TEXT[2, 22] chars:[2, 22, "Headi … /url)"]
      Leaf:HEADER_TEXT[2, 10] chars:[2, 10, "Heading "]
      EXPLICIT_LINK[10, 22] chars:[10, 22, "[text … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[10, 11] chars:[10, 11, "["]
        LINK_REF_TEXT[11, 15] chars:[11, 15, "text"]
          Leaf:HEADER_TEXT[11, 15] chars:[11, 15, "text"]
        Leaf:LINK_REF_TEXT_CLOSE[15, 16] chars:[15, 16, "]"]
        Leaf:LINK_REF_OPEN[16, 17] chars:[16, 17, "("]
        LINK_REF[17, 21] chars:[17, 21, "/url"]
          Leaf:LINK_REF[17, 21] chars:[17, 21, "/url"]
        Leaf:LINK_REF_CLOSE[21, 22] chars:[21, 22, ")"]
    Leaf:WHITESPACE[22, 23] chars:[22, 23, " "]
    Leaf:EOL[23, 24] chars:[23, 24, "\n"]
````````````````````````````````


header embedded

```````````````````````````````` example Markdown elements - Markdown elements - Link: 8
Heading [text](/url) 
---
.
<h2 id="heading-text"><a href="#heading-text" name="heading-text"></a>Heading <a href="/url">text</a></h2>
.
FILE[0, 26] chars:[0, 26, "Headi … \n---\n"]
  SETEXT_HEADER[0, 26] chars:[0, 26, "Headi … \n---\n"]
    HEADER_TEXT[0, 20] chars:[0, 20, "Headi … /url)"]
      Leaf:HEADER_TEXT[0, 8] chars:[0, 8, "Heading "]
      EXPLICIT_LINK[8, 20] chars:[8, 20, "[text … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[8, 9] chars:[8, 9, "["]
        LINK_REF_TEXT[9, 13] chars:[9, 13, "text"]
          Leaf:HEADER_TEXT[9, 13] chars:[9, 13, "text"]
        Leaf:LINK_REF_TEXT_CLOSE[13, 14] chars:[13, 14, "]"]
        Leaf:LINK_REF_OPEN[14, 15] chars:[14, 15, "("]
        LINK_REF[15, 19] chars:[15, 19, "/url"]
          Leaf:LINK_REF[15, 19] chars:[15, 19, "/url"]
        Leaf:LINK_REF_CLOSE[19, 20] chars:[19, 20, ")"]
    Leaf:WHITESPACE[20, 21] chars:[20, 21, " "]
    Leaf:EOL[21, 22] chars:[21, 22, "\n"]
    Leaf:HEADER_SETEXT_MARKER[22, 25] chars:[22, 25, "---"]
    Leaf:EOL[25, 26] chars:[25, 26, "\n"]
````````````````````````````````


header image embedded

```````````````````````````````` example Markdown elements - Markdown elements - Link: 9
# Heading [![alt](/url2)](/url) 
.
<h1 id="heading-alt"><a href="#heading-alt" name="heading-alt"></a>Heading <a href="/url"><img src="/url2" alt="alt" /></a></h1>
.
FILE[0, 33] chars:[0, 33, "# Hea … rl) \n"]
  ATX_HEADER[0, 33] chars:[0, 33, "# Hea … rl) \n"]
    Leaf:HEADER_ATX_MARKER[0, 1] chars:[0, 1, "#"]
    Leaf:WHITESPACE[1, 2] chars:[1, 2, " "]
    HEADER_TEXT[2, 31] chars:[2, 31, "Headi … /url)"]
      Leaf:HEADER_TEXT[2, 10] chars:[2, 10, "Heading "]
      EXPLICIT_LINK[10, 31] chars:[10, 31, "[![al … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[10, 11] chars:[10, 11, "["]
        LINK_REF_TEXT[11, 24] chars:[11, 24, "![alt … url2)"]
          IMAGE[11, 24] chars:[11, 24, "![alt … url2)"]
            Leaf:IMAGE_LINK_REF_TEXT_OPEN[11, 13] chars:[11, 13, "!["]
            IMAGE_LINK_TEXT[13, 16] chars:[13, 16, "alt"]
              Leaf:HEADER_TEXT[13, 16] chars:[13, 16, "alt"]
            Leaf:IMAGE_LINK_REF_TEXT_CLOSE[16, 17] chars:[16, 17, "]"]
            Leaf:IMAGE_LINK_REF_OPEN[17, 18] chars:[17, 18, "("]
            IMAGE_LINK_REF[18, 23] chars:[18, 23, "/url2"]
              Leaf:IMAGE_LINK_REF[18, 23] chars:[18, 23, "/url2"]
            Leaf:IMAGE_LINK_REF_CLOSE[23, 24] chars:[23, 24, ")"]
        Leaf:LINK_REF_TEXT_CLOSE[24, 25] chars:[24, 25, "]"]
        Leaf:LINK_REF_OPEN[25, 26] chars:[25, 26, "("]
        LINK_REF[26, 30] chars:[26, 30, "/url"]
          Leaf:LINK_REF[26, 30] chars:[26, 30, "/url"]
        Leaf:LINK_REF_CLOSE[30, 31] chars:[30, 31, ")"]
    Leaf:WHITESPACE[31, 32] chars:[31, 32, " "]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


header image embedded

```````````````````````````````` example Markdown elements - Markdown elements - Link: 10
Heading [![alt](/url2)](/url) 
---
.
<h2 id="heading-alt"><a href="#heading-alt" name="heading-alt"></a>Heading <a href="/url"><img src="/url2" alt="alt" /></a></h2>
.
FILE[0, 35] chars:[0, 35, "Headi … \n---\n"]
  SETEXT_HEADER[0, 35] chars:[0, 35, "Headi … \n---\n"]
    HEADER_TEXT[0, 29] chars:[0, 29, "Headi … /url)"]
      Leaf:HEADER_TEXT[0, 8] chars:[0, 8, "Heading "]
      EXPLICIT_LINK[8, 29] chars:[8, 29, "[![al … /url)"]
        Leaf:LINK_REF_TEXT_OPEN[8, 9] chars:[8, 9, "["]
        LINK_REF_TEXT[9, 22] chars:[9, 22, "![alt … url2)"]
          IMAGE[9, 22] chars:[9, 22, "![alt … url2)"]
            Leaf:IMAGE_LINK_REF_TEXT_OPEN[9, 11] chars:[9, 11, "!["]
            IMAGE_LINK_TEXT[11, 14] chars:[11, 14, "alt"]
              Leaf:HEADER_TEXT[11, 14] chars:[11, 14, "alt"]
            Leaf:IMAGE_LINK_REF_TEXT_CLOSE[14, 15] chars:[14, 15, "]"]
            Leaf:IMAGE_LINK_REF_OPEN[15, 16] chars:[15, 16, "("]
            IMAGE_LINK_REF[16, 21] chars:[16, 21, "/url2"]
              Leaf:IMAGE_LINK_REF[16, 21] chars:[16, 21, "/url2"]
            Leaf:IMAGE_LINK_REF_CLOSE[21, 22] chars:[21, 22, ")"]
        Leaf:LINK_REF_TEXT_CLOSE[22, 23] chars:[22, 23, "]"]
        Leaf:LINK_REF_OPEN[23, 24] chars:[23, 24, "("]
        LINK_REF[24, 28] chars:[24, 28, "/url"]
          Leaf:LINK_REF[24, 28] chars:[24, 28, "/url"]
        Leaf:LINK_REF_CLOSE[28, 29] chars:[28, 29, ")"]
    Leaf:WHITESPACE[29, 30] chars:[29, 30, " "]
    Leaf:EOL[30, 31] chars:[30, 31, "\n"]
    Leaf:HEADER_SETEXT_MARKER[31, 34] chars:[31, 34, "---"]
    Leaf:EOL[34, 35] chars:[34, 35, "\n"]
````````````````````````````````


### Markdown elements - LinkRef

`LinkRef`

basic

```````````````````````````````` example Markdown elements - Markdown elements - LinkRef: 1
[ref]: /url

[ref]
.
<p><a href="/url">ref</a></p>
.
FILE[0, 19] chars:[0, 19, "[ref] … ref]\n"]
  REFERENCE[0, 12] chars:[0, 12, "[ref] … /url\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 4] chars:[1, 4, "ref"]
      Leaf:TEXT[1, 4] chars:[1, 4, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    REFERENCE_LINK_REF[7, 11] chars:[7, 11, "/url"]
      Leaf:REFERENCE_LINK_REF[7, 11] chars:[7, 11, "/url"]
    Leaf:EOL[11, 12] chars:[11, 12, "\n"]
  BLANK_LINE[12, 13] chars:[12, 13, "\n"]
    Leaf:BLANK_LINE[12, 13] chars:[12, 13, "\n"]
  PARAGRAPH_BLOCK[13, 19] chars:[13, 19, "[ref]\n"]
    TEXT_BLOCK[13, 19] chars:[13, 19, "[ref]\n"]
      REFERENCE_LINK[13, 18] chars:[13, 18, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[13, 14] chars:[13, 14, "["]
        REFERENCE_LINK_REFERENCE[14, 17] chars:[14, 17, "ref"]
          Leaf:TEXT[14, 17] chars:[14, 17, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[17, 18] chars:[17, 18, "]"]
      Leaf:EOL[18, 19] chars:[18, 19, "\n"]
````````````````````````````````


undefined

```````````````````````````````` example Markdown elements - Markdown elements - LinkRef: 2
[ref2]: /url2

[ref]
.
<p>[ref]</p>
.
FILE[0, 21] chars:[0, 21, "[ref2 … ref]\n"]
  REFERENCE[0, 14] chars:[0, 14, "[ref2 … url2\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 5] chars:[1, 5, "ref2"]
      Leaf:TEXT[1, 5] chars:[1, 5, "ref2"]
    Leaf:REFERENCE_TEXT_CLOSE[5, 7] chars:[5, 7, "]:"]
    Leaf:WHITESPACE[7, 8] chars:[7, 8, " "]
    REFERENCE_LINK_REF[8, 13] chars:[8, 13, "/url2"]
      Leaf:REFERENCE_LINK_REF[8, 13] chars:[8, 13, "/url2"]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
  BLANK_LINE[14, 15] chars:[14, 15, "\n"]
    Leaf:BLANK_LINE[14, 15] chars:[14, 15, "\n"]
  PARAGRAPH_BLOCK[15, 21] chars:[15, 21, "[ref]\n"]
    TEXT_BLOCK[15, 21] chars:[15, 21, "[ref]\n"]
      REFERENCE_LINK[15, 20] chars:[15, 20, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[15, 16] chars:[15, 16, "["]
        REFERENCE_LINK_REFERENCE[16, 19] chars:[16, 19, "ref"]
          Leaf:TEXT[16, 19] chars:[16, 19, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[19, 20] chars:[19, 20, "]"]
      Leaf:EOL[20, 21] chars:[20, 21, "\n"]
````````````````````````````````


duplicate

```````````````````````````````` example Markdown elements - Markdown elements - LinkRef: 3
[ref]: /url1
[ref]: /url2

[ref]
.
<p><a href="/url2">ref</a></p>
.
FILE[0, 33] chars:[0, 33, "[ref] … ref]\n"]
  REFERENCE[0, 13] chars:[0, 13, "[ref] … url1\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 4] chars:[1, 4, "ref"]
      Leaf:TEXT[1, 4] chars:[1, 4, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
      Leaf:REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
  REFERENCE[13, 26] chars:[13, 26, "[ref] … url2\n"]
    Leaf:REFERENCE_TEXT_OPEN[13, 14] chars:[13, 14, "["]
    REFERENCE_TEXT[14, 17] chars:[14, 17, "ref"]
      Leaf:TEXT[14, 17] chars:[14, 17, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[17, 19] chars:[17, 19, "]:"]
    Leaf:WHITESPACE[19, 20] chars:[19, 20, " "]
    REFERENCE_LINK_REF[20, 25] chars:[20, 25, "/url2"]
      Leaf:REFERENCE_LINK_REF[20, 25] chars:[20, 25, "/url2"]
    Leaf:EOL[25, 26] chars:[25, 26, "\n"]
  BLANK_LINE[26, 27] chars:[26, 27, "\n"]
    Leaf:BLANK_LINE[26, 27] chars:[26, 27, "\n"]
  PARAGRAPH_BLOCK[27, 33] chars:[27, 33, "[ref]\n"]
    TEXT_BLOCK[27, 33] chars:[27, 33, "[ref]\n"]
      REFERENCE_LINK[27, 32] chars:[27, 32, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[27, 28] chars:[27, 28, "["]
        REFERENCE_LINK_REFERENCE[28, 31] chars:[28, 31, "ref"]
          Leaf:TEXT[28, 31] chars:[28, 31, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[31, 32] chars:[31, 32, "]"]
      Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


dummy ref

```````````````````````````````` example Markdown elements - Markdown elements - LinkRef: 4
[ref]: /url1

[ref][]
.
<p><a href="/url1">ref</a></p>
.
FILE[0, 22] chars:[0, 22, "[ref] … f][]\n"]
  REFERENCE[0, 13] chars:[0, 13, "[ref] … url1\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 4] chars:[1, 4, "ref"]
      Leaf:TEXT[1, 4] chars:[1, 4, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:WHITESPACE[6, 7] chars:[6, 7, " "]
    REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
      Leaf:REFERENCE_LINK_REF[7, 12] chars:[7, 12, "/url1"]
    Leaf:EOL[12, 13] chars:[12, 13, "\n"]
  BLANK_LINE[13, 14] chars:[13, 14, "\n"]
    Leaf:BLANK_LINE[13, 14] chars:[13, 14, "\n"]
  PARAGRAPH_BLOCK[14, 22] chars:[14, 22, "[ref][]\n"]
    TEXT_BLOCK[14, 22] chars:[14, 22, "[ref][]\n"]
      REFERENCE_LINK[14, 21] chars:[14, 21, "[ref][]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[14, 15] chars:[14, 15, "["]
        REFERENCE_LINK_REFERENCE[15, 18] chars:[15, 18, "ref"]
          Leaf:TEXT[15, 18] chars:[15, 18, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[18, 19] chars:[18, 19, "]"]
        Leaf:DUMMY_REFERENCE[19, 21] chars:[19, 21, "[]"]
      Leaf:EOL[21, 22] chars:[21, 22, "\n"]
````````````````````````````````


### Markdown elements - MailLink

`MailLink`

basic

```````````````````````````````` example Markdown elements - Markdown elements - MailLink: 1
name@url.dom
.
<p><a href="mailto:name@url.dom">name@url.dom</a></p>
.
FILE[0, 13] chars:[0, 13, "name@ … .dom\n"]
  PARAGRAPH_BLOCK[0, 13] chars:[0, 13, "name@ … .dom\n"]
    TEXT_BLOCK[0, 13] chars:[0, 13, "name@ … .dom\n"]
      Leaf:MAIL_LINK[0, 12] chars:[0, 12, "name@ … l.dom"]
      Leaf:EOL[12, 13] chars:[12, 13, "\n"]
````````````````````````````````


basic leading

```````````````````````````````` example Markdown elements - Markdown elements - MailLink: 2
name@url.dom embedded
.
<p><a href="mailto:name@url.dom">name@url.dom</a> embedded</p>
.
FILE[0, 22] chars:[0, 22, "name@ … dded\n"]
  PARAGRAPH_BLOCK[0, 22] chars:[0, 22, "name@ … dded\n"]
    TEXT_BLOCK[0, 22] chars:[0, 22, "name@ … dded\n"]
      Leaf:MAIL_LINK[0, 12] chars:[0, 12, "name@ … l.dom"]
      Leaf:TEXT[12, 21] chars:[12, 21, " embedded"]
      Leaf:EOL[21, 22] chars:[21, 22, "\n"]
````````````````````````````````


basic embedded

```````````````````````````````` example Markdown elements - Markdown elements - MailLink: 3
text with name@url.dom embedded
.
<p>text with <a href="mailto:name@url.dom">name@url.dom</a> embedded</p>
.
FILE[0, 32] chars:[0, 32, "text  … dded\n"]
  PARAGRAPH_BLOCK[0, 32] chars:[0, 32, "text  … dded\n"]
    TEXT_BLOCK[0, 32] chars:[0, 32, "text  … dded\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:MAIL_LINK[10, 22] chars:[10, 22, "name@ … l.dom"]
      Leaf:TEXT[22, 31] chars:[22, 31, " embedded"]
      Leaf:EOL[31, 32] chars:[31, 32, "\n"]
````````````````````````````````


basic trailing

```````````````````````````````` example Markdown elements - Markdown elements - MailLink: 4
text with name@url.dom
.
<p>text with <a href="mailto:name@url.dom">name@url.dom</a></p>
.
FILE[0, 23] chars:[0, 23, "text  … .dom\n"]
  PARAGRAPH_BLOCK[0, 23] chars:[0, 23, "text  … .dom\n"]
    TEXT_BLOCK[0, 23] chars:[0, 23, "text  … .dom\n"]
      Leaf:TEXT[0, 10] chars:[0, 10, "text with "]
      Leaf:MAIL_LINK[10, 22] chars:[10, 22, "name@ … l.dom"]
      Leaf:EOL[22, 23] chars:[22, 23, "\n"]
````````````````````````````````


### Markdown elements - OrderedList

`OrderedList` `OrderedListItem`

empty

```````````````````````````````` example Markdown elements - Markdown elements - OrderedList: 1
1. 

.
<ol>
  <li></li>
</ol>
.
FILE[0, 5] chars:[0, 5, "1. \n\n"]
  ORDERED_LIST[0, 4] chars:[0, 4, "1. \n"]
    ORDERED_LIST_ITEM[0, 4] chars:[0, 4, "1. \n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "1. "]
      Leaf:EOL[3, 4] chars:[3, 4, "\n"]
  BLANK_LINE[4, 5] chars:[4, 5, "\n"]
    Leaf:BLANK_LINE[4, 5] chars:[4, 5, "\n"]
````````````````````````````````


empty task list

```````````````````````````````` example(Markdown elements - Markdown elements - OrderedList: 2) options(pegdown-fail)
1. [ ]

.
<ol>
  <li></li>
</ol>
.
FILE[0, 8] chars:[0, 8, "1. [ ]\n\n"]
  ORDERED_LIST[0, 6] chars:[0, 6, "1. [ ]"]
    ORDERED_LIST_ITEM[0, 6] chars:[0, 6, "1. [ ]"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "1. "]
      Leaf:TASK_ITEM_MARKER[3, 6] chars:[3, 6, "[ ]"]
  Leaf:EOL[6, 7] chars:[6, 7, "\n"]
  BLANK_LINE[7, 8] chars:[7, 8, "\n"]
    Leaf:BLANK_LINE[7, 8] chars:[7, 8, "\n"]
````````````````````````````````


nested

```````````````````````````````` example Markdown elements - Markdown elements - OrderedList: 3
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
FILE[0, 46] chars:[0, 46, "4. it … em 3\n"]
  ORDERED_LIST[0, 46] chars:[0, 46, "4. it … em 3\n"]
    ORDERED_LIST_ITEM[0, 10] chars:[0, 10, "4. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "4. "]
      TEXT_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
        Leaf:TEXT[3, 9] chars:[3, 9, "item 1"]
        Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    ORDERED_LIST_ITEM[10, 36] chars:[10, 36, "3. it …  2.1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[10, 13] chars:[10, 13, "3. "]
      TEXT_BLOCK[13, 20] chars:[13, 20, "item 2\n"]
        Leaf:TEXT[13, 19] chars:[13, 19, "item 2"]
        Leaf:EOL[19, 20] chars:[19, 20, "\n"]
      Leaf:WHITESPACE[20, 24] chars:[20, 24, "    "]
      ORDERED_LIST[24, 36] chars:[24, 36, "2. it …  2.1\n"]
        ORDERED_LIST_ITEM[24, 36] chars:[24, 36, "2. it …  2.1\n"]
          Leaf:ORDERED_LIST_ITEM_MARKER[24, 27] chars:[24, 27, "2. "]
          TEXT_BLOCK[27, 36] chars:[27, 36, "item 2.1\n"]
            Leaf:TEXT[27, 35] chars:[27, 35, "item 2.1"]
            Leaf:EOL[35, 36] chars:[35, 36, "\n"]
    ORDERED_LIST_ITEM[36, 46] chars:[36, 46, "1. item 3\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[36, 39] chars:[36, 39, "1. "]
      TEXT_BLOCK[39, 46] chars:[39, 46, "item 3\n"]
        Leaf:TEXT[39, 45] chars:[39, 45, "item 3"]
        Leaf:EOL[45, 46] chars:[45, 46, "\n"]
````````````````````````````````


nested some loose

```````````````````````````````` example(Markdown elements - Markdown elements - OrderedList: 4) options(pegdown-fail)
4. item 1

3. item 2
    2. item 2.1
1. item 3
.
<ol>
  <li>
  <p>item 1</p></li>
  <li>
    <p>item 2</p>
    <ol>
      <li>item 2.1</li>
    </ol>
  </li>
  <li>item 3</li>
</ol>
.
FILE[0, 46] chars:[0, 46, "4. it … tem 3"]
  ORDERED_LIST[0, 46] chars:[0, 46, "4. it … tem 3"]
    ORDERED_LIST_ITEM[0, 10] chars:[0, 10, "4. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "4. "]
      PARAGRAPH_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
        TEXT_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
          Leaf:TEXT[3, 9] chars:[3, 9, "item 1"]
          Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    BLANK_LINE[10, 11] chars:[10, 11, "\n"]
      Leaf:BLANK_LINE[10, 11] chars:[10, 11, "\n"]
    ORDERED_LIST_ITEM[11, 37] chars:[11, 37, "3. it …  2.1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[11, 14] chars:[11, 14, "3. "]
      TEXT_BLOCK[14, 21] chars:[14, 21, "item 2\n"]
        Leaf:TEXT[14, 20] chars:[14, 20, "item 2"]
        Leaf:EOL[20, 21] chars:[20, 21, "\n"]
      Leaf:WHITESPACE[21, 25] chars:[21, 25, "    "]
      ORDERED_LIST[25, 37] chars:[25, 37, "2. it …  2.1\n"]
        ORDERED_LIST_ITEM[25, 37] chars:[25, 37, "2. it …  2.1\n"]
          Leaf:ORDERED_LIST_ITEM_MARKER[25, 28] chars:[25, 28, "2. "]
          TEXT_BLOCK[28, 37] chars:[28, 37, "item 2.1\n"]
            Leaf:TEXT[28, 36] chars:[28, 36, "item 2.1"]
            Leaf:EOL[36, 37] chars:[36, 37, "\n"]
    ORDERED_LIST_ITEM[37, 46] chars:[37, 46, "1. item 3"]
      Leaf:ORDERED_LIST_ITEM_MARKER[37, 40] chars:[37, 40, "1. "]
      TEXT_BLOCK[40, 46] chars:[40, 46, "item 3"]
        Leaf:TEXT[40, 46] chars:[40, 46, "item 3"]
````````````````````````````````


nested loose

```````````````````````````````` example(Markdown elements - Markdown elements - OrderedList: 5) options(pegdown-fail)
4. item 1

3. item 2
    2. item 2.1
    
1. item 3
.
<ol>
  <li>
  <p>item 1</p></li>
  <li>
    <p>item 2</p>
    <ol>
      <li>item 2.1</li>
    </ol>
  </li>
  <li>
  <p>item 3</p></li>
</ol>
.
FILE[0, 51] chars:[0, 51, "4. it … tem 3"]
  ORDERED_LIST[0, 51] chars:[0, 51, "4. it … tem 3"]
    ORDERED_LIST_ITEM[0, 10] chars:[0, 10, "4. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "4. "]
      PARAGRAPH_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
        TEXT_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
          Leaf:TEXT[3, 9] chars:[3, 9, "item 1"]
          Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    BLANK_LINE[10, 11] chars:[10, 11, "\n"]
      Leaf:BLANK_LINE[10, 11] chars:[10, 11, "\n"]
    ORDERED_LIST_ITEM[11, 37] chars:[11, 37, "3. it …  2.1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[11, 14] chars:[11, 14, "3. "]
      PARAGRAPH_BLOCK[14, 21] chars:[14, 21, "item 2\n"]
        TEXT_BLOCK[14, 21] chars:[14, 21, "item 2\n"]
          Leaf:TEXT[14, 20] chars:[14, 20, "item 2"]
          Leaf:EOL[20, 21] chars:[20, 21, "\n"]
      Leaf:WHITESPACE[21, 25] chars:[21, 25, "    "]
      ORDERED_LIST[25, 37] chars:[25, 37, "2. it …  2.1\n"]
        ORDERED_LIST_ITEM[25, 37] chars:[25, 37, "2. it …  2.1\n"]
          Leaf:ORDERED_LIST_ITEM_MARKER[25, 28] chars:[25, 28, "2. "]
          TEXT_BLOCK[28, 37] chars:[28, 37, "item 2.1\n"]
            Leaf:TEXT[28, 36] chars:[28, 36, "item 2.1"]
            Leaf:EOL[36, 37] chars:[36, 37, "\n"]
    BLANK_LINE[37, 42] chars:[37, 42, "    \n"]
      Leaf:BLANK_LINE[37, 42] chars:[37, 42, "    \n"]
    ORDERED_LIST_ITEM[42, 51] chars:[42, 51, "1. item 3"]
      Leaf:ORDERED_LIST_ITEM_MARKER[42, 45] chars:[42, 45, "1. "]
      PARAGRAPH_BLOCK[45, 51] chars:[45, 51, "item 3"]
        TEXT_BLOCK[45, 51] chars:[45, 51, "item 3"]
          Leaf:TEXT[45, 51] chars:[45, 51, "item 3"]
````````````````````````````````


tight nested loose

```````````````````````````````` example(Markdown elements - Markdown elements - OrderedList: 6) options(pegdown-fail)
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
      <p>item 2.1</p></li>
      <li>
      <p>item 2.1</p></li>
    </ol>
  </li>
  <li>item 3</li>
</ol>
.
FILE[0, 67] chars:[0, 67, "4. it … tem 3"]
  ORDERED_LIST[0, 67] chars:[0, 67, "4. it … tem 3"]
    ORDERED_LIST_ITEM[0, 10] chars:[0, 10, "4. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "4. "]
      TEXT_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
        Leaf:TEXT[3, 9] chars:[3, 9, "item 1"]
        Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    ORDERED_LIST_ITEM[10, 58] chars:[10, 58, "3. it …  2.1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[10, 13] chars:[10, 13, "3. "]
      TEXT_BLOCK[13, 20] chars:[13, 20, "item 2\n"]
        Leaf:TEXT[13, 19] chars:[13, 19, "item 2"]
        Leaf:EOL[19, 20] chars:[19, 20, "\n"]
      BLANK_LINE[20, 21] chars:[20, 21, "\n"]
        Leaf:BLANK_LINE[20, 21] chars:[20, 21, "\n"]
      Leaf:WHITESPACE[21, 25] chars:[21, 25, "    "]
      ORDERED_LIST[25, 58] chars:[25, 58, "2. it …  2.1\n"]
        ORDERED_LIST_ITEM[25, 37] chars:[25, 37, "2. it …  2.1\n"]
          Leaf:ORDERED_LIST_ITEM_MARKER[25, 28] chars:[25, 28, "2. "]
          PARAGRAPH_BLOCK[28, 37] chars:[28, 37, "item 2.1\n"]
            TEXT_BLOCK[28, 37] chars:[28, 37, "item 2.1\n"]
              Leaf:TEXT[28, 36] chars:[28, 36, "item 2.1"]
              Leaf:EOL[36, 37] chars:[36, 37, "\n"]
        BLANK_LINE[37, 42] chars:[37, 42, "    \n"]
          Leaf:BLANK_LINE[37, 42] chars:[37, 42, "    \n"]
        Leaf:WHITESPACE[42, 46] chars:[42, 46, "    "]
        ORDERED_LIST_ITEM[46, 58] chars:[46, 58, "3. it …  2.1\n"]
          Leaf:ORDERED_LIST_ITEM_MARKER[46, 49] chars:[46, 49, "3. "]
          PARAGRAPH_BLOCK[49, 58] chars:[49, 58, "item 2.1\n"]
            TEXT_BLOCK[49, 58] chars:[49, 58, "item 2.1\n"]
              Leaf:TEXT[49, 57] chars:[49, 57, "item 2.1"]
              Leaf:EOL[57, 58] chars:[57, 58, "\n"]
    ORDERED_LIST_ITEM[58, 67] chars:[58, 67, "1. item 3"]
      Leaf:ORDERED_LIST_ITEM_MARKER[58, 61] chars:[58, 61, "1. "]
      TEXT_BLOCK[61, 67] chars:[61, 67, "item 3"]
        Leaf:TEXT[61, 67] chars:[61, 67, "item 3"]
````````````````````````````````


An ordered list after bullet list

```````````````````````````````` example(Markdown elements - Markdown elements - OrderedList: 7) options(pegdown-fail)
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
</ul>
<ol>
  <li>item 1</li>
  <li>item 2</li>
</ol>
.
FILE[0, 64] chars:[0, 64, "- ite … tem 3"]
  BULLET_LIST[0, 64] chars:[0, 64, "- ite … tem 3"]
    BULLET_LIST_ITEM[0, 9] chars:[0, 9, "- item 1\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "- "]
      TEXT_BLOCK[2, 9] chars:[2, 9, "item 1\n"]
        Leaf:TEXT[2, 8] chars:[2, 8, "item 1"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    BULLET_LIST_ITEM[9, 18] chars:[9, 18, "- item 2\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[9, 11] chars:[9, 11, "- "]
      TEXT_BLOCK[11, 18] chars:[11, 18, "item 2\n"]
        Leaf:TEXT[11, 17] chars:[11, 17, "item 2"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
    BULLET_LIST_ITEM[18, 31] chars:[18, 31, "- [ ] … em 3\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[18, 20] chars:[18, 20, "- "]
      Leaf:TASK_ITEM_MARKER[20, 24] chars:[20, 24, "[ ] "]
      PARAGRAPH_BLOCK[24, 31] chars:[24, 31, "item 3\n"]
        TEXT_BLOCK[24, 31] chars:[24, 31, "item 3\n"]
          Leaf:TEXT[24, 30] chars:[24, 30, "item 3"]
          Leaf:EOL[30, 31] chars:[30, 31, "\n"]
    BLANK_LINE[31, 32] chars:[31, 32, "\n"]
      Leaf:BLANK_LINE[31, 32] chars:[31, 32, "\n"]
    ORDERED_LIST_ITEM[32, 42] chars:[32, 42, "2. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[32, 35] chars:[32, 35, "2. "]
      TEXT_BLOCK[35, 42] chars:[35, 42, "item 1\n"]
        Leaf:TEXT[35, 41] chars:[35, 41, "item 1"]
        Leaf:EOL[41, 42] chars:[41, 42, "\n"]
    ORDERED_LIST_ITEM[42, 52] chars:[42, 52, "1. item 2\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[42, 45] chars:[42, 45, "1. "]
      TEXT_BLOCK[45, 52] chars:[45, 52, "item 2\n"]
        Leaf:TEXT[45, 51] chars:[45, 51, "item 2"]
        Leaf:EOL[51, 52] chars:[51, 52, "\n"]
    ORDERED_LIST_ITEM[52, 64] chars:[52, 64, "5. [  … tem 3"]
      Leaf:ORDERED_LIST_ITEM_MARKER[52, 55] chars:[52, 55, "5. "]
      Leaf:TASK_ITEM_MARKER[55, 59] chars:[55, 59, "[ ] "]
      TEXT_BLOCK[59, 64] chars:[59, 64, "tem 3"]
        Leaf:TEXT[59, 64] chars:[59, 64, "tem 3"]
````````````````````````````````


no parens delimiter

```````````````````````````````` example Markdown elements - Markdown elements - OrderedList: 8
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
FILE[0, 31] chars:[0, 31, "2. it … em 3\n"]
  ORDERED_LIST[0, 20] chars:[0, 20, "2. it … em 2\n"]
    ORDERED_LIST_ITEM[0, 10] chars:[0, 10, "2. item 1\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[0, 3] chars:[0, 3, "2. "]
      TEXT_BLOCK[3, 10] chars:[3, 10, "item 1\n"]
        Leaf:TEXT[3, 9] chars:[3, 9, "item 1"]
        Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    ORDERED_LIST_ITEM[10, 20] chars:[10, 20, "1. item 2\n"]
      Leaf:ORDERED_LIST_ITEM_MARKER[10, 13] chars:[10, 13, "1. "]
      TEXT_BLOCK[13, 20] chars:[13, 20, "item 2\n"]
        Leaf:TEXT[13, 19] chars:[13, 19, "item 2"]
        Leaf:EOL[19, 20] chars:[19, 20, "\n"]
  BLANK_LINE[20, 21] chars:[20, 21, "\n"]
    Leaf:BLANK_LINE[20, 21] chars:[20, 21, "\n"]
  PARAGRAPH_BLOCK[21, 31] chars:[21, 31, "3) item 3\n"]
    TEXT_BLOCK[21, 31] chars:[21, 31, "3) item 3\n"]
      Leaf:TEXT[21, 30] chars:[21, 30, "3) item 3"]
      Leaf:EOL[30, 31] chars:[30, 31, "\n"]
````````````````````````````````


### Markdown elements - Paragraph

`Paragraph` `Text` `TextBase`

Plain text should return the whole input

```````````````````````````````` example Markdown elements - Markdown elements - Paragraph: 1
First line
Second line
Last line
.
<p>First line Second line Last line</p>
.
FILE[0, 33] chars:[0, 33, "First … line\n"]
  PARAGRAPH_BLOCK[0, 33] chars:[0, 33, "First … line\n"]
    TEXT_BLOCK[0, 33] chars:[0, 33, "First … line\n"]
      Leaf:TEXT[0, 32] chars:[0, 32, "First …  line"]
      Leaf:EOL[32, 33] chars:[32, 33, "\n"]
````````````````````````````````


Item text in a tight list should have no para wrapper.

```````````````````````````````` example Markdown elements - Markdown elements - Paragraph: 2
* first item 
* first item 
.
<ul>
  <li>first item</li>
  <li>first item</li>
</ul>
.
FILE[0, 28] chars:[0, 28, "* fir … tem \n"]
  BULLET_LIST[0, 28] chars:[0, 28, "* fir … tem \n"]
    BULLET_LIST_ITEM[0, 14] chars:[0, 14, "* fir … tem \n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "* "]
      TEXT_BLOCK[2, 14] chars:[2, 14, "first … tem \n"]
        Leaf:TEXT[2, 13] chars:[2, 13, "first … item "]
        Leaf:EOL[13, 14] chars:[13, 14, "\n"]
    BULLET_LIST_ITEM[14, 28] chars:[14, 28, "* fir … tem \n"]
      Leaf:BULLET_LIST_ITEM_MARKER[14, 16] chars:[14, 16, "* "]
      TEXT_BLOCK[16, 28] chars:[16, 28, "first … tem \n"]
        Leaf:TEXT[16, 27] chars:[16, 27, "first … item "]
        Leaf:EOL[27, 28] chars:[27, 28, "\n"]
````````````````````````````````


Paragraphs Following the item text should have paragraph wrappers

```````````````````````````````` example Markdown elements - Markdown elements - Paragraph: 3
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
FILE[0, 72] chars:[0, 72, "* fir … tem \n"]
  BULLET_LIST[0, 72] chars:[0, 72, "* fir … tem \n"]
    BULLET_LIST_ITEM[0, 58] chars:[0, 58, "* fir … pped\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "* "]
      TEXT_BLOCK[2, 14] chars:[2, 14, "first … tem \n"]
        Leaf:TEXT[2, 13] chars:[2, 13, "first … item "]
        Leaf:EOL[13, 14] chars:[13, 14, "\n"]
      BLANK_LINE[14, 19] chars:[14, 19, "    \n"]
        Leaf:BLANK_LINE[14, 19] chars:[14, 19, "    \n"]
      Leaf:WHITESPACE[19, 23] chars:[19, 23, "    "]
      PARAGRAPH_BLOCK[23, 36] chars:[23, 36, "Para  … pped\n"]
        TEXT_BLOCK[23, 36] chars:[23, 36, "Para  … pped\n"]
          Leaf:TEXT[23, 35] chars:[23, 35, "Para  … apped"]
          Leaf:EOL[35, 36] chars:[35, 36, "\n"]
      BLANK_LINE[36, 41] chars:[36, 41, "    \n"]
        Leaf:BLANK_LINE[36, 41] chars:[36, 41, "    \n"]
      Leaf:WHITESPACE[41, 45] chars:[41, 45, "    "]
      PARAGRAPH_BLOCK[45, 58] chars:[45, 58, "Para  … pped\n"]
        TEXT_BLOCK[45, 58] chars:[45, 58, "Para  … pped\n"]
          Leaf:TEXT[45, 57] chars:[45, 57, "Para  … apped"]
          Leaf:EOL[57, 58] chars:[57, 58, "\n"]
    BULLET_LIST_ITEM[58, 72] chars:[58, 72, "* fir … tem \n"]
      Leaf:BULLET_LIST_ITEM_MARKER[58, 60] chars:[58, 60, "* "]
      TEXT_BLOCK[60, 72] chars:[60, 72, "first … tem \n"]
        Leaf:TEXT[60, 71] chars:[60, 71, "first … item "]
        Leaf:EOL[71, 72] chars:[71, 72, "\n"]
````````````````````````````````


### Markdown elements - Reference

`Reference`

```````````````````````````````` example Markdown elements - Markdown elements - Reference: 1
[url1]: /url1
[url2]: /url2
.

.
FILE[0, 28] chars:[0, 28, "[url1 … url2\n"]
  REFERENCE[0, 14] chars:[0, 14, "[url1 … url1\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 5] chars:[1, 5, "url1"]
      Leaf:TEXT[1, 5] chars:[1, 5, "url1"]
    Leaf:REFERENCE_TEXT_CLOSE[5, 7] chars:[5, 7, "]:"]
    Leaf:WHITESPACE[7, 8] chars:[7, 8, " "]
    REFERENCE_LINK_REF[8, 13] chars:[8, 13, "/url1"]
      Leaf:REFERENCE_LINK_REF[8, 13] chars:[8, 13, "/url1"]
    Leaf:EOL[13, 14] chars:[13, 14, "\n"]
  REFERENCE[14, 28] chars:[14, 28, "[url2 … url2\n"]
    Leaf:REFERENCE_TEXT_OPEN[14, 15] chars:[14, 15, "["]
    REFERENCE_TEXT[15, 19] chars:[15, 19, "url2"]
      Leaf:TEXT[15, 19] chars:[15, 19, "url2"]
    Leaf:REFERENCE_TEXT_CLOSE[19, 21] chars:[19, 21, "]:"]
    Leaf:WHITESPACE[21, 22] chars:[21, 22, " "]
    REFERENCE_LINK_REF[22, 27] chars:[22, 27, "/url2"]
      Leaf:REFERENCE_LINK_REF[22, 27] chars:[22, 27, "/url2"]
    Leaf:EOL[27, 28] chars:[27, 28, "\n"]
````````````````````````````````


Footnote looking references with footnotes disabled

```````````````````````````````` example(Markdown elements - Markdown elements - Reference: 2) options(no-footnotes)
[^url1]: /url1
[^url2]: /url2
.

.
FILE[0, 29] chars:[0, 29, "[^url … /url2"]
  REFERENCE[0, 15] chars:[0, 15, "[^url … url1\n"]
    Leaf:REFERENCE_TEXT_OPEN[0, 1] chars:[0, 1, "["]
    REFERENCE_TEXT[1, 6] chars:[1, 6, "^url1"]
      Leaf:TEXT[1, 6] chars:[1, 6, "^url1"]
    Leaf:REFERENCE_TEXT_CLOSE[6, 8] chars:[6, 8, "]:"]
    Leaf:WHITESPACE[8, 9] chars:[8, 9, " "]
    REFERENCE_LINK_REF[9, 14] chars:[9, 14, "/url1"]
      Leaf:REFERENCE_LINK_REF[9, 14] chars:[9, 14, "/url1"]
    Leaf:EOL[14, 15] chars:[14, 15, "\n"]
  REFERENCE[15, 29] chars:[15, 29, "[^url … /url2"]
    Leaf:REFERENCE_TEXT_OPEN[15, 16] chars:[15, 16, "["]
    REFERENCE_TEXT[16, 21] chars:[16, 21, "^url2"]
      Leaf:TEXT[16, 21] chars:[16, 21, "^url2"]
    Leaf:REFERENCE_TEXT_CLOSE[21, 23] chars:[21, 23, "]:"]
    Leaf:WHITESPACE[23, 24] chars:[23, 24, " "]
    REFERENCE_LINK_REF[24, 29] chars:[24, 29, "/url2"]
      Leaf:REFERENCE_LINK_REF[24, 29] chars:[24, 29, "/url2"]
````````````````````````````````


Footnote looking references with footnotes disabled

```````````````````````````````` example(Markdown elements - Markdown elements - Reference: 3) options(no-footnotes)
this is a footnote[^]. And this is another footnote[^A].

this is an undefined footnote [^undef]

[^]: undefined

.
<p>this is a footnote<a href="undefined">^</a>. And this is another footnote[^A].</p>
<p>this is an undefined footnote [^undef]</p>
.
FILE[0, 114] chars:[0, 114, "this  … ned\n\n"]
  PARAGRAPH_BLOCK[0, 57] chars:[0, 57, "this  … ^A].\n"]
    TEXT_BLOCK[0, 57] chars:[0, 57, "this  … ^A].\n"]
      Leaf:TEXT[0, 18] chars:[0, 18, "this  … tnote"]
      REFERENCE_LINK[18, 21] chars:[18, 21, "[^]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[18, 19] chars:[18, 19, "["]
        REFERENCE_LINK_REFERENCE[19, 20] chars:[19, 20, "^"]
          Leaf:TEXT[19, 20] chars:[19, 20, "^"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[20, 21] chars:[20, 21, "]"]
      Leaf:TEXT[21, 51] chars:[21, 51, ". And … tnote"]
      REFERENCE_LINK[51, 55] chars:[51, 55, "[^A]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[51, 52] chars:[51, 52, "["]
        REFERENCE_LINK_REFERENCE[52, 54] chars:[52, 54, "^A"]
          Leaf:TEXT[52, 54] chars:[52, 54, "^A"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[54, 55] chars:[54, 55, "]"]
      Leaf:TEXT[55, 56] chars:[55, 56, "."]
      Leaf:EOL[56, 57] chars:[56, 57, "\n"]
  BLANK_LINE[57, 58] chars:[57, 58, "\n"]
    Leaf:BLANK_LINE[57, 58] chars:[57, 58, "\n"]
  PARAGRAPH_BLOCK[58, 97] chars:[58, 97, "this  … def]\n"]
    TEXT_BLOCK[58, 97] chars:[58, 97, "this  … def]\n"]
      Leaf:TEXT[58, 88] chars:[58, 88, "this  … note "]
      REFERENCE_LINK[88, 96] chars:[88, 96, "[^undef]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[88, 89] chars:[88, 89, "["]
        REFERENCE_LINK_REFERENCE[89, 95] chars:[89, 95, "^undef"]
          Leaf:TEXT[89, 95] chars:[89, 95, "^undef"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[95, 96] chars:[95, 96, "]"]
      Leaf:EOL[96, 97] chars:[96, 97, "\n"]
  BLANK_LINE[97, 98] chars:[97, 98, "\n"]
    Leaf:BLANK_LINE[97, 98] chars:[97, 98, "\n"]
  REFERENCE[98, 113] chars:[98, 113, "[^]:  … ined\n"]
    Leaf:REFERENCE_TEXT_OPEN[98, 99] chars:[98, 99, "["]
    REFERENCE_TEXT[99, 100] chars:[99, 100, "^"]
      Leaf:TEXT[99, 100] chars:[99, 100, "^"]
    Leaf:REFERENCE_TEXT_CLOSE[100, 102] chars:[100, 102, "]:"]
    Leaf:WHITESPACE[102, 103] chars:[102, 103, " "]
    REFERENCE_LINK_REF[103, 112] chars:[103, 112, "undefined"]
      Leaf:REFERENCE_LINK_REF[103, 112] chars:[103, 112, "undefined"]
    Leaf:EOL[112, 113] chars:[112, 113, "\n"]
  BLANK_LINE[113, 114] chars:[113, 114, "\n"]
    Leaf:BLANK_LINE[113, 114] chars:[113, 114, "\n"]
````````````````````````````````


### Markdown elements - SimTocBlock

`SimTocBlock` `SimTocContent` `SimTocOption` `SimTocOptionList`

basic

```````````````````````````````` example(Markdown elements - Markdown elements - SimTocBlock: 1) options(pegdown-fail)
[TOC]:#

- generated content
    - generated content
.
<ul>
  <li>generated content
    <ul>
      <li>generated content</li>
    </ul>
  </li>
</ul>
.
FILE[0, 52] chars:[0, 52, "[TOC] … ntent"]
  SIM_TOC[0, 52] chars:[0, 52, "[TOC] … ntent"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:SIM_TOC_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[6, 7] chars:[6, 7, "#"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    SIM_TOC_CONTENT[8, 52] chars:[8, 52, "\n- ge … ntent"]
      Leaf:GEN_CONTENT[8, 52] chars:[8, 52, "\n- ge … ntent"]
````````````````````````````````


basic

```````````````````````````````` example(Markdown elements - Markdown elements - SimTocBlock: 2) options(pegdown-fail)
[TOC]:#

## Table of Contents
- generated content
    - generated content
.
<ul>
  <li>generated content
    <ul>
      <li>generated content</li>
    </ul>
  </li>
</ul>
.
FILE[0, 73] chars:[0, 73, "[TOC] … ntent"]
  SIM_TOC[0, 73] chars:[0, 73, "[TOC] … ntent"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:SIM_TOC_CLOSE[4, 6] chars:[4, 6, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[6, 7] chars:[6, 7, "#"]
    Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    SIM_TOC_CONTENT[8, 73] chars:[8, 73, "\n## T … ntent"]
      Leaf:GEN_CONTENT[8, 73] chars:[8, 73, "\n## T … ntent"]
````````````````````````````````


options

```````````````````````````````` example Markdown elements - Markdown elements - SimTocBlock: 3
[TOC levels=3]:#
.

.
FILE[0, 17] chars:[0, 17, "[TOC  … 3]:#\n"]
  SIM_TOC[0, 17] chars:[0, 17, "[TOC  … 3]:#\n"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:SIM_TOC_OPTIONS[5, 13] chars:[5, 13, "levels=3"]
    Leaf:SIM_TOC_CLOSE[13, 15] chars:[13, 15, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[15, 16] chars:[15, 16, "#"]
    Leaf:EOL[16, 17] chars:[16, 17, "\n"]
````````````````````````````````


options, empty title

```````````````````````````````` example Markdown elements - Markdown elements - SimTocBlock: 4
[TOC levels=3]:# ""
[TOC levels=3]:# ''
.

.
FILE[0, 40] chars:[0, 40, "[TOC  … # ''\n"]
  SIM_TOC[0, 20] chars:[0, 20, "[TOC  … # \"\"\n"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:SIM_TOC_OPTIONS[5, 13] chars:[5, 13, "levels=3"]
    Leaf:SIM_TOC_CLOSE[13, 15] chars:[13, 15, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[15, 16] chars:[15, 16, "#"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:SIM_TOC_TITLE_MARKER[17, 19] chars:[17, 19, "\"\""]
    Leaf:EOL[19, 20] chars:[19, 20, "\n"]
  SIM_TOC[20, 40] chars:[20, 40, "[TOC  … # ''\n"]
    Leaf:SIM_TOC_OPEN[20, 21] chars:[20, 21, "["]
    Leaf:SIM_TOC_KEYWORD[21, 24] chars:[21, 24, "TOC"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:SIM_TOC_OPTIONS[25, 33] chars:[25, 33, "levels=3"]
    Leaf:SIM_TOC_CLOSE[33, 35] chars:[33, 35, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[35, 36] chars:[35, 36, "#"]
    Leaf:WHITESPACE[36, 37] chars:[36, 37, " "]
    Leaf:SIM_TOC_TITLE_MARKER[37, 39] chars:[37, 39, "''"]
    Leaf:EOL[39, 40] chars:[39, 40, "\n"]
````````````````````````````````


options, title

```````````````````````````````` example Markdown elements - Markdown elements - SimTocBlock: 5
[TOC levels=3]:# "title"
[TOC levels=3]:# 'title'
.

.
FILE[0, 50] chars:[0, 50, "[TOC  … tle'\n"]
  SIM_TOC[0, 25] chars:[0, 25, "[TOC  … tle\"\n"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:SIM_TOC_OPTIONS[5, 13] chars:[5, 13, "levels=3"]
    Leaf:SIM_TOC_CLOSE[13, 15] chars:[13, 15, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[15, 16] chars:[15, 16, "#"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:SIM_TOC_TITLE_MARKER[17, 18] chars:[17, 18, "\""]
    Leaf:SIM_TOC_TITLE[18, 23] chars:[18, 23, "title"]
    Leaf:SIM_TOC_TITLE_MARKER[23, 24] chars:[23, 24, "\""]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
  SIM_TOC[25, 50] chars:[25, 50, "[TOC  … tle'\n"]
    Leaf:SIM_TOC_OPEN[25, 26] chars:[25, 26, "["]
    Leaf:SIM_TOC_KEYWORD[26, 29] chars:[26, 29, "TOC"]
    Leaf:WHITESPACE[29, 30] chars:[29, 30, " "]
    Leaf:SIM_TOC_OPTIONS[30, 38] chars:[30, 38, "levels=3"]
    Leaf:SIM_TOC_CLOSE[38, 40] chars:[38, 40, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[40, 41] chars:[40, 41, "#"]
    Leaf:WHITESPACE[41, 42] chars:[41, 42, " "]
    Leaf:SIM_TOC_TITLE_MARKER[42, 43] chars:[42, 43, "'"]
    Leaf:SIM_TOC_TITLE[43, 48] chars:[43, 48, "title"]
    Leaf:SIM_TOC_TITLE_MARKER[48, 49] chars:[48, 49, "'"]
    Leaf:EOL[49, 50] chars:[49, 50, "\n"]
````````````````````````````````


options, markers, empty title

```````````````````````````````` example Markdown elements - Markdown elements - SimTocBlock: 6
[TOC levels=3]:# "## "
[TOC levels=3]:# '## '
.

.
FILE[0, 46] chars:[0, 46, "[TOC  … ## '\n"]
  SIM_TOC[0, 23] chars:[0, 23, "[TOC  … ## \"\n"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:SIM_TOC_OPTIONS[5, 13] chars:[5, 13, "levels=3"]
    Leaf:SIM_TOC_CLOSE[13, 15] chars:[13, 15, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[15, 16] chars:[15, 16, "#"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:SIM_TOC_TITLE_MARKER[17, 18] chars:[17, 18, "\""]
    Leaf:SIM_TOC_TITLE[18, 21] chars:[18, 21, "## "]
    Leaf:SIM_TOC_TITLE_MARKER[21, 22] chars:[21, 22, "\""]
    Leaf:EOL[22, 23] chars:[22, 23, "\n"]
  SIM_TOC[23, 46] chars:[23, 46, "[TOC  … ## '\n"]
    Leaf:SIM_TOC_OPEN[23, 24] chars:[23, 24, "["]
    Leaf:SIM_TOC_KEYWORD[24, 27] chars:[24, 27, "TOC"]
    Leaf:WHITESPACE[27, 28] chars:[27, 28, " "]
    Leaf:SIM_TOC_OPTIONS[28, 36] chars:[28, 36, "levels=3"]
    Leaf:SIM_TOC_CLOSE[36, 38] chars:[36, 38, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[38, 39] chars:[38, 39, "#"]
    Leaf:WHITESPACE[39, 40] chars:[39, 40, " "]
    Leaf:SIM_TOC_TITLE_MARKER[40, 41] chars:[40, 41, "'"]
    Leaf:SIM_TOC_TITLE[41, 44] chars:[41, 44, "## "]
    Leaf:SIM_TOC_TITLE_MARKER[44, 45] chars:[44, 45, "'"]
    Leaf:EOL[45, 46] chars:[45, 46, "\n"]
````````````````````````````````


options, markers, title

```````````````````````````````` example Markdown elements - Markdown elements - SimTocBlock: 7
[TOC levels=3]:# "##title"
[TOC levels=3]:# '##title'
.

.
FILE[0, 54] chars:[0, 54, "[TOC  … tle'\n"]
  SIM_TOC[0, 27] chars:[0, 27, "[TOC  … tle\"\n"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:SIM_TOC_OPTIONS[5, 13] chars:[5, 13, "levels=3"]
    Leaf:SIM_TOC_CLOSE[13, 15] chars:[13, 15, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[15, 16] chars:[15, 16, "#"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:SIM_TOC_TITLE_MARKER[17, 18] chars:[17, 18, "\""]
    Leaf:SIM_TOC_TITLE[18, 25] chars:[18, 25, "##title"]
    Leaf:SIM_TOC_TITLE_MARKER[25, 26] chars:[25, 26, "\""]
    Leaf:EOL[26, 27] chars:[26, 27, "\n"]
  SIM_TOC[27, 54] chars:[27, 54, "[TOC  … tle'\n"]
    Leaf:SIM_TOC_OPEN[27, 28] chars:[27, 28, "["]
    Leaf:SIM_TOC_KEYWORD[28, 31] chars:[28, 31, "TOC"]
    Leaf:WHITESPACE[31, 32] chars:[31, 32, " "]
    Leaf:SIM_TOC_OPTIONS[32, 40] chars:[32, 40, "levels=3"]
    Leaf:SIM_TOC_CLOSE[40, 42] chars:[40, 42, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[42, 43] chars:[42, 43, "#"]
    Leaf:WHITESPACE[43, 44] chars:[43, 44, " "]
    Leaf:SIM_TOC_TITLE_MARKER[44, 45] chars:[44, 45, "'"]
    Leaf:SIM_TOC_TITLE[45, 52] chars:[45, 52, "##title"]
    Leaf:SIM_TOC_TITLE_MARKER[52, 53] chars:[52, 53, "'"]
    Leaf:EOL[53, 54] chars:[53, 54, "\n"]
````````````````````````````````


options, markers, title

```````````````````````````````` example Markdown elements - Markdown elements - SimTocBlock: 8
[TOC levels=3]:# "## title"
[TOC levels=3]:# '## title'
.

.
FILE[0, 56] chars:[0, 56, "[TOC  … tle'\n"]
  SIM_TOC[0, 28] chars:[0, 28, "[TOC  … tle\"\n"]
    Leaf:SIM_TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:SIM_TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:SIM_TOC_OPTIONS[5, 13] chars:[5, 13, "levels=3"]
    Leaf:SIM_TOC_CLOSE[13, 15] chars:[13, 15, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[15, 16] chars:[15, 16, "#"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:SIM_TOC_TITLE_MARKER[17, 18] chars:[17, 18, "\""]
    Leaf:SIM_TOC_TITLE[18, 26] chars:[18, 26, "## title"]
    Leaf:SIM_TOC_TITLE_MARKER[26, 27] chars:[26, 27, "\""]
    Leaf:EOL[27, 28] chars:[27, 28, "\n"]
  SIM_TOC[28, 56] chars:[28, 56, "[TOC  … tle'\n"]
    Leaf:SIM_TOC_OPEN[28, 29] chars:[28, 29, "["]
    Leaf:SIM_TOC_KEYWORD[29, 32] chars:[29, 32, "TOC"]
    Leaf:WHITESPACE[32, 33] chars:[32, 33, " "]
    Leaf:SIM_TOC_OPTIONS[33, 41] chars:[33, 41, "levels=3"]
    Leaf:SIM_TOC_CLOSE[41, 43] chars:[41, 43, "]:"]
    Leaf:SIM_TOC_HEADER_MARKERS[43, 44] chars:[43, 44, "#"]
    Leaf:WHITESPACE[44, 45] chars:[44, 45, " "]
    Leaf:SIM_TOC_TITLE_MARKER[45, 46] chars:[45, 46, "'"]
    Leaf:SIM_TOC_TITLE[46, 54] chars:[46, 54, "## title"]
    Leaf:SIM_TOC_TITLE_MARKER[54, 55] chars:[54, 55, "'"]
    Leaf:EOL[55, 56] chars:[55, 56, "\n"]
````````````````````````````````


### Markdown elements - SoftLineBreak

`SoftLineBreak`

```````````````````````````````` example Markdown elements - Markdown elements - SoftLineBreak: 1
line 1
line 2
line 3
.
<p>line 1 line 2 line 3</p>
.
FILE[0, 21] chars:[0, 21, "line  … ne 3\n"]
  PARAGRAPH_BLOCK[0, 21] chars:[0, 21, "line  … ne 3\n"]
    TEXT_BLOCK[0, 21] chars:[0, 21, "line  … ne 3\n"]
      Leaf:TEXT[0, 20] chars:[0, 20, "line  … ine 3"]
      Leaf:EOL[20, 21] chars:[20, 21, "\n"]
````````````````````````````````


### Markdown elements - SpecExample

`SpecExampleBlock` `SpecExampleOptionsList` `SpecExampleOption` `SpecExampleOptionSeparator`
`SpecExampleSource` `SpecExampleHtml` `SpecExampleAst` `SpecExampleSeparator`

Empty

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 1) options(pegdown-fail)
```````````````` example
````````````````
.
<p>```````````````` example ````````````````</p>
.
FILE[0, 41] chars:[0, 41, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 41] chars:[0, 41, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[25, 41] chars:[25, 41, "````` … `````"]
````````````````````````````````


Empty with nbsp for space on first line

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 2) options(pegdown-fail)
```````````````` example
````````````````
.
<p>```````````````` example ````````````````</p>
.
FILE[0, 41] chars:[0, 41, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 41] chars:[0, 41, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[25, 41] chars:[25, 41, "````` … `````"]
````````````````````````````````


Single spacer

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 3
```````````````` example
…
````````````````
.
<pre><code class="example">…
</code></pre>
.
FILE[0, 44] chars:[0, 44, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 44] chars:[0, 44, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[25, 27] chars:[25, 27, "…\n"]
    FLEXMARK_EXAMPLE_HTML[27, 27]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[27, 43] chars:[27, 43, "````` … `````"]
    Leaf:EOL[43, 44] chars:[43, 44, "\n"]
````````````````````````````````


Two spacers

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 4
```````````````` example
…
…
````````````````
.
<pre><code class="example">…
…
</code></pre>
.
FILE[0, 46] chars:[0, 46, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 46] chars:[0, 46, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[25, 27] chars:[25, 27, "…\n"]
    FLEXMARK_EXAMPLE_HTML[27, 27]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[27, 29] chars:[27, 29, "…\n"]
    FLEXMARK_EXAMPLE_AST[29, 29]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[29, 45] chars:[29, 45, "````` … `````"]
    Leaf:EOL[45, 46] chars:[45, 46, "\n"]
````````````````````````````````


Extra spacer

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 5
```````````````` example
…
…
…
````````````````
.
<pre><code class="example">…
…
…
</code></pre>
.
FILE[0, 48] chars:[0, 48, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 48] chars:[0, 48, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[25, 27] chars:[25, 27, "…\n"]
    FLEXMARK_EXAMPLE_HTML[27, 27]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[27, 29] chars:[27, 29, "…\n"]
    FLEXMARK_EXAMPLE_AST[29, 31] chars:[29, 31, "…\n"]
      Leaf:FLEXMARK_EXAMPLE_AST[29, 31] chars:[29, 31, "…\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[31, 47] chars:[31, 47, "````` … `````"]
    Leaf:EOL[47, 48] chars:[47, 48, "\n"]
````````````````````````````````


Source Only

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 6
```````````````` example
Markdown only
````````````````
.
<pre><code class="example">Markdown only
</code></pre>
.
FILE[0, 56] chars:[0, 56, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 56] chars:[0, 56, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[39, 55] chars:[39, 55, "````` … `````"]
    Leaf:EOL[55, 56] chars:[55, 56, "\n"]
````````````````````````````````


Source Only, empty HTML

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 7
```````````````` example
Markdown only
…
````````````````
.
<pre><code class="example">Markdown only
…
</code></pre>
.
FILE[0, 58] chars:[0, 58, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 58] chars:[0, 58, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[39, 41] chars:[39, 41, "…\n"]
    FLEXMARK_EXAMPLE_HTML[41, 41]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[41, 57] chars:[41, 57, "````` … `````"]
    Leaf:EOL[57, 58] chars:[57, 58, "\n"]
````````````````````````````````


Source Only, empty HTML and AST

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 8
```````````````` example
Markdown only
…
…
````````````````
.
<pre><code class="example">Markdown only
…
…
</code></pre>
.
FILE[0, 60] chars:[0, 60, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 60] chars:[0, 60, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[39, 41] chars:[39, 41, "…\n"]
    FLEXMARK_EXAMPLE_HTML[41, 41]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[41, 43] chars:[41, 43, "…\n"]
    FLEXMARK_EXAMPLE_AST[43, 43]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[43, 59] chars:[43, 59, "````` … `````"]
    Leaf:EOL[59, 60] chars:[59, 60, "\n"]
````````````````````````````````


Html Only, no AST

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 9
```````````````` example
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
````````````````
.
<pre><code class="example">…
&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
.
FILE[0, 140] chars:[0, 140, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 140] chars:[0, 140, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[25, 27] chars:[25, 27, "…\n"]
    FLEXMARK_EXAMPLE_HTML[27, 123] chars:[27, 123, "<pre> … /ul>\n"]
      Leaf:FLEXMARK_EXAMPLE_HTML[27, 123] chars:[27, 123, "<pre> … /ul>\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[123, 139] chars:[123, 139, "````` … `````"]
    Leaf:EOL[139, 140] chars:[139, 140, "\n"]
````````````````````````````````


Html Only, empty AST

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 10
```````````````` example
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
…
````````````````
.
<pre><code class="example">…
&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
…
</code></pre>
.
FILE[0, 142] chars:[0, 142, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 142] chars:[0, 142, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[25, 27] chars:[25, 27, "…\n"]
    FLEXMARK_EXAMPLE_HTML[27, 123] chars:[27, 123, "<pre> … /ul>\n"]
      Leaf:FLEXMARK_EXAMPLE_HTML[27, 123] chars:[27, 123, "<pre> … /ul>\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[123, 125] chars:[123, 125, "…\n"]
    FLEXMARK_EXAMPLE_AST[125, 125]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[125, 141] chars:[125, 141, "````` … `````"]
    Leaf:EOL[141, 142] chars:[141, 142, "\n"]
````````````````````````````````


Ast Only

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 11
```````````````` example
…
…
Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, "Markd"..." only"]
````````````````
.
<pre><code class="example">…
…
Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, &quot;Markd&quot;...&quot; only&quot;]
</code></pre>
.
FILE[0, 237] chars:[0, 237, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 237] chars:[0, 237, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 25]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[25, 27] chars:[25, 27, "…\n"]
    FLEXMARK_EXAMPLE_HTML[27, 27]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[27, 29] chars:[27, 29, "…\n"]
    FLEXMARK_EXAMPLE_AST[29, 220] chars:[29, 220, "Docum … ly\"]\n"]
      Leaf:FLEXMARK_EXAMPLE_AST[29, 220] chars:[29, 220, "Docum … ly\"]\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[220, 236] chars:[220, 236, "````` … `````"]
    Leaf:EOL[236, 237] chars:[236, 237, "\n"]
````````````````````````````````


Source and HTML, no AST

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 12
```````````````` example
Markdown only
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
````````````````
.
<pre><code class="example">Markdown only
…
&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
.
FILE[0, 154] chars:[0, 154, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 154] chars:[0, 154, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[39, 41] chars:[39, 41, "…\n"]
    FLEXMARK_EXAMPLE_HTML[41, 137] chars:[41, 137, "<pre> … /ul>\n"]
      Leaf:FLEXMARK_EXAMPLE_HTML[41, 137] chars:[41, 137, "<pre> … /ul>\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[137, 153] chars:[137, 153, "````` … `````"]
    Leaf:EOL[153, 154] chars:[153, 154, "\n"]
````````````````````````````````


Source and HTML, empty AST

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 13
```````````````` example
Markdown only
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
…
````````````````
.
<pre><code class="example">Markdown only
…
&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
…
</code></pre>
.
FILE[0, 156] chars:[0, 156, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 156] chars:[0, 156, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[39, 41] chars:[39, 41, "…\n"]
    FLEXMARK_EXAMPLE_HTML[41, 137] chars:[41, 137, "<pre> … /ul>\n"]
      Leaf:FLEXMARK_EXAMPLE_HTML[41, 137] chars:[41, 137, "<pre> … /ul>\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[137, 139] chars:[137, 139, "…\n"]
    FLEXMARK_EXAMPLE_AST[139, 139]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[139, 155] chars:[139, 155, "````` … `````"]
    Leaf:EOL[155, 156] chars:[155, 156, "\n"]
````````````````````````````````


Source, HTML and AST

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 14
```````````````` example
Markdown only
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
…
Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, "Markd"..." only"]
````````````````
.
<pre><code class="example">Markdown only
…
&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
…
Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, &quot;Markd&quot;...&quot; only&quot;]
</code></pre>
.
FILE[0, 347] chars:[0, 347, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 347] chars:[0, 347, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:EOL[24, 25] chars:[24, 25, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[25, 39] chars:[25, 39, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[39, 41] chars:[39, 41, "…\n"]
    FLEXMARK_EXAMPLE_HTML[41, 137] chars:[41, 137, "<pre> … /ul>\n"]
      Leaf:FLEXMARK_EXAMPLE_HTML[41, 137] chars:[41, 137, "<pre> … /ul>\n"]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[137, 139] chars:[137, 139, "…\n"]
    FLEXMARK_EXAMPLE_AST[139, 330] chars:[139, 330, "Docum … ly\"]\n"]
      Leaf:FLEXMARK_EXAMPLE_AST[139, 330] chars:[139, 330, "Docum … ly\"]\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[330, 346] chars:[330, 346, "````` … `````"]
    Leaf:EOL[346, 347] chars:[346, 347, "\n"]
````````````````````````````````


Plain Coordinates, section

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 15
```````````````` example Section
Markdown only
````````````````
.
<pre><code class="example Section">Markdown only
</code></pre>
.
FILE[0, 64] chars:[0, 64, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 64] chars:[0, 64, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[47, 63] chars:[47, 63, "````` … `````"]
    Leaf:EOL[63, 64] chars:[63, 64, "\n"]
````````````````````````````````


Plain Coordinates, number

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 16
```````````````` example :number
Markdown only
````````````````
.
<pre><code class="example :number">Markdown only
</code></pre>
.
FILE[0, 64] chars:[0, 64, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 64] chars:[0, 64, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[25, 26] chars:[25, 26, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[26, 32] chars:[26, 32, "number"]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[47, 63] chars:[47, 63, "````` … `````"]
    Leaf:EOL[63, 64] chars:[63, 64, "\n"]
````````````````````````````````


Plain Coordinates, section and number

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 17
```````````````` example Section:number
…
…
````````````````
.
<pre><code class="example Section:number">…
…
</code></pre>
.
FILE[0, 61] chars:[0, 61, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 61] chars:[0, 61, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[32, 33] chars:[32, 33, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[33, 39] chars:[33, 39, "number"]
    Leaf:EOL[39, 40] chars:[39, 40, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[40, 40]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[40, 42] chars:[40, 42, "…\n"]
    FLEXMARK_EXAMPLE_HTML[42, 42]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[42, 44] chars:[42, 44, "…\n"]
    FLEXMARK_EXAMPLE_AST[44, 44]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[44, 60] chars:[44, 60, "````` … `````"]
    Leaf:EOL[60, 61] chars:[60, 61, "\n"]
````````````````````````````````


Wrapped Coordinates, section

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 18
```````````````` example(Section)
Markdown only
````````````````
.
<pre><code class="example(Section)">Markdown only
</code></pre>
.
FILE[0, 65] chars:[0, 65, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 65] chars:[0, 65, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[32, 33] chars:[32, 33, ")"]
    Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[34, 48] chars:[34, 48, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[34, 48] chars:[34, 48, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[48, 64] chars:[48, 64, "````` … `````"]
    Leaf:EOL[64, 65] chars:[64, 65, "\n"]
````````````````````````````````


Wrapped Coordinates, number

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 19) options(pegdown-fail)
```````````````` example(:number)
````````````````
.
<p>```````````````` example(:number) ````````````````</p>
.
FILE[0, 50] chars:[0, 50, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 50] chars:[0, 50, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[25, 26] chars:[25, 26, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[26, 32] chars:[26, 32, "number"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[32, 33] chars:[32, 33, ")"]
    Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[34, 34]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[34, 50] chars:[34, 50, "````` … `````"]
````````````````````````````````


Wrapped Coordinates, section and number

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 20
```````````````` example(Section:number)
…
…
````````````````
.
<pre><code class="example(Section:number)">…
…
</code></pre>
.
FILE[0, 62] chars:[0, 62, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 62] chars:[0, 62, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[32, 33] chars:[32, 33, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[33, 39] chars:[33, 39, "number"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[39, 40] chars:[39, 40, ")"]
    Leaf:EOL[40, 41] chars:[40, 41, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[41, 41]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[41, 43] chars:[41, 43, "…\n"]
    FLEXMARK_EXAMPLE_HTML[43, 43]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[43, 45] chars:[43, 45, "…\n"]
    FLEXMARK_EXAMPLE_AST[45, 45]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[45, 61] chars:[45, 61, "````` … `````"]
    Leaf:EOL[61, 62] chars:[61, 62, "\n"]
````````````````````````````````


Wrapped Coordinates, section

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 21
```````````````` example(Section
Markdown only
````````````````
.
<pre><code class="example(Section">Markdown only
</code></pre>
.
FILE[0, 64] chars:[0, 64, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 64] chars:[0, 64, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[47, 63] chars:[47, 63, "````` … `````"]
    Leaf:EOL[63, 64] chars:[63, 64, "\n"]
````````````````````````````````


Wrapped Coordinates, number

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 22
```````````````` example(:number
Markdown only
````````````````
.
<pre><code class="example(:number">Markdown only
</code></pre>
.
FILE[0, 64] chars:[0, 64, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 64] chars:[0, 64, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[25, 26] chars:[25, 26, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[26, 32] chars:[26, 32, "number"]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[47, 63] chars:[47, 63, "````` … `````"]
    Leaf:EOL[63, 64] chars:[63, 64, "\n"]
````````````````````````````````


Wrapped Coordinates, section and number

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 23
```````````````` example(Section:number
…
…
````````````````
.
<pre><code class="example(Section:number">…
…
</code></pre>
.
FILE[0, 61] chars:[0, 61, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 61] chars:[0, 61, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[32, 33] chars:[32, 33, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[33, 39] chars:[33, 39, "number"]
    Leaf:EOL[39, 40] chars:[39, 40, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[40, 40]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[40, 42] chars:[40, 42, "…\n"]
    FLEXMARK_EXAMPLE_HTML[42, 42]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[42, 44] chars:[42, 44, "…\n"]
    FLEXMARK_EXAMPLE_AST[44, 44]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[44, 60] chars:[44, 60, "````` … `````"]
    Leaf:EOL[60, 61] chars:[60, 61, "\n"]
````````````````````````````````


Wrapped Coordinates, section

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 24) options(pegdown-fail)
```````````````` example Section)
````````````````
.
<p>```````````````` example Section) ````````````````</p>
.
FILE[0, 50] chars:[0, 50, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 50] chars:[0, 50, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[32, 33] chars:[32, 33, ")"]
    Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[34, 34]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[34, 50] chars:[34, 50, "````` … `````"]
````````````````````````````````


Wrapped Coordinates, number

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 25) options(pegdown-fail)
```````````````` example :number)
Markdown only
````````````````
.
<pre><code class="example :number)">Markdown only
</code></pre>
.
FILE[0, 64] chars:[0, 64, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 64] chars:[0, 64, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[25, 26] chars:[25, 26, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[26, 32] chars:[26, 32, "number"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[32, 33] chars:[32, 33, ")"]
    Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[34, 48] chars:[34, 48, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[34, 48] chars:[34, 48, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[48, 64] chars:[48, 64, "````` … `````"]
````````````````````````````````


Wrapped Coordinates, section and number

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 26) options(pegdown-fail)
```````````````` example Section:number)
…
…
````````````````
.
<pre><code class="example Section:number)">…
…
</code></pre>
.
FILE[0, 61] chars:[0, 61, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 61] chars:[0, 61, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "Section"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[32, 33] chars:[32, 33, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[33, 39] chars:[33, 39, "number"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[39, 40] chars:[39, 40, ")"]
    Leaf:EOL[40, 41] chars:[40, 41, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[41, 41]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[41, 43] chars:[41, 43, "…\n"]
    FLEXMARK_EXAMPLE_HTML[43, 43]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[43, 45] chars:[43, 45, "…\n"]
    FLEXMARK_EXAMPLE_AST[45, 45]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[45, 61] chars:[45, 61, "````` … `````"]
````````````````````````````````


Plain options

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 27
```````````````` example options
Markdown only
````````````````
.
<pre><code class="example options">Markdown only
</code></pre>
.
FILE[0, 64] chars:[0, 64, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 64] chars:[0, 64, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    FLEXMARK_EXAMPLE_OPTIONS[32, 32]
    Leaf:EOL[32, 33] chars:[32, 33, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[33, 47] chars:[33, 47, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[47, 63] chars:[47, 63, "````` … `````"]
    Leaf:EOL[63, 64] chars:[63, 64, "\n"]
````````````````````````````````


Wrapped options

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 28
```````````````` example options()
Markdown only
````````````````
.
<pre><code class="example options()">Markdown only
</code></pre>
.
FILE[0, 66] chars:[0, 66, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 66] chars:[0, 66, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[32, 33] chars:[32, 33, "("]
    FLEXMARK_EXAMPLE_OPTIONS[33, 33]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[33, 34] chars:[33, 34, ")"]
    Leaf:EOL[34, 35] chars:[34, 35, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[35, 49] chars:[35, 49, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[35, 49] chars:[35, 49, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[49, 65] chars:[49, 65, "````` … `````"]
    Leaf:EOL[65, 66] chars:[65, 66, "\n"]
````````````````````````````````


Wrapped options

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 29
```````````````` example options(
…
…
````````````````
.
<pre><code class="example options(">…
…
</code></pre>
.
FILE[0, 55] chars:[0, 55, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 55] chars:[0, 55, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[32, 33] chars:[32, 33, "("]
    FLEXMARK_EXAMPLE_OPTIONS[33, 33]
    Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[34, 34]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[34, 36] chars:[34, 36, "…\n"]
    FLEXMARK_EXAMPLE_HTML[36, 36]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[36, 38] chars:[36, 38, "…\n"]
    FLEXMARK_EXAMPLE_AST[38, 38]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[38, 54] chars:[38, 54, "````` … `````"]
    Leaf:EOL[54, 55] chars:[54, 55, "\n"]
````````````````````````````````


Unwrapped options as section

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 30
```````````````` example options)
…
…
````````````````
.
<pre><code class="example options)">…
…
</code></pre>
.
FILE[0, 55] chars:[0, 55, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 55] chars:[0, 55, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    FLEXMARK_EXAMPLE_OPTIONS[32, 32]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[32, 33] chars:[32, 33, ")"]
    Leaf:EOL[33, 34] chars:[33, 34, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[34, 34]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[34, 36] chars:[34, 36, "…\n"]
    FLEXMARK_EXAMPLE_HTML[36, 36]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[36, 38] chars:[36, 38, "…\n"]
    FLEXMARK_EXAMPLE_AST[38, 38]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[38, 54] chars:[38, 54, "````` … `````"]
    Leaf:EOL[54, 55] chars:[54, 55, "\n"]
````````````````````````````````


Plain options

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 31
```````````````` example options option
Markdown only
````````````````
.
<pre><code class="example options option">Markdown only
</code></pre>
.
FILE[0, 71] chars:[0, 71, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 71] chars:[0, 71, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:WHITESPACE[32, 33] chars:[32, 33, " "]
    FLEXMARK_EXAMPLE_OPTIONS[33, 39] chars:[33, 39, "option"]
      FLEXMARK_EXAMPLE_OPTION[33, 39] chars:[33, 39, "option"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[33, 39] chars:[33, 39, "option"]
    Leaf:EOL[39, 40] chars:[39, 40, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[40, 54] chars:[40, 54, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[40, 54] chars:[40, 54, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[54, 70] chars:[54, 70, "````` … `````"]
    Leaf:EOL[70, 71] chars:[70, 71, "\n"]
````````````````````````````````


Wrapped options

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 32
```````````````` example options(option)
Markdown only
````````````````
.
<pre><code class="example options(option)">Markdown only
</code></pre>
.
FILE[0, 72] chars:[0, 72, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 72] chars:[0, 72, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[32, 33] chars:[32, 33, "("]
    FLEXMARK_EXAMPLE_OPTIONS[33, 39] chars:[33, 39, "option"]
      FLEXMARK_EXAMPLE_OPTION[33, 39] chars:[33, 39, "option"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[33, 39] chars:[33, 39, "option"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[39, 40] chars:[39, 40, ")"]
    Leaf:EOL[40, 41] chars:[40, 41, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[41, 55] chars:[41, 55, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[41, 55] chars:[41, 55, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[55, 71] chars:[55, 71, "````` … `````"]
    Leaf:EOL[71, 72] chars:[71, 72, "\n"]
````````````````````````````````


Wrapped options

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 33
```````````````` example options(option
…
…
````````````````
.
<pre><code class="example options(option">…
…
</code></pre>
.
FILE[0, 61] chars:[0, 61, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 61] chars:[0, 61, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[32, 33] chars:[32, 33, "("]
    FLEXMARK_EXAMPLE_OPTIONS[33, 39] chars:[33, 39, "option"]
      FLEXMARK_EXAMPLE_OPTION[33, 39] chars:[33, 39, "option"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[33, 39] chars:[33, 39, "option"]
    Leaf:EOL[39, 40] chars:[39, 40, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[40, 40]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[40, 42] chars:[40, 42, "…\n"]
    FLEXMARK_EXAMPLE_HTML[42, 42]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[42, 44] chars:[42, 44, "…\n"]
    FLEXMARK_EXAMPLE_AST[44, 44]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[44, 60] chars:[44, 60, "````` … `````"]
    Leaf:EOL[60, 61] chars:[60, 61, "\n"]
````````````````````````````````


Unwrapped options as section

```````````````````````````````` example Markdown elements - Markdown elements - SpecExample: 34
```````````````` example options option)
…
…
````````````````
.
<pre><code class="example options option)">…
…
</code></pre>
.
FILE[0, 62] chars:[0, 62, "````` … ````\n"]
  FLEXMARK_EXAMPLE[0, 62] chars:[0, 62, "````` … ````\n"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:WHITESPACE[32, 33] chars:[32, 33, " "]
    FLEXMARK_EXAMPLE_OPTIONS[33, 39] chars:[33, 39, "option"]
      FLEXMARK_EXAMPLE_OPTION[33, 39] chars:[33, 39, "option"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[33, 39] chars:[33, 39, "option"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[39, 40] chars:[39, 40, ")"]
    Leaf:EOL[40, 41] chars:[40, 41, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[41, 41]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[41, 43] chars:[41, 43, "…\n"]
    FLEXMARK_EXAMPLE_HTML[43, 43]
    Leaf:FLEXMARK_EXAMPLE_SEPARATOR[43, 45] chars:[43, 45, "…\n"]
    FLEXMARK_EXAMPLE_AST[45, 45]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[45, 61] chars:[45, 61, "````` … `````"]
    Leaf:EOL[61, 62] chars:[61, 62, "\n"]
````````````````````````````````


Wrapped options

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 35) options(pegdown-fail)
```````````````` example options(1, 2,, ,  ,3 , 4 )
Markdown only
````````````````
.
<pre><code class="example options(1, 2,, ,  ,3 , 4 )">Markdown only
</code></pre>
.
FILE[0, 82] chars:[0, 82, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 82] chars:[0, 82, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[25, 32] chars:[25, 32, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[32, 33] chars:[32, 33, "("]
    FLEXMARK_EXAMPLE_OPTIONS[33, 50] chars:[33, 50, "1, 2, …  , 4 "]
      FLEXMARK_EXAMPLE_OPTION[33, 34] chars:[33, 34, "1"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[33, 34] chars:[33, 34, "1"]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[34, 35] chars:[34, 35, ","]
      Leaf:WHITESPACE[35, 36] chars:[35, 36, " "]
      FLEXMARK_EXAMPLE_OPTION[36, 37] chars:[36, 37, "2"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[36, 37] chars:[36, 37, "2"]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[37, 39] chars:[37, 39, ",,"]
      Leaf:WHITESPACE[39, 40] chars:[39, 40, " "]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[40, 41] chars:[40, 41, ","]
      Leaf:WHITESPACE[41, 43] chars:[41, 43, "  "]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[43, 44] chars:[43, 44, ","]
      FLEXMARK_EXAMPLE_OPTION[44, 45] chars:[44, 45, "3"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[44, 45] chars:[44, 45, "3"]
      Leaf:WHITESPACE[45, 46] chars:[45, 46, " "]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[46, 47] chars:[46, 47, ","]
      Leaf:WHITESPACE[47, 48] chars:[47, 48, " "]
      FLEXMARK_EXAMPLE_OPTION[48, 49] chars:[48, 49, "4"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[48, 49] chars:[48, 49, "4"]
      Leaf:WHITESPACE[49, 50] chars:[49, 50, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[50, 51] chars:[50, 51, ")"]
    Leaf:EOL[51, 52] chars:[51, 52, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[52, 66] chars:[52, 66, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[52, 66] chars:[52, 66, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[66, 82] chars:[66, 82, "````` … `````"]
````````````````````````````````


Unwrapped coords Wrapped options

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 36) options(pegdown-fail)
```````````````` example section:number options(1, 2,3 , 4 )
````````````````
.
<p>```````````````` example section:number options(1, 2,3 , 4 ) ````````````````</p>
.
FILE[0, 77] chars:[0, 77, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 77] chars:[0, 77, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:WHITESPACE[24, 25] chars:[24, 25, " "]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "section"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[32, 33] chars:[32, 33, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[33, 39] chars:[33, 39, "number"]
    Leaf:WHITESPACE[39, 40] chars:[39, 40, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[40, 47] chars:[40, 47, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[47, 48] chars:[47, 48, "("]
    FLEXMARK_EXAMPLE_OPTIONS[48, 59] chars:[48, 59, "1, 2, …  , 4 "]
      FLEXMARK_EXAMPLE_OPTION[48, 49] chars:[48, 49, "1"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[48, 49] chars:[48, 49, "1"]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[49, 50] chars:[49, 50, ","]
      Leaf:WHITESPACE[50, 51] chars:[50, 51, " "]
      FLEXMARK_EXAMPLE_OPTION[51, 52] chars:[51, 52, "2"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[51, 52] chars:[51, 52, "2"]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[52, 53] chars:[52, 53, ","]
      FLEXMARK_EXAMPLE_OPTION[53, 54] chars:[53, 54, "3"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[53, 54] chars:[53, 54, "3"]
      Leaf:WHITESPACE[54, 55] chars:[54, 55, " "]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[55, 56] chars:[55, 56, ","]
      Leaf:WHITESPACE[56, 57] chars:[56, 57, " "]
      FLEXMARK_EXAMPLE_OPTION[57, 58] chars:[57, 58, "4"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[57, 58] chars:[57, 58, "4"]
      Leaf:WHITESPACE[58, 59] chars:[58, 59, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[59, 60] chars:[59, 60, ")"]
    Leaf:EOL[60, 61] chars:[60, 61, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[61, 61]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[61, 77] chars:[61, 77, "````` … `````"]
````````````````````````````````


Wrapped coords Wrapped options

```````````````````````````````` example(Markdown elements - Markdown elements - SpecExample: 37) options(pegdown-fail)
```````````````` example(section:number) options(1, 2,3 , 4 )
Markdown only
````````````````
.
<pre><code class="example(section:number) options(1, 2,3 , 4 )">Markdown only
</code></pre>
.
FILE[0, 92] chars:[0, 92, "````` … `````"]
  FLEXMARK_EXAMPLE[0, 92] chars:[0, 92, "````` … `````"]
    Leaf:FLEXMARK_EXAMPLE_OPEN[0, 16] chars:[0, 16, "````` … `````"]
    Leaf:WHITESPACE[16, 17] chars:[16, 17, " "]
    Leaf:FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD[17, 24] chars:[17, 24, "example"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_OPEN[24, 25] chars:[24, 25, "("]
    Leaf:FLEXMARK_EXAMPLE_SECTION[25, 32] chars:[25, 32, "section"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER_SEPARATOR[32, 33] chars:[32, 33, ":"]
    Leaf:FLEXMARK_EXAMPLE_NUMBER[33, 39] chars:[33, 39, "number"]
    Leaf:FLEXMARK_EXAMPLE_SECTION_CLOSE[39, 40] chars:[39, 40, ")"]
    Leaf:WHITESPACE[40, 41] chars:[40, 41, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_KEYWORD[41, 48] chars:[41, 48, "options"]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_OPEN[48, 49] chars:[48, 49, "("]
    FLEXMARK_EXAMPLE_OPTIONS[49, 60] chars:[49, 60, "1, 2, …  , 4 "]
      FLEXMARK_EXAMPLE_OPTION[49, 50] chars:[49, 50, "1"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[49, 50] chars:[49, 50, "1"]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[50, 51] chars:[50, 51, ","]
      Leaf:WHITESPACE[51, 52] chars:[51, 52, " "]
      FLEXMARK_EXAMPLE_OPTION[52, 53] chars:[52, 53, "2"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[52, 53] chars:[52, 53, "2"]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[53, 54] chars:[53, 54, ","]
      FLEXMARK_EXAMPLE_OPTION[54, 55] chars:[54, 55, "3"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[54, 55] chars:[54, 55, "3"]
      Leaf:WHITESPACE[55, 56] chars:[55, 56, " "]
      Leaf:FLEXMARK_EXAMPLE_OPTION_SEPARATOR[56, 57] chars:[56, 57, ","]
      Leaf:WHITESPACE[57, 58] chars:[57, 58, " "]
      FLEXMARK_EXAMPLE_OPTION[58, 59] chars:[58, 59, "4"]
        Leaf:FLEXMARK_EXAMPLE_OPTION_NAME[58, 59] chars:[58, 59, "4"]
      Leaf:WHITESPACE[59, 60] chars:[59, 60, " "]
    Leaf:FLEXMARK_EXAMPLE_OPTIONS_CLOSE[60, 61] chars:[60, 61, ")"]
    Leaf:EOL[61, 62] chars:[61, 62, "\n"]
    FLEXMARK_EXAMPLE_SOURCE[62, 76] chars:[62, 76, "Markd … only\n"]
      Leaf:FLEXMARK_EXAMPLE_SOURCE[62, 76] chars:[62, 76, "Markd … only\n"]
    Leaf:FLEXMARK_EXAMPLE_CLOSE[76, 92] chars:[76, 92, "````` … `````"]
````````````````````````````````


### Markdown elements - Strikethrough

`Strikethrough`

basic

```````````````````````````````` example Markdown elements - Markdown elements - Strikethrough: 1
~italic~
text ~italic~ embedded 
~italic~ embedded 
text ~italic~ 
.
<p>~italic~ text ~italic~ embedded ~italic~ embedded text ~italic~ </p>
.
FILE[0, 67] chars:[0, 67, "~ital … ic~ \n"]
  PARAGRAPH_BLOCK[0, 67] chars:[0, 67, "~ital … ic~ \n"]
    TEXT_BLOCK[0, 67] chars:[0, 67, "~ital … ic~ \n"]
      Leaf:TEXT[0, 66] chars:[0, 66, "~ital … lic~ "]
      Leaf:EOL[66, 67] chars:[66, 67, "\n"]
````````````````````````````````


### Markdown elements - StrongEmphasis

`StrongEmphasis`

basic

```````````````````````````````` example Markdown elements - Markdown elements - StrongEmphasis: 1
**italic**
text **italic** embedded 
**italic** embedded 
text **italic** 
.
<p><strong>italic</strong> text <strong>italic</strong> embedded <strong>italic</strong> embedded text <strong>italic</strong> </p>
.
FILE[0, 75] chars:[0, 75, "**ita … c** \n"]
  PARAGRAPH_BLOCK[0, 75] chars:[0, 75, "**ita … c** \n"]
    TEXT_BLOCK[0, 75] chars:[0, 75, "**ita … c** \n"]
      BOLD[0, 10] chars:[0, 10, "**italic**"]
        Leaf:BOLD_MARKER[0, 2] chars:[0, 2, "**"]
        Leaf:BOLD_TEXT[2, 8] chars:[2, 8, "italic"]
        Leaf:BOLD_MARKER[8, 10] chars:[8, 10, "**"]
      Leaf:TEXT[10, 16] chars:[10, 16, "\ntext "]
      BOLD[16, 26] chars:[16, 26, "**italic**"]
        Leaf:BOLD_MARKER[16, 18] chars:[16, 18, "**"]
        Leaf:BOLD_TEXT[18, 24] chars:[18, 24, "italic"]
        Leaf:BOLD_MARKER[24, 26] chars:[24, 26, "**"]
      Leaf:TEXT[26, 37] chars:[26, 37, " embe … ded \n"]
      BOLD[37, 47] chars:[37, 47, "**italic**"]
        Leaf:BOLD_MARKER[37, 39] chars:[37, 39, "**"]
        Leaf:BOLD_TEXT[39, 45] chars:[39, 45, "italic"]
        Leaf:BOLD_MARKER[45, 47] chars:[45, 47, "**"]
      Leaf:TEXT[47, 63] chars:[47, 63, " embe … text "]
      BOLD[63, 73] chars:[63, 73, "**italic**"]
        Leaf:BOLD_MARKER[63, 65] chars:[63, 65, "**"]
        Leaf:BOLD_TEXT[65, 71] chars:[65, 71, "italic"]
        Leaf:BOLD_MARKER[71, 73] chars:[71, 73, "**"]
      Leaf:TEXT[73, 74] chars:[73, 74, " "]
      Leaf:EOL[74, 75] chars:[74, 75, "\n"]
````````````````````````````````


### Markdown elements - TableBlock

`TableBlock` `TableBody` `TableCaption` `TableCell` `TableHead` `TableRow` `TableSeparator`

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 1
Abc|Def
---|---
1|2
table, you are over
.
<table>
  <thead>
    <tr><th>Abc</th><th>Def</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>1</td><td>2</td>
    </tr>
  </tbody>
</table>
<p>table, you are over</p>
.
FILE[0, 40] chars:[0, 40, "Abc|D … over\n"]
  TABLE[0, 20] chars:[0, 20, "Abc|D … \n1|2\n"]
    TABLE_HEADER[0, 8] chars:[0, 8, "Abc|Def\n"]
      TABLE_ROW[0, 8] chars:[0, 8, "Abc|Def\n"]
        TABLE_CELL[0, 3] chars:[0, 3, "Abc"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[0, 3] chars:[0, 3, "Abc"]
        Leaf:TABLE_HDR_ROW_ODD[3, 4] chars:[3, 4, "|"]
        TABLE_CELL[4, 7] chars:[4, 7, "Def"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[4, 7] chars:[4, 7, "Def"]
        Leaf:EOL[7, 8] chars:[7, 8, "\n"]
    TABLE_SEPARATOR[8, 16] chars:[8, 16, "---|---\n"]
      TABLE_CELL[8, 11] chars:[8, 11, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[8, 11] chars:[8, 11, "---"]
      Leaf:TABLE_SEP_ROW_ODD[11, 12] chars:[11, 12, "|"]
      TABLE_CELL[12, 15] chars:[12, 15, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[12, 15] chars:[12, 15, "---"]
      Leaf:EOL[15, 16] chars:[15, 16, "\n"]
    TABLE_BODY[16, 20] chars:[16, 20, "1|2\n"]
      TABLE_ROW[16, 20] chars:[16, 20, "1|2\n"]
        TABLE_CELL[16, 17] chars:[16, 17, "1"]
          Leaf:TABLE_CELL_RODD_CODD[16, 17] chars:[16, 17, "1"]
        Leaf:TABLE_ROW_ODD[17, 18] chars:[17, 18, "|"]
        TABLE_CELL[18, 19] chars:[18, 19, "2"]
          Leaf:TABLE_CELL_RODD_CEVEN[18, 19] chars:[18, 19, "2"]
        Leaf:EOL[19, 20] chars:[19, 20, "\n"]
  PARAGRAPH_BLOCK[20, 40] chars:[20, 40, "table … over\n"]
    TEXT_BLOCK[20, 40] chars:[20, 40, "table … over\n"]
      Leaf:TEXT[20, 39] chars:[20, 39, "table …  over"]
      Leaf:EOL[39, 40] chars:[39, 40, "\n"]
````````````````````````````````


inlines should be processed

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 2
|**Abc** **test** |_Def_ _Def_
---|---
[ref]|`code` `code`
table, you are over

[ref]: /url
.
<table>
  <thead>
    <tr><th><strong>Abc</strong> <strong>test</strong> </th><th><em>Def</em> <em>Def</em></th>
    </tr>
  </thead>
  <tbody>
    <tr><td><a href="/url">ref</a></td><td><code>code</code> <code>code</code></td>
    </tr>
  </tbody>
</table>
<p>table, you are over</p>
.
FILE[0, 92] chars:[0, 92, "|**Ab … /url\n"]
  TABLE[0, 59] chars:[0, 59, "|**Ab … ode`\n"]
    TABLE_HEADER[0, 31] chars:[0, 31, "|**Ab … Def_\n"]
      TABLE_ROW[0, 31] chars:[0, 31, "|**Ab … Def_\n"]
        Leaf:TABLE_HDR_ROW_ODD[0, 1] chars:[0, 1, "|"]
        TABLE_CELL[1, 18] chars:[1, 18, "**Abc … st** "]
          BOLD[1, 8] chars:[1, 8, "**Abc**"]
            Leaf:TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER[1, 3] chars:[1, 3, "**"]
            Leaf:TABLE_HDR_CELL_RODD_CODD_BOLD_TEXT[3, 6] chars:[3, 6, "Abc"]
            Leaf:TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER[6, 8] chars:[6, 8, "**"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[8, 9] chars:[8, 9, " "]
          BOLD[9, 17] chars:[9, 17, "**test**"]
            Leaf:TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER[9, 11] chars:[9, 11, "**"]
            Leaf:TABLE_HDR_CELL_RODD_CODD_BOLD_TEXT[11, 15] chars:[11, 15, "test"]
            Leaf:TABLE_HDR_CELL_RODD_CODD_BOLD_MARKER[15, 17] chars:[15, 17, "**"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[17, 18] chars:[17, 18, " "]
        Leaf:TABLE_HDR_ROW_ODD[18, 19] chars:[18, 19, "|"]
        TABLE_CELL[19, 30] chars:[19, 30, "_Def_ … _Def_"]
          ITALIC[19, 24] chars:[19, 24, "_Def_"]
            Leaf:TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER[19, 20] chars:[19, 20, "_"]
            Leaf:TABLE_HDR_CELL_RODD_CEVEN_ITALIC_TEXT[20, 23] chars:[20, 23, "Def"]
            Leaf:TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER[23, 24] chars:[23, 24, "_"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[24, 25] chars:[24, 25, " "]
          ITALIC[25, 30] chars:[25, 30, "_Def_"]
            Leaf:TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER[25, 26] chars:[25, 26, "_"]
            Leaf:TABLE_HDR_CELL_RODD_CEVEN_ITALIC_TEXT[26, 29] chars:[26, 29, "Def"]
            Leaf:TABLE_HDR_CELL_RODD_CEVEN_ITALIC_MARKER[29, 30] chars:[29, 30, "_"]
        Leaf:EOL[30, 31] chars:[30, 31, "\n"]
    TABLE_SEPARATOR[31, 39] chars:[31, 39, "---|---\n"]
      TABLE_CELL[31, 34] chars:[31, 34, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[31, 34] chars:[31, 34, "---"]
      Leaf:TABLE_SEP_ROW_ODD[34, 35] chars:[34, 35, "|"]
      TABLE_CELL[35, 38] chars:[35, 38, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[35, 38] chars:[35, 38, "---"]
      Leaf:EOL[38, 39] chars:[38, 39, "\n"]
    TABLE_BODY[39, 59] chars:[39, 59, "[ref] … ode`\n"]
      TABLE_ROW[39, 59] chars:[39, 59, "[ref] … ode`\n"]
        TABLE_CELL[39, 44] chars:[39, 44, "[ref]"]
          REFERENCE_LINK[39, 44] chars:[39, 44, "[ref]"]
            Leaf:TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_OPEN2[39, 40] chars:[39, 40, "["]
            REFERENCE_LINK_REFERENCE[40, 43] chars:[40, 43, "ref"]
              Leaf:TABLE_CELL_RODD_CODD[40, 43] chars:[40, 43, "ref"]
            Leaf:TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_CLOSE2[43, 44] chars:[43, 44, "]"]
        Leaf:TABLE_ROW_ODD[44, 45] chars:[44, 45, "|"]
        TABLE_CELL[45, 58] chars:[45, 58, "`code … code`"]
          CODE[45, 51] chars:[45, 51, "`code`"]
            Leaf:CODE_MARKER[45, 46] chars:[45, 46, "`"]
            Leaf:TABLE_CELL_RODD_CEVEN_CODE_TEXT[46, 50] chars:[46, 50, "code"]
            Leaf:CODE_MARKER[50, 51] chars:[50, 51, "`"]
          Leaf:TABLE_CELL_RODD_CEVEN[51, 52] chars:[51, 52, " "]
          CODE[52, 58] chars:[52, 58, "`code`"]
            Leaf:CODE_MARKER[52, 53] chars:[52, 53, "`"]
            Leaf:TABLE_CELL_RODD_CEVEN_CODE_TEXT[53, 57] chars:[53, 57, "code"]
            Leaf:CODE_MARKER[57, 58] chars:[57, 58, "`"]
        Leaf:EOL[58, 59] chars:[58, 59, "\n"]
  PARAGRAPH_BLOCK[59, 79] chars:[59, 79, "table … over\n"]
    TEXT_BLOCK[59, 79] chars:[59, 79, "table … over\n"]
      Leaf:TEXT[59, 78] chars:[59, 78, "table …  over"]
      Leaf:EOL[78, 79] chars:[78, 79, "\n"]
  BLANK_LINE[79, 80] chars:[79, 80, "\n"]
    Leaf:BLANK_LINE[79, 80] chars:[79, 80, "\n"]
  REFERENCE[80, 92] chars:[80, 92, "[ref] … /url\n"]
    Leaf:REFERENCE_TEXT_OPEN[80, 81] chars:[80, 81, "["]
    REFERENCE_TEXT[81, 84] chars:[81, 84, "ref"]
      Leaf:TEXT[81, 84] chars:[81, 84, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[84, 86] chars:[84, 86, "]:"]
    Leaf:WHITESPACE[86, 87] chars:[86, 87, " "]
    REFERENCE_LINK_REF[87, 91] chars:[87, 91, "/url"]
      Leaf:REFERENCE_LINK_REF[87, 91] chars:[87, 91, "/url"]
    Leaf:EOL[91, 92] chars:[91, 92, "\n"]
````````````````````````````````


Column spans are created with repeated | pipes one for each additional column to span

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 3
|Abc|Def
|---|---|
| span ||
.
<table>
  <thead>
    <tr><th>Abc</th><th>Def</th>
    </tr>
  </thead>
  <tbody>
    <tr><td colspan="2">span </td>
    </tr>
  </tbody>
</table>
.
FILE[0, 29] chars:[0, 29, "|Abc| … n ||\n"]
  TABLE[0, 29] chars:[0, 29, "|Abc| … n ||\n"]
    TABLE_HEADER[0, 9] chars:[0, 9, "|Abc|Def\n"]
      TABLE_ROW[0, 9] chars:[0, 9, "|Abc|Def\n"]
        Leaf:TABLE_HDR_ROW_ODD[0, 1] chars:[0, 1, "|"]
        TABLE_CELL[1, 4] chars:[1, 4, "Abc"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[1, 4] chars:[1, 4, "Abc"]
        Leaf:TABLE_HDR_ROW_ODD[4, 5] chars:[4, 5, "|"]
        TABLE_CELL[5, 8] chars:[5, 8, "Def"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[5, 8] chars:[5, 8, "Def"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
    TABLE_SEPARATOR[9, 19] chars:[9, 19, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[9, 10] chars:[9, 10, "|"]
      TABLE_CELL[10, 13] chars:[10, 13, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[10, 13] chars:[10, 13, "---"]
      Leaf:TABLE_SEP_ROW_ODD[13, 14] chars:[13, 14, "|"]
      TABLE_CELL[14, 17] chars:[14, 17, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[14, 17] chars:[14, 17, "---"]
      Leaf:TABLE_SEP_ROW_ODD[17, 18] chars:[17, 18, "|"]
      Leaf:EOL[18, 19] chars:[18, 19, "\n"]
    TABLE_BODY[19, 29] chars:[19, 29, "| span ||\n"]
      TABLE_ROW[19, 29] chars:[19, 29, "| span ||\n"]
        Leaf:TABLE_ROW_ODD[19, 20] chars:[19, 20, "|"]
        TABLE_CELL[20, 26] chars:[20, 26, " span "]
          Leaf:TABLE_CELL_RODD_CODD[20, 26] chars:[20, 26, " span "]
        Leaf:TABLE_ROW_ODD[26, 28] chars:[26, 28, "||"]
        Leaf:EOL[28, 29] chars:[28, 29, "\n"]
````````````````````````````````


Now we try varying the header lines and make sure we get the right output

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 4
|Abc|Def
|Hij|Lmn
|---|---|
| span ||
.
<table>
  <thead>
    <tr><th>Abc</th><th>Def</th>
    </tr>
    <tr><th>Hij</th><th>Lmn</th>
    </tr>
  </thead>
  <tbody>
    <tr><td colspan="2">span </td>
    </tr>
  </tbody>
</table>
.
FILE[0, 38] chars:[0, 38, "|Abc| … n ||\n"]
  TABLE[0, 38] chars:[0, 38, "|Abc| … n ||\n"]
    TABLE_HEADER[0, 18] chars:[0, 18, "|Abc| … |Lmn\n"]
      TABLE_ROW[0, 9] chars:[0, 9, "|Abc|Def\n"]
        Leaf:TABLE_HDR_ROW_ODD[0, 1] chars:[0, 1, "|"]
        TABLE_CELL[1, 4] chars:[1, 4, "Abc"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[1, 4] chars:[1, 4, "Abc"]
        Leaf:TABLE_HDR_ROW_ODD[4, 5] chars:[4, 5, "|"]
        TABLE_CELL[5, 8] chars:[5, 8, "Def"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[5, 8] chars:[5, 8, "Def"]
        Leaf:EOL[8, 9] chars:[8, 9, "\n"]
      TABLE_ROW[9, 18] chars:[9, 18, "|Hij|Lmn\n"]
        Leaf:TABLE_HDR_ROW_EVEN[9, 10] chars:[9, 10, "|"]
        TABLE_CELL[10, 13] chars:[10, 13, "Hij"]
          Leaf:TABLE_HDR_CELL_REVEN_CODD[10, 13] chars:[10, 13, "Hij"]
        Leaf:TABLE_HDR_ROW_EVEN[13, 14] chars:[13, 14, "|"]
        TABLE_CELL[14, 17] chars:[14, 17, "Lmn"]
          Leaf:TABLE_HDR_CELL_REVEN_CEVEN[14, 17] chars:[14, 17, "Lmn"]
        Leaf:EOL[17, 18] chars:[17, 18, "\n"]
    TABLE_SEPARATOR[18, 28] chars:[18, 28, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[18, 19] chars:[18, 19, "|"]
      TABLE_CELL[19, 22] chars:[19, 22, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[19, 22] chars:[19, 22, "---"]
      Leaf:TABLE_SEP_ROW_ODD[22, 23] chars:[22, 23, "|"]
      TABLE_CELL[23, 26] chars:[23, 26, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[23, 26] chars:[23, 26, "---"]
      Leaf:TABLE_SEP_ROW_ODD[26, 27] chars:[26, 27, "|"]
      Leaf:EOL[27, 28] chars:[27, 28, "\n"]
    TABLE_BODY[28, 38] chars:[28, 38, "| span ||\n"]
      TABLE_ROW[28, 38] chars:[28, 38, "| span ||\n"]
        Leaf:TABLE_ROW_ODD[28, 29] chars:[28, 29, "|"]
        TABLE_CELL[29, 35] chars:[29, 35, " span "]
          Leaf:TABLE_CELL_RODD_CODD[29, 35] chars:[29, 35, " span "]
        Leaf:TABLE_ROW_ODD[35, 37] chars:[35, 37, "||"]
        Leaf:EOL[37, 38] chars:[37, 38, "\n"]
````````````````````````````````


No header lines

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 5
|---|---|
| col1 | col2|
.
<table>
  <tbody>
    <tr><td>col1 </td><td>col2</td>
    </tr>
  </tbody>
</table>
.
FILE[0, 25] chars:[0, 25, "|---| … ol2|\n"]
  TABLE[0, 25] chars:[0, 25, "|---| … ol2|\n"]
    TABLE_HEADER[0, 0]
    TABLE_SEPARATOR[0, 10] chars:[0, 10, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[0, 1] chars:[0, 1, "|"]
      TABLE_CELL[1, 4] chars:[1, 4, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[1, 4] chars:[1, 4, "---"]
      Leaf:TABLE_SEP_ROW_ODD[4, 5] chars:[4, 5, "|"]
      TABLE_CELL[5, 8] chars:[5, 8, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[5, 8] chars:[5, 8, "---"]
      Leaf:TABLE_SEP_ROW_ODD[8, 9] chars:[8, 9, "|"]
      Leaf:EOL[9, 10] chars:[9, 10, "\n"]
    TABLE_BODY[10, 25] chars:[10, 25, "| col … ol2|\n"]
      TABLE_ROW[10, 25] chars:[10, 25, "| col … ol2|\n"]
        Leaf:TABLE_ROW_ODD[10, 11] chars:[10, 11, "|"]
        TABLE_CELL[11, 17] chars:[11, 17, " col1 "]
          Leaf:TABLE_CELL_RODD_CODD[11, 17] chars:[11, 17, " col1 "]
        Leaf:TABLE_ROW_ODD[17, 18] chars:[17, 18, "|"]
        TABLE_CELL[18, 23] chars:[18, 23, " col2"]
          Leaf:TABLE_CELL_RODD_CEVEN[18, 23] chars:[18, 23, " col2"]
        Leaf:TABLE_ROW_ODD[23, 24] chars:[23, 24, "|"]
        Leaf:EOL[24, 25] chars:[24, 25, "\n"]
````````````````````````````````


No body lines

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 6
| col1 | col2|
|---|---|
.
<table>
  <thead>
    <tr><th>col1 </th><th>col2</th>
    </tr>
  </thead>
</table>
.
FILE[0, 25] chars:[0, 25, "| col … ---|\n"]
  TABLE[0, 25] chars:[0, 25, "| col … ---|\n"]
    TABLE_HEADER[0, 15] chars:[0, 15, "| col … ol2|\n"]
      TABLE_ROW[0, 15] chars:[0, 15, "| col … ol2|\n"]
        Leaf:TABLE_HDR_ROW_ODD[0, 1] chars:[0, 1, "|"]
        TABLE_CELL[1, 7] chars:[1, 7, " col1 "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[1, 7] chars:[1, 7, " col1 "]
        Leaf:TABLE_HDR_ROW_ODD[7, 8] chars:[7, 8, "|"]
        TABLE_CELL[8, 13] chars:[8, 13, " col2"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[8, 13] chars:[8, 13, " col2"]
        Leaf:TABLE_HDR_ROW_ODD[13, 14] chars:[13, 14, "|"]
        Leaf:EOL[14, 15] chars:[14, 15, "\n"]
    TABLE_SEPARATOR[15, 25] chars:[15, 25, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[15, 16] chars:[15, 16, "|"]
      TABLE_CELL[16, 19] chars:[16, 19, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[16, 19] chars:[16, 19, "---"]
      Leaf:TABLE_SEP_ROW_ODD[19, 20] chars:[19, 20, "|"]
      TABLE_CELL[20, 23] chars:[20, 23, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[20, 23] chars:[20, 23, "---"]
      Leaf:TABLE_SEP_ROW_ODD[23, 24] chars:[23, 24, "|"]
      Leaf:EOL[24, 25] chars:[24, 25, "\n"]
````````````````````````````````


multiple tables parsed correctly

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 7
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
    <tr><th>col1 </th><th>col2</th>
    </tr>
  </thead>
</table>
<table>
  <thead>
    <tr><th>col1 </th><th>col2</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>data1 </td><td>data2</td>
    </tr>
  </tbody>
</table>
<p>not a table, followed by a table</p>
<table>
  <thead>
    <tr><th>col11 </th><th>col12</th>
    </tr>
    <tr><th>col21 </th><th>col22</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>data1 </td><td>data2</td>
    </tr>
  </tbody>
</table>
.
FILE[0, 199] chars:[0, 199, "not a … a2|\n\n"]
  PARAGRAPH_BLOCK[0, 33] chars:[0, 33, "not a … able\n"]
    TEXT_BLOCK[0, 33] chars:[0, 33, "not a … able\n"]
      Leaf:TEXT[0, 32] chars:[0, 32, "not a … table"]
      Leaf:EOL[32, 33] chars:[32, 33, "\n"]
  BLANK_LINE[33, 34] chars:[33, 34, "\n"]
    Leaf:BLANK_LINE[33, 34] chars:[33, 34, "\n"]
  TABLE[34, 59] chars:[34, 59, "| col … ---|\n"]
    TABLE_HEADER[34, 49] chars:[34, 49, "| col … ol2|\n"]
      TABLE_ROW[34, 49] chars:[34, 49, "| col … ol2|\n"]
        Leaf:TABLE_HDR_ROW_ODD[34, 35] chars:[34, 35, "|"]
        TABLE_CELL[35, 41] chars:[35, 41, " col1 "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[35, 41] chars:[35, 41, " col1 "]
        Leaf:TABLE_HDR_ROW_ODD[41, 42] chars:[41, 42, "|"]
        TABLE_CELL[42, 47] chars:[42, 47, " col2"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[42, 47] chars:[42, 47, " col2"]
        Leaf:TABLE_HDR_ROW_ODD[47, 48] chars:[47, 48, "|"]
        Leaf:EOL[48, 49] chars:[48, 49, "\n"]
    TABLE_SEPARATOR[49, 59] chars:[49, 59, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[49, 50] chars:[49, 50, "|"]
      TABLE_CELL[50, 53] chars:[50, 53, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[50, 53] chars:[50, 53, "---"]
      Leaf:TABLE_SEP_ROW_ODD[53, 54] chars:[53, 54, "|"]
      TABLE_CELL[54, 57] chars:[54, 57, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[54, 57] chars:[54, 57, "---"]
      Leaf:TABLE_SEP_ROW_ODD[57, 58] chars:[57, 58, "|"]
      Leaf:EOL[58, 59] chars:[58, 59, "\n"]
  BLANK_LINE[59, 60] chars:[59, 60, "\n"]
    Leaf:BLANK_LINE[59, 60] chars:[59, 60, "\n"]
  TABLE[60, 102] chars:[60, 102, "| col … ta2|\n"]
    TABLE_HEADER[60, 75] chars:[60, 75, "| col … ol2|\n"]
      TABLE_ROW[60, 75] chars:[60, 75, "| col … ol2|\n"]
        Leaf:TABLE_HDR_ROW_ODD[60, 61] chars:[60, 61, "|"]
        TABLE_CELL[61, 67] chars:[61, 67, " col1 "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[61, 67] chars:[61, 67, " col1 "]
        Leaf:TABLE_HDR_ROW_ODD[67, 68] chars:[67, 68, "|"]
        TABLE_CELL[68, 73] chars:[68, 73, " col2"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[68, 73] chars:[68, 73, " col2"]
        Leaf:TABLE_HDR_ROW_ODD[73, 74] chars:[73, 74, "|"]
        Leaf:EOL[74, 75] chars:[74, 75, "\n"]
    TABLE_SEPARATOR[75, 85] chars:[75, 85, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[75, 76] chars:[75, 76, "|"]
      TABLE_CELL[76, 79] chars:[76, 79, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[76, 79] chars:[76, 79, "---"]
      Leaf:TABLE_SEP_ROW_ODD[79, 80] chars:[79, 80, "|"]
      TABLE_CELL[80, 83] chars:[80, 83, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[80, 83] chars:[80, 83, "---"]
      Leaf:TABLE_SEP_ROW_ODD[83, 84] chars:[83, 84, "|"]
      Leaf:EOL[84, 85] chars:[84, 85, "\n"]
    TABLE_BODY[85, 102] chars:[85, 102, "| dat … ta2|\n"]
      TABLE_ROW[85, 102] chars:[85, 102, "| dat … ta2|\n"]
        Leaf:TABLE_ROW_ODD[85, 86] chars:[85, 86, "|"]
        TABLE_CELL[86, 93] chars:[86, 93, " data1 "]
          Leaf:TABLE_CELL_RODD_CODD[86, 93] chars:[86, 93, " data1 "]
        Leaf:TABLE_ROW_ODD[93, 94] chars:[93, 94, "|"]
        TABLE_CELL[94, 100] chars:[94, 100, " data2"]
          Leaf:TABLE_CELL_RODD_CEVEN[94, 100] chars:[94, 100, " data2"]
        Leaf:TABLE_ROW_ODD[100, 101] chars:[100, 101, "|"]
        Leaf:EOL[101, 102] chars:[101, 102, "\n"]
  BLANK_LINE[102, 103] chars:[102, 103, "\n"]
    Leaf:BLANK_LINE[102, 103] chars:[102, 103, "\n"]
  PARAGRAPH_BLOCK[103, 136] chars:[103, 136, "not a … able\n"]
    TEXT_BLOCK[103, 136] chars:[103, 136, "not a … able\n"]
      Leaf:TEXT[103, 135] chars:[103, 135, "not a … table"]
      Leaf:EOL[135, 136] chars:[135, 136, "\n"]
  BLANK_LINE[136, 137] chars:[136, 137, "\n"]
    Leaf:BLANK_LINE[136, 137] chars:[136, 137, "\n"]
  TABLE[137, 198] chars:[137, 198, "| col … ta2|\n"]
    TABLE_HEADER[137, 171] chars:[137, 171, "| col … l22|\n"]
      TABLE_ROW[137, 154] chars:[137, 154, "| col … l12|\n"]
        Leaf:TABLE_HDR_ROW_ODD[137, 138] chars:[137, 138, "|"]
        TABLE_CELL[138, 145] chars:[138, 145, " col11 "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[138, 145] chars:[138, 145, " col11 "]
        Leaf:TABLE_HDR_ROW_ODD[145, 146] chars:[145, 146, "|"]
        TABLE_CELL[146, 152] chars:[146, 152, " col12"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[146, 152] chars:[146, 152, " col12"]
        Leaf:TABLE_HDR_ROW_ODD[152, 153] chars:[152, 153, "|"]
        Leaf:EOL[153, 154] chars:[153, 154, "\n"]
      TABLE_ROW[154, 171] chars:[154, 171, "| col … l22|\n"]
        Leaf:TABLE_HDR_ROW_EVEN[154, 155] chars:[154, 155, "|"]
        TABLE_CELL[155, 162] chars:[155, 162, " col21 "]
          Leaf:TABLE_HDR_CELL_REVEN_CODD[155, 162] chars:[155, 162, " col21 "]
        Leaf:TABLE_HDR_ROW_EVEN[162, 163] chars:[162, 163, "|"]
        TABLE_CELL[163, 169] chars:[163, 169, " col22"]
          Leaf:TABLE_HDR_CELL_REVEN_CEVEN[163, 169] chars:[163, 169, " col22"]
        Leaf:TABLE_HDR_ROW_EVEN[169, 170] chars:[169, 170, "|"]
        Leaf:EOL[170, 171] chars:[170, 171, "\n"]
    TABLE_SEPARATOR[171, 181] chars:[171, 181, "|---|---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[171, 172] chars:[171, 172, "|"]
      TABLE_CELL[172, 175] chars:[172, 175, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[172, 175] chars:[172, 175, "---"]
      Leaf:TABLE_SEP_ROW_ODD[175, 176] chars:[175, 176, "|"]
      TABLE_CELL[176, 179] chars:[176, 179, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[176, 179] chars:[176, 179, "---"]
      Leaf:TABLE_SEP_ROW_ODD[179, 180] chars:[179, 180, "|"]
      Leaf:EOL[180, 181] chars:[180, 181, "\n"]
    TABLE_BODY[181, 198] chars:[181, 198, "| dat … ta2|\n"]
      TABLE_ROW[181, 198] chars:[181, 198, "| dat … ta2|\n"]
        Leaf:TABLE_ROW_ODD[181, 182] chars:[181, 182, "|"]
        TABLE_CELL[182, 189] chars:[182, 189, " data1 "]
          Leaf:TABLE_CELL_RODD_CODD[182, 189] chars:[182, 189, " data1 "]
        Leaf:TABLE_ROW_ODD[189, 190] chars:[189, 190, "|"]
        TABLE_CELL[190, 196] chars:[190, 196, " data2"]
          Leaf:TABLE_CELL_RODD_CEVEN[190, 196] chars:[190, 196, " data2"]
        Leaf:TABLE_ROW_ODD[196, 197] chars:[196, 197, "|"]
        Leaf:EOL[197, 198] chars:[197, 198, "\n"]
  BLANK_LINE[198, 199] chars:[198, 199, "\n"]
    Leaf:BLANK_LINE[198, 199] chars:[198, 199, "\n"]
````````````````````````````````


multi row/column

```````````````````````````````` example Markdown elements - Markdown elements - TableBlock: 8
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
    <tr><th>col11 </th><th>col12</th><th>col13</th>
    </tr>
    <tr><th>col21 </th><th>col22</th><th>col23</th>
    </tr>
    <tr><th>col31 </th><th>col32</th><th>col33</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>data11 </td><td>data12</td><td>data13</td>
    </tr>
    <tr><td>data21 </td><td>data22</td><td>data23</td>
    </tr>
    <tr><td>data31 </td><td>data32</td><td>data33</td>
    </tr>
  </tbody>
</table>
.
FILE[0, 168] chars:[0, 168, "| col … 33|\n\n"]
  TABLE[0, 167] chars:[0, 167, "| col … a33|\n"]
    TABLE_HEADER[0, 72] chars:[0, 72, "| col … l33|\n"]
      TABLE_ROW[0, 24] chars:[0, 24, "| col … l13|\n"]
        Leaf:TABLE_HDR_ROW_ODD[0, 1] chars:[0, 1, "|"]
        TABLE_CELL[1, 8] chars:[1, 8, " col11 "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[1, 8] chars:[1, 8, " col11 "]
        Leaf:TABLE_HDR_ROW_ODD[8, 9] chars:[8, 9, "|"]
        TABLE_CELL[9, 15] chars:[9, 15, " col12"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[9, 15] chars:[9, 15, " col12"]
        Leaf:TABLE_HDR_ROW_ODD[15, 16] chars:[15, 16, "|"]
        TABLE_CELL[16, 22] chars:[16, 22, " col13"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[16, 22] chars:[16, 22, " col13"]
        Leaf:TABLE_HDR_ROW_ODD[22, 23] chars:[22, 23, "|"]
        Leaf:EOL[23, 24] chars:[23, 24, "\n"]
      TABLE_ROW[24, 48] chars:[24, 48, "| col … l23|\n"]
        Leaf:TABLE_HDR_ROW_EVEN[24, 25] chars:[24, 25, "|"]
        TABLE_CELL[25, 32] chars:[25, 32, " col21 "]
          Leaf:TABLE_HDR_CELL_REVEN_CODD[25, 32] chars:[25, 32, " col21 "]
        Leaf:TABLE_HDR_ROW_EVEN[32, 33] chars:[32, 33, "|"]
        TABLE_CELL[33, 39] chars:[33, 39, " col22"]
          Leaf:TABLE_HDR_CELL_REVEN_CEVEN[33, 39] chars:[33, 39, " col22"]
        Leaf:TABLE_HDR_ROW_EVEN[39, 40] chars:[39, 40, "|"]
        TABLE_CELL[40, 46] chars:[40, 46, " col23"]
          Leaf:TABLE_HDR_CELL_REVEN_CODD[40, 46] chars:[40, 46, " col23"]
        Leaf:TABLE_HDR_ROW_EVEN[46, 47] chars:[46, 47, "|"]
        Leaf:EOL[47, 48] chars:[47, 48, "\n"]
      TABLE_ROW[48, 72] chars:[48, 72, "| col … l33|\n"]
        Leaf:TABLE_HDR_ROW_ODD[48, 49] chars:[48, 49, "|"]
        TABLE_CELL[49, 56] chars:[49, 56, " col31 "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[49, 56] chars:[49, 56, " col31 "]
        Leaf:TABLE_HDR_ROW_ODD[56, 57] chars:[56, 57, "|"]
        TABLE_CELL[57, 63] chars:[57, 63, " col32"]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[57, 63] chars:[57, 63, " col32"]
        Leaf:TABLE_HDR_ROW_ODD[63, 64] chars:[63, 64, "|"]
        TABLE_CELL[64, 70] chars:[64, 70, " col33"]
          Leaf:TABLE_HDR_CELL_RODD_CODD[64, 70] chars:[64, 70, " col33"]
        Leaf:TABLE_HDR_ROW_ODD[70, 71] chars:[70, 71, "|"]
        Leaf:EOL[71, 72] chars:[71, 72, "\n"]
    TABLE_SEPARATOR[72, 86] chars:[72, 86, "|---| … ---|\n"]
      Leaf:TABLE_SEP_ROW_ODD[72, 73] chars:[72, 73, "|"]
      TABLE_CELL[73, 76] chars:[73, 76, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[73, 76] chars:[73, 76, "---"]
      Leaf:TABLE_SEP_ROW_ODD[76, 77] chars:[76, 77, "|"]
      TABLE_CELL[77, 80] chars:[77, 80, "---"]
        Leaf:TABLE_SEP_COLUMN_EVEN[77, 80] chars:[77, 80, "---"]
      Leaf:TABLE_SEP_ROW_ODD[80, 81] chars:[80, 81, "|"]
      TABLE_CELL[81, 84] chars:[81, 84, "---"]
        Leaf:TABLE_SEP_COLUMN_ODD[81, 84] chars:[81, 84, "---"]
      Leaf:TABLE_SEP_ROW_ODD[84, 85] chars:[84, 85, "|"]
      Leaf:EOL[85, 86] chars:[85, 86, "\n"]
    TABLE_BODY[86, 167] chars:[86, 167, "| dat … a33|\n"]
      TABLE_ROW[86, 113] chars:[86, 113, "| dat … a13|\n"]
        Leaf:TABLE_ROW_ODD[86, 87] chars:[86, 87, "|"]
        TABLE_CELL[87, 95] chars:[87, 95, " data11 "]
          Leaf:TABLE_CELL_RODD_CODD[87, 95] chars:[87, 95, " data11 "]
        Leaf:TABLE_ROW_ODD[95, 96] chars:[95, 96, "|"]
        TABLE_CELL[96, 103] chars:[96, 103, " data12"]
          Leaf:TABLE_CELL_RODD_CEVEN[96, 103] chars:[96, 103, " data12"]
        Leaf:TABLE_ROW_ODD[103, 104] chars:[103, 104, "|"]
        TABLE_CELL[104, 111] chars:[104, 111, " data13"]
          Leaf:TABLE_CELL_RODD_CODD[104, 111] chars:[104, 111, " data13"]
        Leaf:TABLE_ROW_ODD[111, 112] chars:[111, 112, "|"]
        Leaf:EOL[112, 113] chars:[112, 113, "\n"]
      TABLE_ROW[113, 140] chars:[113, 140, "| dat … a23|\n"]
        Leaf:TABLE_ROW_EVEN[113, 114] chars:[113, 114, "|"]
        TABLE_CELL[114, 122] chars:[114, 122, " data21 "]
          Leaf:TABLE_CELL_REVEN_CODD[114, 122] chars:[114, 122, " data21 "]
        Leaf:TABLE_ROW_EVEN[122, 123] chars:[122, 123, "|"]
        TABLE_CELL[123, 130] chars:[123, 130, " data22"]
          Leaf:TABLE_CELL_REVEN_CEVEN[123, 130] chars:[123, 130, " data22"]
        Leaf:TABLE_ROW_EVEN[130, 131] chars:[130, 131, "|"]
        TABLE_CELL[131, 138] chars:[131, 138, " data23"]
          Leaf:TABLE_CELL_REVEN_CODD[131, 138] chars:[131, 138, " data23"]
        Leaf:TABLE_ROW_EVEN[138, 139] chars:[138, 139, "|"]
        Leaf:EOL[139, 140] chars:[139, 140, "\n"]
      TABLE_ROW[140, 167] chars:[140, 167, "| dat … a33|\n"]
        Leaf:TABLE_ROW_ODD[140, 141] chars:[140, 141, "|"]
        TABLE_CELL[141, 149] chars:[141, 149, " data31 "]
          Leaf:TABLE_CELL_RODD_CODD[141, 149] chars:[141, 149, " data31 "]
        Leaf:TABLE_ROW_ODD[149, 150] chars:[149, 150, "|"]
        TABLE_CELL[150, 157] chars:[150, 157, " data32"]
          Leaf:TABLE_CELL_RODD_CEVEN[150, 157] chars:[150, 157, " data32"]
        Leaf:TABLE_ROW_ODD[157, 158] chars:[157, 158, "|"]
        TABLE_CELL[158, 165] chars:[158, 165, " data33"]
          Leaf:TABLE_CELL_RODD_CODD[158, 165] chars:[158, 165, " data33"]
        Leaf:TABLE_ROW_ODD[165, 166] chars:[165, 166, "|"]
        Leaf:EOL[166, 167] chars:[166, 167, "\n"]
  BLANK_LINE[167, 168] chars:[167, 168, "\n"]
    Leaf:BLANK_LINE[167, 168] chars:[167, 168, "\n"]
````````````````````````````````


real life table

```````````````````````````````` example(Markdown elements - Markdown elements - TableBlock: 9) options(pegdown-fail)
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
    <tr><th align="left">Feature                                                                                                                 </th><th align="center">Basic </th><th align="center">Enhanced </th>
    </tr>
  </thead>
  <tbody>
    <tr><td align="left">Works with builds 143.2370 or newer, product version IDEA 15.0.6                                                        </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Preview Tab so you can see what the rendered markdown will look like on GitHub.                                         </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Syntax highlighting                                                                                                     </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Table syntax highlighting stripes rows and columns                                                                      </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Support for Default and Darcula color schemes for preview tab                                                           </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Warning and Error Annotations to help you validate wiki link errors                                                     </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Link address completion for wiki links                                                                                  </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Quick Fixes for detected wiki link errors                                                                               </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">GFM Task list extension <code>* [ ]</code> open task item and <code>* [x]</code> completed task item                                          </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Line markers, Find usages, Go To Declaration for rapid navigation to wiki link targets                                  </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Markdown extensions configuration to customize markdown dialects                                                        </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">GitHub wiki support makes maintaining GitHub wiki pages easier.                                                         </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">GitHub compatible id generation for headers so you can validate your anchor references                                  </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Swing and JavaFX WebView based preview.                                                                                 </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Supports <strong>JavaFX with JetBrains JRE on OS X</strong>                                                                          </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Supports Highlight JS in WebView preview                                                                                </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left"><strong>Multi-line Image URLs for embedding [gravizo.com] UML diagrams into markdown</strong>                                        </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">Live Templates for common markdown elements                                                                             </td><td align="center">X   </td><td align="center">X     </td>
    </tr>
    <tr><td align="left"><strong>Enhanced Version Benefits</strong>                                                                                           </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Split Editor with Preview or HTML Text modes to view both source and preview                    </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Toolbar for fast access to frequent operations                                                  </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Language Injections for fenced code, HTML, Jekyll front matter and multi-line URL content       </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Code completions, refactoring, annotations and quick fixes to let you work faster               </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Navigation support with Line markers, Find usages, Go To Declaration for rapid navigation       </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Inspections to help you validate links, anchor refs, footnote refs                              </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Complete GitHub wiki support for all links makes maintaining GitHub wiki pages a breeze         </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Jekyll front matter recognition in markdown documents                                           </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Emoji text to icon conversion using [Emoji Cheat Sheet] or GitHub emoji URLs                    </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Wrap on typing and table formatting with column alignment                                       </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Character display width used for wrapping and table formatting                                  </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Structure view for Abbreviations, Headers, Tables, Footnotes, References and Document           </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Document formatting with text wrapping, list renumbering, aranging of elements, etc.            </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Table of Contents generation for any markdown parser, with many style options                   </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left"><strong>As you type automation</strong>                                                                                              </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Double of bold/emphasis markers and remove inserted ones if a space is typed                    </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Wrap text blocks to margins and indentation                                                     </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;ATX headers to match trailing <code>#</code> marker                                                        </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Setext headers to match marker length to text                                                   </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Format tables to pad column width, column alignment and spanning columns                        </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Auto insert empty table row on <kbd>ENTER</kbd>                                                 </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty table row/column on <kbd>BACKSPACE</kbd>                                      </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Auto insert table column when typing before first column or after last column of table          </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Actions to insert: table, row or column; delete: row or column                                  </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Auto insert list item on <kbd>ENTER</kbd>                                                       </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>ENTER</kbd>                                                 </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Auto delete empty list item on <kbd>BACKSPACE</kbd>                                             </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Indent or un-indent list item toolbar buttons and actions                                       </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left"><strong>Code Completions</strong>                                                                                                    </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Absolute link address completions using https:// and file:// formats                            </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Explicit and Image links are GitHub wiki aware                                                  </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;GitHub Issue # Completions after <code>issues/</code> link address and in text                             </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;GitHub special links: Issues, Pull requests, Graphs, and Pulse.                                 </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Link address completions for non-markdown files                                                 </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Emoji text shortcuts completion                                                                 </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Java class, field and method completions in inline code elements                                </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left"><strong>Intention Actions</strong>                                                                                                   </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Change between relative and absolute https:// link addresses via intention action               </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Change between wiki links and explicit link                                                     </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Intentions for links, wiki links, references and headers                                        </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Intention to format Setext Header marker to match marker length to text                         </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Intention to swap Setext/Atx header format                                                      </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Update table of contents quick fix intention                                                    </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Intention to edit Table of Contents style options dialog with preview                           </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left"><strong>Refactoring</strong>                                                                                                         </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Automatic change from wiki link to explicit link when link target file is moved out of the wiki </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;File move refactoring of contained links. This completes the refactoring feature set            </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Refactoring for /, https:// and file:// absolute link addresses to project files                </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Refactoring of header text with update to referencing anchor link references                    </td><td align="center">       </td><td align="center">X     </td>
    </tr>
    <tr><td align="left">&nbsp;&nbsp;&nbsp;&nbsp;Anchor link reference refactoring with update to referenced header text                         </td><td align="center">       </td><td align="center">X     </td>
    </tr>
  </tbody>
</table>
.
FILE[0, 10152] chars:[0, 10152, "| Fea …     |"]
  TABLE[0, 10152] chars:[0, 10152, "| Fea …     |"]
    TABLE_HEADER[0, 143] chars:[0, 143, "| Fea … ed |\n"]
      TABLE_ROW[0, 143] chars:[0, 143, "| Fea … ed |\n"]
        Leaf:TABLE_HDR_ROW_ODD[0, 1] chars:[0, 1, "|"]
        TABLE_CELL[1, 122] chars:[1, 122, " Feat …      "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[1, 122] chars:[1, 122, " Feat …      "]
        Leaf:TABLE_HDR_ROW_ODD[122, 123] chars:[122, 123, "|"]
        TABLE_CELL[123, 130] chars:[123, 130, " Basic "]
          Leaf:TABLE_HDR_CELL_RODD_CEVEN[123, 130] chars:[123, 130, " Basic "]
        Leaf:TABLE_HDR_ROW_ODD[130, 131] chars:[130, 131, "|"]
        TABLE_CELL[131, 141] chars:[131, 141, " Enhanced "]
          Leaf:TABLE_HDR_CELL_RODD_CODD[131, 141] chars:[131, 141, " Enhanced "]
        Leaf:TABLE_HDR_ROW_ODD[141, 142] chars:[141, 142, "|"]
        Leaf:EOL[142, 143] chars:[142, 143, "\n"]
    TABLE_SEPARATOR[143, 286] chars:[143, 286, "|:--- … --:|\n"]
      Leaf:TABLE_SEP_ROW_ODD[143, 144] chars:[143, 144, "|"]
      TABLE_CELL[144, 265] chars:[144, 265, ":---- … -----"]
        Leaf:TABLE_SEP_COLUMN_ODD[144, 265] chars:[144, 265, ":---- … -----"]
      Leaf:TABLE_SEP_ROW_ODD[265, 266] chars:[265, 266, "|"]
      TABLE_CELL[266, 273] chars:[266, 273, ":-----:"]
        Leaf:TABLE_SEP_COLUMN_EVEN[266, 273] chars:[266, 273, ":-----:"]
      Leaf:TABLE_SEP_ROW_ODD[273, 274] chars:[273, 274, "|"]
      TABLE_CELL[274, 284] chars:[274, 284, ":--------:"]
        Leaf:TABLE_SEP_COLUMN_ODD[274, 284] chars:[274, 284, ":--------:"]
      Leaf:TABLE_SEP_ROW_ODD[284, 285] chars:[284, 285, "|"]
      Leaf:EOL[285, 286] chars:[285, 286, "\n"]
    TABLE_BODY[286, 10152] chars:[286, 10152, "| Wor …     |"]
      TABLE_ROW[286, 429] chars:[286, 429, "| Wor …    |\n"]
        Leaf:TABLE_ROW_ODD[286, 287] chars:[286, 287, "|"]
        TABLE_CELL[287, 408] chars:[287, 408, " Work …      "]
          Leaf:TABLE_CELL_RODD_CODD[287, 408] chars:[287, 408, " Work …      "]
        Leaf:TABLE_ROW_ODD[408, 409] chars:[408, 409, "|"]
        TABLE_CELL[409, 416] chars:[409, 416, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[409, 416] chars:[409, 416, "   X   "]
        Leaf:TABLE_ROW_ODD[416, 417] chars:[416, 417, "|"]
        TABLE_CELL[417, 427] chars:[417, 427, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[417, 427] chars:[417, 427, "    X     "]
        Leaf:TABLE_ROW_ODD[427, 428] chars:[427, 428, "|"]
        Leaf:EOL[428, 429] chars:[428, 429, "\n"]
      TABLE_ROW[429, 572] chars:[429, 572, "| Pre …    |\n"]
        Leaf:TABLE_ROW_EVEN[429, 430] chars:[429, 430, "|"]
        TABLE_CELL[430, 551] chars:[430, 551, " Prev …      "]
          Leaf:TABLE_CELL_REVEN_CODD[430, 551] chars:[430, 551, " Prev …      "]
        Leaf:TABLE_ROW_EVEN[551, 552] chars:[551, 552, "|"]
        TABLE_CELL[552, 559] chars:[552, 559, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[552, 559] chars:[552, 559, "   X   "]
        Leaf:TABLE_ROW_EVEN[559, 560] chars:[559, 560, "|"]
        TABLE_CELL[560, 570] chars:[560, 570, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[560, 570] chars:[560, 570, "    X     "]
        Leaf:TABLE_ROW_EVEN[570, 571] chars:[570, 571, "|"]
        Leaf:EOL[571, 572] chars:[571, 572, "\n"]
      TABLE_ROW[572, 715] chars:[572, 715, "| Syn …    |\n"]
        Leaf:TABLE_ROW_ODD[572, 573] chars:[572, 573, "|"]
        TABLE_CELL[573, 694] chars:[573, 694, " Synt …      "]
          Leaf:TABLE_CELL_RODD_CODD[573, 694] chars:[573, 694, " Synt …      "]
        Leaf:TABLE_ROW_ODD[694, 695] chars:[694, 695, "|"]
        TABLE_CELL[695, 702] chars:[695, 702, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[695, 702] chars:[695, 702, "   X   "]
        Leaf:TABLE_ROW_ODD[702, 703] chars:[702, 703, "|"]
        TABLE_CELL[703, 713] chars:[703, 713, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[703, 713] chars:[703, 713, "    X     "]
        Leaf:TABLE_ROW_ODD[713, 714] chars:[713, 714, "|"]
        Leaf:EOL[714, 715] chars:[714, 715, "\n"]
      TABLE_ROW[715, 858] chars:[715, 858, "| Tab …    |\n"]
        Leaf:TABLE_ROW_EVEN[715, 716] chars:[715, 716, "|"]
        TABLE_CELL[716, 837] chars:[716, 837, " Tabl …      "]
          Leaf:TABLE_CELL_REVEN_CODD[716, 837] chars:[716, 837, " Tabl …      "]
        Leaf:TABLE_ROW_EVEN[837, 838] chars:[837, 838, "|"]
        TABLE_CELL[838, 845] chars:[838, 845, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[838, 845] chars:[838, 845, "   X   "]
        Leaf:TABLE_ROW_EVEN[845, 846] chars:[845, 846, "|"]
        TABLE_CELL[846, 856] chars:[846, 856, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[846, 856] chars:[846, 856, "    X     "]
        Leaf:TABLE_ROW_EVEN[856, 857] chars:[856, 857, "|"]
        Leaf:EOL[857, 858] chars:[857, 858, "\n"]
      TABLE_ROW[858, 1001] chars:[858, 1001, "| Sup …    |\n"]
        Leaf:TABLE_ROW_ODD[858, 859] chars:[858, 859, "|"]
        TABLE_CELL[859, 980] chars:[859, 980, " Supp …      "]
          Leaf:TABLE_CELL_RODD_CODD[859, 980] chars:[859, 980, " Supp …      "]
        Leaf:TABLE_ROW_ODD[980, 981] chars:[980, 981, "|"]
        TABLE_CELL[981, 988] chars:[981, 988, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[981, 988] chars:[981, 988, "   X   "]
        Leaf:TABLE_ROW_ODD[988, 989] chars:[988, 989, "|"]
        TABLE_CELL[989, 999] chars:[989, 999, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[989, 999] chars:[989, 999, "    X     "]
        Leaf:TABLE_ROW_ODD[999, 1000] chars:[999, 1000, "|"]
        Leaf:EOL[1000, 1001] chars:[1000, 1001, "\n"]
      TABLE_ROW[1001, 1144] chars:[1001, 1144, "| War …    |\n"]
        Leaf:TABLE_ROW_EVEN[1001, 1002] chars:[1001, 1002, "|"]
        TABLE_CELL[1002, 1123] chars:[1002, 1123, " Warn …      "]
          Leaf:TABLE_CELL_REVEN_CODD[1002, 1123] chars:[1002, 1123, " Warn …      "]
        Leaf:TABLE_ROW_EVEN[1123, 1124] chars:[1123, 1124, "|"]
        TABLE_CELL[1124, 1131] chars:[1124, 1131, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[1124, 1131] chars:[1124, 1131, "   X   "]
        Leaf:TABLE_ROW_EVEN[1131, 1132] chars:[1131, 1132, "|"]
        TABLE_CELL[1132, 1142] chars:[1132, 1142, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[1132, 1142] chars:[1132, 1142, "    X     "]
        Leaf:TABLE_ROW_EVEN[1142, 1143] chars:[1142, 1143, "|"]
        Leaf:EOL[1143, 1144] chars:[1143, 1144, "\n"]
      TABLE_ROW[1144, 1287] chars:[1144, 1287, "| Lin …    |\n"]
        Leaf:TABLE_ROW_ODD[1144, 1145] chars:[1144, 1145, "|"]
        TABLE_CELL[1145, 1266] chars:[1145, 1266, " Link …      "]
          Leaf:TABLE_CELL_RODD_CODD[1145, 1266] chars:[1145, 1266, " Link …      "]
        Leaf:TABLE_ROW_ODD[1266, 1267] chars:[1266, 1267, "|"]
        TABLE_CELL[1267, 1274] chars:[1267, 1274, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[1267, 1274] chars:[1267, 1274, "   X   "]
        Leaf:TABLE_ROW_ODD[1274, 1275] chars:[1274, 1275, "|"]
        TABLE_CELL[1275, 1285] chars:[1275, 1285, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[1275, 1285] chars:[1275, 1285, "    X     "]
        Leaf:TABLE_ROW_ODD[1285, 1286] chars:[1285, 1286, "|"]
        Leaf:EOL[1286, 1287] chars:[1286, 1287, "\n"]
      TABLE_ROW[1287, 1430] chars:[1287, 1430, "| Qui …    |\n"]
        Leaf:TABLE_ROW_EVEN[1287, 1288] chars:[1287, 1288, "|"]
        TABLE_CELL[1288, 1409] chars:[1288, 1409, " Quic …      "]
          Leaf:TABLE_CELL_REVEN_CODD[1288, 1409] chars:[1288, 1409, " Quic …      "]
        Leaf:TABLE_ROW_EVEN[1409, 1410] chars:[1409, 1410, "|"]
        TABLE_CELL[1410, 1417] chars:[1410, 1417, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[1410, 1417] chars:[1410, 1417, "   X   "]
        Leaf:TABLE_ROW_EVEN[1417, 1418] chars:[1417, 1418, "|"]
        TABLE_CELL[1418, 1428] chars:[1418, 1428, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[1418, 1428] chars:[1418, 1428, "    X     "]
        Leaf:TABLE_ROW_EVEN[1428, 1429] chars:[1428, 1429, "|"]
        Leaf:EOL[1429, 1430] chars:[1429, 1430, "\n"]
      TABLE_ROW[1430, 1573] chars:[1430, 1573, "| GFM …    |\n"]
        Leaf:TABLE_ROW_ODD[1430, 1431] chars:[1430, 1431, "|"]
        TABLE_CELL[1431, 1552] chars:[1431, 1552, " GFM  …      "]
          Leaf:TABLE_CELL_RODD_CODD[1431, 1456] chars:[1431, 1456, " GFM  … sion "]
          CODE[1456, 1463] chars:[1456, 1463, "`* [ ]`"]
            Leaf:CODE_MARKER[1456, 1457] chars:[1456, 1457, "`"]
            Leaf:TABLE_CELL_RODD_CODD_CODE_TEXT[1457, 1462] chars:[1457, 1462, "* [ ]"]
            Leaf:CODE_MARKER[1462, 1463] chars:[1462, 1463, "`"]
          Leaf:TABLE_CELL_RODD_CODD[1463, 1483] chars:[1463, 1483, " open …  and "]
          CODE[1483, 1490] chars:[1483, 1490, "`* [x]`"]
            Leaf:CODE_MARKER[1483, 1484] chars:[1483, 1484, "`"]
            Leaf:TABLE_CELL_RODD_CODD_CODE_TEXT[1484, 1489] chars:[1484, 1489, "* [x]"]
            Leaf:CODE_MARKER[1489, 1490] chars:[1489, 1490, "`"]
          Leaf:TABLE_CELL_RODD_CODD[1490, 1552] chars:[1490, 1552, " comp …      "]
        Leaf:TABLE_ROW_ODD[1552, 1553] chars:[1552, 1553, "|"]
        TABLE_CELL[1553, 1560] chars:[1553, 1560, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[1553, 1560] chars:[1553, 1560, "   X   "]
        Leaf:TABLE_ROW_ODD[1560, 1561] chars:[1560, 1561, "|"]
        TABLE_CELL[1561, 1571] chars:[1561, 1571, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[1561, 1571] chars:[1561, 1571, "    X     "]
        Leaf:TABLE_ROW_ODD[1571, 1572] chars:[1571, 1572, "|"]
        Leaf:EOL[1572, 1573] chars:[1572, 1573, "\n"]
      TABLE_ROW[1573, 1716] chars:[1573, 1716, "| Lin …    |\n"]
        Leaf:TABLE_ROW_EVEN[1573, 1574] chars:[1573, 1574, "|"]
        TABLE_CELL[1574, 1695] chars:[1574, 1695, " Line …      "]
          Leaf:TABLE_CELL_REVEN_CODD[1574, 1695] chars:[1574, 1695, " Line …      "]
        Leaf:TABLE_ROW_EVEN[1695, 1696] chars:[1695, 1696, "|"]
        TABLE_CELL[1696, 1703] chars:[1696, 1703, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[1696, 1703] chars:[1696, 1703, "   X   "]
        Leaf:TABLE_ROW_EVEN[1703, 1704] chars:[1703, 1704, "|"]
        TABLE_CELL[1704, 1714] chars:[1704, 1714, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[1704, 1714] chars:[1704, 1714, "    X     "]
        Leaf:TABLE_ROW_EVEN[1714, 1715] chars:[1714, 1715, "|"]
        Leaf:EOL[1715, 1716] chars:[1715, 1716, "\n"]
      TABLE_ROW[1716, 1859] chars:[1716, 1859, "| Mar …    |\n"]
        Leaf:TABLE_ROW_ODD[1716, 1717] chars:[1716, 1717, "|"]
        TABLE_CELL[1717, 1838] chars:[1717, 1838, " Mark …      "]
          Leaf:TABLE_CELL_RODD_CODD[1717, 1838] chars:[1717, 1838, " Mark …      "]
        Leaf:TABLE_ROW_ODD[1838, 1839] chars:[1838, 1839, "|"]
        TABLE_CELL[1839, 1846] chars:[1839, 1846, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[1839, 1846] chars:[1839, 1846, "   X   "]
        Leaf:TABLE_ROW_ODD[1846, 1847] chars:[1846, 1847, "|"]
        TABLE_CELL[1847, 1857] chars:[1847, 1857, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[1847, 1857] chars:[1847, 1857, "    X     "]
        Leaf:TABLE_ROW_ODD[1857, 1858] chars:[1857, 1858, "|"]
        Leaf:EOL[1858, 1859] chars:[1858, 1859, "\n"]
      TABLE_ROW[1859, 2002] chars:[1859, 2002, "| Git …    |\n"]
        Leaf:TABLE_ROW_EVEN[1859, 1860] chars:[1859, 1860, "|"]
        TABLE_CELL[1860, 1981] chars:[1860, 1981, " GitH …      "]
          Leaf:TABLE_CELL_REVEN_CODD[1860, 1981] chars:[1860, 1981, " GitH …      "]
        Leaf:TABLE_ROW_EVEN[1981, 1982] chars:[1981, 1982, "|"]
        TABLE_CELL[1982, 1989] chars:[1982, 1989, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[1982, 1989] chars:[1982, 1989, "   X   "]
        Leaf:TABLE_ROW_EVEN[1989, 1990] chars:[1989, 1990, "|"]
        TABLE_CELL[1990, 2000] chars:[1990, 2000, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[1990, 2000] chars:[1990, 2000, "    X     "]
        Leaf:TABLE_ROW_EVEN[2000, 2001] chars:[2000, 2001, "|"]
        Leaf:EOL[2001, 2002] chars:[2001, 2002, "\n"]
      TABLE_ROW[2002, 2145] chars:[2002, 2145, "| Git …    |\n"]
        Leaf:TABLE_ROW_ODD[2002, 2003] chars:[2002, 2003, "|"]
        TABLE_CELL[2003, 2124] chars:[2003, 2124, " GitH …      "]
          Leaf:TABLE_CELL_RODD_CODD[2003, 2124] chars:[2003, 2124, " GitH …      "]
        Leaf:TABLE_ROW_ODD[2124, 2125] chars:[2124, 2125, "|"]
        TABLE_CELL[2125, 2132] chars:[2125, 2132, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[2125, 2132] chars:[2125, 2132, "   X   "]
        Leaf:TABLE_ROW_ODD[2132, 2133] chars:[2132, 2133, "|"]
        TABLE_CELL[2133, 2143] chars:[2133, 2143, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[2133, 2143] chars:[2133, 2143, "    X     "]
        Leaf:TABLE_ROW_ODD[2143, 2144] chars:[2143, 2144, "|"]
        Leaf:EOL[2144, 2145] chars:[2144, 2145, "\n"]
      TABLE_ROW[2145, 2288] chars:[2145, 2288, "| Swi …    |\n"]
        Leaf:TABLE_ROW_EVEN[2145, 2146] chars:[2145, 2146, "|"]
        TABLE_CELL[2146, 2267] chars:[2146, 2267, " Swin …      "]
          Leaf:TABLE_CELL_REVEN_CODD[2146, 2267] chars:[2146, 2267, " Swin …      "]
        Leaf:TABLE_ROW_EVEN[2267, 2268] chars:[2267, 2268, "|"]
        TABLE_CELL[2268, 2275] chars:[2268, 2275, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[2268, 2275] chars:[2268, 2275, "   X   "]
        Leaf:TABLE_ROW_EVEN[2275, 2276] chars:[2275, 2276, "|"]
        TABLE_CELL[2276, 2286] chars:[2276, 2286, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[2276, 2286] chars:[2276, 2286, "    X     "]
        Leaf:TABLE_ROW_EVEN[2286, 2287] chars:[2286, 2287, "|"]
        Leaf:EOL[2287, 2288] chars:[2287, 2288, "\n"]
      TABLE_ROW[2288, 2431] chars:[2288, 2431, "| Sup …    |\n"]
        Leaf:TABLE_ROW_ODD[2288, 2289] chars:[2288, 2289, "|"]
        TABLE_CELL[2289, 2410] chars:[2289, 2410, " Supp …      "]
          Leaf:TABLE_CELL_RODD_CODD[2289, 2299] chars:[2289, 2299, " Supports "]
          BOLD[2299, 2336] chars:[2299, 2336, "**Jav … S X**"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_MARKER[2299, 2301] chars:[2299, 2301, "**"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_TEXT[2301, 2334] chars:[2301, 2334, "JavaF …  OS X"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_MARKER[2334, 2336] chars:[2334, 2336, "**"]
          Leaf:TABLE_CELL_RODD_CODD[2336, 2410] chars:[2336, 2410, "      …      "]
        Leaf:TABLE_ROW_ODD[2410, 2411] chars:[2410, 2411, "|"]
        TABLE_CELL[2411, 2418] chars:[2411, 2418, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[2411, 2418] chars:[2411, 2418, "   X   "]
        Leaf:TABLE_ROW_ODD[2418, 2419] chars:[2418, 2419, "|"]
        TABLE_CELL[2419, 2429] chars:[2419, 2429, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[2419, 2429] chars:[2419, 2429, "    X     "]
        Leaf:TABLE_ROW_ODD[2429, 2430] chars:[2429, 2430, "|"]
        Leaf:EOL[2430, 2431] chars:[2430, 2431, "\n"]
      TABLE_ROW[2431, 2574] chars:[2431, 2574, "| Sup …    |\n"]
        Leaf:TABLE_ROW_EVEN[2431, 2432] chars:[2431, 2432, "|"]
        TABLE_CELL[2432, 2553] chars:[2432, 2553, " Supp …      "]
          Leaf:TABLE_CELL_REVEN_CODD[2432, 2553] chars:[2432, 2553, " Supp …      "]
        Leaf:TABLE_ROW_EVEN[2553, 2554] chars:[2553, 2554, "|"]
        TABLE_CELL[2554, 2561] chars:[2554, 2561, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[2554, 2561] chars:[2554, 2561, "   X   "]
        Leaf:TABLE_ROW_EVEN[2561, 2562] chars:[2561, 2562, "|"]
        TABLE_CELL[2562, 2572] chars:[2562, 2572, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[2562, 2572] chars:[2562, 2572, "    X     "]
        Leaf:TABLE_ROW_EVEN[2572, 2573] chars:[2572, 2573, "|"]
        Leaf:EOL[2573, 2574] chars:[2573, 2574, "\n"]
      TABLE_ROW[2574, 2717] chars:[2574, 2717, "| **M …    |\n"]
        Leaf:TABLE_ROW_ODD[2574, 2575] chars:[2574, 2575, "|"]
        TABLE_CELL[2575, 2696] chars:[2575, 2696, " **Mu …      "]
          Leaf:TABLE_CELL_RODD_CODD[2575, 2576] chars:[2575, 2576, " "]
          BOLD[2576, 2656] chars:[2576, 2656, "**Mul … own**"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_MARKER[2576, 2578] chars:[2576, 2578, "**"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_TEXT[2578, 2614] chars:[2578, 2614, "Multi … ding "]
            REFERENCE_LINK[2614, 2627] chars:[2614, 2627, "[grav … .com]"]
              Leaf:TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_OPEN2[2614, 2615] chars:[2614, 2615, "["]
              REFERENCE_LINK_REFERENCE[2615, 2626] chars:[2615, 2626, "gravi … o.com"]
                Leaf:TABLE_CELL_RODD_CODD_BOLD_TEXT[2615, 2626] chars:[2615, 2626, "gravi … o.com"]
              Leaf:TABLE_CELL_RODD_CODD_REFERENCE_LINK_REFERENCE_CLOSE2[2626, 2627] chars:[2626, 2627, "]"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_TEXT[2627, 2654] chars:[2627, 2654, " UML  … kdown"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_MARKER[2654, 2656] chars:[2654, 2656, "**"]
          Leaf:TABLE_CELL_RODD_CODD[2656, 2696] chars:[2656, 2696, "      …      "]
        Leaf:TABLE_ROW_ODD[2696, 2697] chars:[2696, 2697, "|"]
        TABLE_CELL[2697, 2704] chars:[2697, 2704, "   X   "]
          Leaf:TABLE_CELL_RODD_CEVEN[2697, 2704] chars:[2697, 2704, "   X   "]
        Leaf:TABLE_ROW_ODD[2704, 2705] chars:[2704, 2705, "|"]
        TABLE_CELL[2705, 2715] chars:[2705, 2715, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[2705, 2715] chars:[2705, 2715, "    X     "]
        Leaf:TABLE_ROW_ODD[2715, 2716] chars:[2715, 2716, "|"]
        Leaf:EOL[2716, 2717] chars:[2716, 2717, "\n"]
      TABLE_ROW[2717, 2860] chars:[2717, 2860, "| Liv …    |\n"]
        Leaf:TABLE_ROW_EVEN[2717, 2718] chars:[2717, 2718, "|"]
        TABLE_CELL[2718, 2839] chars:[2718, 2839, " Live …      "]
          Leaf:TABLE_CELL_REVEN_CODD[2718, 2839] chars:[2718, 2839, " Live …      "]
        Leaf:TABLE_ROW_EVEN[2839, 2840] chars:[2839, 2840, "|"]
        TABLE_CELL[2840, 2847] chars:[2840, 2847, "   X   "]
          Leaf:TABLE_CELL_REVEN_CEVEN[2840, 2847] chars:[2840, 2847, "   X   "]
        Leaf:TABLE_ROW_EVEN[2847, 2848] chars:[2847, 2848, "|"]
        TABLE_CELL[2848, 2858] chars:[2848, 2858, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[2848, 2858] chars:[2848, 2858, "    X     "]
        Leaf:TABLE_ROW_EVEN[2858, 2859] chars:[2858, 2859, "|"]
        Leaf:EOL[2859, 2860] chars:[2859, 2860, "\n"]
      TABLE_ROW[2860, 3003] chars:[2860, 3003, "| **E …    |\n"]
        Leaf:TABLE_ROW_ODD[2860, 2861] chars:[2860, 2861, "|"]
        TABLE_CELL[2861, 2982] chars:[2861, 2982, " **En …      "]
          Leaf:TABLE_CELL_RODD_CODD[2861, 2862] chars:[2861, 2862, " "]
          BOLD[2862, 2891] chars:[2862, 2891, "**Enh … its**"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_MARKER[2862, 2864] chars:[2862, 2864, "**"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_TEXT[2864, 2889] chars:[2864, 2889, "Enhan … efits"]
            Leaf:TABLE_CELL_RODD_CODD_BOLD_MARKER[2889, 2891] chars:[2889, 2891, "**"]
          Leaf:TABLE_CELL_RODD_CODD[2891, 2982] chars:[2891, 2982, "      …      "]
        Leaf:TABLE_ROW_ODD[2982, 2983] chars:[2982, 2983, "|"]
        TABLE_CELL[2983, 2990] chars:[2983, 2990, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[2983, 2990] chars:[2983, 2990, "       "]
        Leaf:TABLE_ROW_ODD[2990, 2991] chars:[2990, 2991, "|"]
        TABLE_CELL[2991, 3001] chars:[2991, 3001, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[2991, 3001] chars:[2991, 3001, "    X     "]
        Leaf:TABLE_ROW_ODD[3001, 3002] chars:[3001, 3002, "|"]
        Leaf:EOL[3002, 3003] chars:[3002, 3003, "\n"]
      TABLE_ROW[3003, 3146] chars:[3003, 3146, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[3003, 3004] chars:[3003, 3004, "|"]
        TABLE_CELL[3004, 3125] chars:[3004, 3125, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[3004, 3005] chars:[3004, 3005, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[3005, 3029] chars:[3005, 3029, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[3029, 3125] chars:[3029, 3125, "Split …      "]
        Leaf:TABLE_ROW_EVEN[3125, 3126] chars:[3125, 3126, "|"]
        TABLE_CELL[3126, 3133] chars:[3126, 3133, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[3126, 3133] chars:[3126, 3133, "       "]
        Leaf:TABLE_ROW_EVEN[3133, 3134] chars:[3133, 3134, "|"]
        TABLE_CELL[3134, 3144] chars:[3134, 3144, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[3134, 3144] chars:[3134, 3144, "    X     "]
        Leaf:TABLE_ROW_EVEN[3144, 3145] chars:[3144, 3145, "|"]
        Leaf:EOL[3145, 3146] chars:[3145, 3146, "\n"]
      TABLE_ROW[3146, 3289] chars:[3146, 3289, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[3146, 3147] chars:[3146, 3147, "|"]
        TABLE_CELL[3147, 3268] chars:[3147, 3268, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[3147, 3148] chars:[3147, 3148, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[3148, 3172] chars:[3148, 3172, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[3172, 3268] chars:[3172, 3268, "Toolb …      "]
        Leaf:TABLE_ROW_ODD[3268, 3269] chars:[3268, 3269, "|"]
        TABLE_CELL[3269, 3276] chars:[3269, 3276, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[3269, 3276] chars:[3269, 3276, "       "]
        Leaf:TABLE_ROW_ODD[3276, 3277] chars:[3276, 3277, "|"]
        TABLE_CELL[3277, 3287] chars:[3277, 3287, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[3277, 3287] chars:[3277, 3287, "    X     "]
        Leaf:TABLE_ROW_ODD[3287, 3288] chars:[3287, 3288, "|"]
        Leaf:EOL[3288, 3289] chars:[3288, 3289, "\n"]
      TABLE_ROW[3289, 3432] chars:[3289, 3432, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[3289, 3290] chars:[3289, 3290, "|"]
        TABLE_CELL[3290, 3411] chars:[3290, 3411, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[3290, 3291] chars:[3290, 3291, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[3291, 3315] chars:[3291, 3315, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[3315, 3411] chars:[3315, 3411, "Langu …      "]
        Leaf:TABLE_ROW_EVEN[3411, 3412] chars:[3411, 3412, "|"]
        TABLE_CELL[3412, 3419] chars:[3412, 3419, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[3412, 3419] chars:[3412, 3419, "       "]
        Leaf:TABLE_ROW_EVEN[3419, 3420] chars:[3419, 3420, "|"]
        TABLE_CELL[3420, 3430] chars:[3420, 3430, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[3420, 3430] chars:[3420, 3430, "    X     "]
        Leaf:TABLE_ROW_EVEN[3430, 3431] chars:[3430, 3431, "|"]
        Leaf:EOL[3431, 3432] chars:[3431, 3432, "\n"]
      TABLE_ROW[3432, 3575] chars:[3432, 3575, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[3432, 3433] chars:[3432, 3433, "|"]
        TABLE_CELL[3433, 3554] chars:[3433, 3554, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[3433, 3434] chars:[3433, 3434, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[3434, 3458] chars:[3434, 3458, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[3458, 3554] chars:[3458, 3554, "Code  …      "]
        Leaf:TABLE_ROW_ODD[3554, 3555] chars:[3554, 3555, "|"]
        TABLE_CELL[3555, 3562] chars:[3555, 3562, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[3555, 3562] chars:[3555, 3562, "       "]
        Leaf:TABLE_ROW_ODD[3562, 3563] chars:[3562, 3563, "|"]
        TABLE_CELL[3563, 3573] chars:[3563, 3573, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[3563, 3573] chars:[3563, 3573, "    X     "]
        Leaf:TABLE_ROW_ODD[3573, 3574] chars:[3573, 3574, "|"]
        Leaf:EOL[3574, 3575] chars:[3574, 3575, "\n"]
      TABLE_ROW[3575, 3718] chars:[3575, 3718, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[3575, 3576] chars:[3575, 3576, "|"]
        TABLE_CELL[3576, 3697] chars:[3576, 3697, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[3576, 3577] chars:[3576, 3577, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[3577, 3601] chars:[3577, 3601, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[3601, 3697] chars:[3601, 3697, "Navig …      "]
        Leaf:TABLE_ROW_EVEN[3697, 3698] chars:[3697, 3698, "|"]
        TABLE_CELL[3698, 3705] chars:[3698, 3705, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[3698, 3705] chars:[3698, 3705, "       "]
        Leaf:TABLE_ROW_EVEN[3705, 3706] chars:[3705, 3706, "|"]
        TABLE_CELL[3706, 3716] chars:[3706, 3716, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[3706, 3716] chars:[3706, 3716, "    X     "]
        Leaf:TABLE_ROW_EVEN[3716, 3717] chars:[3716, 3717, "|"]
        Leaf:EOL[3717, 3718] chars:[3717, 3718, "\n"]
      TABLE_ROW[3718, 3861] chars:[3718, 3861, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[3718, 3719] chars:[3718, 3719, "|"]
        TABLE_CELL[3719, 3840] chars:[3719, 3840, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[3719, 3720] chars:[3719, 3720, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[3720, 3744] chars:[3720, 3744, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[3744, 3840] chars:[3744, 3840, "Inspe …      "]
        Leaf:TABLE_ROW_ODD[3840, 3841] chars:[3840, 3841, "|"]
        TABLE_CELL[3841, 3848] chars:[3841, 3848, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[3841, 3848] chars:[3841, 3848, "       "]
        Leaf:TABLE_ROW_ODD[3848, 3849] chars:[3848, 3849, "|"]
        TABLE_CELL[3849, 3859] chars:[3849, 3859, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[3849, 3859] chars:[3849, 3859, "    X     "]
        Leaf:TABLE_ROW_ODD[3859, 3860] chars:[3859, 3860, "|"]
        Leaf:EOL[3860, 3861] chars:[3860, 3861, "\n"]
      TABLE_ROW[3861, 4004] chars:[3861, 4004, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[3861, 3862] chars:[3861, 3862, "|"]
        TABLE_CELL[3862, 3983] chars:[3862, 3983, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[3862, 3863] chars:[3862, 3863, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[3863, 3887] chars:[3863, 3887, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[3887, 3983] chars:[3887, 3983, "Compl …      "]
        Leaf:TABLE_ROW_EVEN[3983, 3984] chars:[3983, 3984, "|"]
        TABLE_CELL[3984, 3991] chars:[3984, 3991, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[3984, 3991] chars:[3984, 3991, "       "]
        Leaf:TABLE_ROW_EVEN[3991, 3992] chars:[3991, 3992, "|"]
        TABLE_CELL[3992, 4002] chars:[3992, 4002, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[3992, 4002] chars:[3992, 4002, "    X     "]
        Leaf:TABLE_ROW_EVEN[4002, 4003] chars:[4002, 4003, "|"]
        Leaf:EOL[4003, 4004] chars:[4003, 4004, "\n"]
      TABLE_ROW[4004, 4147] chars:[4004, 4147, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[4004, 4005] chars:[4004, 4005, "|"]
        TABLE_CELL[4005, 4126] chars:[4005, 4126, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[4005, 4006] chars:[4005, 4006, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[4006, 4030] chars:[4006, 4030, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[4030, 4126] chars:[4030, 4126, "Jekyl …      "]
        Leaf:TABLE_ROW_ODD[4126, 4127] chars:[4126, 4127, "|"]
        TABLE_CELL[4127, 4134] chars:[4127, 4134, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[4127, 4134] chars:[4127, 4134, "       "]
        Leaf:TABLE_ROW_ODD[4134, 4135] chars:[4134, 4135, "|"]
        TABLE_CELL[4135, 4145] chars:[4135, 4145, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[4135, 4145] chars:[4135, 4145, "    X     "]
        Leaf:TABLE_ROW_ODD[4145, 4146] chars:[4145, 4146, "|"]
        Leaf:EOL[4146, 4147] chars:[4146, 4147, "\n"]
      TABLE_ROW[4147, 4290] chars:[4147, 4290, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[4147, 4148] chars:[4147, 4148, "|"]
        TABLE_CELL[4148, 4269] chars:[4148, 4269, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[4148, 4149] chars:[4148, 4149, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[4149, 4173] chars:[4149, 4173, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[4173, 4209] chars:[4173, 4209, "Emoji … sing "]
          REFERENCE_LINK[4209, 4228] chars:[4209, 4228, "[Emoj … heet]"]
            Leaf:TABLE_CELL_REVEN_CODD_REFERENCE_LINK_REFERENCE_OPEN2[4209, 4210] chars:[4209, 4210, "["]
            REFERENCE_LINK_REFERENCE[4210, 4227] chars:[4210, 4227, "Emoji … Sheet"]
              Leaf:TABLE_CELL_REVEN_CODD[4210, 4227] chars:[4210, 4227, "Emoji … Sheet"]
            Leaf:TABLE_CELL_REVEN_CODD_REFERENCE_LINK_REFERENCE_CLOSE2[4227, 4228] chars:[4227, 4228, "]"]
          Leaf:TABLE_CELL_REVEN_CODD[4228, 4269] chars:[4228, 4269, " or G …      "]
        Leaf:TABLE_ROW_EVEN[4269, 4270] chars:[4269, 4270, "|"]
        TABLE_CELL[4270, 4277] chars:[4270, 4277, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[4270, 4277] chars:[4270, 4277, "       "]
        Leaf:TABLE_ROW_EVEN[4277, 4278] chars:[4277, 4278, "|"]
        TABLE_CELL[4278, 4288] chars:[4278, 4288, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[4278, 4288] chars:[4278, 4288, "    X     "]
        Leaf:TABLE_ROW_EVEN[4288, 4289] chars:[4288, 4289, "|"]
        Leaf:EOL[4289, 4290] chars:[4289, 4290, "\n"]
      TABLE_ROW[4290, 4433] chars:[4290, 4433, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[4290, 4291] chars:[4290, 4291, "|"]
        TABLE_CELL[4291, 4412] chars:[4291, 4412, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[4291, 4292] chars:[4291, 4292, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[4292, 4316] chars:[4292, 4316, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[4316, 4412] chars:[4316, 4412, "Wrap  …      "]
        Leaf:TABLE_ROW_ODD[4412, 4413] chars:[4412, 4413, "|"]
        TABLE_CELL[4413, 4420] chars:[4413, 4420, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[4413, 4420] chars:[4413, 4420, "       "]
        Leaf:TABLE_ROW_ODD[4420, 4421] chars:[4420, 4421, "|"]
        TABLE_CELL[4421, 4431] chars:[4421, 4431, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[4421, 4431] chars:[4421, 4431, "    X     "]
        Leaf:TABLE_ROW_ODD[4431, 4432] chars:[4431, 4432, "|"]
        Leaf:EOL[4432, 4433] chars:[4432, 4433, "\n"]
      TABLE_ROW[4433, 4576] chars:[4433, 4576, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[4433, 4434] chars:[4433, 4434, "|"]
        TABLE_CELL[4434, 4555] chars:[4434, 4555, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[4434, 4435] chars:[4434, 4435, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[4435, 4459] chars:[4435, 4459, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[4459, 4555] chars:[4459, 4555, "Chara …      "]
        Leaf:TABLE_ROW_EVEN[4555, 4556] chars:[4555, 4556, "|"]
        TABLE_CELL[4556, 4563] chars:[4556, 4563, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[4556, 4563] chars:[4556, 4563, "       "]
        Leaf:TABLE_ROW_EVEN[4563, 4564] chars:[4563, 4564, "|"]
        TABLE_CELL[4564, 4574] chars:[4564, 4574, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[4564, 4574] chars:[4564, 4574, "    X     "]
        Leaf:TABLE_ROW_EVEN[4574, 4575] chars:[4574, 4575, "|"]
        Leaf:EOL[4575, 4576] chars:[4575, 4576, "\n"]
      TABLE_ROW[4576, 4719] chars:[4576, 4719, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[4576, 4577] chars:[4576, 4577, "|"]
        TABLE_CELL[4577, 4698] chars:[4577, 4698, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[4577, 4578] chars:[4577, 4578, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[4578, 4602] chars:[4578, 4602, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[4602, 4698] chars:[4602, 4698, "Struc …      "]
        Leaf:TABLE_ROW_ODD[4698, 4699] chars:[4698, 4699, "|"]
        TABLE_CELL[4699, 4706] chars:[4699, 4706, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[4699, 4706] chars:[4699, 4706, "       "]
        Leaf:TABLE_ROW_ODD[4706, 4707] chars:[4706, 4707, "|"]
        TABLE_CELL[4707, 4717] chars:[4707, 4717, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[4707, 4717] chars:[4707, 4717, "    X     "]
        Leaf:TABLE_ROW_ODD[4717, 4718] chars:[4717, 4718, "|"]
        Leaf:EOL[4718, 4719] chars:[4718, 4719, "\n"]
      TABLE_ROW[4719, 4862] chars:[4719, 4862, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[4719, 4720] chars:[4719, 4720, "|"]
        TABLE_CELL[4720, 4841] chars:[4720, 4841, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[4720, 4721] chars:[4720, 4721, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[4721, 4745] chars:[4721, 4745, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[4745, 4841] chars:[4745, 4841, "Docum …      "]
        Leaf:TABLE_ROW_EVEN[4841, 4842] chars:[4841, 4842, "|"]
        TABLE_CELL[4842, 4849] chars:[4842, 4849, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[4842, 4849] chars:[4842, 4849, "       "]
        Leaf:TABLE_ROW_EVEN[4849, 4850] chars:[4849, 4850, "|"]
        TABLE_CELL[4850, 4860] chars:[4850, 4860, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[4850, 4860] chars:[4850, 4860, "    X     "]
        Leaf:TABLE_ROW_EVEN[4860, 4861] chars:[4860, 4861, "|"]
        Leaf:EOL[4861, 4862] chars:[4861, 4862, "\n"]
      TABLE_ROW[4862, 5005] chars:[4862, 5005, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[4862, 4863] chars:[4862, 4863, "|"]
        TABLE_CELL[4863, 4984] chars:[4863, 4984, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[4863, 4864] chars:[4863, 4864, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[4864, 4888] chars:[4864, 4888, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[4888, 4984] chars:[4888, 4984, "Table …      "]
        Leaf:TABLE_ROW_ODD[4984, 4985] chars:[4984, 4985, "|"]
        TABLE_CELL[4985, 4992] chars:[4985, 4992, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[4985, 4992] chars:[4985, 4992, "       "]
        Leaf:TABLE_ROW_ODD[4992, 4993] chars:[4992, 4993, "|"]
        TABLE_CELL[4993, 5003] chars:[4993, 5003, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[4993, 5003] chars:[4993, 5003, "    X     "]
        Leaf:TABLE_ROW_ODD[5003, 5004] chars:[5003, 5004, "|"]
        Leaf:EOL[5004, 5005] chars:[5004, 5005, "\n"]
      TABLE_ROW[5005, 5148] chars:[5005, 5148, "| **A …    |\n"]
        Leaf:TABLE_ROW_EVEN[5005, 5006] chars:[5005, 5006, "|"]
        TABLE_CELL[5006, 5127] chars:[5006, 5127, " **As …      "]
          Leaf:TABLE_CELL_REVEN_CODD[5006, 5007] chars:[5006, 5007, " "]
          BOLD[5007, 5033] chars:[5007, 5033, "**As  … ion**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[5007, 5009] chars:[5007, 5009, "**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_TEXT[5009, 5031] chars:[5009, 5031, "As yo … ation"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[5031, 5033] chars:[5031, 5033, "**"]
          Leaf:TABLE_CELL_REVEN_CODD[5033, 5127] chars:[5033, 5127, "      …      "]
        Leaf:TABLE_ROW_EVEN[5127, 5128] chars:[5127, 5128, "|"]
        TABLE_CELL[5128, 5135] chars:[5128, 5135, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[5128, 5135] chars:[5128, 5135, "       "]
        Leaf:TABLE_ROW_EVEN[5135, 5136] chars:[5135, 5136, "|"]
        TABLE_CELL[5136, 5146] chars:[5136, 5146, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[5136, 5146] chars:[5136, 5146, "    X     "]
        Leaf:TABLE_ROW_EVEN[5146, 5147] chars:[5146, 5147, "|"]
        Leaf:EOL[5147, 5148] chars:[5147, 5148, "\n"]
      TABLE_ROW[5148, 5291] chars:[5148, 5291, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[5148, 5149] chars:[5148, 5149, "|"]
        TABLE_CELL[5149, 5270] chars:[5149, 5270, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[5149, 5150] chars:[5149, 5150, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[5150, 5174] chars:[5150, 5174, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[5174, 5270] chars:[5174, 5270, "Doubl …      "]
        Leaf:TABLE_ROW_ODD[5270, 5271] chars:[5270, 5271, "|"]
        TABLE_CELL[5271, 5278] chars:[5271, 5278, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[5271, 5278] chars:[5271, 5278, "       "]
        Leaf:TABLE_ROW_ODD[5278, 5279] chars:[5278, 5279, "|"]
        TABLE_CELL[5279, 5289] chars:[5279, 5289, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[5279, 5289] chars:[5279, 5289, "    X     "]
        Leaf:TABLE_ROW_ODD[5289, 5290] chars:[5289, 5290, "|"]
        Leaf:EOL[5290, 5291] chars:[5290, 5291, "\n"]
      TABLE_ROW[5291, 5434] chars:[5291, 5434, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[5291, 5292] chars:[5291, 5292, "|"]
        TABLE_CELL[5292, 5413] chars:[5292, 5413, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[5292, 5293] chars:[5292, 5293, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[5293, 5317] chars:[5293, 5317, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[5317, 5413] chars:[5317, 5413, "Wrap  …      "]
        Leaf:TABLE_ROW_EVEN[5413, 5414] chars:[5413, 5414, "|"]
        TABLE_CELL[5414, 5421] chars:[5414, 5421, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[5414, 5421] chars:[5414, 5421, "       "]
        Leaf:TABLE_ROW_EVEN[5421, 5422] chars:[5421, 5422, "|"]
        TABLE_CELL[5422, 5432] chars:[5422, 5432, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[5422, 5432] chars:[5422, 5432, "    X     "]
        Leaf:TABLE_ROW_EVEN[5432, 5433] chars:[5432, 5433, "|"]
        Leaf:EOL[5433, 5434] chars:[5433, 5434, "\n"]
      TABLE_ROW[5434, 5577] chars:[5434, 5577, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[5434, 5435] chars:[5434, 5435, "|"]
        TABLE_CELL[5435, 5556] chars:[5435, 5556, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[5435, 5436] chars:[5435, 5436, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[5436, 5460] chars:[5436, 5460, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[5460, 5490] chars:[5460, 5490, "ATX h … ling "]
          CODE[5490, 5493] chars:[5490, 5493, "`#`"]
            Leaf:CODE_MARKER[5490, 5491] chars:[5490, 5491, "`"]
            Leaf:TABLE_CELL_RODD_CODD_CODE_TEXT[5491, 5492] chars:[5491, 5492, "#"]
            Leaf:CODE_MARKER[5492, 5493] chars:[5492, 5493, "`"]
          Leaf:TABLE_CELL_RODD_CODD[5493, 5556] chars:[5493, 5556, " mark …      "]
        Leaf:TABLE_ROW_ODD[5556, 5557] chars:[5556, 5557, "|"]
        TABLE_CELL[5557, 5564] chars:[5557, 5564, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[5557, 5564] chars:[5557, 5564, "       "]
        Leaf:TABLE_ROW_ODD[5564, 5565] chars:[5564, 5565, "|"]
        TABLE_CELL[5565, 5575] chars:[5565, 5575, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[5565, 5575] chars:[5565, 5575, "    X     "]
        Leaf:TABLE_ROW_ODD[5575, 5576] chars:[5575, 5576, "|"]
        Leaf:EOL[5576, 5577] chars:[5576, 5577, "\n"]
      TABLE_ROW[5577, 5720] chars:[5577, 5720, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[5577, 5578] chars:[5577, 5578, "|"]
        TABLE_CELL[5578, 5699] chars:[5578, 5699, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[5578, 5579] chars:[5578, 5579, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[5579, 5603] chars:[5579, 5603, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[5603, 5699] chars:[5603, 5699, "Setex …      "]
        Leaf:TABLE_ROW_EVEN[5699, 5700] chars:[5699, 5700, "|"]
        TABLE_CELL[5700, 5707] chars:[5700, 5707, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[5700, 5707] chars:[5700, 5707, "       "]
        Leaf:TABLE_ROW_EVEN[5707, 5708] chars:[5707, 5708, "|"]
        TABLE_CELL[5708, 5718] chars:[5708, 5718, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[5708, 5718] chars:[5708, 5718, "    X     "]
        Leaf:TABLE_ROW_EVEN[5718, 5719] chars:[5718, 5719, "|"]
        Leaf:EOL[5719, 5720] chars:[5719, 5720, "\n"]
      TABLE_ROW[5720, 5863] chars:[5720, 5863, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[5720, 5721] chars:[5720, 5721, "|"]
        TABLE_CELL[5721, 5842] chars:[5721, 5842, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[5721, 5722] chars:[5721, 5722, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[5722, 5746] chars:[5722, 5746, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[5746, 5842] chars:[5746, 5842, "Forma …      "]
        Leaf:TABLE_ROW_ODD[5842, 5843] chars:[5842, 5843, "|"]
        TABLE_CELL[5843, 5850] chars:[5843, 5850, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[5843, 5850] chars:[5843, 5850, "       "]
        Leaf:TABLE_ROW_ODD[5850, 5851] chars:[5850, 5851, "|"]
        TABLE_CELL[5851, 5861] chars:[5851, 5861, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[5851, 5861] chars:[5851, 5861, "    X     "]
        Leaf:TABLE_ROW_ODD[5861, 5862] chars:[5861, 5862, "|"]
        Leaf:EOL[5862, 5863] chars:[5862, 5863, "\n"]
      TABLE_ROW[5863, 6006] chars:[5863, 6006, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[5863, 5864] chars:[5863, 5864, "|"]
        TABLE_CELL[5864, 5985] chars:[5864, 5985, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[5864, 5865] chars:[5864, 5865, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[5865, 5889] chars:[5865, 5889, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[5889, 5920] chars:[5889, 5920, "Auto  … w on "]
          Leaf:TABLE_CELL_REVEN_CODD_INLINE_HTML[5920, 5925] chars:[5920, 5925, "<kbd>"]
          Leaf:TABLE_CELL_REVEN_CODD[5925, 5930] chars:[5925, 5930, "ENTER"]
          Leaf:TABLE_CELL_REVEN_CODD_INLINE_HTML[5930, 5936] chars:[5930, 5936, "</kbd>"]
          Leaf:TABLE_CELL_REVEN_CODD[5936, 5985] chars:[5936, 5985, "      …      "]
        Leaf:TABLE_ROW_EVEN[5985, 5986] chars:[5985, 5986, "|"]
        TABLE_CELL[5986, 5993] chars:[5986, 5993, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[5986, 5993] chars:[5986, 5993, "       "]
        Leaf:TABLE_ROW_EVEN[5993, 5994] chars:[5993, 5994, "|"]
        TABLE_CELL[5994, 6004] chars:[5994, 6004, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[5994, 6004] chars:[5994, 6004, "    X     "]
        Leaf:TABLE_ROW_EVEN[6004, 6005] chars:[6004, 6005, "|"]
        Leaf:EOL[6005, 6006] chars:[6005, 6006, "\n"]
      TABLE_ROW[6006, 6149] chars:[6006, 6149, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[6006, 6007] chars:[6006, 6007, "|"]
        TABLE_CELL[6007, 6128] chars:[6007, 6128, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[6007, 6008] chars:[6007, 6008, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[6008, 6032] chars:[6008, 6032, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[6032, 6070] chars:[6032, 6070, "Auto  … n on "]
          Leaf:TABLE_CELL_RODD_CODD_INLINE_HTML[6070, 6075] chars:[6070, 6075, "<kbd>"]
          Leaf:TABLE_CELL_RODD_CODD[6075, 6084] chars:[6075, 6084, "BACKSPACE"]
          Leaf:TABLE_CELL_RODD_CODD_INLINE_HTML[6084, 6090] chars:[6084, 6090, "</kbd>"]
          Leaf:TABLE_CELL_RODD_CODD[6090, 6128] chars:[6090, 6128, "      …      "]
        Leaf:TABLE_ROW_ODD[6128, 6129] chars:[6128, 6129, "|"]
        TABLE_CELL[6129, 6136] chars:[6129, 6136, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[6129, 6136] chars:[6129, 6136, "       "]
        Leaf:TABLE_ROW_ODD[6136, 6137] chars:[6136, 6137, "|"]
        TABLE_CELL[6137, 6147] chars:[6137, 6147, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[6137, 6147] chars:[6137, 6147, "    X     "]
        Leaf:TABLE_ROW_ODD[6147, 6148] chars:[6147, 6148, "|"]
        Leaf:EOL[6148, 6149] chars:[6148, 6149, "\n"]
      TABLE_ROW[6149, 6292] chars:[6149, 6292, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[6149, 6150] chars:[6149, 6150, "|"]
        TABLE_CELL[6150, 6271] chars:[6150, 6271, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[6150, 6151] chars:[6150, 6151, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[6151, 6175] chars:[6151, 6175, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[6175, 6271] chars:[6175, 6271, "Auto  …      "]
        Leaf:TABLE_ROW_EVEN[6271, 6272] chars:[6271, 6272, "|"]
        TABLE_CELL[6272, 6279] chars:[6272, 6279, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[6272, 6279] chars:[6272, 6279, "       "]
        Leaf:TABLE_ROW_EVEN[6279, 6280] chars:[6279, 6280, "|"]
        TABLE_CELL[6280, 6290] chars:[6280, 6290, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[6280, 6290] chars:[6280, 6290, "    X     "]
        Leaf:TABLE_ROW_EVEN[6290, 6291] chars:[6290, 6291, "|"]
        Leaf:EOL[6291, 6292] chars:[6291, 6292, "\n"]
      TABLE_ROW[6292, 6435] chars:[6292, 6435, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[6292, 6293] chars:[6292, 6293, "|"]
        TABLE_CELL[6293, 6414] chars:[6293, 6414, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[6293, 6294] chars:[6293, 6294, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[6294, 6318] chars:[6294, 6318, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[6318, 6414] chars:[6318, 6414, "Actio …      "]
        Leaf:TABLE_ROW_ODD[6414, 6415] chars:[6414, 6415, "|"]
        TABLE_CELL[6415, 6422] chars:[6415, 6422, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[6415, 6422] chars:[6415, 6422, "       "]
        Leaf:TABLE_ROW_ODD[6422, 6423] chars:[6422, 6423, "|"]
        TABLE_CELL[6423, 6433] chars:[6423, 6433, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[6423, 6433] chars:[6423, 6433, "    X     "]
        Leaf:TABLE_ROW_ODD[6433, 6434] chars:[6433, 6434, "|"]
        Leaf:EOL[6434, 6435] chars:[6434, 6435, "\n"]
      TABLE_ROW[6435, 6578] chars:[6435, 6578, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[6435, 6436] chars:[6435, 6436, "|"]
        TABLE_CELL[6436, 6557] chars:[6436, 6557, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[6436, 6437] chars:[6436, 6437, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[6437, 6461] chars:[6437, 6461, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[6461, 6486] chars:[6461, 6486, "Auto  … m on "]
          Leaf:TABLE_CELL_REVEN_CODD_INLINE_HTML[6486, 6491] chars:[6486, 6491, "<kbd>"]
          Leaf:TABLE_CELL_REVEN_CODD[6491, 6496] chars:[6491, 6496, "ENTER"]
          Leaf:TABLE_CELL_REVEN_CODD_INLINE_HTML[6496, 6502] chars:[6496, 6502, "</kbd>"]
          Leaf:TABLE_CELL_REVEN_CODD[6502, 6557] chars:[6502, 6557, "      …      "]
        Leaf:TABLE_ROW_EVEN[6557, 6558] chars:[6557, 6558, "|"]
        TABLE_CELL[6558, 6565] chars:[6558, 6565, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[6558, 6565] chars:[6558, 6565, "       "]
        Leaf:TABLE_ROW_EVEN[6565, 6566] chars:[6565, 6566, "|"]
        TABLE_CELL[6566, 6576] chars:[6566, 6576, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[6566, 6576] chars:[6566, 6576, "    X     "]
        Leaf:TABLE_ROW_EVEN[6576, 6577] chars:[6576, 6577, "|"]
        Leaf:EOL[6577, 6578] chars:[6577, 6578, "\n"]
      TABLE_ROW[6578, 6721] chars:[6578, 6721, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[6578, 6579] chars:[6578, 6579, "|"]
        TABLE_CELL[6579, 6700] chars:[6579, 6700, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[6579, 6580] chars:[6579, 6580, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[6580, 6604] chars:[6580, 6604, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[6604, 6635] chars:[6604, 6635, "Auto  … m on "]
          Leaf:TABLE_CELL_RODD_CODD_INLINE_HTML[6635, 6640] chars:[6635, 6640, "<kbd>"]
          Leaf:TABLE_CELL_RODD_CODD[6640, 6645] chars:[6640, 6645, "ENTER"]
          Leaf:TABLE_CELL_RODD_CODD_INLINE_HTML[6645, 6651] chars:[6645, 6651, "</kbd>"]
          Leaf:TABLE_CELL_RODD_CODD[6651, 6700] chars:[6651, 6700, "      …      "]
        Leaf:TABLE_ROW_ODD[6700, 6701] chars:[6700, 6701, "|"]
        TABLE_CELL[6701, 6708] chars:[6701, 6708, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[6701, 6708] chars:[6701, 6708, "       "]
        Leaf:TABLE_ROW_ODD[6708, 6709] chars:[6708, 6709, "|"]
        TABLE_CELL[6709, 6719] chars:[6709, 6719, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[6709, 6719] chars:[6709, 6719, "    X     "]
        Leaf:TABLE_ROW_ODD[6719, 6720] chars:[6719, 6720, "|"]
        Leaf:EOL[6720, 6721] chars:[6720, 6721, "\n"]
      TABLE_ROW[6721, 6864] chars:[6721, 6864, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[6721, 6722] chars:[6721, 6722, "|"]
        TABLE_CELL[6722, 6843] chars:[6722, 6843, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[6722, 6723] chars:[6722, 6723, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[6723, 6747] chars:[6723, 6747, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[6747, 6778] chars:[6747, 6778, "Auto  … m on "]
          Leaf:TABLE_CELL_REVEN_CODD_INLINE_HTML[6778, 6783] chars:[6778, 6783, "<kbd>"]
          Leaf:TABLE_CELL_REVEN_CODD[6783, 6792] chars:[6783, 6792, "BACKSPACE"]
          Leaf:TABLE_CELL_REVEN_CODD_INLINE_HTML[6792, 6798] chars:[6792, 6798, "</kbd>"]
          Leaf:TABLE_CELL_REVEN_CODD[6798, 6843] chars:[6798, 6843, "      …      "]
        Leaf:TABLE_ROW_EVEN[6843, 6844] chars:[6843, 6844, "|"]
        TABLE_CELL[6844, 6851] chars:[6844, 6851, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[6844, 6851] chars:[6844, 6851, "       "]
        Leaf:TABLE_ROW_EVEN[6851, 6852] chars:[6851, 6852, "|"]
        TABLE_CELL[6852, 6862] chars:[6852, 6862, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[6852, 6862] chars:[6852, 6862, "    X     "]
        Leaf:TABLE_ROW_EVEN[6862, 6863] chars:[6862, 6863, "|"]
        Leaf:EOL[6863, 6864] chars:[6863, 6864, "\n"]
      TABLE_ROW[6864, 7007] chars:[6864, 7007, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[6864, 6865] chars:[6864, 6865, "|"]
        TABLE_CELL[6865, 6986] chars:[6865, 6986, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[6865, 6866] chars:[6865, 6866, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[6866, 6890] chars:[6866, 6890, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[6890, 6986] chars:[6890, 6986, "Inden …      "]
        Leaf:TABLE_ROW_ODD[6986, 6987] chars:[6986, 6987, "|"]
        TABLE_CELL[6987, 6994] chars:[6987, 6994, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[6987, 6994] chars:[6987, 6994, "       "]
        Leaf:TABLE_ROW_ODD[6994, 6995] chars:[6994, 6995, "|"]
        TABLE_CELL[6995, 7005] chars:[6995, 7005, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[6995, 7005] chars:[6995, 7005, "    X     "]
        Leaf:TABLE_ROW_ODD[7005, 7006] chars:[7005, 7006, "|"]
        Leaf:EOL[7006, 7007] chars:[7006, 7007, "\n"]
      TABLE_ROW[7007, 7150] chars:[7007, 7150, "| **C …    |\n"]
        Leaf:TABLE_ROW_EVEN[7007, 7008] chars:[7007, 7008, "|"]
        TABLE_CELL[7008, 7129] chars:[7008, 7129, " **Co …      "]
          Leaf:TABLE_CELL_REVEN_CODD[7008, 7009] chars:[7008, 7009, " "]
          BOLD[7009, 7029] chars:[7009, 7029, "**Cod … ons**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[7009, 7011] chars:[7009, 7011, "**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_TEXT[7011, 7027] chars:[7011, 7027, "Code  … tions"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[7027, 7029] chars:[7027, 7029, "**"]
          Leaf:TABLE_CELL_REVEN_CODD[7029, 7129] chars:[7029, 7129, "      …      "]
        Leaf:TABLE_ROW_EVEN[7129, 7130] chars:[7129, 7130, "|"]
        TABLE_CELL[7130, 7137] chars:[7130, 7137, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[7130, 7137] chars:[7130, 7137, "       "]
        Leaf:TABLE_ROW_EVEN[7137, 7138] chars:[7137, 7138, "|"]
        TABLE_CELL[7138, 7148] chars:[7138, 7148, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[7138, 7148] chars:[7138, 7148, "    X     "]
        Leaf:TABLE_ROW_EVEN[7148, 7149] chars:[7148, 7149, "|"]
        Leaf:EOL[7149, 7150] chars:[7149, 7150, "\n"]
      TABLE_ROW[7150, 7293] chars:[7150, 7293, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[7150, 7151] chars:[7150, 7151, "|"]
        TABLE_CELL[7151, 7272] chars:[7151, 7272, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[7151, 7152] chars:[7151, 7152, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[7152, 7176] chars:[7152, 7176, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[7176, 7216] chars:[7176, 7216, "Absol … sing "]
          AUTO_LINK[7216, 7224] chars:[7216, 7224, "https://"]
            AUTO_LINK_REF[7216, 7224] chars:[7216, 7224, "https://"]
              Leaf:AUTO_LINK_REF[7216, 7224] chars:[7216, 7224, "https://"]
          Leaf:TABLE_CELL_RODD_CODD[7224, 7229] chars:[7224, 7229, " and "]
          AUTO_LINK[7229, 7236] chars:[7229, 7236, "file://"]
            AUTO_LINK_REF[7229, 7236] chars:[7229, 7236, "file://"]
              Leaf:AUTO_LINK_REF[7229, 7236] chars:[7229, 7236, "file://"]
          Leaf:TABLE_CELL_RODD_CODD[7236, 7272] chars:[7236, 7272, " form …      "]
        Leaf:TABLE_ROW_ODD[7272, 7273] chars:[7272, 7273, "|"]
        TABLE_CELL[7273, 7280] chars:[7273, 7280, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[7273, 7280] chars:[7273, 7280, "       "]
        Leaf:TABLE_ROW_ODD[7280, 7281] chars:[7280, 7281, "|"]
        TABLE_CELL[7281, 7291] chars:[7281, 7291, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[7281, 7291] chars:[7281, 7291, "    X     "]
        Leaf:TABLE_ROW_ODD[7291, 7292] chars:[7291, 7292, "|"]
        Leaf:EOL[7292, 7293] chars:[7292, 7293, "\n"]
      TABLE_ROW[7293, 7436] chars:[7293, 7436, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[7293, 7294] chars:[7293, 7294, "|"]
        TABLE_CELL[7294, 7415] chars:[7294, 7415, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[7294, 7295] chars:[7294, 7295, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[7295, 7319] chars:[7295, 7319, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[7319, 7415] chars:[7319, 7415, "Expli …      "]
        Leaf:TABLE_ROW_EVEN[7415, 7416] chars:[7415, 7416, "|"]
        TABLE_CELL[7416, 7423] chars:[7416, 7423, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[7416, 7423] chars:[7416, 7423, "       "]
        Leaf:TABLE_ROW_EVEN[7423, 7424] chars:[7423, 7424, "|"]
        TABLE_CELL[7424, 7434] chars:[7424, 7434, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[7424, 7434] chars:[7424, 7434, "    X     "]
        Leaf:TABLE_ROW_EVEN[7434, 7435] chars:[7434, 7435, "|"]
        Leaf:EOL[7435, 7436] chars:[7435, 7436, "\n"]
      TABLE_ROW[7436, 7579] chars:[7436, 7579, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[7436, 7437] chars:[7436, 7437, "|"]
        TABLE_CELL[7437, 7558] chars:[7437, 7558, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[7437, 7438] chars:[7437, 7438, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[7438, 7462] chars:[7438, 7462, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[7462, 7475] chars:[7462, 7475, "GitHu … ssue "]
          Leaf:TABLE_CELL_RODD_CODD_ISSUE_MARKER[7475, 7476] chars:[7475, 7476, "#"]
          Leaf:TABLE_CELL_RODD_CODD[7476, 7495] chars:[7476, 7495, " Comp … fter "]
          CODE[7495, 7504] chars:[7495, 7504, "`issues/`"]
            Leaf:CODE_MARKER[7495, 7496] chars:[7495, 7496, "`"]
            Leaf:TABLE_CELL_RODD_CODD_CODE_TEXT[7496, 7503] chars:[7496, 7503, "issues/"]
            Leaf:CODE_MARKER[7503, 7504] chars:[7503, 7504, "`"]
          Leaf:TABLE_CELL_RODD_CODD[7504, 7558] chars:[7504, 7558, " link …      "]
        Leaf:TABLE_ROW_ODD[7558, 7559] chars:[7558, 7559, "|"]
        TABLE_CELL[7559, 7566] chars:[7559, 7566, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[7559, 7566] chars:[7559, 7566, "       "]
        Leaf:TABLE_ROW_ODD[7566, 7567] chars:[7566, 7567, "|"]
        TABLE_CELL[7567, 7577] chars:[7567, 7577, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[7567, 7577] chars:[7567, 7577, "    X     "]
        Leaf:TABLE_ROW_ODD[7577, 7578] chars:[7577, 7578, "|"]
        Leaf:EOL[7578, 7579] chars:[7578, 7579, "\n"]
      TABLE_ROW[7579, 7722] chars:[7579, 7722, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[7579, 7580] chars:[7579, 7580, "|"]
        TABLE_CELL[7580, 7701] chars:[7580, 7701, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[7580, 7581] chars:[7580, 7581, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[7581, 7605] chars:[7581, 7605, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[7605, 7701] chars:[7605, 7701, "GitHu …      "]
        Leaf:TABLE_ROW_EVEN[7701, 7702] chars:[7701, 7702, "|"]
        TABLE_CELL[7702, 7709] chars:[7702, 7709, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[7702, 7709] chars:[7702, 7709, "       "]
        Leaf:TABLE_ROW_EVEN[7709, 7710] chars:[7709, 7710, "|"]
        TABLE_CELL[7710, 7720] chars:[7710, 7720, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[7710, 7720] chars:[7710, 7720, "    X     "]
        Leaf:TABLE_ROW_EVEN[7720, 7721] chars:[7720, 7721, "|"]
        Leaf:EOL[7721, 7722] chars:[7721, 7722, "\n"]
      TABLE_ROW[7722, 7865] chars:[7722, 7865, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[7722, 7723] chars:[7722, 7723, "|"]
        TABLE_CELL[7723, 7844] chars:[7723, 7844, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[7723, 7724] chars:[7723, 7724, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[7724, 7748] chars:[7724, 7748, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[7748, 7844] chars:[7748, 7844, "Link  …      "]
        Leaf:TABLE_ROW_ODD[7844, 7845] chars:[7844, 7845, "|"]
        TABLE_CELL[7845, 7852] chars:[7845, 7852, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[7845, 7852] chars:[7845, 7852, "       "]
        Leaf:TABLE_ROW_ODD[7852, 7853] chars:[7852, 7853, "|"]
        TABLE_CELL[7853, 7863] chars:[7853, 7863, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[7853, 7863] chars:[7853, 7863, "    X     "]
        Leaf:TABLE_ROW_ODD[7863, 7864] chars:[7863, 7864, "|"]
        Leaf:EOL[7864, 7865] chars:[7864, 7865, "\n"]
      TABLE_ROW[7865, 8008] chars:[7865, 8008, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[7865, 7866] chars:[7865, 7866, "|"]
        TABLE_CELL[7866, 7987] chars:[7866, 7987, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[7866, 7867] chars:[7866, 7867, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[7867, 7891] chars:[7867, 7891, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[7891, 7987] chars:[7891, 7987, "Emoji …      "]
        Leaf:TABLE_ROW_EVEN[7987, 7988] chars:[7987, 7988, "|"]
        TABLE_CELL[7988, 7995] chars:[7988, 7995, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[7988, 7995] chars:[7988, 7995, "       "]
        Leaf:TABLE_ROW_EVEN[7995, 7996] chars:[7995, 7996, "|"]
        TABLE_CELL[7996, 8006] chars:[7996, 8006, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[7996, 8006] chars:[7996, 8006, "    X     "]
        Leaf:TABLE_ROW_EVEN[8006, 8007] chars:[8006, 8007, "|"]
        Leaf:EOL[8007, 8008] chars:[8007, 8008, "\n"]
      TABLE_ROW[8008, 8151] chars:[8008, 8151, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[8008, 8009] chars:[8008, 8009, "|"]
        TABLE_CELL[8009, 8130] chars:[8009, 8130, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[8009, 8010] chars:[8009, 8010, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[8010, 8034] chars:[8010, 8034, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[8034, 8130] chars:[8034, 8130, "Java  …      "]
        Leaf:TABLE_ROW_ODD[8130, 8131] chars:[8130, 8131, "|"]
        TABLE_CELL[8131, 8138] chars:[8131, 8138, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[8131, 8138] chars:[8131, 8138, "       "]
        Leaf:TABLE_ROW_ODD[8138, 8139] chars:[8138, 8139, "|"]
        TABLE_CELL[8139, 8149] chars:[8139, 8149, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[8139, 8149] chars:[8139, 8149, "    X     "]
        Leaf:TABLE_ROW_ODD[8149, 8150] chars:[8149, 8150, "|"]
        Leaf:EOL[8150, 8151] chars:[8150, 8151, "\n"]
      TABLE_ROW[8151, 8294] chars:[8151, 8294, "| **I …    |\n"]
        Leaf:TABLE_ROW_EVEN[8151, 8152] chars:[8151, 8152, "|"]
        TABLE_CELL[8152, 8273] chars:[8152, 8273, " **In …      "]
          Leaf:TABLE_CELL_REVEN_CODD[8152, 8153] chars:[8152, 8153, " "]
          BOLD[8153, 8174] chars:[8153, 8174, "**Int … ons**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[8153, 8155] chars:[8153, 8155, "**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_TEXT[8155, 8172] chars:[8155, 8172, "Inten … tions"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[8172, 8174] chars:[8172, 8174, "**"]
          Leaf:TABLE_CELL_REVEN_CODD[8174, 8273] chars:[8174, 8273, "      …      "]
        Leaf:TABLE_ROW_EVEN[8273, 8274] chars:[8273, 8274, "|"]
        TABLE_CELL[8274, 8281] chars:[8274, 8281, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[8274, 8281] chars:[8274, 8281, "       "]
        Leaf:TABLE_ROW_EVEN[8281, 8282] chars:[8281, 8282, "|"]
        TABLE_CELL[8282, 8292] chars:[8282, 8292, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[8282, 8292] chars:[8282, 8292, "    X     "]
        Leaf:TABLE_ROW_EVEN[8292, 8293] chars:[8292, 8293, "|"]
        Leaf:EOL[8293, 8294] chars:[8293, 8294, "\n"]
      TABLE_ROW[8294, 8437] chars:[8294, 8437, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[8294, 8295] chars:[8294, 8295, "|"]
        TABLE_CELL[8295, 8416] chars:[8295, 8416, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[8295, 8296] chars:[8295, 8296, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[8296, 8320] chars:[8296, 8320, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[8320, 8357] chars:[8320, 8357, "Chang … lute "]
          AUTO_LINK[8357, 8365] chars:[8357, 8365, "https://"]
            AUTO_LINK_REF[8357, 8365] chars:[8357, 8365, "https://"]
              Leaf:AUTO_LINK_REF[8357, 8365] chars:[8357, 8365, "https://"]
          Leaf:TABLE_CELL_RODD_CODD[8365, 8416] chars:[8365, 8416, " link …      "]
        Leaf:TABLE_ROW_ODD[8416, 8417] chars:[8416, 8417, "|"]
        TABLE_CELL[8417, 8424] chars:[8417, 8424, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[8417, 8424] chars:[8417, 8424, "       "]
        Leaf:TABLE_ROW_ODD[8424, 8425] chars:[8424, 8425, "|"]
        TABLE_CELL[8425, 8435] chars:[8425, 8435, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[8425, 8435] chars:[8425, 8435, "    X     "]
        Leaf:TABLE_ROW_ODD[8435, 8436] chars:[8435, 8436, "|"]
        Leaf:EOL[8436, 8437] chars:[8436, 8437, "\n"]
      TABLE_ROW[8437, 8580] chars:[8437, 8580, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[8437, 8438] chars:[8437, 8438, "|"]
        TABLE_CELL[8438, 8559] chars:[8438, 8559, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[8438, 8439] chars:[8438, 8439, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[8439, 8463] chars:[8439, 8463, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[8463, 8559] chars:[8463, 8559, "Chang …      "]
        Leaf:TABLE_ROW_EVEN[8559, 8560] chars:[8559, 8560, "|"]
        TABLE_CELL[8560, 8567] chars:[8560, 8567, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[8560, 8567] chars:[8560, 8567, "       "]
        Leaf:TABLE_ROW_EVEN[8567, 8568] chars:[8567, 8568, "|"]
        TABLE_CELL[8568, 8578] chars:[8568, 8578, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[8568, 8578] chars:[8568, 8578, "    X     "]
        Leaf:TABLE_ROW_EVEN[8578, 8579] chars:[8578, 8579, "|"]
        Leaf:EOL[8579, 8580] chars:[8579, 8580, "\n"]
      TABLE_ROW[8580, 8723] chars:[8580, 8723, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[8580, 8581] chars:[8580, 8581, "|"]
        TABLE_CELL[8581, 8702] chars:[8581, 8702, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[8581, 8582] chars:[8581, 8582, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[8582, 8606] chars:[8582, 8606, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[8606, 8702] chars:[8606, 8702, "Inten …      "]
        Leaf:TABLE_ROW_ODD[8702, 8703] chars:[8702, 8703, "|"]
        TABLE_CELL[8703, 8710] chars:[8703, 8710, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[8703, 8710] chars:[8703, 8710, "       "]
        Leaf:TABLE_ROW_ODD[8710, 8711] chars:[8710, 8711, "|"]
        TABLE_CELL[8711, 8721] chars:[8711, 8721, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[8711, 8721] chars:[8711, 8721, "    X     "]
        Leaf:TABLE_ROW_ODD[8721, 8722] chars:[8721, 8722, "|"]
        Leaf:EOL[8722, 8723] chars:[8722, 8723, "\n"]
      TABLE_ROW[8723, 8866] chars:[8723, 8866, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[8723, 8724] chars:[8723, 8724, "|"]
        TABLE_CELL[8724, 8845] chars:[8724, 8845, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[8724, 8725] chars:[8724, 8725, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[8725, 8749] chars:[8725, 8749, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[8749, 8845] chars:[8749, 8845, "Inten …      "]
        Leaf:TABLE_ROW_EVEN[8845, 8846] chars:[8845, 8846, "|"]
        TABLE_CELL[8846, 8853] chars:[8846, 8853, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[8846, 8853] chars:[8846, 8853, "       "]
        Leaf:TABLE_ROW_EVEN[8853, 8854] chars:[8853, 8854, "|"]
        TABLE_CELL[8854, 8864] chars:[8854, 8864, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[8854, 8864] chars:[8854, 8864, "    X     "]
        Leaf:TABLE_ROW_EVEN[8864, 8865] chars:[8864, 8865, "|"]
        Leaf:EOL[8865, 8866] chars:[8865, 8866, "\n"]
      TABLE_ROW[8866, 9009] chars:[8866, 9009, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[8866, 8867] chars:[8866, 8867, "|"]
        TABLE_CELL[8867, 8988] chars:[8867, 8988, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[8867, 8868] chars:[8867, 8868, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[8868, 8892] chars:[8868, 8892, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[8892, 8988] chars:[8892, 8988, "Inten …      "]
        Leaf:TABLE_ROW_ODD[8988, 8989] chars:[8988, 8989, "|"]
        TABLE_CELL[8989, 8996] chars:[8989, 8996, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[8989, 8996] chars:[8989, 8996, "       "]
        Leaf:TABLE_ROW_ODD[8996, 8997] chars:[8996, 8997, "|"]
        TABLE_CELL[8997, 9007] chars:[8997, 9007, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[8997, 9007] chars:[8997, 9007, "    X     "]
        Leaf:TABLE_ROW_ODD[9007, 9008] chars:[9007, 9008, "|"]
        Leaf:EOL[9008, 9009] chars:[9008, 9009, "\n"]
      TABLE_ROW[9009, 9152] chars:[9009, 9152, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[9009, 9010] chars:[9009, 9010, "|"]
        TABLE_CELL[9010, 9131] chars:[9010, 9131, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[9010, 9011] chars:[9010, 9011, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[9011, 9035] chars:[9011, 9035, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[9035, 9131] chars:[9035, 9131, "Updat …      "]
        Leaf:TABLE_ROW_EVEN[9131, 9132] chars:[9131, 9132, "|"]
        TABLE_CELL[9132, 9139] chars:[9132, 9139, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[9132, 9139] chars:[9132, 9139, "       "]
        Leaf:TABLE_ROW_EVEN[9139, 9140] chars:[9139, 9140, "|"]
        TABLE_CELL[9140, 9150] chars:[9140, 9150, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[9140, 9150] chars:[9140, 9150, "    X     "]
        Leaf:TABLE_ROW_EVEN[9150, 9151] chars:[9150, 9151, "|"]
        Leaf:EOL[9151, 9152] chars:[9151, 9152, "\n"]
      TABLE_ROW[9152, 9295] chars:[9152, 9295, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[9152, 9153] chars:[9152, 9153, "|"]
        TABLE_CELL[9153, 9274] chars:[9153, 9274, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[9153, 9154] chars:[9153, 9154, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[9154, 9178] chars:[9154, 9178, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[9178, 9274] chars:[9178, 9274, "Inten …      "]
        Leaf:TABLE_ROW_ODD[9274, 9275] chars:[9274, 9275, "|"]
        TABLE_CELL[9275, 9282] chars:[9275, 9282, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[9275, 9282] chars:[9275, 9282, "       "]
        Leaf:TABLE_ROW_ODD[9282, 9283] chars:[9282, 9283, "|"]
        TABLE_CELL[9283, 9293] chars:[9283, 9293, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[9283, 9293] chars:[9283, 9293, "    X     "]
        Leaf:TABLE_ROW_ODD[9293, 9294] chars:[9293, 9294, "|"]
        Leaf:EOL[9294, 9295] chars:[9294, 9295, "\n"]
      TABLE_ROW[9295, 9438] chars:[9295, 9438, "| **R …    |\n"]
        Leaf:TABLE_ROW_EVEN[9295, 9296] chars:[9295, 9296, "|"]
        TABLE_CELL[9296, 9417] chars:[9296, 9417, " **Re …      "]
          Leaf:TABLE_CELL_REVEN_CODD[9296, 9297] chars:[9296, 9297, " "]
          BOLD[9297, 9312] chars:[9297, 9312, "**Ref … ing**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[9297, 9299] chars:[9297, 9299, "**"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_TEXT[9299, 9310] chars:[9299, 9310, "Refac … oring"]
            Leaf:TABLE_CELL_REVEN_CODD_BOLD_MARKER[9310, 9312] chars:[9310, 9312, "**"]
          Leaf:TABLE_CELL_REVEN_CODD[9312, 9417] chars:[9312, 9417, "      …      "]
        Leaf:TABLE_ROW_EVEN[9417, 9418] chars:[9417, 9418, "|"]
        TABLE_CELL[9418, 9425] chars:[9418, 9425, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[9418, 9425] chars:[9418, 9425, "       "]
        Leaf:TABLE_ROW_EVEN[9425, 9426] chars:[9425, 9426, "|"]
        TABLE_CELL[9426, 9436] chars:[9426, 9436, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[9426, 9436] chars:[9426, 9436, "    X     "]
        Leaf:TABLE_ROW_EVEN[9436, 9437] chars:[9436, 9437, "|"]
        Leaf:EOL[9437, 9438] chars:[9437, 9438, "\n"]
      TABLE_ROW[9438, 9581] chars:[9438, 9581, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[9438, 9439] chars:[9438, 9439, "|"]
        TABLE_CELL[9439, 9560] chars:[9439, 9560, " &nbs … wiki "]
          Leaf:TABLE_CELL_RODD_CODD[9439, 9440] chars:[9439, 9440, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[9440, 9464] chars:[9440, 9464, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[9464, 9560] chars:[9464, 9560, "Autom … wiki "]
        Leaf:TABLE_ROW_ODD[9560, 9561] chars:[9560, 9561, "|"]
        TABLE_CELL[9561, 9568] chars:[9561, 9568, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[9561, 9568] chars:[9561, 9568, "       "]
        Leaf:TABLE_ROW_ODD[9568, 9569] chars:[9568, 9569, "|"]
        TABLE_CELL[9569, 9579] chars:[9569, 9579, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[9569, 9579] chars:[9569, 9579, "    X     "]
        Leaf:TABLE_ROW_ODD[9579, 9580] chars:[9579, 9580, "|"]
        Leaf:EOL[9580, 9581] chars:[9580, 9581, "\n"]
      TABLE_ROW[9581, 9724] chars:[9581, 9724, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[9581, 9582] chars:[9581, 9582, "|"]
        TABLE_CELL[9582, 9703] chars:[9582, 9703, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[9582, 9583] chars:[9582, 9583, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[9583, 9607] chars:[9583, 9607, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[9607, 9703] chars:[9607, 9703, "File  …      "]
        Leaf:TABLE_ROW_EVEN[9703, 9704] chars:[9703, 9704, "|"]
        TABLE_CELL[9704, 9711] chars:[9704, 9711, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[9704, 9711] chars:[9704, 9711, "       "]
        Leaf:TABLE_ROW_EVEN[9711, 9712] chars:[9711, 9712, "|"]
        TABLE_CELL[9712, 9722] chars:[9712, 9722, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[9712, 9722] chars:[9712, 9722, "    X     "]
        Leaf:TABLE_ROW_EVEN[9722, 9723] chars:[9722, 9723, "|"]
        Leaf:EOL[9723, 9724] chars:[9723, 9724, "\n"]
      TABLE_ROW[9724, 9867] chars:[9724, 9867, "| &nb …    |\n"]
        Leaf:TABLE_ROW_ODD[9724, 9725] chars:[9724, 9725, "|"]
        TABLE_CELL[9725, 9846] chars:[9725, 9846, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[9725, 9726] chars:[9725, 9726, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[9726, 9750] chars:[9726, 9750, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[9750, 9769] chars:[9750, 9769, "Refac … r /, "]
          AUTO_LINK[9769, 9777] chars:[9769, 9777, "https://"]
            AUTO_LINK_REF[9769, 9777] chars:[9769, 9777, "https://"]
              Leaf:AUTO_LINK_REF[9769, 9777] chars:[9769, 9777, "https://"]
          Leaf:TABLE_CELL_RODD_CODD[9777, 9782] chars:[9777, 9782, " and "]
          AUTO_LINK[9782, 9789] chars:[9782, 9789, "file://"]
            AUTO_LINK_REF[9782, 9789] chars:[9782, 9789, "file://"]
              Leaf:AUTO_LINK_REF[9782, 9789] chars:[9782, 9789, "file://"]
          Leaf:TABLE_CELL_RODD_CODD[9789, 9846] chars:[9789, 9846, " abso …      "]
        Leaf:TABLE_ROW_ODD[9846, 9847] chars:[9846, 9847, "|"]
        TABLE_CELL[9847, 9854] chars:[9847, 9854, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[9847, 9854] chars:[9847, 9854, "       "]
        Leaf:TABLE_ROW_ODD[9854, 9855] chars:[9854, 9855, "|"]
        TABLE_CELL[9855, 9865] chars:[9855, 9865, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[9855, 9865] chars:[9855, 9865, "    X     "]
        Leaf:TABLE_ROW_ODD[9865, 9866] chars:[9865, 9866, "|"]
        Leaf:EOL[9866, 9867] chars:[9866, 9867, "\n"]
      TABLE_ROW[9867, 10010] chars:[9867, 10010, "| &nb …    |\n"]
        Leaf:TABLE_ROW_EVEN[9867, 9868] chars:[9867, 9868, "|"]
        TABLE_CELL[9868, 9989] chars:[9868, 9989, " &nbs …      "]
          Leaf:TABLE_CELL_REVEN_CODD[9868, 9869] chars:[9868, 9869, " "]
          Leaf:TABLE_CELL_REVEN_CODD_HTML_ENTITY[9869, 9893] chars:[9869, 9893, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_REVEN_CODD[9893, 9989] chars:[9893, 9989, "Refac …      "]
        Leaf:TABLE_ROW_EVEN[9989, 9990] chars:[9989, 9990, "|"]
        TABLE_CELL[9990, 9997] chars:[9990, 9997, "       "]
          Leaf:TABLE_CELL_REVEN_CEVEN[9990, 9997] chars:[9990, 9997, "       "]
        Leaf:TABLE_ROW_EVEN[9997, 9998] chars:[9997, 9998, "|"]
        TABLE_CELL[9998, 10008] chars:[9998, 10008, "    X     "]
          Leaf:TABLE_CELL_REVEN_CODD[9998, 10008] chars:[9998, 10008, "    X     "]
        Leaf:TABLE_ROW_EVEN[10008, 10009] chars:[10008, 10009, "|"]
        Leaf:EOL[10009, 10010] chars:[10009, 10010, "\n"]
      TABLE_ROW[10010, 10152] chars:[10010, 10152, "| &nb …     |"]
        Leaf:TABLE_ROW_ODD[10010, 10011] chars:[10010, 10011, "|"]
        TABLE_CELL[10011, 10132] chars:[10011, 10132, " &nbs …      "]
          Leaf:TABLE_CELL_RODD_CODD[10011, 10012] chars:[10011, 10012, " "]
          Leaf:TABLE_CELL_RODD_CODD_HTML_ENTITY[10012, 10036] chars:[10012, 10036, "&nbsp … nbsp;"]
          Leaf:TABLE_CELL_RODD_CODD[10036, 10132] chars:[10036, 10132, "Ancho …      "]
        Leaf:TABLE_ROW_ODD[10132, 10133] chars:[10132, 10133, "|"]
        TABLE_CELL[10133, 10140] chars:[10133, 10140, "       "]
          Leaf:TABLE_CELL_RODD_CEVEN[10133, 10140] chars:[10133, 10140, "       "]
        Leaf:TABLE_ROW_ODD[10140, 10141] chars:[10140, 10141, "|"]
        TABLE_CELL[10141, 10151] chars:[10141, 10151, "    X     "]
          Leaf:TABLE_CELL_RODD_CODD[10141, 10151] chars:[10141, 10151, "    X     "]
        Leaf:TABLE_ROW_ODD[10151, 10152] chars:[10151, 10152, "|"]
````````````````````````````````


### Markdown elements - ThematicBreak

`ThematicBreak`

```````````````````````````````` example Markdown elements - Markdown elements - ThematicBreak: 1
.

.
FILE[0, 0]
  Leaf:WHITESPACE[0, 0]
````````````````````````````````


### Markdown elements - TocBlock

`TocBlock`

basic

```````````````````````````````` example Markdown elements - Markdown elements - TocBlock: 1
[TOC]
.

.
FILE[0, 6] chars:[0, 6, "[TOC]\n"]
  TOC[0, 5] chars:[0, 5, "[TOC]"]
    Leaf:TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:TOC_CLOSE[4, 5] chars:[4, 5, "]"]
  Leaf:EOL[5, 6] chars:[5, 6, "\n"]
````````````````````````````````


options

```````````````````````````````` example Markdown elements - Markdown elements - TocBlock: 2
[TOC levels=3]
.

.
FILE[0, 15] chars:[0, 15, "[TOC  … s=3]\n"]
  TOC[0, 14] chars:[0, 14, "[TOC  … ls=3]"]
    Leaf:TOC_OPEN[0, 1] chars:[0, 1, "["]
    Leaf:TOC_KEYWORD[1, 4] chars:[1, 4, "TOC"]
    Leaf:WHITESPACE[4, 5] chars:[4, 5, " "]
    Leaf:TOC_OPTION[5, 13] chars:[5, 13, "levels=3"]
    Leaf:TOC_CLOSE[13, 14] chars:[13, 14, "]"]
  Leaf:EOL[14, 15] chars:[14, 15, "\n"]
````````````````````````````````


### Markdown elements - TypographicQuotes

`TypographicQuotes`

basic quotes

```````````````````````````````` example(Markdown elements - Markdown elements - TypographicQuotes: 1) options(flexmark-fail)
Sample "double" 'single' <<angle>> "l'ordre" 'l'ordre'
.
<p>Sample &ldquo;double&rdquo; &lsquo;single&rsquo; &laquo;angle&raquo; &ldquo;l&rsquo;ordre&rdquo; &lsquo;l&rsquo;ordre&rsquo;</p>
.
FILE[0, 55] chars:[0, 55, "Sampl … dre'\n"]
  PARAGRAPH_BLOCK[0, 55] chars:[0, 55, "Sampl … dre'\n"]
    TEXT_BLOCK[0, 55] chars:[0, 55, "Sampl … dre'\n"]
      Leaf:TEXT[0, 7] chars:[0, 7, "Sample "]
      QUOTE[7, 15] chars:[7, 15, "\"double\""]
        Leaf:QUOTE_MARKER[7, 8] chars:[7, 8, "\""]
        Leaf:QUOTED_TEXT[8, 14] chars:[8, 14, "double"]
        Leaf:QUOTE_MARKER[14, 15] chars:[14, 15, "\""]
      Leaf:TEXT[15, 16] chars:[15, 16, " "]
      QUOTE[16, 24] chars:[16, 24, "'single'"]
        Leaf:QUOTE_MARKER[16, 17] chars:[16, 17, "'"]
        Leaf:QUOTED_TEXT[17, 23] chars:[17, 23, "single"]
        Leaf:QUOTE_MARKER[23, 24] chars:[23, 24, "'"]
      Leaf:TEXT[24, 25] chars:[24, 25, " "]
      QUOTE[25, 34] chars:[25, 34, "<<angle>>"]
        Leaf:QUOTE_MARKER[25, 26] chars:[25, 26, "<"]
        Leaf:QUOTED_TEXT[26, 33] chars:[26, 33, "<angle>"]
        Leaf:QUOTE_MARKER[33, 34] chars:[33, 34, ">"]
      Leaf:TEXT[34, 35] chars:[34, 35, " "]
      QUOTE[35, 44] chars:[35, 44, "\"l'ordre\""]
        Leaf:QUOTE_MARKER[35, 36] chars:[35, 36, "\""]
        Leaf:QUOTED_TEXT[36, 37] chars:[36, 37, "l"]
        Leaf:QUOTED_TEXT_SMARTS[37, 38] chars:[37, 38, "'"]
        Leaf:QUOTED_TEXT[38, 43] chars:[38, 43, "ordre"]
        Leaf:QUOTE_MARKER[43, 44] chars:[43, 44, "\""]
      Leaf:TEXT[44, 45] chars:[44, 45, " "]
      QUOTE[45, 54] chars:[45, 54, "'l'ordre'"]
        Leaf:QUOTE_MARKER[45, 46] chars:[45, 46, "'"]
        Leaf:QUOTED_TEXT[46, 47] chars:[46, 47, "l"]
        Leaf:QUOTED_TEXT_SMARTS[47, 48] chars:[47, 48, "'"]
        Leaf:QUOTED_TEXT[48, 53] chars:[48, 53, "ordre"]
        Leaf:QUOTE_MARKER[53, 54] chars:[53, 54, "'"]
      Leaf:EOL[54, 55] chars:[54, 55, "\n"]
````````````````````````````````


escaped quotes

```````````````````````````````` example(Markdown elements - Markdown elements - TypographicQuotes: 2) options(pegdown-fail)
Sample \"double\" \'single\' \<<angle\>> \"l\'ordre\" \'l\'ordre\'
.
<p>Sample &quot;double&quot; &#39;single&#39; &lt;&lt;angle&gt;&gt; &quot;l&rsquo;ordre&quot; &#39;l&rsquo;ordre&#39;</p>
.
FILE[0, 66] chars:[0, 66, "Sampl … dre\'"]
  PARAGRAPH_BLOCK[0, 66] chars:[0, 66, "Sampl … dre\'"]
    TEXT_BLOCK[0, 66] chars:[0, 66, "Sampl … dre\'"]
      Leaf:TEXT[0, 7] chars:[0, 7, "Sample "]
      SPECIAL[7, 9] chars:[7, 9, "\\""]
        Leaf:SPECIAL_TEXT_MARKER[7, 8] chars:[7, 8, "\"]
        Leaf:SPECIAL_TEXT[8, 9] chars:[8, 9, "\""]
      Leaf:TEXT[9, 15] chars:[9, 15, "double"]
      SPECIAL[15, 17] chars:[15, 17, "\\""]
        Leaf:SPECIAL_TEXT_MARKER[15, 16] chars:[15, 16, "\"]
        Leaf:SPECIAL_TEXT[16, 17] chars:[16, 17, "\""]
      Leaf:TEXT[17, 18] chars:[17, 18, " "]
      SPECIAL[18, 20] chars:[18, 20, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[18, 19] chars:[18, 19, "\"]
        Leaf:SPECIAL_TEXT[19, 20] chars:[19, 20, "'"]
      Leaf:TEXT[20, 26] chars:[20, 26, "single"]
      SPECIAL[26, 28] chars:[26, 28, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[26, 27] chars:[26, 27, "\"]
        Leaf:SPECIAL_TEXT[27, 28] chars:[27, 28, "'"]
      Leaf:TEXT[28, 29] chars:[28, 29, " "]
      SPECIAL[29, 31] chars:[29, 31, "\<"]
        Leaf:SPECIAL_TEXT_MARKER[29, 30] chars:[29, 30, "\"]
        Leaf:SPECIAL_TEXT[30, 31] chars:[30, 31, "<"]
      Leaf:TEXT[31, 37] chars:[31, 37, "<angle"]
      SPECIAL[37, 39] chars:[37, 39, "\>"]
        Leaf:SPECIAL_TEXT_MARKER[37, 38] chars:[37, 38, "\"]
        Leaf:SPECIAL_TEXT[38, 39] chars:[38, 39, ">"]
      Leaf:TEXT[39, 41] chars:[39, 41, "> "]
      SPECIAL[41, 43] chars:[41, 43, "\\""]
        Leaf:SPECIAL_TEXT_MARKER[41, 42] chars:[41, 42, "\"]
        Leaf:SPECIAL_TEXT[42, 43] chars:[42, 43, "\""]
      Leaf:TEXT[43, 44] chars:[43, 44, "l"]
      SPECIAL[44, 46] chars:[44, 46, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[44, 45] chars:[44, 45, "\"]
        Leaf:SPECIAL_TEXT[45, 46] chars:[45, 46, "'"]
      Leaf:TEXT[46, 51] chars:[46, 51, "ordre"]
      SPECIAL[51, 53] chars:[51, 53, "\\""]
        Leaf:SPECIAL_TEXT_MARKER[51, 52] chars:[51, 52, "\"]
        Leaf:SPECIAL_TEXT[52, 53] chars:[52, 53, "\""]
      Leaf:TEXT[53, 54] chars:[53, 54, " "]
      SPECIAL[54, 56] chars:[54, 56, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[54, 55] chars:[54, 55, "\"]
        Leaf:SPECIAL_TEXT[55, 56] chars:[55, 56, "'"]
      Leaf:TEXT[56, 57] chars:[56, 57, "l"]
      SPECIAL[57, 59] chars:[57, 59, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[57, 58] chars:[57, 58, "\"]
        Leaf:SPECIAL_TEXT[58, 59] chars:[58, 59, "'"]
      Leaf:TEXT[59, 64] chars:[59, 64, "ordre"]
      SPECIAL[64, 66] chars:[64, 66, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[64, 65] chars:[64, 65, "\"]
        Leaf:SPECIAL_TEXT[65, 66] chars:[65, 66, "'"]
````````````````````````````````


### Markdown elements - TypographicSmarts

`TypographicSmarts`

basic

```````````````````````````````` example(Markdown elements - Markdown elements - TypographicSmarts: 1) options(flexmark-fail)
Sample with l'existence, from 1...2 and so on. . . 

en--dash and em---dash
.
<p>Sample with l&rsquo;existence, from 1&hellip;2 and so on&hellip; </p>
<p>en&ndash;dash and em&mdash;dash</p>
.
FILE[0, 76] chars:[0, 76, "Sampl … dash\n"]
  PARAGRAPH_BLOCK[0, 52] chars:[0, 52, "Sampl … . . \n"]
    TEXT_BLOCK[0, 52] chars:[0, 52, "Sampl … . . \n"]
      Leaf:TEXT[0, 13] chars:[0, 13, "Sampl … ith l"]
      Leaf:SMARTS[13, 14] chars:[13, 14, "'"]
      Leaf:TEXT[14, 31] chars:[14, 31, "exist … rom 1"]
      Leaf:SMARTS[31, 34] chars:[31, 34, "..."]
      Leaf:TEXT[34, 45] chars:[34, 45, "2 and … so on"]
      Leaf:SMARTS[45, 50] chars:[45, 50, ". . ."]
      Leaf:TEXT[50, 51] chars:[50, 51, " "]
      Leaf:EOL[51, 52] chars:[51, 52, "\n"]
  BLANK_LINE[52, 53] chars:[52, 53, "\n"]
    Leaf:BLANK_LINE[52, 53] chars:[52, 53, "\n"]
  PARAGRAPH_BLOCK[53, 76] chars:[53, 76, "en--d … dash\n"]
    TEXT_BLOCK[53, 76] chars:[53, 76, "en--d … dash\n"]
      Leaf:TEXT[53, 55] chars:[53, 55, "en"]
      Leaf:SMARTS[55, 57] chars:[55, 57, "--"]
      Leaf:TEXT[57, 68] chars:[57, 68, "dash  … nd em"]
      Leaf:SMARTS[68, 71] chars:[68, 71, "---"]
      Leaf:TEXT[71, 75] chars:[71, 75, "dash"]
      Leaf:EOL[75, 76] chars:[75, 76, "\n"]
````````````````````````````````


escaped smarts

```````````````````````````````` example(Markdown elements - Markdown elements - TypographicSmarts: 2) options(flexmark-fail)
Sample with l\'existence, from 1\...2 and so on\. . . 

en\--dash and em\---dash
.
<p>Sample with l&#39;existence, from 1...2 and so on. . . </p>
<p>en--dash and em-&ndash;dash</p>
.
FILE[0, 81] chars:[0, 81, "Sampl … dash\n"]
  PARAGRAPH_BLOCK[0, 55] chars:[0, 55, "Sampl … . . \n"]
    TEXT_BLOCK[0, 55] chars:[0, 55, "Sampl … . . \n"]
      Leaf:TEXT[0, 13] chars:[0, 13, "Sampl … ith l"]
      SPECIAL_TEXT[13, 15] chars:[13, 15, "\'"]
        Leaf:SPECIAL_TEXT_MARKER[13, 14] chars:[13, 14, "\"]
        Leaf:SPECIAL_TEXT[14, 15] chars:[14, 15, "'"]
      Leaf:TEXT[15, 32] chars:[15, 32, "exist … rom 1"]
      SPECIAL_TEXT[32, 34] chars:[32, 34, "\."]
        Leaf:SPECIAL_TEXT_MARKER[32, 33] chars:[32, 33, "\"]
        Leaf:SPECIAL_TEXT[33, 34] chars:[33, 34, "."]
      Leaf:TEXT[34, 47] chars:[34, 47, "..2 a … so on"]
      SPECIAL_TEXT[47, 49] chars:[47, 49, "\."]
        Leaf:SPECIAL_TEXT_MARKER[47, 48] chars:[47, 48, "\"]
        Leaf:SPECIAL_TEXT[48, 49] chars:[48, 49, "."]
      Leaf:TEXT[49, 54] chars:[49, 54, " . . "]
      Leaf:EOL[54, 55] chars:[54, 55, "\n"]
  BLANK_LINE[55, 56] chars:[55, 56, "\n"]
    Leaf:BLANK_LINE[55, 56] chars:[55, 56, "\n"]
  PARAGRAPH_BLOCK[56, 81] chars:[56, 81, "en\-- … dash\n"]
    TEXT_BLOCK[56, 81] chars:[56, 81, "en\-- … dash\n"]
      Leaf:TEXT[56, 58] chars:[56, 58, "en"]
      SPECIAL_TEXT[58, 60] chars:[58, 60, "\-"]
        Leaf:SPECIAL_TEXT_MARKER[58, 59] chars:[58, 59, "\"]
        Leaf:SPECIAL_TEXT[59, 60] chars:[59, 60, "-"]
      Leaf:TEXT[60, 72] chars:[60, 72, "-dash … nd em"]
      SPECIAL_TEXT[72, 74] chars:[72, 74, "\-"]
        Leaf:SPECIAL_TEXT_MARKER[72, 73] chars:[72, 73, "\"]
        Leaf:SPECIAL_TEXT[73, 74] chars:[73, 74, "-"]
      Leaf:SMARTS[74, 76] chars:[74, 76, "--"]
      Leaf:TEXT[76, 80] chars:[76, 80, "dash"]
      Leaf:EOL[80, 81] chars:[80, 81, "\n"]
````````````````````````````````


### Markdown elements - WikiLink

`WikiLink`

no spaces between brackets

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 1) options(pegdown-fail)
[ [not wiki link]]
.
<p>[ [not wiki link]]</p>
.
FILE[0, 18] chars:[0, 18, "[ [no … ink]]"]
  PARAGRAPH_BLOCK[0, 18] chars:[0, 18, "[ [no … ink]]"]
    TEXT_BLOCK[0, 18] chars:[0, 18, "[ [no … ink]]"]
      Leaf:TEXT[0, 2] chars:[0, 2, "[ "]
      REFERENCE_LINK[2, 17] chars:[2, 17, "[not  … link]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[2, 3] chars:[2, 3, "["]
        REFERENCE_LINK_REFERENCE[3, 16] chars:[3, 16, "not w …  link"]
          Leaf:TEXT[3, 16] chars:[3, 16, "not w …  link"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[16, 17] chars:[16, 17, "]"]
      Leaf:TEXT[17, 18] chars:[17, 18, "]"]
````````````````````````````````


no spaces between brackets

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 2) options(pegdown-fail)
[[not wiki link] ]
.
<p>[[not wiki link] ]</p>
.
FILE[0, 18] chars:[0, 18, "[[not … nk] ]"]
  PARAGRAPH_BLOCK[0, 18] chars:[0, 18, "[[not … nk] ]"]
    TEXT_BLOCK[0, 18] chars:[0, 18, "[[not … nk] ]"]
      Leaf:TEXT[0, 1] chars:[0, 1, "["]
      REFERENCE_LINK[1, 16] chars:[1, 16, "[not  … link]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[1, 2] chars:[1, 2, "["]
        REFERENCE_LINK_REFERENCE[2, 15] chars:[2, 15, "not w …  link"]
          Leaf:TEXT[2, 15] chars:[2, 15, "not w …  link"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[15, 16] chars:[15, 16, "]"]
      Leaf:TEXT[16, 18] chars:[16, 18, " ]"]
````````````````````````````````


simple wiki link

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 3
[[wiki link]]
.
<p><a href="./wiki-link.html">wiki link</a></p>
.
FILE[0, 14] chars:[0, 14, "[[wik … nk]]\n"]
  PARAGRAPH_BLOCK[0, 14] chars:[0, 14, "[[wik … nk]]\n"]
    TEXT_BLOCK[0, 14] chars:[0, 14, "[[wik … nk]]\n"]
      WIKI_LINK[0, 13] chars:[0, 13, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[11, 13] chars:[11, 13, "]]"]
      Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


wiki link with text

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 4
[[wiki text|wiki link]]
.
<p><a href="./wiki-text.html">wiki link</a></p>
.
FILE[0, 24] chars:[0, 24, "[[wik … nk]]\n"]
  PARAGRAPH_BLOCK[0, 24] chars:[0, 24, "[[wik … nk]]\n"]
    TEXT_BLOCK[0, 24] chars:[0, 24, "[[wik … nk]]\n"]
      WIKI_LINK[0, 23] chars:[0, 23, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_TEXT[2, 11] chars:[2, 11, "wiki text"]
          Leaf:WIKI_LINK_TEXT[2, 11] chars:[2, 11, "wiki text"]
        Leaf:WIKI_LINK_SEPARATOR[11, 12] chars:[11, 12, "|"]
        WIKI_LINK_REF[12, 21] chars:[12, 21, "wiki link"]
          Leaf:WIKI_LINK_REF[12, 21] chars:[12, 21, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[21, 23] chars:[21, 23, "]]"]
      Leaf:EOL[23, 24] chars:[23, 24, "\n"]
````````````````````````````````


simple wiki link with ! before

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 5) options(pegdown-fail)
![[wiki link]]
.
<p>![[wiki link]]</p>
.
FILE[0, 14] chars:[0, 14, "![[wi … ink]]"]
  PARAGRAPH_BLOCK[0, 14] chars:[0, 14, "![[wi … ink]]"]
    TEXT_BLOCK[0, 14] chars:[0, 14, "![[wi … ink]]"]
      Leaf:TEXT[0, 1] chars:[0, 1, "!"]
      WIKI_LINK[1, 14] chars:[1, 14, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[1, 3] chars:[1, 3, "[["]
        WIKI_LINK_REF[3, 12] chars:[3, 12, "wiki link"]
          Leaf:WIKI_LINK_REF[3, 12] chars:[3, 12, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[12, 14] chars:[12, 14, "]]"]
````````````````````````````````


wiki link with text with ! before

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 6) options(pegdown-fail)
![[wiki text|wiki link]]
.
<p>![[wiki text|wiki link]]</p>
.
FILE[0, 24] chars:[0, 24, "![[wi … ink]]"]
  PARAGRAPH_BLOCK[0, 24] chars:[0, 24, "![[wi … ink]]"]
    TEXT_BLOCK[0, 24] chars:[0, 24, "![[wi … ink]]"]
      Leaf:TEXT[0, 1] chars:[0, 1, "!"]
      WIKI_LINK[1, 24] chars:[1, 24, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[1, 3] chars:[1, 3, "[["]
        WIKI_LINK_TEXT[3, 12] chars:[3, 12, "wiki text"]
          Leaf:WIKI_LINK_TEXT[3, 12] chars:[3, 12, "wiki text"]
        Leaf:WIKI_LINK_SEPARATOR[12, 13] chars:[12, 13, "|"]
        WIKI_LINK_REF[13, 22] chars:[13, 22, "wiki link"]
          Leaf:WIKI_LINK_REF[13, 22] chars:[13, 22, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[22, 24] chars:[22, 24, "]]"]
````````````````````````````````


reference following will be a reference, even if not defined

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 7
[[wiki link]][ref]
.
<p><a href="./wiki-link.html">wiki link</a>[ref]</p>
.
FILE[0, 19] chars:[0, 19, "[[wik … ref]\n"]
  PARAGRAPH_BLOCK[0, 19] chars:[0, 19, "[[wik … ref]\n"]
    TEXT_BLOCK[0, 19] chars:[0, 19, "[[wik … ref]\n"]
      WIKI_LINK[0, 13] chars:[0, 13, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[11, 13] chars:[11, 13, "]]"]
      REFERENCE_LINK[13, 18] chars:[13, 18, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[13, 14] chars:[13, 14, "["]
        REFERENCE_LINK_REFERENCE[14, 17] chars:[14, 17, "ref"]
          Leaf:TEXT[14, 17] chars:[14, 17, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[17, 18] chars:[17, 18, "]"]
      Leaf:EOL[18, 19] chars:[18, 19, "\n"]
````````````````````````````````


reference following will be a reference

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 8
[[wiki link]][ref]

[ref]: /url
.
<p><a href="./wiki-link.html">wiki link</a><a href="/url">ref</a></p>
.
FILE[0, 32] chars:[0, 32, "[[wik … /url\n"]
  PARAGRAPH_BLOCK[0, 19] chars:[0, 19, "[[wik … ref]\n"]
    TEXT_BLOCK[0, 19] chars:[0, 19, "[[wik … ref]\n"]
      WIKI_LINK[0, 13] chars:[0, 13, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[11, 13] chars:[11, 13, "]]"]
      REFERENCE_LINK[13, 18] chars:[13, 18, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[13, 14] chars:[13, 14, "["]
        REFERENCE_LINK_REFERENCE[14, 17] chars:[14, 17, "ref"]
          Leaf:TEXT[14, 17] chars:[14, 17, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[17, 18] chars:[17, 18, "]"]
      Leaf:EOL[18, 19] chars:[18, 19, "\n"]
  BLANK_LINE[19, 20] chars:[19, 20, "\n"]
    Leaf:BLANK_LINE[19, 20] chars:[19, 20, "\n"]
  REFERENCE[20, 32] chars:[20, 32, "[ref] … /url\n"]
    Leaf:REFERENCE_TEXT_OPEN[20, 21] chars:[20, 21, "["]
    REFERENCE_TEXT[21, 24] chars:[21, 24, "ref"]
      Leaf:TEXT[21, 24] chars:[21, 24, "ref"]
    Leaf:REFERENCE_TEXT_CLOSE[24, 26] chars:[24, 26, "]:"]
    Leaf:WHITESPACE[26, 27] chars:[26, 27, " "]
    REFERENCE_LINK_REF[27, 31] chars:[27, 31, "/url"]
      Leaf:REFERENCE_LINK_REF[27, 31] chars:[27, 31, "/url"]
    Leaf:EOL[31, 32] chars:[31, 32, "\n"]
````````````````````````````````


dummy reference following will be an empty reference

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 9) options(pegdown-fail)
[[wiki link]][]
.
<p><a href="./wiki-link.html">wiki link</a>[]</p>
.
FILE[0, 15] chars:[0, 15, "[[wik … k]][]"]
  PARAGRAPH_BLOCK[0, 15] chars:[0, 15, "[[wik … k]][]"]
    TEXT_BLOCK[0, 15] chars:[0, 15, "[[wik … k]][]"]
      WIKI_LINK[0, 13] chars:[0, 13, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[11, 13] chars:[11, 13, "]]"]
      REFERENCE_LINK[13, 15] chars:[13, 15, "[]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[13, 14] chars:[13, 14, "["]
        REFERENCE_LINK_REFERENCE[14, 14]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[14, 15] chars:[14, 15, "]"]
````````````````````````````````


reference inside is not a wiki link but a link ref with brackets around it

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 10) options(pegdown-fail)
[[not wiki link][ref]]
.
<p><a href="./not-wiki-link%5D%5Bref.html">not wiki link][ref</a></p>
.
FILE[0, 22] chars:[0, 22, "[[not … ref]]"]
  PARAGRAPH_BLOCK[0, 22] chars:[0, 22, "[[not … ref]]"]
    TEXT_BLOCK[0, 22] chars:[0, 22, "[[not … ref]]"]
      Leaf:TEXT[0, 1] chars:[0, 1, "["]
      REFERENCE_LINK[1, 21] chars:[1, 21, "[not  … [ref]"]
        Leaf:REFERENCE_LINK_TEXT_OPEN[1, 2] chars:[1, 2, "["]
        REFERENCE_LINK_TEXT[2, 15] chars:[2, 15, "not w …  link"]
          Leaf:TEXT[2, 15] chars:[2, 15, "not w …  link"]
        Leaf:REFERENCE_LINK_TEXT_CLOSE[15, 16] chars:[15, 16, "]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN[16, 17] chars:[16, 17, "["]
        REFERENCE_LINK_REFERENCE[17, 20] chars:[17, 20, "ref"]
          Leaf:WHITESPACE[17, 20] chars:[17, 20, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE[20, 21] chars:[20, 21, "]"]
      Leaf:TEXT[21, 22] chars:[21, 22, "]"]
````````````````````````````````


dummy reference inside is not a wiki link but a link ref with brackets around it

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 11) options(pegdown-fail)
[[not wiki link][]]
.
<p><a href="./not-wiki-link%5D%5B.html">not wiki link][</a></p>
.
FILE[0, 19] chars:[0, 19, "[[not … k][]]"]
  PARAGRAPH_BLOCK[0, 19] chars:[0, 19, "[[not … k][]]"]
    TEXT_BLOCK[0, 19] chars:[0, 19, "[[not … k][]]"]
      Leaf:TEXT[0, 1] chars:[0, 1, "["]
      REFERENCE_LINK[1, 18] chars:[1, 18, "[not  … nk][]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[1, 2] chars:[1, 2, "["]
        REFERENCE_LINK_REFERENCE[2, 15] chars:[2, 15, "not w …  link"]
          Leaf:TEXT[2, 15] chars:[2, 15, "not w …  link"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[15, 16] chars:[15, 16, "]"]
        Leaf:DUMMY_REFERENCE[16, 18] chars:[16, 18, "[]"]
      Leaf:TEXT[18, 19] chars:[18, 19, "]"]
````````````````````````````````


```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 12) options(pegdown-fail)
[[wiki link]] [^link][ref] [[^wiki link]]
.
<p><a href="./wiki-link.html">wiki link</a> <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup>[ref] [[^wiki link]]</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p><a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 41] chars:[0, 41, "[[wik … ink]]"]
  PARAGRAPH_BLOCK[0, 41] chars:[0, 41, "[[wik … ink]]"]
    TEXT_BLOCK[0, 41] chars:[0, 41, "[[wik … ink]]"]
      WIKI_LINK[0, 13] chars:[0, 13, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[11, 13] chars:[11, 13, "]]"]
      Leaf:TEXT[13, 14] chars:[13, 14, " "]
      FOOTNOTE_REF[14, 21] chars:[14, 21, "[^link]"]
        Leaf:FOOTNOTE_REF_OPEN[14, 16] chars:[14, 16, "[^"]
        FOOTNOTE_REF_ID[16, 20] chars:[16, 20, "link"]
          Leaf:FOOTNOTE_REF_ID[16, 20] chars:[16, 20, "link"]
        Leaf:FOOTNOTE_REF_CLOSE[20, 21] chars:[20, 21, "]"]
      REFERENCE_LINK[21, 26] chars:[21, 26, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[21, 22] chars:[21, 22, "["]
        REFERENCE_LINK_REFERENCE[22, 25] chars:[22, 25, "ref"]
          Leaf:TEXT[22, 25] chars:[22, 25, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[25, 26] chars:[25, 26, "]"]
      Leaf:TEXT[26, 27] chars:[26, 27, " "]
      WIKI_LINK[27, 41] chars:[27, 41, "[[^wi … ink]]"]
        Leaf:WIKI_LINK_OPEN[27, 29] chars:[27, 29, "[["]
        WIKI_LINK_REF[29, 39] chars:[29, 39, "^wiki link"]
          Leaf:WIKI_LINK_REF[29, 39] chars:[29, 39, "^wiki link"]
        Leaf:WIKI_LINK_CLOSE[39, 41] chars:[39, 41, "]]"]
````````````````````````````````


Exclamation before is just text

```````````````````````````````` example(Markdown elements - Markdown elements - WikiLink: 13) options(pegdown-fail)
![[wiki link]] [^link][ref] [[^wiki link]] [[wiki]][ref]
.
<p>![[wiki link]] <sup id="fnref-1"><a href="#fn-1" class="footnote-ref">1</a></sup>[ref] [[^wiki link]] <a href="./wiki.html">wiki</a>[ref]</p><div class="footnotes">
<hr/>
<ol>
<li id="fn-1"><p><a href="#fnref-1" class="footnote-backref">&#8617;</a></p></li>
</ol>
</div>

.
FILE[0, 56] chars:[0, 56, "![[wi … [ref]"]
  PARAGRAPH_BLOCK[0, 56] chars:[0, 56, "![[wi … [ref]"]
    TEXT_BLOCK[0, 56] chars:[0, 56, "![[wi … [ref]"]
      Leaf:TEXT[0, 1] chars:[0, 1, "!"]
      WIKI_LINK[1, 14] chars:[1, 14, "[[wik … ink]]"]
        Leaf:WIKI_LINK_OPEN[1, 3] chars:[1, 3, "[["]
        WIKI_LINK_REF[3, 12] chars:[3, 12, "wiki link"]
          Leaf:WIKI_LINK_REF[3, 12] chars:[3, 12, "wiki link"]
        Leaf:WIKI_LINK_CLOSE[12, 14] chars:[12, 14, "]]"]
      Leaf:TEXT[14, 15] chars:[14, 15, " "]
      FOOTNOTE_REF[15, 22] chars:[15, 22, "[^link]"]
        Leaf:FOOTNOTE_REF_OPEN[15, 17] chars:[15, 17, "[^"]
        FOOTNOTE_REF_ID[17, 21] chars:[17, 21, "link"]
          Leaf:FOOTNOTE_REF_ID[17, 21] chars:[17, 21, "link"]
        Leaf:FOOTNOTE_REF_CLOSE[21, 22] chars:[21, 22, "]"]
      REFERENCE_LINK[22, 27] chars:[22, 27, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[22, 23] chars:[22, 23, "["]
        REFERENCE_LINK_REFERENCE[23, 26] chars:[23, 26, "ref"]
          Leaf:TEXT[23, 26] chars:[23, 26, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[26, 27] chars:[26, 27, "]"]
      Leaf:TEXT[27, 28] chars:[27, 28, " "]
      WIKI_LINK[28, 42] chars:[28, 42, "[[^wi … ink]]"]
        Leaf:WIKI_LINK_OPEN[28, 30] chars:[28, 30, "[["]
        WIKI_LINK_REF[30, 40] chars:[30, 40, "^wiki link"]
          Leaf:WIKI_LINK_REF[30, 40] chars:[30, 40, "^wiki link"]
        Leaf:WIKI_LINK_CLOSE[40, 42] chars:[40, 42, "]]"]
      Leaf:TEXT[42, 43] chars:[42, 43, " "]
      WIKI_LINK[43, 51] chars:[43, 51, "[[wiki]]"]
        Leaf:WIKI_LINK_OPEN[43, 45] chars:[43, 45, "[["]
        WIKI_LINK_REF[45, 49] chars:[45, 49, "wiki"]
          Leaf:WIKI_LINK_REF[45, 49] chars:[45, 49, "wiki"]
        Leaf:WIKI_LINK_CLOSE[49, 51] chars:[49, 51, "]]"]
      REFERENCE_LINK[51, 56] chars:[51, 56, "[ref]"]
        Leaf:REFERENCE_LINK_REFERENCE_OPEN2[51, 52] chars:[51, 52, "["]
        REFERENCE_LINK_REFERENCE[52, 55] chars:[52, 55, "ref"]
          Leaf:TEXT[52, 55] chars:[52, 55, "ref"]
        Leaf:REFERENCE_LINK_REFERENCE_CLOSE2[55, 56] chars:[55, 56, "]"]
````````````````````````````````


With empty anchor ref

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 14
[[wiki link#]] 
.
<p><a href="./wiki-link.html#">wiki link#</a> </p>
.
FILE[0, 16] chars:[0, 16, "[[wik … #]] \n"]
  PARAGRAPH_BLOCK[0, 16] chars:[0, 16, "[[wik … #]] \n"]
    TEXT_BLOCK[0, 16] chars:[0, 16, "[[wik … #]] \n"]
      WIKI_LINK[0, 14] chars:[0, 14, "[[wik … nk#]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_REF_ANCHOR_MARKER[11, 12] chars:[11, 12, "#"]
        WIKI_LINK_REF_ANCHOR[12, 12]
        Leaf:WIKI_LINK_CLOSE[12, 14] chars:[12, 14, "]]"]
      Leaf:TEXT[14, 15] chars:[14, 15, " "]
      Leaf:EOL[15, 16] chars:[15, 16, "\n"]
````````````````````````````````


With Anchor ref

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 15
[[wiki link#anchor-ref]] 
.
<p><a href="./wiki-link.html#anchor-ref">wiki link#anchor-ref</a> </p>
.
FILE[0, 26] chars:[0, 26, "[[wik … f]] \n"]
  PARAGRAPH_BLOCK[0, 26] chars:[0, 26, "[[wik … f]] \n"]
    TEXT_BLOCK[0, 26] chars:[0, 26, "[[wik … f]] \n"]
      WIKI_LINK[0, 24] chars:[0, 24, "[[wik … ref]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
          Leaf:WIKI_LINK_REF[2, 11] chars:[2, 11, "wiki link"]
        Leaf:WIKI_LINK_REF_ANCHOR_MARKER[11, 12] chars:[11, 12, "#"]
        WIKI_LINK_REF_ANCHOR[12, 22] chars:[12, 22, "anchor-ref"]
          Leaf:WIKI_LINK_REF_ANCHOR[12, 22] chars:[12, 22, "anchor-ref"]
        Leaf:WIKI_LINK_CLOSE[22, 24] chars:[22, 24, "]]"]
      Leaf:TEXT[24, 25] chars:[24, 25, " "]
      Leaf:EOL[25, 26] chars:[25, 26, "\n"]
````````````````````````````````


With text, empty anchor ref

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 16
[[wiki text|wiki link#]] 
.
<p><a href="./wiki-text.html">wiki link#</a> </p>
.
FILE[0, 26] chars:[0, 26, "[[wik … #]] \n"]
  PARAGRAPH_BLOCK[0, 26] chars:[0, 26, "[[wik … #]] \n"]
    TEXT_BLOCK[0, 26] chars:[0, 26, "[[wik … #]] \n"]
      WIKI_LINK[0, 24] chars:[0, 24, "[[wik … nk#]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_TEXT[2, 11] chars:[2, 11, "wiki text"]
          Leaf:WIKI_LINK_TEXT[2, 11] chars:[2, 11, "wiki text"]
        Leaf:WIKI_LINK_SEPARATOR[11, 12] chars:[11, 12, "|"]
        WIKI_LINK_REF[12, 21] chars:[12, 21, "wiki link"]
          Leaf:WIKI_LINK_REF[12, 21] chars:[12, 21, "wiki link"]
        Leaf:WIKI_LINK_REF_ANCHOR_MARKER[21, 22] chars:[21, 22, "#"]
        WIKI_LINK_REF_ANCHOR[22, 22]
        Leaf:WIKI_LINK_CLOSE[22, 24] chars:[22, 24, "]]"]
      Leaf:TEXT[24, 25] chars:[24, 25, " "]
      Leaf:EOL[25, 26] chars:[25, 26, "\n"]
````````````````````````````````


With text, anchor ref

```````````````````````````````` example Markdown elements - Markdown elements - WikiLink: 17
[[wiki text|wiki link#anchor-ref]] 
.
<p><a href="./wiki-text.html">wiki link#anchor-ref</a> </p>
.
FILE[0, 36] chars:[0, 36, "[[wik … f]] \n"]
  PARAGRAPH_BLOCK[0, 36] chars:[0, 36, "[[wik … f]] \n"]
    TEXT_BLOCK[0, 36] chars:[0, 36, "[[wik … f]] \n"]
      WIKI_LINK[0, 34] chars:[0, 34, "[[wik … ref]]"]
        Leaf:WIKI_LINK_OPEN[0, 2] chars:[0, 2, "[["]
        WIKI_LINK_TEXT[2, 11] chars:[2, 11, "wiki text"]
          Leaf:WIKI_LINK_TEXT[2, 11] chars:[2, 11, "wiki text"]
        Leaf:WIKI_LINK_SEPARATOR[11, 12] chars:[11, 12, "|"]
        WIKI_LINK_REF[12, 21] chars:[12, 21, "wiki link"]
          Leaf:WIKI_LINK_REF[12, 21] chars:[12, 21, "wiki link"]
        Leaf:WIKI_LINK_REF_ANCHOR_MARKER[21, 22] chars:[21, 22, "#"]
        WIKI_LINK_REF_ANCHOR[22, 32] chars:[22, 32, "anchor-ref"]
          Leaf:WIKI_LINK_REF_ANCHOR[22, 32] chars:[22, 32, "anchor-ref"]
        Leaf:WIKI_LINK_CLOSE[32, 34] chars:[32, 34, "]]"]
      Leaf:TEXT[34, 35] chars:[34, 35, " "]
      Leaf:EOL[35, 36] chars:[35, 36, "\n"]
````````````````````````````````


### Issue xxx-04

nested lists should have correct offsets

```````````````````````````````` example Markdown elements - Issue xxx-04: 1
* 
  * list item
.
<ul>
  <li>
    <ul>
      <li>list item</li>
    </ul>
  </li>
</ul>
.
FILE[0, 17] chars:[0, 17, "* \n   … item\n"]
  BULLET_LIST[0, 17] chars:[0, 17, "* \n   … item\n"]
    BULLET_LIST_ITEM[0, 3] chars:[0, 3, "* \n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "* "]
      Leaf:EOL[2, 3] chars:[2, 3, "\n"]
    Leaf:WHITESPACE[3, 5] chars:[3, 5, "  "]
    BULLET_LIST_ITEM[5, 17] chars:[5, 17, "* lis … item\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[5, 7] chars:[5, 7, "* "]
      TEXT_BLOCK[7, 17] chars:[7, 17, "list item\n"]
        Leaf:TEXT[7, 16] chars:[7, 16, "list item"]
        Leaf:EOL[16, 17] chars:[16, 17, "\n"]
````````````````````````````````


nested lists should have correct offsets

```````````````````````````````` example Markdown elements - Issue xxx-04: 2
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
FILE[0, 14] chars:[0, 14, "* * l … item\n"]
  BULLET_LIST[0, 14] chars:[0, 14, "* * l … item\n"]
    BULLET_LIST_ITEM[0, 14] chars:[0, 14, "* * l … item\n"]
      Leaf:BULLET_LIST_ITEM_MARKER[0, 2] chars:[0, 2, "* "]
      BULLET_LIST[2, 14] chars:[2, 14, "* lis … item\n"]
        BULLET_LIST_ITEM[2, 14] chars:[2, 14, "* lis … item\n"]
          Leaf:BULLET_LIST_ITEM_MARKER[2, 4] chars:[2, 4, "* "]
          TEXT_BLOCK[4, 14] chars:[4, 14, "list item\n"]
            Leaf:TEXT[4, 13] chars:[4, 13, "list item"]
            Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


lists with block quotes

```````````````````````````````` example Markdown elements - Issue xxx-04: 3
> 1. item 1
> 2. item 2
> 3. item 3
> 4. item 4
.
<ul>
  <li>
    <ul>
      <li>list item</li>
    </ul>
  </li>
</ul>
.
FILE[0, 48] chars:[0, 48, "> 1.  … em 4\n"]
  BLOCK_QUOTE[0, 48] chars:[0, 48, "> 1.  … em 4\n"]
    Leaf:BLOCK_QUOTE_MARKER[0, 2] chars:[0, 2, "> "]
    ORDERED_LIST[2, 48] chars:[2, 48, "1. it … em 4\n"]
      ORDERED_LIST_ITEM[2, 12] chars:[2, 12, "1. item 1\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[2, 5] chars:[2, 5, "1. "]
        TEXT_BLOCK[5, 12] chars:[5, 12, "item 1\n"]
          Leaf:TEXT[5, 11] chars:[5, 11, "item 1"]
          Leaf:EOL[11, 12] chars:[11, 12, "\n"]
      Leaf:WHITESPACE[12, 14] chars:[12, 14, "> "]
      ORDERED_LIST_ITEM[14, 24] chars:[14, 24, "2. item 2\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[14, 17] chars:[14, 17, "2. "]
        TEXT_BLOCK[17, 24] chars:[17, 24, "item 2\n"]
          Leaf:TEXT[17, 23] chars:[17, 23, "item 2"]
          Leaf:EOL[23, 24] chars:[23, 24, "\n"]
      Leaf:WHITESPACE[24, 26] chars:[24, 26, "> "]
      ORDERED_LIST_ITEM[26, 36] chars:[26, 36, "3. item 3\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[26, 29] chars:[26, 29, "3. "]
        TEXT_BLOCK[29, 36] chars:[29, 36, "item 3\n"]
          Leaf:TEXT[29, 35] chars:[29, 35, "item 3"]
          Leaf:EOL[35, 36] chars:[35, 36, "\n"]
      Leaf:WHITESPACE[36, 38] chars:[36, 38, "> "]
      ORDERED_LIST_ITEM[38, 48] chars:[38, 48, "4. item 4\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[38, 41] chars:[38, 41, "4. "]
        TEXT_BLOCK[41, 48] chars:[41, 48, "item 4\n"]
          Leaf:TEXT[41, 47] chars:[41, 47, "item 4"]
          Leaf:EOL[47, 48] chars:[47, 48, "\n"]
````````````````````````````````


lists with aside block

```````````````````````````````` example(Markdown elements - Issue xxx-04: 4) options(IGNORE)
| 1. item 1
| 2. item 2
| 3. item 3
| 4. item 4
.
.
FILE[0, 48] chars:[0, 48, "| 1.  … em 4\n"]
  ASIDE_BLOCK[0, 48] chars:[0, 48, "| 1.  … em 4\n"]
    Leaf:ASIDE_BLOCK_MARKER[0, 2] chars:[0, 2, "| "]
    ORDERED_LIST[2, 48] chars:[2, 48, "1. it … em 4\n"]
      ORDERED_LIST_ITEM[2, 12] chars:[2, 12, "1. item 1\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[2, 5] chars:[2, 5, "1. "]
        TEXT_BLOCK[5, 12] chars:[5, 12, "item 1\n"]
          Leaf:TEXT[5, 11] chars:[5, 11, "item 1"]
          Leaf:EOL[11, 12] chars:[11, 12, "\n"]
      Leaf:WHITESPACE[12, 14] chars:[12, 14, "| "]
      ORDERED_LIST_ITEM[14, 24] chars:[14, 24, "2. item 2\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[14, 17] chars:[14, 17, "2. "]
        TEXT_BLOCK[17, 24] chars:[17, 24, "item 2\n"]
          Leaf:TEXT[17, 23] chars:[17, 23, "item 2"]
          Leaf:EOL[23, 24] chars:[23, 24, "\n"]
      Leaf:WHITESPACE[24, 26] chars:[24, 26, "| "]
      ORDERED_LIST_ITEM[26, 36] chars:[26, 36, "3. item 3\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[26, 29] chars:[26, 29, "3. "]
        TEXT_BLOCK[29, 36] chars:[29, 36, "item 3\n"]
          Leaf:TEXT[29, 35] chars:[29, 35, "item 3"]
          Leaf:EOL[35, 36] chars:[35, 36, "\n"]
      Leaf:WHITESPACE[36, 38] chars:[36, 38, "| "]
      ORDERED_LIST_ITEM[38, 48] chars:[38, 48, "4. item 4\n"]
        Leaf:ORDERED_LIST_ITEM_MARKER[38, 41] chars:[38, 41, "4. "]
        TEXT_BLOCK[41, 48] chars:[41, 48, "item 4\n"]
          Leaf:TEXT[41, 47] chars:[41, 47, "item 4"]
          Leaf:EOL[47, 48] chars:[47, 48, "\n"]
````````````````````````````````


## GitHub Issue Marker

```````````````````````````````` example GitHub Issue Marker: 1
issue # 
.
<p>issue # </p>
.
FILE[0, 9] chars:[0, 9, "issue # \n"]
  PARAGRAPH_BLOCK[0, 9] chars:[0, 9, "issue # \n"]
    TEXT_BLOCK[0, 9] chars:[0, 9, "issue # \n"]
      Leaf:TEXT[0, 6] chars:[0, 6, "issue "]
      Leaf:ISSUE_MARKER[6, 7] chars:[6, 7, "#"]
      Leaf:TEXT[7, 8] chars:[7, 8, " "]
      Leaf:EOL[8, 9] chars:[8, 9, "\n"]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 2
*issue #* 
.
<p><em>issue #</em> </p>
.
FILE[0, 11] chars:[0, 11, "*issu …  #* \n"]
  PARAGRAPH_BLOCK[0, 11] chars:[0, 11, "*issu …  #* \n"]
    TEXT_BLOCK[0, 11] chars:[0, 11, "*issu …  #* \n"]
      ITALIC[0, 9] chars:[0, 9, "*issue #*"]
        Leaf:ITALIC_MARKER[0, 1] chars:[0, 1, "*"]
        Leaf:ITALIC_TEXT[1, 7] chars:[1, 7, "issue "]
        Leaf:ITALIC_TEXT_ISSUE_MARKER[7, 8] chars:[7, 8, "#"]
        Leaf:ITALIC_MARKER[8, 9] chars:[8, 9, "*"]
      Leaf:TEXT[9, 10] chars:[9, 10, " "]
      Leaf:EOL[10, 11] chars:[10, 11, "\n"]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 3
**issue #** 
.
<p><strong>issue #</strong> </p>
.
FILE[0, 13] chars:[0, 13, "**iss … #** \n"]
  PARAGRAPH_BLOCK[0, 13] chars:[0, 13, "**iss … #** \n"]
    TEXT_BLOCK[0, 13] chars:[0, 13, "**iss … #** \n"]
      BOLD[0, 11] chars:[0, 11, "**iss … e #**"]
        Leaf:BOLD_MARKER[0, 2] chars:[0, 2, "**"]
        Leaf:BOLD_TEXT[2, 8] chars:[2, 8, "issue "]
        Leaf:BOLD_TEXT_ISSUE_MARKER[8, 9] chars:[8, 9, "#"]
        Leaf:BOLD_MARKER[9, 11] chars:[9, 11, "**"]
      Leaf:TEXT[11, 12] chars:[11, 12, " "]
      Leaf:EOL[12, 13] chars:[12, 13, "\n"]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 4
**_issue #_**
.
<p><strong><em>issue #</em></strong></p>
.
FILE[0, 14] chars:[0, 14, "**_is … #_**\n"]
  PARAGRAPH_BLOCK[0, 14] chars:[0, 14, "**_is … #_**\n"]
    TEXT_BLOCK[0, 14] chars:[0, 14, "**_is … #_**\n"]
      BOLD[0, 13] chars:[0, 13, "**_is …  #_**"]
        Leaf:BOLD_MARKER[0, 2] chars:[0, 2, "**"]
        ITALIC[2, 11] chars:[2, 11, "_issue #_"]
          Leaf:ITALIC_MARKER[2, 3] chars:[2, 3, "_"]
          Leaf:BOLD_TEXT_ITALIC_TEXT[3, 9] chars:[3, 9, "issue "]
          Leaf:BOLD_TEXT_ITALIC_TEXT_ISSUE_MARKER[9, 10] chars:[9, 10, "#"]
          Leaf:ITALIC_MARKER[10, 11] chars:[10, 11, "_"]
        Leaf:BOLD_MARKER[11, 13] chars:[11, 13, "**"]
      Leaf:EOL[13, 14] chars:[13, 14, "\n"]
````````````````````````````````


```````````````````````````````` example GitHub Issue Marker: 5
~~_**issue #**_~~
.
<p><del><em><strong>issue #</strong></em></del></p>
.
FILE[0, 18] chars:[0, 18, "~~_** … *_~~\n"]
  PARAGRAPH_BLOCK[0, 18] chars:[0, 18, "~~_** … *_~~\n"]
    TEXT_BLOCK[0, 18] chars:[0, 18, "~~_** … *_~~\n"]
      STRIKETHROUGH[0, 17] chars:[0, 17, "~~_** … **_~~"]
        Leaf:STRIKETHROUGH_MARKER[0, 2] chars:[0, 2, "~~"]
        ITALIC[2, 15] chars:[2, 15, "_**is …  #**_"]
          Leaf:ITALIC_MARKER[2, 3] chars:[2, 3, "_"]
          BOLD[3, 14] chars:[3, 14, "**iss … e #**"]
            Leaf:BOLD_MARKER[3, 5] chars:[3, 5, "**"]
            Leaf:STRIKETHROUGH_TEXT_BOLD_TEXT_ITALIC_TEXT[5, 11] chars:[5, 11, "issue "]
            Leaf:STRIKETHROUGH_TEXT_BOLD_TEXT_ITALIC_TEXT_ISSUE_MARKER[11, 12] chars:[11, 12, "#"]
            Leaf:BOLD_MARKER[12, 14] chars:[12, 14, "**"]
          Leaf:ITALIC_MARKER[14, 15] chars:[14, 15, "_"]
        Leaf:STRIKETHROUGH_MARKER[15, 17] chars:[15, 17, "~~"]
      Leaf:EOL[17, 18] chars:[17, 18, "\n"]
````````````````````````````````


