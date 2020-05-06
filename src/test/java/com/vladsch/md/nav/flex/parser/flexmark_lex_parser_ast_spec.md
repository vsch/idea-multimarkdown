---
title: Flexmark File Element Stash
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

### Flexmark Front Matter

flexmark front matter

```````````````````````````````` example(Flexmark Front Matter: 1) options(flexmark-spec)
---
title: SimToc Extension Spec
author: 
version: 
date: '2016-06-30'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

.
<hr />
.
 MdFile:[0, 155, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n...\n\n---\n\n"]
   FlexmarkFrontMatterBlockImpl:[0, 148, "---\nt … itle: SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_BLOCK:[4, 145, "title … : SimToc Extension Spec\nauthor: \nversion: \ndate: '2016-06-30'\nlicense: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[145, 148, "..."]
   EOL:[148, 149, "\n"]
   MdBlankLineImpl:[149, 150, "\n"]
     BLANK_LINE:[149, 150, "\n"]
   MdHRuleImpl:[150, 154, "---\n"]
     HRULE_TEXT:[150, 154, "---\n"]
   EOL:[154, 155, "\n"]
````````````````````````````````


### SpecExample

`SpecExampleBlock` `SpecExampleOptionsList` `SpecExampleOption` `SpecExampleOptionSeparator`
`SpecExampleSource` `SpecExampleHtml` `SpecExampleAst` `SpecExampleSeparator`

Empty

```````````````````````````````` example(SpecExample: 1) options(flexmark-spec)
---
...    

```````````````` example
````````````````
.
<hr />
.
 MdFile:[0, 54, "---\n. … ..    \n\n```````````````` example\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 54, "````` … ``````````` example\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_CLOSE:[38, 54, "````` … ```````````"]
````````````````````````````````


Empty with nbsp for space on first line

```````````````````````````````` example(SpecExample: 2) options(flexmark-spec)
---
...    

```````````````` example
````````````````
.
<hr />
.
 MdFile:[0, 54, "---\n. … ..    \n\n```````````````` example\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 54, "````` … ``````````` example\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_CLOSE:[38, 54, "````` … ```````````"]
````````````````````````````````


Single spacer

```````````````````````````````` example(SpecExample: 3) options(flexmark-spec)
---
...    

```````````````` example
…
````````````````
.
<hr />
.
 MdFile:[0, 56, "---\n. … ..    \n\n```````````````` example\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 56, "````` … ``````````` example\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_SEPARATOR:[38, 40, "…\n"]
     FlexmarkExampleHtmlImpl:[40, 40]
     FLEXMARK_EXAMPLE_CLOSE:[40, 56, "````` … ```````````"]
````````````````````````````````


Two spacers

```````````````````````````````` example(SpecExample: 4) options(flexmark-spec)
---
...    

```````````````` example
…
…
````````````````
.
<hr />
.
 MdFile:[0, 58, "---\n. … ..    \n\n```````````````` example\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 58, "````` … ``````````` example\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_SEPARATOR:[38, 40, "…\n"]
     FlexmarkExampleHtmlImpl:[40, 40]
     FLEXMARK_EXAMPLE_SEPARATOR:[40, 42, "…\n"]
     FlexmarkExampleAstImpl:[42, 42]
     FLEXMARK_EXAMPLE_CLOSE:[42, 58, "````` … ```````````"]
````````````````````````````````


Extra spacer

```````````````````````````````` example(SpecExample: 5) options(flexmark-spec)
---
...    

```````````````` example
…
…
…
````````````````
.
<hr />
<hr />
<pre><code class="text">…
</code></pre>
.
 MdFile:[0, 60, "---\n. … ..    \n\n```````````````` example\n…\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 60, "````` … ``````````` example\n…\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_SEPARATOR:[38, 40, "…\n"]
     FlexmarkExampleHtmlImpl:[40, 40]
     FLEXMARK_EXAMPLE_SEPARATOR:[40, 42, "…\n"]
     FlexmarkExampleAstImpl:[42, 44, "…\n"]
       FLEXMARK_EXAMPLE_AST:[42, 44, "…\n"]
     FLEXMARK_EXAMPLE_CLOSE:[44, 60, "````` … ```````````"]
````````````````````````````````


Source Only

```````````````````````````````` example(SpecExample: 6) options(flexmark-spec)
---
...    

```````````````` example
Markdown only
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 68, "---\n. … ..    \n\n```````````````` example\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 68, "````` … ``````````` example\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 52, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[38, 52, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[52, 68, "````` … ```````````"]
````````````````````````````````


Source Only, empty HTML

```````````````````````````````` example(SpecExample: 7) options(flexmark-spec)
---
...    

```````````````` example
Markdown only
…
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 70, "---\n. … ..    \n\n```````````````` example\nMarkdown only\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 70, "````` … ``````````` example\nMarkdown only\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 52, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[38, 52, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[52, 54, "…\n"]
     FlexmarkExampleHtmlImpl:[54, 54]
     FLEXMARK_EXAMPLE_CLOSE:[54, 70, "````` … ```````````"]
````````````````````````````````


Source Only, empty HTML and AST

```````````````````````````````` example(SpecExample: 8) options(flexmark-spec)
---
...    

```````````````` example
Markdown only
…
…
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 72, "---\n. … ..    \n\n```````````````` example\nMarkdown only\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 72, "````` … ``````````` example\nMarkdown only\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 52, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[38, 52, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[52, 54, "…\n"]
     FlexmarkExampleHtmlImpl:[54, 54]
     FLEXMARK_EXAMPLE_SEPARATOR:[54, 56, "…\n"]
     FlexmarkExampleAstImpl:[56, 56]
     FLEXMARK_EXAMPLE_CLOSE:[56, 72, "````` … ```````````"]
````````````````````````````````


Html Only, no AST

```````````````````````````````` example(SpecExample: 9) options(flexmark-spec)
---
...    

```````````````` example
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
````````````````
.
<hr />
<hr />
<pre><code class="html">&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
.
 MdFile:[0, 152, "---\n. … ..    \n\n```````````````` example\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 152, "````` … ``````````` example\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_SEPARATOR:[38, 40, "…\n"]
     FlexmarkExampleHtmlImpl:[40, 136, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
       FLEXMARK_EXAMPLE_HTML:[40, 136, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
     FLEXMARK_EXAMPLE_CLOSE:[136, 152, "````` … ```````````"]
````````````````````````````````


Html Only, empty AST

```````````````````````````````` example(SpecExample: 10) options(flexmark-spec)
---
...    

```````````````` example
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
…
````````````````
.
<hr />
<hr />
<pre><code class="html">&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
.
 MdFile:[0, 154, "---\n. … ..    \n\n```````````````` example\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 154, "````` … ``````````` example\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_SEPARATOR:[38, 40, "…\n"]
     FlexmarkExampleHtmlImpl:[40, 136, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
       FLEXMARK_EXAMPLE_HTML:[40, 136, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[136, 138, "…\n"]
     FlexmarkExampleAstImpl:[138, 138]
     FLEXMARK_EXAMPLE_CLOSE:[138, 154, "````` … ```````````"]
````````````````````````````````


Ast Only

```````````````````````````````` example(SpecExample: 11) options(flexmark-spec)
---
...    

```````````````` example
…
…
Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, "Markd"..." only"]
````````````````
.
<hr />
<hr />
<pre><code class="text">Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, &quot;Markd&quot;...&quot; only&quot;]
</code></pre>
.
 MdFile:[0, 249, "---\n. … ..    \n\n```````````````` example\n…\n…\nDocument[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 249, "````` … ``````````` example\n…\n…\nDocument[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 38]
     FLEXMARK_EXAMPLE_SEPARATOR:[38, 40, "…\n"]
     FlexmarkExampleHtmlImpl:[40, 40]
     FLEXMARK_EXAMPLE_SEPARATOR:[40, 42, "…\n"]
     FlexmarkExampleAstImpl:[42, 233, "Docum … ent[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n"]
       FLEXMARK_EXAMPLE_AST:[42, 233, "Docum … ent[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n"]
     FLEXMARK_EXAMPLE_CLOSE:[233, 249, "````` … ```````````"]
````````````````````````````````


Source and HTML, no AST

```````````````````````````````` example(SpecExample: 12) options(flexmark-spec)
---
...    

```````````````` example
Markdown only
…
<pre><code class="language-markdown">Markdown only</code></pre>
<ul>
  <li>List item</li>
</ul>
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
<hr />
<pre><code class="html">&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
.
 MdFile:[0, 166, "---\n. … ..    \n\n```````````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 166, "````` … ``````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 52, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[38, 52, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[52, 54, "…\n"]
     FlexmarkExampleHtmlImpl:[54, 150, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
       FLEXMARK_EXAMPLE_HTML:[54, 150, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
     FLEXMARK_EXAMPLE_CLOSE:[150, 166, "````` … ```````````"]
````````````````````````````````


Source and HTML, empty AST

```````````````````````````````` example(SpecExample: 13) options(flexmark-spec)
---
...    

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
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
<hr />
<pre><code class="html">&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
.
 MdFile:[0, 168, "---\n. … ..    \n\n```````````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 168, "````` … ``````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 52, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[38, 52, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[52, 54, "…\n"]
     FlexmarkExampleHtmlImpl:[54, 150, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
       FLEXMARK_EXAMPLE_HTML:[54, 150, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[150, 152, "…\n"]
     FlexmarkExampleAstImpl:[152, 152]
     FLEXMARK_EXAMPLE_CLOSE:[152, 168, "````` … ```````````"]
````````````````````````````````


Source, HTML and AST

```````````````````````````````` example(SpecExample: 14) options(flexmark-spec)
---
...    

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
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
<hr />
<pre><code class="html">&lt;pre&gt;&lt;code class=&quot;language-markdown&quot;&gt;Markdown only&lt;/code&gt;&lt;/pre&gt;
&lt;ul&gt;
  &lt;li&gt;List item&lt;/li&gt;
&lt;/ul&gt;
</code></pre>
<hr />
<pre><code class="text">Document[0, 56]
  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]
    SpecExampleSource[25, 38] chars:[25, 38, &quot;Markd&quot;...&quot; only&quot;]
</code></pre>
.
 MdFile:[0, 359, "---\n. … ..    \n\n```````````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\nDocument[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 359, "````` … ``````````` example\nMarkdown only\n…\n<pre><code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n…\nDocument[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     EOL:[37, 38, "\n"]
     FlexmarkExampleSourceImpl:[38, 52, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[38, 52, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[52, 54, "…\n"]
     FlexmarkExampleHtmlImpl:[54, 150, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
       FLEXMARK_EXAMPLE_HTML:[54, 150, "<pre> … <code class=\"language-markdown\">Markdown only</code></pre>\n<ul>\n  <li>List item</li>\n</ul>\n"]
     FLEXMARK_EXAMPLE_SEPARATOR:[150, 152, "…\n"]
     FlexmarkExampleAstImpl:[152, 343, "Docum … ent[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n"]
       FLEXMARK_EXAMPLE_AST:[152, 343, "Docum … ent[0, 56]\n  SpecExampleBlock[0, 55] openingMarker:[0, 16] exampleKeyword:[17, 24] source:[25, 38] closingMarker:[39, 55]\n    SpecExampleSource[25, 38] chars:[25, 38, \"Markd\"...\" only\"]\n"]
     FLEXMARK_EXAMPLE_CLOSE:[343, 359, "````` … ```````````"]
````````````````````````````````


Plain Coordinates, section

```````````````````````````````` example(SpecExample: 15) options(flexmark-spec)
```````````````` example Section
Markdown only
````````````````
.
<hr />
<h5>Section: </h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 63, "````` … ``````````` example Section\nMarkdown only\n````````````````"]
   TEXT:[0, 63, "````` … ``````````` example Section\nMarkdown only\n````````````````"]
````````````````````````````````


Plain Coordinates, number

```````````````````````````````` example(SpecExample: 16) options(flexmark-spec)
---
...    

```````````````` example :number
Markdown only
````````````````
.
<hr />
<h5>: number</h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 76, "---\n. … ..    \n\n```````````````` example :number\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 76, "````` … ``````````` example :number\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[38, 39, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[39, 45, "number"]
     EOL:[45, 46, "\n"]
     FlexmarkExampleSourceImpl:[46, 60, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[46, 60, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[60, 76, "````` … ```````````"]
````````````````````````````````


Plain Coordinates, section and number

```````````````````````````````` example(SpecExample: 17) options(flexmark-spec)
---
...    

```````````````` example Section:number
…
…
````````````````
.
<hr />
<h5>Section: number</h5>
.
 MdFile:[0, 73, "---\n. … ..    \n\n```````````````` example Section:number\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 73, "````` … ``````````` example Section:number\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[45, 46, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[46, 52, "number"]
     EOL:[52, 53, "\n"]
     FlexmarkExampleSourceImpl:[53, 53]
     FLEXMARK_EXAMPLE_SEPARATOR:[53, 55, "…\n"]
     FlexmarkExampleHtmlImpl:[55, 55]
     FLEXMARK_EXAMPLE_SEPARATOR:[55, 57, "…\n"]
     FlexmarkExampleAstImpl:[57, 57]
     FLEXMARK_EXAMPLE_CLOSE:[57, 73, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, section

```````````````````````````````` example(SpecExample: 18) options(flexmark-spec)
---
...    

```````````````` example(Section)
Markdown only
````````````````
.
<hr />
<h5>Section: </h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 77, "---\n. … ..    \n\n```````````````` example(Section)\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 77, "````` … ``````````` example(Section)\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[45, 46, ")"]
     EOL:[46, 47, "\n"]
     FlexmarkExampleSourceImpl:[47, 61, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[47, 61, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[61, 77, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, number

```````````````````````````````` example(SpecExample: 19) options(flexmark-spec)
---
...    

```````````````` example(:number)
````````````````
.
<hr />
<h5>: number</h5>
.
 MdFile:[0, 63, "---\n. … ..    \n\n```````````````` example(:number)\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 63, "````` … ``````````` example(:number)\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[38, 39, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[39, 45, "number"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[45, 46, ")"]
     EOL:[46, 47, "\n"]
     FlexmarkExampleSourceImpl:[47, 47]
     FLEXMARK_EXAMPLE_CLOSE:[47, 63, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, section and number

```````````````````````````````` example(SpecExample: 20) options(flexmark-spec)
---
...    

```````````````` example(Section:number)
…
…
````````````````
.
<hr />
<h5>Section: number</h5>
.
 MdFile:[0, 74, "---\n. … ..    \n\n```````````````` example(Section:number)\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 74, "````` … ``````````` example(Section:number)\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[45, 46, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[46, 52, "number"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[52, 53, ")"]
     EOL:[53, 54, "\n"]
     FlexmarkExampleSourceImpl:[54, 54]
     FLEXMARK_EXAMPLE_SEPARATOR:[54, 56, "…\n"]
     FlexmarkExampleHtmlImpl:[56, 56]
     FLEXMARK_EXAMPLE_SEPARATOR:[56, 58, "…\n"]
     FlexmarkExampleAstImpl:[58, 58]
     FLEXMARK_EXAMPLE_CLOSE:[58, 74, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, section

```````````````````````````````` example(SpecExample: 21) options(flexmark-spec)
---
...    

```````````````` example(Section
Markdown only
````````````````
.
<hr />
<h5>Section: </h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 76, "---\n. … ..    \n\n```````````````` example(Section\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 76, "````` … ``````````` example(Section\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     EOL:[45, 46, "\n"]
     FlexmarkExampleSourceImpl:[46, 60, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[46, 60, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[60, 76, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, number

```````````````````````````````` example(SpecExample: 22) options(flexmark-spec)
---
...    

```````````````` example(:number
Markdown only
````````````````
.
<hr />
<h5>: number</h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 76, "---\n. … ..    \n\n```````````````` example(:number\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 76, "````` … ``````````` example(:number\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[38, 39, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[39, 45, "number"]
     EOL:[45, 46, "\n"]
     FlexmarkExampleSourceImpl:[46, 60, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[46, 60, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[60, 76, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, section and number

```````````````````````````````` example(SpecExample: 23) options(flexmark-spec)
---
...    

```````````````` example(Section:number
…
…
````````````````
.
<hr />
<h5>Section: number</h5>
.
 MdFile:[0, 73, "---\n. … ..    \n\n```````````````` example(Section:number\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 73, "````` … ``````````` example(Section:number\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[45, 46, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[46, 52, "number"]
     EOL:[52, 53, "\n"]
     FlexmarkExampleSourceImpl:[53, 53]
     FLEXMARK_EXAMPLE_SEPARATOR:[53, 55, "…\n"]
     FlexmarkExampleHtmlImpl:[55, 55]
     FLEXMARK_EXAMPLE_SEPARATOR:[55, 57, "…\n"]
     FlexmarkExampleAstImpl:[57, 57]
     FLEXMARK_EXAMPLE_CLOSE:[57, 73, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, section

```````````````````````````````` example(SpecExample: 24) options(flexmark-spec)
---
...    

```````````````` example Section)
````````````````
.
<hr />
<h5>Section: </h5>
.
 MdFile:[0, 63, "---\n. … ..    \n\n```````````````` example Section)\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 63, "````` … ``````````` example Section)\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[45, 46, ")"]
     EOL:[46, 47, "\n"]
     FlexmarkExampleSourceImpl:[47, 47]
     FLEXMARK_EXAMPLE_CLOSE:[47, 63, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, number

```````````````````````````````` example(SpecExample: 25) options(flexmark-spec)
---
...    

```````````````` example :number)
Markdown only
````````````````
.
<hr />
<h5>: number</h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 77, "---\n. … ..    \n\n```````````````` example :number)\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 77, "````` … ``````````` example :number)\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[38, 39, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[39, 45, "number"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[45, 46, ")"]
     EOL:[46, 47, "\n"]
     FlexmarkExampleSourceImpl:[47, 61, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[47, 61, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[61, 77, "````` … ```````````"]
````````````````````````````````


Wrapped Coordinates, section and number

```````````````````````````````` example(SpecExample: 26) options(flexmark-spec)
---
...    

```````````````` example Section:number)
…
…
````````````````
.
<hr />
<h5>Section: number</h5>
.
 MdFile:[0, 74, "---\n. … ..    \n\n```````````````` example Section:number)\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 74, "````` … ``````````` example Section:number)\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "Section"]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[45, 46, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[46, 52, "number"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[52, 53, ")"]
     EOL:[53, 54, "\n"]
     FlexmarkExampleSourceImpl:[54, 54]
     FLEXMARK_EXAMPLE_SEPARATOR:[54, 56, "…\n"]
     FlexmarkExampleHtmlImpl:[56, 56]
     FLEXMARK_EXAMPLE_SEPARATOR:[56, 58, "…\n"]
     FlexmarkExampleAstImpl:[58, 58]
     FLEXMARK_EXAMPLE_CLOSE:[58, 74, "````` … ```````````"]
````````````````````````````````


Plain options

```````````````````````````````` example(SpecExample: 27) options(flexmark-spec)
---
...    

```````````````` example options
Markdown only
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 76, "---\n. … ..    \n\n```````````````` example options\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 76, "````` … ``````````` example options\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FlexmarkExampleOptionsImpl:[45, 45]
     EOL:[45, 46, "\n"]
     FlexmarkExampleSourceImpl:[46, 60, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[46, 60, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[60, 76, "````` … ```````````"]
````````````````````````````````


Wrapped options

```````````````````````````````` example(SpecExample: 28) options(flexmark-spec)
---
...    

```````````````` example options()
Markdown only
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 78, "---\n. … ..    \n\n```````````````` example options()\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 78, "````` … ``````````` example options()\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[45, 46, "("]
     FlexmarkExampleOptionsImpl:[46, 46]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[46, 47, ")"]
     EOL:[47, 48, "\n"]
     FlexmarkExampleSourceImpl:[48, 62, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[48, 62, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[62, 78, "````` … ```````````"]
````````````````````````````````


Wrapped options

```````````````````````````````` example(SpecExample: 29) options(flexmark-spec)
---
...    

```````````````` example options(
…
…
````````````````
.
<hr />
.
 MdFile:[0, 67, "---\n. … ..    \n\n```````````````` example options(\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 67, "````` … ``````````` example options(\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[45, 46, "("]
     FlexmarkExampleOptionsImpl:[46, 46]
     EOL:[46, 47, "\n"]
     FlexmarkExampleSourceImpl:[47, 47]
     FLEXMARK_EXAMPLE_SEPARATOR:[47, 49, "…\n"]
     FlexmarkExampleHtmlImpl:[49, 49]
     FLEXMARK_EXAMPLE_SEPARATOR:[49, 51, "…\n"]
     FlexmarkExampleAstImpl:[51, 51]
     FLEXMARK_EXAMPLE_CLOSE:[51, 67, "````` … ```````````"]
````````````````````````````````


Unwrapped options as section

```````````````````````````````` example(SpecExample: 30) options(flexmark-spec)
---
...    

```````````````` example options)
…
…
````````````````
.
<hr />
.
 MdFile:[0, 67, "---\n. … ..    \n\n```````````````` example options)\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 67, "````` … ``````````` example options)\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FlexmarkExampleOptionsImpl:[45, 45]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[45, 46, ")"]
     EOL:[46, 47, "\n"]
     FlexmarkExampleSourceImpl:[47, 47]
     FLEXMARK_EXAMPLE_SEPARATOR:[47, 49, "…\n"]
     FlexmarkExampleHtmlImpl:[49, 49]
     FLEXMARK_EXAMPLE_SEPARATOR:[49, 51, "…\n"]
     FlexmarkExampleAstImpl:[51, 51]
     FLEXMARK_EXAMPLE_CLOSE:[51, 67, "````` … ```````````"]
````````````````````````````````


Plain options

```````````````````````````````` example(SpecExample: 31) options(flexmark-spec)
---
...    

```````````````` example options option
Markdown only
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 83, "---\n. … ..    \n\n```````````````` example options option\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 83, "````` … ``````````` example options option\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     WHITESPACE:[45, 46, " "]
     FlexmarkExampleOptionsImpl:[46, 52, "option"]
       FlexmarkExampleOptionImpl:[46, 52, "option"]
         FlexmarkExampleOptionNameImpl:[46, 52, "option"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[46, 52, "option"]
     EOL:[52, 53, "\n"]
     FlexmarkExampleSourceImpl:[53, 67, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[53, 67, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[67, 83, "````` … ```````````"]
````````````````````````````````


Wrapped options

```````````````````````````````` example(SpecExample: 32) options(flexmark-spec)
---
...    

```````````````` example options(option)
Markdown only
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 84, "---\n. … ..    \n\n```````````````` example options(option)\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 84, "````` … ``````````` example options(option)\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[45, 46, "("]
     FlexmarkExampleOptionsImpl:[46, 52, "option"]
       FlexmarkExampleOptionImpl:[46, 52, "option"]
         FlexmarkExampleOptionNameImpl:[46, 52, "option"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[46, 52, "option"]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[52, 53, ")"]
     EOL:[53, 54, "\n"]
     FlexmarkExampleSourceImpl:[54, 68, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[54, 68, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[68, 84, "````` … ```````````"]
````````````````````````````````


Wrapped options

```````````````````````````````` example(SpecExample: 33) options(flexmark-spec)
---
...    

```````````````` example options(option
…
…
````````````````
.
<hr />
.
 MdFile:[0, 73, "---\n. … ..    \n\n```````````````` example options(option\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 73, "````` … ``````````` example options(option\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[45, 46, "("]
     FlexmarkExampleOptionsImpl:[46, 52, "option"]
       FlexmarkExampleOptionImpl:[46, 52, "option"]
         FlexmarkExampleOptionNameImpl:[46, 52, "option"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[46, 52, "option"]
     EOL:[52, 53, "\n"]
     FlexmarkExampleSourceImpl:[53, 53]
     FLEXMARK_EXAMPLE_SEPARATOR:[53, 55, "…\n"]
     FlexmarkExampleHtmlImpl:[55, 55]
     FLEXMARK_EXAMPLE_SEPARATOR:[55, 57, "…\n"]
     FlexmarkExampleAstImpl:[57, 57]
     FLEXMARK_EXAMPLE_CLOSE:[57, 73, "````` … ```````````"]
````````````````````````````````


Unwrapped options as section

```````````````````````````````` example(SpecExample: 34) options(flexmark-spec)
---
...    

```````````````` example options option)
…
…
````````````````
.
<hr />
.
 MdFile:[0, 74, "---\n. … ..    \n\n```````````````` example options option)\n…\n…\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 74, "````` … ``````````` example options option)\n…\n…\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     WHITESPACE:[45, 46, " "]
     FlexmarkExampleOptionsImpl:[46, 52, "option"]
       FlexmarkExampleOptionImpl:[46, 52, "option"]
         FlexmarkExampleOptionNameImpl:[46, 52, "option"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[46, 52, "option"]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[52, 53, ")"]
     EOL:[53, 54, "\n"]
     FlexmarkExampleSourceImpl:[54, 54]
     FLEXMARK_EXAMPLE_SEPARATOR:[54, 56, "…\n"]
     FlexmarkExampleHtmlImpl:[56, 56]
     FLEXMARK_EXAMPLE_SEPARATOR:[56, 58, "…\n"]
     FlexmarkExampleAstImpl:[58, 58]
     FLEXMARK_EXAMPLE_CLOSE:[58, 74, "````` … ```````````"]
````````````````````````````````


Wrapped options

```````````````````````````````` example(SpecExample: 35) options(flexmark-spec)
---
...    

```````````````` example options(1, 2,, ,  ,3 , 4 )
Markdown only
````````````````
.
<hr />
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 95, "---\n. … ..    \n\n```````````````` example options(1, 2,, ,  ,3 , 4 )\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 95, "````` … ``````````` example options(1, 2,, ,  ,3 , 4 )\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[38, 45, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[45, 46, "("]
     FlexmarkExampleOptionsImpl:[46, 63, "1, 2, … , ,  ,3 , 4 "]
       FlexmarkExampleOptionImpl:[46, 47, "1"]
         FlexmarkExampleOptionNameImpl:[46, 47, "1"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[46, 47, "1"]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[47, 48, ","]
       WHITESPACE:[48, 49, " "]
       FlexmarkExampleOptionImpl:[49, 50, "2"]
         FlexmarkExampleOptionNameImpl:[49, 50, "2"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[49, 50, "2"]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[50, 52, ",,"]
       WHITESPACE:[52, 53, " "]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[53, 54, ","]
       WHITESPACE:[54, 56, "  "]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[56, 57, ","]
       FlexmarkExampleOptionImpl:[57, 58, "3"]
         FlexmarkExampleOptionNameImpl:[57, 58, "3"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[57, 58, "3"]
       WHITESPACE:[58, 59, " "]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[59, 60, ","]
       WHITESPACE:[60, 61, " "]
       FlexmarkExampleOptionImpl:[61, 62, "4"]
         FlexmarkExampleOptionNameImpl:[61, 62, "4"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[61, 62, "4"]
       WHITESPACE:[62, 63, " "]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[63, 64, ")"]
     EOL:[64, 65, "\n"]
     FlexmarkExampleSourceImpl:[65, 79, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[65, 79, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[79, 95, "````` … ```````````"]
````````````````````````````````


Unwrapped coords Wrapped options

```````````````````````````````` example(SpecExample: 36) options(flexmark-spec)
---
...    

```````````````` example section:number options(1, 2,3 , 4 )
````````````````
.
<hr />
<h5>section: number</h5>
.
 MdFile:[0, 90, "---\n. … ..    \n\n```````````````` example section:number options(1, 2,3 , 4 )\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 90, "````` … ``````````` example section:number options(1, 2,3 , 4 )\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     WHITESPACE:[37, 38, " "]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "section"]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[45, 46, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[46, 52, "number"]
     WHITESPACE:[52, 53, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[53, 60, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[60, 61, "("]
     FlexmarkExampleOptionsImpl:[61, 72, "1, 2, … 3 , 4 "]
       FlexmarkExampleOptionImpl:[61, 62, "1"]
         FlexmarkExampleOptionNameImpl:[61, 62, "1"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[61, 62, "1"]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[62, 63, ","]
       WHITESPACE:[63, 64, " "]
       FlexmarkExampleOptionImpl:[64, 65, "2"]
         FlexmarkExampleOptionNameImpl:[64, 65, "2"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[64, 65, "2"]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[65, 66, ","]
       FlexmarkExampleOptionImpl:[66, 67, "3"]
         FlexmarkExampleOptionNameImpl:[66, 67, "3"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[66, 67, "3"]
       WHITESPACE:[67, 68, " "]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[68, 69, ","]
       WHITESPACE:[69, 70, " "]
       FlexmarkExampleOptionImpl:[70, 71, "4"]
         FlexmarkExampleOptionNameImpl:[70, 71, "4"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[70, 71, "4"]
       WHITESPACE:[71, 72, " "]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[72, 73, ")"]
     EOL:[73, 74, "\n"]
     FlexmarkExampleSourceImpl:[74, 74]
     FLEXMARK_EXAMPLE_CLOSE:[74, 90, "````` … ```````````"]
````````````````````````````````


Wrapped coords Wrapped options

```````````````````````````````` example(SpecExample: 37) options(flexmark-spec)
---
...    

```````````````` example(section:number) options(1, 2,3 , 4 )
Markdown only
````````````````
.
<hr />
<h5>section: number</h5>
<hr />
<pre><code class="markdown">Markdown only
</code></pre>
.
 MdFile:[0, 105, "---\n. … ..    \n\n```````````````` example(section:number) options(1, 2,3 , 4 )\nMarkdown only\n````````````````"]
   FlexmarkFrontMatterBlockImpl:[0, 7, "---\n..."]
     FLEXMARK_FRONT_MATTER_OPEN:[0, 3, "---"]
     EOL:[3, 4, "\n"]
     FLEXMARK_FRONT_MATTER_CLOSE:[4, 7, "..."]
   EOL:[7, 12, "    \n"]
   MdBlankLineImpl:[12, 13, "\n"]
     BLANK_LINE:[12, 13, "\n"]
   FlexmarkExampleImpl:[13, 105, "````` … ``````````` example(section:number) options(1, 2,3 , 4 )\nMarkdown only\n````````````````"]
     FLEXMARK_EXAMPLE_OPEN:[13, 29, "````` … ```````````"]
     WHITESPACE:[29, 30, " "]
     FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD:[30, 37, "example"]
     FLEXMARK_EXAMPLE_SECTION_OPEN:[37, 38, "("]
     FLEXMARK_EXAMPLE_SECTION:[38, 45, "section"]
     FLEXMARK_EXAMPLE_NUMBER_SEPARATOR:[45, 46, ":"]
     FLEXMARK_EXAMPLE_NUMBER:[46, 52, "number"]
     FLEXMARK_EXAMPLE_SECTION_CLOSE:[52, 53, ")"]
     WHITESPACE:[53, 54, " "]
     FLEXMARK_EXAMPLE_OPTIONS_KEYWORD:[54, 61, "options"]
     FLEXMARK_EXAMPLE_OPTIONS_OPEN:[61, 62, "("]
     FlexmarkExampleOptionsImpl:[62, 73, "1, 2, … 3 , 4 "]
       FlexmarkExampleOptionImpl:[62, 63, "1"]
         FlexmarkExampleOptionNameImpl:[62, 63, "1"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[62, 63, "1"]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[63, 64, ","]
       WHITESPACE:[64, 65, " "]
       FlexmarkExampleOptionImpl:[65, 66, "2"]
         FlexmarkExampleOptionNameImpl:[65, 66, "2"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[65, 66, "2"]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[66, 67, ","]
       FlexmarkExampleOptionImpl:[67, 68, "3"]
         FlexmarkExampleOptionNameImpl:[67, 68, "3"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[67, 68, "3"]
       WHITESPACE:[68, 69, " "]
       FLEXMARK_EXAMPLE_OPTION_SEPARATOR:[69, 70, ","]
       WHITESPACE:[70, 71, " "]
       FlexmarkExampleOptionImpl:[71, 72, "4"]
         FlexmarkExampleOptionNameImpl:[71, 72, "4"]
           FLEXMARK_EXAMPLE_OPTION_NAME:[71, 72, "4"]
       WHITESPACE:[72, 73, " "]
     FLEXMARK_EXAMPLE_OPTIONS_CLOSE:[73, 74, ")"]
     EOL:[74, 75, "\n"]
     FlexmarkExampleSourceImpl:[75, 89, "Markd … own only\n"]
       FLEXMARK_EXAMPLE_SOURCE:[75, 89, "Markd … own only\n"]
     FLEXMARK_EXAMPLE_CLOSE:[89, 105, "````` … ```````````"]
````````````````````````````````


