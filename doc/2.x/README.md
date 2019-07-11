# JavaSpec 2.0

TODO KDK: Links to Mocha and Jasmine.

Behavior-Driven Development testing for Java using lambdas.  Inspired by Mocha and Jasmine.

**This documentation is for JavaSpec 1.0.1.**  If you are looking for a different version, please see
the [JavaSpec documentation site][github-io-javaspec].

[github-io-javaspec]: http://javaspec.info


## Why

*Why create another testing framework for Java, and why use lambdas?*

JavaSpec attempts to be:

* **Concise**: Simple behavior should be simple to test and describe in a small amount of space.
* **Searchable**: Finding call sites in Java code is easy.  Finding where a test calls your code should be just as easy.
* **Transparent**: You shouldn't have to keep any caveats in mind when writing test code.

There are many testing libraries out there with some of these characteristics, but expresiveness does not need to come
at the cost of adding complexity.  For example you can write your tests in Ruby or Groovy (as the author once
considered), but now you're adding more components between your test and production code, adding new dependencies, and
losing out on searchability.

Lambdas are the weapon of choice for turning simple expressions into one-liners.  A test with one assertion can be 1
line instead of several for tagging and creating whole, new test method.


## Installation

TODO KDK: Update dependency info

JavaSpec is located in the Maven Central Repository, under the following coordinates:

```xml
<dependency>
  <groupId>info.javaspec</groupId>
  <artifactId>javaspec-runner</artifactId>
  <version>1.0.1</version>
</dependency>
```

It depends upon JUnit 4 and Java 8+.


# Getting started

There's no magic in how JavaSpec works.  This guide describes JavaSpec in terms of its similarities to popular libraries
instead of pretending like these are radical, never-before-seen ideas.

TODO KDK: Discuss the runner and the script used to call it.

TODO KDK: Show an example of the syntax and provide links to other examples.


## If you have any other questions

Hopefully JavaSpec works like you think it does.

For times when it doesn't, start by looking at the tests on
[`JavaSpecRunner`](https://github.com/kkrull/javaspec/blob/master/src/test/java/info/javaspec/runner/JavaSpecRunnerTest.java)
and related classes.

If that still doesn't do the trick, feel free to [post an issue](https://github.com/kkrull/javaspec/issues) or submit a
pull request with any suggested modifications.
