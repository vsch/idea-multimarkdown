idea-multimarkdown
==================

**[Markdown](http://daringfireball.net/projects/markdown) language support for [IntelliJ IDEA](http://www.jetbrains.com/idea), [RubyMine](http://www.jetbrains.com/ruby), [PhpStorm](http://www.jetbrains.com/phpstorm), [WebStorm](http://www.jetbrains.com/webstorm), [PyCharm](http://www.jetbrains.com/pycharm), [AppCode](http://www.jetbrains.com/objc) and [Android Studio](http://developer.android.com/sdk/installing/studio.html).**

**You can download it on the [JebBrains plugin page](https://plugins.jetbrains.com/plugin?pr=&pluginId=7896).**

**For any communications requiring a reply** please use the [GitHub Issues page](../../issues) for this plugin. There is no ability to reply to comments left on the [JetBrains plugin comment and rate page](https://plugins.jetbrains.com/plugin/writeComment?pr=&pluginId=7896).

**[Wiki Pages](../../wiki) added** with instructions on [how to include your GitHub wiki in the IntelliJ IDE](../../wiki/Adding-GitHub-Wiki-To-Your-IntelliJ-Project) so you can work on the wiki in the IDE and use the plugin for syntax highlighting and preview. Makes adding images and manipulating the wiki a lot easier.

#### This plugin is in the process of migrating to a two tier model

1. Free and open source for the basic functionality as it is present now. I will continue to support it, respond to issues and fix bugs. If you only need the basic functionality of editing and previewing Markdown, then you will not be affected and will continue to enjoy using this plugin with the same level of support you have received thus far.

2. Licensed model with a 30 day trial period which will implement parser based advanced features such as refactoring, find usages, etc. (see issue #12 for the roadmap). After the 30 day trial it will revert to the functionality of the free, open source model. $25 purchase of the license will give you free upgrades and maintenance for one year on the advanced features. I have to commercialize this plugin so that I can devote my full attention to its development. I feel that doing this will benefit both free and licensed versions because I will be able to concentrate my attention on delivering a quality product in the shortest amount of time.

**Everyone who has generously given a tip** will get a one year license for every $20 that they have donated, plus one extra year as my show of appreciation for your support. ETA is October 15th, 2015.

### Version 1.1.7 Adds highlight.js syntax highlighting and wiki link refactoring/completions

* new preview tab now supports [highlight.js](https://highlightjs.org/) for syntax highlighting verbatim code blocks.

Wiki links are the first to get the boost to IntelliJ intelligence:

* Wiki link page references are linked to the files they target. For example `[[NOTICE]]` refers to `NOTICE.md` in the same directory as the file containing the link. 
* Auto completion of Wiki link text part ie. `[[LinkRef|Text]]` will take the text from the linkRef and present several variations. Also any link texts that are the same on the page can be renamed by using refactor/rename operation to change all of them in one operation.
* Refactor rename operation on a wiki link does a file rename operation with all links that refer to it also changed.
* Moving a file within the project tree also updates the links. 
* Navigate to the file a wiki link targets in the markdown source via go to definition or line marker. 
* Find all wiki link references to a markdown file in the project via find usages on the file node in the project view
* Quick fix to create a wiki markdown file for wiki links with missing files, also checks for spaces in target file name, dashes in wiki link target reference and mismatched case between link target and file name of the target.
* Different icon <img src="src/main/resources/com/vladsch/idea/multimarkdown/multimarkdown%402x.png" width="16" height="17" alt="MultiMarkdown file Icon"> for markdown files, <img src="src/main/resources/com/vladsch/idea/multimarkdown/wikimarkdown%402x.png" width="16" height="17" alt="Wiki Page Icon"> is used for files whose ancestor directory has a `.wiki` extension. These are treated as GitHub wiki pages and will display pretty much as they do on GitHub, minus the footer and sidebar. Footer and sidebar are TBI for MultiMarkdown. 

* Many more changes, see the plugin version notes on the JetBrains page or in the IDE <kbd>Settings > Plugins > MultiMarkdown</kbd>

*[TBI]:To Be Implemented.

### Version 1.1.x out and humming

I have implemented a JavaFX WebView based HTML Preview Tab and the results are stunning. Not only was I able to make it look like GitHub's markdown, which I could not do with the old preview, but it is a joy to work with and maintain compared to `HTMLEditorKit`.

Here is a screenshot of the plugin's HTML Preview and GitHub of this readme file:

![Preview](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_jfx_webview.png)

To get the new preview tab you need to configure your IDEA to use JDK 1.8 that includes `jfxrt.jar` in the jdk's `lib/ext` directory, which is its standard location. If `jfxrt.jar` is not found or the plugin cannot create the new editor class, it will fall back to using the `JEditorPane` with `HTMLEditorKit` preview. You can also disable using JavaFX based preview in plugin settings, for those that prefer to keep using the old preview.

You **cannot** get the new preview if the boot JDK is the **IDEA bundled JDK**, which does not include `jfxrt.jar` nor the native libraries that it uses.

#### PHPStorm 9.5 EAP, WebStorm 11 EAP on OS X with bundled JDK

You can switch the boot jdk from the GUI. However, due to a bug they keep booting with the bundled jdk. The bug and the workaround is similar.

- PhpStorm: (last checked in PS-142.4491) To change the boot jdk you need to add a `phpstorm.jdk` file with the path to the alternate jdk, for example:

        /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk

    to the `/Library/Preferences/WebIde95` directory. Restart PhpStorm.

- WebStorm: (last checked in WS-142.4723) To change the boot jdk you need to do one of:
    1. add a `webstorm.jdk` file with the path to the alternate jdk, for example:

            /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk

    2. use the GUI to select the JDK but then rename `wstorm.jdk` created by the gui to `webstorm.jdk` that it uses to get the boot jdk setting

    in the `/Library/Preferences/WebStorm11` directory. Restart WebStorm.

The Background
--------------

It all started with a desire to see Markdown files in PhpStorm IDE as they would look on GitHub. I was already using [nicoulaj/idea-markdown plugin](https://github.com/nicoulaj/idea-markdown) but found its preview was more like [Craig's List](http://montreal.en.craigslist.ca/) than [GitHub](https://github.com/vsch/laravel-translation-manager). It did not appear to have been recently updated, so I decided to fork it and modify the style sheet it uses. How hard could that be?

I found out quickly that there was more to it than meets the eye. Rendering is done by Java not a browser, the parser is HTML 3.1 and not all features are implemented. Additionally, the Table extension did not work in the version of `pegdown` used by the plugin. I needed that because maintaining HTML tables is a pain. So I upgraded the plugin to use the latest `pegdown`, `parboiled` and fixed a few bugs. Since I was already in the code, I might as well add a few more desired features like user editable style sheet, fix a few more bugs, add updates to preview so that I could split the editor pane and edit in one while seeing the preview in the other.

Then I encountered some bugs in parsing of compound nested lists in `pegdown` and had to dive into its source to fix them. Having done that and gotten familiar with it, I decided to add a new extension. Finally, to help me with debugging and generating test expectations for `pegdown`, I had to have the HTML Text tab to display the generated HTML.

It has been a fun trip down the rabbit hole of IntelliJ IDEA plugin development that started with a simple desire for a Markdown preview that looked like GitHub's.

Hope you enjoy using this plugin and **if you do then please leave a comment or rate it on the [official plugin page](https://plugins.jetbrains.com/plugin/writeComment?pr=&pluginId=7896).**

Enhancements
------------

- [x] Striped tables
- [x] GitHub style task list items
- [x] CSS editable in settings
- [x] Option to require a space after `#` for Atx headers
- [x] HTML Text tab to view the generated HTML
- [x] syntax highlighting, color striped tables by row and column in your source
- [x] Default and Darcula for syntax highlighting and for HTML Preview supported.
    CSS Style sheet for HTML Perview selectable in Settings/Other Settings/MultiMarkdown.
- [x] Add Lexer to use IntelliJ standard features
- [x] Add Standard HTML/CSS rendering engine to make styling easier.
- [ ] Add PsiBuilder compatible parser to implement expected comforts:
    - [ ] formatting
    - [ ] navigation
    - [ ] document structure display

Screenshots
-----------

![idea-multimarkdown-source](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_source_preview.png)

### Split your editor and see the preview as you type

![idea-multimarkdown](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_preview.png)

### Peek at the HTML

![idea-multimarkdown-settings](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_html.png)

#### Change options, customize the syntax colors and CSS to your liking.
![idea-multimarkdown-settings](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_color_settings.png)

![idea-multimarkdown-settings](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_settings.png)

Forking
-------

This plugin is using a modified version of [sirthias/pegdown](https://github.com/sirthias), I post my PR's but there is always a delay in both generating them and for them to be merged.
I added a few changes and extensions to the parser. For now I am using my forked copy until the official version has all the features.

The pegdown source used in this plugin can be found [vsch/pegdown](https://github.com/vsch/pegdown/tree/develop).

---

\* This plugin is based on the [Markdown plugin](https://github.com/nicoulaj/idea-markdown) by [nicoulaj](https://github.com/nicoulaj), which is based on [pegdown](http://pegdown.org) library by [sirthias](https://github.com/sirthias).

