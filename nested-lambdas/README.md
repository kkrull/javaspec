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
    - [Add dependencies](#add-dependencies)
    - [Run specs on the Jupiter platform (Gradle)](#run-specs-on-the-jupiter-platform-gradle)
    - [Run specs with JUnit Console](#run-specs-with-junit-console)
    - [Basic spec syntax](#basic-spec-syntax)
    - [More spec syntax](#more-spec-syntax)
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
### Add dependencies

To start using JavaSpec, add the following dependencies:

1. `testImplementation 'info.javaspec:javaspec-api'`: the syntax you need to
   declare specs.  This needs to be on the classpath you use for compiling test
   sources and on the one you use when running tests.
1. `testRuntimeOnly 'info.javaspec:javaspec-engine'`: the Jupiter `TestEngine`
   that runs specs.  This only needs to be on the classpath you use when running
   tests.
1. some kind of library for assertions.  For example:
   `testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'`

In Gradle, that means adding the following to your `build.gradle` file:

```gradle
//build.gradle
dependencies {
  //Add these dependencies for JavaSpec
  testImplementation 'info.javaspec:javaspec-api:<version>'
  testRuntimeOnly 'info.javaspec:javaspec-engine:<version>'

  //Add an assertion library (JUnit 5's assertions shown here)
  testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'
}
```


### Run specs on the Jupiter platform (Gradle)

Once you have the right dependencies, you need a way to run specs on the JUnit /
Jupiter platform.

If you are accustomed to using JUnit in Gradle projects already, you should
continue to add this to your `build.gradle`:

```gradle
//build.gradle
test {
	useJUnitPlatform()
}
```

Then it's `./gradlew test` to run specs, like usual.

For extra-pretty console output, try adding `id 'com.adarshr.test-logger'` to
your `plugins` section.


### Run specs with JUnit Console

  * JUnit Console works too: add --include-engine and classpaths for API,
    engine, test code, prod code, test dependencies (AssertJ) and prod
    dependencies.


### Basic spec syntax

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


### More spec syntax

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
