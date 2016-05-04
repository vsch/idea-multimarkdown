### 1.2.3.1 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced

- Update Sources for latest Kotlin compatibility
- Add Pegdown timeout exception handling to prevent ide hanging during indexing
- Change default pegdown parse timeout to 200 ms to prevent erroneous parsing from hanging the
  UI.

### 1.2.3 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced

- Change: default JavaFX preview font order to have Helvetica before Arial, cleaner text rendering. 
- Fix #73: self referencing links with 'Show Modified' not selected do not generate correct HTML.
- Change: update JavaFX preview to new GitHub style, wider pages.
- Add: inspections when wiki pages are linked with file extension, they will display raw text not rendered HTML
- Fix: anchor links markdown extension was always enabled when 'Use Old Preview' was selected, regardless of preferences settings
- Add: fork and raw GitHub link to be recognized
- Add: line icon to show multiple resolved targets
- Change: preferences for the plugin are now split between local, non-roaming persistence in multimarkdown.local.xml and shared roaming persistence in multimarkdown.shared.xml. Preferences from multimarkdown.xml are migrated to new files and the file is deleted. 

#### Enhanced Edition

* Change: new implementation of file link resolving logic handles a full set of GitHub idiosyncrasies for relative links, raw/blob access, absolute https:// links, file extensions and case sensitivity.
* Add: new annotation inspections for common errors in link resolution
* New: modified HTML now has all links as URI's during rendering with preference to GitHub repository file when available, fallback to local file://
* Fix: added validation for invalid PsiElements during refactoring to prevent exceptions
* Fix: all link refactoring for file move/rename adjusts affected link according to GitHub rules
* Add: warning annotation in text editor, link color and image border in preview when a link target is not on GitHub according to its status
* Add: intention to change relative to absolute link addresses and vice-versa
* Add: validation of absolute links that look like they are targeting files in the same repository as the containing file 
* Add: navigation line items for absolute links that resolve to files
* Add: absolute address link completions and link completions based on extensions other than markdown for wiki and explicit and images for image links, just add file:// or https:// in front to get url completions and an extension without a file name to get extension based completions. `https://.java` will show java files with https:// absolute link address.  
* Fix #71: license information is now saved in multimarkdown.local.xml and is marked as non-roaming 
* Add: handle relative image links in wiki pages must resolve to raw file access in main repository default behavior and validation  
* Add: automatically change wiki link to explicit link if target file is moved out of wiki link access range
* Add: refactoring now keeps link format as it was before, if it was absolute or page relative.

### 1.2.2 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced

- Add a change to quick fix on an unresolved link will apply the same change to all links that match the link being modified. 
- Add annotation if wiki link ends in .md, with quick fix to remove .md extension  
- Fix #60: use another class for string to input stream conversion that is universally support across os types.
- Fix #62: Plugin exhausted thread resources if a lot of projects were open 
- Fix #63: Unresolved links were not always highlighted as error
- Fix #65: threading issues causing slow preview update and other intermittent errors.
- Fix #67: change message for not finding javafx from warning to info.
- Fix #70: closed to to task list items with capital x `- [X]` would not render correctly in the WevView preview.
- Fix link resolution did not work if non-default Markdown extension was used on Wiki pages  
- Fix link resolution in preview to match the rules for markdown source
- Fix wiki link with multiple matched targets would not resolve according to sort order 
- Fix wiki link with multiple matched targets would show quick fix to rename unreachable targets
- Fix wiki links with subdirectory references did not show annotation popup or quick fix to remove directory 
- Fix buy license button in settings navigated to the get trial license page instead of the full license page.

#### Enhanced Edition

- Add validation for relative links accessing wiki raw content by using the Wiki page link with extension.
- Add validation of all explicit relative links from the main repository to wiki files. 
- Add warning when a file is a target of a link but is not on GitHub. 
- Fix #64: class cast exception in explicit link.
- Fix link errors in source, preview and click actions are now in agreement  
- Fix link resolution was not always consistent with GitHub  
- Fix license registration failing in version 133.1711 for lack of methods in some classes.  
- Fix intention to change wiki link to explicit and vice-versa. 
