---
title: List Item Action Spec Test
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

# List Item Actions

* [ ] Add fixed spacing option

## Item Toggle

```````````````````````````````` example(Item Toggle: 1) options(bullet-action)
⦙text
.
* ⦙text
````````````````````````````````


```````````````````````````````` example(Item Toggle: 2) options(bullet-action)
1. ⦙text
.
* ⦙text
````````````````````````````````


```````````````````````````````` example(Item Toggle: 3) options(number-action)
* [ ]⦙
.
1. [ ]⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 4) options(number-action)
* [ ] ⦙
.
1. [ ] ⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 5) options(number-action)
* ⦙
.
1. ⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 6) options(number-action)
* text⦙
.
1. text⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 7) options(number-action)
* [ ] text⦙
.
1. [ ] text⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 8) options(number-action)
text⦙
.
1. text⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 9) options(task-action)
* [ ] ⦙
.
* ⦙
````````````````````````````````


mixed adds prefix not removes it

```````````````````````````````` example(Item Toggle: 10) options(task-action)
⟦* [ ] 
  * 
  * [x] 
⟧    
.
⦙⟦* 
  * 
  * 
⟧    
````````````````````````````````


```````````````````````````````` example(Item Toggle: 11) options(task-action)
* [ ] test⦙
.
* test⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 12) options(task-action)
* test⦙
.
* [ ] test⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 13) options(task-action)
test⦙
.
* [ ] test⦙
````````````````````````````````


```````````````````````````````` example(Item Toggle: 14) options(task-action)
test⦙
.
* [ ] test⦙
````````````````````````````````


## Toggle Done

```````````````````````````````` example(Toggle Done: 1) options(toggle-task-done)
* [ ] test⦙
.
* [x] test⦙
````````````````````````````````


```````````````````````````````` example(Toggle Done: 2) options(toggle-task-done)
* [x] test⦙
.
* [ ] test⦙
````````````````````````````````


```````````````````````````````` example(Toggle Done: 3) options(toggle-task-done)
* [X] test⦙
.
* [ ] test⦙
````````````````````````````````


## Typing

```````````````````````````````` example(Typing: 1) options(type[ ])
⦙* text
.
 ⦙* text
````````````````````````````````


```````````````````````````````` example(Typing: 2) options(type[ ])
*⦙ text
.
* ⦙ text
````````````````````````````````


```````````````````````````````` example(Typing: 3) options(type[ ])
* ⦙text
.
*  ⦙text
````````````````````````````````


```````````````````````````````` example(Typing: 4) options(type[abc])
* ⦙text
.
* abc⦙text
````````````````````````````````


