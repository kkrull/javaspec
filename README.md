# JavaSpec

Spec-style testing for Java using lambdas.  Inspired by [RSpec](http://rspec.info) and
[Machine.Specifications](https://github.com/machine/machine.specifications).

*Why create another testing framework for Java, and why use lambdas?*

JavaSpec attempts to be:

- **Concise**: Simple behavior should be simple to test and describe in a small amount of space.
- **Searchable**: Finding call sites in Java code is easy.  Finding where a test calls your code should be just as easy.
- **Transparent**: You shouldn't have to keep any caveats in mind when writing test code.

There are many testing libraries out there with some of these characteristics, but expresiveness does not need to come
at the cost of adding complexity.  For example you can write your tests in Ruby or Groovy (as the author once
considered), but now you're adding more components between your test and production code, adding new dependencies, and
losing out on searchability.

Lambdas are the weapon of choice for turning simple expressions into one-liners.  A test with one assertion can be 1
line instead of several for tagging and creating whole, new test method.

## Installation

JavaSpec is located in the Maven Central Repository, under the following coordinates:

```
<dependency>
  <groupId>info.javaspec</groupId>
  <artifactId>javaspec</artifactId>
</dependency>
```

It depends upon JUnit and Java 8+.


# Getting started

There's no magic in how JavaSpec works.  This guide describes JavaSpec in terms of its similarities to popular libraries
instead of pretending like these are radical, never-before-seen ideas.

## It runs on JUnit

In JUnit, you create a test class and put `@Test` methods in it.  JavaSpec is similar:

- Make a test class.
- Tag it with `@RunWith(JavaSpecRunner.class)`.
- Include 1 or more `It` fields in the class and assign a no-arg lambda to it.  Put whatever code you would normally run
  in the `@Test` method in this lambda.
- Run your tests anywhere you run JUnit.  Maven (surefire plugin) and Eclipse Luna work.

A simple "Hello World" test looks like this:

```java
@RunWith(JavaSpecRunner.class)
class GreeterTest {
  It says_hello = () -> assertEquals("Hello World!", new Greeter().sayHello());
}
```

As with JUnit, you get 1 instance of your test class per test.  Each `It` is its own test.

## It's like Machine.Specifications

Machine.Specifications and JavaSpec represent the different steps of a test the same way:

- An `Establish` lambda runs the Arrange part of your test.  This runs first, when present.
- A `Because` lambda runs the Act part of your test.  This runs next, when present.
- An `It` lambda does the Assert part of your test.
- A `Cleanup` lambda - when present - always runs, even if a prior step failed.
- If any step throws an exception, the test fails.

You can think of `Establish` and `Because` as what a `@Before` method would do in JUnit.  These lambdas run before each
`It` lambda in the same class (and also before `It` fields in inner classes).  `Cleanup` is like `@After` in JUnit,
running after each `It` in the same test class.

Unlike MSpec, your lambdas execute in an *instance* of the class in which they are declared.  Non-static helper methods
in your test class are fair game to be called from any step.

A JavaSpec test fixture looks like this:

```java
@RunWith(JavaSpecRunner.class)
class GreeterWithFixtureTest {
  private final PrintStreamSpy printStreamSpy = new PrintStreamSpy();
  private Widget subject;
  private String returned;

  Establish that = () -> subject = new Widget(printStreamSpy);
  Because of = () -> returned = subject.foo();
  Cleanup closeStreams = () -> {
    if(subject != null)
      subject.close();
  };

  It returns_bar = () -> assertEquals("bar", returned);
  It prints_baz = () -> assertEquals("baz", printStreamSpy.getWhatWasPrinted());
}
```

## It's like RSpec

RSpec lets you organize hierarchies of tests and fixtures with `describe` and `context`, and each level in the tree can
have its own `before` and `after` methods to work the test fixture.  JavaSpec provides nested contexts by nesting
*context classes* (inner, **non-static** classes) in the top-level test class.

Each class can have as many `It` lambdas as you want, plus up to 1 of each type of fixture lambda (`Establish`,
`Because`, and `Cleanup`) to build up the test fixture.  As with RSpec, setup runs outside-in and cleanup runs
inside-out.  If you happen to have an `Because` in an outer class and an `Establish` in an inner class and wonder which
one runs first, the outer class lambdas run first (i.e. `Because` runs first).

An example of using nested contexts:

```java
@RunWith(JavaSpecRunner.class)
class WidgetTest {
  private Widget subject;
  Establish that = () -> subject = new Widget();

  class foo {
    private String returned;
    Because of = () -> returned = subject.sayHello();
    It says_hello = () -> assertEquals("Hello World!", returned);
  }
  
  class bar {
    class givenAMultipleOf3 {
      It prints_fizz = () -> assertEquals("fizz", subject.bar(3));
    }

    class givenAMultipleOf5 {
      It prints_buzz = () -> assertEquals("buzz", subject.bar(5));
    }
  }
}
```

In short:

- Only tag the outer-most class with `@RunWith(JavaSpecRunner.class)`.  Don't tag any inner classes with this.
- Make as many contexts as you like by making nested, non-static classes.
- Add up to 1 each of `Establish`, `Because` and `Cleanup` to each context class.
- Make as many tests as you want in each context class with 0 or more `It` lambdas.

## If you have any other questions

Hopefully JavaSpec works like you think it does  For times when it doesn't, start by looking at the tests on
[`JavaSpecRunner`](https://github.com/kkrull/javaspec/blob/master/src/test/java/info/javaspec/runner/JavaSpecRunnerTest.java)
and related classes.

# Future work

- Ability to `@Ignore` a class or an `It`.
- JavaSpec can use strings to allow a more natural language for describing behavior.
- Something similar to RSpec's `shared_examples` and `shared_context` could be useful.
- Feel free to [add an issue in GitHub](https://github.com/kkrull/javaspec/issues) if if JavaSpec's current behavior is
  not clear or could be improved.

# Release history

- 0.4.1: Fixed [Issue 1](https://github.com/kkrull/javaspec/issues/1), dealing with being able to instantiate non-public
  context classes.
- 0.4.0: Initial release
