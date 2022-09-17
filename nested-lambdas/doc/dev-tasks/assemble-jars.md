# Assemble JARs with the Java plugin

Add the `local.jar-convention` plugin to a project, to configure Gradle tasks
for building JAR artifacts that you need when deploying to the Maven Central
Repository.

```groovy
//build.gradle
plugins {
  id 'local.jar-convention'
}

jarConvention.licenseFile = rootProject.file('../LICENSE')
```

This includes the customary JAR with compiled code, along with `-javadoc` and
`-sources` JARs that contain Javadoc and raw sources, respectively.  Each
published artifact also includes the license and copyright notices that are in
this repository.

The [`jar task`][gradle-bundling] builds JAR files like normal, adding the
additional files that are required for the convention.  Use the task like you
always would:

```shell
$ ./gradlew assemble #Build artifacts for each project
$ jar tf build/libs/<.jar file> #Should include a META-INF/LICENSE file
```

See `buildSrc/local.jar-convention.gradle` for details.

[gradle-bundling]: https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html
