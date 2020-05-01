---
title: File Element Stash
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Referenced

### Links

```````````````````````````````` example(Referenced - Links: 1) options(referenced-elements)
[ref]: example.com

[ref]
    
.
.
ref: { ref:[0, 19, type: REFERENCE] }
````````````````````````````````


```````````````````````````````` example(Referenced - Links: 2) options(referenced-elements, backspace)
[ref]: example.com

[refs]: example.com

[ref]
    
[refs⦙]
    
.
[ref]: example.com

[refs]: example.com

[ref]
    
[ref⦙]
    
.
---- Before Action -----------------------------------------------------
ref: { ref:[0, 19, type: REFERENCE] }
refs: { refs:[20, 40, type: REFERENCE] }

---- After Action ------------------------------------------------------
ref: { ref:[0, 19, type: REFERENCE] }
````````````````````````````````


## Definition Counts

### Links

```````````````````````````````` example(Definition Counts - Links: 1) options(reference-definition-counts)
[ref]: example.com

text [ref]
    
.
.
REFERENCE: { ref: 1 }
````````````````````````````````


```````````````````````````````` example(Definition Counts - Links: 2) options(reference-definition-counts, backspace)
[ref]: example.com

text [ref]
    
[refs⦙]: example.com
    
.
[ref]: example.com

text [ref]
    
[ref⦙]: example.com
    
.
---- Before Action -----------------------------------------------------
REFERENCE: { ref: 1, refs: 1 }

---- After Action ------------------------------------------------------
REFERENCE: { ref: 2 }
````````````````````````````````


