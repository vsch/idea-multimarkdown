<img src="https://github.com/vsch/idea-multimarkdown/raw/master/assets/images/MNLogo.png" height="64" width="64" align="absmiddle"> Markdown Navigator 2.8
==========================================================================================================================================================

[TOC levels=2,3]: # "Table of Contents"

### Table of Contents
- [Overview](#overview)
    - [Document with pleasure!](#document-with-pleasure)
    - [Two tier model](#two-tier-model)
- [Release Road Map](#release-road-map)
    - [Future Release: Version 3.0.0](#future-release-version-300)
    - [Version 2.9.0/2.9.7](#version-290297)
    - [Version 2.8.4](#version-284)
    - [Version 2.8.2](#version-282)
    - [Source Update is Long Overdue](#source-update-is-long-overdue)
- [Background](#background)


## Overview

![Screenshot](/assets/images/capabilities2.png)

**[<span style="color:#30A0D8;">Markdown</span>][Markdown] language support for IntelliJ
platform**

**A Markdown plugin** with GFM and a **matching** preview style.

**You can download it on the [JetBrains plugin page].**

**Get Markdown Navigator [enhanced edition][Markdown Navigator] to unlock all productivity
features.**

### Document with pleasure!

Work with [Markdown] files like you do with other languages in the IDE, by getting full support
for:

* **Paste images** into documents for fast screen capture links
* **Drag & Drop files and images** for fast link insertion
* **Drag and Drop** text, HTML or links from other applications
* **Formatting** to cleanup documents with a key stroke
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
   and editing of markdown documents without full document format.

2. Advanced features used for creating and maintaining markdown documents: refactoring, find
   usages, validation and auto formatting are only available in the Enhanced licensed version.
   15-day free trial licenses are available from [Markdown Navigator] page on my website.

Release Road Map
----------------

### Future Release: Version 3.0.0

**Major Refactoring Release**

* Extensibility API
* Enhanced functionality to be provided by separate plugin(s).
* Improved Multiple Module Support
* Link resolution support for non-GitHub VCS providers
* Plus the usual bug fixes that come up between now and the release.

### Version 2.9.0/2.9.7

Long list of Bug and compatibility fixes you can find in [Version Notes].

[Install EAP plugin](../../wiki/EAP-Update-Channel) version to preview next release.

* **Split plugin versions**: 2.9.0 for pre 2019.2 IDEs and 2.9.5 for 2019.2 and later
* **Configuration Search** in all configuration panes
* **Exception Report** tracking with notification of fixes
* **Java 11 JRE Support**

### Version 2.8.4

* **Java 11 JRE Support**
* **Improved Preview and Typing** response
* **Cut/Copy/Paste Improved** to handle Macros, Enumerated References and Link adjustments.
* Copy/Paste **between IDE instances** with all references and links properly included and
  adjusted.
* **Plain Text Completions** to insert references defined in file and in project scope. See:
  [Plain Text Suggestion Scope]
* **Inspection Option** to ignore markdown text in fenced code
* **New Inspections**
  * mismatch of link to heading text for anchor refs to headings
  * unicode LINE SEPARATOR in file
  * when a list item needs more indentation to be a sub-item
* **Annotator Settings** to customize enabled annotations
* **Annotator Errors** to show underline for file in project tree
* **New Intentions** to wrap auto-link looking text in `<>`
* **New HTML Paste Options** convert links to: references, text, HTML or explicit links
* **New Structure View Option** Show Headings & Tasks
* **Show Members** in project view to show headings
* **Format Document** option to remove unused reference element definitions.
* **In-place Rename** of headings, references and other elements
* **Dropped Links** insert options: `Link`, `Ref Link & Reference`, `Reference Only`
* **Syntax Highlighter Attributes** for GitLab extension elements
* Comment directive to **Ignore invalid link or anchor** for annotation
* Create **PDF Export** profile, see [Configuring for Non-Latin Character Sets]
* Default **Completion format** setting to change starting link address format.

### Version 2.8.2

* Enhanced edition features migrated to basic edition:
  * **Extensions**: Jekyll Front Matter
  * **Inspections**: GitHub Table, Jekyll Front Matter, Emoji Shortcut
  * **Completions**: Emoji shortcuts, Links, References, Fenced code language
  * **Editor Settings**: Language Injections, Remove prefix when joining lines, Simple structure
    view
  * **Actions and Toolbar buttons**:
    * Toggle inline styles
    * Block quote actions
    * List actions
    * Insert table action
    * Format Element
  * **Wrap on typing**
* Create **PDF Export** profile [for non-Latin character sets][Rendering Profiles Settings]
* **Drag and Drop** text, HTML or links from other applications
* **Copy PlantUML image** intention to copy diagram to clipboard
* Convert between **codecogs.com images** and **Math block and inline**
* Convert between **gravizo.com images** and **PlantUML fenced code**
* **Math block and inline** rendering by [Katex] or [codecogs.com]
* **PlantUML fenced code** rendering via PlantUML library or gravizo.com
* **Multi-Line Image URL** language injections
* **GitHub Issue** completions insert link to issue
* **Show HTML page as Documentation** for GitHub issues and optionally for URLs
* **Improved Directory/Package Refactoring** for all IDEs
* Improved: toolbar actions dealing with block quotes, list items, heading level up/down to
  handle conversion to/from plain text.
* New: heading style conversion during document format
* New: Convert fenced code to indented code intention
* New: `Copy Exported as HTML Mime Content` Action to copy HTML export text as HTML mime content
* New: Image Paste highlight option to annotate an area of the image.
  ![Screenshot_PasteImage_Highlight]
* New: `Copy markdown without soft line breaks` copies document or selection to the clipboard
  eliminating soft line breaks. Useful when copying wrapped markdown to GitHub comments.
* New: All copy markdown actions: `CopyHtmlMimeFormattedAction`, `CopyJiraFormattedAction`,
  `CopyNoSoftLineBreaksAction` and `CopyYouTrackFormattedAction`, now include all reference
  defining elements which were referenced from the copied part. All links and other elements
  resolve without needing to include their reference definitions in the selection.
* New: option for full highlight attributes, enabled takes 3700 attributes out of the pool,
  while disabled less than 300.

### Source Update is Long Overdue

Now that I unified the display between the licensed and unlicensed versions by removing support
for preview tabs in favour of split editor as the only option, I will be updating the open
source with a new version much easier.

The update will be more like a replacement of the source than an evolution of it. The directory
structure has changed significantly and a lot of Java has been converted to Kotlin.

The enhanced version is now 120k lines of code so my goal is to separate the open source
functionality from the proprietary code with a clean boundary to make open source releases
synchronized with plugin releases like they used to be but without the need for manual merging
between the two.

#### Working with the source

IntelliJ Plugin SDK development environment.

#### Some internal details, should you care to know

The [pegdown][] [Markdown] parser used by the plugin in its original incarnation was changed to
[flexmark-java] and [pegdown] dependencies have been removed as of version 2.2.0.

[flexmark-java] is a fork of [commonmark-java], with changes:

* source element based AST with detailed break down of each element for syntax highlighting
* complete source position tracking for all elements and their lexical parts
* optimized for efficient parsing with many parser extensions installed
* unified core and extension options API to simplify parser/renderer configuration
* options to tweak core parser rules

In the process of making the needed modifications to the original [commonmark-java] parser,
performance was impacted by about 25-35%. This still makes the new parser **7x-10x** faster than
[intellij-markdown] parser used by [Markdown Support] and **25x-50x** faster than pegdown. As an
added benefit, the new parser does not suffer from pegdown's idiosyncrasies of exponential parse
times or pathological input cases that cause infinite loops in the parser's state machine.

On the coding end, the new parser is a joy to maintain and enhance. The parser architecture,
inherited from [commonmark-java], is easy to debug and test. Markdown element parsers have
little or no interdependencies with other element parsers making it easy to fine tune parser
behaviour on a per element basis and add parser configuration options to emulate other markdown
processors. All this is in contrast to pegdown's one big PEG grammar implementation with
everything potentially inter-dependent.

Background
----------

It all started with a desire to see Markdown files in PhpStorm IDE as they would look on GitHub.
I was already using [nicoulaj/idea-markdown plugin] but found its preview was more like
[Craig's List] than [GitHub]. It did not appear to have been recently updated, so I decided to
fork it and modify its style sheet. How hard could that be?

I found out quickly that there was more to it than meets the eye. Rendering is done by Swing not
a browser, the parser is HTML 3.1, with many features not implemented. Additionally, the Table
extension did not work in `pegdown` version used by the plugin.

I upgraded the plugin to use the latest `pegdown`, `parboiled`. Fixed a few bugs. I was already
in the code so I might as well add a few more features like user editable style sheet, fix a few
more bugs, add updates to preview so that I could split the editor pane and edit in one while
seeing the preview in the other.

Now almost four years later, the plugin is brimming with features to make Markdown editing and
creation a breeze. It was more work than I imagined but more satisfying that I had hoped.

It has been a fun trip down the rabbit hole of IntelliJ IDEA plugin development which started
with a simple desire for a Markdown preview that looked like GitHub's.

---

\* This plugin was originally based on the [nicoulaj/idea-markdown plugin] by [nicoulaj], which
was based on [pegdown] library by [sirthias].

Markdown Navigator, Copyright (c) 2015-2019, V. Schneider, <http://vladsch.com> All Rights
Reserved.

[codecogs.com]: https://codecogs.com
[commonmark-java]: https://github.com/atlassian/commonmark-java
[Craig's List]: https://montreal.craigslist.ca/?lang=en&cc=us
[flexmark-java]: https://github.com/vsch/flexmark-java
[GitHub]: https://github.com/vsch/laravel-translation-manager
[gravizo.com]: http://gravizo.com
[intellij-markdown]: https://github.com/valich/intellij-markdown
[JetBrains plugin page]: https://plugins.jetbrains.com/plugin/7896-markdown-navigator
[Katex]: https://github.com/Khan/KaTeX
[Markdown]: https://daringfireball.net/projects/markdown
[Markdown Navigator]: http://vladsch.com/product/markdown-navigator
[Markdown Support]: https://plugins.jetbrains.com/plugin/7793?pr=
[nicoulaj]: https://github.com/nicoulaj
[nicoulaj/idea-markdown plugin]: https://github.com/nicoulaj/idea-markdown
[pegdown]: http://pegdown.org
[PlantUML]: http://plantuml.com
[Rendering Profiles Settings]: https://github.com/vsch/idea-multimarkdown/wiki/Rendering-Profiles-Settings#configuring-for-non-latin-character-sets
[Screenshot_PasteImage_Highlight]: /assets/images/faq/ScreenShot_PasteImage_sm_h.png
[sirthias]: https://github.com/sirthias
[Version Notes]: https://github.com/vsch/idea-multimarkdown/blob/master/resources/META-INF/VERSION.md
[.gitignore]: http://ignore.hsz.mobi/
[Admonition Extension, Material for MkDocs]: https://squidfunk.github.io/mkdocs-material/extensions/admonition/
[Android Studio]: https://developer.android.com/sdk/installing/studio.html
[AppCode]: http://www.jetbrains.com/objc
[CLion]: https://www.jetbrains.com/clion
[CommonMark]: https://commonmark.org
[CommonMark (spec 0.28)]: https://spec.commonmark.org/0.28
[Copy Jira]: https://github.com/vsch/idea-multimarkdown/raw/master/resources/icons/editor_actions/Copy_jira.png
[Copy Markdown to HTML formatted text]: https://github.com/vsch/idea-multimarkdown/wiki/Enhanced-Features#copy-markdown-to-html-formatted-text
[DataGrip]: https://www.jetbrains.com/datagrip
[GitHub Issues]: https://github.com/vsch/idea-multimarkdown/issues
[GitHub Issues page]: https://github.com/vsch/idea-multimarkdown/issues/
[GitHub wiki in IntelliJ IDE]: https://github.com/vsch/idea-multimarkdown/wiki/Adding-GitHub-Wiki-to-IntelliJ-Project
[GitHub Wiki pages]: https://github.com/vsch/idea-multimarkdown/wiki
[GitHub-userscripts]: https://github.com/Mottie/GitHub-userscripts
[holgerbrandl/pasteimages]: https://github.com/holgerbrandl/pasteimages
[IntelliJ IDEA]: http://www.jetbrains.com/idea
[Jekyll]: https://jekyllrb.com
[JetBrains plugin comment and rate page]: https://plugins.jetbrains.com/plugin/7896-markdown-navigator
[Kotlin]: https://kotlinlang.org
[Kramdown]: https://kramdown.gettalong.org/
[Modifying Link Processing]: https://github.com/vsch/idea-multimarkdown/wiki/Link-Map-Settings
[MultiMarkdown]: https://fletcherpenney.net/multimarkdown
[Pandoc]: https://pandoc.org/MANUAL.html#pandocs-markdown
[PhpExtra]: https://michelf.ca/projects/php-markdown/extra/
[PhpStorm]: http://www.jetbrains.com/phpstorm
[Pipe Table Formatter]: https://github.com/anton-dev-ua/PipeTableFormatter
[PyCharm]: http://www.jetbrains.com/pycharm
[RubyMine]: http://www.jetbrains.com/ruby
[sirthias/pegdown]: https://github.com/sirthias/pegdown
[vsch/pegdown]: https://github.com/vsch/pegdown/tree/develop
[WebStorm]: http://www.jetbrains.com/webstorm
[Wiki]: https://github.com/vsch/idea-multimarkdown/wiki


[Configuring for Non-Latin Character Sets]: https://github.com/vsch/idea-multimarkdown/wiki/Rendering-Profiles-Settings#configuring-for-non-latin-character-sets

[Plain Text Suggestion Scope]: https://github.com/vsch/idea-multimarkdown/wiki/Rendering-Profiles-Settings#plain-text-suggestion-scope
