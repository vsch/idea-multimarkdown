---
title: Typing Handler Spec Test
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Typing Handler

```````````````````````````````` example(Typing Handler: 1) options(type[ ])
⦙text
.
 ⦙text
````````````````````````````````


```````````````````````````````` example(Typing Handler: 2) options(type[ ])
⦙ text
.
 ⦙ text
````````````````````````````````


```````````````````````````````` example(Typing Handler: 3) options(wrap, margin[96], type[ ])
* Fix: for⦙#651, Drop image with dialog issues
.
* Fix: for ⦙#651, Drop image with dialog issues
````````````````````````````````


```````````````````````````````` example(Typing Handler: 4) options(wrap, margin[30], type[ ])
⦙text should wrap onto the next line at right margin of 30
.
 ⦙text should wrap onto the
 next line at right margin of
 30
````````````````````````````````


```````````````````````````````` example(Typing Handler: 5) options(wrap, margin[30], type[t], with-ranges)
text should wrap onto the next ⦙
line at right margin of 30
.
text should wrap onto the next
t⦙ line at right margin of 30

---- RANGES ------------------------------------------------------------
⟦text should wrap onto the next⟧
⟦⟧⟦t⟧ ⟦line at right margin of 30⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[0, 59), s=0:0, u=0:0, t=3:3, l=60, sz=8, na=6: [0, 30), '\n', [30), [31, 32), ' ', [33, 59), '\n', [59) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 6) options(wrap, margin[32], type[t])
text should wrap onto the next t⦙
line at right margin of 30
.
text should wrap onto the next
tt⦙ line at right margin of 30
````````````````````````````````


```````````````````````````````` example(Typing Handler: 7) options(wrap, margin[32], backspace)
text should wrap onto the next
tt⦙ line at right margin of 30
.
text should wrap onto the next t⦙
line at right margin of 30
````````````````````````````````


```````````````````````````````` example(Typing Handler: 8) options(wrap, margin[30], type[ ])
text should wrap onto the next⦙.
line at right margin of 30
.
text should wrap onto the next
⦙. line at right margin of 30
````````````````````````````````


```````````````````````````````` example(Typing Handler: 9) options(wrap, margin[32], type[ ])
text should wrap onto the next⦙\\. 
line at right margin of 30
.
text should wrap onto the next
⦙\\. line at right margin of 30
````````````````````````````````


```````````````````````````````` example(Typing Handler: 10) options(wrap, margin[32], type[ ])
text should wrap onto the next ⦙\\. 
line at right margin of 30
.
text should wrap onto the next
⦙\\. line at right margin of 30
````````````````````````````````


```````````````````````````````` example(Typing Handler: 11) options(wrap, margin[96], type[d], with-ranges)
## Some Heading
    
Text to shift offset 
    
* Fix: Jekyll `{% include "" %}` completions would not work unless there was an `.html`
  extension between the strings. a;lsdfj ladsfj dlsf; jlasdfj l;asdfj lads;fj lasdfj l;dsaj flad⦙
  asdfasfdsaffdsa
.
## Some Heading
    
Text to shift offset 
    
* Fix: Jekyll `{% include "" %}` completions would not work unless there was an `.html`
  extension between the strings. a;lsdfj ladsfj dlsf; jlasdfj l;asdfj lads;fj lasdfj l;dsaj
  fladd⦙ asdfasfdsaffdsa

---- RANGES ------------------------------------------------------------
⟦⟧* ⟦Fix: Jekyll `{% include "" %}` completions would not work unless there was an `.html`
  extension between the strings. a;lsdfj ladsfj dlsf; jlasdfj l;asdfj lads;fj lasdfj l;dsaj⟧
⟦⟧ ⟦ fladd⟧⟦ asdfasfdsaffdsa⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 251), s=0:0, u=0:0, t=4:5, l=204, sz=10, na=7: [50), '* ', [50, 227), '\n', [227), ' ', [227, 233), [235, 251), '\n', [251) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 12) options(wrap, margin[96], format-element, with-ranges)
## Some Heading
    
Text to shift offset 
    
* [ ] Fix: mixed task and non-task items, toggle prefix adds it to all instead of removing only task
      item prefix or adding to only list items. Test is done.⦙
.
## Some Heading
    
Text to shift offset 
    
