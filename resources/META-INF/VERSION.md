## Markdown Navigator

[TOC levels=3,6]: # "Version History"

### Version History
- [To Do](#to-do)
- [2.4.0.56 - Bug Fix & Enhancement Release](#24056-bug-fix-enhancement-release)
- [2.4.0 - Bug Fix & Enhancement Release](#240-bug-fix-enhancement-release)
- [2.3.8 - Bug Fix Release](#238-bug-fix-release)
- [2.3.7 - Bug Fix Release](#237-bug-fix-release)
- [2.3.6 - Bug Fix & Enhancement Release](#236-bug-fix-enhancement-release)
- [2.3.5 - Bug Fix & Enhancement Release](#235-bug-fix-enhancement-release)
- [2.3.4 - Bug Fix & Enhancement Release](#234-bug-fix-enhancement-release)
- [2.3.3 - Bug Fix & Enhancement Release](#233-bug-fix-enhancement-release)
- [2.3.2 - Compatibility & Bug Fix Release](#232-compatibility-bug-fix-release)
- [2.3.1 - Compatibility & Enhancement Release](#231-compatibility-enhancement-release)
- [2.3.0 - Compatibility & Enhancement Release](#230-compatibility-enhancement-release)
- [2.2.0 - Compatibility & Enhancement Release](#220-compatibility-enhancement-release)
- [2.1.1 - Bug Fix & Enhancement Release](#211-bug-fix-enhancement-release)
- [2.1.0 - Bug Fix Release](#210-bug-fix-release)
- [2.0.0 - New Parser Release](#200-new-parser-release)


### To Do

* [ ] Add: when typing in the text field for change link to reference, automatically enable the
      add reference text if reference id is different from original
* [ ] Add: transpose table, best to add `copy to clipboard transposed table`
* [ ] Add: join processor to remove bullet list marker when joining next line item
* [ ] Fix: cursor navigation very slow in table with few rows but very long text in columns: see
      `Extensions.md` in `flexmark-java` wiki. Suspect is figuring out table context for toolbar
      button state update.
* [ ] Fix: When pasting text that contains ref links over a selection that already has these
      references, after the paste the references are deleted but new ones are not added. Put a
      check if possible to ignore any existing references in a selection since they will be
      deleted by the paste.
* [ ] Fix: can't modify PSI inside on save listener.
* [ ] Add: GitHub links should offer the same change relative/http: intention as the rest of the
      links.
* [ ] Fix: Link Map
  * [ ] implement `ExpandedItemRendererComponentWrapper` for table cells so that the extended
        tooltip does not hide an error tooltip.
* [ ] Add: ability to move a lookup-up to the start of an element's location so that completions
      for emoji shortcuts and links located in heading elements can be properly aligned.
* [ ] Fix: take a look at the toolbar implementation to see if it can be made to put in a drop
      down for buttons that don't fit.
* [ ] Add: source synchronization for Swing preview window
* [ ] Add: source synchronization for HTML plain text previews
* [ ] Add: detection for **GitHub** issue completions when no task servers are defined.
* [ ] Add: transpose table, best to add `copy to clipboard transposed table`
* [ ] Add option to configure a directory as a GitHub special link: ie. `issues` would treat
      files in that directory as issues, so that links would resolve in them the same way they
      do in a GitHub issue comment.
  * [ ] optionally convert to "Hard Wraps" mode on copy from these issue files so the text can
        be wrapped in IDEA and pasted as unwrapped into an issue. Effectively, remove any
        soft-breaks before copying to the clipboard.
  * [ ] option to display a list of issues or pull in an issue as a file in an issues directory
        for editing and responding.
  * [ ] Figure out a multi-part markdown format that would work well for displaying an issues
        thread. No hierarchy. Just the facts and user names. See if can intersperse editable and
        non-editable parts in one file. Would be nice to have text and preview of the issues in
        split editor.
    * [ ] add ability to select and post the selection/current editing part to the issue that
          this file represents to save on the app switching and clicking and cut/pasting.
    * [ ] See if there is a plugin to integrate GitHub issues into IDEA sort of like Bee for mac
          but more IntelliJ friendly and Markdown Navigator editing friendly.
  * [ ] handle GitHub `#`, `@` and other short cut links. Preferably via completions the way it
        is done on GitHub.
* [ ] Add flexible link resolvers
* [ ] Add: option to mark a directory as representing GitHub repo issues link. This will allow
      to create markdown for issues with the right relative links.
* [ ] Add: ability to display GitHub issue list for importing as files into issues directory
* [ ] Add: option to push a file as a new issue or a reply to an existing one
* [ ] Add: view of github issues as a tool window with 'edit' or 'reply' options that open a
      markdown editor window in the tool window to have all comforts of IDEA for GitHub issue
      editing. Make preview split vertical by default. Allow multiple tabs so more than one
      github repo's issues could be displayed in a project.

#### Cleanup up consistency of new features

* file/ref anchor target search/explore intention on unresolved link anchor ref.
  * [ ] Add: clicking on an anchor or any anchor target should update the file anchor list
        selection.
  * [ ] Add: add highlight of selected anchor target. Editor identifier highlight only works if
        caret is moved.
* [ ] Add: intention to view markdown as flexmark-spec-example with HTML and AST results in a
      regular markdown editor. A button to copy to clipboard or insert into the file. Later add
      modules and options configurable in the dialog. For now just use the current file's
      flexmark options.
* [ ] Add: code style settings and editor colors to settings import/export for markdown.
* [ ] Fix: for debugging only point links to debug URL server if the file actually exist on it,
      otherwise use the default URL.
* [ ] Fix: Rendering profiles, validate export HTML and HTML preview in javafx, seems
      inconsistent when exporting file with embedded css text.
* [ ] Add: copy resources button to debug pane, to copy jar resource files to directory for
      debugging.
* [ ] Fix: add spellchecking intention for Markdown that handles embedded inline markup in text
      and provides proper underlining and change to: intention actions.

#### High Priority

* [ ] Fix: wiki links from main repo don't resolve if the file is in a sub-directory and access
      is without extension or sub-directory. (Was probably broken when non-github rules were
      added to resolver).
* [ ] Fix: conversion from raw explicit link to wiki page looses the raw reference and results
      in rendered markdown reference
* [ ] Add: change logic for exported link format to use the link format for non-exported files,
      even when "Link to exported HTML" option is selected.
* [ ] Add option to disable images in preview for files that have more than N images on typing
      and re-enabling them after M ms. With N & M user definable, to address update delay in
      files with many images causing typing delay.
* [ ] Add inspection for improper indentation of sub-items, where they are indented more than
      the previous item but not enough to be its child.

##### Format Element/Document

* [ ] Handle multiple attributes per heading and take last? first?, figure out what flexmark
      does with the id attribute.
* [ ] Do not extend setext heading markers on trailing attributes
  * [ ] Format
  * [ ] Intentions
  * [ ] On typing
  * [ ] Refactoring
* Format as you type
  * [ ] Fix: splitting a list item when the caret is on a "list marker" causes double list item
        to be added. Check if what follows the caret is a "list marker" and do not insert one.
  * [ ] Fix: typing spaces at paragraph start does not insert space to let the paragraph be
        indented to next level
  * [ ] Fix: inserting block quote marker before block mark gets confused

### 2.4.0.56 - Bug Fix & Enhancement Release

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
    * No Change,
    * None, double, single preferred,
    * None, single, double preferred,
    * Double, single preferred,
    * Double quotes always,
    * Single, double preferred,
    * Single quotes always,
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
  [Admonition Extension](https://github.com/vsch/flexmark-java/wiki/Extensions#admonition) To
  create block-styled side content.
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
* Fix: #548, When "Autoscroll to source" is enabled in project view, markdown navigator editor
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
  [Attributes Extension](https://github.com/vsch/flexmark-java/wiki/Extensions#attributes) and
  [Enumerated Reference](https://github.com/vsch/flexmark-java/wiki/Extensions#enumerated-reference)
  of flexmark-java.
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

### 2.3.4 - Bug Fix & Enhancement Release

* Fix: incorrect parsing in GitHub Doc compatibility mode, #401, Parser fails if code chunks
  contain markdown header elements
* Add: Link Format option to `Paste Image` and `Copy Image` dialogs
  * Page Relative
  * Repo Relative (`/...`)
  * Absolute (`http://`)
  * Absolute (`file://`)

  ![ScreenShot_PasteImages_sm](/assets/images/ScreenShot_PasteImages_sm.png)
* Add: Link Format option to drag & drop non-image files if copy dragging (Ctrl on
  Linux/Windows, ⌥ on OS X) dialog is presented before the first file link is created and the
  same format is used for the rest of the files.
* Add: File drag and drop into markdown document to create links, image files create image links
  the rest create inline links. Each file link will be added as a separately undoable command.

  If copy dragging images (Ctrl on Linux/Windows, ⌥ on OS X) then for image files the `Copy
  Image` dialog will open (like paste image) that will allow you to modify the image and choose
  the location where to store the copy.
* Fix: #394, NullPointerException in MarkdownPasteHandler.doExecute()
* Fix: references to images in the project from wiki home page with `file://`, `http://` or
  `https://` format would show as unresolved and conversion. Also affected conversion of link
  format on wiki home page.

### 2.3.3 - Bug Fix & Enhancement Release

* Add: change mouse cursor to cross hairs when over preview image in paste image dialog
* Add: outline to center pixel zoomed in image
* Add: cropping options to paste image dialog
* Add: color selection with tolerance to make transparent
* Fix: change directory history for paste image dialog to display project directories without
  project base path prefix and store them as system independent.
* Fix: image link not correct when pasting image in non-wiki document with actual image file is
  put in wiki
* Add: image scaling with interpolation type to paste image dialog
* Add: paste of images directly into markdown files as image links. Pasting with a caret on an
  existing image link will set the defaults in the paste image dialog from the link.
  Functionality implemented thanks image utils library from [holgerbrandl/pasteimages].
* Fix: #390, `Copy Markdown as HTML formatted` action is disabled unless there is a selection in
  the document.
* Fix: #389, Null Pointer Exception on in toolbar disabled button handling.
* Add: suggestion for file rename refactoring replacing spaces with dashes
* Fix: convert link format intention would be disabled for some image links
* Add: pdf export customizable profile, defaults to `COPY_HTML_MIME`
* Add: pdf export font family drop down for built in fonts
* Fix: toggle style would not wrap the previous word if caret at end of document without a
  terminating EOL.
* Add: max image width and default font family settings for use by Copy Markdown to HTML
  formatted and Export to PDF actions
* Add: img style `max-width:100%` to HTML mime copy and `max-width:504pt` PDF export to scale
  images instead of clipping them.
* Add: JavaFX WebView state persistence through `window.__MarkdownNavigator.getState("name")`
  and `window.__MarkdownNavigator.setState("name", value)`
* Add: #388, JavaFX script for GitHub Collapse Markdown script use state persistence for
  initializing the open/close state of headings. Now collapsed heading are preserved between
  page refreshes and when opening markdown files.
* Fix: paragraph spacing in `html_mime_default.css`
* Add: PDF export action, menu and toolbar item
* Fix: highlight on scroll to only exclude typing and backspace edit actions if Highlight on
  edit is disabled.
* Fix: Swing preview paragraphs not missing inter-paragraph spacing
* Fix: add parser option to turn off GFM treatment of list item as loose if blank line follows
  item paragraph. This is incompatible with GitHub rendering but gives better control of when an
  item is loose.

### 2.3.2 - Compatibility & Bug Fix Release

* Add: #385, toolbar button tooltips to show reason for button being disabled.
* Fix: #384, Add block quote level on ATX heading looses the heading prefix
* Fix: Markdown list indentation format conversion would loose blank lines.
* Fix: HTML to Markdown intention would only take the opening inline HTML tag
* Fix: #383, Absolute `http://` links to wiki pages do not resolve to files
* Fix: #382, JavaFX Preview is using project settings for Zoom and Grey Scale Antialiasing
  instead of scoped rendering profile
* Fix: update to flexmark-java 0.14.0 with typographic quotes and smarts conversion
* Fix: #379, HTML block content indent doubling on document format
* Fix: #378, Leading spaces on reference definition after the first would cause the reference to
  be processed as text.
* Fix: copy markdown as html to use the current file's rendering profile as the parent for
  COPY_HTML_MIME so that any settings not marked as overriding project settings will be as per
  rendering profile for the file.
* Fix: typing a character right after a list item marker would sometimes insert a space after
  the character.
* Change: #380, Sorting of non-rendering elements should not be case sensitive
* Add: annotation and quick fix for GitHub Doc parsing issue of `- [ ] [reference]` being
  interpreted as `- [ ][reference]` which is a list item with a link reference with explicit
  link text of a space.
* Add: #377, Add Format conversion between list indentation types for files.
* Fix: #376, HTML to Markdown converter does not handle pre tags with inner span tags. HTML to
  Markdown to properly handle conversion to fenced code when HTML consists of `<pre>` and
  content is `<span>`'s with color and highlighting information.

### 2.3.1 - Compatibility & Enhancement Release

* Fix: #374, EAP Plugin version Find Usages related functionality generates exception in 2016.3
  and prior
* Change: inline code completion for Java now second invocation does simple completion and third
  does fully qualified names.
* Add: toolbar button for underline, subscript, superscript
* Update: emoji shortcuts and icons to latest Emoji Cheat Sheet version and refactor code to use
  flexmark-java emoji extension data.
* Add: Automatic mime `text/html` conversion to markdown on paste in Markdown documents.
* Add: Markdown To Html intention action to convert some markdown elements like:
  * Tables
  * Image Links
  * Definition Lists
* Add: Html to Markdown intention action to convert html blocks to Markdown
* Fix: `NoClassDefFoundError` when running in PhpStorm for flexmark-java-plugin code
* Fix: toc refactoring was not reflecting raw title string
* Add: annotation and quick fix for TOC with empty title string. GitHub does not accept this so
  an empty title needs at least one space in the title string of the TOC element.
* Add: adding markdown referenceable elements such as footnotes, references and headings from
  files included through Jekyll include tags
* Add: Jekyll include tag parsing and processing the include, if markdown will be parsed to HTML
  and rendered in the view, if not markdown will be included in the HTML
* Add: flexmark-java Inline Parser Extension option to module defaults
* Fix: #365, PhpStorm - Highlight preview element settings does not persist between sessions
* Fix: enable emoji completions if completions are invoked after a single `:` with the caret
  right after the colon or without spaces or tabs between colon and caret position.
* Fix: enable GitHub issue completion when completion invoked with only digits between caret and
  preceding #.
* Fix: #363, Markdown aware trailing space stripping is not working in 2017.1 EAP
* Fix: #362, Java exception in version 2.3.0 with Pycharm 2016.1.5
* Fix: #361, HTML Export "CSS dir" and "script dir" not saved
* Fix: #360, Auto-formatting creates list by accident, added two code style settings for text
  wrapping options for `Special Characters` which are `*+-:~>#` and digits followed by `.` or
  `)` for CommonMark, enabled by default:
  * `Escape when wrapped to start of line`
  * `Unescape when wrapped away from start of line`
* Fix: #358, "Unresolved link reference" errors for `gh-pages` and `releases` where branch is
  expected. Now only `blob/` and `raw/` are attempted to be resolved, the rest are treated as
  external unchecked links.
* Fix: HTML export was not working on Windows due to missed system dependent/independent
  conversions.
* Fix: #359, "Reformat Document" inflates files with link references, footnotes would double
  their text content output
* Fix: "Jekyll front matter" inspection to force re-parse of files after parser option is
  enabled.
* Add: `PARSE_JEKYLL_MACROS_IN_URLS` option for parser when Jekyll Front matter parsing is
  enabled to allow parsing of jekyll macros in urls with spaces between braces.
* Fix: fenced code that is adjusted for indentation should keep content indent relative to other
  content unchanged.
* Fix: sub-list after a paragraph needs a blank line, it should not be considered a loose list.

### 2.3.0 - Compatibility & Enhancement Release

* Add: annotation and quick fix when an empty list item needs a space after item marker.
* Add: annotation and quick fixes when possible list items are found in paragraphs and indented
  code when list indentation rules are set to CommonMark or GitHub because incorrect
  indentation.
* Fix: fenced code and indented code would loose indentation prefix when formatting document
* Fix: list items whose first element is a heading, indented code or fenced code now add a line
  break when parser compatibility set to other than CommonMark to allow GitHub to properly
  process these list items.
* Add: Code Style option for spaces after definition item marker, default 3 for greater
  compatibility with various markdown processors.
* Add: Code Style option to insert a blank line before a list item if it is not preceded by
  another list item, disabled by default and formatting a document will create maximum
  compatibility with markdown processors that limit list item interpretation when not preceded
  by a blank line. When disabled formatting a document may still add a blank line before a list
  if the current list processing type requires a blank line before a list.
* Add: Annotation and quick fix if first list item needs to have a blank line before it to be
  properly parsed as a list. List indentation type dependent and for CommonMark list item type
  and numeric sequence dependent.
* Fix: #349, Show version number on plugin configuration screen, moved version number. Now more
  prominently displayed at the top of the main panel next to update channel selection.
* Fix: #348, IDE error when switching to Default JavaFX Stylesheet, added a validation for
  browser/stylesheet combinations so that invalid restored editor state would be corrected.
* Fix: #347, Stops rendering on PHPStorm, added a validation for split editor setting and
  preview type so that if restored editor setting is not editor only and preview set to NONE
  then preview will be changed to PREVIEW.
* Fix: #352, Files without suffix are not resolved in links, also show them in completion. These
  files are assumed to be Text files
* Add: Parser extensions:
  * Inserted: `++inserted text++` results in underlined text
  * Subscript: `~subscript~`
  * Superscript: `^superscript^`
* Add: Actions to navigate table cells: next/prev table cell with and without selection to stop
  at beginning/end of cell and start/end of table row
* Fix: List syntax dependent list item action behavior.
  * Fix: indent/un-indent for other than fixed 4 has to re-indent child items to the parent's
    new indent level. Otherwise parsing of the children will be off. Right now works only for
    fixed4
  * Add: flexmark option to recognize empty list sub-items option to PARSER purpose.
  * Fix: psi list item prefix reporting to match fixed4, github and CommonMark list processing
    settings.
* Add: List syntax dependent list format behavior.
  * Fix: list indent for nested items should not indent to more than (listLevel)*4 + 3 in fixed
    4 mode
  * GitHub enforces styleSettings.LIST_ALIGN_CHILD_BLOCKS and has no maximum for content offset.
  * CommonMark enforces styleSettings.LIST_ALIGN_CHILD_BLOCKS and has a maximum for content
    offset beyond the marker end of 4 characters before the content is treated as indented text.
* Fix: indent/un-indent empty task item adds extra blank line after it on every action, on first
  action caret at start of next line
* Change: `StripTrailingSpacesSmartFilter` to use the abstract class instead of an interface.
* Add: parser profile added to functions handling formatting and prefix generation. Now this can
  vary significantly from one parser family to another.
* Fix: when ENTER deletes a list item prefix inserted extra blank line
* Fix: Un-indent item action leaves leading indent if it was aligned to parent's left text edge.
* Fix: SimToc requires default settings option so that rendering will reflect project settings
  not defaults of flexmark-java SimToc extension.
* Fix: HRule would span the whole line even when it was not the first element on the line
* Remove: smart asterisk, underscore and tilde handlers and options.
* Fix: edge formatting cases when list item marker is immediately followed by a block element,
  then the elements are left on the same line and no extra blank lines are added:
  * thematic break
  * atx heading
  * setext heading
  * fenced code
  * verbatim code
  * block quote marker
* Fix: edge formatting cases when list item marker is immediately followed by a block element,
  then the element is put on its own line:
  * list item marker
* Add: parser settings parameter to prefix related functions to use when prefix limits are
  defined by content indent not fixed indent
* Fix: Swing preview HTML table did not render correctly. Swing CSS used `tbody` and `thead`
  which Swing browser does not support. Now generating different class for `tr` tags depending
  on whether they are `thead` or `tbody` rows.
* Add: format option to sort task items based on their done/not done status:
  * `No Change`: leave all as is
  * `Incomplete first`: put incomplete tasks first, followed by the rest
  * `Has incomplete first`: put incomplete tasks and list items that contain incomplete tasks
    first followed by the rest
  * `Incomplete first, complete to non-task`: put incomplete tasks first, followed by the rest
    and convert complete tasks to non-task items.
  * `Has incomplete, complete to non-task`: put incomplete tasks and list items that contain
    incomplete tasks first followed by the rest and convert complete tasks to non-task items.
* Fix: task list item content indent based parsing was broken in flexmark-java.
* Fix: all inline toggling actions to remove markers if caret is between markers and no
  intervening text, as occurs when toggle action was just used to inserted markers.
* Add: all inline toggling actions take punctuation characters that they will not wrap by
  default if caret is on them or the current word to wrap ends on them. Default punctuation
  symbols in settings: `.,;:!?`. If the caret is right after one of them then default behavior
  is to wrap the word immediately before the punctuation characters.
* Change: Added option to Main Settings `Inline code toggle like other text style actions`
  change inline code action to work just like bold, italic and strike through, instead of
  continuously adding back ticks when at the end of word.
* Fix: references or links to `raw/master/*.png` showed as unresolved when on wiki home because
  only image links would handle the optional wiki prefix from home page for image files.
* Fix: backspace at end of file after `# ` did nothing.
* Fix: Header marker equalization was broken.
* Fix: CSS when task list item was first level, bullet sub-item spacing was messed up
* Fix: when inserting list item above in a loose list, added a blank line right after the first
  line of the next item, even if the item has more than one line of text. Should not add blank
  line after the next item at all.
* Add: surround live templates for:
  * fenced code,
  * collapsed sections,
  * @formatter:off / @formatter:on comments
* Add: Markdown context for Live Templates
* Fix: Table body and head should not use node text for breadcrumb. Row does that causing double
  breadcrumb text to appear.
* Add: definition lists implementation from flexmark-java
* Fix: format document did not preserve block quotes on fenced code
* Change: image links to `http://github.com/user/project/blob` are now always flagged with a
  warning regardless of whether they are part of an image link or reference.
* Add: `<html></html>` wrapper tags to `JavaFxHtmlGenerator` and `SwingHtmlGenerator`
* Add: `NO_FILE_EOL` to flexmark example options as built-in option
* Add: updated to flexmark-java 0.9.0, added subscript/superscript/ins extensions to parser
  options. Can be used with Jira, Copy HTML mime
* Add: Toc options for table of contents list generation:
  * hierarchy: as before hierarchical list of headings in document order
  * flat: flat list of headings in document order
  * reversed: flat reversed list of headings in reverse document order
  * increasing: flat, alphabetically increasing by heading text
  * decreasing: flat, alphabetically decreasing by heading text
* Change: for parsing purposes all bullets interrupt all paragraphs. Eliminate the possibility
  of wrap on typing merging a block of list items when one of them is edited to non-list item.
* Add: wrap on typing and document format respects the `@formatter:off`/`@formatter:on` tags
* Change: refactor all the settings to use settable component list where possible.
* Fix: Copy HTML mime formatted text to use CSS settings only if the profile name is
  `COPY_HTML_MIME`, otherwise use internal defaults.
* Fix: table column alignment was not taking accumulated span offset in the table row when
  getting alignment for the column from separator row.
* Add: `Copy Markdown as HTML formatted text` action that will copy document or selection to the
  clipboard in HTML mime format that will paste as formatted HTML into applications that
  recognize this format. Useful for pasting rendered markdown in e-mails. To override the
  default styles and parser options for rendered HTML create a profile named `COPY_HTML_MIME`
  and override CSS Text. Use
  [html_mime_default.css](/resources/com/vladsch/idea/multimarkdown/html_mime_default.css) as a
  starting template. All style settings must be contained in a single matching one line entry
  since they are set in each element and there is no stylesheet. The "css" text is parsed with a
  simple parser, one line comments stripped out and each line's style attributed to element
  selector, with style to be added to the element's style attribute. The "parent" selector is
  based on Markdown AST hierarchy and not actual HTML, so any HTML tags surrounding Markdown
  elements will have no effect. The classes are hardcoded into the attribute provider such as:
  `tr.odd`, `tr.even` and `li.loose` based on Markdown AST.
* Add: option to not load GIF images, later if possible to not animate them just display the
  first frame. Really messes up preview and scrolling. Even crashed PhpStorm needing a power
  down because it would not be killed. Same with IDEA but force quit worked.
* Fix: In profiles Stylesheet and HTML override project settings options were reversed in the
  code. Html controlled Stylesheet and Stylesheet controlled HTML.
* Fix: Copy Jira and YouTrack heading would not have text if `Anchor Links` parser option was
  selected.
* Add: option to not load GIF images, later if possible to not animate them just display the
  first frame. Really messes up preview and scrolling. Even crashed PhpStorm needing a power
  down because it would not be killed.
* Add: formatter control tags support
* Add: Copy YouTrack formatted text, like Jira but with differences
* Fix: Copy Jira formatted text adding extra blank line in block quote
* Add: fenced/indented code trailing space trimming options.
* Add: flexmark-java flexmark example trailing space trimming options.
* Add: fenced code style option `Space before language info` to put a space between opening
  marker and language info string
* Fix: disable backspace, enter and typed character handlers in multi-caret mode.
* Add: multi-invoke for inline code completion to select fully qualified names or just simple
  names. Make simple name the default. Very annoying to get full names in docs.

### 2.2.0 - Compatibility & Enhancement Release

#### Basic & Enhanced Editions

* Add: markdown live template for collapsible details mnemonic `.collapsed`
* Add: option in settings to hide disabled buttons
* Change: move disable annotations from debug to document settings.
* Fix #335, Markdown Navigator breaks the line end whitespace trimming feature of EditorConfig
* Change: remove all **pegdown** dependencies
* Change: remove tab previews and enable split editor for basic edition, with fixed position
  restoring.
* Add: basic version now has split editor
* Fix: Slow scrolling with JavaFX WebView, was also causing unacceptable typing response for
  files of 500+ lines. Caused by WebView handling of CSS parameters not code.
* Fix: reimplemented JavaFX WebView integration with interruptible rendering to favour typing
  response.
* Add: #225, code highlight line number via Prism.js highlighter option
* Fix: #313, Changing fonts causes WebStorm to freeze
* Add: #316, Make shared settings Project specific
* Fix: #315, NullPointerException with v2016.3 EAP (163.6110.12)
* Fix: Implement multi-line URL image links in flexmark-java
* Fix: #327, IntelliJ IDEA 2016.3 EAP API change incompatibility.
* Fix: #328, wiki link can use ` `, `-`, `+`, `<` or `>` to match a `-` in the file name. Added
  stub index for links to make file reference search efficient.
* Change: document icons to match 2016.3 style

#### Enhanced Edition

* Fix: Rename refactoring of referencing elements broken by stub index work
* Add: JavaFX WebView script provider `Details tag opener` to open all `<details>` tags in
  preview so the content can be seen while editing
* Add: collapsible headers and markdown scripts
* Fix: setting change export now forces to re-export files in case some settings changed that
  affect the content of exported files.
* Change list toolbar icons to be simpler and more distinguishable. they all look alike.
* Add: link map move up/down groups within tree node
* Add: link map add quick fix to move errant mapping group to `unused` link type so config can
  be saved.
* Add: jekyll templates by adding an option to create a initial content when creating a mapping
  text group: empty, sample1,... each sample is based on element type
* Fix: splitting a line right after list item marker would be inconsistent and not result in a
  new list item above the current one.
* Fix: bump up the index file version numbers
* Change: disabled swing synchronization until it works properly
* Add: warning that prism syntax highlighter slows typing response also added for Fire Bug Lite
* Fix: document format would sometimes wrap early.
* Fix: swing css files not to have embedded `<` in comments to eliminate `Unterminated Comment`
  exception when using `Embed stylesheet URL content` option in HTML Generation with Swing
  browser
* Fix: swing browser pane to process HTML header for stylesheet links and load them. Now Swing
  browser can be used with exported HTML documents and a fast way to play with Swing stylesheet
  by embedding it in the HTML to get live update in the preview.
* Add: warning to Prism.js and Fire Bug Lite that they can affect preview display and typing
  response.
* Add: preview update delay tweak, default of 500ms makes typing a breeze and preview updates
  half second later.
* Fix: export on smart mode exit broke exporting all together
* Fix: style sheets need url prefix when displaying HTML
* Add: Re-Export action that will ignore modification time and force re-exporting of all
  required files.
* Fix: added a short time delay to running export after settings change or project open.
* Add: option to not wrap on typing when soft wrap is enabled for the editor
* Fix: #340, 2.1.1.40 Fail to re-gen HTML files when HTML already exists
* Add: option for format document with soft wraps: disabled, enabled and infinite margins. Will
  remove all soft breaks when formatting the document.
* Fix: balloon on html project export
* Add: link text completion for GitHub issue titles. Completes same as in text. Fast way to link
  to issues and have the title in the link.
* Add: #314, Export .html files (as part of build?)
  * exported files are limited to being under the project base directory to prevent erroneous
    target directory from writing to the file system in unexpected location.
  * copy custom font file if stylesheet has reference to it
  * optionally use relative links to:
  * exported html files
  * stylesheets and scripts
  * custom font
  * image files
  * optionally copy image files
* Fix : Jira copy to add blank lines for loosely spaced lists and after the last list item of
  the outer-most list and the next element
* Add: scope based rendering profiles allowing fine grained control on markdown rendering
  options.
* Add: #319, Synchronize source caret to preview element on click.
* Add: #283, print html preview for now only for JavaFx
* Add: #174, Suggestion: URL-to-filename transformation rules for image previews
  * Options to map from markdown link text to GitHub based link reference. ie. `{{ static_root
    }}` --> `/`
  * Options to map from GitHub based link reference to markdown link text. ie. `/` --> `{{
    static_root }}`
  * With scope based rendering profiles this mapping can be customized for specific files and/or
    directories
* Add: #331, Add markdown context aware trailing space removal
* Add: #329, Now can delete all previously generated file through HTML export or just the files
  that were previously generated and will no longer be generated in the current configuration.
* Add: Update HTML Export on project settings change option.
* Fix: #330, unexpected HTML export files on save.
* Fix: exported HTML was missing custom CSS text from Stylesheet options.
* Add: HTML Export will export any HTML files that were exported with different settings
* Add: Export Markdown to HTML action will export all changed files and delete any invalid ones
  from previous exports.
* Add: HTML Export to display error on export of different sources to same target
* Add: progress indicator to HTML Export and make it backgroundable and cancellable.
* Add: Soft wrap at right margin option to application settings for markdown documents.
* Add: configurable file reference recognition in jekyll front matter element
* Fix: linked map settings adding empty group on settings load in migration code
* Add: #332, refactor file name reference in jekyll front matter when renaming file
* Fix: when Prism.js is used as highlighter, scrolling to source with caret in the code part of
  the fenced code would always scroll to top of document.
* Fix: #320, ArrayIndexOutOfBoundsException at BlockQuoteAddAction
* Fix: JavaFX preview synchronize to caret would mess up for heading and fenced code in list
  items.
* Fix: Edit TOC dialog did not add a space between `levels=...` and the next option
* Fix: Jira copy failed to include `:lang=` for fenced code and did not add an extra blank line
  after the fenced code
* Fix: flexmark-java options refactoring exception and make dialog reflect position and
  selection of element being renamed.

### 2.1.1 - Bug Fix & Enhancement Release

#### Basic & Enhanced Editions

* Fix: #299, Tables not syntax highlighted in basic version.
* Add: List syntax options: CommonMark, Fixed, GitHub.
* Add: #301, License activation not working for some network security configurations, Option to
  use non-secure connection for license activation.
* Fix: #302, IndexOutOfBoundsException: Index out of range: 190
* Fix: #307, NegativeArraySizeException when opening .md.erb file, IDE bug
* Change: update Kotlin to 1.0.4

#### Enhanced Edition

* Fix: #305, Document Format indents Footmarks converting them to code blocks
* Add: #306, Copy/Cut of reference links, images or footnote references to include the
  references and footnotes on paste.
* Add: #300, Breadcrumbs support for Markdown documents
* Fix: breadcrumbs to show heading hierarchy as parents, including headings nested within other
  elements like list items, block quotes, etc.
* Add: breadcrumb option to show element text and maximum number of characters of text to use
  (10-60, 30 default).
* Fix: breadcrumb setext heading to use atx equivalent text
* Fix: breadcrumbs to show paragraph text instead of `Text Block`
* Add: Copy as JIRA formatted text action. Copy selection or whole document to clipboard as JIRA
  formatted text.
* Fix: #308, Wiki vcs repo not recognized in 2016.3 due to API changes. Affects to http:...
  absolute link conversion from non wiki markdown files to wiki target files.
* Add: on paste reference link format resolution for new destination file
* Add: on paste link format resolution for new destination file

### 2.1.0 - Bug Fix Release

#### Basic & Enhanced Editions

* Change: update source for flexmark-java refactored file layout.
* Fix: #287, tables stopped rendering
* Fix: #286, PyCharm 2016.2.1, unterminated fenced code causing too many exceptions
* Fix: #285, Not able to parse .md.erbfile
* Fix: #287, tables stopped rendering part 2, tables not rendering at all
* Fix: #291, on open idea load multimarkdown failure some time!, tentative fix.
* Change: remove Lobo Evolution library and other unused dependencies.
* Fix: #293, Cannot adjust settings for "Explicit Link"

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
  1. Headings: searchable text is the heading text, greyed out text is the heading id with `#`
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

* Fix: #282, Child paragraphs of tight list items are merged into the item text in preview
  instead of being a separate paragraph.
* Change: Component name for Markdown Navigator application shared settings changed to `Markdown
  Navigator` from a confusing `ApplicationShared`. Did't realize that the component name was
  used for display in import/export settings dialog.
* Fix: JavaFX and Swing stylesheets to better match GFM rendering.
* Add: Flexmark parser used for Swing preview rendering and plain HTML text previews.
* Add: allow task list items from ordered list items like GitHub, rendering is the same as
  bullet items.
* Fix: emoji renderer was not setting image height, width nor align attributes
* Fix: emoji parser flags were not being passed to HTML Renderer
* Add: Flexmark parser used for JavaFX Html preview rendering.
* Add: Debug setting to allow switching to pegdown for HTML rendering for debug and comparison
  purposes.
* Change: update flexmark-java parser to spec 0.26 with more intuitive emphasis parsing
* Add: skeleton error reporter to make reporting plugin errors more convenient and also more
  complete. No server code yet. For now disabled.
* Fix: With lexer as syntax highlighter deleting the last space after `[ ]` would cause an
  exception that was trapped but it would mess up syntax highlighting
* Fix: parser would accept ordered lists using `)` delimiter, as per CommonMark spec.
* Add: flexmark parser as the default option for lexer, parser and external annotator. Typing
  response is amazing. Some elements still missing:
  * Definitions
  * Typographic: Quotes, Smarts
  * Multi-Line Image URLs

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

[holgerbrandl/pasteimages]: https://github.com/holgerbrandl/pasteimages

