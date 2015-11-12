### 1.2.1.1 - Bug Fixes and Enhancements

#### Basic Edition
- Fix #62: Plugin exhausted thread resources if a lot of projects were open 
- Add annotation if wiki link ends in .md, with quick fix to remove .md extension  
- Fix: buy license button in settings navigated to the get trial license page instead of the buy license page.
- Fix #60: use another class for string to input stream conversion that is universally support across os types.
- Fix #63: Unresolved links were not always highlighted as error
- Add a change to quick fix on an unresolved link will apply the same change to all links that match the link being modified. 

#### Enhanced Edition

- Fix #64: Explicit link annotation had WikiLink cast causing CastClassException.