* [ ] Fix: mixed task and non-task items, toggle prefix adds it to all instead of removing only
      task item prefix or adding to only list items. Test is done.⦙

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: mixed task and non-task items, toggle prefix adds it to all instead of removing only⟧
⟦⟧     ⟦ task⟧⟦ item prefix or adding to only list items. Test is done.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[54, 210), s=0:0, u=0:0, t=4:13, l=163, sz=10, na=7: [54), '* [ ] ', [54, 143), '\n', [143), '     ', [143, 148), [154, 210), '\n', [210) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 13) options(wrap, type[t], margin[96], with-ranges)
## Markdown Navigator

[TOC levels=3,4]: # "Version History"

# Version History
- [2.7.0.8 - Bug Fix & Enhancement Release](#2708---bug-fix--enhancement-release)
- [2.7.0 - Bug Fix & Enhancement Release](#270---bug-fix--enhancement-release)
- [2.6.0 - Bug Fix & Enhancement Release](#260---bug-fix--enhancement-release)
- [2.5.4 - Bug Fix Release](#254---bug-fix-release)
- [2.5.2 - Bug Fix & Enhancement Release](#252---bug-fix--enhancement-release)
- [2.4.0 - Bug Fix & Enhancement Release](#240---bug-fix--enhancement-release)
- [2.3.8 - Bug Fix Release](#238---bug-fix-release)
- [2.3.7 - Bug Fix Release](#237---bug-fix-release)
- [2.3.6 - Bug Fix & Enhancement Release](#236---bug-fix--enhancement-release)
- [2.3.5 - Bug Fix & Enhancement Release](#235---bug-fix--enhancement-release)

### 2.7.0.8 - Bug Fix & Enhancement Release

* Fix: copy fixed utils from Arduino Support plugin.
* item <http://> text https://
* Fix: nasty bug introducing typing delay with preview enabled.
* Fix: diagnostic-2012, Kotlin NPE.
* Fix: Paste Image: old crop settings out of bounds for new image caused exception
* Fix: for #651, Drop image with dialog issues
  * Spaces in file name were url encoded
  * Copy dragging a file leaves its original directory instead of setting it to the closest or bes⦙ 
    guess based on the destination file. Should be the same
    as if the image was pasted into the file. If the destination directory is
    the same as the source then a new name should be generated to uniquify it.
* Add: in Paste/Modify Image if dragging the highlight selection without having
  highlight enabled or no border, inner nor outer fill enabled, will enable
  highlight and border to provide feedback otherwise it is confusing.
  * Add: drag selection can be used for cropping if image tab is selected and
    `Use mouse selection only for highlight` is not selected.
  * Fix: only copy image to transparent if Image tab is selected. The rest leave
    as is.
  * Add: restart notification if changing full highlight combinations
.
## Markdown Navigator

[TOC levels=3,4]: # "Version History"

# Version History
- [2.7.0.8 - Bug Fix & Enhancement Release](#2708---bug-fix--enhancement-release)
- [2.7.0 - Bug Fix & Enhancement Release](#270---bug-fix--enhancement-release)
- [2.6.0 - Bug Fix & Enhancement Release](#260---bug-fix--enhancement-release)
- [2.5.4 - Bug Fix Release](#254---bug-fix-release)
- [2.5.2 - Bug Fix & Enhancement Release](#252---bug-fix--enhancement-release)
- [2.4.0 - Bug Fix & Enhancement Release](#240---bug-fix--enhancement-release)
- [2.3.8 - Bug Fix Release](#238---bug-fix-release)
- [2.3.7 - Bug Fix Release](#237---bug-fix-release)
- [2.3.6 - Bug Fix & Enhancement Release](#236---bug-fix--enhancement-release)
- [2.3.5 - Bug Fix & Enhancement Release](#235---bug-fix--enhancement-release)

### 2.7.0.8 - Bug Fix & Enhancement Release

* Fix: copy fixed utils from Arduino Support plugin.
* item <http://> text https://
* Fix: nasty bug introducing typing delay with preview enabled.
* Fix: diagnostic-2012, Kotlin NPE.
* Fix: Paste Image: old crop settings out of bounds for new image caused exception
* Fix: for #651, Drop image with dialog issues
  * Spaces in file name were url encoded
  * Copy dragging a file leaves its original directory instead of setting it to the closest or
    best⦙ guess based on the destination file. Should be the same as if the image was pasted into
    the file. If the destination directory is the same as the source then a new name should be
    generated to uniquify it.
* Add: in Paste/Modify Image if dragging the highlight selection without having
  highlight enabled or no border, inner nor outer fill enabled, will enable
  highlight and border to provide feedback otherwise it is confusing.
  * Add: drag selection can be used for cropping if image tab is selected and
    `Use mouse selection only for highlight` is not selected.
  * Fix: only copy image to transparent if Image tab is selected. The rest leave
    as is.
  * Add: restart notification if changing full highlight combinations
---- RANGES ------------------------------------------------------------
⟦⟧  * ⟦Copy dragging a file leaves its original directory instead of setting it to the closest or⟧
⟦⟧   ⟦ best⟧ ⟦⟧⟦guess based on the destination file. Should be the same⟧⟦ as if the image was pasted into⟧
⟦⟧   ⟦ the file. If the destination directory is⟧⟦ the same as the source then a new name should be⟧
⟦⟧   ⟦ generated to uniquify it.
⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[1197, 1511), s=0:0, u=0:0, t=8:17, l=317, sz=20, na=15: [1197), '  * ', [1197, 1287), '\n', [1287), '   ', [1287, 1292), ' ', [1292), [1298, 1353), [1357, 1389), '\n', [1389), '   ', [1389, 1431), [1435, 1484), '\n', [1484), '   ', [1484, 1511) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 14) options(wrap, margin[96], type[ ], with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] ⦙Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      to keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ]  ⦙Fix: remove formatter and use flexmark formatter for document format to eliminate the
       need to keep duplicate code.

---- RANGES ------------------------------------------------------------
⟦⟧* [ ]  ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the⟧
⟦⟧      ⟦ need⟧⟦ to keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[51, 171), s=0:0, u=0:0, t=4:15, l=129, sz=10, na=7: [51), '* [ ]  ', [51, 136), '\n', [136), '      ', [136, 141), [147, 171), '\n', [171) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 15) options(wrap, margin[96], type[ ], with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need⦙ to keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need ⦙
      to keep duplicate code.

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the need⟧ ⟦⟧
⟦⟧    ⟦  to keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 165), s=0:0, u=0:0, t=5:13, l=128, sz=11, na=7: [50), '* [ ] ', [50, 140), ' ', [140), '\n', [140), '    ', [140, 165), '\n', [165) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 16) options(wrap, margin[96], type[ ], with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      ⦙to keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      ⦙to keep duplicate code.

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the need
 ⟧⟦     to keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 171), s=0:0, u=0:0, t=2:7, l=127, sz=6, na=4: [50), '* [ ] ', [50, 142), [143, 171), '\n', [171) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 17) options(wrap, margin[96], type[ ], with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      to⦙ keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      to ⦙ keep duplicate code.
---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      to  keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 171), s=0:0, u=0:0, t=2:7, l=128, sz=5, na=3: [50), '* [ ] ', [50, 171), '\n', [171) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 18) options(wrap, margin[96], backspace, with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      t⦙ keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need ⦙
      keep duplicate code.

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the need⟧ ⟦
 ⟧⟦     keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 168), s=0:0, u=0:0, t=3:8, l=125, sz=8, na=6: [50), '* [ ] ', [50, 140), ' ', [140, 142), [143, 168), '\n', [168) }
````````````````````````````````


backspace on first non-blank should splice lines.

```````````````````````````````` example(Typing Handler: 19) options(wrap, margin[96], backspace, with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      ⦙keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the
      need⦙keep duplicate code.

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the⟧
⟦⟧     ⟦ need⟧⟦keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 166), s=0:0, u=0:0, t=4:13, l=123, sz=10, na=7: [50), '* [ ] ', [50, 135), '\n', [135), '     ', [135, 140), [146, 166), '\n', [166) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 20) options(wrap, margin[96], backspace, with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      keep duplicate code.
      ⦙keep duplicate code.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* [ ] Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      keep duplicate code.⦙keep duplicate code.

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Fix: remove formatter and use flexmark formatter for document format to eliminate the need
      keep duplicate code.⟧⟦keep duplicate code.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[50, 193), s=0:0, u=0:0, t=2:7, l=144, sz=6, na=4: [50), '* [ ] ', [50, 167), [173, 193), '\n', [193) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 21) options(wrap, margin[96], type[ ], with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* Fix: conversion from Smart to based to extract more source information from segmented
  sequence⦙and mapped sequence.
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* Fix: conversion from Smart to based to extract more source information from segmented sequence
  ⦙and mapped sequence.

---- RANGES ------------------------------------------------------------
⟦⟧* ⟦Fix: conversion from Smart to based to extract more source information from segmented⟧⟦ sequence⟧
⟦⟧ ⟦ and mapped sequence.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[46, 163), s=0:0, u=0:0, t=4:5, l=120, sz=10, na=7: [46), '* ', [46, 131), [133, 142), '\n', [142), ' ', [142, 163), '\n', [163) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 22) options(wrap, margin[96], type[ ], with-ranges)
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* Fix: wrap on typing caret adjustment on space after non-space and before keep at start of line elements.⦙
.
### Next 2.9.0.227/2.9.7.227 - Dev Build
  
* Fix: wrap on typing caret adjustment on space after non-space and before keep at start of line
  elements. ⦙

---- RANGES ------------------------------------------------------------
⟦⟧* ⟦Fix: wrap on typing caret adjustment on space after non-space and before keep at start of line⟧
⟦⟧ ⟦ elements.⟧ ⟦⟧⟦⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[46, 151), s=0:0, u=0:0, t=5:6, l=110, sz=12, na=7: [46), '* ', [46, 140), '\n', [140), ' ', [140, 150), ' ', [150), [151), '\n', [151) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 23) options(wrap, margin[66], type[ ], with-ranges, style-keep-at-start-explicit-link-all)
Add: configuration for repeated.
⦙[simLink](simLink.md)
.
Add: configuration for repeated. ⦙
[simLink](simLink.md)

---- RANGES ------------------------------------------------------------
⟦Add: configuration for repeated.⟧ ⟦
⟧⟦[simLink](simLink.md)⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[0, 55), s=0:0, u=0:0, t=2:2, l=56, sz=6, na=5: [0, 32), ' ', [32, 33), [34, 55), '\n', [55) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 24) options(wrap, margin[66], type[t], with-ranges, style-keep-at-start-explicit-link-all)
Add: configuration for repeated.
⦙[simLink](simLink.md)
.
Add: configuration for repeated. t⦙
[simLink](simLink.md)

---- RANGES ------------------------------------------------------------
⟦Add: configuration for repeated.⟧ ⟦t⟧
⟦[simLink](simLink.md)⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[0, 55), s=0:0, u=0:0, t=3:3, l=57, sz=7, na=6: [0, 32), ' ', [33, 34), '\n', [34, 55), '\n', [55) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 25) options(wrap, margin[72], type[T], with-ranges)
* **Copy Table as ⟦JSON⦙⟧** action to convert markdown table to JSON structure
* **Adjust Links on Paste** and **Confirm on Paste** options for selective link URL adjustments
.
* **Copy Table as T⦙** action to convert markdown table to JSON structure
* **Adjust Links on Paste** and **Confirm on Paste** options for selective link URL adjustments
---- RANGES ------------------------------------------------------------
⟦⟧* ⟦**Copy Table as T** action to convert markdown table to JSON structure
⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[2, 73), s=0:0, u=0:0, t=1:2, l=73, sz=3, na=2: [2), '* ', [2, 73) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 26) options(wrap, margin[72], type[T], with-ranges)
* **Adjust Links on ⟦Paste⦙⟧** and **Confirm on Paste** options for selective link URL adjustments
.
* **Adjust Links on T⦙** and **Confirm on Paste** options for selective
  link URL adjustments

---- RANGES ------------------------------------------------------------
⟦⟧* ⟦**Adjust Links on T** and **Confirm on Paste** options for selective⟧
⟦⟧ ⟦ link URL adjustments⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[2, 91), s=0:0, u=0:0, t=4:5, l=94, sz=9, na=6: [2), '* ', [2, 70), '\n', [70), ' ', [70, 91), '\n', [91) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 27) options(wrap, margin[72], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for x⦙
.
* [ ] Add: validation to `Formatter.render` for ⦙
````````````````````````````````


```````````````````````````````` example(Typing Handler: 28) options(wrap, margin[72], repeat[2], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for xx⦙
.
* [ ] Add: validation to `Formatter.render` for ⦙
````````````````````````````````


Should preserve space at end of line

```````````````````````````````` example(Typing Handler: 29) options(wrap, margin[72], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space x⦙
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space ⦙
---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space⟧ ⟦⟧⟦⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[6, 106), s=0:0, u=0:0, t=3:8, l=107, sz=8, na=4: [6), '* [ ] ', [6, 105), ' ', [105), [106), '\n', [106) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 30) options(wrap, margin[72], repeat[2], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx⦙
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space ⦙
---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space⟧ ⟦⟧⟦⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[6, 106), s=0:0, u=0:0, t=3:8, l=107, sz=8, na=4: [6), '* [ ] ', [6, 105), ' ', [105), [106), '\n', [106) }
````````````````````````````````


doubling of leading character on child paragraph item

```````````````````````````````` example(Typing Handler: 31) options(wrap, margin[72], type[D], with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  ⦙First paragraph.
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  D⦙First paragraph.
---- RANGES ------------------------------------------------------------
⟦⟧  ⟦DFirst paragraph.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[118, 135), s=0:0, u=0:0, t=2:3, l=20, sz=5, na=3: [118), '  ', [118, 135), '\n', [135) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 32) options(wrap, margin[72], type[ ], with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  ⦙First paragraph.
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
   ⦙First paragraph.
---- RANGES ------------------------------------------------------------
⟦⟧  ⟦ First paragraph.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[118, 135), s=0:0, u=0:0, t=2:3, l=20, sz=5, na=3: [118), '  ', [118, 135), '\n', [135) }
````````````````````````````````


Unmatched after wrapping

```````````````````````````````` example(Typing Handler: 33) options(wrap, margin[70], type[ ])
Require `jclg/php-slack-bot` dev-master (the library has not been updated⦙
abandoned there is no release
.
Require `jclg/php-slack-bot` dev-master (the library has not been
updated ⦙ abandoned there is no release
````````````````````````````````


```````````````````````````````` example(Typing Handler: 34) options(wrap, margin[70], type[ ])
Require `jclg/php-slack-bot` dev-master (the library has not been updated⦙
 abandoned there is no release
.
Require `jclg/php-slack-bot` dev-master (the library has not been
updated ⦙ abandoned there is no release
````````````````````````````````


Allow spaces in ref link

```````````````````````````````` example(Typing Handler: 35) options(wrap, type[ ])
[B⦙]
.
[B ⦙]
````````````````````````````````


```````````````````````````````` example(Typing Handler: 36) options(wrap, margin[72], type[ ], with-ranges)
* Add: validation to `Formatter.render` for with overflow to the next line causes loss of space xx
    
  :information_source: nested link ref derived elements in link ref text have priority and cannot be used to embed into link ref text using `[link ref text][ref id]` syntax.⦙

.
* Add: validation to `Formatter.render` for with overflow to the next line causes loss of space xx
    
  :information_source: nested link ref derived elements in link ref text
  have priority and cannot be used to embed into link ref text using
  `[link ref text][ref id]` syntax. ⦙


---- RANGES ------------------------------------------------------------
⟦⟧  ⟦:information_source: nested link ref derived elements in link ref text⟧
⟦⟧ ⟦ have priority and cannot be used to embed into link ref text using⟧
⟦⟧ ⟦ `[link ref text][ref id]` syntax.⟧ ⟦⟧⟦
⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[106, 279), s=0:0, u=0:0, t=6:7, l=179, sz=14, na=10: [106), '  ', [106, 176), '\n', [176), ' ', [176, 243), '\n', [243), ' ', [243, 277), ' ', [277), [278, 279) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 37) options(wrap, margin[72], type[ ], with-ranges)
* Add: validation to `Formatter.render` for with overflow to the next line causes loss of space xx     ⦙
    
.
* Add: validation to `Formatter.render` for with overflow to the next
  line causes loss of space xx      ⦙
    

---- RANGES ------------------------------------------------------------
⟦⟧* ⟦Add: validation to `Formatter.render` for with overflow to the next⟧
⟦⟧ ⟦ line causes loss of space xx⟧      ⟦⟧⟦
⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[2, 105), s=0:0, u=0:0, t=4:10, l=107, sz=10, na=7: [2), '* ', [2, 69), '\n', [69), ' ', [69, 98), '      ', [98), [104, 105) }
````````````````````````````````


```````````````````````````````` example(Typing Handler: 38) options(wrap, margin[72], type[ ], with-ranges)
* Add: validation to `Formatter.render` for with overflow to the next line causes loss of space xx     
⦙    
.
* Add: validation to `Formatter.render` for with overflow to the next line causes loss of space xx     
 ⦙    
````````````````````````````````


```````````````````````````````` example(Typing Handler: 39) options(wrap, margin[72], type[ ])
* Fix:
  reverted file type test code for editor provider to file language type
  test which works for scratch files created via `New File` action in `Scratches` directory.⦙
* Fix: make all plugin topics to not broadcast to parent/child message
  buses.
.
* Fix: reverted file type test code for editor provider to file language
  type test which works for scratch files created via `New File` action
  in `Scratches` directory. ⦙
* Fix: make all plugin topics to not broadcast to parent/child message
  buses.
````````````````````````````````


## Block Quotes

### Add

```````````````````````````````` example(Block Quotes - Add: 1) options(wrap, margin[72], type[>], with-ranges)
⦙Add: validation to `Formatter.render` for with overflow to the
next line causes loss of space xx
.
>⦙Add: validation to `Formatter.render` for with overflow to the next
>line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧>⟦Add: validation to `Formatter.render` for with overflow to the⟧ ⟦next⟧
⟦⟧⟦⟧>⟦line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[1, 97), s=0:0, u=0:0, t=5:5, l=99, sz=12, na=8: [1), '>', [1, 63), ' ', [64, 68), '\n', [68), [69), '>', [69, 97), '\n', [97) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Add: 2) options(wrap, margin[72], type[>], with-ranges)
* [ ] >⦙Add: validation to `Formatter.render` for with overflow to the
      >next line causes loss of space xx
.
* [ ] >>⦙Add: validation to `Formatter.render` for with overflow to the
      >>next line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] >>⟦Add: validation to `Formatter.render` for with overflow to the
      >⟧>⟦next line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[8, 111), s=0:0, u=0:0, t=3:10, l=113, sz=7, na=5: [8), '* [ ] >>', [8, 78), '>', [78, 111), '\n', [111) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Add: 3) options(wrap, margin[72], type[>], with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  ⦙First paragraph.
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >⦙First paragraph.
---- RANGES ------------------------------------------------------------
⟦⟧  >⟦First paragraph.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[119, 135), s=0:0, u=0:0, t=2:4, l=20, sz=5, na=3: [119), '  >', [119, 135), '\n', [135) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Add: 4) options(wrap, margin[72], type[>], with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >⦙First paragraph.
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >>⦙First paragraph.
---- RANGES ------------------------------------------------------------
⟦⟧  >>⟦First paragraph.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[120, 136), s=0:0, u=0:0, t=2:5, l=21, sz=5, na=3: [120), '  >>', [120, 136), '\n', [136) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Add: 5) options(wrap, margin[72], type[>], with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  ⦙Add: validation to `Formatter.render` for with overflow to the
  next line causes loss of space xx
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >⦙Add: validation to `Formatter.render` for with overflow to the next
  >line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧  >⟦Add: validation to `Formatter.render` for with overflow to the⟧⟦ next⟧
⟦⟧⟦⟧  >⟦line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[119, 217), s=0:0, u=0:0, t=4:8, l=103, sz=11, na=7: [119), '  >', [119, 181), [183, 188), '\n', [188), [189), '  >', [189, 217), '\n', [217) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Add: 6) options(wrap, margin[72], type[>], with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >⦙Add: validation to `Formatter.render` for with overflow to the next
  >line causes loss of space xx
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >>⦙Add: validation to `Formatter.render` for with overflow to the next
  >>line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧  >>⟦Add: validation to `Formatter.render` for with overflow to the next
  >⟧>⟦line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[120, 219), s=0:0, u=0:0, t=3:6, l=105, sz=7, na=5: [120), '  >>', [120, 191), '>', [191, 219), '\n', [219) }
````````````````````````````````


### Remove

```````````````````````````````` example(Block Quotes - Remove: 1) options(wrap, margin[72], backspace, with-ranges)
>⦙Add: validation to `Formatter.render` for with overflow to the next
>line causes loss of space xx
.
⦙Add: validation to `Formatter.render` for with overflow to the next line
causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦Add: validation to `Formatter.render` for with overflow to the next⟧ ⟦line⟧
⟦⟧⟦causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[0, 97), s=0:0, u=0:0, t=3:3, l=97, sz=8, na=6: [0, 67), ' ', [69, 73), '\n', [73), [74, 97), '\n', [97) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 2) options(wrap, margin[72], backspace, with-ranges)
* [ ] >>⦙Add: validation to `Formatter.render` for with overflow to the
      >>next line causes loss of space xx
.
* [ ] > ⦙Add: validation to `Formatter.render` for with overflow to the
      > next line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] > ⟦Add: validation to `Formatter.render` for with overflow to the
      >⟧ ⟦next line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[7, 111), s=0:0, u=0:0, t=3:10, l=113, sz=7, na=5: [7), '* [ ] > ', [7, 77), ' ', [78, 111), '\n', [111) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 3) options(wrap, margin[72], backspace, with-ranges)
* [ ] > ⦙Add: validation to `Formatter.render` for with overflow to the
      > next line causes loss of space xx
.
* [ ] ⦙Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Add: validation to `Formatter.render` for with overflow to the
      ⟧⟦next line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[7, 111), s=0:0, u=0:0, t=2:7, l=109, sz=6, na=4: [7), '* [ ] ', [7, 76), [78, 111), '\n', [111) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 4) options(wrap, margin[72], backspace, with-ranges)
* [ ] >⦙ Add: validation to `Formatter.render` for with overflow to the
      > next line causes loss of space xx
.
* [ ] ⦙Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Add: validation to `Formatter.render` for with overflow to the
      ⟧⟦next line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[7, 111), s=0:0, u=0:0, t=2:7, l=109, sz=6, na=4: [7), '* [ ] ', [7, 76), [78, 111), '\n', [111) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 5) options(wrap, margin[72], backspace, with-ranges)
* [ ] >⦙Add: validation to `Formatter.render` for with overflow to the
      >next line causes loss of space xx
.
* [ ] ⦙Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧* [ ] ⟦Add: validation to `Formatter.render` for with overflow to the
      ⟧⟦next line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[6, 109), s=0:0, u=0:0, t=2:7, l=109, sz=6, na=4: [6), '* [ ] ', [6, 75), [76, 109), '\n', [109) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 6) options(wrap, margin[72], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >⦙First paragraph.
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  ⦙First paragraph.
---- RANGES ------------------------------------------------------------
⟦⟧  ⟦First paragraph.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[118, 134), s=0:0, u=0:0, t=2:3, l=19, sz=5, na=3: [118), '  ', [118, 134), '\n', [134) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 7) options(wrap, margin[72], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >>⦙First paragraph.
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  > ⦙First paragraph.

---- RANGES ------------------------------------------------------------
⟦⟧  > ⟦First paragraph.⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[119, 135), s=0:0, u=0:0, t=2:5, l=21, sz=5, na=3: [119), '  > ', [119, 135), '\n', [135) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 8) options(wrap, margin[72], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >⦙Add: validation to `Formatter.render` for with overflow to the next
  >line causes loss of space xx
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  ⦙Add: validation to `Formatter.render` for with overflow to the next
  line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧  ⟦Add: validation to `Formatter.render` for with overflow to the next
  ⟧⟦line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[118, 217), s=0:0, u=0:0, t=2:3, l=101, sz=6, na=4: [118), '  ', [118, 188), [189, 217), '\n', [217) }
````````````````````````````````


```````````````````````````````` example(Block Quotes - Remove: 9) options(wrap, margin[72], backspace, with-ranges)
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  >>⦙Add: validation to `Formatter.render` for with overflow to the next
  >>line causes loss of space xx
.
* [ ] Add: validation to `Formatter.render` for with overflow to the
      next line causes loss of space xx
      
  > ⦙Add: validation to `Formatter.render` for with overflow to the next
  > line causes loss of space xx

---- RANGES ------------------------------------------------------------
⟦⟧  > ⟦Add: validation to `Formatter.render` for with overflow to the next
  >⟧ ⟦line causes loss of space xx⟧
⟦⟧
---- SEGMENTS ----------------------------------------------------------
BasedSegmentBuilder{[119, 219), s=0:0, u=0:0, t=3:6, l=105, sz=7, na=5: [119), '  > ', [119, 190), ' ', [191, 219), '\n', [219) }
````````````````````````````````


## Smart Edit

### Asterisks

```````````````````````````````` example(Smart Edit - Asterisks: 1) options(type[*], smart-edit-asterisks)
test ⦙
.
test *⦙*
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 2) options(backspace, smart-edit-asterisks)
test *⦙*
.
test ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 3) options(type[ ], smart-edit-asterisks)
test *⦙*
.
test * ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 4) options(margin[50], wrap, type[ ], smart-edit-asterisks)
test *⦙* long line needing wrapping on space and should be wrapped.
.
test * ⦙ long line needing wrapping on space and
should be wrapped.
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 5) options(margin[50], wrap, type[ ], smart-edit-asterisks, wrap-on-space)
test *⦙* long line needing wrapping on space and should be wrapped.
.
test * ⦙ long line needing wrapping on space and
should be wrapped.
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 6) options(margin[50], wrap, type[a], smart-edit-asterisks, wrap-on-space)
test *⦙* long line needing wrapping on space and should be wrapped.
.
test *a⦙* long line needing wrapping on space and should be wrapped.
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 7) options(type[*], smart-edit-asterisks)
test *⦙*
.
test **⦙**
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 8) options(backspace, smart-edit-asterisks)
test **⦙**
.
test *⦙*
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 9) options(type[ ], smart-edit-asterisks)
test **⦙**
.
test ** ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 10) options(type[*], smart-edit-asterisks)
test **⦙**
.
test ***⦙***
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 11) options(backspace, smart-edit-asterisks)
test ***⦙***
.
test **⦙**
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 12) options(type[ ], smart-edit-asterisks)
test ***⦙***
.
test *** ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 13) options(type[*], smart-edit-asterisks)
test ***⦙***
.
test ****⦙***
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 14) options(backspace, smart-edit-asterisks)
test ****⦙***
.
test ***⦙***
````````````````````````````````


