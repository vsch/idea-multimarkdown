Building from sources
=====================

First of all, clone the project:

```bash
git clone git://github.com/vsch/idea-multimarkdown.git
```

Setting up the project in Intellij IDEA
---------------------------------------

### IntelliJ IDEA plugin Gradle Build

* Open the project (<kbd>File > Open project</kbd>).

* :information_source: Markdown Navigator conflicts with the bundled
  Markdown plugin. The following only needs to be performed once per
  sandbox creation:

  * To disable the bundled plugin run the `prepareSandbox` task from the
    IDE Gradle tool window or `./gradlew prepareSandbox` from the
    command line to create the sandbox used for the plugin.
  * create a file
    [disabled_plugins.txt](build/idea-sandbox/config/disabled_plugins.txt)
    with `org.intellij.plugins.markdown` as the only line.

### Building

* Run gradle `buildPlugin` task from the IDE Gradle tool window or
  `./gradlew buildPlugin` from the command line.

  `build/distributions` will contain `idea-multimarkdown.zip` plugin
  installation file.


### Testing

* Run gradle `test` task from the IDE Gradle tool window or `./gradlew
  test` from the command line.

  :information_source: Some tests are not working with gradle build due
  to:

       java.lang.IllegalArgumentException: Missing extension point: com.vladsch.idea.multimarkdown.settingsExtension in container Application

  The cause is most likely the same for

       java.lang.NoClassDefFoundError: Could not initialize class com.vladsch.md.nav.settings.MdRenderingProfile

  I am still trying to figure out the cause of this error and how to fix
  it. The tests when run as part of Plugin DevKit worked. So the issue
  is in the configuration of the test sandbox not the tests.


Notes
-----

The gradle build is a work in progress. The project was switched from
Plugin DevKit to gradle. Not all gradle quirks have been resolved.

1. JavaFX for Java 11 complains that
   `com.sun.javafx.application.PlatformImpl` is not exported from
   `javafx.graphics` module. The project is configured for Java 8 source
   and target level. Trying to figure out how to get the right option
   and where to set it in the `build.gradle` script hit a dead end.

   One thing I tried was adding compiler option `--add-exports
   javafx.graphics/com.sun.javafx.application=ALL-UNNAMED` with no
   success.

   I resorted to adding a copy of `jfxrt.jar` to `lib` directory and
   adding it as compile only dependency to allow `JavaFxHtmlPanel` to
   compile.

   If you know how to fix this properly, please let me know.

