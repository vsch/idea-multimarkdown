---
title: File Element Stash
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Find Elements

```````````````````````````````` example(Find Elements: 1) options(find-references)
* list item 1
* list item 2

[ref]: example.com

[ref]
    
.
.
 MdReferenceImpl:[29, 48, "[ref] … : example.com\n"]
   REFERENCE_TEXT_OPEN:[29, 30, "["]
   MdReferenceIdentifierImpl:[30, 33, "ref"]
     REFERENCE_TEXT_LEAF:[30, 33, "ref"]
   REFERENCE_TEXT_CLOSE:[33, 35, "]:"]
   WHITESPACE:[35, 36, " "]
   MdReferenceLinkRefImpl:[36, 47, "examp … le.com"]
     REFERENCE_LINK_REF:[36, 47, "examp … le.com"]
   EOL:[47, 48, "\n"]
````````````````````````````````


```````````````````````````````` example(Find Elements: 2) options(find-list-items-list)
* list item 1
* list item 2

[ref]: example.com

[ref]
    
.
.
 MdUnorderedListItemImpl:[0, 14, "* lis … t item 1\n"]
   BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
   MdTextBlockImpl:[2, 14, "list  … item 1\n"]
     TEXT:[2, 13, "list  … item 1"]
     EOL:[13, 14, "\n"]
 MdUnorderedListItemImpl:[14, 28, "* lis … t item 2\n"]
   BULLET_LIST_ITEM_MARKER:[14, 16, "* "]
   MdTextBlockImpl:[16, 28, "list  … item 2\n"]
     TEXT:[16, 27, "list  … item 2"]
     EOL:[27, 28, "\n"]
````````````````````````````````


```````````````````````````````` example(Find Elements: 3) options(find-list-items)
* list item 1
* list item 2

[ref]: example.com

[ref]
    
.
.
 MdUnorderedListItemImpl:[0, 14, "* lis … t item 1\n"]
   BULLET_LIST_ITEM_MARKER:[0, 2, "* "]
   MdTextBlockImpl:[2, 14, "list  … item 1\n"]
     TEXT:[2, 13, "list  … item 1"]
     EOL:[13, 14, "\n"]
 MdUnorderedListItemImpl:[14, 28, "* lis … t item 2\n"]
   BULLET_LIST_ITEM_MARKER:[14, 16, "* "]
   MdTextBlockImpl:[16, 28, "list  … item 2\n"]
     TEXT:[16, 27, "list  … item 2"]
     EOL:[27, 28, "\n"]
````````````````````````````````


```````````````````````````````` example(Find Elements: 4) options(find-headers)
# Header 1
    
# Header 2
    
    
.
.
 MdAtxHeaderImpl:[0, 11, "# Hea … der 1\n"]
   HEADER_ATX_MARKER:[0, 1, "#"]
   WHITESPACE:[1, 2, " "]
   MdHeaderTextImpl:[2, 10, "Header 1"]
     HEADER_TEXT:[2, 10, "Header 1"]
   EOL:[10, 11, "\n"]
 MdAtxHeaderImpl:[16, 27, "# Hea … der 2\n"]
   HEADER_ATX_MARKER:[16, 17, "#"]
   WHITESPACE:[17, 18, " "]
   MdHeaderTextImpl:[18, 26, "Header 2"]
     HEADER_TEXT:[18, 26, "Header 2"]
   EOL:[26, 27, "\n"]
````````````````````````````````


