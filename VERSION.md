&nbsp;<details id="version-history"><summary>**Table of
Contents**</summary>

[TOC]: # ""

- [3.0.xxx.103 - Bug Fix Release](#30xxx103---bug-fix-release)
- [3.0.xxx.101 - Bug Fix Release](#30xxx101---bug-fix-release)
- [3.0.xxx.99 - Bug Fix Release](#30xxx99---bug-fix-release)
- [3.0.xxx.97 - Bug Fix Release](#30xxx97---bug-fix-release)
- [3.0.xxx.95 - Bug Fix Release](#30xxx95---bug-fix-release)
- [3.0.xxx.93 - Bug Fix Release](#30xxx93---bug-fix-release)
- [3.0.xxx.91 - Bug Fix Release](#30xxx91---bug-fix-release)
- [3.0.xxx.89 - Bug Fix Release](#30xxx89---bug-fix-release)
- [3.0.xxx.87 - Bug Fix Release](#30xxx87---bug-fix-release)
- [3.0.xxx.85 - Bug Fix EAP Release](#30xxx85---bug-fix-eap-release)
- [3.0.xxx.83 - Bug Fix EAP Release](#30xxx83---bug-fix-eap-release)
- [3.0.xxx.81 - 2020.1 Bug Fix EAP Release](#30xxx81---20201-bug-fix-eap-release)
- [3.0.xxx.79 - 2020.1 EAP Bug Fix & Enhancement EAP Release](#30xxx79---20201-eap-bug-fix--enhancement-eap-release)
- [3.0.xxx.75 - 2020.1 EAP Bug Fix EAP Release](#30xxx75---20201-eap-bug-fix-eap-release)
- [3.0.xxx.72 - 2019.x Bug Fix Release](#30xxx72---2019x-bug-fix-release)
- [3.0.192.66 - 2019.2 Compatibility Release](#3019266---20192-compatibility-release)
- [3.0.197.64 - 2019.3 Compatibility Release](#3019764---20193-compatibility-release)
- [3.0.197.62 - 2019.3 Compatibility Release](#3019762---20193-compatibility-release)
- [3.0.193.60 - 2019.3 Compatibility Release](#3019360---20193-compatibility-release)
- [2.9.11 - 2019.3 Compatibility Release](#2911---20193-compatibility-release)
- [2.9.10 - 2019.3 Compatibility Release](#2910---20193-compatibility-release)
- [2.9.9 - 2019.3 Compatibility Release](#299---20193-compatibility-release)
- [2.9.8 - 2019.3 Compatibility Release](#298---20193-compatibility-release)

&nbsp;</details>

### 3.0.xxx.103 - Bug Fix Release

* Fix: fix link refs to Java/Kotlin files for 2020.2 and AndroidStudio

### 3.0.xxx.101 - Bug Fix Release

* Fix: javafx tools debugger to handle failure when running Java 14 with
  alternate JavaFX Implementation.

  For example for Java 14 and installation of
  [Gluon JavaFX](https://gluonhq.com/products/javafx/) to
  `/Library/Java/Extensions/javafx-sdk-14.0.1` requires running the IDE
  with additional vm options:

  ```
  --add-exports=java.base/jdk.internal.vm=ALL-UNNAMED
  --add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
  --add-exports=javafx.web/com.sun.javafx.scene.web=ALL-UNNAMED
  --module-path /Library/Java/Extensions/javafx-sdk-14.0.1/lib
  --add-modules javafx.controls
  --add-modules javafx.web
  --add-modules javafx.swing
  ```

### 3.0.xxx.99 - Bug Fix Release

* Fix: erroneous optimized TOC levels options for single level headings.
* Fix: add anchor links settings migrated flag.
* Fix: smart edit chars: `*`, `_`, `~` and `` ` ``, on space to delete
  auto-inserted char and allow space processing to proceed. Otherwise,
  auto-wrap on space only does not wrap after auto-inserted character is
  replaced by a space.

### 3.0.xxx.97 - Bug Fix Release

* Fix: diagnostic/4988, `IndexOutOfBoundsException` in
  `MoveColumnLeftAction`
* Fix: diagnostic/4985, `IndexOutOfBoundsException` in
  `MdFoldingVisitor.updateHeadingRanges()`
* Fix: diagnostic/4975, `AlreadyDisposedException` in
  `HtmlRendererExtension.addRenderingProfileOptions`
* Fix: anchor links setting migration would reset new settings flag.

### 3.0.xxx.95 - Bug Fix Release

* Fix: remove 2020.1.2 API use

### 3.0.xxx.93 - Bug Fix Release

* Fix: multi-file drop could mess up links for files after the first
  dropped file.
* Fix: spurious reset of enhanced settings to defaults.
* Fix: clean up deprecated API use.
* Fix: TOC levels parsing to match optimized `levels` syntax.
* Fix: Move `Anchor links` option from `Parser` to `HTML` settings.

### 3.0.xxx.91 - Bug Fix Release

* Add: editor visual guides field to code style wrapping tab
* Fix:
  [#848, Markdown panel does not appear on scratch files - Pycharm 2020.1 Pro],
  reverted file type test code for editor provider to file language type
  test which works for scratch files created via `New File` action in
  `Scratches` directory.
* Fix: make all plugin topics to not broadcast to parent/child message
  buses.
* Fix:
  [#844, Jekyll include elements do not show included content in preview]
* Fix:
  [#828, Memory leak when project is closed due to MdProjectSettings]
* Fix: Tab/Back-Tab list indentation for items at end of file, without
  an EOL and for empty list items with no space after marker.
* Fix: paragraph wrapping context at end of line after editing.

### 3.0.xxx.89 - Bug Fix Release

* Fix: diagnostic/4558, concurrent modification in fenced code
  conversion manager.
* Fix:
  [#840, Edit Table of Contents style intention dialog has no effect],
  toc style edit ignores title level setting if title text has leading
  `#` chars.
* Fix: wrap on typing removing footnote and definition prefix, resulting
  in indented code instead of the original element.

### 3.0.xxx.87 - Bug Fix Release

* Add: Inspections for math, mermaid and plantuml to enable required
  settings if with code fenced elements for these are found in the file.
* Fix: diagnostic/4843, tracked position mismatch after wrapping.
* Fix:
  [#840, Edit Table of Contents style intention dialog has no effect],
  missing update of title level in form

### 3.0.xxx.85 - Bug Fix EAP Release

* Fix:
  [#840, Edit Table of Contents style intention dialog has no effect],
  introduced in code refactoring
* Fix: CSS Script requirement validation done by image converter
  extensions
* Add: GitLab mermaid link from Parser mermaid to fenced code `mermaid`
* Add: GitLab math link from Parser math to fenced code `math`

### 3.0.xxx.83 - Bug Fix EAP Release

* Fix: diagnostic/4707
* Fix: diagnostic/4640
* Fix: diagnostic/4550
* Fix: reversed line marker for flexmark spec example `FILE_EOL` and
  `NO_FILE_EOL` options.
* Fix: update `Show heading id` label text on license state change

### 3.0.xxx.81 - 2020.1 Bug Fix EAP Release

* Fix: multiline URL image links should not wrap
* Fix: for image-to-fenced code and image-to-inline math conversion,
  accept `http://` url prefix as equivalent to `https://`
* Fix: GitHub header id generation compatibility:
  * Add Emoji shortcuts
  * Fix ref link images should not trim trailing spaces in reference or

### 3.0.xxx.79 - 2020.1 EAP Bug Fix & Enhancement EAP Release

* Fix: wiki links should not be wrapped during formatting.
* Fix: Link map default map content for Link Address Element
* Add: Preferences dialog `Menu Split` proportion for configurable tree
  menu to allow full control of preferences dialog layout for screenshot
  capture.
* Fix: exception report settings saved twice in plugin shared settings.
* Add: `kroki.io` fenced code rendering extension
* Fix: update plantuml to version `asl-1.2020.6`
* Add: [JLatexMath](https://github.com/opencollab/jlatexmath) library
  and option to math image rendering.
* Fix: add `Fenced Code` settings pane under `HTML Generation`
* Fix: change UML rendering to use fenced code image rendering EP
* Fix: change Math rendering to use fenced code image rendering EP
* Fix: change HTML Settings to use new fenced code image rendering EP

### 3.0.xxx.75 - 2020.1 EAP Bug Fix EAP Release

* Add: `GrammarCheckingStrategy` for `Grazie` support in Markdown files.
* Add: `Ignore casing of simple list item text` in editor settings to
  ignore capitalization of single line text for list item and definition
  paragraphs.
* Fix: compatibility for 2020.2 snapshot
* Fix: Project file monitor not checking that alarm is disposed.
* Fix: use of IDE resource bundle in code style
* Fix: update to flexmark-java 0.61.0

### 3.0.xxx.72 - 2019.x Bug Fix Release

* Fix: add border around legacy license code text area to identify it in
  settings
* Fix: diagnostic/4566, IndexOutOfBoundsException:
  MdLanguageInjector.getLanguagesToInject
* Add: ability to collapse extension settings sections to allow to
  minimize needed real-estate in settings dialog.
* Fix: `HTML Generation`, `Head Top` checkbox does not change settings
  and does not save.
* Fix: `HTML Generation` sections should have scroll pane, otherwise
  section are hidden if dialog is not big enough
* Fix: repo relative images do not get resolved in preview
* Fix: regression bug, [#818, broken TOC links]

### 3.0.192.66 - 2019.2 Compatibility Release

* Fix: regression bug `MdLexer` and `MdPlainTextLexer`

### 3.0.197.64 - 2019.3 Compatibility Release

* Add: div based HTML tables based on hard-coded class names

### 3.0.197.62 - 2019.3 Compatibility Release

* Fix: change version numbers to be compatible between marketplace and
  legacy versions.
* Fix: marketplace licensed plugin for EAP IDE versions.

### 3.0.193.60 - 2019.3 Compatibility Release

* Add: lic dist versions for marketplace licensing
* Fix: move licensing to own class
* Fix: broken Markdown to HTML intention
* Fix: change references keep to first so sort unused last does not
  toggle references
* Fix: for `Copy Markdown as HTML Mime Content` action to use current
  file's profile parser settings instead of the `COPY_HTML_MIME` profile
  parser settings
* Fix: use own icons for table dialogs
* Fix: Formatter equalize trailing marker should not add marker if there
  is none
* Fix: toc dropping apostrophe from heading text if text only TOC option
  and typographic apostrophes are enabled.
* Fix: update to cleaned up flexmark-java
* Fix: missing add ignore annotation to links and reference definitions
* Fix: missing prepare file for write in quick fixes
* Fix: Add spec test selection is not available in list items
* Fix: GitHub page displayed in preview without option being selected
* Fix: flexmark debug extensions settings not saved.
* Fix: [#767, Do not highlight \[DONE\] as error], adding a reference
  map to empty text will disable these references from being unresolved
  and also render them as text.
* Add: [#779, Ignore character casing when checking for matching header]
* Fix: change `MdProjectRemoteContentCache` to project component
* Fix: typing before/after table caption
* Fix: convert parser tests to light platform test case
* Fix: remove tail blank line generation in formatter after fenced code
  when last child item
* Fix: Remove unused static `LOG` in classes
* Fix: document removed free features from basic version. Intentions are
  mostly enhanced.
* Fix: GitHub issue completion when `#` is at start of line, parsed as
  header level not paragraph text.
* Fix: move references to enh implementation from basic sources
* Fix: complete remove move refs to enh sources from basic psi dir
* Fix: remove flexmark plugin refs from enh and basic sources
  * Add: flexmark document and structure view icons to basic icons
* Fix: make `MdHighlighterColors` a service to prevent multiple instance
  in the application
* Fix: restore `GitHubTableInspection` and add tests
* Fix: split basic/enh resources
* Fix: table element formatting
* Fix: format document with actual char width
* Fix: code style settings preview with actual char width
* Fix: cleanup unused icons
* Fix: move flexmark intention icon to flexmark icons
* Fix: sort extensions by name and add extension name to settings forms
  based on extension provided name
* Fix: add md nav enh heading to exception reports settings
* Fix: move all intentions using enh features to enh plugin.
* Fix: split plugin.xml into components and add `xmlmerge` java utility
  to merge them into single plugin.xml file to allow sharing parts
  between enh and basic plugins.
* Fix: rename dir `idea/multimarkdown` to `md/nav`
* Fix: message bus creation compatibility with 2020.1 and 2019.3.
* Fix: typing `>` in prefix removes first x characters where x is the
  size of indentation before `>`
* Fix: backspacing over `>` after list item prefix should remove block
  quote not the list item
* Fix: typing space at start of indented child paragraph would unindent
  the paragraph instead of indenting it.
* Fix: doubling of typed character when typing at start of indented
  child paragraph.
* Fix: on file save toc update did not resolve duped heading ids.
* Fix: add 250ms delay to `ProjectFileMonitor` invoking file test to
  allow the IDE to detect and sync modified jars.
* Fix: diagnostic/4450, Write access is allowed inside write-action
  only, inside `MdPasteHandler`
* Fix: diagnostic/4462, IndexOutOfBoundsException:
  MdStripTrailingSpacesSmartFilter
* Fix: split parser settings into extensions
* Fix: split flexmark debug settings to extension
* Fix: split enh and flexmark color settings into extensions
* Fix: when pasting selection reference over the link address of link
  whose text matches selection link ref text pattern then replace the
  link element not just the address and anchor ref.
* Fix: remove `SimToc` annotation for title string needs space. No
  longer true for updated flexmark-java parser.
* Add: `MdTabOverride`/`MdBackTabOverride` option to treat empty items
  differently:
  * treat empty items as having no children, should only indent/unindent
    the item in all cases.
  * should roll-over between highest and lowest indentation level when
    the item can no longer be indented to allow tab to be used to adjust
    sub-item to parent item
* Fix: replace Psi based TOC update on save by flexmark-java library
  based update.
* Add: `MdOnSaveFileUpdater` test
* Fix: fix TOC update on save to use parser defined list indent instead
  of 4 fixed.
* Fix: change `EAP` update channel to JetBrains plugin repository EAP
  channel
* Fix: add task item prefix to disable wrap prefix patterns to allow
  typing task item with wrap on typing enabled.
* Fix: `LineAppendable` should not remove leading EOL with 0 flags nor
  do any space trimming when appending content to an appendable. Was
  causing injected language processing to generate exception.
* Fix: wrap on typing caret position recovery.
* Fix: missing typing disabled pattern for ATX heading
* Fix: assertion failure in `MdCodeStyleSettings` which could cause
  settings load issues.
* Fix: preserve task items and spec example order in structure view
* Fix: use `EditorScrollingPositionKeeper` to restore caret vertical
  position after doc format.
* Fix: replace formatter with flexmark-java format module
* Fix: replace wrap on typing with flexmark-java document format for
  paragraph
* Fix: replace use of `BasedSequence` by `BasedSequence` and accumulate
  lines in `LineAppendable`
* Fix: ` * [ ] d|` backspace of last char after indented task item,
  caret jumps to start of line.
* Fix: move remote content fetch to background tasks.
* Fix: NPE on virtualFile being null for formatting documents in code
  style settings.
* Fix: `MdRemoteContentCache`, wrong setting used for remote content
  cache limit causing content fetch task limit to be exceeded. This also
  addresses diagnostic/4425.
* Fix: show editor tooltip when format document or element results in no
  changes.
* Fix: `getEditorTextWithCaretMarkup` is broken for selection with caret
  pos.
* Fix: formatting of flexmark spec files
  * Fix: example with ast not adding EOL before example break
  * Fix: example number not incremented
  * Fix: set min keep blank lines to 2 for spec test files
* Fix: replace Psi based formatter with flexmark formatter.
* Fix: caret tracking for multi-caret mode doc format

### 2.9.11 - 2019.3 Compatibility Release

* Fix:
  [#804, Parser settings always setting default options when loading settings.]
* Fix: profile settings could be deleted if markdown code style settings
  changed and applied if profiles pane was opened before changing
  markdown code style.
* Fix: changing profiles and applying would cause export on settings
  change to be triggered for every changed profile, instead of once for
  all changes.
* Fix: wrap on typing caret jumping to end of next line if typing
  replaces selection.
* Fix: separate out test requiring java plugin into intellij test suite
* Fix: add test run config for PhpStorm and PyCharm Community

### 2.9.10 - 2019.3 Compatibility Release

* Fix: table to JSON to collect link element text if not extracting URL
* Fix: table to JSON to collect link URL not percent decoding
* Fix: table sort moving column up/down deselects it, preventing
  repeated use of shortcuts to arrange column order.
* Add: Copy Transposed Table
* Fix: implement `EditorHighlighterProvider` to provide syntax
  highlighter based on profile for the file and syntax highlighter type
  to eliminate the need to reset editor highlighter on creation to plain
  text lexer for annotator syntax highlighter.
* Fix:
  [#801, Latest version : All options in plugin settings cleared. Reset to defaults does not work.]
* Add: [#759, Table sorting \[Feature request\]]
* Add: Transpose Table
* Fix: indented fenced code content change not adding trailing EOL to
  content before replacing. Causes trailing close marker to be appended
  to last line of content.
* Fix: [#799, Reload editor on underlying file change is not working]
* Fix: [#800, Slow typing response caused by Lexer syntax highlighter]
* Add: `SplitEditor` timing and clean up updates
* Fix: Parser settings to remove extension flags that are set by code
  and not available in the UI: `INTELLIJ_DUMMY_IDENTIFIER`,
  `MULTI_LINE_IMAGE_URLS`, `EXTANCHORLINKS`, `EXTANCHORLINKS_WRAP` when
  saving extensions and option flags. Also to not compare these in
  equals or include in hash code.
* Fix: update profile recognition by removing
* Fix: diagnostic/4386, Throwable: Too many element types registered.
  [#797, Syntax Highlight Permutations can cause: Too many element types exception]
* Fix:
  [#798, Remote content link validation can exceed its max simultaneous fetch threads]
* Fix: consolidate all context parameters into `PsiEditContext` for all
  methods. Remove styleSettings, parserSettings, etc.
* Fix: add `MdCodeStyleSettings` to `MdRenderingProfile`, no UI yet for
  profile code style settings but the code is ready.
* Fix: Spec example with no option was still formatting with `options()`
  keyword
* Fix: diagnostic/4388, IndexOutOfBounds in link resolver manager
* Fix: `ProjectFileMonitor` to not restart code analyzer for a file that
  was invalidated by its own code analyzer run.
* Fix: use flexmark `CharWidthProvider` instead of `SmartData` one.
* Fix: add context logging with timing information to isolate typing
  response lag after extended use.
* Fix: option leak between test spec example
* Fix: should not escape of plain `.` at start of continuation.
* Fix: file move/rename refactoring adjusting links when search for
  references is not selected.
* Fix: index exception on paragraph wrapping
* Fix: backspace after typing a char should not remove spaces which
  surrounded the char
* Fix: Should be called at least in the state COMPONENTS_LOADED
  exception on initialization for 2020.1 compatibility.

### 2.9.9 - 2019.3 Compatibility Release

* Fix: diagnostic/4375, Erroneous use of `javafx.util.Pair` instead of
  `com.vladsch.flexmark.util.Pair` in `MdCodeStyleSettings`

### 2.9.8 - 2019.3 Compatibility Release

* Add: enable enhanced features for Community and Educational IDEs.
* Fix: diagnostic/4369, IllegalStateException: NotNull method
  InlineParserImpl.parseCustom must not return null
* Fix: `JavaFxPreviewAvailable` to only show for project rendering
  profile
* Fix: Merge `MdEnhCodeStyleSettings` into `MdCodeStyleSettings` and
  remove all API and code related to the distinction.
* Fix: un-escape link addresses before resolving links or caching
* Fix: convert flexmark reference search for example option to use
  `FlexmarkSpecCachedData`
* Fix: flexmark example options highlight would highlight unrelated
  ranges. `FlexmarkReferenceSearch` did not check if references were in
  scope for super class definitions.
* Fix: wrap on typing caret adjustment on space between spliced words
* Fix: wrap on typing caret adjustment on space after non-space and
  before keep at start of line elements.
* Add: Add visible LS intention action `➥`
* Fix: remove log4j dependency from `JavaFx-WebView-Debugger`
* Fix: link mapped image links showed unresolved in edge case
  conditions. Easiest way to properly map them is to use a repo relative
  address format so they will resolve everywhere.
* Fix: gutter icons for links do not show in tables because leaf types
  for tables must be matched by token type suffix.
* Fix: add image link ref gutter icon type
* Fix: wrap on typing caret adjustment on backspace in continuation
  indent prefix
* Fix: wrap on typing caret adjustment on backspace when on first
  non-blank of continuation line to splice to last word of previous
  line.
* Fix: conversion from Smart to based to extract more source information
  from segmented sequence and mapped sequence.
* Fix: enable table formatting in document formatter as temp measure to
  make formatter feature complete.
* Fix: wrap on typing with new library use.
* Add: code style option to assign task priority to ordered list items
  for formatting sorting.
* Fix: class cast exception when anchor reference link resolves to
  `MdUrlFakePsiElement` and not a `PsiFile`.
* Fix: use `FlexmarkSpecCachedData` for annotations of spec example
  options.
* Fix: missed disposable in plugin application settings
* Add: flexmark spec line marker option definition data to cached file
  data.
* Remove: from plugin-test-util/lib and plugin-util/lib from git, they
  are duplicated in Mia and Md
* Fix: Add `MdLineSelectionFakePsiElement` resolve caching to
  `MdCachedResolvedLinks` so the file is re-analyzed when the target is
  modified.
* Fix: Add all inherited Java super classes as dependencies to spec file
  even if they do not define options or have `SPEC_RESOURCE` literal.
  The spec file cached data should still be invalidated when those files
  are changed. They may define options and spec literal later.
* Fix: `ProjectFileMonitor` to invalidate the key before restarting code
  analyzer otherwise the key may not be recomputed and code analyzer
  will re-run with stale data.
* Add: `ProjectFilePredicate.getDependentFile()`, if non-null value is
  returned then it will be passed to `DaemonCodeAnalyzer.restartFile()`
  when `ProjectFileMonitor` invalidates the dependency through the
  predicate or by detecting that dependency is not valid. Any key which
  wants to restart its containing file code analyzer should return the
  file from this method.
* Add: `RestartableProjectFileDependency` which creates a default
  restartable predicate that always returns true for the test but has
  the effect of restarting code analyzer for the file. Adding an
  instance of this as a dependency of any key will restart code analyzer
  on the file when the dependency is invalidated.
* Fix: document format adds blank line after last nested list-item if
  followed by a blank line, even when the blank line is after the end of
  the list
* Fix: document format messes up table because it does not include the
  table prefix for the first line.
* Fix: project disposed checking in ProjectFileMonitor.
* Add: option to alias `latex` and `math` for GitLab math if `latex` is
  an available language.
  * `latex` fenced code gets rendered by Katex if extension enabled
  * `math` block and inline elements get `latex` language injection
* Add: Language injection host test case
* Add: GitLab inline math as language injection host
* Add: `MdLanguageProvider` extension point to add aliases and resolve
  language info to language.
* Fix: diagnostic/4355, invalid regex for invalid link address
* Fix: formatter to use max task item from item or descendants for
  sorting (not item priority*10+descendant).
* Fix: move components to services
* Fix: missed dispose disposables
* Add: Goto Rendering profile settings button
* Add: Goto Rendering profile link to show rendering profile/scope
  dialog
* Add: Code style margin override to rendering profile.
* Fix: pasting selection reference (with anchor) on link address which
  already has a ref anchor causes ref anchors to be duplicated. Ref
  anchor should be replaced when pasted link has one.
* Fix: pasting selection reference (with anchor) on link address which
  already has a ref anchor causes ref anchors to be duplicated. Ref
  anchor should be replaced when pasted link has one.
* Fix: `Remove caret and selection markup` does not remove markup in
  selection.
* Fix: change use of `FileContentUtil.reparseFiles` to
  `FileContentUtilCore.reparseFiles`
* Fix: components moved to services
* Fix: formatter incorrectly wrapped text for list items whose complete
  task item marker was removed.
* Fix: formatter to not change bullet item marker for task items when
  prioritized task option is enabled.
* Add: task list item priority by type of bullet list marker. Change
  task item icon in structure view to reflect this. Only applies to
  incomplete tasks.
  * Add: sorting on format to take priority into account if `Enable Task
    List Item Priority` is enabled. `Has Incomplete First` will use
    priority of item and max priority of its sub-items and sort in order
    of descending priority.
  * `+`: high priority, bold red icon
  * `*`: normal priority, normal red icon
  * `-`: low priority, normal blank icon
  * empty tasks have normal grey icon regardless of priority
  * complete tasks have normal grey checked icon regardless of priority
* Fix: preview editor initialized to default typing delay instead of one
  in settings.
* Add: `As URL` option for `Copy Upsource Reference`
* Fix: [#762, freeze when target link is a big file (16mb)]
* Fix:
  [#786, Do not show Clipboard contains markdown  warning in same document]
* Fix: Use
  `com.intellij.testFramework.builders.JavaModuleFixtureBuilder#addJdk`
  for test JDK builders
* Add: `Add test spec example` intention
* Add: `Copy Upsource Reference` for copying upsource refs in
  intellij-community sources.
* Fix: Add custom protocol to line marker and `MdUrlFakePsiElement` for
  navigation
  * Add: icon for `jetbrains://` custom protocol
  * Add: icon for `upsource://` custom protocol
  * Add: icon for custom protocols
  * Add: Gutter icon option for custom protocols
* Fix: change markdown live template element shortcuts to start with
  `,,` instead of `.` to remove accidental live template insertion when
  typing text.
* Fix: `ProjectFilePredicate` test to return `true` when dependency is
  still valid.
* Fix: put heading folding settings to one line
* Fix: put flexmark section folding settings to one line
* Fix: line selection anchors to files not updated when files are edited
  only when saved. Applies to all links to other files. Now on any
  change the links to that file are removed from the cache so they get
  recomputed.
* Fix: optimize link resolver to pre-filter files by name, then by fixed
  prefix before applying the regex for full match test. 2x-4x faster
  link resolution with large number of files.
* Fix: use `linkRef.linkToFile()` in `MdCachedResolvedLinks` instead of
  `urlDecode()` which does not work for `WikiLinkRef`
* Fix: reference search scope failed to pass project to hand-rolled
  effective scope
* Fix: use `resolve()` in `MdLineMarkerProvider` for all except wiki
  links or links to wiki pages.
* Fix: use `resolve()` in `MdAnnotator` for all except wiki links and
  wiki page targets and if not resolved then use loose matching to see
  what can be fixed. Right now `MdAnnotator` is not taking advantage of
  resolved link cache and for large files with tons of links it is very
  slow.
* Add: per file resolved link cache. Amazing speed up for preview and
  all functions which require resolved links.
* Fix: url encoded links do not resolve if they have sub-directories in
  the link address.
* Add: Paste Image option to checker transparency background
* Add: Flexmark Example Option gutter icon option for SPEC_EXAMPLE
* Fix: use singleton list cell renderer for Java line marker spec
  options.
* Fix: use singleton or class for list cell renderers for markdown line
  markers: TOC, spec option, emoji, referencing and file elements
* Fix: spec resource resolving in resources.
* Fix: remove `&nbsp;` use in flexmark spec examples. No longer needed
  for GitHub, since it switched to CommonMark processor.
* Fix: HTML escape quotes in line marker tooltip description in rendered
  spec actual output.
* Fix: flexmark option definitions not showing line markers for
  interfaces defining options with rendering subclasses far down the
  class hierarchy
* Fix: flexmark option definitions are not found if defined in
  interfaces instead of direct supers.
* Fix: annotate flexmark option not defined in all test case classes as
  warning and add navigation quick fix to class(es) missing definition.
* Fix: store file cached data in IDE Key<> for each file instead of a
  `PsiMap` keyed on the file.
* Fix: split `base/` platform/java test cases to `plugin-test-util`
  module.
* Fix: complete flexmark java line marker test code using java test spec
  case.
* Fix: NPE in `FlexmarkPsiImplUtils` on resolution of supers
* Fix: refactor platform spec test case impl to have platform/java
  versions and to have a re-usable base for use in other plugins.
* Fix: add new built-in options to edit example options dialog
* Add: gutter icon configuration with the IDE `Editor > General > Gutter
  Icons`.
* Fix: flexmark example `options` keyword completion to work like normal
  completions and add `()` with caret between `()`
* Add: gutter icon configuration with the IDE `Editor > General > Gutter
  Icons`.
* Fix: `MdLineMarkerProvider` no gutter icon for link address and one
  for anchor. Only anchor icon.
* Fix: fake psi navigation elements to return `null` for
  `getContainingFile()` to disable default IDE navigation and force it
  to use `Navigatable.navigate()` of the element.
  * Fix: `MdUrlFakePsiElement`
  * Fix: `MdLineSelectionFakePsiElement`
* Fix: rebuild for new snapshot build of IDE.
* Fix: update GitHub link resolver tests
* Fix: after format document scroll caret into view.
* Add: option to disable list item smarts if wrap on typing is disabled
  to allow quick disabling of list smart keys without adding extra
  shortcuts or actions.
  * when disabled, disable ENTER list smart key.
  * when disabled, disable backspace list smart key.
* Fix: copy selection reference would add 1 line at the end
* Fix: use `BuilderBase.removeExtensions()` to remove
  `TypographicExtension` from extension list.
* Fix: use `MdUrlFakePsiElement.copyForLineMarkerNavigation()` to create
  fake URL which returns null for containing file so line marker
  navigation uses `navigate()` instead of default implementation.
* Fix: missing partial link refs from copy because of typographic
  extension messing with undefined ref link as if it was text.
* Add: disabled reasons as extra text to menu actions to show why an
  item is disabled.
* Fix: change link to reference modified and committed document before
  creating reference which uses an element from the pre-mod psi file
  causing invalid element access.
* Fix: no line markers for remote links with anchors because the anchor
  is not resolved. If an anchor is not resolved then line marker should
  be to the remote link address.
* Fix: `a` tags in headings do not show up in completions of injected
  markdown if only scoped profile `a` tag parser setting is enabled but
  not project. If project is enabled then scope does not matter. Scope
  should not be ignored for injected fragments.
* Fix: much improved image paste destination directory list
  determination (in decreasing order of priority):
  * last used directory for image paste in current file
  * image directories used for image links in current file, prioritized
    by usage count
  * image directories used for image links in markdown files in the
    parent directory of current file, prioritized by usage count across
    all such files.
  * directories containing image files under the current file's parent
    directory, prioritized by number of images in the directory.
* Add: rudimentary matched profiles and scopes for current file to debug
  which scoped profile is used for a file. The scopes and profile
  assignments are ordered with first match used. Figuring out which one
  is applied to a specific file is otherwise impossible.
* Fix: absolute links to wiki pages do not show change link to relative
  for mapped links. Issue was with link map not code. Needed to map only
  to actual git repo URL not local file directory name based URL. Had
  mapping to markdown3.wiki as per wiki directory name in project
  instead of markdown2.wiki as per git remote URL for the project wiki.
* Fix: message bus wrapper disposed test error.
* Fix: spec files no longer render HTML when option selected. This is
  making all spec test examples fail because the contained text is plain
  text.
* Fix: flexmark example option completions are missing all but built-in
  options, for completions need to use `PsiFile.getOriginalFile()` for
  cached data access since the element is a stand-in, its file is a copy
  of the original and no cache information for it is available.
* Fix: compatibility break in community master branch,
  MessageBusFactory, RestartableLexer
* Add: intention to change HTML entities to text, for example `&lt;` to
  `<`
* Add: intention to change text to HTML entities, reverse of HTML
  entities to text
* Add: Actions to for action test text generation.
* Fix: mixed task and non-task items, toggle prefix adds it to all
  instead of removing only task item prefix or adding to only list
  items. Test is done.
* Fix: wiki links from main repo show as unresolved. All vcs roots in
  SNAPSHOT build of IDE were not seen at initialization.
* Fix: diagnostic/4280, editor is disposed.
* Fix: update `flexmark-java` to reworked rich char impl.
* Fix: update `flexmark-java` to reworked rich char impl.
* Fix: update to fixed reverse-regex
* Fix: update to latest flexmark-java
* Fix: spec examples without options have no line marker to Java test
  case file.
* Fix: spec file link refs contain `&nbsp;` after document format
* Fix: spec file once references show unused, they don't change to used,
  cache invalidation issue. When computing a key, getting another key
  only added dependency if that key was computed, instead of always
  adding dependency to parent transaction.
* Fix: psi set/map to not convert psi file keys to original file. Only
  elements reference `originalElement()`
* Fix: spec example injections replaced any section content with ast
  section of new element
* Fix: cached key version did not check it the key is still valid, so if
  key was not recomputed then dependents would think it is still valid.
* Add: all style and parser options to test options to allow quick test
  creation and minimize reluctance to create test because needed options
  are missing.
* Add: tests for cached referenced map and definition count map.
* Fix: format with wrap text disabled would double the indent of
  continuations every time.
* Fix: paragraph wrapping now working in document format
* Fix: enabled wrap on typing. Caret position tracking is off. Needs
  attention.
* Fix: clean up use of smart data lib where char sequences are not
  involved
* Fix: clean up psi edit adjustment use, eliminate null adjustment and
  always provide file/chars for the instance.
* Fix: localize lex parser state access
* Fix: disable all use of smart data, no table formatting, no wrapping
  until fixed
* Fix: Beta 2019.3 surprises. SmartData library not compatible with
  Kotlin 1.3.60 and needs to be removed.
* Fix: formatter control ignored when child of list item. Was by design.
  removed test for top level element.
* Fix: html comment was improperly parsed to PSI if had leading
  whitespace which CommonMark passes on as is, with leading
  non-indenting whitespace.
* Fix: update flexmark-java 0.59.42, refactored and renamed
  `RichSequence`
* Fix: format would loose custom option params on flex example options
* Fix: wrong offset use in MdInlineCode for offset in parent. Not
  handling multi-line content with prefixed parents.
* Fix: `UrlExtractingTextCollectingVisitor` did not collect typographic
  node text
* Fix: plain text (JSON) was asking about HTML to markdown conversion.
* Fix: HTML paste, if plain text == HTML then disable HTML paste button
* Fix: language injections for fenced code blocks and indented code. Too
  vigorous cleanup in plugin.xml. Removed manipulator in error.
* Fix: inline code injection. Returned wrong range in parent. Did not
  handle blank lines or more than 3 marker chars `` ` `` at beginning of
  a paragraph. Now for the latter, inserts `&nbsp;` before the
  inline-code.
* Fix: `PsiSet` and `PsiMap` to not check equivalency of keys for
  `PsiFile`
* Add: compound spec-example sections, to combine previous lower level
  headings via `splice(" - ")` to allow hierarchical structure view for
  spec files
* Fix: formatter to properly generate spec example section names for
  compound sections
* Fix: example option renaming across all related files: markdown/java
  from spec resource and java classes from class super/sub class
  defining the same option.
* Fix: structure view for spec files to allow hierarchies of sections,
  same as headings
* Fix: make undefined example options resolve to test class for the spec
  file for navigation.
* Fix: annotator for example options
* Fix: flexmark project data cache to have super/subclass and super
  nesting information resolved at data computation time.
  `RenderingTestCase` and first implementor of `SpecExampleProcessor` to
  have level 0, all inheritors add +1.
* Fix: java psi file cache to use project sub/super/level information
  and provide options sorted deeper subclass overrides super classes.
* Fix: resolved literals should have literal name with sorted by class
  hierarchy literal expressions for all classes in the hierarchy which
  define the literal of that name.
* Fix: cached data dependencies rewrite. Now done like the IDE cached
  data management but with transactions and automatic data key
  dependency additions.
* Fix: spec-example spec file name resolution should use class relative
  for non absolute resource paths like the tests themselves.
* Fix: Add proper spec resource string to spec file resolution using
  module and module roots
* Fix: flexmark html settings to provide default sections if no sections
  are defined in persisted state
* Fix: enable in place rename for Java side options.
* Add: add global invalidation lock to use for all dataKey invalidation
  actions
* Add: invalidation nesting for invalidation log indentations
* Fix: `CachedDataSet` was not removing invalidated keys, only
  propagating invalidation to their dependents
* Fix: Cache logging now with low 4 digits of millisecond timestamp and
  thread id to show multi-thread activity.
* Fix: transaction manager locks key/data cache lock for the duration of
  get/compute to prevent access to incomplete results from other threads
* Fix: transaction manager now starts get/compute for initial
  transaction in `readAction`, nested transactions are part of it.
* Fix: all cachedData add/remove/test are synchronized to map instance
  for access only. For computations a lock per dataKey/dataCache is used
  to lock get/compute to ensure consistency from different threads.
* Fix: remove erroneous optimization when registering dependencies which
  caused dependencies to be missed
* Fix: add callback to monitors that all container dependencies were
  removed, as occurs on `CachedDataSet.clearCachedData()`
* Fix: remove cache clearing on `updateContainerState()` call. Only
  restart highlights is needed.
* Fix: custom options highlight red for super classes of test without
  their own `SPEC_RESOURCE` definition.
* Fix: rename of options with custom param preserves custom params
* Fix: data key dependency version should have data key as the instance
* Fix: add transactions for getting cached key values, without
  transaction can `getOrNull()`
* Fix: add validation to all cached data and recompute if has invalid
  elements and log error.
* Fix: flexmark options do not resolve all the way to base class, base
  class show as unresolved and java file has no line markers. Bug in
  flexmark option stub creation
* Fix: intentions for heading link text mismatch don't work without link
  resolution.
* Add: re-entrant lock on cached data key computation so only one thread
  can do a computation for a given key is at a time. Other keys can
  proceed in parallel.
* Add: re-entrant lock on monitor `addDependent()` and
  `checkDependent()`
* Fix: VirtualFileMonitor now subscribes to
  `VirtualFileManager.VFS_CHANGES.after` instead of the async listener.
  Invalidation is done in `Application.invokeLater`.
* Fix: Psi Monitor now subscribes to
  `DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC.daemonStarting` and invalidates
  dependencies before highlighting run, which is ideal to force
  recomputation of invalid data used for highlighting.
* Fix: Psi Monitor now subscribes to
  `VirtualFileManager.VFS_CHANGES.after`. Checks if virtual file
  corresponds to `PsiFile` for invalidation, in
  `Application.invokeLater`. asdfasf
* Add: cached dependencies and management
* Fix: `DataKey` used for nullable value in `HtmlMimeExtension` causing
  `@NotNull` exception when trying to copy HTML mime content.
* Fix: convert inspection to use file cached element data.
* Fix: remove synchronization. Issue was with using computeIfAbsent and
  passing it the data key factory which could and does add other keys as
  it retrieves data to compute its value. This was causing concurrent
  modification exception. Now computing value then adding it.
* Fix: make `MutableDataSet` synchronize all access to `HashMap` to make
  it thread safe for lexing/parsing during indexing.
* Add: `CachedDataSet<>` and `CachedDataKey` to have caching of data in
  `MdFile` and automatic invalidation on cache clear
* Fix: flexmark spec file:
  * use section config for range highlights in editor
  * config for sections in `HTML Generation`
    * languages text boxes for: source, html ast
    * `AST offsets for` checkboxes: `source`, `html`
* Fix: nullable/notnull annotations to flexmark-java library
* Add: allow all in file intention for change link to text
* Fix: `#` to escaped leading chars
* Fix: escaping of leading chars when changing link to text.
* Fix: keep only settings action keys without the instance keys.
* Fix: test settings, add compare and diff settings for non-default at
  end of test case.
* Fix: flexmark spec options to recognize options based on type being
  `Map<String, ? extends DataHolder>`.
* Fix: flexmark resource location/resolver manager class initialization
  conflict
* Fix: full spec expected to use original spec example opening line
* Fix: flexmark custom options now recognized.
* Fix: no highlighting is done because it was disabled with
  `Highlighting Level` set to none.
* Fix: update to new spec-example tests
* Fix: task item done toggle replaces item prefix with task marker
* Fix: trailing spaces filter and tests
* Fix: flexmark options search to walk up the class tree not just check
  class and immediate super
* Fix: structure view headings do not show when only have task items
  contained in children
* Fix: diagnostic/4143 - AssertionError: CharSubSequence
* Fix: diagnostic/4145 - PsiInvalidElementAccessException:
  MdEnhAtxHeaderImpl
* Fix: diagnostic/4146, StackOverflowError
* Fix: add code fence info attribute explicit setting
* Fix: trailing spaces are stripped in all that disable stripping of any
  spaces
* Fix: language injection broken spurious plugin.xml edit
* Fix: toggle task list item not adding prefix
* Fix: move all optional plugin extensions to config files
* Fix: task item toggle was not removing task
* Fix: toggle off empty task item leaves extra space after marker
* Fix: plain text lexer to be restartable
* Fix: diagnostic/4134, IndexOutOfBoundsException, MarkdownTable
* Fix: NPE in injector on checking virtual file writeable
* Fix: restartable lexer getting out of sync with text causing wrong
  highlighting in editor.
* Fix: diagnostic/4131 File is not writable: VirtualFileWindow, in
  PhpStorm
* Fix: wrap on typing:
  * Fix: doubling of first char at start of child element paragraph
  * Fix: indenting list item by typing space before the list marker
    would snap back to original item.
  * Fix: typing `>` after list item marker but before `>` would loose
    the list item marker in `1. >`
* Fix: diagnostic settings migrated but not saved in new version
* Add: fixed in next release status for exception reports
* Fix: smart inline to not erroneously detect context when in existing
  style marker chars
* Fix: attribute paste handler exception to the real cause for it.
* Fix: remove paste handler customization option. Always customize paste
  handler
* Fix: dropped formatter control handling code.
* Fix: visit handler now maps registered element factory class to final
  mapped class
* Fix: Heading inspections broken, add remapping of handler class for
  replaced class factories
* Fix: add check for unregistered class factory during visit/format
  handler declaration, now undefined classes will log a warning.
* Fix: revert code style settings component name back to original. Make
  basic code style settings a new name. Otherwise, existing settings
  will be lost for enhanced plugin uses while basic plugin does not have
  relevant settings to preserve.
* Fix: move translation options to editor settings (from Debug)
* Fix: move flexmark rendering options. (from Debug)
* Fix: add option for text hex dialog to Debug
* Fix: add option for size preferences dialog to Debug
* Fix: sim toc blank line spacer flag is coming from project defaults
  not profile of the file containing the toc.
  * Affected all created elements from text, used project for creating
    file from text cased default profile to be used.
* Fix: diagnostic/4103, Invalid file on MdFileIconProvider.getIcon
* Fix: change default emoji image style to `verical-alighn: middle;`.
* Fix: removing license and pressing cancel then ok. Leaves the license
  but when opening settings not on main markdown settings page, all will
  show as if no license until main settings is opened.
* Fix: Add test for default project to all `project.getComponent()` or
  `project.getService()` calls. These return `null` for the component
  and cause null assertion failures and null pointer failures.
* Add: optional copy path providers, enabled in Preferences | Languages
  & Frameworks | Markdown, add project view copy path options:
  * `File Name` file name with extension and `/` suffix on directories,
  * `Bare File Name` file without extension, if directory then name with
    extension but no `/` suffix
* Fix: spurious failure of JavaFX preview to synchronize preview to
  source or perform highlighting. Caused by bug in passing non-mutable
  JsonObject for state saving
* Fix: extendable settings in rendering profile were not saving to
  rendering profile
* Fix: text field with history were not saving/loading history
* Fix: complete `MdFactoryRenderingProfile` to use for creating code
  style samples with custom rendering profile.
* Fix: discrepancy between todo items in file and item count in `To Do`
  tool window summary.
* Fix: provide file specific profile for lexer highlighter to editors
  based on file.
* Fix: markdown files are always opened with preview and editor,
  ignoring settings.
* Fix: trailing spaces filter broken by moving code to extension point.
* Add: file inspection to notify of TOC element detection with TOC
  extension disabled.
* Fix: indexing of markdown files with customized rendering profile
  instead of project default.
* Fix: add attributes extension to options for code style format samples
  to show format changes.
* Fix: Paste handler now does not require restart to disable if MdNav is
  still the registered paste handler. Next step will be to disable error
  reporting for delegated paste handler errors.
* Add: Admonition info name completions.
* Add: Admonition info inspection and quick-fixes.
* Fix: formatter flows admonition text which is following the first line
  without a blank line.
* Fix: tab list indentation does not work when list item is at EOF
  without EOL
* Fix: heading structure view in enh plugin
* Fix: type factory to be initialized by extensions by providing either
  `IElementType` or class of psi element to override with new type.
* Fix: margin guide in editor always shows default until code style is
  opened and changed.
* Fix: project export occurs often when opening settings. Need to check
  notifications.
* Fix: non-break spaces inserted into file if remove block quote action
  resulted in removal of all block quote prefixes.
* Fix: toggle bullet/numbered/task item on multi-line paragraph no
  longer sets all lines to items. To set all lines to items of a
  paragraph select the lines to be converted to items.
* Fix: `Rendering` config page erroneously showing as modified.
* Fix: Paste image dialog highlights to automatically deselect highlight
  on creation, but not on redefinition with mouse move.
* Add: Paste image dialog checkered background for transparency.
* Fix: line navigation for line selection anchor to not use default file
  navigation
* Fix: duplicate line marker navigation for links
* Add: `Copy Selection Reference` for 2019.3 and prior versions
* Fix: [#773, Pasting URL from chrome pastes meta-data] on Windows
  Chrome. Data contained Chrome HTML segment information.
* Fix: [#768, Copy as reference copies invalid link] on Windows
* Fix: not fixable at this time.
  [#771, macOS: Paste doesn't work when copying link from Safari's context menu],
  new Safari copy link does not put HTML on the clipboard and Java
  cannot access url/url-name data flavours.
* Add: option `Adjust Links on Paste` and `Confirm on Paste` to allow
  not adjusting links when pasting
  * Add: dialog with preview of links and checkbox next to each link to
    allow selecting which links should be adjusted with ability to edit
    adjusted text
* Fix: Paste Image dialog:
  * Fix: use "image" for file name in image paste dialog if it is empty
    and link format is not embedded.
  * Fix: image paste now saves highlights in original image coordinates
    so crop/scale does not affect the highlighted image region.
  * Add: checkered background to show image transparency
* Fix: single space list action would delete blank line and next list
  item marker. Broken in 2.9.0.8/2.9.7.8.
* Fix: disable wrap on typing and auto-format table actions when caret
  is in non-formatting region to provide some feedback that no auto
  format actions will be taken
* Fix: backspace and typing to respect format control markers
* Fix: parser `StackOverflowError` exception with spaces in link urls
  enabled when parsing long URLs.
* Fix: [#776, JavaFx Preview displays cached image for deleted file]
* Fix:
  [#777, No query suffix is added to image links if absolute file:/ format is used]
* Fix: consolidate history and exported files settings into
  `markdown-navigator.xml` project settings file to reduce settings
  files in `.idea` directory.
* Add: `Copy Table as JSON` action to convert table data to JSON
  array/object.
  * Add: auto-selection of last settings used to copy JSON for the table
    from history either named or auto-saved.
  * Add: button to reset column order to markdown column order
  * Add: button to include all columns
  * Add: history based on match based on table header text. Sorted by
    closest match.
  * Add: user definable presets with names and recall saved with project
    settings.
  * Add: extraction of link ref and image ref URL not just inline link
    elements.
* Fix: line markers not showing for elements in tables.
* Fix: uppercase image extensions showed as unresolved in preview and
  would not show in completions or refactor/navigate to file.
* Add: `Space in link URLs` parser option to allow spaces in URLs which
  will be URL encoded for HTML preview and export.
* Fix: export of plantuml, puml and math generated images
* Fix: force export of HTML files which contain generated images because
  the image name changes between exports regardless whether markdown
  content changes or not.
* Fix: `RestartableLexer` implementation for 2019.2
* Fix: flexmark spec case options search to handle options defined in
  super classes
* Fix: merge split settings changes
* Fix: partial fix for
  [#771, macOS: Paste doesn't work when copying link from Safari's context menu],
  will no longer paste HTML link since OS X 10.14.6 does not provide
  HTML on Copy Link from Safari but will paste the address.
* Add: [#768, Copy as reference copies invalid link], now handles copy
  reference of markdown anchor ref target elements so paste will paste
  link to element
* Fix: incorrect determination of loose list items in lists
* Add: Markdown code style settings to import/export settings action.
* Fix: inspection for list item needs more indentation to work with
  CommonMark list rules when mixed ordered/unordered list items are
  mixed and the list item has non-indenting spaces but not enough to
  become a sub-item of previous list's last item.
* Fix: diagnostic/3922, PsiInvalidElementAccessException: Element: class
  invalidated, MdInjector.getLanguagesToInject
* Fix: diagnostic/3923, StringIndexOutOfBoundsException: String index: 0
  out of range: 0, 0, highlight
* Fix: preview links did not preserve anchor ref when launching external
  browser
* Fix: disable HTML Preview missing link highlighting if corresponding
  annotation is disabled.
* Fix: Copy as HTML Mime action image file names with spaces do not get
  converted to embedded images during export.
* Fix: emoji image files are copied on export but generated HTML has
  link to absolute location instead of a relative link to copied file.
* Fix: when linking to exported HTML, allow default link format to
  remain enabled and use it for generating links to exported files.
* Fix: dropping file reference on empty back-ticks to insert file name
  not link
* Fix: formatting of ATX heading in list items would double leading
  markers
* Fix: diagnostic/3872, IllegalStateException: frame must not be null,
  MdPlugin.checkFixes
* Fix: diagnostic/3872, IllegalStateException: frame must not be null,
  MdPlugin.checkFixes

[#759, Table sorting \[Feature request\]]: https://github.com/vsch/idea-multimarkdown/issues/759
[#762, freeze when target link is a big file (16mb)]: https://github.com/vsch/idea-multimarkdown/issues/762
[#767, Do not highlight \[DONE\] as error]: https://github.com/vsch/idea-multimarkdown/issues/767
[#768, Copy as reference copies invalid link]: https://github.com/vsch/idea-multimarkdown/issues/768
[#771, macOS: Paste doesn't work when copying link from Safari's context menu]: https://github.com/vsch/idea-multimarkdown/issues/771
[#773, Pasting URL from chrome pastes meta-data]: https://github.com/vsch/idea-multimarkdown/issues/773
[#776, JavaFx Preview displays cached image for deleted file]: https://github.com/vsch/idea-multimarkdown/issues/776
[#777, No query suffix is added to image links if absolute file:/ format is used]: https://github.com/vsch/idea-multimarkdown/issues/777
[#779, Ignore character casing when checking for matching header]: https://github.com/vsch/idea-multimarkdown/issues/779
[#786, Do not show Clipboard contains markdown  warning in same document]: https://github.com/vsch/idea-multimarkdown/issues/786
[#797, Syntax Highlight Permutations can cause: Too many element types exception]: https://github.com/vsch/idea-multimarkdown/issues/797
[#798, Remote content link validation can exceed its max simultaneous fetch threads]: https://github.com/vsch/idea-multimarkdown/issues/798
[#799, Reload editor on underlying file change is not working]: https://github.com/vsch/idea-multimarkdown/issues/799
[#800, Slow typing response caused by Lexer syntax highlighter]: https://github.com/vsch/idea-multimarkdown/issues/800
[#801, Latest version : All options in plugin settings cleared. Reset to defaults does not work.]: https://github.com/vsch/idea-multimarkdown/issues/801
[#804, Parser settings always setting default options when loading settings.]: https://github.com/vsch/idea-multimarkdown/issues/804
[#818, broken TOC links]: https://github.com/vsch/idea-multimarkdown/issues/818
[#828, Memory leak when project is closed due to MdProjectSettings]: https://github.com/vsch/idea-multimarkdown/issues/828
[#840, Edit Table of Contents style intention dialog has no effect]: https://github.com/vsch/idea-multimarkdown/issues/840
[#844, Jekyll include elements do not show included content in preview]: https://github.com/vsch/idea-multimarkdown/issues/844
[#848, Markdown panel does not appear on scratch files - Pycharm 2020.1 Pro]: https://github.com/vsch/idea-multimarkdown/issues/848

