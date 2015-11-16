### 1.2.1.3 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced
- Change link coloring for preview for now disabled. It slows down preview refresh even with as little as a few dozen relative links on the page.
- Fix wiki link with multiple matched targets would not resolve according to sort order 
- Fix wiki link with multiple matched targets would show quick fix to rename unreachable targets
- Fix wiki links with subdirectory references did not show annotation popup or quick fix to remove directory 
- Fix link resolution did not work if non-default Markdown extension was used on Wiki pages  
- Fix link resolution in preview to match the rules for markdown source
- Fix closed to to task list items with capital x `- [X]` would not render correctly in the WevView preview.

#### Enhanced Edition

- Fix link resolution was not always consistent with GitHub  

### 1.2.1.2 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced
- Change link coloring for preview for now disabled. It slows down preview refresh even with as little as a few dozen relative links on the page.
- Fix wiki link with multiple matched targets would not resolve according to sort order 
- Fix wiki link with multiple matched targets would show quick fix to rename unreachable targets
- Fix wiki links with subdirectory references did not show annotation popup or quick fix to remove directory 
- Fix link resolution did not work if non-default Markdown extension was used on Wiki pages  
- Fix link resolution in preview to match the rules for markdown source
- Fix closed to to task list items with capital x `- [X]` would not render correctly in the WevView preview.

#### Enhanced Edition

- Fix link resolution was not always consistent with GitHub  

### 1.2.1.1 - Bug Fixes and Enhancements

#### Basic Edition & Enhanced
- Fix #62: Plugin exhausted thread resources if a lot of projects were open 
- Add annotation if wiki link ends in .md, with quick fix to remove .md extension  
- Fix: buy license button in settings navigated to the get trial license page instead of the buy license page.
- Fix #60: use another class for string to input stream conversion that is universally support across os types.
- Fix #63: Unresolved links were not always highlighted as error
- Add a change to quick fix on an unresolved link will apply the same change to all links that match the link being modified. 

#### Enhanced Edition

- Fix #64: Explicit link annotation had WikiLink cast causing CastClassException.
