---
title: Flexmark Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

If intention action is empty will simply compare available intentions to expected. If not empty
will invoke that intention action and compare expected to editor text.

## Flexmark

### Caret Markup

#### Add Position

```````````````````````````````` example(Flexmark - Caret Markup - Add Position: 1) options(caret-markup, flexmark-features, intention[Add caret position markup])
Markdown only∣
.
Markdown only⦙∣
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Position: 2) options(caret-markup, flexmark-features, intention[Add caret position markup])
* Markdown only∣
.
* Markdown only⦙∣
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Position: 3) options(flexmark-spec, caret-markup, intention[Add caret position markup])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only∣
````````````````
.
Markdown only⦙∣
.
````````````````````````````````


#### Add Selection

```````````````````````````````` example(Flexmark - Caret Markup - Add Selection: 1) options(caret-markup, flexmark-features, intention[Add caret selection markup])
Markdown only∣
.
Markdown only∣⟦⟧
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Selection: 2) options(caret-markup, flexmark-features, intention[Add caret selection markup])
* Markdown only∣
.
* Markdown only∣⟦⟧
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Selection: 3) options(caret-markup, flexmark-features, intention[Add caret selection markup])
Markdown ⦗only∣⦘
.
Markdown ⟦⦗only∣⦘⟧
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Selection: 4) options(caret-markup, flexmark-features, intention[Add caret selection markup])
* Markdown ⦗only∣⦘
.
* Markdown ⟦⦗only∣⦘⟧
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Selection: 5) options(flexmark-spec, caret-markup, intention[Add caret selection markup])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only∣
````````````````
.
Markdown only∣⟦⟧
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Add Selection: 6) options(flexmark-spec, caret-markup, intention[Add caret selection markup])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown ⦗only∣⦘
````````````````
.
Markdown ⟦⦗only∣⦘⟧
````````````````````````````````


#### Remove

```````````````````````````````` example(Flexmark - Caret Markup - Remove: 1) options(caret-markup, flexmark-features, intention[Remove caret position and selection markup])
Markd⦙own ⟦only⦙⟧∣
.
Markdown only∣
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Remove: 2) options(caret-markup, flexmark-features, intention[Remove caret position and selection markup])
* Markd⦙own ⟦only⦙⟧∣
.
* Markdown only∣
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Remove: 3) options(flexmark-spec, caret-markup, intention[Remove caret position and selection markup])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markd⦙own ⟦only⦙⟧∣
````````````````
.
Markdown only∣
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Remove: 4) options(flexmark-spec, caret-markup, intention[Remove caret position and selection markup])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markd⦙own ⦗⟦only⦙⟧∣⦘
````````````````
.
Markd⦙own ⦗only∣⦘
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Remove: 5) options(flexmark-spec, caret-markup, intention[Remove caret position and selection markup])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Add some offset to it    
    
Markd⦙own ⦗⟦only⦙⟧∣⦘
````````````````
.
Add some offset to it    
    
Markd⦙own ⦗only∣⦘
````````````````````````````````


#### Test Section Break

```````````````````````````````` example(Flexmark - Caret Markup - Test Section Break: 1) options(flexmark-features, intention[Add test spec example section break])
Markdown only⦙
.
Markdown only…⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Section Break: 2) options(flexmark-features, intention[Add test spec example section break])
* Markdown only⦙
.
* Markdown only…⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Section Break: 3) options(flexmark-spec, intention[Add test spec example section break])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only⦙
````````````````
.
Markdown only
…⦙
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Section Break: 4) options(flexmark-spec, intention[Add test spec example section break])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown⟦ ⦙⟧only
````````````````
.
Markdown⟦
…
⦙⟧only
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Section Break: 5) options(flexmark-spec, intention[Add test spec example section break])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown⟦abc⦙⟧only
````````````````
.
Markdown⟦
…
⦙⟧only
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Section Break: 6) options(flexmark-spec, intention[Add test spec example section break])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only
⦙
````````````````
.
Markdown only
…⦙
````````````````````````````````


