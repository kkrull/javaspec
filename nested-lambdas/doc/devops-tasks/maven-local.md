# Publish artifacts to Maven Local

Add the `local.maven-publish-convention` plugin to a project, to add Gradle
tasks for publishing project artifacts to Maven repositories.

```groovy
//build.gradle
plugins {
  id 'local.maven-publish-convention'
}

mavenPublishConvention {
  publicationDescription = project.description
  publicationFrom = components.java
  publicationName = '<human readable name for your artifact>'
}
```

The [Maven Publish plugin [for Gradle]][gradle-publishing-maven] provides the
Gradle tasks to do things like generate POM files and push to Maven
repositories.  Each project that wishes to publish an artifact does so by
creating a single `maven` publication.  The `maven-publish` plugin therefore has
several tasks with the word `Maven` in it, that are meant for that sole
publication.  There are also aggregator tasks with fixed names, that process all
publications.

For example:

```shell
# Generate POM files for all publications [in all sub-projects].
# Creates <project>/build/publications/maven/pom-default.xml.
$ ./gradlew generatePomFile

# Publish all artifacts to ~/.m2/repository (similar to `mvn install`)
$ ./gradlew publishToMavenLocal
```

Dependent projects need to be configured to resolve dependencies locally:

```groovy
//build.gradle
repositories {
  mavenLocal()
  ...
}
```

See `buildSrc/local.maven-publish-convention.gradle` for details.

[gradle-publishing-maven]: https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example
