---
title: Injection Spec Test
author:
version:
date: '2019-12-13'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

# Injection Host

* [ ] Test: html block
* [ ] Test: inline html
* [ ] Test: fenced code
  * [ ] Test: puml
  * [ ] Test: plant uml
  * [ ] Test: math
  * [ ] Test: latex
* [ ] Test: indented code
* [ ] Test: multi-line image URL

<!--
:information_source: default action `tab` defined in
[TabHandlerSpecTest.java: Lines 45-51](TabHandlerSpecTest.java#L45-L51)
-->

## Inline Code

Insert enough markers to keep internal markers wrapped

```````````````````````````````` example(Inline Code: 1) options(inject[``])
``class Test { static String test = "⦙"; boolean main() { return true; } }``
.
```class Test { static String test = ⦙"``"; boolean main() { return true; } }```
````````````````````````````````


Insert `&nbsp`; when at start of paragraph and inserting EOL

```````````````````````````````` example(Inline Code: 2) options(inject[\n])
```class Test { static String test = "``"; boolean main() { return true; } ⦙}```
.
&nbsp;```class Test { static String test = "``"; boolean main() { return true; } ⦙
}```
````````````````````````````````


### In List

Insert enough markers to keep internal markers wrapped

```````````````````````````````` example(Inline Code - In List: 1) options(inject[``])
* item

  ``class Test { static String test = "⦙"; boolean main() { return true; } }``
.
* item

  ```class Test { static String test = ⦙"``"; boolean main() { return true; } }```
````````````````````````````````


```````````````````````````````` example(Inline Code - In List: 2) options(inject[``])
* ``class Test { static String test = "⦙"; boolean main() { return true; } }``
.
* ```class Test { static String test = ⦙"``"; boolean main() { return true; } }```
````````````````````````````````


Insert `&nbsp`; when at start of paragraph and inserting EOL

```````````````````````````````` example(Inline Code - In List: 3) options(inject[\n])
* item

  ```class Test { static String test = "``"; boolean main() { return true; } ⦙}```
.
* item

  &nbsp;```class Test { static String test = "``"; boolean main() { return true; } ⦙
  }```
````````````````````````````````


```````````````````````````````` example(Inline Code - In List: 4) options(inject[\n])
* ```class Test { static String test = "``"; boolean main() { return true; } ⦙}```
.
* ```class Test { static String test = "``"; boolean main() { return true; } ⦙
  }```
````````````````````````````````


## Fenced Code

### Basic

```````````````````````````````` example(Fenced Code - Basic: 1) options(inject[text])
```text
⦙add after blank line
```
.
```text
⦙textadd after blank line
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 2) options(inject[text])
```php
⦙add after blank line
```
.
```php
⦙textadd after blank line
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 3) options(inject[\n])
```text
⦙add after blank line
```
.
```text
⦙
add after blank line
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 4) options(inject[\n])
```php
⦙add after blank line
```
.
```php
⦙
add after blank line
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 5) options(inject[text\n])
```text
⦙add after blank line
```
.
```text
⦙text
add after blank line
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 6) options(inject[text\n])
```php
⦙add after blank line
```
.
```php
⦙text
add after blank line
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 7) options(inject[text])
```text
add after blank line
⦙```
.
```text
add after blank line
⦙text
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 8) options(inject[text])
```php
add after blank line
⦙```
.
```php
add after blank line
⦙text
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 9) options(inject[\n])
```text
add after blank line
⦙```
.
```text
add after blank line
⦙
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 10) options(inject[\n])
```php
add after blank line
⦙```
.
```php
add after blank line
⦙
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 11) options(inject[text\n])
```text
add after blank line
⦙```
.
```text
add after blank line
⦙text
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Basic: 12) options(inject[text\n])
```php
add after blank line
⦙```
.
```php
add after blank line
⦙text
```
````````````````````````````````


### List Item

```````````````````````````````` example(Fenced Code - List Item: 1) options(inject[text])
* item
  ```text
  ⦙add after blank line
  ```
.
* item
  ```text
  ⦙textadd after blank line
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 2) options(inject[text])
* item
  ```php
  ⦙add after blank line
  ```