```````````````````````````````` example(Smart Edit - Asterisks: 15) options(type[ ], smart-edit-asterisks)
test ****⦙***
.
test **** ⦙***
````````````````````````````````


### Underscores

```````````````````````````````` example(Smart Edit - Underscores: 1) options(type[_], smart-edit-underscore)
test ⦙
.
test _⦙_
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 2) options(backspace, smart-edit-underscore)
test _⦙_
.
test ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 3) options(type[ ], smart-edit-underscore)
test _⦙_
.
test _ ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 4) options(margin[50], wrap, type[ ], smart-edit-underscore)
test _⦙_ long line needing wrapping on space and should be wrapped.
.
test _ ⦙ long line needing wrapping on space and
should be wrapped.
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 5) options(type[_], smart-edit-underscore)
test _⦙_
.
test __⦙__
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 6) options(backspace, smart-edit-underscore)
test __⦙__
.
test _⦙_
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 7) options(type[ ], smart-edit-underscore)
test __⦙__
.
test __ ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 8) options(type[_], smart-edit-underscore)
test __⦙__
.
test ___⦙___
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 9) options(backspace, smart-edit-underscore)
test ___⦙___
.
test __⦙__
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 10) options(type[ ], smart-edit-underscore)
test ___⦙___
.
test ___ ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 11) options(type[_], smart-edit-underscore)
test ___⦙___
.
test ____⦙___
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 12) options(backspace, smart-edit-underscore)
test ____⦙___
.
test ___⦙___
````````````````````````````````


