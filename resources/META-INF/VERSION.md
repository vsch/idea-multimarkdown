[TOC levels=3,4]: # "Version History"

### Version History
- [2.9.7 - Bug Fix & Compatibility Release](#297---bug-fix--compatibility-release)
- [2.9.0/2.9.5 - Bug Fix & Compatibility Release](#290295---bug-fix--compatibility-release)
- [2.8.4 - Bug Fix & Enhancement Release](#284---bug-fix--enhancement-release)
- [2.8.2 - Bug Fix & Enhancement Release](#282---bug-fix--enhancement-release)
- [2.8.0 - Bug Fix & Enhancement Release](#280---bug-fix--enhancement-release)
- [2.7.0 - Bug Fix & Enhancement Release](#270---bug-fix--enhancement-release)
- [2.6.0 - Bug Fix & Enhancement Release](#260---bug-fix--enhancement-release)


### 2.9.7 - Bug Fix & Compatibility Release

* Fix: compatibility with 193.SNAPSHOT

### 2.9.0/2.9.5 - Bug Fix & Compatibility Release

* Fix: diagnostic/3761, NoClassDefFoundError: ArraysKt$asSequence
* Fix: Formatter adding `LINE_SEPARATOR` after images and links when they are at start of line
* Fix: Add attribute formatting code style settings
* Fix: attributes code style settings in headings were not applied when formatting
* Fix: diagnostic reporting fixed version to handle split plugin versions for IDE versions < 192
  and >= 192.

##### 2.8.4.38/2.8.9.38 - EAP Release

* Fix: [#761, html paste option can't save]
* Fix: diagnostic/3638 AssertionError: MdProjectComponent.reloadMarkdownEditors
* Fix: diagnostic/3425 Assertion: MdPasteHandler.getFilePathAnchorFromFqn

##### 2.8.4.36/2.8.9.36 - EAP Release

* Fix: [#760, Attributes support is broken/incompatible with fenced code blocks]

##### 2.8.4.34/2.8.9.34 - EAP Release

* Fix: Split plugin into 192 and pre-192 versions
* Fix: update to flexmark-java 0.50.18, including new HTML converter
* Fix: hard break inside inline elements were not preserved on save.
* Fix: pasting HTML with image URLs starting with `//` without `https:` or `http:` protocol
  prefix would cause UI to hang while attempting to download image. Now only non-image
  extensions or `.svg` links which have protocol extension or non-absolute links will attempt to
  convert svg to png.

##### 2.8.4.32 - EAP Release

* Fix: [#757, Gitlab multi-line blockquote syntax incorrect]
* Fix: diagnostic/3359, IllegalStateException: projectPlainTextFileTypeManager must not be null
* Fix: compatibility with 2019.2 IDEs
* Fix: update to flexmark-java 0.50.13
* Fix: diagnostic/3251, NullPointerException: MdProjectComponent$exportAllHtmlRaw
* Fix: link map settings to sort entries by group name when saving to fix order of entries
* Fix: diagnostic/3224, NullPointerException: MdProjectComponent$exportAllHtmlRaw

##### 2.8.4.30 - EAP Release

* Fix: [#741, Links], file without extension would sometimes not show up in completion or show
  as unresolved.
* Fix: diagnostic/3203, Throwable: Icon cannot be found in
  '/icons/application-flexmark-extension.svg'
* Add: missing svg application icons

##### 2.8.4.28 - EAP Release

* Fix: html to markdown converter set default to wrapped auto-links
* Fix: header up action disabled if text is inline element (bold, italic, etc.)
* Fix: diagnostic/3147 Throwable: Invalid file: MdProjectComponent.reparseMarkdown
* Fix: diagnostic/3144 caused by conversion of empty ATX to setext
* Fix: [#747, Useless "Markdown Export On Settings Change" notifications]
* Fix: [#746, Plugin creates files under .idea with default settings], removed
  `markdown-navigator.xml` and `markdown-navigator/` if defaults are used.
* Fix: diagnostic/3117, ClassCastException: cannot be cast to MdLinkElement

##### 2.8.4.26 - EAP Release

* Fix:
  [#745, Exception from AppUtils.isAppVersionGreaterThan when running on development build of IntelliJ IDEA]
* Fix: diagnostic/3095, StringIndexOutOfBoundsException: EMPTY subSequence,
  TableRow.fillMissingColumns
* Fix: diagnostic/3079, Java 11 - Flexmark parser exception: Illegal repetition near index
* Fix: remove trailing EOL from html to markdown converted text for new HTML parser code.
* Add: code-stream sponsor banner

##### 2.8.4.24 - EAP Release

* Fix: possible fix for [#743, auto-move in preview to match source code cursor position]
* Fix: update flexmark-java 0.50.2
* Fix: diagnostic/3062, NPE Json write
  * Fix: `boxed-json` add deep `null` to `JsonValue.NULL` replacement for `MutableJsArray` and
    `MutableJsObject` `toString()` calls to eliminate null pointer exceptions on `toString()`.

##### 2.8.4.22 - EAP Release

* Fix: diagnostic/3038, NPE: MdEditorKit MarkdownImageView.getImageURL
* Fix: diagnostic/3037, assertion error
* Fix: copy/modify image intention to work with base64 encoded images
* Fix: update flexmark-java 0.50.0

##### 2.8.4.20 - EAP Release

* Fix: copy/modify image intention to work with base64 encoded images
* Fix: diagnostic/3023, NoClassDefFoundError: sun/misc/BASE64Encoder
* Fix: add block quote action toggled existing block quote marker instead of adding one.
  * Fix: add/remove block quote actions for better control where block quote is added/removed
    based on caret position.
* Fix: add whitespace parsing to external annotator per lexer processing
* Fix: leave individual text parts of a paragraph separate to allow block quote and aside block
  white space prefixes to be syntax highlighted.

##### 2.8.4.18 - EAP Release

* Fix: regression [#727, css files not created in export folder]
* Fix: for duplicate heading ids and id attributes, remove navigate to element at caret

##### 2.8.4.16 - EAP Release

* Add: filter out links consisting of only URI/URL prefixes from line markers
* Fix: line marker provider to filter out plain auto-links not wrapped in `<>`
* Add: on Windows OS use of `file://` URI prefix instead of `file:/`
* Fix: diagnostic/2966, IllegalArgumentException: path must be canonical but got: '//..'
* Fix: diagnostic/2976, Must not modify PSI inside save: Update TOC on Save
* Fix: minimum build version changed to `173.2463` corresponding to 2017.3
* Fix: [#737, "Hard-break" Trailing Spaces Removed on Manual Save]
* Add: diagnostic/2952 to fixed list: Preview to use immutable document char sequence

##### 2.8.4.14 - EAP Release

* Fix: diagnostic/2951, Illegal group reference, ChangeTextToAutoLinkIntention.visitNode
* Fix: diagnostic/2949, IllegalStateException: frame must not be null
* Fix: diagnostic/2945, IndexOutOfBoundsException: `MdOnSaveFileUpdater$updateToc`
* Fix: plantUML (puml language) parser settings change is not detected

##### 2.8.4.12 - EAP Release

* Add: Diagnostic report tracking description for identified reports.
* Fix: diagnostic/2940, project is already disposed

##### 2.8.4.10 - EAP Release

* Add: Diagnostic report tracking.
* Fix: possible fix for diagnostic/2876, NoClassDefFoundError: Could not initialize class
  icons.MdIcons
* Fix: diagnostic/2374, Assertion: TableContext.getTable(TableContext.kt)
* Fix: diagnostic/2879, ClassCastException: PsiPlainTextFileImpl cannot be cast to PsiDirectory
* Fix: possible fix for diagnostic/2830, KNPE MdPlugin$Companion.getProjectComponent
* Fix: possible fix for diagnostic/2883, IllegalStateException: cannot open system clipboard

##### 2.8.4.6 - EAP Release

* Fix: add `!include` parsing for plantUML diagrams and invalidate generated image if include
  file content changes
* Fix: diagnostic/2896, NPE Json write of editor state
* Fix: diagnostic/2423, parser exception, `AutolinkNodePostProcessor`
* Fix: diagnostic/2612, SubSequence assertion error. Use Document `immutableCharSequence`
  instead of `charSequence` which can change between parse tree generation and rendering.
* Fix: diagnostic/2773, Wrong line separators
* Fix: diagnostic/2889, NPE `TableParagraphPreProcessor.cleanUpInlinedSeparators`
* Fix: [#733, PlantUML include path]
* Fix: Upgrade to `plantuml.asl.1.2019.4`
* Add: `resources/search/searchableOptions.xml` for full text search across all configuration
  settings.
* Fix: 2017.3 loads .svg icons if available even when .png icon is requested, while its .svg
  loader results in grey scale icons resulting in mostly black icons.
  * Fix: put svg and png icons into separate directories to prevent png load request loading svg
    files.
* Fix: diagnostic/2846, Empty PSI elements
* Fix: diagnostic/2858, Project Already Disposed: JavaFxHtmlPanel

### 2.8.4 - Bug Fix & Enhancement Release

* Fix: indent/unindent ordered list items with FIXED 4 list indentation parser setting
* Fix: single spaced list item remove on ENTER deletes the line not just list item prefix
* Fix: pasting markdown with link information would not move caret to end of pasted text
* Add: compound enumerated references for implementing legal numbering
* Add: enumerated reference text without element id in headings to use for heading numbering
* Fix: typing enumerated reference or reference definition deletes the prefix.
* Add: Inspection for list items which are indented to other list items but not enough to be a
  sub-item.
* Add: annotator config for reference elements
* Fix: move inspections for reference elements back to annotator to allow file in project view
  to show errors
* Fix: [#731, apache pdfbox vulnerability], update to flexmark-java 0.40.26
* Fix: [#713, Autocomplete links as "repository relative" by default]
  * Add: Languages & Frameworks > Markdown > Editor option `Default link completion format`,
    sets the starting format for link completions. Use multiple invocation to cycle to other
    formats.
* Fix: resolve link on click for optimized rendering which does not resolve links for preview
  HTML until they are clicked.
* Add: editor hint when document formatting is disabled with soft wraps on.
* Add: to Stylesheet `Apply CSS from URI` browse button, validation and error message when
  invalid URI is given.
* Add: [#705, Show navigable headings as file members inside project view]
* Add: annotation to detect and correct the processor updated `[TOC]` element to proper
  simulated toc syntax `[TOC]: #`, updated by plugin and not processor dependent.
* Fix: [#706, URLs with a fragment identifier are treated as syntax errors],
  * GitHub line selection anchors use permissive GitHub format to enable quick fix corrections.
* Add: Annotation settings panel
  * Fix: [#712, Target file is not on github annotation]
  * Links
  * Link Anchors
  * Table of Contents
  * Enumerated References
* Fix: [#730, Can not configure toc anymore], NPE on get rendering profile with project = null.
* Fix: [#729, Can't disable 'Add query suffix to URI which increments when file changes' option]
* Fix: disable paste post processing when multi-caret paste is used. Messes up pasted text.
* Fix: remove underline from auto-link markers in custom scheme.
* Fix: [#728, Loading indicator in settings doesn't disappear after loading]
* Fix: [#727, css files not created in export folder]
  * Fix: if css/js dir is blank or empty in settings then default to css/js sub-directory in
    target dir
* Fix: [#726, unable to show ref anchor link explorer]
* Fix: markdown file move result of package move with no sub-directories and only markdown files
  affected would not adjust links.
* Fix: [#721, EAP: QuickFix for "Wrap Links" shows the message-key instead of description]
* Fix: disable wrapping when typing `<<<` at beginning of line to allow typing macro closing
  marker.
* Fix: Cut/Paste on markdown text results in all contained elements becoming invalid.
  * Implemented Psi independent format for reference information
  * Add: abbreviation and enumerated references to copied elements if they are referenced but
    not part of the copied text
  * Add: copy markdown to clipboard to share link/reference information to allow Markdown
    Navigator running in another IDE instance to make use of appended references and links
* Fix: copying/pasting image links from wiki to main repo file messes up the link `../` count
  and creates a wrong link.
* Fix: absolute http:// links from main repo files to wiki pages were not recognized as wiki
  pages of the project
* Add: clear link cache when any repository is pushed so any remote links which become valid are
  updated.
* Add: Notification settings panel to enable/disable notification messages
* Fix: Some notification disable links did not work or disabled the wrong message
* Fix: ref links and images would generate multiple line markers if the ref contained
  typographic or escaped characters
* Fix: do not show Yaml Front matter notification if flexmark front matter parser extension is
  enabled.
* Fix: flexmark-example option `NO_FILE_EOL` shows up as an unknown option icon in structure
  view.
* Add: **plain text completions** for completing references and headings defined in the file and
  references/links in the project.
  * Add: documentation about including too many files in the scope. Only the first 1000
    suggestions will be collected. Too many files may cause some suggestions not to be shown.
  * Fix: plain text completions don't work in table cells
  * Add: current file's headers to link ref completions.
  * Editor settings option to control plain text auto-popup, disabled by default. Will make
    default false when can disable completions on typing space for plain text completions.
  * plain text search scope defined in rendering settings: `Plain text suggestion search
    scope:`, default is taken as `Project Files` which is too wide to be useful.
  * plain text auto popup to show only if prefix is 3 or more alphabetic characters, and not to
    complete on typed character, only tab or enter
  * disable automatic completion on char typed if auto-popped-up.
  * link anchor completion
    * Fix: replacement end to be end of link ref caused by plain text adjusting
      `replacementOffset`.
* Add: Document the fact that auto-links extension is not usable for very large documents and
  introduces significant typing delays.
* Fix: copy without line breaks of full document text with references, adds duplicate references
  if they are used and included in the partial copy selection.
* Fix: [#720, Indentation in a numbered list 'flickers' when typing and deleting text], caused
  by BACKSPACE ignoring the list indentation type of "Fixed 4 spaces"
* Fix: ENTER insert hard-break to not insert spaces if caret is at first column or preceded by
  all spaces.
* Fix: HTML to Markdown on paste adding extra line breaks if `div` is used to wrap text for `li`
  tags.
* Add: inspection for links whose link text does not match the heading text referenced by the
  link anchor ref, quick fix to change/add link text, remove text use ref id or remove
  text/rename ref id.
* Fix: FakeURLPsiElement could cause not awt thread exception in 2019.2 EAP.
* Fix: inserting characters before or between block quote markers does not work. Now disable
  wrap when typing in prefix with block quote or aside markers
* Fix: disable IDE caused paste handling errors and divert them to log as info, having had
  enough of these spurious bug reports.
* Fix: line marker for reference links/images but navigate to the destination as if it was an
  explicit link and not reference link.
* Fix: [#719, Wrap links in <> Do Not Show does not hide the inspection], same for Unicode line
  separator inspection.
* Fix: list item's second paragraph children get the item prefix, converting them to items.
  Introduced by a fix for the culprit below.
  * Fix: List item whose first child is block quote, on format/wrap on typing looses its item
    prefixes and becomes indented text.
* Add: HTML paste option to convert links to references, add pasted link types:
  * Add: document information so existing reference definitions can be re-used instead of
    creating duplicates.
  * None - skip element generating no text
  * Text - just text or alt text
  * Explicit - explicit link/image
  * Reference - reference link/image with reference definition
  * HTML - paste HTML as is
* Add: Intention on auto-link looking text to Wrap in `<>` so `Auto links` parser option could
  be disabled and still get annotations for auto-link text.
* Fix: [#708, Empty element in simplified structure view when using just url as header text],
  now falls back to using the heading as is if plain text gathering results in empty text
* Add: task list option to simple structure view to display task list items under the heading
  section with filter options:
  * Add: structure view type combo in settings:
    * simple - headings only
    * simple tasks - headings and tasks
      * only headings with tasks
      * to show completed tasks,
      * incomplete tasks,
      * empty item tasks (either complete or incomplete)
    * detailed - all elements and sections
  * Fix: Badge filter should filter out any headings without tasks
  * Fix: empty tasks should not add to count but should affect the badge color and badge display
  * Fix: navigation from editor to list item should be based on position at the end of the line
    but for empty items on the last non-blank of the line.
* **Link text completions**
  * disable automatic completion on char typed if auto-popped-up.
  * remove query string from link text suggestions
* **emoji completions**
  * only auto popup if lowercase or _ is preceding the caret.
  * work properly and in all elements
  * ENTER completion of emoji shortcut in heading adds `:` and tail of heading line
* Add: `Not Sorted, delete unused` and `Sorted, delete unused` options to all applicable
  elements to remove unused references during document format.
* Fix: formatting list sort by task completion status broken in 2.8.0/2.8.2
* Fix: [#702, Setext header marker equalization on ENTER broken]
* Fix: block quote and list items should not be inserted while in multi-line non-wrapping inline
  elements like inline math and multi-line url image
* Fix: Do not allow wrapping of inline math. Otherwise wrapping on `\` without a following space
  creates issues with rendering. For now math inline elements are treated like explicit links
  and images. If they do not fit on a line, they are moved to a line by themselves.
* Fix: visitor base impl was not visiting children of inline elements
* Fix: ENTER after empty task item inserts extra blank line
* Add: disable indent on paste for markdown documents
* Fix: Lexer based syntax highlighter to work correctly for restarted lexing. This is still slow
  because re-parse can only be done starting with file level items. Annotator is still be best
  choice for markdown syntax highlighting.
* Add: GitLab inline math elements to distinguish them from inline code.
* Fix: reverse the lexer/annotator editor swapping. Start all files with lexer, then switch
  write enabled ones to annotator. This way syntax highlighting gets performed immediately but
  the greatest benefit is to have Markdown syntax highlighting in injected language elements
  which use lexer syntax highlighting.
* Fix: Remove swap readonly editor highlighter option it is no longer used.
* Fix: re-parse markdown editors when annotator type is changed.
* Add: GitLab inline math attributes to distinguish from inline code
* Add: Task item complete text attribute
* Fix: GitHub file line selection anchors not to validate except for form: `L#-L#`.
* Fix: GitLab file line selection anchors not to validate except for form: `L#-#`.
* Add: Anchor quick fix to convert between GitHub and GitLab line selection anchors when the
  wrong form is used. ie. GitHub with GitLab form and vice versa.
* Add: Editor option Annotator: `Ignore fenced code content`
* Fix: unresolved link annotation shows up 2x in inspection results
* Fix: https:// link to issues of another module in a project showing as unresolved
* Add: option to validation inspections to ignore elements in fenced code, ignored by default.
* Add: apply to all in file intention to `Delete unused reference`
* Fix: rename file quick fixes failing with illegal file name.
* Add: error annotations to error highlight project view files
* Add: Inspection for Unicode
  ['LINE SEPARATOR' (U+2028)](https://www.fileformat.info/info/unicode/char/2028/index.htm)
  which is used to force line separator which will not wrap.
* Fix: doubling of TODO items and spelling errors, was caused by having comment nodes in HTML
  blocks which had HTML language injected. Causing comments to be processed twice: once for each
  language.
* Fix: Enable in-place renaming of all elements where possible.
* Fix: file move refactoring to handle IDE and refactoring listener both calling move handler
  for the same file. Happens when directories are moved. This was causing unadjusted link
  because the IDE called the prepMove **after** the file was already moved, useless because all
  the relative links are now not guaranteed to work. Now works because IDE gets results computed
  for refactoring listener before file is moved.
* Fix: line markers for ref links and images don't show up in gutter, broken by element type
  cleanup.
* Fix: node class refactoring for list and list item left some code testing for unordered list
  item when generic list item was intended.
  * Renumber list items in formatting always resets the first item to 1. even when that option
    is not selected.
  * BACKSPACE gets rid off the ordered item prefix when editing any non-first list item, works
    fine if `Backspace removes empty item setting` is off.
* Fix: completed task items on ordered items get converted to simple ordered items on ENTER
  insertion of another item.
* Add: Splitting a paragraph in the middle (not list item) should automatically add hard break
  spaces at the end of the old line so it is not wrapped.
  * Add: smart key option for ENTER to add hard-break spaces when splitting a line
* Fix: Add awareness to formatter for `<!-- IGNORE PREVIOUS -->` comment being associated with a
  reference and move it with the reference when formatting.
* Fix: loose list with empty item not recognized as double spaced
* Fix: ENTER on empty task list item inserts blank line after deleting task item prefix
* Fix: typing before indented paragraph swallows characters instead of inserting them before the
  paragraph.
* Fix: typing right at first character of indented text doubles the typed character. Cannot
  reproduce.
* Fix: weird behavior when ENTER hit at end of bullet item with ordered item children. Get
  spliced new first item `1. X` followed by old item `2.` on the same line. Cannot reproduce.
* Fix: insert line item on ENTER **after the end of the item text** should insert child item if
  original item has children. ie. as if insert before was done on first child item
  * Fix: inserting when the first child is an empty item removes the child and inserts blank
    lines.
* Fix: ENTER when inserting new task item above the current one should clear the task done
  marker in the new task item and leave the done marker for the old item. Right now it does the
  reverse.
* Fix: task item prefix was not handled on ordered list items for insert/renumber
* Fix: insert after end of ordered list item with child task item splits the next line in two
  but does not add prefix
* Fix: in fixed indent mode BACKSPACE indents ignoring fixed indent config.
* Fix: reference sort to be done on normalized id not actual id
* Fix: [#715, Completed task list items not rendered with checkmark]
* Fix: `AUTOLINKS` extension is now an option for annotation syntax highlighter. Previously, it
  was always enabled regardless of parser settings.
* Fix: inline reference intention only appears on the reference part of `[reference
  text][reference]`, it should appear for the whole element.
* Add: Intention to match ref link case to reference id case when the two don't match, with All
  in file intention option
* Fix: remove separate line icon for link and anchor. Only add combined navigate to header.
* Add: Intention on selected text or word to add Abbreviation if this parser option is selected.
* Add: File inspection for large files with auto-links parser option enabled.
* Add: convert all auto-links to <http://> which work without needing to parse all text for
  possible link.
* Add: Inspection to find possible unwrapped auto-links in text and convert them to `<>` wrapped
  auto-links
* Add: intention to remove ref link text when it matches the id, also offer to do the same after
  refactoring the ref id and it matches text
* Add: to line marker tooltip the element's identifying characteristics so can see which element
  from the line is being referred to by the line icon.
* Fix: initialization issues in paste options form and dialog
* Add: HTML options from editor settings to show disabled paste plain text and paste html
  buttons.
* Add: dropped link insert as option: `Link`, `Ref Link & Reference`, `Reference Only`
* Fix: remote content first heading makes no sense, use title with fallback to first heading
* Fix: [#704, NoClassDefFoundError on 2019.1 EAP with JBRE], JRE 11 support
* Fix: diagnostic/2477, Desktop API not supported on the current platform
* Fix: [#710, Memory leak in HTML preview window], swing HTML preview had a memory leak.
* Add: [#709, New Icon looks bad on dark background], option to use old document icons.
* Fix: ignore rendering profiles with no name. Do not know how it occurred but when it did it
  was incomplete and caused headaches to figure out why the plugin was screwing up parser
  settings.
* Fix: HTML Export with `Use style attribute` not to add `<meta http-equiv="content-type"
  content="text/html; charset=UTF-8">`
* Fix: diagnostic/2335, `java.lang.NoSuchMethodError: com.intellij.util.KotlinUtils.`, must have
  been a new addition to the library.
* Fix: diagnostic/2348, `Parameter specified as non-null is null: method` for drag/drop files
* Fix: diagnostic/2344 illegal access `EditorWindow.INITIAL_INDEX_KEY`, static field was
  temporarily made package private between 2016/10 and 2018/06 in the API.
* Fix: [#711, Editor -> Toggle Editor Layout setting is not saved.]
* Fix: [diagnostic/2556](http://vladsch.com/admin/diagnostics/2556) Index out of Bounds.
* Fix: Toggle task item done does not work for ordered task list items

### 2.8.2 - Bug Fix & Enhancement Release

* Fix: icon sizes for non-hdpi displays
* Add: option to insert references instead of links, if there is no name conflict, no dialog is
  brought up and the reference is simply inserted into the text.
* Fix: backspace on a blank line after the first bullet list item text deletes list item prefix.
* Add: All in file intentions for image <--> code convesions:
  * codecogs math to multi-line url image
  * gravizo uml to multi-line url image
  * image to fenced code math
  * image to inline math
  * image to plant uml fenced code
* Add: Condition to codecogs image to fenced code intention to only do conversions if the image
  is the only element in a paragraph and the reverse condition to convert to inline math. This
  way apply to all will only convert elements which can be converted back to images without
  changing the file layout.

### 2.8.0 - Bug Fix & Enhancement Release

* Fix: update for `flexmark-java` 0.40.18
* Fix: HTML Options dialog for editor kit used for swing preview. Enables rendering of svg
  images if these are contained in pasted HTML.
* Add: suppress `math` tag or convert to text in HTML Past options dialog
* Add: drag/drop text from applications into page to insert it, if mime content is
  `text/uri-list` then create a link, if `text/html` then handle like html paste
* Fix: line marker icons for darcula
* Add: definition list and definition terms added to structure view
* Add: markdown navigator overlay to wiki directory
* Fix: change wiki link to explicit link when containing file is not a wiki page and wiki link
  is not resolved.
* Fix: wiki links were not part of processed links for copy/paste
* Add: wiki link to explicit link conversion on paste if original does not resolve
* Fix: [#695, Click on structure view does not update preview]. If highlight mode was not
  `Line`.
* Fix: [#697, Autoscroll from source does not work in simplified structure view]
* Fix: atx<-->setext not add blank line before if contained as first element in parent item: ie.
  `* ## Text Heading In List Item`
* Fix: indenting/unindent messed up with all the list editing changes.
* Fix: footnotes did not format correctly if they contained anything other than a single
  paragraph of text.
* Fix: definition extension parser caused list items to require 1 extra space for child content.
* Fix: parent prefix handling for prefix marker item elements when there first element is not
  text and the first block element is possibly on the next line:
  * footnotes
  * list item
  * definitions
* Add: Inspection for missing macro and footnote definitions. The were missing since missing
  link ref definitions were moved to inspections from combined error annotations.
* Add: allow editing (type/backspace) first ordered list item and update the rest of the items
  to reflect first item change if list item renumbering is enabled and style not set to always
  reset first list item.
* Fix: deleting first ordered list item would renumber subsequent items as if first item was
  there.
* Fix: backspace not to delete list item prefix unless removing the character makes it no longer
  a list item.
* Fix: any renumbering of list items required indentation change of child elements otherwise
  they could fall out of parent indent scope. ie. `9. text` changing to `10. text` requires
  adding 1 space to child indents or they would no longer be under the parent, according to
  CommonMark.
* Fix: indent/unindent list item messed up if had empty list item ancestor
* Fix: double marker inserted when typing first `>` or `|` after list item prefix
* Fix: copy/paste between files and between projects to properly adjust link path/format
* Fix: paste/drop image adding numeric sequence would remove all characters after the first `.`
* Fix: computation of paste/drop image file, directory priority which caused image directories
  with most images to override most used image directory in current file.
* Fix: link to file with an extension not registered to a specific file type in the IDE would
  show as unresolved.
* Fix: changed dropped file links now spaced by blank line in blocks of 20 files to eliminate
  creating very long paragraphs when many files are dropped. Was causing update to be very slow
  if a lot of files were dropped. 1000 hung the IDE.
* Fix: wrap on typing is not invoked if typing at left edge of paragraph and what could be start
  of an element prefix:
  * `-` or `=` : setext heading marker
  * ordered list item `\\d+[.)] `
  * bullet list item `[+*-] `
  * definition item `[:] `
  * block quote and aside markers are handled by inserting these into the text
* Fix: improve link resolution for markdown and image files. 5000 links was 7 sec, now 4.5
  seconds.
* Fix: slow reference resolution performance for large documents with many reference elements
  checked for unused state. 5k line file with 100 references took 35 seconds to format, then 14
  more seconds to annotate. Now takes less 2 seconds for both operations.
* Fix: escaping of possible item marker characters when splitting a list item to prevent text
  from being interpreted as a markdown element after inserting EOL. ie. `* item |1. text`, split
  after `|` would interpret as an ordered list item. Now the `1.` is changed to `1\.`
* Fix: aside block to behave the same as block quotes for formatting, wrap on typing, CSS
  layout, etc.
* Fix: list in block quote trailing item renumbering would not be applied for CommonMark list
  parsing rules
* Fix: block quote ENTER handling to happen before list item ENTER if the block quote is the
  last prefix marker for the element then block quote ENTER has priority.
* Fix: task item markers are dropped from list item prefixes with new implementation
* Fix: typing prefix/spaces before text now allows pushing left text edge of paragraph
* Fix: list manipulation and wrap on typing for complex nested lists/block quotes, with nested
  empty list items
* Fix: format of nested empty list items duplicated list item marker
* Fix: nested empty list items would duplicate all item markers on ENTER
* Fix: typing space at start of text item would not indent paragraph
* Fix: BACKSPACE in empty item with immediate child block item would delete non-list item prefix
  of the child item (as if it was task item marker)
* Fix: parser combined markers of consecutive empty list items into single leaf element
* Fix: [#690, Link title should not be selected when pressing space], disable auto-popup
  completion for link text.
* Add: Editor setting `Show page content as documentation for URLs`, when enabled will show
  documentation (F1 by default) will load the URL content and show it as the documentation for
  the link/URL
* Fix: ref link referenceId did not trim spaces inside would not resolve to reference because
  reference trimmed spaces.
* Add: `Change link to text` intention to Reference Links
* Fix: missing emoji cheat sheet `simple_smile` shortcut.
* Add: quick documentation popup for links display the full path or URL for the link. Navigation
  for these links will navigate to file if target is part of the project or URL via external
  browser
* GitHub issue related:
  * Fix: GitHub issue completion start with showing all issues, ^Space cycles -> open only ->
    reload tasks from server -> all issues -> open only -> all issues -> ...
  * Add: Editor settings `GitHub issue completions insert` option to make GitHub Issue
    completion insert: text, explicit link or ref link. Default is text, if set to link then can
    use `Change link to text` intention to get text or set option to text.
  * Add: Editor settings `Force reload max issues:` to give max issues to load from server when
    forcing reload through triple completion invocation of GitHub completions.
  * Add: for URL links to GitHub issues/pull will fetch page when documentation is requested on
    the link (F1 key) and show comments for the issue.
  * Fix: link text completion for links to a GitHub issue/pull now add the issue completion to
    list of completion strings, if the target repository is configured as a Task server.
  * Fix: page relative links to GitHub links like `issues` navigate to GitHub URL for the link.
  * Fix: GitHub issue completions in text to remove text to EOL (less line break spaces) if
    using TAB completion.
  * Fix: GitHub issue completions to escape special characters in inserted summary
  * Fix: GitHub issue completion in link text element
  * Fix: GitHub issue completions in links if the url is referencing issues/ and Task server
    exists for the given repo URL
  * Fix: GitHub issue completions for page relative issues/ URL only if there is Task server for
    GitHub repo given by the VCS root for the containing file.
* Fix: setext heading marker equalization for multi-line setext headings now equalizes marker to
  length of last line.
* Fix: URL for generated PlantUML image files on Windows
* Fix: escape possible leading markdown element markers when converting from Atx to Setext
  headings, with unescaping for the reverse conversion. Otherwise, a valid Atx heading can be
  converted to an invalid setext but confusingly looking like it should be valid. For example
  `## 1. Heading` without escaping will be converted to ordered list `1. Heading` item with
  `--------` lazy continuation and not a setext heading marker.
* Add: header text escaping/unescaping to formatter induced header conversions
* Fix: package rename to sub-package did not adjust links to subdirectories of original package.
  ie. rename package `abc` to `abc.xyz` would not refactor links for `abc/def` to `abc/xyz/def`.
* Add: Create PDF Export Profile button to Rendering > Profiles
* Fix: Rendering settings would reset PDF export profile to default if Rendering > Profiles were
  modified.
* Fix: #533, export to pdf error (when Chinese in content). Requires adding CSS embedded font.
  Instructions added to Wiki
  [Rendering-Profiles-Settings](https://github.com/vsch/idea-multimarkdown/wiki/Rendering-Profiles-Settings).
* Fix: #578, Change setext to atx header fails if title starts with \<number>.
  * Fix: equalize setext header marker implementation to properly handle parent indent
  * Fix: toggle header type to properly indent setext header marker based on parent element
  * Fix: setext header level up/down to properly handle parent indent
* Fix: #595, PDF Generation Silently Fails when PDF file locked
* Fix: do not wrap when typing space after last non-blank of the line if the line does not need
  wrapping, to allow typing hard break at end of line.
* Fix: #624, emoji suggestions do not filter as you type, when emoji shortcut not terminated by
  `:`
* Fix: for case-insensitive completions the IDE changes the case of inserted text to match typed
  text.
* Add: drag/drop & file paste option to use first heading in markdown file for link text
* Fix: remove case sensitivity from link address and link text suggestions
* Add: type information and priority for some link text suggestions to improve their placement
* Add: link text suggestions
  * based on file name and first heading in the file if it is Markdown
  * first heading: anchor ref heading based link text
* Fix: with wrap on all characters, do not wrap when typing or backspacing setext heading marker
  line
* Fix: diagnostic/2273, index out of bounds exception
* Add: logic in wrap on typing to disable wrapping if caret is at left text edge and typing a
  special char (`*`, `-`, `+`, `>`, `:`) or space after a special character to prevent
  potentially new element being typed from wrapping as plain text.
* Add: editor option to limit wrap on typing to only occur after typing a space, by default wrap
  on typing is now performed for all characters. Disable if wrap on typing causes unacceptable
  typing response.
* Add: intentions to convert between inline or block math elements and `latex.codecogs.com`
  multi-line image URL links.
* Add: html generation options for rendering math inline/block elements using
  `latex.codecogs.com`.
* Fix: if default link format for drag/drop is Wiki Link but wiki links are not available for
  the document then use page relative format.
* Fix: file move handler exception if only directory was moved without files.
* Fix: diagnostic/2245, IndexNotReadyException: Please change caller according to
  IndexNotReadyException documentation caused by pasting possible reference while indexing is in
  progress.
* Fix: ENTER would insert loose list item in tight list if code style setting for lists was
  `Loosen if has loose item`.
* Fix: do not highlight PlantUML images in preview as local only.
* Fix: index out of bounds when using TAB to skip trailing auto characters located at end of
  file not terminated by EOL.
* Fix: html generation embed remote images to handle SVG, gravizo and codecogs images.
* Fix: `Copy/Modify Image` to use gravizo.com SVG link for images with PNG link because Swing
  cannot load gravizo PNG images.
* Fix: `Copy/Modify Image` intention on multi-line image URL to remove URL content when
  replacing link address.
* Fix: #676, Text colouring is missing in commit file preview window
* Add: `Copy PlantUML Image intention` to copy resulting image to the clipboard
* Fix: PlantUML to gravizo.com image link intention method not found exception on Android Studio
* Fix: change PlantUML to gravizo.com image link intention to generate SVG for Swing and PNG for
  JavaFX so both display correctly by default.
* Fix: auto-link refactoring not to loose angle wrapping
* Add: `puml` and `plantuml` to fenced code language completion when corresponding parser
  PlantUML extension is enabled.
* Add: intentions to change between anchor ref HTML and Markdown attributes element
* Fix: plant uml fenced code rendering to work with HTML mime copy and export
* Add: border to images when focus-highlighted in preview
* Add: intentions to convert between multi-line URL Gravizo and PlantUML fenced code intentions
* Add: PlantUML/DOT rendering for `puml` and `plantuml` fenced code as options
  * Requires enabling parser extensions for `PlantUML` and `DOT` fenced code rendering
  * Selecting type of rendering in HTML Generation settings: `Embedded` using PlantUML jar,
    `Gravizo PNG` or `Gravizo SVG` using gravizo.com
* :warning: Swing does not display `Gravizo PNG`
* :warning: JavaFX may not display `Gravizo SVG` correctly
* Fix: language injections to default to plain-text if specific language is not found to allow
  editing fragment in separate editor
* Fix: image was not embedded if the image URI had query suffix added
* Fix: fenced code injected fragment editing when contained in parent with prefix.
* Add: multi-line image URL language injection
  * Add: hard-coded `gravizo.com` that starts with `@startuml` to default to `puml` language
    type if `PlantUML` plugin is installed. Otherwise, defaults to plain text.
* Fix: re-implement package/directory move/rename refactoring to make it compatible with Android
  Studio and RubyMine
* Fix: library bundled with IDEA missing from CLion
* Add: file drag/drop and image paste option to preserve existing link format. Applies when file
  is dropped on existing link or image pasted on existing link and `preserve existing format`
  option is selected in the corresponding options dialog.
* Fix: Java PsiClass and PsiPackage dependencies for non-java IDEs
* Fix: file move link adjustments caused by directory move of parent
* Fix: #672, Refactoring does not update reference to file after renaming package
  * Fix: find usages to find directory references in links
  * Fix: find usages to find package references in links
  * Fix: find usages to find java class file references in links
  * Fix: directory renaming to trigger link refactoring
  * Fix: package renaming to trigger link refactoring
  * Fix: java class renaming to trigger link refactoring
  * Fix: java class move to trigger link refactoring
* Add: missing elements to HTMLOptions simulated sample (when invoked from settings)
* Add: Change link to text intention
* Add: HTMLOption to suppress links (convert them to their plain text equivalent)
* Fix: NPE when trying to open HTMLOptions dialog from settings
* Fix: #605, allow uppercase letters in custom URI protocols
* Fix: table format as you type would loose leading table prefix spaces
* Fix: diagnostic/2216, trailing spaces filter index out of document text range
* Fix: restore lib apache commons-io 2.4, in PHP storm 2018.3 the library is missing.
* Fix: editor setting `Remove prefix when joining lines` was disabled in basic version and `Use
  rename for spelling error text` was enabled. Reverse of what is intended.
* Add: #668, Enhanced Edition Features migrating to Basic Edition
* Fix: make copy html mime exported to handle partial selections with reference transfer from
  full document.
* Fix: image file move link refactoring broken by multi-file move fix.
* Add: to `HTML Paste Option` dialog
  * `Paste Plain Text` button to allow ignoring HTML content for a particular paste operation.
  * `Suppress` option for `Fenced Code`. When enabled will use indented code instead of fenced
    code.
  * `Suppress` option for `Escaping Special Characters`. When enabled will not escape any
    special characters in converted markdown code.
  * Preview of text which would be pasted when mouse over the `Paste Plain Text` or `Paste HTML`
    buttons
* Fix: if reload affected editors is refused, then these files will not trigger reload editor
  prompt until reloaded by other files' prompt or reset to not needing reloading. Avoids
  multiple prompts when already refused.
* Fix: table parser to render table separator char (`|`) embedded in inline elements as text.
* Fix: list type toggle actions without selection would erroneously toggle child items
* Add: ENTER smart key option to insert block quote prefix when inserting line in block quote
* Fix: formatter would not strip block quote prefix if it was preceded by non-indenting spaces
* Fix: block quote prefix handling in complex nested markdown elements
* Fix: Setext marker equalization glitches during fast typing
* Add: Heading formatting option for preferred heading style:
  * No Change - leave all ATX and Setext headings as they are
  * ATX - change all Setext to ATX
  * Setext - change all ATX level 1 & 2 headings to Setext
* Fix: heading type changes to work with arbitrary parent prefixes
* Fix: Setext marker equalization as you type to work with arbitrary parent prefixes
* Add: `Heading level up` now changes Setext to ATX to allow increase of heading levels beyond 2
  available for setext headings.
* Fix: ATX to Setext heading change to add blank line before ATX heading if it is not there,
  otherwise preceding text is interpreted as part of the Setext heading
* Fix: change `Header` to `Heading` in localizations for consistency when referring to markdown
  heading elements
* Add: #528, `Header level up` should create H1 when there is no heading, also `Header level
  down` removes heading when level 1 heading.
* Add: `Format Markdown` to `Markdown Navigator` tools menu, with format element and format
  document actions
* Add: `Copy Exported as HTML Mime Content` Action to copy HTML export text as HTML mime content
  to allow customizing HTML Mime for other uses such as pasting it to websites which handle HTML
  paste conversion, like JetBrains forums, which do not need all the extra formatting provided
  by `Copy Markdown as HTML Mime Content`
* Add: create `COPY_HTML_MIME` rendering profile with a copy of the default template as css text
* Fix: drag/drop file after physical space mistakenly takes previous element. Insert actual
  spaces at drop point to avoid the issue.
* Fix: table as you type formatting broken by refactoring in version 2.7.0.14
* Add: plugin icon
* Fix: move file refactoring failing when non-markdown file is moved
* Fix: 2016.3 compatibility
* Add: svg image support to swing browser
* Add: debug option `Reinitialize editors on settings change` to allow turning off this feature.
  Affects reduction of icon gutter area when some gutter icons are disabled. Without
  reinitialization the editor gutter remains wider until the editor is closed or reinitialized
  by the IDE.
* Add: debug option `Reload open editors when the underlying file type changes` to allow turning
  off this feature.
* Add: debug option `Change editor highlighter to Lexer based for all read-only editors` to
  allow turning off this feature.
* Fix: add `editorReleased` method to listener for 2018.2 and earlier compatibility
* Add: for readonly editors for Markdown, reset highlighter to lexer after editor creation since
  annotator never runs for these files.
* Fix: adding a list item with ENTER with first list item start > 1 would insert the wrong item
  number.
* Add: when list items are inserted with ENTER and list code style spacing is set to `No Change`
  will use the loose status of the current list item to insert loose/tight list item. This means
  that if a blank line follows a list item then inserted list item will also have blank lines
  around it.
* Add: selecting lines in the file should allow adding block quotes, block quote level will now
  allow selecting arbitrary lines in the file and will add block quote prefix to elements which
  span the selected lines. Adding block quotes will not split an element into two parts so all
  lines of the element will be prefixed. For example, selecting a part of a list's items will
  prefix all items in the list. Similarly, selecting some lines of a paragraph will prefix all
  lines of the paragraph.
* Add: #663, Convert code block type from indent to triple-quoted
* Fix: file move refactoring did not preserve self reference link file name if it was used in
  the link and always optimized it to `#`
* Fix: file move refactoring conversion of wiki links to explicit links would not preserve link
  text of wiki link if only page ref was given
* Fix: file move refactoring was not preserving `file://` addressing format of links
* Fix: file move refactoring removing link address for self referencing page relative link
  without an anchor. should add empty anchor `#` to make the link valid.
* Fix: multi-file move refactoring does not update page relative links in files being moved to
  other files being moved.
* Fix: `file://` addressing format to wiki pages with file ext should not warn of raw vs
  rendered markdown
* Fix: un-indenting task item would change child items to siblings
* Fix: wrong caret offset when indenting empty list item with caret located in trailing spaces
* Fix: for smart edit asterisk, underscore and tilde add removal of one with mirror on
  backspace.
* Add: tab option for skipping smart edit enabled character sequences.
* Add: Smart Edit back ticks option
* Add: #641, creating list from selection, as a special case if the selection contains a single
  paragraph then all lines in the paragraph will be converted to list items, toggling list item
  markers again will convert them back to a block of lines. Removal of list item prefixes will
  convert a block of selected list items which consist of a single line of text and no
  intervening blank lines to a block of text. In all other cases the selected elements will be
  converted to list item per paragraph and removal of list item prefixes will add blank lines
  between resulting paragraphs.
* Add: #633, Invoke renumbering ordered lists, option to formatter to reset first list item.
  Addressed by fix to formatter.
* Fix: formatting always reset the first ordered list item to 1. Now it is a code style option.
* Update code style settings wiki
* Update application settings wiki
* Add: reopening of editors when settings or associated file type changes for plugin supported
  file types to eliminate user confusion by the file not reflecting their expectations.
* Add: resetting of gutter size when settings remove some line marker icons from showing up.
* Change: code style settings' smart keys to `Editor` settings pane, under Languages &
  Frameworks > Markdown. These settings are not migrated because they are moving from per
  project to application settings.
  * backspace remove empty list item
  * enter remove list item
  * enter add list item
  * table delete empty columns
  * table delete empty rows
  * table insert new rows
  * smart edit asterisks
  * smart edit tildes
  * smart edit underscores
* Change: move application settings to separate `Editor` settings pane, under Languages &
  Frameworks > Markdown.
* Change: move markdown code style settings to separate tabs instead of a single tab with 100
  foot scroll of options.
* Fix: backspace in empty table cell did not move caret left.
* Add: enter with caret at leading pipe of table now inserts row above.
* Fix: enter on table caption line inserted table row
* Add: table navigation for:
  * header/body/caption with selection option
  * separator stops at start and end of cell to allow editing of alignment 7* Fix: table
    manipulation/navigation with format as you type to eliminate caret position idiosyncrasies.
* Fix: Rewrite table format as you type code to reduce typing lag for wide tables (120+
  characters wide). Now can comfortably type with 200+ line by 120 character tables with table
  format as you type enabled.
* Fix: paste image highlight showing selection ring when saving changes to clipboard
* Fix: back tab changes indent to wrong prefix, causing to mess up the list
* Fix: list item indent/un-indent did not adjust for change in ordered list prefix size when
  adjusting caret position
* Fix: back-tab override was not invoked consistently
* Fix: tab override or list indent on empty (only space after marker) with sub-items, missing
  EOL after item, causing sub-items to be joined to parent item as indented code.
* Fix: fenced code has extra blank line in preview but looks fine in HTML text.
* Fix: mixedColor now is too dark for script table in style sheet settings.
* Add: oval and circle shape types to highlights in paste/modify image dialog
* Change: factor out util library to plugin-util
* Fix: copy fixed utils from Arduino Support plugin.
* Add: multiple highlights editing in paste/modify image dialog
* Fix: drag/drop file after end of file causes exception. Most likely will be fixed by above.
* Fix: pasting an image and selecting non-existent directory path would not save image but only
  create directories.
* Add: `Copy markdown without soft line breaks` copies document or selection to the clipboard
  eliminating soft line breaks. Useful when copying wrapped markdown to GitHub comments.
* Add: All copy markdown actions: `CopyHtmlMimeFormattedAction`, `CopyJiraFormattedAction`,
  `CopyNoSoftLineBreaksAction` and `CopyYouTrackFormattedAction`, now include all reference
  defining elements which were referenced from the copied part. All links and other elements
  resolve without needing to include their reference definitions.
* Fix: add missing handling of `abbr` in html mime formatted copy
* Fix: diagnostic/2119, Double cannot be cast to Integer exception
* Fix: When cropping using selection if crop is not enabled, then select it on mouse release.
* Fix: nasty bug introducing typing delay with preview enabled.
* Fix: optimize link resolution to for HTML rendering by a factor of 5+ for image and markdown
  links. Now can handle 100+ links in the same time it used to take to resolve 20 links.
* Fix: Preview option replace emoji unicode with emoji image would take a whopping 180ms to
  process 90k rendered HTML with no emoji in the text. So much for RegEx speed. Hand rolled code
  does it in 0.5 ms. Even if the file contains all available emoji characters it is still under
  1 ms to process.
* Fix: diagnostic-2012, kotlin NPE.
* Fix: Paste Image: old crop settings out of bounds for new image caused exception
* Fix: for #651, Drop image with dialog issues
  * Spaces in file name were url encoded
  * Copy dragging a file leaves its original directory instead of setting it to the closest or
    best guess based on the destination file. Should be the same as if the image was pasted into
    the file. If the destination directory is the same as the source then a new name should be
    generated to avoid overwriting it.
* Add: in Paste/Modify Image if dragging the highlight selection without having highlight
  enabled or no border, inner nor outer fill enabled, will enable highlight and border to
  provide feedback otherwise it is confusing.
  * Add: drag selection can be used for cropping if image tab is selected and `Use mouse
    selection only for highlight` is not selected.
  * Fix: only copy image to transparent if Image tab is selected. The rest leave as is.
  * Add: restart notification if changing full highlight combinations
* Add: Image Paste highlight option to annotate an area of the image.
* Add: option to disable synthetic highlight attributes.
  * Fix: #648, too many element types registered, Option for full syntax highlighter
    combinations, disabling creates minimal set to reduce the limit of short index for these in
    the IDE.
* Add: Code Style option to treat `Hard Wraps` parser option as if soft-wraps are enabled.
* Add: Main option to force soft-wraps mode for file when opening if `Hard Wraps` are enabled

### 2.7.0 - Bug Fix & Enhancement Release

* Fix: jekyll parser option notification would not use the file's scope based profile.
* Fix: bump up dependencies to newer versions
* Fix: #647, md to html link conversion not working for exported files on Windows
* Fix: exported files without stylesheet should not decorate link with resolved status class.
* Fix: `{% include ` link resolution does not work without a VCS root.
* Fix: Jekyll `{% include "" %}` completions would not work unless there was an `.html`
  extension between the strings.
* Fix: update for 2019.1 eap
* Fix: intentions missing groupKey were not showing up or being run
* Fix: make hex text dialog a licensed feature instead of dev feature.
* Fix: diagnostic/1931, possible fix for intermittent based sequence index out of bounds fix
* Fix: catch exception when github tasks request fails
* Fix: settings for HTML paste are not dependent on paste handler override.
* Fix: when ask on paste for html paste options would cause subsequent undo to fail due to
  temporary file modification.
* Fix: reverse the order of split editor configuration for "Show editor and preview" and "Show
  editor only"
* Fix: for API change in 2019.1 EAP.
* Add: `Simple structure view` option to display only heading hierarchy in the structure view
* Fix: optimize parser PSI generation by using hash map for type to factory function
* Fix: diagnostic/1849, ClassCastException: LeafPsiElement cannot be cast to
  MultiMarkdownLinkElement
* Fix: image reference links to references with wrong file type or not raw would not register as
  references to the reference definition. Added `getExactReference()` to return reference only
  if it is an exact match, `getReference()` not matches strictly by id since it is used for
  navigation and usages.
* Add: `Use Style Attribute` option to HTML Export settings. When enabled will apply stylesheet
  via `style` attribute of each element as done for `Copy Markdown as HTML mime content`.
* **NOTE:** stylesheet is expected to be in the same format as `COPY_HTML_MIME` stylesheet. See
  [Copy Markdown to HTML formatted Text Profile](https://github.com/vsch/idea-multimarkdown/wiki/Rendering-Profiles-Settings#copy-markdown-to-html-formatted-text-profile)
* **NOTE:** if `No Stylesheets, No Scripts` is selected then only styles explicitly defined by
  the profile will be used. If this option is not selected then `COPY_HTML_MIME` profile
  stylesheet will be used or if the `COPY_HTML_MIME` profile is not defined then the
  [default stylesheet for `COPY_HTML_MIME`][html_mime_default.css] will be used.
* Fix: move annotations for `Reference Links` to inspections
* Fix: move annotations for `References` to inspections
* Fix: move annotations for `Emoji` to inspections
* Fix: move annotations for `Anchor` to inspections
* Fix: move annotations for `Headings` to inspections
* Fix: move annotations for `Tables` to inspections and add quick fix for column spans
* Fix: move annotations for `List Items` and `Possible list items` to inspections
* Add: Html Generation option to not wrap paragraphs in `<p>` and use `<br>` between paragraphs
  instead. Useful for HTML exported files for use in Swing panels
* Add: Html Export target file path options to add to target directory. Useful if need to
  flatten directory structure of markdown files to a single directory for exported HTML
  * Add path relative to project
  * Add path relative to parent directory
  * Add file name only
* Add: same file path type options as target path for export image copied file path.
* Fix: IDE hangs when copying text containing the macro references which contained recursive
  macros.
* Fix: document format to ensure one blank line after macro definition
* Fix: `<>` wrapped auto links would prevent following bare auto-links from being parsed.
* Add: all elements intention to select element if intention displays dialog to give user
  feedback on which element is being used.
* Fix: do not highlight auto links as errors if remote link validation is disabled
* Fix: remote link annotation disabled by custom URI scheme handler
* Fix: #640, java.lang.NullPointerException with HtmlPasteOptionsForm
* Add: Parser
  [Macros Extension](https://github.com/vsch/idea-multimarkdown/wiki/Macros-Extension)
* Fix: list item indent/unindent could insert `&nbsp;` inserted/removed during wrapping but do
  not perform wrapping, causing the `&nbsp;` to be left in the text.
* Add: intention for auto link to explicit link conversion and vice-versa
* Fix: #605, Support for system specific protocol handlers. Pass through custom protocols to IDE
  browser launcher.
* Fix: to not highlight external URL links which consist of only the protocol at the end of the
  line.
* Add: color scheme export to save only non-synthetic attributes: `Intellij IDEA color scheme,
  reduced markdown (.icls)`
* Add: validation to auto-link remote url and completion/validation to anchor ref
* Add: url based parser settings for remote link markdown parsing. For now hardcoded for GitHub,
  GitLab and legacy GitBook compatibility. New GitBook anchor links not supported yet.
* Fix: diagnostic/1827, Empty collection can't be reduced.
* Fix: broken remote URL links to markdown files validation and anchor ref completions
* Add: quick fix intention for fixing unresolved anchor refs when a match can be made by
  ignoring case and removing duplicated `-`
* Fix: GitHub heading ids do not convert non-ascii to lowercase.
  * Add: `Heading ids lowercase non-ascii text`, selected for:
    * GitLab profile
    * GitBook profile
    * CommonMark profile
* Fix: formatter for extension
* Fix: invalid anchor refs not annotated for local links (broken by remote link validation)
* Add: intention for unresolved link addresses starting with `www.` to prefix with `https://`
  and `http://`. If remote link validation is enabled then only prefix which results in valid
  link address will be shown in the intention. If the resulting address reports as permanently
  moved then also add the destination location to intentions.
* Add: handling of HTTP:301 for remote content and intention to update link address
* Fix: for remote content cache only store list of anchors instead of content, more compact and
  provides the needed data
* Fix: remove directories from link completions to reduce noise in completions
* Fix: remote image links showed as unresolved, now IOExceptions during fetching treated as
  resolved.
* Fix: remove links returning image data now treated as resolved.
* Fix: #637, Links from main project repository to files in a sub-directory repository show
  unresolved
* Add: unresolved remote link address annotation error.
* Add: in settings total remote link count and memory use for remote content.
* Fix: only cache remote content when it is needed for anchor ref validation. For remote link
  validation only store the fact that it exists.
* Add: remote link content cache to use for validating remote content links and anchor refs
* Add: option to enable validation of remote links (annotates unresolved link if server returns
  error)
* Fix: remove anchor ref error annotation for links which do not resolve to a project file or do
  not exist if validating remote link anchor refs
* Add: error annotation for links to HTML files in project with anchor refs which do not link to
  `a` or `h1` through `h6` html tags with `name` or `id` attribute given by anchor ref
* Add: anchor link completion for links to HTML files in project to `a` or `h1` through `h6`
  html tags with `name` or `id` attribute giving the anchor ref
* Add: anchor link completion on external URLs which do not resolve to a project file.
  * Special handling if file extension matches a Markdown Language extension, will download the
    markdown file and will render it as HTML to extract anchor definitions
  * Special handling for GitHub (ones starting with http:// or https:// followed by github.com)
    * markdown files: If the link is to a `blob` type then will use `raw` type URL to get
      Markdown so it can be correctly rendered as HTML to extract anchor definitions.
    * html content:
      * remove `user-content-` prefix from anchor refs (GitHub adds these automatically)
      * remove `[0-9a-fA-F]{32}-[0-9a-fA-F]{40}` looking anchor ids
  * Special handling for GitLab (ones starting with http:// or https:// followed by gitlab.com)
    * markdown files: If the link is to a `blob` type then will use `raw` type URL to get
      Markdown so it can be correctly rendered as HTML to extract anchor definitions.
    * html content:
      * remove `[0-9a-fA-F]{32}-[0-9a-fA-F]{40}` looking anchor ids

### 2.6.0 - Bug Fix & Enhancement Release

* Fix: change definition indent maximum to 4, beyond which it converts the text to indented
  code.
* Fix: definition formatting would not add indent removal causing contained block quote prefix
  to be doubled
* Add: option to remove prefixes when joining lines
* Fix: move code style `Continuation Lines` indent into `Text` code style panel.
* Add: `Left Justified` option to ordered list style options
* Fix: force code style parser settings to CommonMark
* Fix: change code style sample parsing flags to modify parser flags to allow formatting all
  sample elements.
* Fix: settings "Manage..." exception in DataGrip without an open project. Now uses user home
  dir as default directory without an open project.
* Fix: fenced code and indented code indented with tabs would not minimize indent during
  formatting.
* Fix: HTML to markdown conversion
  * Fix: #268, Pipe characters are not escaped in Table (FlexmarkHtmlParser)
    * Fix: escape pipe characters in text (to avoid accidental use as table or other markup)
      when not inline code nor fenced code
    * Fix: escape back ticks when inside code
    * Fix: disable escaping of `[]` when inside code
      * Fix: disable escaping of `\` when inside code
    * Fix: replace non-break space with space when inside code
* Fix: `FlexmarkHtmlParser.BR_AS_EXTRA_BLANK_LINES` now adds `<br />` followed by blank line
* Fix: JavaFx Browser initialization bug introduced by 2016.3 compatibility fix.
* Add: "Paste HTML" button to HTML Paste Options dialog to paste HTML without conversion to
  markdown.
* Fix: clean up code style formatting and preview of style changes
  * style changes are now highlighted to properly reflect the last change, not whole document
    reformat changes
  * prefix changes would not be applied (or formatted) if text wrap for paragraphs was disabled,
    affected list items, definitions, block quotes
  * block quote prefix (compact with space) always inserted space after firs `>` instead of last
    `>`
  * TOC with html language option would not update preview
  * Remove unused list formatting options
* Add: link text suggestion for user label `@username` for GitHub user links of the form:
  `https://github.com/username`
* Change: remove runtime null assertions for function arguments
* Fix: scroll sync not working in 2018.3 EAP
* Fix: change lambdas to functions to have `arguments` available (causing exception in JetBrains
  Open JDK 1.8.0_152-release-1293-b10 x86_64
* Add: extra diagnostic information for Swing Browser `EmptyStackException`
* Fix: diagnostic/1759, kotlin arguments erroneously defined as not nullable.
* Fix: 2016.3 compatibility
* Fix: markdown code style settings to be created from file when available to allow IDE scope
  based resolution for markdown files to work properly.
* Add: HTML Settings option `Add <!DOCTYPE html>` to enable/disable having doc type at top of
  document. Required by Katex to work.
* Fix: update emoji icons
* Fix: GitLab math blocks to display as blocks instead of inlines
* Fix: disable tab overrides if there is a selection in the editor or multiple carets
* Change: split math and chart options from GitLab so that each can be selected without GitLab
  extensions if GitLab extensions are not selected.
* Add:
  [GitLab Flavoured Markdown](https://gitlab.com/gitlab-org/gitlab-ce/blob/master/doc/user/markdown.md)
  parsing and rendering functionality
  * Math inline using ```$``$``` and fenced code blocks with info of `math` using
    [Katex](https://github.com/Khan/KaTeX)
  * Chart fenced code blocks with info of `mermaid` using
    [Mermaid](https://github.com/knsv/mermaid)
  * Inserted text (underlined) via `{+text+}` or `[+text+]`
  * Deleted text (strike through) via `{-text-}` or `[-text-]`
  * Multiline block quotes using `>>>` at start of line to mark block start and `<<<` at start
    of line to mark block end.
    [GFM: Multiline Blockquote](https://gitlab.com/gitlab-org/gitlab-ce/blob/master/doc/user/markdown.md#multiline-blockquote)
  * Video image link rendering
    [GFM: Videos](https://gitlab.com/gitlab-org/gitlab-ce/blob/master/doc/user/markdown.md#videos)
* Fix: disable tab override when popup is showing to allow tab to be used for completions.
* Fix: with CommonMark list type bullet list item after ordered list and vice versa, would allow
  indentation in error.
* Fix: #469, pressing tab in a empty list item should indent the list
  * added option "List item indentation" under "Tab/Backtab Overrides" which enables using
    tab/backtab to change list item indentation
* Fix: #541, using tab to move to the next table cell
  * added option "Table Navigation" under "Tab/Backtab Overrides" which enables using
    tab/backtab to navigate table cells
* Fix: GitHub issue completions for 2018.3 EAP
* Fix: #577, Add feature to move table columns, added Move table column left/right
* Fix: remove line/search highlights from plain HTML preview
* Fix: remove auto links from spellchecking elements
* Fix: partial fix for list item needs blank line annotation for list in block quotes
* Fix: #610, hope can have more paste option, add HTML paste options to suppress conversion of
  inline and heading elements
* Fix: #623, Directory linking occasionally broken
* Fix: compatibility with versions 2017.
* Fix: possibly fix diagnostic pycharm exception on migrate code settings
* Change: update deprecated API usage
* Change: reduce number of highlighter overlay attributes to 3609 from 8497
* Change: update source code for 2018.2 API changes.
* Fix: #621, plugin oom in goland, potential memory leak
* Fix: #615, Plugin can't initialize in multi-user setup, now temp directory
  `.markdownNavigator` is created under the user's home directory
* Fix: #620, Check keyboard shortcut changes wrong list item
* Fix: #619, Create unchecked list item when pressing enter next to a line with a checked list
  item
* Fix: reference paste to add line references to link text in the form: `: Line #` or `: Lines
  #-#`
* Fix: diagnostic/1575, `node.treeNext must not be null`
* Fix: wrong range calculation for #612 fix
* Fix: #611, Backspace in empty check mark box deletes check mark
* Fix: #612, Code folding eats one character for underline headers
* Add: HTML comment folding and options
* Fix: diagnostic, parent already disposed

[#690, Link title should not be selected when pressing space]: https://github.com/vsch/idea-multimarkdown/issues/690
[#695, Click on structure view does not update preview]: https://github.com/vsch/idea-multimarkdown/issues/695
[#697, Autoscroll from source does not work in simplified structure view]: https://github.com/vsch/idea-multimarkdown/issues/697
[#702, Setext header marker equalization on ENTER broken]: https://github.com/vsch/idea-multimarkdown/issues/702
[#704, NoClassDefFoundError on 2019.1 EAP with JBRE]: https://github.com/vsch/idea-multimarkdown/issues/704
[#705, Show navigable headings as file members inside project view]: https://github.com/vsch/idea-multimarkdown/issues/705
[#706, URLs with a fragment identifier are treated as syntax errors]: https://github.com/vsch/idea-multimarkdown/issues/706
[#708, Empty element in simplified structure view when using just url as header text]: https://github.com/vsch/idea-multimarkdown/issues/708
[#709, New Icon looks bad on dark background]: https://github.com/vsch/idea-multimarkdown/issues/709
[#710, Memory leak in HTML preview window]: https://github.com/vsch/idea-multimarkdown/issues/710
[#711, Editor -> Toggle Editor Layout setting is not saved.]: https://github.com/vsch/idea-multimarkdown/issues/711
[#712, Target file is not on github annotation]: https://github.com/vsch/idea-multimarkdown/issues/712
[#713, Autocomplete links as "repository relative" by default]: https://github.com/vsch/idea-multimarkdown/issues/713
[#715, Completed task list items not rendered with checkmark]: https://github.com/vsch/idea-multimarkdown/issues/715
[#719, Wrap links in <> Do Not Show does not hide the inspection]: https://github.com/vsch/idea-multimarkdown/issues/719
[#720, Indentation in a numbered list 'flickers' when typing and deleting text]: https://github.com/vsch/idea-multimarkdown/issues/720
[#721, EAP: QuickFix for "Wrap Links" shows the message-key instead of description]: https://github.com/vsch/idea-multimarkdown/issues/721
[#726, unable to show ref anchor link explorer]: https://github.com/vsch/idea-multimarkdown/issues/726
[#727, css files not created in export folder]: https://github.com/vsch/idea-multimarkdown/issues/727
[#728, Loading indicator in settings doesn't disappear after loading]: https://github.com/vsch/idea-multimarkdown/issues/728
[#729, Can't disable 'Add query suffix to URI which increments when file changes' option]: https://github.com/vsch/idea-multimarkdown/issues/729
[#730, Can not configure toc anymore]: https://github.com/vsch/idea-multimarkdown/issues/730
[#731, apache pdfbox vulnerability]: https://github.com/vsch/idea-multimarkdown/issues/731
[#733, PlantUML include path]: https://github.com/vsch/idea-multimarkdown/issues/733
[#737, "Hard-break" Trailing Spaces Removed on Manual Save]: https://github.com/vsch/idea-multimarkdown/issues/737
[#741, Links]: https://github.com/vsch/idea-multimarkdown/issues/741
[#743, auto-move in preview to match source code cursor position]: https://github.com/vsch/idea-multimarkdown/issues/743
[#745, Exception from AppUtils.isAppVersionGreaterThan when running on development build of IntelliJ IDEA]: https://github.com/vsch/idea-multimarkdown/issues/745
[#746, Plugin creates files under .idea with default settings]: https://github.com/vsch/idea-multimarkdown/issues/746
[#747, Useless "Markdown Export On Settings Change" notifications]: https://github.com/vsch/idea-multimarkdown/issues/747
[#757, Gitlab multi-line blockquote syntax incorrect]: https://github.com/vsch/idea-multimarkdown/issues/757
[#760, Attributes support is broken/incompatible with fenced code blocks]: https://github.com/vsch/idea-multimarkdown/issues/760
[#761, html paste option can't save]: https://github.com/vsch/idea-multimarkdown/issues/761
[html_mime_default.css]: https://github.com/vsch/idea-multimarkdown/blob/master/resources/com/vladsch/idea/multimarkdown/html_mime_default.css

