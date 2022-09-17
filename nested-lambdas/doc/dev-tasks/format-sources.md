# Format Java sources with Spotless

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
