---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

If intention action is empty will simply compare available intentions to
expected. If not empty will invoke that intention action and compare
expected to editor text.

## Emoji

```````````````````````````````` example(Emoji: 1) options(invalid-emoji-shortcut)
:invalid_id⦙:
.
<error descr="Emoji ':invalid_id:' not supported by GitHub"><info descr="null">:</info><info descr="null">invalid_id</info><info descr="null">:</info></error>
````````````````````````````````


## Table

### GitHub Table

```````````````````````````````` example(Table - GitHub Table: 1) options(git-hub-table, parser-gfm-table-rendering, with-quick-fixes)
| ⦙Header 1 | Header 2 |
|----------|----------|
| Data 2   | Data 2   |
[Caption]
.
<info descr="null">|</info><info descr="null"> Header 1 </info><info descr="null">|</info><info descr="null"> Header 2 </info><info descr="null">|</info><info descr="null">
</info><info descr="null">|</info><info descr="null">----------</info><info descr="null">|</info><info descr="null">----------</info><info descr="null">|</info><info descr="null">
</info><info descr="null">|</info><info descr="null"> Data 2   </info><info descr="null">|</info><info descr="null"> Data 2   </info><info descr="null">|</info><info descr="null">
</info><weak_warning descr="GitHub tables do not support [table caption] syntax."><info descr="null">[</info><info descr="null">Caption</info><info descr="null">]</info></weak_warning>
.
intention[Delete caption]
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 2) options(git-hub-table, parser-gfm-table-rendering, intention[Delete caption])
| Header 1 | Header 2 |
|----------|----------|
| Data 2   | Data 2   |
[⦙Caption]
.
| Header 1 | Header 2 |
|----------|----------|
| Data 2   | Data 2   |
⦙
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 3) options(git-hub-table, parser-gfm-table-rendering, intention[Reformat table])
| ⦙Header 1 | Header 2 |
|----------|----------|
|          |
.
| ⦙Header 1 | Header 2 |
|:---------|:---------|
|          |          |
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 4) options(git-hub-table, parser-gfm-table-rendering, intention[Delete caption])
| Header 1 | Header 2 |
|----------|----------|
| Data 2   ||
[⦙Caption]
.
| Header 1 | Header 2 |
|----------|----------|
| Data 2   ||
⦙
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 5) options(git-hub-table, parser-gfm-table-rendering, intention[Add spaces between |])
| ⦙Header 1 | Header 2 |
|----------|----------|
| Data 2   ||
[Caption]
.
| ⦙Header 1 | Header 2 |
|----------|----------|
| Data 2   | |
[Caption]
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 6) options(git-hub-table, parser-gfm-table-rendering, intention[Delete caption])
> | Header 1 | Header 2 |
> |----------|----------|
> | Data 2   | Data 2   |
> [⦙Caption]
.
> | Header 1 | Header 2 |
> |----------|----------|
> | Data 2   | Data 2   |
⦙
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 7) options(git-hub-table, parser-gfm-table-rendering, intention[Reformat table])
> | ⦙Header 1 | Header 2 |
> |----------|----------|
> |          |
.
> | ⦙Header 1 | Header 2 |
> |:---------|:---------|
> |          |          |
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 8) options(git-hub-table, parser-gfm-table-rendering, intention[Delete caption])
> | Header 1 | Header 2 |
> |----------|----------|
> | Data 2   ||
> [⦙Caption]
.
> | Header 1 | Header 2 |
> |----------|----------|
> | Data 2   ||
⦙
````````````````````````````````


```````````````````````````````` example(Table - GitHub Table: 9) options(git-hub-table, parser-gfm-table-rendering, intention[Add spaces between |])
> | ⦙Header 1 | Header 2 |
> |----------|----------|
> | Data 2   ||
> [Caption]
.
> | ⦙Header 1 | Header 2 |
> |----------|----------|
> | Data 2   | |
> [Caption]
````````````````````````````````


