# Developing JavaSpec

This guide covers common development tasks.  Make sure you have [set up your
development environment](./development-environment.md) first.

This project tries to standardize most development tasks with Gradle.  It
further compartmentalizes shared Gradle configuration in [pre-compiled, custom
plugins][gradle-custom-plugins].  `buildSrc/` contains sources for these.

[gradle-custom-plugins]: https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins


## Format Java sources with Spotless

Use the [Spotless plugin][github-diffplug-spotless] to format Java sources,
using the Eclipse formatter.  Each project uses its own format in
`etc/eclipse-format.xml`, so that you don't have to change every single Java
project's format at the same time.

See the Gradle convention plugin in
`buildSrc/javaspec.java-format-convention.gradle` for details.


[github-diffplug-spotless]: https://github.com/diffplug/spotless


## Publish SNAPSHOT jars to Maven Local

Use the [Maven Publish plugin][gradle-publishing-maven] to generate POM files
for project artifacts that are compatible for use with Maven.  Configure this
process in the `publishing` section of `build.gradle` and in the conventional
plugin in `buildSrc/javaspec.maven-publish-convention.gradle`.

Each project that wishes to publish an artifact does so by creating a single
`maven` publication.  The `maven-publish` plugin therefore has several tasks
with the word `Maven` in it, that are meant for that sole publication.  There
are also aggregator tasks with fixed names, that process all publications.

For example:

```shell
# If you just want to see the generated POM file in build/publications
$ ./gradlew generatePomFileForMavenPublication

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

[gradle-publishing-maven]: https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example
