# Test Java code with the JUnit Platform

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
