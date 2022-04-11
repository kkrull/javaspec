# Developing JavaSpec

* Developing on JavaSpec
  * [Setting up Your Development Environment](./doc/development-environment.md)
  * Common tasks -- go through gradle tasks like spotless and build/test/etc
  * Contribution guide: Please make an effort to test, but feel free to ask for
    help

## Publish jars to Maven Central

Use the [Maven Publish plugin][gradle-publishing-maven] to generate POM files
for project artifacts that are compatible for use with Maven.  Configure this
process in the `publishing` section of `build.gradle`.

Each project that wishes to publish an artifact does so by creating a single
`maven` publication.  The `maven-publish` plugin therefore has several tasks
with the word `Maven` in it, that are meant for that sole publication.

For example:

```shell
$ ./gradlew generatePomFileForMavenPublication
```

[gradle-publishing-maven]: https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example
