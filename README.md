idea-multimarkdown
==================

**[Markdown](http://daringfireball.net/projects/markdown) language support for [IntelliJ IDEA](http://www.jetbrains.com/idea), [RubyMine](http://www.jetbrains.com/ruby), [PhpStorm](http://www.jetbrains.com/phpstorm), [WebStorm](http://www.jetbrains.com/webstorm), [PyCharm](http://www.jetbrains.com/pycharm), [AppCode](http://www.jetbrains.com/objc) and [Android Studio](http://developer.android.com/sdk/installing/studio.html).**

**<span color="#c00038">If you like this plugin, please leave a comment or rate it on the</span> [official plugin page](https://plugins.jetbrains.com/plugin/writeComment?pr=&pluginId=7896).**

The Full Story
--------------

It all started with a desire to see Markdown files in PhpStorm IDE as they would look on GitHub. I was already using [nicoulaj/idea-markdown plugin](https://github.com/nicoulaj/idea-markdown) but found its preview was more like [Craig's List](http://montreal.en.craigslist.ca/) than [GitHub](https://github.com/vsch/laravel-translation-manager). It did not appear to have been recently updated, so I decided to fork it and modify the style sheet it uses. How hard could that be?

I found out quickly that there was more to it than meets the eye. Rendering is done by Java not a browser, the parser is HTML 3.1 and not all features are implemented. Additionally, the Table extension did not work in the version of `pegdown` used by the plugin. I needed that because maintaining HTML tables is a pain. So I upgraded the plugin to use the latest `pegdown`, `parboiled` and fixed a few bugs. Since I was already in the code, I might as well add a few more desired features like user editable style sheet, fix a few more bugs, add updates to preview so that I could split the editor pane and edit in one while seeing the preview in the other.

Then I encountered some bugs in parsing of compound nested lists in `pegdown` and had to dive into its source to fix them. Having done that and gotten familiar with it, I decided to add a new extension. Finally, to help me with debugging and generating test expectations for `pegdown`, I had to have the HTML Text tab to display the generated HTML.

It has been a fun trip down the rabbit hole of IntelliJ IDEA plugin development that started with a simple desire for a Markdown preview that looked like GitHub's.

Hope you enjoy using this plugin and **if you do then please leave a comment or rate it on the [official plugin page](https://plugins.jetbrains.com/plugin/writeComment?pr=&pluginId=7896).**

Enhancements
-------------

- [x] Striped tables
- [x] GitHub style task list items
- [x] CSS editable in settings
- [x] Option to require a space after `#` for Atx headers
- [x] HTML Text tab to view the generated HTML
- [x] syntax highlighting, color striped tables by row and column in your source
- [x] Default and Darcula for syntax highlighting and for HTML Preview supported.
    CSS Style sheet for HTML Perview selectable in Settings/Other Settings/MultiMarkdown.
- [x] Add Lexer to use IntelliJ standard features
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

This plugin is using a modified version of [sirthias/pegdown](https://github.com/sirthias), I post my PR's but there is always a delay in both generating them, for them to be merged.
I added a few changes and extensions to the parser. For now I am using my forked copy until the official version has all the features. 

The pegdown source used in this plugin can be found [vsch/pegdown](https://github.com/vsch/pegdown/tree/develop).

---

\* This plugin is based on the [Markdown plugin](https://github.com/nicoulaj/idea-markdown) by [nicoulaj](https://github.com/nicoulaj), which is based on [pegdown](http://pegdown.org) library by [sirthias](https://github.com/sirthias).
     
