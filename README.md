idea-multimarkdown
==================

**[Markdown](http://daringfireball.net/projects/markdown) language support for [IntelliJ IDEA](http://www.jetbrains.com/idea), [RubyMine](http://www.jetbrains.com/ruby), [PhpStorm](http://www.jetbrains.com/phpstorm), [WebStorm](http://www.jetbrains.com/webstorm), [PyCharm](http://www.jetbrains.com/pycharm), [AppCode](http://www.jetbrains.com/objc) and [Android Studio](http://developer.android.com/sdk/installing/studio.html).**

**You can download it on the [JetBrains plugin page](https://plugins.jetbrains.com/plugin?pr=&pluginId=7896).**

**For any communications requiring a reply** please use the [GitHub Issues page](../../issues) for this plugin. There is no ability to reply to comments left on the [JetBrains plugin comment and rate page](https://plugins.jetbrains.com/plugin/writeComment?pr=&pluginId=7896).

**[Wiki Pages](../../wiki) added** with instructions on [how to include your GitHub wiki in the IntelliJ IDE](../../wiki/Adding-GitHub-Wiki-to-Your-IntelliJ-Project) so you can work on the wiki in the IDE and use the plugin for syntax highlighting and preview. Makes adding images and manipulating the wiki a lot easier.

#### Two tier model of the plugin

1. Free and open source for the basic functionality.

2. Licensed version with enhanced refactoring and code completion functionality. 30-day free trial licenses are available in plugin preferences and from [idea-multimarkdown](http://vladsch.com/product/multimarkdown)

Latest Developments, version 1.3.x
----------------------------------

Latest release 1.3.2 is a bug fix and stability release, fixing issues that arose since the big code churn release 1.3.0.

Another three week rewrite of the old infrastructure to be able to split editor support for markdown files. Now you can have split preview with either rendered or text HTML beside your markdown source.

User interface for settings has been moved under **Languages & Frameworks**, split into sections with new customizations added for html generation. Now can add CSS url, CSS Text or even extras directly in HTML with four insertion points: head top, head bottom, body top, body bottom.

Easier to show than explain, see screenshots below. For a summary of changes please see [Version Notes](resources/META-INF/VERSION.md)

### Still Great GitHub Rendering Resemblance for your preview pleasure

##### One of the images is idea-multimarkdown preview, the other of same file on GitHub: 

![Screen Shot Jfx Webview](assets/images/ScreenShot_jfx_webview.png)

Plugins that are a great compliment to idea-multimarkdown
---------------------------------------------------------

[Pipe Table Formatter](https://github.com/anton-dev-ua/PipeTableFormatter) A plugin that is great for formatting tables. I use to pretty up the tables in markdown and find it a great compliment to idea-multimarkdown.

[.gitignore](http://hsz.mobi) to add .gitignore file brains to the IDE and allows idea-multimarkdown to show files and links to these files that will only resolve locally because they are not under git vcs.
                                                        
The Background
--------------

It all started with a desire to see Markdown files in PhpStorm IDE as they would look on GitHub. I was already using [nicoulaj/idea-markdown plugin](https://github.com/nicoulaj/idea-markdown) but found its preview was more like [Craig's List](http://montreal.en.craigslist.ca/) than [GitHub](https://github.com/vsch/laravel-translation-manager). It did not appear to have been recently updated, so I decided to fork it and modify the style sheet it uses. How hard could that be?

I found out quickly that there was more to it than meets the eye. Rendering is done by Java not a browser, the parser is HTML 3.1 and not all features are implemented. Additionally, the Table extension did not work in the version of `pegdown` used by the plugin. I needed that because maintaining HTML tables is a pain. So I upgraded the plugin to use the latest `pegdown`, `parboiled` and fixed a few bugs. Since I was already in the code, I might as well add a few more desired features like user editable style sheet, fix a few more bugs, add updates to preview so that I could split the editor pane and edit in one while seeing the preview in the other.

Then I encountered some bugs in parsing of compound nested lists in `pegdown` and had to dive into its source to fix them. Having done that and gotten familiar with it, I decided to add a new extension. Finally, to help me with debugging and generating test expectations for `pegdown`, I had to have the HTML Text tab to display the generated HTML.

It has been a fun trip down the rabbit hole of IntelliJ IDEA plugin development that started with a simple desire for a Markdown preview that looked like GitHub's.

Enhancements
------------

- [x] Split Editor
- [x] Striped tables
- [x] GitHub style task list items
- [x] CSS editable in settings
- [x] Option to require a space after `#` for Atx headers
- [x] HTML Text tab to view the generated HTML
- [x] syntax highlighting, color striped tables by row and column in your source
- [x] Default and Darcula for syntax highlighting and for HTML Preview supported.
- [x] Add Lexer to use IntelliJ standard features
- [x] Add Standard HTML/CSS rendering engine to make styling easier.
- [x] Add PsiBuilder compatible parser to implement expected comforts:
    - [ ] formatting
    - [x] navigation
    - [x] refactoring
    - [ ] document structure display

Screenshots
-----------

![idea-multimarkdown-source](assets/images/ScreenShot_source_preview.png)   

### Split your editor and see the preview as you type

![idea-multimarkdown](assets/images/ScreenShot_preview.png)

### Peek at the HTML

![idea-multimarkdown-settings](assets/images/ScreenShot_peek_html.png)

#### Change options, customize the syntax colors and CSS to your liking.

![idea-multimarkdown-settings](assets/images/ScreenShot_settings_color.png)

![idea-multimarkdown-settings](assets/images/ScreenShot_settings_markdown.png)
 
![idea-multimarkdown-settings](assets/images/ScreenShot_settings_parser.png)

![idea-multimarkdown-settings](assets/images/ScreenShot_settings_css.png)

![idea-multimarkdown-settings](assets/images/ScreenShot_settings_html.png)

Forking
-------

This plugin is using a modified version of [sirthias/pegdown](https://github.com/sirthias), I post my PR's but there is always a delay in both generating them and for them to be merged.
I added a few changes and extensions to the parser. For now I am using my forked copy until the official version has all the features.

The pegdown source used in this plugin can be found [vsch/pegdown](https://github.com/vsch/pegdown/tree/develop).

---

\* This plugin is based on the [Markdown plugin](https://github.com/nicoulaj/idea-markdown) by [nicoulaj](https://github.com/nicoulaj), which is based on [pegdown](http://pegdown.org) library by [sirthias](https://github.com/sirthias).

