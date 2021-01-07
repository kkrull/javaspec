# JUnit5 Prototype for JavaSpec

## IntelliJ IDEA

Set the test runner in (Preferences | Build, Execution, Deployment | Build Tools | Gradle) to
"IntelliJ IDEA", instead of the default "Gradle", in order to show `@DisplayName` properly, from
JUnit Jupiter tests.


## Problem Space

### Writing Specs

1. Syntax for defining specs:
  * Description of what is being tested
  * Descriptions of expected behavior(s)
  * Description of any particular circumstances, during which those expectations apply
  * Procedures to verify those expectations

2. Where in a spec class to use this syntax, to declare specs:
  * Jupiter-tagged test factory method
  * Implement a method from a `JavaSpec` base class, that is wired up to a Jupiter-tagged test
    factory method
  * Constructor
  * Instance initializer
  * Static initializer (ew)


### Interface with `jupiter-engine`

1. How to get `junit-jupiter-engine` to ask JavaSpec for `DynamicNodes` (tests and containers)
  * Tag the factory method with `@TestFactory`
  * Tag the test/spec class with an extension: Is there an extension for test factories?
  * Call a Jupiter registration method?  Does it need to be tagged with something else?


2. How to represent specs, as Jupiter tests:
  * `DynamicNode` base class for all test-related constructs
  * `Stream<DynamicNode>` for any number of tests and/or containers
  * `DynamicTest` for test descriptions and verification procedures
  * `DynamicContainer` for what is being tested and contextual circumstances


3. Where to store specs and/or Jupiter test objects (`DynamicNode`):
  * JavaSpec singleton: Might work as long as only 1 spec class is declaring at a time.
  * JavaSpec "class-local": Something like `ThreadLocal`, but tied to the calling test class instead
    of to a thread.


### Running Specs

1. How to run specs, in IntelliJ:
  * Gradle test runner: Does not show `@DisplayName`, for regular Jupiter syntax.
  * IntelliJJ test runner: Works for `@DisplayName` in Jupiter syntax, and working so far for
    `DynamicTest`.

2. How to run specs, on the command line:
  * Use the `junit-jupiter-standalone` jar
  * Make our own CLI, for JavaSpec (hard)

3. How to run specs, in CI:
   
   Is running in CI _exactly_ the same as regular, command-line usage, or are could there be subtle
   differences?

4. How to report results:
  * On the command-line: The Jupiter standalone jar would be best
  * When running on the command-line, during CI
  * When running in Gradle: Uhh...use the built-in test reporter?  Compatible with custom reporters?
    Write our own custom reporter?
  * When running in IntelliJ: IntelliJ Test Runner seems to be handling Jupiter syntax ok, so far.

5. How to trace back to failed assertions and/or failures in production code:
  * Pass a `URI` to the `DynamicNode` factory methods, with a URI to the related spec syntax.
