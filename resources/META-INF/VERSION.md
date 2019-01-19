## Markdown Navigator

[TOC levels=3,4]: # "Version History"

### Version History
- [2.7.0.60 - Bug Fix & Enhancement Release](#27060---bug-fix--enhancement-release)
- [2.7.0 - Bug Fix & Enhancement Release](#270---bug-fix--enhancement-release)
- [2.6.0 - Bug Fix & Enhancement Release](#260---bug-fix--enhancement-release)
- [2.5.4 - Bug Fix Release](#254---bug-fix-release)
- [2.5.2 - Bug Fix & Enhancement Release](#252---bug-fix--enhancement-release)
- [2.4.0 - Bug Fix & Enhancement Release](#240---bug-fix--enhancement-release)
- [2.3.8 - Bug Fix Release](#238---bug-fix-release)
- [2.3.7 - Bug Fix Release](#237---bug-fix-release)
- [2.3.6 - Bug Fix & Enhancement Release](#236---bug-fix--enhancement-release)
- [2.3.5 - Bug Fix & Enhancement Release](#235---bug-fix--enhancement-release)


### 2.7.0.60 - Bug Fix & Enhancement Release

* Fix: update for `flexmark-java` 0.40.8
* Fix: file move handler exception if only directory was moved without files.
* Fix: diagnostic/2245, IndexNotReadyException: Please change caller according to
  IndexNotReadyException documentation caused by pasting possible reference while indexing is in
  progress.
* Fix: ENTER would insert loose list item in tight list if code style setting for lists was
  `Loosen if has loose item`.
* Fix: do not highlight PlantUML images in preview as local only.
* Fix: index out of bounds when using TAB to skip trailing auto characters located at end of
  file not terminated by EOL.
* Fix: html generation embed remote images to handle SVG and gravizo images.
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

  :warning: Swing does not display `Gravizo PNG`

  :warning: JavaFX may not display `Gravizo SVG` correctly
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

  **NOTE:** stylesheet is expected to be in the same format as `COPY_HTML_MIME` stylesheet. See
  [Copy Markdown to HTML formatted Text Profile](https://github.com/vsch/idea-multimarkdown/wiki/Rendering-Profiles-Settings#copy-markdown-to-html-formatted-text-profile)

  **NOTE:** if `No Stylesheets, No Scripts` is selected then only styles explicitly defined by
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

### 2.5.4 - Bug Fix Release

* Fix: High Sierra JavaFx issue, implement JavaFX initialization as per Markdown Support.
* Add: Document Translation using Yandex.Translate and flexmark-java translation helper API,
  configured in debug settings. This is a temporary implementation which will be moved to its
  own plugin in the future.
* Fix: diagnostic/NPE on JavaFX WebView save editor state.
* Add: conversion of emoji from HTML mime copied back from Apple mail.
* Fix: paste image file name from link name would not be URI decoded.
* Add: folding of list items longer than one line of text
* Fix: #590, "Create directories and folder" does only create directory on first hit.
* Fix: #591, uncomment does not remove leading space, removed padding spaces from comment
  prefix/suffix.
* Fix: shorten toolbar by moving more rare actions to popup menus: list, table, misc and copy
* Fix: wrap on typing `>` to insert block quote level into existing block quote or using
  backspace to remove a block quote level
* Fix: wrap on typing backspace in footnote definition would replicate the first line prefix on
  continuation lines
* Fix: inserting an EOL in a list item before text matching bullet list marker or numbered would
  double the list marker on the resulting line
* Add: option for escape/unescape all numbered list lead-in of `number.` when wrapping text.
* Fix: diagnostic java.lang.RuntimeException in ImageUtils.getImageFromTransferable when pasting
  an image
* Fix: java.lang.Throwable: Invalid file: DB VirtualFile: table when caused sometimes by file
  watcher requesting markdown files be re-parsed
* Fix: diagnostic java.lang.IllegalArgumentException: Argument for @NotNull parameter 'project'
* Fix: accept license code when extra spaces are added before EOL in e-mail.
* Fix: diagnostic/ prevSibling should not be null.
* Add: option to disable gutter margin for markdown documents
* Add: option to disable line markers to main settings panel.
* Fix: file types by specific extension completion did not work
* Fix: link resolution would not find files with nested extensions like `blade.php`
* Change: toggle inline attribute when the caret is at the end of a non-space span now restores
  the caret position when applying the style instead of end of the span, inside the markers.
  More natural when inlining a word to continue typing.
* Fix: #575, Broken Spell Checker, spell checking not working on paragraph text for basic
  plugin.
* Fix: JavaFX detection with Android Studio 3.0
* Fix: #434, Spellchecker context menu is duplicated
* Add: `Use rename for spelling error intention` to allow turning off `Rename to:` spellchecking
  intention and use the regular `Change to:` intention.
* Fix: remove old storage macros

### 2.5.2 - Bug Fix & Enhancement Release

* Fix: newer API use which causes exceptions in 2017.3 IDE versions.
* Add: link and image paste/drop options to exported/imported settings management
* Fix: preview window would show links resolved after link map transformation to `http://` as
  `local only`.
* Fix: #567, '_'s are replaced by '-'s in TOC, GitHub now preserves `_` in heading anchor refs
* Fix: paste handler using 2018 api `getSelectedEditor()`
* Fix: #564, Some problems with "Copy / Drop Image" dialog. Modify image directory history drop
  down to include, in order of priority:
  * last used image directory of the current file
  * image directories of images in the current file, ordered by count of occurrence
  * image directories in the current project, ordered by count of occurrence
* Fix: absolute `http://` link from main repo to wiki page which is located in a sub-directory
  would show as unresolved
* Add: help topics for all settings panels
* Fix: improve spelling error alignment in text with many embedded inline markers
* Fix: compatibility with IDE versions 2016.3, limit since version to 163.15529
* Fix: COPY_HTML_MIME and PDF stylesheets now combine user provided attributes with defaults so
  only difference has to be added to custom CSS.
* Add: emoji images to COPY_HTML_MIME and PDF stylesheets so emoji display properly.
* Fix: wiki page file name rename refactoring. Broken since 2.4.0 release
* Fix: manually exporting a file from the toolbar now treats this as if `Export on Save` was set
  for the file.
* Fix: update GitHub wiki home link resolution for image links. Changed recently by GitHub
* Fix: trailing spaces filter behavior changed, postponed trailing spaces would all be deleted.
* Fix: reverse fix for "WebView should be available by now", was causing deadlock if
  Accessibility features were enabled.
* Fix: JavaFX preview was using project profile parser settings, not scope based rendering
  profile parser settings.
* Fix: Formatting default table column alignment when no alignment marker is specified, header
  columns are centered, body columns are left aligned.
* Add: Export to treat emoji images as image linked files.
* Fix: NoSuchMethodError for IDE versions < 2018
* Fix: image paste/modify dialog to not add _# suffix to file name if pasting on image target
  ref and on paste action is "Replace file content" for link-ref targeting an image, since the
  name should be the same as the link-ref in order to replace file content. Changing the name
  will save it under a new file and change the link-ref.
* Fix: remove old project settings handling and replace with IDE provided method. Old settings
  copied to default project settings on first plugin initialization after upgrade. Now default
  project settings support having defaults for Rendering > Profiles
* Add: Format options for Attributes:
  * space inside braces: no change, add, remove
  * space around attribute equal sign: no change, add, remove
  * attribute value quotes:
    * No Change
    * None, double, single
    * None, single, double
    * Double, single
    * Double quotes
    * Single, double
    * Single quotes
* Fix: table formatting would disable wrap on typing unnecessarily because it failed to properly
  detect table at caret offset.
* Add Table Caption formatting options:
  * Caption: no change, always add, remove if empty, always remove;
  * Caption space: no change, space around text, trim spaces check box.
* Add: #556, Default editor layout > Use last selected layout, option to have layout follow last
  editor layout change action.
* Fix: typographic `'` breaking words for spell checker generates erroneous spelling errors.
* Fix: spell checking now done across inline markup. Error underline and Change to: intention do
  not work well because of interspersed markup messing up offsets but at least spelling errors
  will be highlighted. For example `do**sn't**` will now show a spelling error because the
  effective text is `dosn't`.
* Add: history to CSS settings URI text box.
* Fix: default completion for explicit `http://` absolute links to wiki pages uses the extension
* Fix: `file://` links to wiki pages with anchor refs showed as "Only resolving locally" in the
  preview, all `file://` links show as resolving only locally.
* Fix: Admonition extension would be disabled if Attributes extension was not enabled.
* Add: Admonition parser extension.
  **[Admonition](https://github.com/vsch/flexmark-java/wiki/Admonition-Extension)**, Parser
  extension based on [Admonition Extension, Material for MkDocs] to create block-styled side
  content.
* Fix: JavaFX WebView debug page reload in Not on FX application thread exception.
* Fix: remove the "canDebug" field and replace with dynamic value.
* Fix: remove all break points on connection shutdown.
* Fix: JavaFX debugger core dumping if turning off debugging while it is paused.
* Fix: Project Default settings not being copied to new projects
* Fix: intermittent preview element highlight stopped working until page refresh
* Fix: pasting a page relative URL would be mistaken for FQN reference and always paste a link
  instead of text
* Fix: renaming rendering profile would not be saved properly.
* Fix: copy action on rendering profiles caused exception
* Add: all console commands work with Chrome dev tools.
* Fix: Chrome dev tools console evals and console logging from JavFX WebView scripts.
* Fix: #561, Scroll sync and highlight preview element broken in EAP 2.4.0.44
* Remove: FirebugLite script option for JavaFX. It never worked for debugging and Chrome Dev
  Tools work really well with JavaFX WebView.
* Add: "Toggle Editor Split Orientation" action to toggle Vertical/Horizontal split orientation
* Add: drag/drop file inside inline, fenced or indented code to insert file name.
* Add: dropping file after end of line with virtual spaces enabled, will insert spaces to fill
  virtual spaces.
* Fix: Adding explicit attribute to heading did not put space between text and attributes
  element.
* Add: file/ref anchor target search/explore intention on unresolved link anchor ref.
  * Fix: launching on an anchor and cancelling, does not show intention until file is edited.
  * Add: do a partial match for anchor when no anchors match exactly.
  * Add: filter text box to filter anchor list (show all partials, the rest hidden) otherwise
    too many in the list.
* Fix: Github collapse headers script not working in 2018.1
* Fix: intermittent position not highlighting in preview.
* Fix: Drag/Drop copy files does not save link drop options.Always resets or gets them wrong.
* Add: buttons for link and image drop/paste options in markdown settings
* Add: Updated emoji to include full set of GitHub supported ones
  * Add: option to select which shortcuts to recognize:
    * Emoji Cheat Sheet
    * GitHub
    * Both: Emoji Cheat Sheet, GitHub (in order of preference for URL generation in HTML)
    * Both: GitHub, Emoji Cheat Sheet (in order of preference for URL generation in HTML)
  * Add: option to select what type of image to use:
    * Images: image files only
    * Unicode and Images: use Unicode characters when available, image file otherwise
    * Unicode Only: only use unicode characters, don't recognize shortcuts which have no unicode
      equivalent.
  * Add: option to preview settings to replace Unicode emoji characters which have a
    corresponding image file with the image file. This allows preview browser to display Unicode
    emoji for which the browser would display unrecognized character symbol instead.
* Update to flexmark-java-0.32.2
  * Fix: java-flexmark API changes
  * Fix: java-flexmark Attributes processing changes
  * Add: Parser option for Attributes assignment to text
  * Add: Parser option for Emoji Shortcut Type, Emoji Image Type
  * Add: Preview option for replacing Emoji unicode with image
* Add: settings option to allow directories as link targets. Allows directories to be used in
  links. This functionality affects operation to completions, annotations, drag/drop link
  creation and navigation.
* Add: Drag/Drop link creation in Wiki should have wiki option for link format.
* Fix: `http://` link to wiki home without the file shows as unresolved by annotator
* Fix: change explicit to wiki not showing if link format is http:// or https:// absolute
* Fix: when converting explicit to wiki don't generate text & page ref if the explicit link text
  is the same as the file part of the target: `[Page-Ref](Page-Ref.md)` -> `[[Page Ref]]`, not
  `[[Page-Ref|Page Ref]]`
* Fix: Allow links to directories under the repo not to show them as unresolved. Create ref to
  directory object if it is under VCS
* Fix: drag/drop directories to create a link to the directory
* Fix: document format would remove table caption element
* Add: Query user for new id on explicit id to heading intention to save a step of rename
  refactoring it.
* Add: if a heading has explicit id attributes, rename refactoring for it is disabled since the
  id is not part of attributes.
* Add parser option to parse inline HTML for `<a id="...">` for anchor targets
* Fix abbreviation definition with empty abbreviation would cause an exception
* Add Option to enable/disable use of image URI query serial, used to force preview update of
  image when the image file changes. Disabled by default to reduce java image caching memory
  issues.
* Fix: custom paste handling into scratch files was not handled in CLion, possibly other
  non-Java IDEs.
* Fix: #554, Settings, Import and Copy from project do not get applied until corresponding
  settings pane is viewed. The settings would be changed but not applied until the settings pane
  was clicked on first.
* Fix: diagnostic/1159, Inserting table rows could cause an index out bounds exception
* Fix: files not under VCS root would show no completions for relative addressing, only had
  completions for `file://` format completions.
* Add: recall of the last imported settings file to make it easier to reset settings to a known
  value.
* Add: markdown Application settings to exported and imported settings.
* Fix: disable local only status for links and annotation when the link is to the file itself.
* Add: allow source/preview synchronization and search/selection highlighting in basic version.
* Fix: diagnostic/1140, NPE in flexmark-java core node renderer.
* Fix: diagnostic/1141, null editor causes exception in toolbar button test.
* Add: #549, Add settings management functionality. Now in main settings panel there is a
  "Manage..." button in top-right corner, clicking it pops up a menu with the following options:
  * `Copy to Project Defaults`
  * `Copy from Project Defaults`
  * `Export Settings`
  * `Import Settings`
  * `Reset Settings` to reset settings to default. Project defaults, current project settings
    and markdown navigator application settings.

  These actions copy from current unsaved project settings and to current unsaved project
  settings therefore you can modify settings, copy to project defaults (or export) and then
  cancel, result will be project defaults (or exported settings) having modified settings while
  project settings being those before modification.

  If you copy from defaults or import a file followed by `Cancel` then no settings will be
  modified.
* Fix: #548, When "Auto-scroll to source" is enabled in project view, markdown navigator editor
  steals focus when moving through project view with keyboard arrows.
* Fix: #542, Typographical Error in PHPStorm Preferences > Editor > Code Style > Markdown
* Add: option in settings to enable editor paste handler registration so that paste handler is
  enabled by default. Because the IDE has a lot of formatter exceptions on paste which get
  erroneously attributed to the plugin when it delegates paste action to previous handler. Now a
  notification balloon will inform of the IDE exception and offer a link to disable paste
  handler customization.
* Fix: #546, Panel is guaranteed to be not null Regression.
* Fix: #260, Add horizontal split editor option to allow preview below the text editor. Added
  option in Languages & Frameworks > Markdown: `Vertical Text/Preview Split`, default not
  selected.
* Fix: #524, Dedent shortcut not working properly.
* Fix: #539, Big local images (e.g. .gif) referred to in an open .md file get locked and cause
  merge conflicts and issues on checkout. Now swing implements disable GIF images option.
* Fix: #512, Add keyboard shortcut to `Cycle between Preview only and Editor only`. Instead
  added application setting to select text/split or text/preview toggle for the toggle editor
  layout action.
* Fix: #511, `Cycle split layout` shortcut stop working when `Preview Only` is selected.
* Fix: #527, How to use *italics* instead of _italics_ when pressing `Ctrl+I`. Option added to
  Languages & Frameworks > Markdown: `Use asterisks (*) for italic text`, enabled by default.
  When enabled italic action will use only asterisks for as markers.
* Fix: #535, Documentation for link maps and mapping groups. Documentation link added to Link
  Map settings panel.
* Fix: diagnostic/1100, start/end offset on paste beyond end of document
* Fix: clicking on a link with anchor ref by name of element would not scroll element into view
* Add: #391, #anchor tags not working. Added anchors of the form `<a .... attr=anchorId
  ...>...</a>` where `attr` is `id` or `name` to be treated as anchor ref targets. NOTE: the
  first name or id attribute will be treated as the "anchor target" the other as a reference to
  the anchor target. If both have the same string value then renaming one will rename the other.
* Fix: regex error flexmark-java attributes parser which could cause a parsing loop
* Add: parser option to not generate duplicate dashes `-` in heading ids
* Fix: fenced code content erroneously processed GitHub issue marker `#`.
* Fix: #544, Export to PDF greyed out. Editor actions would be disabled if the text editor was
  not visible.
* Add: parser options for
  * **[Attributes](https://github.com/vsch/flexmark-java/wiki/Attributes-Extension)** and
  * **[Enumerated References](https://github.com/vsch/flexmark-java/wiki/Enumerated-References-Extension)**
    parser extensions
  * Add: heading intentions to add/remove explicit id
  * Add: completions for link anchors to id attribute values
  * Add: completions for enumerated references and reference formats
  * Add: formatting options and formatting for Enumerated References
  * Add: error/unused annotations for enumerated reference, enumerated format and attribute id
  * Add: refactoring/navigation for Enumerated Reference format id's, Attribute Id's, Enumerated
    Reference link/text.
* Fix: diagnostic: 1055, sometimes virtual file == null for a PsiFile causing an exception.
* Add: option to add serial query suffix to CSS URI which increments when the css file changes
  (only file:// URI's and document relative URLs are supported.)
* Fix: diagnostic 1030, when bread-crumb provider steps up to file level while looking for
  headings.
* Fix: diagnostic: 1032, sometimes an exception is thrown "AssertionError: Unexpected content
  storage modification"
* Fix: diagnostic 1033, paste handler exception `IllegalStateException: Clipboard is busy`
* Fix: diagnostic 1035, null pointer exception in Swing preview when image tag has no `src`
  attribute.
* Fix: diagnostic 1047, sometimes an IOException is generated if markdown sub-type is requested
  during indexing operation.

### 2.4.0 - Bug Fix & Enhancement Release

* Fix: #517, Invalid tool tip for "Show Breadcrumb text"
* Change: #520, Not working: As you type automation: Double of bold/emphasis markers and remove
  inserted ones if a space is typed. Enable these options in code style, disabled by default.
* Fix: #509, Text with colons is incorrectly interpreted as an invalid emoji shortcut
* Add: #507, How to be sure that HTML auto generated link will have unchanged url. Link format
  option for HTML export: page relative, project relative, http: absolute, file: absolute if
  option `Link to exported HTML` is not selected.
* Add: #466, Indents with 4 spaces instead of 2 as I like. Code style option for indent size
  added sets number of spaces to insert when pressing tab.
* Change: Remove attribute and settings migration from pre 2.3.0 versions.
* Add: nested heading outline collapsing
* Fix: improved HTML to markdown conversion from Apple Mail copied text.
* Fix: don't show emoji completions in link address part ( http: triggers it)
* Fix: abbreviation navigation and refactoring was not implemented
* Fix: line markers generate for leaf elements only, performance improvement
* Fix: swing preview on linux not showing fixed pitch font for code
* Fix: Task list items now require indent at task item marker not item content, to match GitHub
  parsing rules. Indenting to content column treats children as inline code and child list items
  not separated by a blank line as lazy continuation lines.
* Fix: formatter for new task item indentation rules.
* Fix: remove `Replace File Content` option from non-image target ref drop downs in paste/modify
  image dialog, and all link options from copy/drop image dialog and link drop/paste ref options
  dialog.
* Fix: #489, Paste Image does not create file if parent directory does not exist.
* Fix: #484, Open links in preview, not browser. Option added to preview settings to have page
  relative and repo relative links resolve to GitHub files when selected. When not selected they
  resolve to local project files.
* Fix: #486, Multi-line links do not preview correctly, when in `Line` preview element highlight
  mode.
* Fix: #481, Will not allow me to crop beyond 200px, now limits are derived from the image
  dimensions and image operations.
* Fix: Update to latest flexmark-java supporting CommonMark Spec 0.28.
* Fix: TOC entries would exclude typographic characters when "text only" option was used with
  typographic parser extension enabled.
* Fix: HTML to Markdown not adding HTML comment between consecutive lists
* Fix: #479, Multi-line URL images are not converted in PDF export or Copy HTML Mime
* Add: Show "Apply all '...'" intention on any element option to allow showing file level
  intentions to be available on any element. Otherwise only shown on elements which they affect.
* Add: enable image intentions on multi-line URL image links
* Add: Code Folding option in settings for embedded image links
* Add: HTML generation options to convert image links to embedded images, with separate option
  for http:// and https:// image urls.
* Add: base64 embedded image display in Swing Preview browser
* Add: `Base64 Encoded` as a link format for pasted images and dropped files to the Paste Image
  dialog.
* Fix: base64 encode intention would keep path of url if it was present
* Fix: image reference links to references with base64 encoded images would show as unresolved
* Add: intentions to convert images to base64 encoding and vice-versa
* Fix: base64 encoded embedded images did not display in JavaFX preview
* Fix: preview navigation to links with anchor refs and line anchor refs
* Fix: dropping a file in a document appends `null` string to the file name in error.
* Fix: #468, Move (refactoring) of .md files breaks links to sections in same file.
* Fix: reference paste with line ref anchor would always paste page relative format URL
  regardless of the link paste format (set with file copy drop action).
* Fix: diagnostics/713, tree view icon update before `FileManager` has been initialized will to
  return markdown file type (without resolving sub-type).
* Add: Convert markdown to HTML intention for fenced code and indented code blocks.
* Fix: unresolved link references would be rendered in HTML instead of being treated as plain
  text. Broken by `Reference` link map code.
* Fix: paste reference past end of line will insert spaces to caret column before inserting
  link.
* Fix: links from FQN references with spaces did not url encode the link.
* Fix: reference to link conversion for PhpStorm to truncate the reference at the `:` since
  PhpStorm is not able to convert FQN strings with class method names.
* Add: use QualifiedNameProviders to resolve reference to link conversion.
* Add: logic to not convert qualified string to link when caret is inside inline code, fenced
  code or between two back-ticks.
* Fix: HTTP links with anchor refs should not highlight anchor links as unresolved.
* Add: paste of file reference with or without line number converted to paste of link with
  GitHub line ref anchor added if line number is part of the reference. This will insert/replace
  link.
* Fix: non-vcs projects links without a path would show unresolved even when files exist.

### 2.3.8 - Bug Fix Release

* Add: GitHub Line reference anchors in the form `L#` or `L#-L#` for line ranges. Now navigating
  to such an anchor in a project file will move the caret to the line and if second form is used
  select the lines.
* Add: with JavaFX browser clicking on task item box in preview toggles open/closed task status
  in source.
* Fix: image refs and image links to non-raw GitHub image files to show as warning. Only show
  warning for references not in raw when referenced by image refs.
* Add: Apply all '...' in file intentions where these make sense.
* Add: intention to convert between typographic symbols and markdown smarts/quotes extension
  text.
* Add: `HTML block deep parsing` parser option to allow better handling of raw text tag parsing
  when they are not the first tag on the first line of the block.
* Add: split inline code class `line-spliced` for code elements split across multiple lines not
  to appear as two inline code elements in preview.
* Fix: HTML generation with line source line highlighting when inline styling spans source lines
* Add: #74, Launching external URLs inside the browser, now `navigate to declaration` opens url
  in browser, ftp or mail client depending on the link. Can also use line markers for navigation
  of these elements.
* Fix: parsing of lists in fixed 4 spaces mode would not allow last item to be loose
* Fix: reference to non-image but not used as image target warning not raw.
* Fix: exception when navigating next/previous table cells in editor without an associated
  virtual file.
* Fix: #461, TOC with HTML generated content causes exception if skipping heading levels
* Fix: #460, TOC options do not change default Heading level
* Fix: #459, PDF export does not resolve local ref anchors
* Fix: #456, Register r markdown code chunk prefix
* Fix: #453, Option to hide toolbar
* Fix: #454, Incorrect filename inspection error, weak warning now only for wiki link targets
  that contain spaces in resolved link.
* Fix: flexmark-java issue 109, image ref loosing title tag.
* Add: GitBook compatible include tags when `GitBook compatibility mode` is enabled in `Parser`
  options.
* Fix: Nested stub index exception in reference search
* Fix: breadcrumb tooltip of task items would be missing the task item marker
* Fix: completions broken on Windows
* Fix: document format erroneously creates column spans for some tables.
* Fix: diagnostics/531, line painter provider passed line number > document line count.
* Fix: diagnostics/498, highlight in preview causing exception
* Fix: diagnostics/497, flexmark-java lib erroneous assert failure
* Fix: #447, Exported HTML has unexpected CSS and JS URLs
* Fix: #445, there should no be default language injection in bare code chunks
* Add: handling of optional quotes for jekyll include tags. Either single `'` or double `"`
  quotes will be ignored if the file name is wrapped in them.
* Fix: API break with version 2016.2.3 by using EditorModificationUtil methods missing from that
  version.
* Fix: #444, Markdown Navigator 2.3.7 breaks paste of github checkout url
* Fix: #441, false positive typo annotation in header, caused by using IdentifierSplitter
  instead of TextSplitter to handle elements that can have references.
* Fix: #442, Image Paste in Windows always pastes absolute file:// regardless of selection
* Add: Insert table column on right actions and changed description of previous action to insert
  table column on left.
* Fix: exception when exporting PDF or Copy HTML Mime
* Fix: #440, Auto links should not appear in fenced code
* Add: #411, Network drives links are not resolved correctly, URI links outside of project now
  error highlighted if the file does not exist
* Add: #433, Support external links for the Link Map (eg. JIRA link), Reference to Link Map to
  allow creating automatic reference URLs from Reference IDs

### 2.3.7 - Bug Fix Release

* Fix: parser erroneously processing escape `\` is encountered in fenced code and causing a
  parsing exception.

### 2.3.6 - Bug Fix & Enhancement Release

* Fix: intermittent index out of bounds exception if document is edited after parsing but before
  AST is built.
* Fix: #438, Markdown Syntax Change looses TOC element in source
* Add: annotation to detect when list syntax is set to GitHub
* Fix: #432, Add a way to disable the startup notification
* Fix: #436, Header link results in bad Table of Contents entry formatting
* Fix: #411, Network drives links are not resolved correctly, for `file://` which is outside the
  project and any module directory structure.
* Fix: NPE in settings under rare conditions
* Fix: assertion failure in settings under rare timing conditions
* Fix: paste NPE when pasting into link with empty address
* Fix: drag/drop without copy modifier of image files uses last non-image link format instead of
  last used image link format.
* Fix: diagnostic id:208, invalid virtual file in line painter
* Add: option to break definition list on two or more blank lines
* Fix: #428, Lack of encoding declaration when exporting in html
* Add: Global page zoom for JavaFX preview in application settings so that project preview zoom
  does not need to be changed when project is opened on a machine with different HIDPI. Now can
  leave project zoom to 1.00 and change global zoom to desired value.
* Fix: #426, Cannot add images from clipboard or drag and drop under Windows
* Fix: Setext heading to not show heading id on marker line
* Add: #425, Add Heading anchor ID display in editor
* Fix: #424, NoClassDefFoundError in WS 2017.1
* Fix: #421, NoSuchFieldError on startup after upgrading plugin on IDEs version 2016.1
* Fix: image link from non-wiki page to image in wiki would show as unresolved by annotator when
  it was resolved by line marker and preview.

### 2.3.5 - Bug Fix & Enhancement Release

* Fix: #420, java.lang.IllegalStateException: node.treeNext must not be null.
* Fix: do not un-escape HTML entities in HTML, let the browser handle that.
* Fix: #419, Bread crumbs broken when running in 2017.1
* Fix: licensed features highlight now full balloon notification.
* Fix: detection when containing file and target file of a link are not under the same VCS root
  when the containing file is in a sub-directory of target VCS root but has its own root.
* Fix: #416, NPE version 2.3.4 (w/license)
* Fix: #415, Setting default right margin in code style markdown settings disables wrapping
* Fix: #414, Exception when starting IDEA
* Fix: do not hide wrap on typing and table auto format buttons even when these are disabled.
* Fix: drag/drop image files should only show copy dialog if no drop action information or it is
  a drop copy action
* Add: plugin exception reporting to `vladsch.com` without effort.
* Fix: wiki to main repo links would not resolve. Erroneously treated two vcs repos as separate.
* Fix: clipboard mime text/html now has higher priority than file list and image on the
  clipboard.
* Add: operation options for non-image drop/paste file based on caret location
* Add: `Copy Modified Image to Clipboard` in Copy/Paste Image Dialog to replace clipboard image
  contents with modified image, can use it to replace image on clipboard then Cancel dialog to
  not modify the Markdown document but still have the modified image on the clipboard.
* Add: Copy/Modify Image intention that will open the Image Copy/Paste Dialog for the image
  content of a link element at caret position. Works with local files and URLs. Get the option
  to change directory, file name and modify the image.
* Fix: `http://` and `https://` addresses to project files would be ignored due to a typo in the
  code.
* Fix: update to flexmark-java 18.2, HTML to Markdown hang fix and MS-Word and MS-Excel HTML
  quirks handling fixed.
* Fix: link resolution logic to work for multi-vcs-root projects and modules not under project
  root.
* Fix: update to flexmark-java 18.1, HTML to Markdown adds space after empty list items.
* Add: Markdown application settings for:
  * `Use clipboard text/html content when available` disabled by default, enabling it will allow
    pasting text/html when available
  * `Convert HTML content to Markdown` enabled by default, disabling will paste text/html
    content without conversion to Markdown
* Add: `Delete empty list items` intention on lists to delete all empty list items
* Fix: HTML to Markdown converter to not ignore text in lists which is not included in list item
  but instead to put this text into a new list item.
* Add: aside extension which uses leading pipe `|` to mark an aside block just like block quote
  uses leading greater than `>` to mark a block quote element
* Add: pasting file list into markdown document inserts links the same as dropping files with
  copy action.
* Add: confirmation dialog when original markdown file is going to be overwritten with
  transformed content when pasting file list or drag and dropping files.
* Fix: absolute `http://..../wiki` link to wiki home page would to resolve as a file reference.
* Fix: drag/drop wiki page files would ignore link address format and always insert page
  relative link.
* Fix: style auto wrapping when caret at end of word that is at end of file without trailing
  EOL.
* Add: future API for drag/drop handling code to eliminate the need for replacing editor
  drag/drop handler.
* Add: highlight selection in preview, `Show source selection in preview` enabled by default.
  Wraps selection in `<span>` with `selection-highlight` class.
* Add: #399, Highlight search results in preview, `Show source search highlights in preview`
  enabled by default. Wraps highlights in `<span>` with `search-highlight` class.
* Fix: text drag/drop not working because of MarkdownPasteHandler
* Add: option to enable drag/drop handler replacement to allow "Copy" extended file drag/drop
  action at the expense of text drag/drop. Disabled by default in settings `Languages &
  Frameworks > Markdown`
* Fix: loosen/tighten list action to not mark a list as loose when blank line precedes the first
  list item.
* Fix: #404, Conversion from CommonMark or FixedIndent to GitHub does not properly indent code
  blocks in list items
* Fix: #403, Indented code in list items not indented enough with GitHub list parser option
* Change: link color in Preview and Editor Colors to match new GitHub colors
* Fix: #400, Better code color consistency needed. Now same as Fenced Code/Verbatim. Also change
  copy Markdown as HTML formatted text and PDF export to align inline code color with indented
  and fenced code.
* Fix: #398, Poor alignment between source and preview when using "Sync source to preview". Now
  there is an option to vertically align synchronized position in Preview Settings, selected by
  default.
* Fix: #402, PDF Export action fails silently if no text is selected in document instead of
  exporting the full document.

[Admonition Extension, Material for MkDocs]: https://squidfunk.github.io/mkdocs-material/extensions/admonition/
[html_mime_default.css]: https://github.com/vsch/idea-multimarkdown/blob/master/resources/com/vladsch/idea/multimarkdown/html_mime_default.css
[holgerbrandl/pasteimages]: https://github.com/holgerbrandl/pasteimages