```````````````````````````````` example(Find Elements: 5) options(find-headers, type[# Header 3])
# Header 1
    
# Header 2
    
⦙
    
.
# Header 1
    
# Header 2
    
# Header 3⦙
    
.
---- Before Action -----------------------------------------------------
 MdAtxHeaderImpl:[0, 11, "# Hea … der 1\n"]
   HEADER_ATX_MARKER:[0, 1, "#"]
   WHITESPACE:[1, 2, " "]
   MdHeaderTextImpl:[2, 10, "Header 1"]
     HEADER_TEXT:[2, 10, "Header 1"]
   EOL:[10, 11, "\n"]
 MdAtxHeaderImpl:[16, 27, "# Hea … der 2\n"]
   HEADER_ATX_MARKER:[16, 17, "#"]
   WHITESPACE:[17, 18, " "]
   MdHeaderTextImpl:[18, 26, "Header 2"]
     HEADER_TEXT:[18, 26, "Header 2"]
   EOL:[26, 27, "\n"]

---- After Action ------------------------------------------------------
 MdAtxHeaderImpl:[0, 11, "# Hea … der 1\n"]
   HEADER_ATX_MARKER:[0, 1, "#"]
   WHITESPACE:[1, 2, " "]
   MdHeaderTextImpl:[2, 10, "Header 1"]
     HEADER_TEXT:[2, 10, "Header 1"]
   EOL:[10, 11, "\n"]
 MdAtxHeaderImpl:[16, 27, "# Hea … der 2\n"]
   HEADER_ATX_MARKER:[16, 17, "#"]
   WHITESPACE:[17, 18, " "]
   MdHeaderTextImpl:[18, 26, "Header 2"]
     HEADER_TEXT:[18, 26, "Header 2"]
   EOL:[26, 27, "\n"]
 MdAtxHeaderImpl:[32, 43, "# Hea … der 3\n"]
   HEADER_ATX_MARKER:[32, 33, "#"]
   WHITESPACE:[33, 34, " "]
   MdHeaderTextImpl:[34, 42, "Header 3"]
     HEADER_TEXT:[34, 42, "Header 3"]
   EOL:[42, 43, "\n"]
````````````````````````````````


## Referenced

### Reference

```````````````````````````````` example(Referenced - Reference: 1) options(referenced-elements)
[ref]: example.com

[ref]
    
.
.
ref: { ref:[0, 19, type: REFERENCE] }
````````````````````````````````


```````````````````````````````` example(Referenced - Reference: 2) options(referenced-elements, backspace)
[ref]: example.com

[refs]: example.com

[ref]
    
[refs⦙]
    
.
[ref]: example.com

[refs]: example.com

[ref]
    
[ref⦙]
    
.
---- Before Action -----------------------------------------------------
ref: { ref:[0, 19, type: REFERENCE] }
refs: { refs:[20, 40, type: REFERENCE] }

---- After Action ------------------------------------------------------
ref: { ref:[0, 19, type: REFERENCE] }
````````````````````````````````


### Abbreviations

```````````````````````````````` example(Referenced - Abbreviations: 1) options(referenced-elements, parser-abbreviations)
*[ref]: example.com

ref
    
.
.
ref: { ref:[0, 20, type: ABBREVIATION] }
````````````````````````````````


```````````````````````````````` example(Referenced - Abbreviations: 2) options(referenced-elements, backspace, parser-abbreviations)
*[ref]: example.com

*[refs]: example.com

ref
  
refs⦙
    
.
*[ref]: example.com

*[refs]: example.com

ref
  
ref⦙
    
.
---- Before Action -----------------------------------------------------
ref: { ref:[0, 20, type: ABBREVIATION] }
refs: { refs:[21, 42, type: ABBREVIATION] }

---- After Action ------------------------------------------------------
ref: { ref:[0, 20, type: ABBREVIATION] }
````````````````````````````````


### RefAnchor

#### A Tag

Not applicable, has out of file references, use MdReferenceSearch for these

```````````````````````````````` example(Referenced - RefAnchor - A Tag: 1) options(referenced-elements, parser-parse-html-anchor-id)
ref <a id="ref">

[ref](#ref) 
    
.
.
````````````````````````````````


```````````````````````````````` example(Referenced - RefAnchor - A Tag: 2) options(referenced-elements, backspace, parser-parse-html-anchor-id)
ref <a id="ref">
    
refs <a id="refs">

[ref](#ref) 
    
[ref](#refs⦙) 

.
ref <a id="ref">
    
refs <a id="refs">

[ref](#ref) 
    
[ref](#ref⦙) 

.
---- Before Action -----------------------------------------------------

---- After Action ------------------------------------------------------
````````````````````````````````


#### Heading

Not applicable, has out of file references, use MdReferenceSearch for these

```````````````````````````````` example(Referenced - RefAnchor - Heading: 1) options(referenced-elements, parser-attributes-ext)
# Ref

[ref](#ref) 
.
.
````````````````````````````````


```````````````````````````````` example(Referenced - RefAnchor - Heading: 2) options(referenced-elements, backspace, parser-attributes-ext)
# Ref

# Refs
    
[ref](#ref) 

[refs](#refs⦙) 
    
.
# Ref

# Refs
    
[ref](#ref) 

[refs](#ref⦙) 
    
.
---- Before Action -----------------------------------------------------

---- After Action ------------------------------------------------------
````````````````````````````````


## Definition Counts

### Reference

```````````````````````````````` example(Definition Counts - Reference: 1) options(reference-definition-counts)
[ref]: example.com

[ref]
    
.
.
REFERENCE: { ref: 1 }
````````````````````````````````


```````````````````````````````` example(Definition Counts - Reference: 2) options(reference-definition-counts, backspace)
[ref]: example.com

[ref]
    
[refs⦙]: example.com
    
.
[ref]: example.com

[ref]
    
[ref⦙]: example.com
    
.
---- Before Action -----------------------------------------------------
REFERENCE: { ref: 1, refs: 1 }

---- After Action ------------------------------------------------------
REFERENCE: { ref: 2 }
````````````````````````````````


### Abbreviations

```````````````````````````````` example(Definition Counts - Abbreviations: 1) options(reference-definition-counts, parser-abbreviations)
*[ref]: example.com

ref
    
.
.
ABBREVIATION: { ref: 1 }
````````````````````````````````


```````````````````````````````` example(Definition Counts - Abbreviations: 2) options(reference-definition-counts, backspace, parser-abbreviations)
*[ref]: example.com

*[refs⦙]: example.com

ref
  
.
*[ref]: example.com

*[ref⦙]: example.com

ref
  
.
---- Before Action -----------------------------------------------------
ABBREVIATION: { ref: 1, refs: 1 }

---- After Action ------------------------------------------------------
ABBREVIATION: { ref: 2 }
````````````````````````````````


### RefAnchor

#### A Tag

Not applicable, has out of file references, use MdReferenceSearch for these

```````````````````````````````` example(Definition Counts - RefAnchor - A Tag: 1) options(reference-definition-counts, parser-parse-html-anchor-id)
ref <a id="ref">

[ref](#ref) 
    
.
.
````````````````````````````````


```````````````````````````````` example(Definition Counts - RefAnchor - A Tag: 2) options(reference-definition-counts, backspace, parser-parse-html-anchor-id)
ref <a id="ref">
    
refs <a id="refs⦙">

[ref](#ref) 
    
.
ref <a id="ref">
    
refs <a id="ref⦙">

[ref](#ref) 
    
.
---- Before Action -----------------------------------------------------

---- After Action ------------------------------------------------------
````````````````````````````````


#### Heading

Not applicable, has out of file references, use MdReferenceSearch for these

```````````````````````````````` example(Definition Counts - RefAnchor - Heading: 1) options(reference-definition-counts, parser-attributes-ext)
# Ref

[ref](#ref) 
.
.
````````````````````````````````


```````````````````````````````` example(Definition Counts - RefAnchor - Heading: 2) options(reference-definition-counts, backspace, parser-attributes-ext)
# Ref

# Refs⦙
    
[ref](#ref) 

.
# Ref

# Ref⦙
    
[ref](#ref) 

.
---- Before Action -----------------------------------------------------

---- After Action ------------------------------------------------------
````````````````````````````````


