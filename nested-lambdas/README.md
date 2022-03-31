# JavaSpec for JUnit Jupiter

_Bringing the Java testing community to the 21st century, using technology from
the 1960's._

_Knocks the fat out of JUnit tests._

This is the next version of JavaSpec.  It's the one that's going to change the
world.

* Top line
  * Build status?
  * Test coverage?
  * This documentation is for JavaSpec 2.0.  If you are looking for a different
    version, please see the [JavaSpec documentation site](http://javaspec.info).
* Introduction to JavaSpec
  * What does it do?  It helps you write specs that run on JUnit / Jupiter as
    tests.
  * Intended audience / why use it?  If you like describing behavior with text
    and don't mind lambdas, this is for you.
  * The big idea: Use strings to describe and contextualize behavior, use
    lambdas to organize specs.
  * It's intended to look a lot like Mocha / Jasmine.
  * Behavior-Driven Development testing for Java using lambdas and plain
    language.
  * Inspired by [Mocha](https://mochajs.org) and
    [Jasmine](https://jasmine.github.io).
* What you need to use this (Artifacts, toolchain)
  * As with JUnit, add test section with useJUnitPlatform as usual.
  * As with JUnit, add dependencies on `org.junit.jupiter:junit-jupiter-api` and
    `org.junit.jupiter:junit-jupiter-engine` like you always would.
  * API artifact needed to declare specs - testCompile
  * Engine artifact needed at runtime to run them - testRuntimeOnly
  * Watch out for 1.x artifacts - that is for a whole different syntax, from an
    older version.
  * JVM requirements - Java 11+?
  * License requiremeents - compatible with JUnit and Jupiter?
  * What version of JUnit?  JUnit 5.
* Writing specs (must haves)
  * Make any Java class.  It doesn't have to end in Spec or Test, but Spec or
    Specs is recommended.
  * Optional: Add `@Testable` to get your IDE to pick up on it.  What artifact
    does this come from?  It should be `testImplmenntation`, right?
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
* How does it work?
  * Tries to be transparent.
  * It's just an adapter from one syntax with lambdas to JUnit containers
    (classes) and tests (methods).
* Why?  What are the goals?
  * To put it plainly, the main author got tired of trying to describe and
    contextualize behavior with method and class names. You can do it all in
    JUnit5, but it's verbose.
  * **Descriptive**: You should be able to type out your thoughts and state your
    intentions without making it fit within naming conventions that make more
    sense to computers than they do to human beings.
  * **Concise**: Simple behavior should be simple to test and describe in a
    small amount of space.
  * **Searchable**: Finding call sites in Java code is easy.  Finding where a
    test calls your code should be just as easy.
  * **Transparent**: You shouldn't have to keep any caveats in mind when writing
    test code.
  * There are many testing libraries out there with some of these
    characteristics, but expresiveness does not need to come at the cost of
    adding complexity.  For example you can write your tests in Ruby or Groovy
    (as the author once considered), but now you're adding more components
    between your test and production code, adding new dependencies, and losing
    out on searchability.
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
* Developing on JavaSpec
  * [Setting up Your Development Environment](./doc/development-environment.md)
