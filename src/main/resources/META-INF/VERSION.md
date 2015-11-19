### 1.2.2.1 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced
- Fix #73: self referencing links with 'Show Modified' not selected do not generate correct HTML.

#### Enhanced Edition

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

