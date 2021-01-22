# Problem Space

## Writing Specs
### 1 - Syntax for defining specs

* [x] Descriptions of expected behavior(s): `JavaSpec[#::]it`.
* [x] Procedures to verify those expectations `JavaSpec[#::]it`.
* [x] Description of what (class, method, or function) is being tested: `JavaSpec[#::]describe`.
* [x] Description of any particular circumstances, during which those expectations apply: `JavaSpec[#::]context`.
* [x] Disabled spec, that should not be run: `JavaSpec[#::]disabled`.
* [x] Pending spec, that lacks a verification: `JavaSpec[#::]pending`.

`StaticMethodSyntax` shows concise JavaSpec syntax by using static imports for JavaSpec methods.  It's more concise–and
there are fewer opportunities to mix up scope–than with passing scope/context parameters back to the lambdas.

The syntax for `disabled` is nice to write, but running it is quite misleading.  Could JavaSpec create the `DyanmicTest`
as always, but also add a `TestFilter` to ignore it?


### 2 - Syntax to help Arrange

* [x] Common instance/factory for generating the test subject: `JavaSpec<S>#subject`.
* [x] Declaration of reused variables: Class fields, local variables (immutable), local atomic variables (mutable).
* [x] Creating common data or instantiating collaborators: Extract helper functions.
* [ ] Defining a common setup procedure that runs before each spec in a container.
* [ ] Defining a common teardown procedure that runs after each spec in a container.

TODO KDK: Work here on trying out `beforeEach`.

Declaring and instantiating the test subject was a point of friction with declaration-only syntax with static methods.
There isn't a way to add static methods that return type-safe subjects in their own type, so this pushed the design
towards instantiating a `JavaSpec<S>` instance and using that to declare all specs.  This allowed for the addition of
generic methods to declare and create test subjects.

What is the impact of using a `JavaSpec` instance instead of static methods?  While it is a little more verbose to
declare and use the instance, it's not as distracting as I thought it would be.  It still remains posible to make a
static wrapper (that tracks a thread- or class-local `JavaSpec` instance), if a more compact syntax is needed.  Such a
wrapper would sacrifice the type-safe subject methods, however.


### Where in a spec class to use this syntax, to declare specs

* [ ] Jupiter-tagged test factory method
* [ ] Implement a method from a `JavaSpec` base class, that is wired up to a Jupiter-tagged test factory method
* [ ] Constructor
* [ ] Instance initializer
* [ ] Static initializer (ew)


----
## Running Specs
### How to run specs, in IntelliJ

* [ ] Gradle test runner: Does not show `@DisplayName`, for regular Jupiter syntax.
* [ ] IntelliJ test runner: Works for `@DisplayName` in Jupiter syntax, and working so far for `DynamicTest`.


### How to run specs, on the command line

* [ ] Use the `junit-jupiter-standalone` jar
* [ ] Make our own CLI, for JavaSpec (hard)


### How to run specs, in CI

> Is running in CI _exactly_ the same as regular, command-line usage, or are could there be subtle differences?


### How to report results

* [ ] Finding exceptions that happen in the production code and exceptions that happen in the test code, and tracing
  back from the stack trace to both.
* [ ] On the command-line: The Jupiter standalone jar would be best
* [ ] When running on the command-line, during CI
* [ ] When running in Gradle: Uhh...use the built-in test reporter?  Compatible with custom reporters? Write our own
  custom reporter?
* [ ] When running in IntelliJ: IntelliJ Test Runner seems to be handling Jupiter syntax ok, so far.


### How to trace back to failed assertions and/or failures in production code

* [ ] Pass a `URI` to the `DynamicNode` factory methods, with a URI to the related spec syntax.


----
## Interface with `jupiter-engine`
### How to get `junit-jupiter-engine` to ask JavaSpec for `DynamicNodes` (tests and containers)

* [ ] Tag the factory method with `@TestFactory`
* [ ] Tag the test/spec class with an extension: Is there an extension for test factories?
* [ ] Call a Jupiter registration method?  Does it need to be tagged with something else?


### How to represent specs, as Jupiter tests

* [ ] `DynamicNode` base class for all test-related constructs
* [ ] `Stream<DynamicNode>` for any number of tests and/or containers
* [ ] `DynamicTest` for test descriptions and verification procedures
* [ ] `DynamicContainer` for what is being tested and contextual circumstances


### Where to store specs and/or Jupiter test objects (`DynamicNode`)

* [ ] JavaSpec singleton: Might work as long as only 1 spec class is declaring at a time.
* [ ] JavaSpec "class-local": Something like `ThreadLocal`, but tied to the calling test class instead of to a thread.
