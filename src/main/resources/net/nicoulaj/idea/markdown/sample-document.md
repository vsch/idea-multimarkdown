Test Markdown document
======================

Text
----

Here is a paragraph with bold text. Here is a paragraph with bold text. Here is a paragraph with
bold text. **Here is a paragraph with bold text.** Here is a paragraph with bold text. Here is a
paragraph with bold text. Here is a paragraph with bold text. Here is a paragraph with bold text.

Here is another one with italic text. Here is another one with italic text. Here is another one
with italic text. *Here is another one with italic text.* Here is another one with italic text.
Here is another one with italic text. Here is another one with italic text. Here is another one
with italic text. *Here is another one with* italic text. Here is another one with italic text.

Links
-----

Autolink: <http://example.com>

Link: [Example](http://example.com)

Reference style [link][1].

[1]: http://example.com  "Example"


Images
------

Image: ![My image](http://www.foo.bar/image.png)



Headers
-------

# First level title
## Second level title
### Third level title
#### Fourth level title
##### Fifth level title
###### Sixth level title

### Title with [link](http://localhost)
### Title with ![image](http://localhost)

Code
----

Inline `code in a` paragraph.

A code block:

    /**
     * Load a {@link String} from the {@link #BUNDLE} {@link ResourceBundle}.
     *
     * @param key    the key of the resource.
     * @param params the optional parameters for the specific resource.
     * @return the {@link String} value or {@code null} if no resource found for the key.
     */
    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
    }

Quotes
------


> This is the first level of quoting.
>
> > This is nested blockquote.
>
> Back to the first level.


> A list within a blockquote:
>
> *	asterisk 1
> *	asterisk 2
> *	asterisk 3


> Formatting within a blockquote:
>
> ### header
> Link: [Example](http://example.com)



Tables
-------

<table>
  <tr>
    <th>Column 1</th>
    <th>Column 2</th>
  </tr>
  <tr>
    <td>Row 1 Cell 1</td>
    <td>Row 1 Cell 2</td>
  </tr>
  <tr>
    <td>Row 2 Cell 1</td>
    <td>Row 2 Cell 2</td>
  </tr>
</table>

Horizontal rules
----------------

---

___


***


Lists
-----

Unordered list:

*	asterisk 1
*	asterisk 2
*	asterisk 3

Ordered list:

1.	First
2.	Second
3.	Third

Mixed:

1. First
2. Second:
	* Fee
	* Fie
	* Foe
3. Third
