# Using `gradle`

This is a [Gradle][gradle] multi-module project.  Project configuration is in
`settings.gradle` and `build.gradle` at the parent level, along with a
`build.gradle` for each sub-project.  These files configure project metadata,
plugins, and tasks.  These tasks automate as many development tasks as possible,
so that developers don't need to install and maintain quite so many tools for
just one project.

You do not need to install Gradle separately to work on this project.  Instead,
use the appropriate wrapper script for your platform– either `gradlew` or
`gradlew.bat`–to use Gradle.  This helps to ensure that everybody is using the
same version of Gradle, which is known to be compatible with this project.

Gradle provides a few tasks to help you get started:

```shell
$ ./gradlew projects #List projects for API, engine, etc...
$ ./gradlew tasks [--all] #List available tasks for the project/modules
```

[gradle]: https://gradle.org/

## Use conventional plugins for shared configuration

Since there are multiple projects in this Gradle project, some of them wind up
sharing the same configuration.  Much of this shared configuration exists in the
form of [pre-compiled, custom plugins][gradle-custom-plugins].

Much like with the top-level projects themselves, each plugin tries to be as
independent (and make as few assumptions) as possible.  For example the
`local.maven-publish-convention` allows you to publish artifacts, without
specifically mandating they be derived from Java sources.

_TL;DR - conventional plugins still require some configuration, in the name of
limiting dependencies among them._

Also note that Gradle supports [deferred
configuration][gradle-deferred-configuration], meaning it allows a build file or
plugin to have forward references to configuration has not been processed yet.
This is especially true of plugins that need to be configured via an extension,
and rely upon `afterEvaluate` to defer fetching build configuration until it is
available.

See sources in `buildSrc/` for details.

[gradle-custom-plugins]: https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins
[gradle-deferred-configuration]: https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:deferred_configuration

## Visualize task dependencies

If you're getting lost in which tasks trigger each other, the
[gradle-task-tree plugin][github-gradle-task-tree] can help.  Start by
temporarily adding this plugin:

```groovy
//build.gradle
plugins {
  id 'com.dorongold.task-tree' version '2.1.0'
}
```

Then run the plugin task, as in the following example:

```shell
$ ./gradlew <task> taskTree
$ ./gradlew build taskTree
:build
+--- :assemble
|    \--- :jar
|         \--- :classes
|              +--- :compileJava
|              \--- :processResources
\--- :check
     \--- :test
          +--- :classes
          |    +--- :compileJava
          |    \--- :processResources
          \--- :testClasses
               +--- :compileTestJava
               |    \--- :classes
               |         +--- :compileJava
               |         \--- :processResources
               \--- :processTestResources
```

[github-gradle-task-tree]: https://github.com/dorongold/gradle-task-tree
