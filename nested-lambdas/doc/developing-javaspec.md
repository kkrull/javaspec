# Developing JavaSpec

This guide covers common development tasks.  Make sure you have [set up your
development environment](./development-environment.md) first.


## Publish SNAPSHOT jars to Maven Local

Use the [Maven Publish plugin][gradle-publishing-maven] to generate POM files
for project artifacts that are compatible for use with Maven.  Configure this
process in the `publishing` section of `build.gradle`.

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
