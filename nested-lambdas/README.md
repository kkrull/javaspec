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
  - [Write Specs](#write-specs)
  - [Run specs with Gradle](#run-specs-with-gradle)
  - [Run specs in your IDE](#run-specs-in-your-ide)
  - [Run specs with JUnit Platform Console](#run-specs-with-junit-platform-console)
  - [More helpful syntax](#more-helpful-syntax)
- [Support](#support)
  - [Known Issues](#known-issues)


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
and one human readable `@DisplayName`).

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

```groovy
//build.gradle
dependencies {
  //Add these dependencies for JavaSpec
  testImplementation 'info.javaspec:javaspec-api:<version>'
  testRuntimeOnly 'info.javaspec:javaspec-engine:<version>'

  //Add an assertion library (JUnit 5's assertions shown here)
  testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'
}
```


### Write Specs

Start writing specs with JavaSpec the same way you would with JUnit: by making a
new class.  It is often helpful for the name of that class to end in something
like `Specs`, but JavaSpec does not require following any particular convention.

Once you have your new spec class:

1. Implement `SpecClass` (`info.javaspec:javaspec-api`) and
   `#declareSpecs(JavaSpec)`.
2. Inside `declareSpecs`, call `JavaSpec#describe` with:
   1. some description of what you are testing (or the class itself)
   2. a lambda to hold all the specifications for what you're describing
3. Inside the `describe` lambda, call `JavaSpec#it` with:
   1. some description of the expected behavior
   2. a lambda with [Arrange Act Assert][c2wiki-arrange-act-assert] in it, like
      in any other test

Put it all together, and a basic spec class looks something like this:

```java
import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

public class GreeterSpecs implements SpecClass {
  public void declareSpecs(JavaSpec javaspec) {
    javaspec.describe(Greeter.class, () -> {
      javaspec.describe("#greet", () -> {
        javaspec.it("greets the world, given no name", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello world!", subject.greet());
        });
      });
    });
  }
}
```

[c2wiki-arrange-act-assert]: http://wiki.c2.com/?ArrangeActAssert


### Run specs with Gradle

Once you have the right dependencies, you need a way to run specs on the JUnit
Platform.  This section describes how to do that in a Gradle project.

As with regular JUnit tests, you still need to add this to your `build.gradle`:

```groovy
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


### More helpful syntax

The JavaSpec API supports a few more things that developers often need to do
while testing:

* `JavaSpec#pending`: Stub in a pending / todo item reminding you to test
  something later.  JUnit Platform skips the resulting test.
* `JavaSpec#skip`: Skip running a spec that already has a defined procedure.
  This can be useful for allowing a spec to be _temporarily_ disabled while you
  fix something else.

The API also has a variety of ways to help you organize your specs:

* `JavaSpec#describe` is used most often to define the class and methods being
  tested, but it is really just a general-purpose container with no special
  behavior of its own.
* `JavaSpec#context` can be useful for defining any circumstances under which
  some specifications apply.  It's not implemented any differently from
  `#describe`, so use `#context` if you feel like it reads better.
* `JavaSpec#given` is like the other containers, except that it adds the word
  "given" before your description.  For example
  `javaspec.given("a name", () -> ...)`
  results in a container called `given a name`.

Note that these containers exist simply to help you be as descriptive and
organized as you need to be.  Try to use them judiciously to enhance
human readability.


## Support

Feel free to file an [Issue on Github][github-javaspec-issues] if you have any
questions about using JavaSpec, or if something is not working the way you
expected.

[github-javaspec-issues]: https://github.com/kkrull/javaspec/issues


### Known Issues

* There is not an equivalent of `@BeforeEach` and `@AfterEach` yet, for defining
  shared setup and teardown around a series of related specs.
* Running specs in Gradle causes specs to be reported under `default-package`
  and `UnknownClass` instead of their actual package and class names.  This
  applies to HTML test reports in `build/reports/tests/test`.
* Running specs in Gradle with the [Gradle Test Logger
  Plugin][github-gradle-test-logger-plugin] causes the name of the spec class to
  be printed _after_ all the specs in the class have run, instead of _before_
  it.
