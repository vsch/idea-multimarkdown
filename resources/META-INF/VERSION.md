## Markdown Navigator

[TOC levels=3,6]: # "Version History"

### Version History
- [2.1.1.8 - Bug Fix & Enhancement Release](#2118---bug-fix--enhancement-release)
- [2.1.1 - Bug Fix & Enhancement Release](#211---bug-fix--enhancement-release)
- [2.1.0 - Bug Fix Release](#210---bug-fix-release)
- [2.0.0 - New Parser Release](#200---new-parser-release)
- [1.8.4 - Bug Fixes and Enhancements](#184---bug-fixes-and-enhancements)
- [1.8.3 - Bug Fixes and Enhancements](#183---bug-fixes-and-enhancements)
- [1.8.2 - Bug Fixes and Enhancements](#182---bug-fixes-and-enhancements)
- [1.8.0 - Bug Fixes and Enhancements](#180---bug-fixes-and-enhancements)
- [1.7.1 - Bug Fixes and Enhancements](#171---bug-fixes-and-enhancements)
- [1.7.0 - Full PsiTree Parser, Doc Structure View & Doc Formatting](#170---full-psitree-parser-doc-structure-view--doc-formatting)
- [1.6.3.3 - Bug Fix Patch Release](#1633---bug-fix-patch-release)
- [1.6.1 - Name Change Release](#161---name-change-release)
- [1.5.0 - Bug Fix & Optimization Release](#150---bug-fix--optimization-release)
- [1.4.10 - Bug Fix & Optimization Release](#1410---bug-fix--optimization-release)
- [1.4.9 - Bug Fix & Optimization Release](#149---bug-fix--optimization-release)
- [1.4.8 - Bug Fix & Optimization Release](#148---bug-fix--optimization-release)
- [1.4.7 - Bug Fix & Optimization Release](#147---bug-fix--optimization-release)
- [1.4.6 - Bug Fix Release](#146---bug-fix-release)


# ** This version requires Boot JDK 1.8 **

### 2.1.1.8 - Bug Fix & Enhancement Release

#### Basic & Enhanced Editions

- Add: #225, code highlight line number via Prism.js highlighter option
- Fix: #313, Changing fonts causes WebStorm to freeze
- Add: #316, Make shared settings Project specific
- Fix: #315, NullPointerException with v2016.3 EAP (163.6110.12)
- Fix: Implement multi-line URL image links in flexmark-java

#### Enhanced Edition

* Fix: when Prism.js is used as highlighter, scrolling to source with caret in the code part of
  the fenced code would always scroll to top of document.
* Add: #314, Export .html files (as part of build?)
    * exported files are limited to being under the project base directory to prevent erroneous
      target directory from writing to the file system in unexpected location.
* Add: scope based rendering profiles allowing fine grained control on markdown rendering
  options.
* Add: #319, Synchronize source caret to preview element on click. 
* [ ] Add: List syntax dependent list item action behavior.
* [ ] Add: List syntax dependent list format behavior.

### 2.1.1 - Bug Fix & Enhancement Release

#### Basic & Enhanced Editions

- Fix: #299, Tables not syntax highlighted in basic version.
- Add: List syntax options: CommonMark, Fixed, GitHub.
- Add: #301, License activation not working for some network security configurations, Option to
  use non-secure connection for license activation.
- Fix: #302, IndexOutOfBoundsException: Index out of range: 190
- Fix: #307, NegativeArraySizeException when opening .md.erb file, IDE bug
- Change: update Kotlin to 1.0.4

#### Enhanced Edition

- Fix: #305, Document Format indents Footmarks converting them to code blocks
- Add: #306, Copy/Cut of reference links, images or footnote references to include the
  references and footnotes on paste.
- Add: #300, Breadcrumbs support for Markdown documents
- Fix: breadcrumbs to show heading hierarchy as parents, including headings nested within other
  elements like list items, block quotes, etc.
- Add: breadcrumb option to show element text and maximum number of characters of text to use
  (10-60, 30 default).
- Fix: breadcrumb setext heading to use atx equivalent text
- Fix: breadcrumbs to show paragraph text instead of `Text Block`
- Add: Copy as JIRA formatted text action. Copy selection or whole document to clipboard as JIRA
  formatted text.
- Fix: #308, Wiki vcs repo not recognized in 2016.3 due to API changes. Affects to http:...
  absolute link conversion from non wiki markdown files to wiki target files.
- Add: on paste reference link format resolution for new destination file
- Add: on paste link format resolution for new destination file

### 2.1.0 - Bug Fix Release

#### Basic & Enhanced Editions

- Change: update source for flexmark-java refactored file layout.
- Fix: #287, tables stopped rendering
- Fix: #286, PyCharm 2016.2.1, unterminated fenced code causing too many exceptions
- Fix: #285, Not able to parse .md.erbfile
- Fix: #287, tables stopped rendering part 2, tables not rendering at all
- Fix: #291, on open idea load multimarkdown failure some time!, tentative fix.
- Change: remove Lobo Evolution library and other unused dependencies.
- Fix: #293, Cannot adjust settings for "Explicit Link"

#### Enhanced Edition

* Fix: remove e-mail validation from fetch license dialog.
* Fix: typing at the start of text of a numbered list item with wrap on typing enabled would
  delete the character as soon as it was typed.
* Fix: wrap on typing would stop wrapping text when space was typed. Caused by the IDE no longer
  generating pre-char typed handler calls for some yet unknown reasons.
* Fix: remove wrap on typing disabling when typing back ticks or back slashes because it was
  only needed due to pegdown parser quirks.
* Fix: #288, IndexOutOfBoundsException
* Fix: #294, Structure view text not compatible with text search.
    1. Headings: searchable text is the heading text, greyed out text is the heading id with #
       prefixed showing the ref anchor for the heading
    2. Images: searchable text is the image link, greyed out text is the alt text
    3. List Items: searchable text is the first line of the item text
    4. Links: searchable text is the link url, greyed out text is the link text
    5. Footnotes: searchable text is footnote reference `:` first line of footnote text
    6. References: searchable text is the reference id `:` reference link url
* Fix: #296, License expiration not handled properly by plugin for versions released before
  license expired
* Fix: #297, Code Fence only minimizes leading spaces of the first code line during formatting
* Fix: #298, Formatting list items with empty text and first item a Atx heading, moves the
  heading before the list item

### 2.0.0 - New Parser Release

#### Basic & Enhanced Editions

- Fix: #282, Child paragraphs of tight list items are merged into the item text in preview
  instead of being a separate paragraph.
- Change: Component name for Markdown Navigator application shared settings changed to `Markdown
  Navigator` from a confusing `ApplicationShared`. Did't realize that the component name was
  used for display in import/export settings dialog.
- Fix: JavaFX and Swing stylesheets to better match GFM rendering.
- Add: Flexmark parser used for Swing preview rendering and plain HTML text previews.
- Add: allow task list items from ordered list items like GitHub, rendering is the same as
  bullet items.
- Fix: emoji renderer was not setting image height, width nor align attributes
- Fix: emoji parser flags were not being passed to HTML Renderer
- Add: Flexmark parser used for JavaFX Html preview rendering.
- Add: Debug setting to allow switching to pegdown for HTML rendering for debug and comparison
  purposes.
- Change: update flexmark-java parser to spec 0.26 with more intuitive emphasis parsing
- Add: skeleton error reporter to make reporting plugin errors more convenient and also more
  complete. No server code yet. For now disabled.
- Fix: With lexer as syntax highlighter deleting the last space after `[ ]` would cause an
  exception that was trapped but it would mess up syntax highlighting
- Fix: parser would accept ordered lists using `)` delimiter, as per commonmark spec.
- Add: flexmark parser as the default option for lexer, parser and external annotator. Typing
  response is amazing. Some elements still missing:
    - Definitions
    - Typographic: Quotes, Smarts
    - Multi-Line Image URLs

#### Enhanced Edition

* Change: Move pegdown timeout from parser settings to debug settings. :grinning:
* Add: use actual char width to fix for wrap on typing fix when typing right before start of
  line elements.
* Add: GFM table rendering option to render tables text that GFM would render as text.
* Fix: wrap on typing right before an element set to always be at the beginning of line would
  always put the caret right before the element after wrapping, typing the next word and space
  would wrap the word to the previous line, leaving the caret at the start of line. Now the
  caret is kept at the end of the previous line making caret behaviour more natural.
* Fix: split editor layout change actions and preview content change actions now restore focus
  back to the text editor. Now they can be used in keyboard shortcuts without interrupting
  typing by needing a mouse click to restore focus.
* Add: source position information to list items.
* Fix: link text suggestion provider to remove any `..` directory references
* Fix: Refine JavaFX scroll preview to source position and highlighting to work more intuitively
  for block elements, images and address JavaFX WebView DOM element offset quirks.
* Add: JavaFX scroll preview to source position and various highlight options to show which
  element in the source has focus.
* Add: flexmark spec example rendering options: fenced code, sections, definition list
* Change: simulated TOC to allow `''` for titles to match what is allowed in references
* Add: list annotation and quick fixes when list items are inconsistent. i.e. bullet and
  numbered items mixed in one list.
* Add: table annotations and reformat quick fix
* Add: parser option for generated TOC to include a blank line spacer after the `[TOC]:#` marker
  to increase compatibility with existing markdown parsers.

### 1.8.4 - Bug Fixes and Enhancements

#### Basic & Enhanced Editions

- Fix: #278, After upgrading to 1.8.3. IntelliJ IDEA 2016.2 hangs immediately after startup. One
  more effing pegdown parsing related issue.

### 1.8.3 - Bug Fixes and Enhancements

#### Basic & Enhanced Editions

- Fix: if remote url in git config lacked a .git extension then the repo would not resolve links
  in preview.
- Fix: #263, AssertionError while indexing .md file and probably a few others. pegdown bug would
  cause some AST nodes to have start > end.

#### Enhanced Edition

* Add: project module names to inline code completions
* Fix: range assertion fail in folding builder for empty range
* Fix: #274, Default Editor layout always reset to "editor and preview"
* Fix: #273, Loosen list action IndexOutOfBoundsException when not all list items are selected
* Fix: Reformat document and reformat element actions now remove unnecessary trailing spaces at
  end of text blocks. Reformat element will only remove trailing spaces located after the caret
  to prevent the caret from hanging off the end of the text block after reformatting or changing
  the caret position.
* Fix: caret placed one character left of a space if wrapping after a typed space with a space
  before caret.
* Add: flexmark spec line markers for option missing in some test classes.
* Add: more flexible inline code completions, will allow qualified class names and multi-class
  name matches when completing members. If more than one class name matches then the combined
  set of members is used from all matched classes.

### 1.8.2 - Bug Fixes and Enhancements

#### Basic & Enhanced Editions

- Fix: #268, Installed exception on webstorm load V1.80, caused by attempt to use a Java support
  class that does not exist on WebStorm. Causes exception on any refactoring action.

#### Enhanced Edition

* Fix: #271, ClassNotFoundError with Latest PhpStorm update and other locations where Java based
  refactoring classes were used.
* Fix: toc element post parsing bug that caused assertion failure during parsing
* Fix: annotator showing reference used when it is not used
* Fix: unnecessary no issue token type logging
* Add: tooltip to flexmark option checkbox in edit example options dialog
* Fix: list loose/tight formatting when last item of nested list has blank line
* Fix: list loose/tight formatting when tight option is selected and have multiple blank lines
  between items.
* Fix: insert loose list item on ENTER with `loosen if has loose item` list spacing option
* Fix: improper flexmark ext rename processor implementation disabled test spec class/file and
  spec file renaming.
* Add: flexmark-java spec example FAIL option
* Change: inline code now treated as literal so that classes, methods and fields can be
  refactored with search in strings. :warning: this only works if syntax highlighting is set to
  lexer not annotator. Lexer used with annotator syntax highlighting only distinguishes text and
  html comments. The latter so that TODO processing will work with either highlighter.
* Change: markdown inside code fence or verbatim with markdown injected language gets all types
  of references from the outer file. Outer references see their inner referencing elements and
  will be marked as used if there are any inner or outer references. Refactoring will rename all
  referring elements. Outer reference elements do not see inner duplicates, but inner ones see
  outer ones.

    :warning: Exception to outer reference access is a `[TOC]:#` element. It only uses outer
    context headings if none are defined within the injected context. An inner `[TOC]:#` element
    is not updated automatically on file save or format. **Only manual update** via the update
    table of contents quick fix to prevent inadvertent changes to code fence or verbatim text.
* Prep: for sim toc parsing to be done by flexmark-java options parser
* Add: flexmark-java extension to convert module camel case to dot for extension package.
* Add: flexmark-java extension to convert module camel case to dashed for: module name
* Add: flexmark-java extension module config common profiles to select common combinations:
    * Select None/All Buttons
    * BlockParser

### 1.8.0 - Bug Fixes and Enhancements

#### Basic & Enhanced Editions

- Fix: add tests for project being disposed in a few places to catch when later invocation may
  occur after a project is closed.
- Fix: make emoji shortcut into a non spell checking element
- Add: `.emoji` live template
- Fix: wiki link syntax parser option was being taken from old settings in some parts of the
  code.
- Fix: #262, NPE opening file
- Fix: clicking on links anchor referencing links did not move the element into view in JavaFX
  preview.

#### Enhanced Edition

* Change: Now disabling table formatting also disables row insert on ENTER and delete row/column
  on BACKSPACE to make it easier to turn off side-effects for manual editing.
* Fix: #252, update failed for AnAction with
  ID=com.vladsch.idea.multimarkdown.editor.actions.styling.ListTightAction
* Fix: #253, null
* Fix: occasional fast typing with wrap as you type would insert a copy of previously typed few
  characters.
* Fix: reformat element action added an extra prefix and duplicated last character of the
  paragraph.
* Fix: Task item toggle action to preserve `-` or `*` prefix of the item and only use the new
  bullet item prefix code style setting when converting paragraphs or numbered list items.
* Fix: Bullet item toggle action to preserve `-` or `*` prefix of the item and only use the new
  bullet item prefix code style setting when converting paragraphs or numbered list items.
* Fix: add missing `+` prefix for bullet lists in code style settings
* Fix: exception in paragraph context with empty paragraph.
* Fix: wrap on typing turns off intermittently when typing in a paragraph immediately followed
  by a table
* Add: flexmark-java test spec file format handling with flexmark front matter and example
  elements.
* Add: flexmark-java test spec option completions, references, annotations and quick fixes.
* Add: flexmark-java test spec options keyword completion
* Fix: spell checking of text had identifier setting in error
* Add: implement stub elements and index for flexmark options
* Fix: flexmark option refactoring
* Add: debug actions to collect unique markdown files and to open all of them one at a time to
  check for WebView crashing bug.
* Add: List spacing option to code style settings
* Add: add list item on ENTER will add a blank line above/below a new item if list spacing is
  set to Loosen or Loose in code style.
* Add: flexmark edit options intention and dialog
* Add: flexmark refactoring of options. Needs work but functional.
* Fix: invalid regex in wrap on typing before markdown element prefix text
* Add: flexmark options completion to show actual `DataKey` and value assigned making it easier
  to figure out which option is the one you want.
* Add: flexmark java completion contributor to handle option string literal completions.
* Add: inline code to be a language injection host
* Add: completion in inline code will attempt to complete a Java references separated by `.`.
  First classes, then: methods, fields and inner classes. Methods complete with their parameter
  type signature. Fields, methods and inner classes will continue completion if a `.` is typed.
* Fix: self referencing links in scratch files with would not resolve because scratch files are
  not part of the project. Now a self referencing link in any file will resolve.
* Fix: Reference refactoring of reference name would not work if the link reference was self ß
  referencing.
* Add: Table of Contents element that works with basic Markdown processors. Updated on format
  element format document when
  [Table Of Contents](../../../../wiki/Enhanced-Features#table-of-contents)
* Change: toggle task list item now changes items to bullet list items if all selected items are
  already task list items.
* Add: flexmark-java feature to work with unicode invisible characters. Dialog to show, add.
  clean or remove from selected text.
* Fix: toc to check there is a blank line between it and the first child. Also need to split a
  list at first loose item or one that is not a TOC style list item since it is not part of the
  toc. Very important. Otherwise accidentally blows away list items that follow a TOC element.
* Fix: TOC element to accept `[TOC ....]:#` anything after the space in the tag as a valid
  `[TOC]:#` tag. Otherwise partial edits may cause the elements to be chewed up since the TOC
  element disappears on formatting.
* Add: TOC levels to select which heading levels to include:
    * a single number: levels accepted 2 to number
    * a list of numbers: heading accepted if its number is in the list
    * a list of ranges #-#: heading accepted if its number is in one of the listed ranges
* Fix: TOC option to not create missing empty header levels. ie. collapse missing headers.
* Fix: task list item action not to change closed task items to open ones.
* Add: combination syntax attributes for abbreviations in quotes, tables, headers and definition
  term.
* Add: code style option to sort abbreviations, footnotes and references, with or without unused
  ones last.
* Add: test for TOC option in parser settings before parsing SimToc
* Add: annotation for TOC needs updating and a quick fix
* Add: intention to edit TOC options, with preview and all the fixings
* Add: dedicated license server with two fallback URLs in case of failure.
* Add: TOC options refactoring will use dummy headings if the current file does not have any.
* Fix: issues when inserting characters and backspacing right after numbered item prefix.
* Fix: characters typed on a blank line right above a paragraph would insert a duplicate of the
  character at the start of the next line.
* Fix: anchor ref links should not check if the file is on GitHub
* Fix: completions for emoji and GitHub issues in headers
* Add: TOC line marker to navigate to toc listed headers
* Add: update TOC on save
* Fix: TOC element with empty title `""` would use default title setting
* Add: format list loose/tight style setting implementation.
* [ ] Fix: typing on a line with the next text stretch too long to fit, should keep cursor on
      the same line after space, add extra space if needed.
* [ ] Add: intelligent nested children find function that understands which elements can be
      inside others. Generic function too slow because it dives into unnecessary elements.
* [ ] Add: Toggle emphasis action config for end of region punctuations to be treated as
      whitespace when the caret is immediately to the left of one and is at the end of a
      non-space region, to allow the toggle actions to work when at the end of a word but
      followed by a punctuation.

### 1.7.1 - Bug Fixes and Enhancements

#### Basic & Enhanced Editions

- Fix: #244, NPE upon opening simple markdown file
- Fix: scratch files not recognized as Markdown during editor creation in latest
  intellij-community builds.

#### Enhanced Edition

* Fix: #245, Inserting an ordered list item on ENTER with renumbering enabled causes exception
* Fix: #246, Auto scroll to source. Swing preview would always scroll to top of page after
  document modification.
* Add: preview setting for `Scroll preview to source position` for Swing preview.
* Fix: #247, Error and corrupting files, pegdown source position out of range
* Add: Loosen/Tighten list toolbar buttons and action to make all items in a list double spaces
  or single spaced.
* Fix: add list item will now add task item marker if the current list item has one.
* Add: task list item toggle done intention, button and action for caret or selection
* Add: change list item or selection elements to: bullet, ordered, task list button and action
  for caret or selection. If election contains nothing but paragraphs then convert them to list
  items. If the selection contains nothing but list items which match the action type then the
  list items will be converted to paragraphs.
* Change: bold/italic/strikethrough buttons and actions when removing their markers will not
  create a selection if the action automatically would automatically select the same text if
  invoked again.
* Fix: first paragraph of a file would not be recognized for list actions unless it had a blank
  line before.
* Fix: memory use going high in 2016.3 IDEA and probably other versions of the IDE
* Add: code style option for unordered list prefix: no change, dash, asterisk
* Add: code style option for new unordered list prefix: dash, asterisk, used for bullet list
  item action
* Fix: reference link completions, broken by moving to full parse tree
* Fix: intermittent backspace at end of paragraph would duplicate last non-space character of
  the paragraph.
* Fix: exception when creating a code fence element with an empty content
* Fix: pegdown parsing code fence with first line containing three or more `-` or `=` would
  parse code fence opening sequence as a Setext header.
* Fix: show preview for HTML documents would not recognize HTML scratch files.
* Change: backspace to delete row/column when backspacing in an empty cell. Previously it
  deleted these when the cell became empty.
* Change: bold/italic/strikethrough/code actions now will apply to the next word when caret is
  at the beginning of a word and there is no selection.
* Fix: insert link action with no selection would not move the caret inside the link URL
  parentheses.
* Add: develop feature flexmark-java spec file format handling.

### 1.7.0 - Full PsiTree Parser, Doc Structure View & Doc Formatting

#### Basic & Enhanced Editions

- Add: Now combination syntax attributes are created on load to simulate overlay with alpha of
  various attribute combinations. Colors for these combination attributes are updated when
  current scheme changes. Changing colors within a color scheme does not generate and update
  event so if you are changing a color scheme and need the computed combination attribute colors
  re-computed, select another color scheme, press <kbd>Apply</kbd>, now select your desired
  color scheme and close the preferences dialog.
- Change: Inserting a link when there is a selection in the document will use the selected text
  as the link text and put the caret ready for typing or auto-completing the link.
- Change: Bold/Italic/Strikethrough actions now will wrap the word if caret is at the end of the
  word. Easier to make emphasis by typing a word and then invoking the action.
- Change: Bold/Italic/Strikethrough actions now will un-wrap their corresponding emphasis if
  caret is right after the trailing marker of the emphasis.
- Change: removed Lobo browser preview browser option.
- Fix: #100, invalidate JavaFX WebView image cache on reload, currently images are not updated
  if they change on disk. Not able to clear the cache but have a workaround. JavaFX WebView
  preview now adds a query parameter to modified image files so that each modification is
  treated as a different file by the WebView file cache. This fixes the problem of the preview
  displaying a cached image after modification but will cause WebView to cache more files, an
  extra copy for each modification.
- Fix: Markdown Application settings to handle null for UIManager.LookAndFeelInfo.
- Fix: pegdown parser error in abbreviation definition that contains trailing spaces
- Add: combination attributes for definition term and inline attributes.

#### Enhanced Edition

* Change: implemented full PsiTree building from pegdown AST as transition to pegdown
  replacement. Introduced bugs fixed:
    * Move refactoring of contained links was broken
    * Completions were broken
    * Completions of elements in header text was broken
    * Completions of link text was broken
    * Completions of wiki links was broken
    * Wiki link anchor refs were broken
    * Definition wrapping was broken
    * Multi-line image URL injection was broken
    * Editing injected language fragment of verbatim element
* Fix: Header ids of headers that contained link or ref links or images would not be generated
  identical to GitHub.
* Add: block quote increase/decrease level
* Change: list item indent/unindent now affects sub-items and renumbers ordered list items.
* Add: document structure view with Headers, Images, Tables, References, Footnotes sections and
  Document.
* Add: image link text suggestions now strip out file ending of @2x and @2x_dark before building
  the suggestion list from the file name.
* Fix: table auto-format to handle arbitrary parent prefixes
* Fix: fenced code to use proper prefix handling
* Fix: multi-line image URL to use proper prefix handling
* Fix: #223, Wrap on typing and Table format do not take actual character widths into account
  for multi-byte characters
* Add: Code style option to use actual character width for wrapping and table formatting. On by
  default.
* Fix: table insert row on ENTER with soft wrap on would not place the caret on the new row.
* Fix: table auto-format on typing was not formatting when using keyboard input for multi-byte
  character sequences.
* Add: document format action to reformat and re-arrange elements in current document.
* Add: abbreviations section to document structure view
* Fix: ordered list item renumbering was off by blank lines between items
* Add: definition marker code style option: any, colon, tilde to affect the markers used for
  definitions in the document
* Fix: block quote indent must always be multiple of 4 spaces.
* Fix: formatter was not outputting jekyll front matter
* Fix: formatter with wrap text option disabled would leave non-break spaces in file.

### 1.6.3.3 - Bug Fix Patch Release

#### Basic & Enhanced Editions

- Fix: #233, links to file names with + do not resolve.
- Fix: #234, blurry text in preview mode, now using IDE Editor setting for font smoothing by
  default and basic version has grey scale font smoothing as an option.

#### Enhanced Edition

* Add: parser option to not encode/decode `+` in links for compatibility with GitBook.
* Add: quick fix to create missing file and directories in the path.
* Fix: quick fixes that create file or retarget link to display url decoded file path.
* Change: inline HTML to not split across lines when wrapping text to margins.
* Fix: link format change intentions to account for GitBook URL encoding setting
* Fix: remove URL encoding from URI based format (`file://`) links

### 1.6.1 - Name Change Release

#### Basic & Enhanced Editions

- Change: plugin name changed to Markdown Navigator
- Add: emoji text to icon conversion for preview, see:
  [Emoji Cheat Sheet](http://emoji-cheat-sheet.com/). Default is enabled. Optionally can have
  image url point to GitHub for those that want to generate a stand alone file from the preview
  HTML text and don't want to lug the 860+ icons for emoji with the file.
- Fix: emoji shortcut non_potable_water to non-potable_water.
- Fix: emoji not being translated in Swing preview.

#### Enhanced Edition

* Fix: inline elements were not syntax highlighted in footnote text. This also resulted in no
  completions, validation or intentions being available in footnote text.
* Change: toolbar table actions will not be updated for enable/disable state if the current
  table is \>100 lines, to prevent excessive cursor movement delays.
* Fix: Load license from server dialog had get trial license title.
* Fix: #233, Usages of files are not found if the link url encoding did not equal the original
  file name.
* Fix: image links and ref images in a link text element would not show completions
* Add: Emoji completions and annotations of invalid or ones missing on GitHub.
* Fix: valid emoji text characters to include `+` and `-`
* Add: code style settings migration from MultiMarkdown to MarkdownNavigator

### 1.5.0 - Bug Fix & Optimization Release

#### Basic & Enhanced Editions

- Add: patch release and eap update streams
- Fix: syntax highlighting inline elements in definition text
- Add: HTML entity syntax highlighting.
- Fix: Comments with todo items would not show up in to do list when syntax highlighting was set
  to annotator. Now comments are parsed by the plain text lexer used to disable lexer syntax
  highlighting.
- Fix: update highlight.js to version 9.3.0 and include all available languages.
- Add: live templates starting with `.`

    | Element       | Abbreviation    | Expansion                                               |
    |---------------|-----------------|---------------------------------------------------------|
    | Abbreviation  | `.abbreviation` | `*[]: `                                                 |
    | Code fence    | `.codefence`    | \`\`\` ... \`\`\`                                       |
    | Explicit link | `.link`         | `[]()`                                                  |
    | Footnote      | `.footnote`     | `[^]: `                                                 |
    | Footnote Ref  | `.rfootnote`    | `[^]`                                                   |
    | Image         | `.image`        | `![]()`                                                 |
    | Ref image     | `.rimage`       | `![][]`                                                 |
    | Ref link      | `.rlink`        | `[][]`                                                  |
    | Reference     | `.reference`    | `[]: `                                                  |
    | Table         | `.table`        | <pre><code>`|   |`&#10;`|---|`&#10;`|   |`</code></pre> |
    | Task          | `.task`         | `- [ ] `                                                |
    | Wiki link     | `.wikilink`     | `[[]]`                                                  |

#### Enhanced Edition

* Fix: #195, License activation being reset when no network connection is available.

* Fix: #196, Incorrect parsing of compound reference links

* Fix: #198, Image links that don't end with an extension don't get recognized. Now image links
  without extension are assumed to be correct. No error or warning is generated for these links.
  Query strings are also stripped from the link address before looking for an extension.

* Fix: #199, Multi-line image URLs not parsed correctly when terminating ) is followed by white
  space characters file and without EOL.

* Fix: #201, Image link completion in wiki pages leaves out subdirectories

* Fix: #211, Completions for some empty link elements show no suggestions.

* Add: highlighting of auto-inserted `*`, `_` or `~` that would be deleted if a space is typed.

* Change: Auto-format table on typing and smart `*` `~` duplication to be off by default.

* Add: Auto inserted `*`, `_` and `~` that will be deleted by typing a space are now colored in
  the scheme's comment color to highlight that they can be deleted by typing a space

* Fix: block quote prefix on fenced code would not be stripped off. Prefix needs to be
  consistent on all the lines for the prefix to be properly stripped for the injected language
  fragment.

* **List editing features**
    * Add: #210, List item un-indent/indent toolbar buttons, and actions. List item
      un-indent/indent toolbar buttons, assigned to Ctrl-Y/Ctrl-U respectively.
    * Fix: #209, lines ending in `* ` or `- ` would be erroneously handled as empty list items
      by <kbd>BACKSPACE</kbd> handler.
    * Fix: second list items would not enable the indent list action.

* **Table editing features**
    * Fix: #212, Table formatting while typing sometimes causes the cursor to jump erratically
      to end of table.
    * Add: #214, Feature: add insert table toolbar button
    * Add: #215, Feature: table insert/delete row/column toolbar buttons
    * Add: #216, On <kbd>ENTER</kbd> insert table row with configuration options
    * Add: #217, On <kbd>BACKSPACE</kbd> delete empty table row/column with configuration
      options
    * Add: Syntax highlighting colors for table header cells separate from table body cells.
    * Fix: as you type table parsing changed to use pegdown instead of hand rolled parser.
      Handles escaped pipes and pipes in code spans correctly.
    * Fix: toggle actions: bold, italic, code, strikethrough; to select text when toggling the
      effect off.
    * Add: logic to disable `auto-format table on typing` when modifying a table causes it to no
      longer have the same number of columns on all rows for editing actions that may be partial
      modifications and if `Add missing columns` is enabled. ie. backspacing or typing back
      ticks, backslash or pipe characters.
    * Fix: reformatting tables without lead/trail pipes would loose the last columns that were
      blank. Now a trailing pipe is added if the column is blank to preserve the correct column
      count.
    * Fix: reformatting tables that were not terminated by a blank line would delete text after
      table up to a blank line.
    * Fix: Mitigate effects of table cell containing unterminated strong, emphasis or strike
      through markers which absorb all text until a blank line. Now this condition is detected
      and wrap on typing and auto-format table are turned off to prevent messing up the format.
    * Fix: Adjust caret to be at the pipe symbol when typing before the first table column.
      Otherwise indentation could be changed causing the table to no longer be valid.

* **Auto-format and Wrap on typing features**
    * Add: Toolbar buttons for toggle Auto-format and toggle wrap on typing.
    * Fix: #202, Plain text paragraphs that have indentation spaces do not get properly wrapped
      as you type.
    * Fix: made wiki link elements non-wrap so that they will not be wrapped across lines.
    * Fix: intermittent wrap on typing failure to wrap paragraphs
    * Fix: #205, Wrap on typing of paragraphs with embedded multi-line URL images will only wrap
      text before the image. Now for the purpose of wrapping these image links are treated as
      paragraph breaks and each segment is wrapped separately.
    * Fix: extra spaces added at end of wrapped text lines. Now only hard break spaces will be
      kept at end of text lines.
    * Add: #206, Wrap on typing continuation line indenting now has the following options:
        * None - continuation lines will start at column 1
        * **Align text edge - default**. Will align continuation lines with the text of the
          first line
        * Indent - continuation lines will start at indent of first line
        * Indent +1 level - continuation lines will start at indent of first line + 4 spaces
        * Indent +2 levels - continuation lines will start at indent of first line + 8 spaces
    * Fix: #207, Markdown hard break spaces are not always be preserved when formatting
      paragraphs
    * Fix: #208, Auto links are not recognized as inline elements during wrap on typing
      reformatting op
    * Fix: #213, Enabling HARD WRAPS in parser options prevents wrap on typing of list items
      that span multiple lines. HARD WRAPS parser extension is now disabled for purposes of
      syntax highlighting and PSI generation. It is only used for HTML rendering.
    * Add: logic to disable `wrap on typing` when modifying a table causes it to no longer be a
      valid markdown table
    * Add: tooltip when `wrap on typing` or `auto-format table on typing` is automatically
      disabled.
    * Add: logic to disable `wrap on typing` when a block is not terminated by a blank line and
      the following block can be potentially merged into the paragraph.

* **Typing response optimizations**
    * Fix: #19, Optimize typing response with or without wrap on typing and table reformat
    * minimize updates to unchanged parts of the paragraph or table
    * handle IDE skipping calls to handlers when fast typing (or rolling forehead on the
      keyboard).
    * HTML generation was not disabled when only main editor was shown. This would cause
    * Changed default syntax highlighter to external annotator based to reduce typing delay. If
      you want to use lexer based syntax highlighting you will need to change it in
      settings/preferences.
    * disabled html generation when preview is not shown

    Fastest typing response is achieved when:

    * syntax highlighting is turned off

    * wrap on typing is disabled

    * auto-format tables is disabled

    * all previews are turned off

* **Jekyll front matter handling**
    * Fix: #200, Jekyll front matter is not recognized if the terminating marker is at the end
      of file.
    * Fix: # 222 Inspection to detect Jekyll front matter presence in the file, with option to
      enable or ignore.
    * Code style option to not splice image and explicit links which are start of line to
      previous line during paragraph reformatting. * Jekyll front matter folding region and
      config

### 1.4.10 - Bug Fix & Optimization Release

#### Basic & Enhanced Editions

- Fix: book icon in preview would not open document in browser
- Fix: incorrect HTML rendering of undefined reference link `[some-text][]` as
  `[some-text][some-text]`

#### Enhanced Edition

* Add: list item handling on <kbd>ENTER</kbd> and <kbd>BACKSPACE</kbd>. <kbd>ENTER</kbd> will
  add unordered item or ordered item depending on what is preceding caret location.
  <kbd>ENTER</kbd> and <kbd>BACKSPACE</kbd> will remove empty list items. Configuration to
  enable/disable in *Editor > Code Style > MultiMarkdown* under `List Items`
* Improve: auto format handling on typing, <kbd>ENTER</kbd> and <kbd>BACKSPACE</kbd>
* Add: table formatting via the format toolbar button
* Add: table formatting option to format as you type in table separator line

### 1.4.9 - Bug Fix & Optimization Release

#### Basic & Enhanced Editions

- Add: formatting buttons
- Add: markdown specific soft-wrap setting. This only affects the editor when it is first
  opened. Thereafter use keyboard shortcuts to toggle soft wrap mode.
    - default - uses global setting
    - disabled - always opens editor with soft-wraps turned off
    - enabled - always opens editor with soft-wraps turned on
- Fix #190, Incorrect parsing of HTML blocks. This also fixes improper handling of HTML block
  suppression.

#### Enhanced Edition

* Add: handling of Jekyll front matter in markdown documents. Document must start with `---` and
  have another `---` terminating the front matter for it not to be parsed as Markdown.
* Add: option to embed CSS resources into the HTML document instead of URL links

### 1.4.8 - Bug Fix & Optimization Release

#### Basic & Enhanced Editions

- Add: option for syntax highlighting type either lexer based, external annotator or none at all
- Fix: #176, Exception: Panel is guaranteed to be not null now
- Fix: #180, code syntax highlighting has border around code blocks in HTML preview.
- Fix: #183, Incomplete Tasks in Task list not rendering correctly
- Fix: #182, Light scroll bars showing in dark theme. Added CSS to change WebView scroll bar
  colors.
- Fix: #178, Chinese character display problem. Headings would be empty if they contained only
  non-ascii alpha characters.
- Fix: #184, Smooth scroll issues with preview. Reduce frequency of on scroll callbacks to
  reduce delay during scrolling.
- Fix: #185, IntelliJ IDEA performance is heavily affected by this plug-in. Pegdown bug causes
  exponential parse time for markdown with unclosed HTML tags and fenced code blocks that
  contain HTML.

#### Enhanced Edition

* Add: parsing of HTML anchor elements with id for resolving, annotating duplicates and finding
  usages for ref anchors. Only the opening tag is significant: `<a id='ref-anchor'>` or `<a
  id="ref-anchor">`
* Change: anchor ref completion shows header level and text, for ref anchors shows complete
  opening tag
* Fix: optimizations that skip reformatting to be less optimistic improving wrap on typing
  results.
* Add: handling of non-wrap inline elements that cannot handle being wrapped across lines.
  Embedded spaces in these elements are now treated as non-break spaces.
* Add: reformat document action which for now formats the current paragraph.

### 1.4.7 - Bug Fix & Optimization Release

#### Basic & Enhanced Editions

- Fix: #164, PyCharm & RubyMine highlight a single space at the end of the line as Markdown
  `HARD BREAK`
- Fix: #167, NoSuchMethodError when typing text
- Fix: #169, `IndexOutOfBoundsException chars sequence.length:5, start:-1, end:5`
- Add: external annotator to reduce typing delay.

#### Enhanced Edition

* Fix: #165, Reference Images and Links split across a line boundary show as unresolved
* Fix: #166, Image Links embedded in text are not recognized as inline elements that can be
  wrapped.
* Fix: #168, Optimize wrap on typing to not reformat text block on every typed character

### 1.4.6 - Bug Fix Release

#### Basic & Enhanced Editions

- Fix: #163, NullPointerException on new files
