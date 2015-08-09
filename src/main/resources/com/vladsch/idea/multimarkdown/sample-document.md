Test Markdown document
======================

Text
----

Bold: **Here is a paragraph with bold text.** followed by normal text

Italic: *Here is a paragraph with italic text.* followed by normal text

Strikethrough: ~~Here is a paragraph with strikethrough.~~ followed by normal text

Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi commodo nibh eros, a aliquet diam pharetra nec. Proin tempus felis sit amet lacus tempor, vel dictum sem congue. Quisque in mi nulla. Sed orci urna, tincidunt id volutpat non, condimentum a augue. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec laoreet nec lacus vel egestas. In hac habitasse platea dictumst. Etiam volutpat odio tellus, vitae finibus est volutpat volutpat. Etiam justo augue, aliquet eget elit ut, scelerisque pulvinar ipsum. Morbi orci ante, eleifend eu libero nec, efficitur suscipit turpis.

Nullam condimentum fermentum enim non tristique. Vestibulum ultrices iaculis ex vel lacinia. Aliquam molestie velit pellentesque ipsum vehicula ullamcorper. Phasellus eget pretium dolor. Maecenas in faucibus eros, a iaculis erat. Praesent in porttitor risus. In nunc nisi, elementum eget tellus ac, pulvinar vestibulum sapien. Praesent gravida id ex in pulvinar. Curabitur eleifend vitae eros id placerat. Nulla egestas, dolor vestibulum ultrices feugiat, nulla dolor posuere velit, id accumsan nisl eros vitae risus. Curabitur laoreet pharetra mi, ut blandit lectus aliquet ut. Duis interdum, augue non eleifend sagittis, purus ipsum accumsan massa, ac fermentum diam elit nec enim. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Curabitur nunc justo, sagittis vitae justo nec, fringilla lobortis nibh. Donec congue eros at massa pretium auctor.


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

|Column 1    |Column 2    |Column 3    |
|:-----------|:----------:|-----------:|
|Row 1 Cell 1|Row 1 Cell 2|Row 1 Cell 3|
|Row 2 Cell 1|Row 2 Cell 2|Row 2 Cell 3|

Horizontal rules
----------------

Paragraph text separated by horizontal rules

---

Paragraph text separated by horizontal rules

___

Paragraph text separated by horizontal rules

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

Task List:

- [x] @mentions, #refs, [links](), **formatting**, and <del>tags</del> supported
- [x] list syntax required (any unordered or ordered list supported)
- [x] this is a complete item
- [ ] this is an incomplete item
