# JavaSpec Architecture

## Project Goals

* What is JavaSpec (details)?
  * The big idea: Use strings to describe and contextualize behavior, use
    lambdas to organize specs.
  * It's intended to look a lot like Mocha / Jasmine.
  * Behavior-Driven Development testing for Java using lambdas and plain
    language.
  * Inspired by [Mocha](https://mochajs.org) and
    [Jasmine](https://jasmine.github.io).
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
    characteristics, but expressiveness does not need to come at the cost of
    adding complexity.  For example you can write your tests in Ruby or Groovy
    (as the author once considered), but now you're adding more components
    between your test and production code, adding new dependencies, and losing
    out on searchability.
* How does it work?
  * Tries to be transparent.
  * It's just an adapter from one syntax with lambdas to JUnit containers
    (classes) and tests (methods).
