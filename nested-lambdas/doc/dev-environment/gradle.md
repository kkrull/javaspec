# Gradle

This project uses [Gradle][gradle-what-is-gradle] in the traditional fashion by
using the included scripts (`gradlew` and `gradlew.bat`).  These scripts
automatically download and use a pinned version of Gradle that is known to be
compatible with the code and the configured JDK.

This project is a [multi-project build][gradle-multi-project], with
configuration in the following files:

* `settings.gradle`: For the most part, this just lists the sub-projects to
  build.
* `build.gradle`: Defines tasks and plugin settings that apply to most (or all)
  sub-projects.
* `<sub-project>/build.gradle`: Defines tasks and plugin settings that apply to
  a sub-project.

There are a couple of things that come in handy, if you are new to Gradle:

```shell
# Verify your Gradle installation
$ ./gradlew -v

# List available tasks
$ ./gradlew tasks --all
```

[gradle-multi-project]: https://docs.gradle.org/current/samples/sample_building_java_applications_multi_project.html
[gradle-what-is-gradle]: https://docs.gradle.org/current/userguide/what_is_gradle.html
