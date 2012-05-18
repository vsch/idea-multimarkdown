Building from sources
=====================

First of all, clone the project:

    git clone git://github.com/nicoulaj/idea-markdown.git


Setting up the project in Intellij IDEA
---------------------------------------

* Open the project (*File > Open project*).
* Open the module settings (*Ctrl + Alt + Shift + S*).
* Setup the project SDK with an *Intellij IDEA Plugin SDK*.
* Edit your *Intellij IDEA Plugin SDK* and add `$IDEA_HOME/lib/idea.jar` to its classpath.

Now you can:

* Use the run configurations to run the plugin and JUnit tests.
* Use *Build > Prepare plugin for deployment* to generate the release package.


Building with Ant
-----------------

* Go the project root:

        cd idea-markdown

* Define `$IDEA_HOME` if you don't want the build to automatically download IntelliJ IDEA Community Edition:

        export IDEA_HOME=/path/to/IDEA

* Compile, run tests and generate release package:

        ant
