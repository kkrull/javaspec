# Work To Do

## JavaSpec 2.0 (Mocha-like syntax run via console)

### Scope

Mocha-like syntax that:

* can be run in the console during development and on Jenkins for CI
* tells you which specs passed and failed
* tells you about the errors causing specs to fail


### Features

* Easy way to run JavaSpec's console-runner
  * Gradle's `application` plugin creates a
    [distribution](https://docs.gradle.org/current/userguide/application_plugin.html) that
    contains start scripts, which set up the classpath and call the JVM.
  * Fat jar of JavaSpec and its dependencies.
  * Bash script to set the classpath and run the jar.
  * Acceptance test, to know that it is working.
* Documented (tested) error reporting for things like not being able to find/load/instantiate a spec
  class. It may be helpful to return a value type for the exit status, instead of a just the number,
  so the offending class can be reported.
* Release
  * Version artifacts: New artifacts get 2.0 version; old artifacts can remain at their
    previously-released value.
  * Upload artifacts to Maven Central Repository.
  * Write new README for JavaSpec 2.0.  Make the main README be for the latest version and point to
    the older documents.
  * Publish new documentation (the Github page) for the new release.


### Technical debt

* Consider switching to [Gradle](https://docs.gradle.org/5.0/userguide/java_library_plugin.html).
* Release automation: `rake` or otherwise.
* Find a new home for artifacts, such as [Bintray OSS](https://bintray.com/signup/oss).
* Put the Java artifacts on independent version numbers.  There really isn't any 2.x development on
  the artifacts that have already been released.
* Switch to whatever license JUnit is using, to remove a potential blocker of adoption.
* Enable others to contribute:
  * Identify good first commits
  * Contributing guide, with coding principles and instructions for submitting a pull request
* `yard` [dependency alert](https://github.com/kkrull/javaspec/network/alert/Gemfile.lock/yard/open)

