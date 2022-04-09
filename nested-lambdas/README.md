<!-- omit in toc -->
# JavaSpec for JUnit Jupiter

![JavaSpec build status](https://github.com/kkrull/javaspec/actions/workflows/gradle.yml/badge.svg)

JavaSpec 2 helps you write specifications (unit tests) that run on the JUnit
Platform (aka JUnit 5), using lambdas instead of annotated test methods.  It
does the same thing you can do with JUnit 5, but with a syntax that is more
descriptive–and more concise–than its JUnit counterpart.

JavaSpec 2 should work everywhere JUnit 5 works.  It runs in Gradle, IDEs, and
the JUnit Platform Console just like regular JUnit tests do.  All you have to do
is add one compile dependency for the new spec syntax and one runtime dependency
for the `TestEngine` that turns the new syntax into tests that run on the JUnit
Platform.

_TL;DR - it's kind of like the syntax from
[Jest][jest] / [Mocha][mocha] / [Jasmine][jasmine], but for Java._

- [What is JavaSpec?](#what-is-javaspec)
- [Getting Started](#getting-started)
  - [Add dependencies](#add-dependencies)
  - [Run specs with Gradle](#run-specs-with-gradle)
  - [Run specs in your IDE](#run-specs-in-your-ide)
  - [Run specs with JUnit Platform Console](#run-specs-with-junit-platform-console)
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

JavaSpec helps you take a JUnit test that looks like this...

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

...and declare it with lambdas instead:

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
to add extra decorators or name tests twice (one machine-readable method name
and onc human readable `@DisplayName`).

If you're into testing, like being descriptive, and don't mind lambdas: this
might be the testing library for you.


## Getting Started
### Add dependencies

To start using JavaSpec, add the following dependencies:

1. `testImplementation 'info.javaspec:javaspec-api'`: the syntax you need to
   declare specs.  This needs to be on the classpath you use for compiling test
   sources and on the one you use when running tests.
1. `testRuntimeOnly 'info.javaspec:javaspec-engine'`: the `TestEngine` that runs
   specs.  This only needs to be on the classpath you use at runtime when
   running tests.
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


### Run specs with Gradle

Once you have the right dependencies, you need a way to run specs on the JUnit
Platform.  This section describes how to do that in a Gradle project.

As with regular JUnit tests, you still need to add this to your `build.gradle`:

```gradle
//build.gradle
test {
  useJUnitPlatform()
}
```

Then it's `./gradlew test` to run specs, like usual.

For extra-pretty console output, try adding the [Gradle Test Logger
Plugin][github-gradle-test-logger-plugin] with the `mocha` theme.

[github-gradle-test-logger-plugin]: https://github.com/radarsh/gradle-test-logger-plugin


### Run specs in your IDE

If you have an IDE that can already run JUnit 5 tests, there's a good chance
that it can also run JavaSpec by following these steps:

1. Make sure you have added the dependencies listed in
   [Add Dependencies](#add-dependencies).
2. Add the JUnit Platform Commons dependency:
   `testImplementation 'org.junit.platform:junit-platform-commons:<version>'`
3. Add `@Testable` to each `SpecClass` that contains specifications, as a hint
   to your IDE that this class contains some sort of tests that run on a
   `TestEngine`.

This is usually enough for your IDE to indicate that it can run tests in a
class, once it has had time to download any new dependencies and index your
sources.

For example:

```java
import org.junit.platform.commons.annotation.Testable;

@Testable //Add this IDE hint
public class GreeterSpecs implements SpecClass {
  public void declareSpecs(JavaSpec javaspec) {
    javaspec.describe(Greeter.class, () -> {
      ...
    });
  }
}
```


### Run specs with JUnit Platform Console

Since this is just another `TestEngine` for the JUnit Platform, you can also run
specs on the [JUnit Platform Console][junit-console-launcher] as seen in this
shell snippet:

```shell
junit_console_jar='junit-platform-console-standalone-1.8.1.jar'
java -jar "$junit_console_jar" \
  --classpath=info.javaspec.javaspec-api-0.0.1.jar \
  --classpath=<compiled production code and its dependencies> \
  --classpath=<compiled specs and their dependencies> \
  --classpath=info.javaspec.javaspec-engine-0.0.1.jar \
  --include-engine=javaspec-engine \
  ...
```

Specifically, this means running passing the following arguments to JUnit
Platform Console, on top of whichever options you are already using:

* `--classpath` for `javaspec-api` and `javaspec-engine`
* `--include-engine=javaspec-engine`

[junit-console-launcher]: https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher


### Basic spec syntax

* Writing specs (must haves)
  * Make any Java class.  It doesn't have to end in Spec or Test, but Spec or
    Specs is recommended.
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