#### Test Example

```````````````````````````````` example(Flexmark - Caret Markup - Test Example: 1) options(flexmark-spec, intention[Add test spec example])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only⦙
````````````````
.
Markdown only
```````````````` example
…
````````````````⦙
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Example: 2) options(flexmark-spec, intention[Add test spec example])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown⟦ ⦙⟧only
````````````````
.
Markdown⟦
```````````````` example
…
````````````````
⦙⟧only
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Example: 3) options(flexmark-spec, intention[Add test spec example])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown⟦abc⦙⟧only
````````````````
.
Markdown⟦
```````````````` example
…
````````````````
⦙⟧only
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Test Example: 4) options(flexmark-spec, intention[Add test spec example])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only
⦙
````````````````
.
Markdown only
```````````````` example
…
````````````````⦙
````````````````````````````````


#### CR

:information_source: Spec reading upon seeing the visible CR in actual HTML converts it to HTML
entity to preserve it and not conflict with the visible CR.

```````````````````````````````` example(Flexmark - Caret Markup - CR: 1) options(flexmark-features, intention[Add visible spec CR])
Markdown only⦙
.
Markdown only&#23ce;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - CR: 2) options(flexmark-features, intention[Add visible spec CR])
* Markdown only⦙
.
* Markdown only&#23ce;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - CR: 3) options(flexmark-spec, intention[Add visible spec CR])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only⦙
````````````````
.
Markdown only&#23ce;⦙
````````````````````````````````


#### IntelliJ dummy identifier

:information_source: Spec reading upon seeing the visible dummy identifier in actual HTML
converts it to HTML entity to preserve it and not conflict with the visible dummy identifier.

```````````````````````````````` example(Flexmark - Caret Markup - IntelliJ dummy identifier: 1) options(flexmark-features, intention[Add visible spec IntelliJ dummy identifier])
Markdown only⦙
.
Markdown only&#23ae;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - IntelliJ dummy identifier: 2) options(flexmark-features, intention[Add visible spec IntelliJ dummy identifier])
* Markdown only⦙
.
* Markdown only&#23ae;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - IntelliJ dummy identifier: 3) options(flexmark-spec, intention[Add visible spec IntelliJ dummy identifier])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only⦙
````````````````
.
Markdown only&#23ae;⦙
````````````````````````````````


#### LS

:information_source: Spec reading upon seeing the visible LS in actual HTML converts it to HTML
entity to preserve it and not conflict with the visible LS.

```````````````````````````````` example(Flexmark - Caret Markup - LS: 1) options(flexmark-features, intention[Add visible spec LS])
Markdown only⦙
.
Markdown only&#27a5;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - LS: 2) options(flexmark-features, intention[Add visible spec LS])
* Markdown only⦙
.
* Markdown only&#27a5;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - LS: 3) options(flexmark-spec, intention[Add visible spec LS])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only⦙
````````````````
.
Markdown only&#27a5;⦙
````````````````````````````````


#### Tab

:information_source: Spec reading upon seeing the visible Tab in actual HTML converts it to HTML
entity to preserve it and not conflict with the visible Tab.

```````````````````````````````` example(Flexmark - Caret Markup - Tab: 1) options(flexmark-features, intention[Add visible spec Tab])
Markdown only⦙
.
Markdown only&#2192;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Tab: 2) options(flexmark-features, intention[Add visible spec Tab])
* Markdown only⦙
.
* Markdown only&#2192;⦙
.
````````````````````````````````


```````````````````````````````` example(Flexmark - Caret Markup - Tab: 3) options(flexmark-spec, intention[Add visible spec Tab])
---
title: Intentions Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

```````````````` example
Markdown only⦙
````````````````
.
Markdown only&#2192;⦙
````````````````````````````````


