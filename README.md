idea-multimarkdown
==================
       
idea-multimarkdown plugin provides **[Markdown] language support for [IntelliJ IDEA], [RubyMine], [PhpStorm], [WebStorm], [PyCharm], [AppCode], [Android Studio], [CLion] and [DataGrip].**

**You can download it on the [JetBrains plugin page].**

The aim of this plugin is to be able to generate a preview that it is as close as possible to how the page will look on GitHub but do it with more IntelliJ environment intelligence to make editing and maintaining markdown documents easier. Developing with pleasure is only half the job. Real projects need to be documented. This plugin wants to extend the pleasure principle to the inevitable documentation part of the project.     

GitHub may recognize variants of Markdown syntax that this plugin will not and vice versa. This is due to the parser differences and the fact that GitHub has a few syntax modifications that conflict with original Markdown spec. This plugin uses [pegdown] library by [sirthias] with a few extensions added to make the rendering of GFM more faithful.

**For any communications requiring a reply** please use the [GitHub Issues page] for this plugin. There is no ability to reply to comments left on the [JetBrains plugin comment and rate page].

**[Wiki] added** with instructions on [how to include your GitHub wiki in the IntelliJ IDE](../../wiki/Adding-GitHub-Wiki-to-Your-IntelliJ-Project) so you can work on the wiki in the IDE and use the plugin for syntax highlighting,preview, link completions and validation. This makes adding images and manipulating the wiki a lot easier.

#### Two tier model of the plugin

1. Previewing and syntax highlighting functionality is available in the Basic open source edition. Intended for mostly previewing markdown documents. Wiki link refactoring and completions are also available in the basic edition to ease the task of wiki maintenance. 

2. Advanced features used for creating and maintaining markdown documents such as split editor, refactoring, find usages, validation, auto formatting and HTML page customizations are only available in the Enhanced licensed version. 30-day free trial licenses are available from [idea-multimarkdown] page on my website. 

Latest Developments: Version 1.4.3
----------------------------------

#### Minor Improvements & Bug Fixes

- GitHub issue # completions added in plain text and `issues/` link
- Option to view GitHub page in preview if the document was committed and has not been modified.
- Added more elements to code folding and added configuration for collapse by default in settings/preferences
- Improved link completions and added changing of completion addressing format by repeated invocations of the completion action

#### File Move Refactoring added in 1.4.2

For a full list see the [Version Notes]

* Implemented refactoring of links contained in the file being moved. Link format is preserved, wiki links will be changed to explicit links if the file is moved out of the wiki directory.
* **To Keep your links in sync** use rename refactoring for: 
    * link addresses
    * anchor references and header text
    * references and reference links
* Intentions implemented to replace annotator quick fixes. Now intentions can be individually disabled and no more weak warning highlighting just to get the quick fix to show up. 
                     
#### Language Injections added in 1.4.1

* Fixed bug in split editor that would prevent injected language fragment editor from being resizeable.

Language injections added to verbatim, fenced code, HTML blocks and multi-line URL content. Also auto completions, validation, refactoring for all link elements is now done, including anchor reference completions to available headers in the target file. Footnote completions and validation were also done.

Language injections are at the top of my favorite feature list of IntelliJ. Here is a demo of nested injections: 

* Java injected into Kotlin String
* in Kotlin code which is injected into a idea-multimarkdown fenced code block
* fenced code block located in a nested block quote   

##### Editing is performed on the Java code fragment

![Nested Code Injection](/assets/images/NestedCodeInjection.gif)

### Still Great GitHub Rendering Resemblance for your preview pleasure

##### One of the images is idea-multimarkdown preview, the other of same file on GitHub: 

![Screen Shot Jfx WebView](/assets/images/ScreenShot_jfx_webview.png)

Plugins that are a great compliment to idea-multimarkdown
---------------------------------------------------------

[Pipe Table Formatter] A plugin that is great for formatting markdown tables. I use to pretty up the tables in markdown and find it a great compliment to idea-multimarkdown.

[.gitignore] to add .gitignore file brains to the IDE and allows idea-multimarkdown to show files and links to files that will only resolve locally because they are not under git vcs.
                                                        
The Background
--------------

