# Add license and copyright notices with `license-gradle-plugin`

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
