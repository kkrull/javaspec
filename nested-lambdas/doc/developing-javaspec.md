# Developing JavaSpec

This guide covers common development tasks.  Make sure you have [set up your
development environment](./development-environment.md) first.


## Start with `gradle`

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

Since there are multiple projects in this Gradle project, some of them wind up
sharing the same configuration.  Much of this shared configuration exists in the
form of [pre-compiled, custom plugins][gradle-custom-plugins].  Sources for
these are in `buildSrc/`.

[gradle]: https://gradle.org/
[gradle-custom-plugins]: https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins


## Format Java sources with Spotless

Add the `javaspec.java-format-convention` plugin to a project, to add Gradle
tasks for validating and fixing the format of Java sources:

```gradle
plugins {
	id 'javaspec.java-format-convention'
}
```

The [Spotless plugin][github-diffplug-spotless] creates and configures the
Gradle tasks for formatting source code.  The actual formatting is done by the
[Eclipse Formatter][github-diffplug-spotless-eclipse], according to the format
defined in `etc/eclipse-format.xml`.  Rather than share a single format for all
top-level Gradle projects, each one has its own copy of the format definition
file.  That way, changing the format in one project doesn't mean that all the
other top-level projects are instantly outdated.

See `buildSrc/javaspec.java-format-convention.gradle` for details.

[github-diffplug-spotless]: https://github.com/diffplug/spotless
[github-diffplug-spotless-eclipse]: https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-jdt


## Publish SNAPSHOT jars to Maven Local

Add the `javaspec.maven-publish-convention` plugin to a project, to add Gradle
tasks for publishing project artifacts to Maven repositories.

```gradle
plugins {
	id 'javaspec.maven-publish-convention'
}
```

The [Maven Publish plugin][gradle-publishing-maven] provides the Maven tasks to
do things like generate POM files and push to Maven repositories.  Each project
that wishes to publish an artifact does so by creating a single `maven`
publication.  The `maven-publish` plugin therefore has several tasks with the
word `Maven` in it, that are meant for that sole publication.  There are also
aggregator tasks with fixed names, that process all publications.

For example:

```shell
# Generate POM files for all publications [in all sub-projects].
# Creates <project>/build/publications/maven/pom-default.xml.
$ ./gradlew generatePomFile

# Publish everything to ~/.m2/repository (similar to `mvn install`)
$ ./gradlew publishToMavenLocal
```

Dependent projects need to be configured to resolve dependencies locally:

```gradle
//build.gradle
repositories {
  mavenLocal()
  ...
}
```

See `buildSrc/javaspec.maven-publish-convention.gradle` for details.

[gradle-publishing-maven]: https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example
