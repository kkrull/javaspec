# Javaspec

Spec-style testing for Java, inspired by [RSpec](http://rspec.info) for Ruby and similar in fashion to
[Machine.Specifications](https://github.com/machine/machine.specifications) for C#.

Not to be confused with similarly named frameworks that test JavaScript.  These tests are written in Java, using Java
8's new lambda syntax and run in [JUnit](http://junit.org).

## Getting started

- Make a test class
- Tag it with `@RunWith(JSpecRunner.class)`.
- Do any test setup you want to do in a public, no-arg constructor (similar to JUnit).
- Include 1 or more `It` fields in the class and assign a thunk (a no-arg, anonymous function) like so:

```java
It does_something_cool = () -> assertEquals("hello", widget.sayHello());
```

- Run this test anywhere you run JUnit.
