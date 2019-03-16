# Design Log

## Coming up

Technical debt:

- Add a linter.
- Break up and organize the Ruby feature tests now that there are a few more of them.
- Put the Java artifacts on independent version numbers.  There really isn't any 2.x development on the artifacts that
  have already been released.


Features:

- Formatted test output.
- Documented (tested) error reporting for things like not being able to find/load/instantiate a spec class.
  It may be helpful to return a value type for the exit status, instead of a just the number, so the offending class can
  be reported.
- Report spec failures to the console reporter.
  - This will cause `Spec#run` to pass the error to `SpecReporter#specFailed`.
  - Format the console output.


## Clarifying the terminology

- A **root context class** is the top-level class passed to `JavaSpecRunner`.  It's the outer-most level of detail
  of abstraction at which to run tests, and it may contain test fixtures and examples just like any other context class.
- All other **context classes** are [inner classes](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html) 
  enclosed within the root context class or another context class.  Each context class may contain test fixtures and
  examples, or it may exist solely for the purpose of describing the system under test in a structured and readable
  manner.
- A **nested static class** could also contain `It` fields and the like, but it is not considered a context class at
  this time.  It is, however, a perfectly reasonable place to put helper methods.


## Who names tests?  Where should the boundaries among objects be?

The Spec (or its factory, as it currently stands) names the test itself.  It's the only part of the system that knows
about the assertion being made by the test.

The context names and creates the hierarchy of (sub-)context.  It's the part of the system that understands the
hierarchical structure of context classes.

Prior designs debated between isolating JUnit dependencies in the JUnit test runner or in a gateway class.  The former
required extensive queries on and knowledge of test class structure.  The latter hid some of the implementation details
of the test structure from the test runner, but required the gateway to go a step beyond merely transforming the data
from a data storage representation to an agnostic data model.  Both required more code than necessary, and their tests
suffered from overly tight coupling to implementation details (even though it didn't seem like it, at the time).

The object-based approach encapsulates much of the data, and the tests have been refactored to work more at the
package / feature level, rather than operate on the exact boundaries among all classes in the solution domain.


## What can be tagged with `@RunWith`?

Workflows:

- **Run a whole test file**: The intended target of `@RunWith` is an outer class.  It therefore can't be static.  
  It must also have a public no-arg constructor.
- **Run a tests in a context**: The top-level class still must have `@RunWith`, but the class passed to the runner may
  not be the top-level class.  In this case, it must have a (implicit) constructor that takes its parent class as a
  parameter.

In the second case, should the class be allowed to be static?

On one hand, JavaSpec won't need any extra logic to instantiate such an outer context.  As far as it knows, a static
class constructor works just as well as a no-arg constructor on a top-level class.

On the other hand, what if the static class has tests and/or context behavior inside of it?  If an `Establish` lambda
is in a static class, there can still be multiple instances of the static class - each with its own value/side effect -
as long as the field itself isn't static.  Maybe it's not a big deal after all?