```````````````````````````````` example(Smart Edit - Underscores: 13) options(type[ ], smart-edit-underscore)
test ____⦙___
.
test ____ ⦙___
````````````````````````````````


### Tildes

```````````````````````````````` example(Smart Edit - Tildes: 1) options(type[~], smart-edit-tildes)
test ⦙
.
test ~⦙~
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 2) options(backspace, smart-edit-tildes)
test ~⦙~
.
test ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 3) options(type[ ], smart-edit-tildes)
test ~⦙~
.
test ~ ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 4) options(margin[50], wrap, type[ ], smart-edit-tildes)
test ~⦙~ long line needing wrapping on space and should be wrapped.
.
test ~ ⦙ long line needing wrapping on space and
should be wrapped.
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 5) options(type[~], smart-edit-tildes)
test ~⦙~
.
test ~~⦙~~
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 6) options(backspace, smart-edit-tildes)
test ~~⦙~~
.
test ~⦙~
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 7) options(type[ ], smart-edit-tildes)
test ~~⦙~~
.
test ~~ ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 8) options(type[~], smart-edit-tildes)
test ~~⦙~~
.
test ~~~⦙~~
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 9) options(backspace, smart-edit-tildes)
test ~~~⦙~~
.
test ~~⦙~~
````````````````````````````````


