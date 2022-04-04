# Work To Do

## JavaSpec 2.0 (Mocha-like syntax run via console)

### Scope

Mocha-like syntax that:

* can be run in the console during development and on Jenkins for CI
* tells you which specs passed and failed
* tells you about the errors causing specs to fail


### Features

* Documented (tested) error reporting for things like not being able to find/load/instantiate a spec
  class. It may be helpful to return a value type for the exit status, instead of a just the number,
  so the offending class can be reported.
* Release
  * Version artifacts: New artifacts get 2.0 version; old artifacts can remain at their
    previously-released value.
  * Upload artifacts to Maven Central Repository or jcenter.
  * Update README for JavaSpec 2.0.
  * Publish new documentation (the Github page) for the new release.


### Technical debt

* Find a new home for artifacts, such as [Bintray OSS](https://bintray.com/signup/oss).
* Switch to whatever license JUnit is using, to remove a potential blocker of adoption.
* Do whatever is required for the jcommander library's license.  Attribution?  Reference?
* Enable others to contribute:
  * Identify good first commits
  * Contributing guide, with coding principles and instructions for submitting a pull request
