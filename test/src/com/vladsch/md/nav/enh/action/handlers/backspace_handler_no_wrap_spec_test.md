---
title: Backspace Disabled Handler Spec Test
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

# Backspace Disabled

:information_source: default options `backspace-as-wrap-on-typing, no-wrap, backspace` defined
in
[BackspaceHandlerNoWrapSpecTest.java: Lines 43-47](BackspaceHandlerNoWrapSpecTest.java#L43-L47)

## Unordered

### Simple

```````````````````````````````` example Unordered - Simple: 1
*⦙ text
.
⦙ text
````````````````````````````````


```````````````````````````````` example Unordered - Simple: 2
* ⦙text
.
*⦙text
````````````````````````````````


```````````````````````````````` example Unordered - Simple: 3
* t⦙ext
.
* ⦙ext
````````````````````````````````


```````````````````````````````` example Unordered - Simple: 4
* text⦙
.
* tex⦙
````````````````````````````````


### After

```````````````````````````````` example Unordered - After: 1
* item
*⦙ text
.
* item
⦙ text
````````````````````````````````


```````````````````````````````` example Unordered - After: 2
* item
* ⦙text
.
* item
*⦙text
````````````````````````````````


```````````````````````````````` example Unordered - After: 3
* item
* t⦙ext
.
* item
* ⦙ext
````````````````````````````````


### Before

```````````````````````````````` example Unordered - Before: 1
*⦙ text
* item
.
⦙ text
* item
````````````````````````````````


```````````````````````````````` example Unordered - Before: 2
* ⦙text
* item
.
*⦙text
* item
````````````````````````````````


```````````````````````````````` example Unordered - Before: 3
* t⦙ext
* item
.
* ⦙ext
* item
````````````````````````````````


```````````````````````````````` example Unordered - Before: 4
* text⦙
* item
.
* tex⦙
* item
````````````````````````````````


### Middle

```````````````````````````````` example Unordered - Middle: 1
* item 1
*⦙ text
* item 2
.
* item 1
⦙ text
* item 2
````````````````````````````````


```````````````````````````````` example Unordered - Middle: 2
* item 1
* ⦙text
* item 2
.
* item 1
*⦙text
* item 2
````````````````````````````````


```````````````````````````````` example Unordered - Middle: 3
* item 1
* t⦙ext
* item 2
.
* item 1
* ⦙ext
* item 2
````````````````````````````````


```````````````````````````````` example Unordered - Middle: 4
* item 1
* text⦙
* item 2
.
* item 1
* tex⦙
* item 2
````````````````````````````````


### Indented

* [ ] Add indented versions

### Nested

#### Simple

```````````````````````````````` example Unordered - Nested - Simple: 1
* item 1
  *⦙ text
.
* item 1
  ⦙ text
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Simple: 2
* item 1
  * ⦙text
.
* item 1
  *⦙text
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Simple: 3
* item 1
  * t⦙ext
.
* item 1
  * ⦙ext
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Simple: 4
* item 1
  * text⦙
.
* item 1
  * tex⦙
````````````````````````````````


#### After

```````````````````````````````` example Unordered - Nested - After: 1
* item 1
  * sub-item 1
  *⦙ text
.
* item 1
  * sub-item 1
  ⦙ text
````````````````````````````````


```````````````````````````````` example Unordered - Nested - After: 2
* item 1
  * sub-item 1
  * ⦙text
.
* item 1
  * sub-item 1
  *⦙text
````````````````````````````````


```````````````````````````````` example Unordered - Nested - After: 3
* item 1
  * sub-item 1
  * t⦙ext
.
* item 1
  * sub-item 1
  * ⦙ext
````````````````````````````````


```````````````````````````````` example Unordered - Nested - After: 4
* item 1
  * sub-item 1
  * text⦙
.
* item 1
  * sub-item 1
  * tex⦙
````````````````````````````````


#### Before

```````````````````````````````` example Unordered - Nested - Before: 1
* item 1
  *⦙ text
  * sub-item 2
.
* item 1
  ⦙ text
  * sub-item 2
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Before: 2
* item 1
  * ⦙text
  * sub-item 2
.
* item 1
  *⦙text
  * sub-item 2
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Before: 3
* item 1
  * t⦙ext
  * sub-item 2
.
* item 1
  * ⦙ext
  * sub-item 2
````````````````````````````````


#### Middle

```````````````````````````````` example Unordered - Nested - Middle: 1
* item 1
  * sub-item 1
  *⦙ text
  * sub-item 2
.
* item 1
  * sub-item 1
  ⦙ text
  * sub-item 2
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Middle: 2
* item 1
  * sub-item 1
  * ⦙text
  * sub-item 2
.
* item 1
  * sub-item 1
  *⦙text
  * sub-item 2
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Middle: 3
* item 1
  * sub-item 1
  * t⦙ext
  * sub-item 2
.
* item 1
  * sub-item 1
  * ⦙ext
  * sub-item 2
````````````````````````````````


```````````````````````````````` example Unordered - Nested - Middle: 4
* item 1
  * sub-item 1
  * text⦙
  * sub-item 2
.
* item 1
  * sub-item 1
  * tex⦙
  * sub-item 2
````````````````````````````````


## Ordered

### Simple

```````````````````````````````` example Ordered - Simple: 1
1⦙. text
.
⦙. text
````````````````````````````````


```````````````````````````````` example Ordered - Simple: 2
1.⦙ text
.
1⦙ text
````````````````````````````````


```````````````````````````````` example Ordered - Simple: 3
1. ⦙text
.
1.⦙text
````````````````````````````````


```````````````````````````````` example Ordered - Simple: 4
1. t⦙ext
.
1. ⦙ext
````````````````````````````````


```````````````````````````````` example Ordered - Simple: 5
1. text⦙
.
1. tex⦙
````````````````````````````````


### After

```````````````````````````````` example Ordered - After: 1
1. item
1⦙. text
.
1. item
⦙. text
````````````````````````````````


```````````````````````````````` example Ordered - After: 2
1. item
1.⦙ text
.
1. item
1⦙ text
````````````````````````````````


```````````````````````````````` example Ordered - After: 3
1. item
1. ⦙text
.
1. item
1.⦙text
````````````````````````````````


```````````````````````````````` example Ordered - After: 4
1. item
1. t⦙ext
.
1. item
1. ⦙ext
````````````````````````````````


### Before

```````````````````````````````` example Ordered - Before: 1
1⦙. text
1. item
.
⦙. text
1. item
````````````````````````````````


```````````````````````````````` example Ordered - Before: 2
1.⦙ text
1. item
.
1⦙ text
1. item
````````````````````````````````


```````````````````````````````` example Ordered - Before: 3
1. ⦙text
1. item
.
1.⦙text
1. item
````````````````````````````````


```````````````````````````````` example Ordered - Before: 4
1. t⦙ext
1. item
.
1. ⦙ext
1. item
````````````````````````````````


```````````````````````````````` example Ordered - Before: 5
1. text⦙
1. item
.
1. tex⦙
1. item
````````````````````````````````


### Middle

```````````````````````````````` example Ordered - Middle: 1
1. item 1
1⦙. text
1. item 2
.
1. item 1
⦙. text
1. item 2
````````````````````````````````


```````````````````````````````` example Ordered - Middle: 2
1. item 1
1.⦙ text
1. item 2
.
1. item 1
1⦙ text
1. item 2
````````````````````````````````


```````````````````````````````` example Ordered - Middle: 3
1. item 1
1. ⦙text
1. item 2
.
1. item 1
1.⦙text
1. item 2
````````````````````````````````


```````````````````````````````` example Ordered - Middle: 4
1. item 1
1. t⦙ext
1. item 2
.
1. item 1
1. ⦙ext
1. item 2
````````````````````````````````


```````````````````````````````` example Ordered - Middle: 5
1. item 1
1. text⦙
1. item 2
.
1. item 1
1. tex⦙
1. item 2
````````````````````````````````


### Indented

#### Simple

```````````````````````````````` example Ordered - Indented - Simple: 1
1. item 1
  1⦙. text
.
1. item 1
  ⦙. text
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Simple: 2
1. item 1
  1.⦙ text
.
1. item 1
  1⦙ text
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Simple: 3
1. item 1
  1. ⦙text
.
1. item 1
  1.⦙text
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Simple: 4
1. item 1
  1. t⦙ext
.
1. item 1
  1. ⦙ext
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Simple: 5
1. item 1
  1. text⦙
.
1. item 1
  1. tex⦙
````````````````````````````````


#### After

```````````````````````````````` example Ordered - Indented - After: 1
1. item 1
  1. sub-item 1
  1⦙. text
.
1. item 1
  1. sub-item 1
  ⦙. text
````````````````````````````````


```````````````````````````````` example Ordered - Indented - After: 2
1. item 1
  1. sub-item 1
  1.⦙ text
.
1. item 1
  1. sub-item 1
  1⦙ text
````````````````````````````````


```````````````````````````````` example Ordered - Indented - After: 3
1. item 1
  1. sub-item 1
  1. ⦙text
.
1. item 1
  1. sub-item 1
  1.⦙text
````````````````````````````````


```````````````````````````````` example Ordered - Indented - After: 4
1. item 1
  1. sub-item 1
  1. t⦙ext
.
1. item 1
  1. sub-item 1
  1. ⦙ext
````````````````````````````````


```````````````````````````````` example Ordered - Indented - After: 5
1. item 1
  1. sub-item 1
  1. text⦙
.
1. item 1
  1. sub-item 1
  1. tex⦙
````````````````````````````````


#### Before

```````````````````````````````` example Ordered - Indented - Before: 1
1. item 1
  1⦙. text
  1. sub-item 2
.
1. item 1
  ⦙. text
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Before: 2
1. item 1
  1.⦙ text
  1. sub-item 2
.
1. item 1
  1⦙ text
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Before: 3
1. item 1
  1. ⦙text
  1. sub-item 2
.
1. item 1
  1.⦙text
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Before: 4
1. item 1
  1. t⦙ext
  1. sub-item 2
.
1. item 1
  1. ⦙ext
  1. sub-item 2
````````````````````````````````


#### Middle

```````````````````````````````` example Ordered - Indented - Middle: 1
1. item 1
  1. sub-item 1
  1⦙. text
  1. sub-item 2
.
1. item 1
  1. sub-item 1
  ⦙. text
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Middle: 2
1. item 1
  1. sub-item 1
  1.⦙ text
  1. sub-item 2
.
1. item 1
  1. sub-item 1
  1⦙ text
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Middle: 3
1. item 1
  1. sub-item 1
  1. ⦙text
  1. sub-item 2
.
1. item 1
  1. sub-item 1
  1.⦙text
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Middle: 4
1. item 1
  1. sub-item 1
  1. t⦙ext
  1. sub-item 2
.
1. item 1
  1. sub-item 1
  1. ⦙ext
  1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Indented - Middle: 5
1. item 1
  1. sub-item 1
  1. text⦙
  1. sub-item 2
.
1. item 1
  1. sub-item 1
  1. tex⦙
  1. sub-item 2
````````````````````````````````


### Nested

#### Simple

```````````````````````````````` example Ordered - Nested - Simple: 1
1. item 1
   1⦙. text
.
1. item 1
   ⦙. text
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Simple: 2
1. item 1
   1.⦙ text
.
1. item 1
   1⦙ text
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Simple: 3
1. item 1
   1. ⦙text
.
1. item 1
   1.⦙text
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Simple: 4
1. item 1
   1. t⦙ext
.
1. item 1
   1. ⦙ext
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Simple: 5
1. item 1
   1. text⦙
.
1. item 1
   1. tex⦙
````````````````````````````````


#### After

```````````````````````````````` example Ordered - Nested - After: 1
1. item 1
   1. sub-item 1
   1⦙. text
.
1. item 1
   1. sub-item 1
   ⦙. text
````````````````````````````````


```````````````````````````````` example Ordered - Nested - After: 2
1. item 1
   1. sub-item 1
   1.⦙ text
.
1. item 1
   1. sub-item 1
   1⦙ text
````````````````````````````````


```````````````````````````````` example Ordered - Nested - After: 3
1. item 1
   1. sub-item 1
   1. ⦙text
.
1. item 1
   1. sub-item 1
   1.⦙text
````````````````````````````````


```````````````````````````````` example Ordered - Nested - After: 4
1. item 1
   1. sub-item 1
   1. t⦙ext
.
1. item 1
   1. sub-item 1
   1. ⦙ext
````````````````````````````````


```````````````````````````````` example Ordered - Nested - After: 5
1. item 1
   1. sub-item 1
   1. text⦙
.
1. item 1
   1. sub-item 1
   1. tex⦙
````````````````````````````````


#### Before

```````````````````````````````` example Ordered - Nested - Before: 1
1. item 1
   1⦙. text
   1. sub-item 2
.
1. item 1
   ⦙. text
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Before: 2
1. item 1
   1.⦙ text
   1. sub-item 2
.
1. item 1
   1⦙ text
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Before: 3
1. item 1
   1. ⦙text
   1. sub-item 2
.
1. item 1
   1.⦙text
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Before: 4
1. item 1
   1. t⦙ext
   1. sub-item 2
.
1. item 1
   1. ⦙ext
   1. sub-item 2
````````````````````````````````


#### Middle

```````````````````````````````` example Ordered - Nested - Middle: 1
1. item 1
   1. sub-item 1
   1⦙. text
   1. sub-item 2
.
1. item 1
   1. sub-item 1
   ⦙. text
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Middle: 2
1. item 1
   1. sub-item 1
   1.⦙ text
   1. sub-item 2
.
1. item 1
   1. sub-item 1
   1⦙ text
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Middle: 3
1. item 1
   1. sub-item 1
   1. ⦙text
   1. sub-item 2
.
1. item 1
   1. sub-item 1
   1.⦙text
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Middle: 4
1. item 1
   1. sub-item 1
   1. t⦙ext
   1. sub-item 2
.
1. item 1
   1. sub-item 1
   1. ⦙ext
   1. sub-item 2
````````````````````````````````


```````````````````````````````` example Ordered - Nested - Middle: 5
1. item 1
   1. sub-item 1
   1. text⦙
   1. sub-item 2
.
1. item 1
   1. sub-item 1
   1. tex⦙
   1. sub-item 2
````````````````````````````````


