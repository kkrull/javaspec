# JavaSpec for JUnit Jupiter

_Bringing the Java testing community to the 21st century, using technology from
the 1960's._

This is the next version of JavaSpec.  It's the one that's going to change the
world.

* Introduction to JavaSpec
  * What does it do?  It helps you write specs that run on JUnit / Jupiter as
    tests.
  * Intended audience / why use it?  If you like describing behavior with text
    and don't mind lambdas, this is for you.
  * The big idea: Use strings to describe and contextualize behavior, use
    lambdas to organize specs.
  * It's intended to look a lot like Mocha / Jasmine.
* Artifacts
  * API artifact needed to declare specs - testCompile
  * Engine artifact needed at runtime to run them - testRuntimeOnly
  * JVM requirements - Java 11+?
  * License requiremeents - compatible with JUnit and Jupiter?
* Writing specs (must haves)
  * Make any Java class.  It doesn't have to end in Spec or Test, but Spec or
    Specs is recommended.
  * Implement SpecClass and #declareSpecs
  * #it with a description and a lambda, and you're off and running.  This is
    all you really have to have.
  * Use JUnit's build-in assertions, another library like hamcrest, or make your
    own like you would in JUnit.
* Writing specs (nice to haves)
  * Recommend #describe for the class and method you want, to organine specs.
  * #pending for anything you haven't written yet and #skip for anything to
    skip. (as-needed)
  * #given and #describe for context / flavor (optional)
* Running specs
  * ./gradlew test like usual, as long as you have the engine as a runtime
    depdendency.
  * testlogger looks nice -- recommended.
  * JUnit Console works too: add --include-engine and classpaths for API,
    engine, test code, prod code, test dependencies (assertj) and prod
    dependencies.
* Support
  * File an issue in Github.
  * HMU on Twitter?  Discord?
  * Optional listeners for debugging - how to add them as Gradle dependencies.
* Known issues
  * Gradle reports the name of the test class at the bottom, instead of the top.
    Make Github issue for this.
  * Reports as default package and unknown class in JUnit report.  Make Github
    issue for this.
* Developing on JavaSpec
  * [Setting up Your Development Environment](./doc/development-environment.md)
