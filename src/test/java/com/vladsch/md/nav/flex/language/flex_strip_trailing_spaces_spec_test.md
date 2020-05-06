---
title: Strip Trailing Spaces Spec Test
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Spec Example

```````````````````````````````` example Spec Example: 1
```````````````` example(Section: Number) options(option)     
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````    
    
.
```````````````` example(Section: Number) options(option)
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````
    
````````````````````````````````


```````````````````````````````` example(Spec Example: 2) options(spec-keep-none)
```````````````` example(Section: Number) options(option)     
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````    
    
.
```````````````` example(Section: Number) options(option)
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````
    
````````````````````````````````


```````````````````````````````` example(Spec Example: 3) options(spec-keep-all)
```````````````` example(Section: Number) options(option)     
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````    
    
.
```````````````` example(Section: Number) options(option)
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````
    
````````````````````````````````


```````````````````````````````` example(Spec Example: 4) options(spec-keep-break)
```````````````` example(Section: Number) options(option)     
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````    
    
.
```````````````` example(Section: Number) options(option)
Markdown only   
…
<p>Markdown only</p>    
…
Document[0, 56]   
````````````````
    
````````````````````````````````


