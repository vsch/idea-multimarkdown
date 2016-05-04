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

This plug-in is using a modified version of [sirthias/pegdown](https://github.com/sirthias). The
jar is included in the lib directory of this project.

If you are not modifying pegdown then you can build idea-multimarkdown with the included pegdown
.jar in the lib directory. Otherwise, you will need to get the pegdown source used in this
plug-in from [vsch/pegdown](https://github.com/vsch/pegdown/tree/develop).

I had had to be jarjar the parboiled libraries and asm4 library otherwise they would conflict in
tests which would prevent running pegdown in test cases. The rules, script and original libraries
are included in the `preplib` directory.
