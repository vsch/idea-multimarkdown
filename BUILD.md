Building from sources
=====================

First of all, clone the project:

    git clone git://github.com/nicoulaj/idea-markdown.git


Setting up the project in Intellij IDEA
---------------------------------------

### IntelliJ IDEA plugin SDK setup

* Open the project (*File > Open project*).
* Open the module settings (*Ctrl + Alt + Shift + S*).
* Setup the project SDK with an *Intellij IDEA Plugin SDK*.
* Edit your *Intellij IDEA Plugin SDK* and add `$IDEA_HOME/lib/idea.jar` to its classpath.

### Building

* Use the run configurations to run the plugin and JUnit tests.
* Use *Build > Prepare plugin for deployment* to generate the release package.
