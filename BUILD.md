Building from sources
=====================

First of all, clone the project:

    git clone git://github.com/vsch/idea-multimarkdown.git


Setting up the project in Intellij IDEA
---------------------------------------

### IntelliJ IDEA plugin SDK setup

* Open the project (<kbd>File > Open project</kbd>).
* Open the module settings (<kbd>Ctrl + Alt + Shift + S</kbd> or <kbd>⌘,</kbd>).
* Setup the project SDK with an *Intellij IDEA Plugin SDK*.
* Edit your *Intellij IDEA Plugin SDK* and add `$IDEA_HOME/lib/idea.jar` to its classpath.

### Building

* Use the run configurations to run the plugin and JUnit tests.
* Use *Build > Prepare plugin for deployment* to generate the release package.

Note
----

This plug-in is using a modified version of [sirthias/pegdown](https://github.com/sirthias), I post my PR's but there is a delay in both generating them and for them to be merged.
I added a few changes and extensions to the parser. For now I am using my forked copy until the official version has these features.

If you are not modifying pegdown then you can build idea-multimarkdown with the included pegdown .jar in the lib directory. Otherwise, you will need to get the pegdown source used in this plug-in from [vsch/pegdown](https://github.com/vsch/pegdown/tree/develop).

                    
