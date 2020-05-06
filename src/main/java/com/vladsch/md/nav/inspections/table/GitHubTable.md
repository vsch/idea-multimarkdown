This inspection validates markdown tables for compatibility with GitHub markdown parser.

**GitHub** markdown tables **must have** a single heading row and column spans (consecutive `|`
characters) are ignored:

* Missing table heading, will be treated as plain text:
  ```markdown
  |-----------|-----------|
  | Column 1  | Column 2  |
  ```
* Has column spans, which GitHub will ignore
  ```markdown
  | Heading 1 | Heading 2 |
  |-----------|-----------|
  | Column 1  | Column 2  |
  | Column span          ||
  ```
  
