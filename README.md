idea-multimarkdown
==================

**[Markdown](http://daringfireball.net/projects/markdown) language support for [IntelliJ IDEA](http://www.jetbrains.com/idea), [RubyMine](http://www.jetbrains.com/ruby), [PhpStorm](http://www.jetbrains.com/phpstorm), [WebStorm](http://www.jetbrains.com/webstorm), [PyCharm](http://www.jetbrains.com/pycharm), [AppCode](http://www.jetbrains.com/objc) and [Android Studio](http://developer.android.com/sdk/installing/studio.html).**

See the [official plugin page](http://plugins.intellij.net/plugin?id=7896).

I wanted a Markdown preview plug-in that resembled GitHub's markdown style. I decided to start with [Markdown plug-in](https://github.com/nicoulaj/idea-markdown), upgrade it to latest pegdown, parboiled
then add a few creature comforts like user editable style sheet, fix a few bugs then add ability to split ide editors and display Markdown in one pane and HTML Preview in the other, that is updated as you type.

Features:

- [x] Striped tables
- [x] GitHub style task list items
- [x] CSS editable in settings

To Do:

- [ ] Add configurable HTML tag replacements. The need for this is caused by the java HTMLEditorKit, which is used to render the HTML,
     ignoring CSS attributes for known tags. The workaround is to replace the generated HTML to something the editor kit will render better.
     For example, `<hr/>` hardcodes color to black and size to minimum 2px. To have more control `<hr>` is replaced with `<div class="hr">&nbsp;</div>`.
- [ ] Change Parser to use Grammar-Kit:
    - [ ] finish the [Markdown for an Interactive Parser](https://github.com/vsch/idea-multimarkdown/blob/master/SPEC.md)
    - [ ] add the IntelliJ expected comforts:
    - [ ] syntax highlighting
    - [ ] formatting
    - [ ] navigation
    - [ ] document structure display

[![idea-multimarkdown](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_preview.png)](http://plugins.intellij.net/plugin?id=7896)

Change options and customize the CSS to your liking.

![idea-multimarkdown](https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_settings.png)

*This plugin is based on the excellent [Markdown plug-in](https://github.com/nicoulaj/idea-markdown) by [nicoulaj](https://github.com/nicoulaj), 
which is based on [PegDown](http://pegdown.org) library by [sirthias](https://github.com/sirthias). 
