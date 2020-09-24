---
title: Line Marker Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Links

### Markdown

* LINK_REF_MARKDOWN
  * LINK_ANCHOR_LINE_SELECTION
  * LINK_ANCHOR_A_TAG
  * LINK_ANCHOR_ID_ATTRIBUTE
  * LINK_ANCHOR_HEADING

```````````````````````````````` example Links - Markdown: 1
[MarkdownWithHeadings.md](MarkdownWithHeadings.md) 
.
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - Markdown: 2) options(disable-link-ref-markdown)
[MarkdownWithHeadings.md](MarkdownWithHeadings.md) 
.
[MarkdownWithHeadings.md](MarkdownWithHeadings.md) 
````````````````````````````````


#### Anchor

##### A-Tag

```````````````````````````````` example Links - Markdown - Anchor - A-Tag: 1
[MarkdownWithAnchor.md](MarkdownWithAnchor.md#anchor-ref) 
.
[MarkdownWithAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>#anchor-ref) 
````````````````````````````````


Test disabling link ref disabled anchor

```````````````````````````````` example(Links - Markdown - Anchor - A-Tag: 2) options(disable-link-ref-markdown)
[MarkdownWithAnchor.md](MarkdownWithAnchor.md#anchor-ref) 
.
[MarkdownWithAnchor.md](MarkdownWithAnchor.md#anchor-ref) 
````````````````````````````````


Test disabling anchor shows link ref marker

```````````````````````````````` example(Links - Markdown - Anchor - A-Tag: 3) options(disable-link-anchor-a-tag)
[MarkdownWithAnchor.md](MarkdownWithAnchor.md#anchor-ref) 
.
[MarkdownWithAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>#anchor-ref) 
````````````````````````````````


##### Heading

```````````````````````````````` example Links - Markdown - Anchor - Heading: 1
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#heading-1) 
.
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.HEADER" descr="Navigate to target MarkdownWithHeadings.md#heading-1" >heading-1</lineMarker>) 
````````````````````````````````


Test disabling link ref disabled anchor

```````````````````````````````` example(Links - Markdown - Anchor - Heading: 2) options(disable-link-ref-markdown)
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#heading-1) 
.
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#heading-1) 
````````````````````````````````


Test disabling anchor shows link ref marker

```````````````````````````````` example(Links - Markdown - Anchor - Heading: 3) options(disable-link-anchor-heading)
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#heading-1) 
.
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>#heading-1) 
````````````````````````````````


##### Attribute

```````````````````````````````` example Links - Markdown - Anchor - Attribute: 1
[MarkdownWithIdAnchor.md](MarkdownWithIdAttribute.md#id-attribute) 
.
[MarkdownWithIdAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithIdAttribute.md" >MarkdownWithIdAttribute.md</lineMarker>#id-attribute) 
````````````````````````````````


Test disabling link ref disabled anchor

```````````````````````````````` example(Links - Markdown - Anchor - Attribute: 2) options(disable-link-ref-markdown)
[MarkdownWithIdAnchor.md](MarkdownWithIdAttribute.md#id-attribute) 
.
[MarkdownWithIdAnchor.md](MarkdownWithIdAttribute.md#id-attribute) 
````````````````````````````````


Test disabling anchor shows link ref marker

```````````````````````````````` example(Links - Markdown - Anchor - Attribute: 3) options(disable-link-anchor-id-attribute)
[MarkdownWithIdAnchor.md](MarkdownWithIdAttribute.md#id-attribute) 
.
[MarkdownWithIdAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithIdAttribute.md" >MarkdownWithIdAttribute.md</lineMarker>#id-attribute) 
````````````````````````````````


##### Line

```````````````````````````````` example Links - Markdown - Anchor - Line: 1
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#L5-L6)
.
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.ANCHOR" descr="Navigate to target /src/MarkdownWithHeadings.md:5-6" >L5-L6</lineMarker>)
````````````````````````````````


Test disabling link ref disabled anchor

```````````````````````````````` example(Links - Markdown - Anchor - Line: 2) options(disable-link-ref-markdown)
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#L5-L6)
.
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#L5-L6)
````````````````````````````````


Test disabling anchor shows link ref marker

```````````````````````````````` example(Links - Markdown - Anchor - Line: 3) options(disable-link-anchor-line-selection)
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#L5-L6)
.
[MarkdownWithHeadings: Lines 5-6](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>#L5-L6)
````````````````````````````````


##### Combinations

Test all others not affected

```````````````````````````````` example(Links - Markdown - Anchor - Combinations: 1) options(disable-one[link-anchor-heading; link-anchor-a-tag; link-anchor-id-attribute; link-anchor-line-selection; ])
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#heading-1) 
[MarkdownWithAnchor.md](MarkdownWithAnchor.md#anchor-ref) 
[MarkdownWithIdAnchor.md](MarkdownWithIdAttribute.md#id-attribute) 
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#L5-L6)
.
---- Disabled: link-anchor-heading -------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>#heading-1) 
[MarkdownWithAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>#anchor-ref) 
[MarkdownWithIdAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithIdAttribute.md" >MarkdownWithIdAttribute.md</lineMarker>#id-attribute) 
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.ANCHOR" descr="Navigate to target /src/MarkdownWithHeadings.md:5-6" >L5-L6</lineMarker>)

---- Disabled: link-anchor-a-tag ---------------------------------------
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.HEADER" descr="Navigate to target MarkdownWithHeadings.md#heading-1" >heading-1</lineMarker>) 
[MarkdownWithAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>#anchor-ref) 
[MarkdownWithIdAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithIdAttribute.md" >MarkdownWithIdAttribute.md</lineMarker>#id-attribute) 
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.ANCHOR" descr="Navigate to target /src/MarkdownWithHeadings.md:5-6" >L5-L6</lineMarker>)

---- Disabled: link-anchor-id-attribute --------------------------------
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.HEADER" descr="Navigate to target MarkdownWithHeadings.md#heading-1" >heading-1</lineMarker>) 
[MarkdownWithAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>#anchor-ref) 
[MarkdownWithIdAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithIdAttribute.md" >MarkdownWithIdAttribute.md</lineMarker>#id-attribute) 
[MarkdownWithHeadings: Lines 5-6](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.ANCHOR" descr="Navigate to target /src/MarkdownWithHeadings.md:5-6" >L5-L6</lineMarker>)

---- Disabled: link-anchor-line-selection ------------------------------
[MarkdownWithHeadings.md](MarkdownWithHeadings.md#<lineMarker icon="MdIcons.Element.HEADER" descr="Navigate to target MarkdownWithHeadings.md#heading-1" >heading-1</lineMarker>) 
[MarkdownWithAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>#anchor-ref) 
[MarkdownWithIdAnchor.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithIdAttribute.md" >MarkdownWithIdAttribute.md</lineMarker>#id-attribute) 
[MarkdownWithHeadings: Lines 5-6](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>#L5-L6)
````````````````````````````````


### Non-Markdown

* LINK_REF_FILE
  * LINK_ANCHOR_LINE_SELECTION
* IMAGE_LINK

```````````````````````````````` example Links - Non-Markdown: 1
[JavaFile.txt](JavaFile.txt) 
.
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - Non-Markdown: 2) options(disable-link-ref-file)
[JavaFile.txt](JavaFile.txt) 
.
[JavaFile.txt](JavaFile.txt) 
````````````````````````````````


### Image

```````````````````````````````` example Links - Image: 1
![](sample.png) 
.
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - Image: 2) options(disable-link-ref-image)
![](sample.png) 
.
![](sample.png) 
````````````````````````````````


Image in table cell

```````````````````````````````` example Links - Image: 3
|      Data       |
|-----------------|
| ![](sample.png) |
.
|      Data       |
|-----------------|
| ![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) |
````````````````````````````````


#### Anchor

##### Line

```````````````````````````````` example Links - Image - Anchor - Line: 1
[JavaFile.txt](JavaFile.txt#L4-L6) 
.
[JavaFile.txt](JavaFile.txt#<lineMarker icon="MdIcons.Element.ANCHOR" descr="Navigate to target /src/JavaFile.txt:4-6" >L4-L6</lineMarker>) 
````````````````````````````````


Test disabling link ref disabled anchor

```````````````````````````````` example(Links - Image - Anchor - Line: 2) options(disable-link-ref-file)
[JavaFile.txt](JavaFile.txt#L4-L6) 
.
[JavaFile.txt](JavaFile.txt#L4-L6) 
````````````````````````````````


Test disabling anchor shows link ref marker

```````````````````````````````` example(Links - Image - Anchor - Line: 3) options(disable-link-anchor-line-selection)
[JavaFile.txt](JavaFile.txt#L4-L6) 
.
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>#L4-L6) 
````````````````````````````````


### FTP

* LINK_REF_FTP

```````````````````````````````` example Links - FTP: 1
[ftp](ftp://vladsch.com) 
.
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - FTP: 2) options(disable-link-ref-ftp)
[ftp](ftp://vladsch.com) 
.
[ftp](ftp://vladsch.com) 
````````````````````````````````


### Mail

* LINK_REF_MAIL

```````````````````````````````` example Links - Mail: 1
[vladimir@vladsch.com](mailto:vladimir@vladsch.com) 
.
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - Mail: 2) options(disable-link-ref-mail)
[vladimir@vladsch.com](mailto:vladimir@vladsch.com) 
.
[vladimir@vladsch.com](mailto:vladimir@vladsch.com) 
````````````````````````````````


### GitHub

* LINK_REF_GITHUB

```````````````````````````````` example Links - GitHub: 1
[markdown-navigator](https://github.com/vsch/idea-multimarkdown) 
.
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - GitHub: 2) options(disable-link-ref-github)
[markdown-navigator](https://github.com/vsch/idea-multimarkdown) 
.
[markdown-navigator](https://github.com/vsch/idea-multimarkdown) 
````````````````````````````````


### Web

* LINK_REF_WEB

```````````````````````````````` example Links - Web: 1
[markdown-navigator](https://vladsch.com/product/markdown-navigator) 
.
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
````````````````````````````````


```````````````````````````````` example(Links - Web: 2) options(disable-link-ref-web)
[markdown-navigator](https://vladsch.com/product/markdown-navigator) 
.
[markdown-navigator](https://vladsch.com/product/markdown-navigator) 
````````````````````````````````


### Combinations

* LINK_REF_MARKDOWN
* LINK_REF_FILE
* LINK_REF_GITHUB
* LINK_REF_IMAGE
* LINK_REF_WEB
* LINK_REF_FTP
* LINK_REF_MAIL

Test all others not affected

```````````````````````````````` example(Links - Combinations: 1) options(disable-one[link-ref-markdown; link-ref-file; link-ref-github; link-ref-image; link-ref-web; link-ref-ftp;  link-ref-mail; ])
[MarkdownWithHeadings.md](MarkdownWithHeadings.md) 
[JavaFile.txt](JavaFile.txt) 
[ftp](ftp://vladsch.com) 
[vladimir@vladsch.com](mailto:vladimir@vladsch.com) 
[markdown-navigator](https://github.com/vsch/idea-multimarkdown) 
[markdown-navigator](https://vladsch.com/product/markdown-navigator) 
![](sample.png) 
.
---- Disabled: link-ref-markdown ---------------------------------------
[MarkdownWithHeadings.md](MarkdownWithHeadings.md) 
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 

---- Disabled: link-ref-file -------------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
[JavaFile.txt](JavaFile.txt) 
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 

---- Disabled: link-ref-github -----------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
[markdown-navigator](https://github.com/vsch/idea-multimarkdown) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 

---- Disabled: link-ref-image ------------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
![](sample.png) 

---- Disabled: link-ref-web --------------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
[markdown-navigator](https://vladsch.com/product/markdown-navigator) 
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 

---- Disabled: link-ref-ftp --------------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
[ftp](ftp://vladsch.com) 
[vladimir@vladsch.com](<lineMarker icon="MdIcons.LinkTypes.Mail" descr="Navigate to target mailto:vladimir@vladsch.com" >mailto:vladimir@vladsch.com</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 

---- Disabled: link-ref-mail -------------------------------------------
[MarkdownWithHeadings.md](<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithHeadings.md" >MarkdownWithHeadings.md</lineMarker>) 
[JavaFile.txt](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/fileTypes/text.svg, null]" descr="Navigate to target JavaFile.txt" >JavaFile.txt</lineMarker>) 
[ftp](<lineMarker icon="MdIcons.LinkTypes.Ftp" descr="Navigate to target ftp://vladsch.com" >ftp://vladsch.com</lineMarker>) 
[vladimir@vladsch.com](mailto:vladimir@vladsch.com) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.GitHub" descr="Navigate to target https://github.com/vsch/idea-multimarkdown" >https://github.com/vsch/idea-multimarkdown</lineMarker>) 
[markdown-navigator](<lineMarker icon="MdIcons.LinkTypes.Web" descr="Navigate to target https://vladsch.com/product/markdown-navigator" >https://vladsch.com/product/markdown-navigator</lineMarker>) 
![](<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>) 
````````````````````````````````


## References

* REFERENCE_REF_LINK
* REFERENCE_REF_IMAGE
* REFERENCE_REF_FOOTNOTE
* REFERENCE_REF_MACRO
* Combinations

### Ref Image

```````````````````````````````` example References - Ref Image: 1
![Image]

[Image]: sample.png
.
<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to referenced page sample.png" >![</lineMarker>Image]

[Image]: <lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>
````````````````````````````````


```````````````````````````````` example(References - Ref Image: 2) options(disable-reference-ref-image)
![Image]

[Image]: sample.png
.
![Image]

[Image]: <lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>
````````````````````````````````


### Ref Link

```````````````````````````````` example References - Ref Link: 1
[Link]

[Link]: MarkdownWithAnchor.md
.
<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to referenced page MarkdownWithAnchor.md" >[</lineMarker>Link]

[Link]: <lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>
````````````````````````````````


```````````````````````````````` example(References - Ref Link: 2) options(disable-reference-ref-link)
[Link]

[Link]: MarkdownWithAnchor.md
.
[Link]

[Link]: <lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>
````````````````````````````````


### Combinations

```````````````````````````````` example(References - Combinations: 1) options(disable-one[reference-ref-image; reference-ref-link; ])
![Image]
[Link]
test [^Reference]
<<<Reference>>>

[Image]: sample.png
[Link]: MarkdownWithAnchor.md
    
.
---- Disabled: reference-ref-image -------------------------------------
![Image]
<lineMarker icon="MdIcons.Document.FILE" descr="Navigate to referenced page MarkdownWithAnchor.md" >[</lineMarker>Link]
test [^Reference]
<<<Reference>>>

[Image]: <lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>
[Link]: <lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>
    


---- Disabled: reference-ref-link --------------------------------------
<lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to referenced page sample.png" >![</lineMarker>Image]
[Link]
test [^Reference]
<<<Reference>>>

[Image]: <lineMarker icon="Deferred. Base=Row icon. myIcons=[IconWrapperWithTooltip:/org/intellij/images/icons/ImagesFileType.svg, null]" descr="Navigate to target sample.png" >sample.png</lineMarker>
[Link]: <lineMarker icon="MdIcons.Document.FILE" descr="Navigate to target MarkdownWithAnchor.md" >MarkdownWithAnchor.md</lineMarker>
    
````````````````````````````````


