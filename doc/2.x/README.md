# JavaSpec 2.0

Behavior-Driven Development testing for Java using lambdas and plain language.

Inspired by [Mocha][mocha] and [Jasmine][jasmine].

**This documentation is for JavaSpec 2.0.**  If you are looking for a different version, please see
the [JavaSpec documentation site][github-io-javaspec].

[github-io-javaspec]: http://javaspec.info
[jasmine]: https://jasmine.github.io
[mocha]: https://mochajs.org


## Installation

TODO KDK: Update this section about how to declare a dependency on JavaSpec.

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

There's no magic in how JavaSpec works.  This guide describes JavaSpec in terms of its similarities
to popular libraries instead of pretending like these are radical, never-before-seen ideas.

TODO KDK: Discuss the runner and the script used to call it.

TODO KDK: Show an example of the syntax and provide links to other examples.


## Why does this library exist?

*Why create another testing library for Java, when JUnit is so popular?*

To put it plainly, the main author got tired of trying to describe and contextualize behavior with
method and class names.  Additional syntax in newer versions of JUnit does add the ability to be
more descriptive, but now you're describing the same behavior in two places (the annotation and the
name of the test method) and it adds overhead to an already verbose test syntax.

> Why not just say what you mean once and be done with it?

JavaSpec 2.0 attempts to be:

* **Descriptive**: You should be able to type out your thoughts and state your intentions without
  making it fit within naming conventions that make more sense to computers than they do to human
  beings.
* **Concise**: Simple behavior should be simple to test and describe in a small amount of space.
* **Searchable**: Finding call sites in Java code is easy.  Finding where a test calls your code
  should be just as easy.
* **Transparent**: You shouldn't have to keep any caveats in mind when writing test code.

There are many testing libraries out there with some of these characteristics, but expresiveness
does not need to come at the cost of adding complexity.  For example you can write your tests in
Ruby or Groovy, but this adds language barriers and data structure transformations between your test
code and your production code.  Other approaches may add a lot more dependencies and make it harder
to find call sites in test code.


## If you have any questions

Hopefully JavaSpec works like you think it does.

For times when it doesn't, feel free to [post an issue][github-javaspec-issues] with any questions
or suggestions.

[github-javaspec-issues]: https://github.com/kkrull/javaspec/issues
