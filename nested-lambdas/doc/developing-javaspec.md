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

[gradle]: https://gradle.org/


### Use conventional plugins for shared configuration

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


### Visualize task dependencies

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


## Common Development Tasks

Gradle has tasks to handle many of the things you need to do as a developer.


### Add license and copyright notices with `license-gradle-plugin`

Add the `local.license-convention` plugin to a project, to add Gradle tasks for
checking and updating license and copyright headers in source files:

```groovy
//build.gradle
plugins {
  id 'local.license-convention'
}

licenseConvention.licenseFile = rootProject.file('../LICENSE')
```

The [`license-gradle-plugin`][github-license-gradle-plugin] creates and
configures Gradle tasks for adding headers to source files, to clarify who owns
the copyright for each source file and how it may be used.   This adds a task
that parses source files to make sure they all have the same license header that
is in the given `LICENSE` file.

```shell
$ ./gradlew check #lifecycle task that also runs licenseCheck
$ ./gradlew licenseCheck #Make sure source file headers are the same as the LICENSE file

#Example output when something goes wrong
Missing header in: javaspec-api/src/main/java/info/javaspec/api/BehaviorDeclaration.java
> Task :javaspec-api:licenseMain FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':javaspec-api:licenseMain'.
> License violations were found: javaspec-api/src/main/java/info/javaspec/api/BehaviorDeclaration.java}
```

There is also a Gradle task to add (or correct) the appropriate headers to
source files:

```shell
$ ./gradlew licenseFormat #Re-apply the contents of LICENSE to source file headers
```

See `buildSrc/local.license-convention.gradle` for details.

[github-license-gradle-plugin]: https://github.com/hierynomus/license-gradle-plugin/tree/v0.16.1


### Assemble JARs with the Java plugin

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


### Format Java sources with Spotless

Add the `local.java-format-convention` plugin to a project, to add Gradle tasks
for validating and fixing the format of Java sources:

```groovy
//build.gradle
plugins {
  id 'local.java-format-convention'
}

//File that holds the Eclipse Formatter configuration
javaFormatConvention.eclipseConfigFile = rootProject.file('etc/eclipse-format.xml')
```

The [Spotless plugin][github-diffplug-spotless] creates and configures the
Gradle tasks for formatting source code.  The actual formatting is done by the
[Eclipse Formatter][github-diffplug-spotless-eclipse], according to the format
defined in `etc/eclipse-format.xml`.  Rather than share a single format for all
top-level Gradle projects, each one has its own copy of the format definition
file.  That way, changing the format in one project doesn't mean that all the
other top-level projects are instantly outdated.

This adds the following tasks to a project:

```shell
$ ./gradlew spotlessCheck #Fail if sources are not format-compliant
$ ./gradlew spotlessApply #Re-format sources
```

See `buildSrc/local.java-format-convention.gradle` for details.

[github-diffplug-spotless]: https://github.com/diffplug/spotless
[github-diffplug-spotless-eclipse]: https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-jdt


### Test Java code with the JUnit Platform

Add the `local.java-junit-convention` plugin to a project, to add Gradle tasks
for running automated unit tests.

```groovy
//build.gradle
plugins {
  id 'local.java-junit-convention'
}
```

Once applied to a project:

* `build`, `check`, and `test` each run [JUnit via Gradle][gradle-java-testing].
* Tests report progress to the console with the
  [test-logger][github-gradle-test-logger] plugin.

Note that some projects additionally use JavaSpec (which runs on the JUnit
Platform) for their own tests, by adding the appropriate dependencies in much
the same fashion that a regular user would.

Put it all together, and you get:

```shell
# Run tests with default test-logger theme
$ ./gradlew test

# Customize test-logger
$ ./gradlew -Dtestlogger.theme=plain test
```


[github-gradle-test-logger]: https://github.com/radarsh/gradle-test-logger-plugin
[gradle-java-testing]: https://docs.gradle.org/current/userguide/java_testing.html


## Debugging Tools

There are some tools that can help to understand how JavaSpec is running under
the JUnit Platform, if you get stuck.


### Log a `DiscoveryRequest` with `javaspec-engine-discovery-request-listener`

Add this dependency if you need to have a look at the `DiscoveryRequest` that
`JavaSpecEngine` receives, when it is looking for specs:

```groovy
//build.gradle
dependencies {
  testRuntimeOnly project(':javaspec-engine-discovery-request-listener')
}
```

You also need to add some more configuration so that Gradle and/or JUnit
Platform allow the `TestEngine` to write to the console.

```groovy
//build.gradle
test {
  //Show all output: the specs themselves *and* the TestEngine
  onOutput { _descriptor, TestOutputEvent engineEvent ->
    logger.lifecycle(engineEvent.message.trim())
  }
}
```


### Log test execution events with `jupiter-test-execution-listener`

You can add this dependency at runtime to get some more information about what
is happening when `JavaSpecEngine` is running specs.

```groovy
//build.gradle
dependencies {
  testRuntimeOnly project(':jupiter-test-execution-listener')
}
```


## Deployment Tasks

If you maintain this repository or manage releases, you will need a way to sign
and publish artifacts with Gradle.


### Publish artifacts to Maven Local

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


### Publish artifacts to Sonatype

Publishing to [Sonatype OSSRH][sonatype-nexus] uses the same Gradle plugins and
configuration that is needed to [publish to Maven
Local](#publish-artifacts-to-maven-local).  The conventional plugin configures a
single `sonatype` repository, which leads to creating several Gradle tasks
containing the word `Sonatype`.

Before you run the tasks, check the versions in each `build.gradle` file to make
sure it is a SNAPSHOT or a regular release, as intended.

```shell
$ ./gradlew publishAllPublicationsToSonatypeRepository
```

Running this with a SNAPSHOT version will deploy this to the [Snapshots
repository][sonatype-snapshots].  You can view the deployed artifacts on the Nexus webapp by going to:

> Views/Repositories - Repositories - Snapshots - Browse Storage - info.javaspec

If you get any error messages while deploying, remember that you need to setup a
Sonatype account and tell Gradle how to access it.  See the [environment setup
document](./development-environment.md#publish-artifacts-to-sonatype-ossrh) for
details.

[sonatype-nexus]: https://oss.sonatype.org/
[sonatype-snapshots]: https://oss.sonatype.org/content/repositories/snapshots/


### Sign JARs with `signing` and GPG

The [signing plugin][gradle-signing-plugin] handles the details of calling GPG
to sign artifacts, including JAR files and generated POM files.

After you have installed GPG and generated a key, you can sign assemblies with:

```shell
$ ./gradlew signMavenPublication
```

You may get an error message that looks like this:

```shell
$ ./gradlew signArchives
Execution failed for task ':javaspec-api:signArchives'.
> Cannot perform signing task ':javaspec-api:signArchives' because it has no configured signatory
```

If you get an error message like that, [double-check your GPG keys and Gradle
configuration](./development-environment.md#sign-artifacts-with-gnupg).
Alternatively, you can define [provide this configuration at
runtime][gradle-signing-credentials] as follows:

```shell
$ ./gradlew signMavenPublication -Psigning.key=<ASCII armored private key> ...
```

[gradle-signing-plugin]: https://docs.gradle.org/current/userguide/signing_plugin.html
[gradle-signing-credentials]: https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