.
* item
  ```php
  ⦙textadd after blank line
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 3) options(inject[\n])
* item
  ```text
  ⦙add after blank line
  ```
.
* item
  ```text
  ⦙
  add after blank line
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 4) options(inject[\n])
* item
  ```php
  ⦙add after blank line
  ```
.
* item
  ```php
  ⦙
  add after blank line
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 5) options(inject[text\n])
* item
  ```text
  ⦙add after blank line
  ```
.
* item
  ```text
  ⦙text
  add after blank line
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 6) options(inject[text\n])
* item
  ```php
  ⦙add after blank line
  ```
.
* item
  ```php
  ⦙text
  add after blank line
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 7) options(inject[text])
* item
    
  ```text
  add after blank line
  ⦙```
.
* item
    
  ```text
  add after blank line
  ⦙text
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 8) options(inject[text])
* item
    
  ```php
  add after blank line
  ⦙```
.
* item
    
  ```php
  add after blank line
  ⦙text
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 9) options(inject[\n])
* item
    
  ```text
  add after blank line
  ⦙```
.
* item
    
  ```text
  add after blank line
  ⦙
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 10) options(inject[\n])
* item
    
  ```php
  add after blank line
  ⦙```
.
* item
    
  ```php
  add after blank line
  ⦙
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 11) options(inject[text\n])
* item
    
  ```text
  add after blank line
  ⦙```
.
* item
    
  ```text
  add after blank line
  ⦙text
  ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - List Item: 12) options(inject[text\n])
* item
    
  ```php
  add after blank line
  ⦙```
.
* item
    
  ```php
  add after blank line
  ⦙text
  ```
````````````````````````````````


### Block Quote

```````````````````````````````` example(Fenced Code - Block Quote: 1) options(inject[text])
>```text
>⦙add after blank line
>```
.
>```text
>⦙textadd after blank line
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 2) options(inject[text])
>```php
>⦙add after blank line
>```
.
>```php
>⦙textadd after blank line
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 3) options(inject[\n])
>```text
>⦙add after blank line
>```
.
>```text
>⦙
>add after blank line
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 4) options(inject[\n])
>```php
>⦙add after blank line
>```
.
>```php
>⦙
>add after blank line
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 5) options(inject[text\n])
>```text
>⦙add after blank line
>```
.
>```text
>⦙text
>add after blank line
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 6) options(inject[text\n])
>```php
>⦙add after blank line
>```
.
>```php
>⦙text
>add after blank line
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 7) options(inject[text])
>```text
>add after blank line
>⦙```
.
>```text
>add after blank line
>⦙text
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 8) options(inject[text])
>```php
>add after blank line
>⦙```
.
>```php
>add after blank line
>⦙text
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 9) options(inject[\n])
>```text
>add after blank line
>⦙```
.
>```text
>add after blank line
>⦙
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 10) options(inject[\n])
>```php
>add after blank line
>⦙```
.
>```php
>add after blank line
>⦙
>```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Block Quote: 11) options(inject[text\n])
>```text
>add after blank line
>⦙```
.
>```text
>add after blank line
>⦙text
>```
````````````````````````````````


### Mixed

```````````````````````````````` example(Fenced Code - Mixed: 1) options(inject[text\n])
> * item 
>
>   ```php
>   add after blank line
>   ⦙```
.
> * item 
>
>   ```php
>   add after blank line
>   ⦙text
>   ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Mixed: 2) options(inject[text\n])
> * item 
>   ```php
>   add after blank line
>   ⦙```
.
> * item 
>   ```php
>   add after blank line
>   ⦙text
>   ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Mixed: 3) options(inject[text\n])
> * item 
>   >```php
>   >add after blank line
>   >⦙```
.
> * item 
>   >```php
>   >add after blank line
>   >⦙text
>   >```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Mixed: 4) options(inject[text\n])
> * item 
>   > ```php
>   > add after blank line
>   > ⦙```
.
> * item 
>   > ```php
>   > add after blank line
>   > ⦙text
>   > ```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Mixed: 5) options(inject[text\n])
> * >item
>   >```php
>   >add after blank line
>   >⦙```
.
> * >item
>   >```php
>   >add after blank line
>   >⦙text
>   >```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Mixed: 6) options(inject[text\n])
> * > item
>   > ```php
>   > add after blank line
>   > ⦙```
.
> * > item
>   > ```php
>   > add after blank line
>   > ⦙text
>   > ```
````````````````````````````````


### Issues

```````````````````````````````` example(Fenced Code - Issues: 1) options(inject[text\n])
# find-parent-dir [![build status](https://secure.travis-ci.org/thlorenz/find-parent-dir.png)](http://travis-ci.org/thlorenz/find-parent-dir)

Finds the first parent directory that contains a given file or directory.

    npm install find-parent-dir

```js
// assuming this is called from a file in a subdirectory of /myprojects/foo which contains .git directory
var findParentDir = require('find-parent-dir');

