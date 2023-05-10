<img src="https://github.com/vsch/idea-multimarkdown/raw/master/assets/images/MNLogo.png" height="64" width="64" align="absmiddle"> Markdown Navigator 3.0
==========================================================================================================================================================

[TOC]: #

### Table of Contents
- [Overview](#overview)
  - [Document with pleasure!](#document-with-pleasure)
  - [Two tier model](#two-tier-model)
  - [Source](#source)
- [Background](#background)

## Overview

![Screenshot](/assets/images/capabilities2.png)

**[<span style="color:#30A0D8;">Markdown</span>][Markdown] language support for IntelliJ
platform**

**You can download it on the [JetBrains plugin page].**

### Document with pleasure!

Work with [Markdown] files like you do with other languages in the IDE, by getting full support
for:

* **Paste images** into documents for fast screen capture links
* **Drag & Drop files and images** for fast link insertion
* **Drag and Drop** text, HTML or links from other applications
* **Formatting** to clean up documents with a keystroke
* **Navigation and Find Usages** to find references without effort
* **Copy Markdown** as **JIRA**, **YouTrack** or **HTML** formatted text
* Convert **HTML to Markdown by pasting** it into a Markdown document.
* **Export** to **HTML** or **PDF** to share with others
* **Refactoring** of all referencing elements:
  * files ⟺ links
  * headings ⟺ ref anchors
  * footnotes ⟺ footnote refs
  * references ⟺ ref links/ref images
* **Completions** to reduce typing:
  * link address ⇐ files
  * ref anchors ⇐ headings
  * footnote refs ⇐ footnotes
  * ref links/ref images ⇐ references
  * link text ⇐ ref anchor/link address
* **Error and Warning** annotations to help catch mistakes early
* **Intention actions** for fast results with less effort
* **Wrap on Typing** to keep it nicely formatted as you edit
* **GitHub style rendering** that you are used to, out of the box
* **GitLab parsing and rendering** extensions
  * **Math** extension using [Katex](https://github.com/Khan/KaTeX)
  * **Charts** extension using [Mermaid](https://github.com/knsv/mermaid)
* **Math block and inline** rendering by [Katex] or [codecogs.com]
* **PlantUML fenced code** rendering by [PlantUML] or [gravizo.com]
* **Fast typing response** for distraction free editing
* **Fully customizable** to adjust to your preferences and project needs

### Two tier model

1. Previewing, syntax highlighting, basic editing functionality with link completions with split
   editor preview is available in the basic open source edition. Intended for mostly previewing
   and editing of Markdown documents without full document format.

2. Advanced features used for creating and maintaining Markdown documents: refactoring, find
   usages, validation and auto formatting were available with the Enhanced version, from the
   [JetBrains Marketplace].

   :information_source: The latest release is only to address compatibility issues for newer
   IDEs from 2021.3 to 2023.1.1.

   **It is a full-featured Enhanced version, released without needing a license.**

   It is only intended as a measure to allow users, who depended on this plugin for their
   workflow, to have a working solution until I can figure out if it is possible to continue
   maintaining this plugin in an economically sustainable manner and return Markdown Navigator
   Enhanced to the [JetBrains Marketplace] as a paid plugin, or let it go for good.

### Source

The source in this repository is for the unlicensed version of the plugin. As such, it does not
have all the features of the enhanced version.

#### Working with the source

IntelliJ Gradle Plugin development environment. See [Building from sources](BUILD.md)

#### Some internal details, should you care to know

The [pegdown][] [Markdown] parser used by the plugin in its original incarnation was changed to
[flexmark-java] and [pegdown] dependencies have been removed as of version 2.2.0.

[flexmark-java] is a fork of [commonmark-java], with changes:

* source element based AST with detailed break down of each element for syntax highlighting
* complete source position tracking for all elements and their lexical parts
* optimized for efficient parsing with many parser extensions installed
* unified core and extension options API to simplify parser/renderer configuration
* options to tweak core parser rules

On the coding end, the new parser is a joy to maintain and enhance. The parser architecture,
inherited from [commonmark-java], is easy to debug and test. Markdown element parsers have
little or no interdependencies with other element parsers making it easy to fine tune parser
behaviour on a per-element basis and add parser configuration options to emulate other Markdown
processors. All this is in contrast to pegdown's one big PEG grammar implementation with
everything potentially inter-dependent.

## Background

It all started with a desire to see Markdown files in PhpStorm IDE as they would look on GitHub.
I was already using [nicoulaj/idea-markdown plugin] but found its preview was more like
[Craig's List] than [GitHub]. It did not appear to have been recently updated, so I decided to
fork it and modify its style sheet. How hard could that be?

I found out quickly that there was more to it than meets the eye. Rendering is done by Swing not
a browser, the parser is HTML 3.1, with many features not implemented. Additionally, the Table
extension did not work in `pegdown` version used by the plugin.

I upgraded the plugin to use the latest `pegdown`, `parboiled`. Fixed a few bugs. I was already
in the code, so I might as well add a few more features like user editable style sheet, fix a
few more bugs, add updates to preview so that I could split the editor pane and edit in one
while seeing the preview in the other.

Now, years later, the plugin is brimming with features to make Markdown editing and creation a
breeze. It was a lot more work, and code, than I imagined but more satisfying that I had hoped.

It has been a fun trip down the rabbit hole of IntelliJ IDEA plugin development which started
with a simple desire for a Markdown preview that looked like GitHub's.

---

\* This plugin was originally based on the [nicoulaj/idea-markdown plugin] by [nicoulaj], which
was based on [pegdown] library by [sirthias].

Markdown Navigator, Copyright (c) 2015-2023, Vladimir Schneider, All Rights Reserved.

[JetBrains Marketplace]: https://plugins.jetbrains.com/idea/plugin/7896-markdown-navigator-enhanced
[codecogs.com]: https://codecogs.com
[commonmark-java]: https://github.com/atlassian/commonmark-java
[Craig's List]: https://montreal.craigslist.ca/?lang=en&cc=us
[GitHub]: https://github.com/vsch/laravel-translation-manager
[JetBrains plugin page]: https://plugins.jetbrains.com/plugin/7896-markdown-navigator-enhanced
[Katex]: https://github.com/Khan/KaTeX
[Markdown]: https://daringfireball.net/projects/markdown
[PlantUML]: https://plantuml.com
[flexmark-java]: https://github.com/vsch/flexmark-java
[gravizo.com]: https://gravizo.com
[nicoulaj]: https://github.com/nicoulaj
[nicoulaj/idea-markdown plugin]: https://github.com/nicoulaj/idea-markdown
[pegdown]: http://pegdown.org
[sirthias]: https://github.com/sirthias

