# Work To Do

## JavaSpec 2.0 (Mocha-like syntax run via console)

### Scope

* Mocha-like syntax
  * that can be run in the console during development and on Jenkins for CI
  * that tells you which specs passed and failed
  * that tells you about the errors causing a spec to fail


### Features

* Easy way to run JavaSpec's console-runner
  * Fat jar of JavaSpec and its dependencies.
  * Bash script to set the classpath and run the jar.
  * Acceptance test, to know that it is working.
* Documented (tested) error reporting for things like not being able to find/load/instantiate a spec class.
  It may be helpful to return a value type for the exit status, instead of a just the number, so the offending class can
  be reported.
* Release
  * Version artifacts: New artifacts get 2.0 version; old artifacts can remain at their previously-released value.
  * Upload artifacts to Maven Central Repository.
  * Write new README for JavaSpec 2.0.  Make the main README be for the latest version and point to the older documents.
  * Publish new documentation (the Github page) for the new release.


### Technical debt

* Separate the documentation for 1.x and 2.x.
* Put the Java artifacts on independent version numbers.  There really isn't any 2.x development on the artifacts that
  have already been released.
* Switch to whatever license JUnit is using, to remove a potential blocker of adoption.
* Release automation via `rake`.
* Enable others to contribute:
  * Identify good first commits
  * Contributing guide, with coding principles and instructions for submitting a pull request
* `yard` [dependency alert](https://github.com/kkrull/javaspec/network/alert/Gemfile.lock/yard/open)
