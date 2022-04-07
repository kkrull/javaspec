# JavaSpec for JUnit Jupiter

![JavaSpec build status](https://github.com/kkrull/javaspec/actions/workflows/gradle.yml/badge.svg)

JavaSpec 2 is a plugin for the JUnit 5 (Jupiter) platform that lets you write
specifications (unit tests) with lambda functions instead of annotated test
methods.  It does the same thing you can do with regular JUnit, but with a
syntax that is more descriptive–and more concise–than its JUnit counterpart.

JavaSpec 2 should work everywhere JUnit 5 works.  It runs in Gradle, IDEs, and
the JUnit Console just like regular JUnit tests do.  All you have to do is add a
couple of small dependencies: one at compile time for the new test syntax and
one at runtime for the `TestEngine` that turns the new syntax into Jupiter
tests.

_TL;DR - it's kind of like the syntax from
[Jest][jest] / [Mocha][mocha] / [Jasmine][jasmine], but for Java._

For more details:

- [JavaSpec for JUnit Jupiter](#javaspec-for-junit-jupiter)
  - [What is JavaSpec?](#what-is-javaspec)
  - [Getting Started](#getting-started)
  - [Support](#support)

- [Installation](./doc/installation.md)
- [Goals and development philosophy](./doc/goals.md)

**Note that this documentation is for the new version of JavaSpec**.  It uses a
different syntax than [JavaSpec 1.x][javaspec-1x].

[jasmine]: https://jasmine.github.io/
[javaspec-1x]: http://javaspec.info
[jest]: https://jestjs.io/
[mocha]: https://mochajs.org/


## What is JavaSpec?

JavaSpec helps you take a JUnit test that looks like this:

```java
class GreeterTest {
	@Nested
	@DisplayName("#greet")
	class greet {
		@Test
		@DisplayName("greets the world, given no name")
		void givenNoNameGreetsTheWorld() {
			Greeter subject = new Greeter();
			assertEquals("Hello world!", subject.greet());
		}

		@Test
		@DisplayName("greets a person by name, given a name")
		void givenANameGreetsThePersonByName() {
			Greeter subject = new Greeter();
			assertEquals("Hello Adventurer!", subject.greet("Adventurer"));
		}
	}
}
```

and turn declare it with lambdas instead, like this:

```java
@Testable
public class GreeterSpecs implements SpecClass {
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Greeter.class, () -> {
			javaspec.describe("#greet", () -> {
				javaspec.it("greets the world, given no name", () -> {
					Greeter subject = new Greeter();
					assertEquals("Hello world!", subject.greet());
				});

				javaspec.it("greets a person by name, given a name", () -> {
					Greeter subject = new Greeter();
					assertEquals("Hello Adventurer!", subject.greet("Adventurer"));
				});
			});
		});
	}
}
```

This results in test output that looks like this:

```
Greeter

  #greet

    ✔ greets the world, given no name
    ✔ greets a person by name, given a name
```

Using this syntax, you can describe behavior with plain language without having
to add extra decorators or name tests twice.  So if you're into testing, like
being descriptive, and don't mind lambdas: this might be the testing library for
you.


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
