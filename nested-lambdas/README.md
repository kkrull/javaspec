# JavaSpec for JUnit Jupiter

_Brings Java tests into the 21st century, using technology from the 1960's._
_Knocks the fat out of JUnit tests._

This is the next version of JavaSpec, which uses nested lambda functions to
organize specs.  It uses a different syntax than the 1.x release.

* Top line
  * Build status?
  * Test coverage?
  * This documentation is for JavaSpec 2.0.  If you are looking for a different
    version, please see the [JavaSpec documentation site](http://javaspec.info).
  * _Brief_ summary of the goals: descriptive and concise being the most
    important.  There are more goals, but those are less important.
* Table of Contents
  * [What is JavaSpec?](#what-is-javaspec)
  * [Getting Started](#getting-started)
  * [Installation and toolchain](./doc/installation.md)
  * [Support and known issues](#support)
  * [Goals and development philosophy](./doc/goals.md)
  * [Developing JavaSpec](./doc/developing-javaspec.md)


## What is JavaSpec?

* What is JavaSpec (brief)?
  * What does it do?  It helps you write specs that run on JUnit / Jupiter as
    tests.
  * Intended audience / why use it?  If you like describing behavior with text
    and don't mind lambdas, this is for you.


## Getting Started

* Installation briefing
  * _Brief_ example of which dependencies to add.  Link to installation guide
    for details.
* Writing specs (must haves)
  * Make any Java class.  It doesn't have to end in Spec or Test, but Spec or
    Specs is recommended.
  * Optional: Add `@Testable` to get your IDE to pick up on it.  What artifact
    does this come from?  It should be `testImplementation`, right?
  * Implement SpecClass and #declareSpecs
  * #it with a description and a lambda, and you're off and running.  This is
    all you really have to have.
  * Use JUnit's build-in assertions, another library like Hamcrest, or make your
    own like you would in JUnit.
* Running specs
  * ./gradlew test like usual, as long as you have the engine as a runtime
    dependency.
  * testlogger looks nice -- recommended.
  * JUnit Console works too: add --include-engine and classpaths for API,
    engine, test code, prod code, test dependencies (AssertJ) and prod
    dependencies.
* Writing specs (nice to haves)
  * Recommend #describe for the class and method you want, to organize specs.
  * #pending for anything you haven't written yet and #skip for anything to
    skip. (as-needed)
  * #given and #describe for context / flavor (optional)


## Support

* Support
  * File an issue in Github.
  * HMU on Twitter?  Discord?
  * Optional listeners for debugging - see the readme for that project.
* Known issues and limitations
  * No beforeEach or afterEach yet.
  * Gradle reports the name of the test class at the bottom, instead of the top.
    Make Github issue for this.
  * Reports as default package and unknown class in JUnit report.  Make Github
    issue for this.