It all started with a desire to see Markdown files in PhpStorm IDE as they would look on GitHub. I was already using [nicoulaj/idea-markdown plugin] but found its preview was more like [Craig's List] than [GitHub]. It did not appear to have been recently updated, so I decided to fork it and modify the style sheet it uses. How hard could that be?

I found out quickly that there was more to it than meets the eye. Rendering is done by Java not a browser, the parser is HTML 3.1 and not all features are implemented. Additionally, the Table extension did not work in the version of `pegdown` used by the plugin. I needed that because maintaining HTML tables is a pain. So I upgraded the plugin to use the latest `pegdown`, `parboiled` and fixed a few bugs. Since I was already in the code, I might as well add a few more desired features like user editable style sheet, fix a few more bugs, add updates to preview so that I could split the editor pane and edit in one while seeing the preview in the other.

Then I encountered some bugs in parsing of compound nested lists in `pegdown` and had to dive into its source to fix them. Having done that and gotten familiar with it, I decided to add a new extension. Finally, to help me with debugging and generating test expectations for `pegdown`, I had to have the HTML Text tab to display the generated HTML.

It has been a fun trip down the rabbit hole of IntelliJ IDEA plugin development that started with a simple desire for a Markdown preview that looked like GitHub's.

Screenshots
-----------

![idea-multimarkdown-source](/assets/images/ScreenShot_source_preview.png)   

### Split your editor and see the preview as you type

![idea-multimarkdown](/assets/images/ScreenShot_preview.png)

### Peek at the HTML

![idea-multimarkdown-settings](/assets/images/ScreenShot_peek_html.png)

#### Change options, customize the syntax colors and CSS to your liking.

![Screen Shot Settings Intentions](/assets/images/ScreenShot_settings_intentions.png)

![Screen Shot Settings Color](/assets/images/ScreenShot_settings_color.png)

![Screen Shot Settings Markdown](/assets/images/ScreenShot_settings_markdown.png)
 
![Screen Shot Settings Parser](/assets/images/ScreenShot_settings_parser.png)

![Screen Shot Settings Css](/assets/images/ScreenShot_settings_css.png)

![Screen Shot Settings Html](/assets/images/ScreenShot_settings_html.png)

Forking
-------

This plugin is using a modified version of [sirthias], I post my PR's but there is always a delay in both generating them and for them to be merged.

I added a few changes and extensions to the parser. For now I am using my forked copy until the official version has all the features.

The pegdown source used in this plugin can be found [vsch/pegdown].

---

\* This plugin is based on the [nicoulaj/idea-markdown plugin] by [nicoulaj], which is based on [pegdown] library by [sirthias].

[.gitignore]: http://hsz.mobi
[Android Studio]: http://developer.android.com/sdk/installing/studio.html
[AppCode]: http://www.jetbrains.com/objc
[CLion]: https://www.jetbrains.com/clion
[Craig's List]: http://montreal.en.craigslist.ca/
[DataGrip]: https://www.jetbrains.com/datagrip
[GitHub Issues page]: ../../issues
[GitHub]: https://github.com/vsch/laravel-translation-manager
[idea-multimarkdown]: http://vladsch.com/product/multimarkdown
[IntelliJ IDEA]: http://www.jetbrains.com/idea
[JetBrains plugin comment and rate page]: https://plugins.jetbrains.com/plugin/writeComment?pr=&pluginId=7896
[JetBrains plugin page]: https://plugins.jetbrains.com/plugin?pr=&pluginId=7896
[Markdown]: http://daringfireball.net/projects/markdown
[nicoulaj/idea-markdown plugin]: https://github.com/nicoulaj/idea-markdown
[nicoulaj]: https://github.com/nicoulaj
[pegdown]: http://pegdown.org
[PhpStorm]: http://www.jetbrains.com/phpstorm
[Pipe Table Formatter]: https://github.com/anton-dev-ua/PipeTableFormatter
[PyCharm]: http://www.jetbrains.com/pycharm
[RubyMine]: http://www.jetbrains.com/ruby
[sirthias]: https://github.com/sirthias
[Version Notes]: resources/META-INF/VERSION.md
[vsch/pegdown]: https://github.com/vsch/pegdown/tree/develop
[WebStorm]: http://www.jetbrains.com/webstorm
[Wiki]: ../../wiki
