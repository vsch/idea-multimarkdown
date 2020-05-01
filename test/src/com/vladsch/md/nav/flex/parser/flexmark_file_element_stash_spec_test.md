---
title: File Element Stash
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Find Elements

```````````````````````````````` example(Find Elements: 1) options(find-flexmark-spec, flexmark-spec)
---
...    

```````````````` example
````````````````
.
.
 FlexmarkExampleImpl:[13, 54, "````` … ``````````` example\n````````````````"]
   FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
   WHITESPACE:[29, 30, " "]
   FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
   EOL:[37, 38, "\n"]
   FlexmarkExampleSourceImpl:[38, 38]
   FLEXMARK_EXAMPLE_CLOSE:[38, 54, "````` … ```````````"]
````````````````````````````````


```````````````````````````````` example(Find Elements: 2) options(find-flexmark-spec, flexmark-spec)
---
...    

```````````````` example
````````````````

paragraph text

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
.
 FlexmarkExampleImpl:[13, 55, "````` … ``````````` example\n````````````````\n"]
   FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
   WHITESPACE:[29, 30, " "]
   FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
   EOL:[37, 38, "\n"]
   FlexmarkExampleSourceImpl:[38, 38]
   FLEXMARK_EXAMPLE_CLOSE:[38, 54, "````` … ```````````"]
   EOL:[54, 55, "\n"]
 FlexmarkExampleImpl:[72, 227, "````` … ``````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\n````````````````"]
   FLEXMARK_EXAMPLE_OPEN:[72, 88, "````` … ```````````"]
   WHITESPACE:[88, 89, " "]
   FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[89, 96, "example"]
   EOL:[96, 97, "\n"]
   FlexmarkExampleSourceImpl:[97, 111, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_SOURCE:[97, 111, "Markd … own only\n"]
   FLEXMARK_EXAMPLE_SEPARATOR:[111, 113, "…\n"]
   FlexmarkExampleHtmlImpl:[113, 209, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
     FLEXMARK_EXAMPLE_HTML:[113, 209, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
   FLEXMARK_EXAMPLE_SEPARATOR:[209, 211, "…\n"]
   FlexmarkExampleAstImpl:[211, 211]
   FLEXMARK_EXAMPLE_CLOSE:[211, 227, "````` … ```````````"]
````````````````````````````````


