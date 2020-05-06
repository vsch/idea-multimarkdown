---
title: Strip Trailing Spaces Spec Test
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Text

```````````````````````````````` example Text: 1
text with trailing spaces      
with lazy continuation      
.
text with trailing spaces      
with lazy continuation      
````````````````````````````````


```````````````````````````````` example(Text: 2) options(trailing-keep-none)
text with trailing spaces      
with lazy continuation      
.
text with trailing spaces
with lazy continuation
````````````````````````````````


```````````````````````````````` example(Text: 3) options(trailing-keep-all)
text with trailing spaces      
with lazy continuation      
.
text with trailing spaces      
with lazy continuation      
````````````````````````````````


## List Item

```````````````````````````````` example List Item: 1
* text    
.
* text    
````````````````````````````````


```````````````````````````````` example(List Item: 2) options(trailing-keep-none)
* text    
.
* text
````````````````````````````````


```````````````````````````````` example(List Item: 3) options(trailing-keep-all)
* text    
.
* text    
````````````````````````````````


```````````````````````````````` example List Item: 4
* text with trailing spaces      
with lazy continuation      
.
* text with trailing spaces      
with lazy continuation      
````````````````````````````````


```````````````````````````````` example(List Item: 5) options(trailing-keep-none)
* text with trailing spaces      
with lazy continuation      
.
* text with trailing spaces
with lazy continuation
````````````````````````````````


```````````````````````````````` example(List Item: 6) options(trailing-keep-all)
* text with trailing spaces      
with lazy continuation      
.
* text with trailing spaces      
with lazy continuation      
````````````````````````````````


## List Item Next line

```````````````````````````````` example List Item Next line: 1
*       
with lazy continuation      
.
*       
with lazy continuation      
````````````````````````````````


```````````````````````````````` example(List Item Next line: 2) options(trailing-keep-none)
*       
with lazy continuation      
.
*
with lazy continuation
````````````````````````````````


```````````````````````````````` example(List Item Next line: 3) options(trailing-keep-all)
*       
with lazy continuation      
.
*       
with lazy continuation      
````````````````````````````````


## Empty List Item

```````````````````````````````` example Empty List Item: 1
*    
.
*    
````````````````````````````````


```````````````````````````````` example(Empty List Item: 2) options(trailing-keep-none)
*    
.
*
````````````````````````````````


```````````````````````````````` example(Empty List Item: 3) options(trailing-keep-all)
*    
.
*    
````````````````````````````````


```````````````````````````````` example Empty List Item: 4
*    
    
.
*    
    
````````````````````````````````


```````````````````````````````` example(Empty List Item: 5) options(trailing-keep-none)
*    
    
.
*
    
````````````````````````````````


```````````````````````````````` example(Empty List Item: 6) options(trailing-keep-all)
*    
    
.
*    
    
````````````````````````````````


## Empty List Item Next line

```````````````````````````````` example Empty List Item Next line: 1
*       
with lazy continuation      
.
*       
with lazy continuation      
````````````````````````````````


```````````````````````````````` example(Empty List Item Next line: 2) options(trailing-keep-none)
*       
with lazy continuation      
.
*
with lazy continuation
````````````````````````````````


```````````````````````````````` example(Empty List Item Next line: 3) options(trailing-keep-all)
*       
with lazy continuation      
.
*       
with lazy continuation      
````````````````````````````````


## Indented Code

```````````````````````````````` example Indented Code: 1
    come code here      
.
    come code here      
````````````````````````````````


```````````````````````````````` example(Indented Code: 2) options(code-keep-none)
    come code here      
.
    come code here
````````````````````````````````


```````````````````````````````` example(Indented Code: 3) options(code-keep-all)
    come code here      
.
    come code here      
````````````````````````````````


Check that next line after it is not included

```````````````````````````````` example Indented Code: 4
    come code here      
* list item     
.
    come code here      
* list item     
````````````````````````````````


```````````````````````````````` example(Indented Code: 5) options(code-keep-none)
    come code here      
* list item     
.
    come code here
* list item     
````````````````````````````````


```````````````````````````````` example(Indented Code: 6) options(code-keep-all)
    come code here      
* list item     
.
    come code here      
* list item     
````````````````````````````````


```````````````````````````````` example Indented Code: 7
    come code here      
     
.
    come code here      
     
````````````````````````````````


```````````````````````````````` example(Indented Code: 8) options(code-keep-none)
    come code here      
     
.
    come code here

````````````````````````````````


```````````````````````````````` example(Indented Code: 9) options(code-keep-all)
    come code here      
     
.
    come code here      
     
````````````````````````````````


## Fenced Code

```````````````````````````````` example Fenced Code: 1
```     
come code here      
```     
.
```
come code here      
```
````````````````````````````````


```````````````````````````````` example(Fenced Code: 2) options(code-keep-none)
```     
come code here      
```     
.
```
come code here
```
````````````````````````````````


```````````````````````````````` example(Fenced Code: 3) options(code-keep-all)
```     
come code here      
```     
.
```
come code here      
```
````````````````````````````````


