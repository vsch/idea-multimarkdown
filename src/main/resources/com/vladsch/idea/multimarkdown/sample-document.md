Test Markdown document
======================

Text
----

Bold: \** **Here is a paragraph with bold text.**\** followed by normal text
\<strong><strong>Here is a paragraph with bold text.</strong>\</strong> followed by normal text
\<b><b>Here is a paragraph with bold text.</b>\</b> followed by normal text

Italic: \* *Here is a paragraph with italic text.*\* followed by normal text
using \<i> *Here is a paragraph with italic text.*\</i> followed by normal text

Strikethrough: \~~ ~~Here is a paragraph with strikethrough.~~\~~ followed by normal text
\<del><del>Here is a paragraph with strikethrough.</del>\</del> followed by normal text
\<strike><strike>Here is a paragraph with strikethrough.</strike>\</strike> followed by normal text
\<s><s>Here is a paragraph with strikethrough.</s>\</s> followed by normal text

Superscript/Subscript: normal<sup>superscript</sup> and normal<sub>subscript</sub> followed by normal text

Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi commodo nibh eros, a aliquet diam pharetra nec. Proin tempus felis sit amet lacus tempor, vel dictum sem congue. Quisque in mi nulla. Sed orci urna, tincidunt id volutpat non, condimentum a augue. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec laoreet nec lacus vel egestas. In hac habitasse platea dictumst. Etiam volutpat odio tellus, vitae finibus est volutpat volutpat. Etiam justo augue, aliquet eget elit ut, scelerisque pulvinar ipsum. Morbi orci ante, eleifend eu libero nec, efficitur suscipit turpis.

Nullam condimentum fermentum enim non tristique. Vestibulum ultrices iaculis ex vel lacinia. Aliquam molestie velit pellentesque ipsum vehicula ullamcorper. Phasellus eget pretium dolor. Maecenas in faucibus eros, a iaculis erat. Praesent in porttitor risus. In nunc nisi, elementum eget tellus ac, pulvinar vestibulum sapien. Praesent gravida id ex in pulvinar. Curabitur eleifend vitae eros id placerat. Nulla egestas, dolor vestibulum ultrices feugiat, nulla dolor posuere velit, id accumsan nisl eros vitae risus. Curabitur laoreet pharetra mi, ut blandit lectus aliquet ut. Duis interdum, augue non eleifend sagittis, purus ipsum accumsan massa, ac fermentum diam elit nec enim. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Curabitur nunc justo, sagittis vitae justo nec, fringilla lobortis nibh. Donec congue eros at massa pretium auctor.


Links
-----

PlainLink: http://example.com
Autolink: <http://example.com>
e-mail: e-mail@example.com
[e-mail](mailto:example@example.com)
[~~Scala~~](http://www.scala-lang.org/)
[[Wiki Link]]

Link: [Example](http://example.com)

Reference style [link][1].

[1]: http://example.com  "Example"

Definitions
----------- 

Definition Term
:   definition 

Images
------

Image: ![*My* image](http://www.foo.bar/image.png)

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
|Row 3 Cell 1|Row 3 Cell 2|Row 3 Cell 3|
|Row 4 Cell 1|Row 4 Cell 2|Row 4 Cell 3|
|Row 5 Cell 1|Row 5 Cell 2|Row 5 Cell 3|
|Row 6 Cell 1|Row 6 Cell 2|Row 6 Cell 3|
[Caption]

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

Double spaced:

*	asterisk 1

*	asterisk 2

*	asterisk 3

Ordered list:

1.	First
2.	Second
3.	Third

Double spaced:

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

GitHub Style Task List:

- [x] @mentions, #refs, [links](), **formatting**, and <del>tags</del> supported
- [x] list syntax required (any unordered or ordered list supported)
- [x] this is a complete item
- [ ] this is an incomplete item

Loose:

- [x] @mentions, #refs, [links](), **formatting**, and <del>tags</del> supported

- [x] list syntax required (any unordered or ordered list supported)

- [x] this is a complete item

- [ ] this is an incomplete item

Mixed:

- [x] Tight
- [x] Tight list syntax required (any unordered or ordered list supported)

- [x] Loose this is a complete item
- [ ] Tight, this is an incomplete item
- [ ] Tight, this is an incomplete item

```

# This is just a line begining with '#'. Not a header.

```

```
# used in the single view template to render the news content
lib.mainContent < styles.content.col0

lib.pageTemplate {
    variables {
        CONTENT = COA
        CONTENT {
            # load the plugin instead of any page template content on news pages
            10 = COA
            10 {
                10 < tt_content.list.20.sgnews_singleview
                if.value.field = doktype
                if.equals = 116
            }

            # load the overview plugin on category pages
            20 = COA
            20 {
                10 < tt_content.list.20.sgnews_overview
                if.value.field = doktype
                if.equals = 117
            }
        }
    }
}
```

1. List Item

        ```ansi.vga
        # Code Block

        # With Newlines
        ```
  
  
##### See any Permutation of attributes right in your source

~~strikeout *italic* **bold** ***bold italic***~~ *italic* **bold** ***bold italic***

##### Stripe tables on rows and columns

| Language   | Locale | Language   | Locale | Language   | Locale |
|------------|--------|------------|--------|------------|--------|
| Albanian   | sq     | French     | fr     | Norwegian  | no     |
| Arabian    | ar     | Georgian   | ka     | Polish     | pl     |
| Armenian   | hy     | German     | de     | Portuguese | pt     |
| Azeri      | az     | Greek      | el     | Romanian   | ro     |
| Belarusian | be     | Hebrew     | he     | Russian    | ru     |
| Bosnian    | bs     | Hungarian  | hu     | Spanish    | es     |
| Bulgarian  | bg     | Icelandic  | is     | Serbian    | sr     |
| Catalan    | ca     | Indonesian | id     | Slovak     | sk     |
| Croatian   | hr     | Italian    | it     | Slovenian  | sl     |
| Czech      | cs     | Japanese   | ja     | Swedish    | sv     |
| Chinese    | zh     | Korean     | ko     | Thai       | th     |
| Danish     | da     | Latvian    | lv     | Turkish    | tr     |
| Dutch      | nl     | Lithuanian | lt     | Ukrainian  | uk     |
| English    | en     | Macedonian | mk     | Vietnamese | vi     |
| Estonian   | et     | Malay      | ms     |            |        |
| Finnish    | fi     | Maltese    | mt     |            |        |

  
Test   
----