```````````````````````````````` example(Smart Edit - Tildes: 10) options(type[ ], smart-edit-tildes)
test ~~~⦙~~
.
test ~~~ ⦙~~
````````````````````````````````


### Back Tics

```````````````````````````````` example(Smart Edit - Back Tics: 1) options(type[`], smart-edit-back-tics)
test ⦙
.
test `⦙`
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 2) options(backspace, smart-edit-back-tics)
test `⦙`
.
test ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 3) options(type[ ], smart-edit-back-tics)
test `⦙`
.
test ` ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 4) options(margin[50], wrap, type[ ], smart-edit-back-tics)
test `⦙` long line needing wrapping on space and should be wrapped.
.
test ` ⦙ long line needing wrapping on space and
should be wrapped.
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 5) options(type[`], smart-edit-back-tics)
test `⦙`
.
test ``⦙``
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 6) options(backspace, smart-edit-back-tics)
test ``⦙``
.
test `⦙`
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 7) options(type[ ], smart-edit-back-tics)
test ``⦙``
.
test `` ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 8) options(type[`], smart-edit-back-tics)
test ``⦙``
.
test ```⦙```
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 9) options(backspace, smart-edit-back-tics)
test ```⦙```
.
test ``⦙``
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 10) options(type[ ], smart-edit-back-tics)
test ```⦙```
.
test ``` ⦙
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 11) options(type[`], smart-edit-back-tics)
test ```⦙```
.
test ````⦙````
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 12) options(backspace, smart-edit-back-tics)
test ````⦙````
.
test ```⦙```
````````````````````````````````


```````````````````````````````` example(Smart Edit - Back Tics: 13) options(type[ ], smart-edit-back-tics)
test ````⦙````
.
test ```` ⦙````
````````````````````````````````