findParentDir(__dirname, '.git', function (err, dir) {
  // has err if some file access error occurred
  console.log(dir); // => /myprojects/foo/
  
  // if parent dir wasn't found, dir is null
})

// Same using `sync` method
var dir;
try { 
  dir = findParentDir.sync(__dirname, '.git');
  console.log(dir); // => /myprojects/foo/
  // if parent dir wasn't found, dir is null
} catch(err) {
  console.error('error', err);⦙ 
}
```
.
# find-parent-dir [![build status](https://secure.travis-ci.org/thlorenz/find-parent-dir.png)](http://travis-ci.org/thlorenz/find-parent-dir)

Finds the first parent directory that contains a given file or directory.

    npm install find-parent-dir

```js
// assuming this is called from a file in a subdirectory of /myprojects/foo which contains .git directory
var findParentDir = require('find-parent-dir');

findParentDir(__dirname, '.git', function (err, dir) {
  // has err if some file access error occurred
  console.log(dir); // => /myprojects/foo/
  
  // if parent dir wasn't found, dir is null
})

// Same using `sync` method
var dir;
try { 
  dir = findParentDir.sync(__dirname, '.git');
  console.log(dir); // => /myprojects/foo/
  // if parent dir wasn't found, dir is null
} catch(err) {
  console.error('error', err);⦙text
 
}
```
````````````````````````````````


```````````````````````````````` example(Fenced Code - Issues: 2) options(inject[\n])
# find-parent-dir [![build status](https://secure.travis-ci.org/thlorenz/find-parent-dir.png)](http://travis-ci.org/thlorenz/find-parent-dir)

Finds the first parent directory that contains a given file or directory.

    npm install find-parent-dir

```js
    
// assuming this is called from a file in a subdirectory of /myprojects/foo which contains .git directory
var findParentDir = require('find-parent-dir');

findParentDir(__dirname, '.git', function (err, dir) {
  // has err if some file access error occurred
  console.log(dir); // => /myprojects/foo/
  
  // if parent dir wasn't found, dir is null
})⦙
```
.
# find-parent-dir [![build status](https://secure.travis-ci.org/thlorenz/find-parent-dir.png)](http://travis-ci.org/thlorenz/find-parent-dir)

Finds the first parent directory that contains a given file or directory.

    npm install find-parent-dir

```js
    
// assuming this is called from a file in a subdirectory of /myprojects/foo which contains .git directory
var findParentDir = require('find-parent-dir');

findParentDir(__dirname, '.git', function (err, dir) {
  // has err if some file access error occurred
  console.log(dir); // => /myprojects/foo/
  
  // if parent dir wasn't found, dir is null
})⦙

```
````````````````````````````````


## Image URL

```````````````````````````````` example(Image URL: 1) options(inject[a])
![LaTex Embedded Image](http://latex.codecogs.com/gif.latex?
\begin{⦙lign*}
x^2 + y^2 &= 1 \\
y &= \sqrt{1 - x^2}
\end{align*}
)
.
![LaTex Embedded Image](http://latex.codecogs.com/gif.latex?
\begin{⦙align*}
x^2 + y^2 &= 1 \\
y &= \sqrt{1 - x^2}
\end{align*}
)
````````````````````````````````


