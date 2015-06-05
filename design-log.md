# Design Log

## Who names tests?

The context / example names that you see when running tests are from the class and method names used to build
`Description` objects in JUnit.  Providing some sort of human readable transformation from class/method names to
sentences can promote a more BDD style of writing tests.

But who should perform this transformation?

- **The test runner**: It's the only class that deals with Description objects, and it's the only interface to JUnit.  
  It would make sense for this class to be fully aware of the fact that tests are contained in fields inside classes, 
  and of the transformation from those field/class names to test/suite names.  If that's the case, then the 
  `ExampleGateway` should be fully transparent about it providing access to classes and fields.
- **The example gateway** could make an abstraction of class to context and of field to example.  If such an abstraction
  exists, then there's no reason to believe that all implementations would be bound by the identifier naming schemes in
  Java.  If the intention of abstracting contexts and examples is to be agnostic of where they come from, then it sounds
  like the gateway had better handle any name transformations.  However it will also need to provide some abstract means
  of identifying each context and example, and this proved difficult in the last iteration.

Perhaps the question is not so much **who** maps class/field names to behavior names, but **when** in the workflow that
abstraction is made.  `ExampleGateway` is in the middle of it all, and it needs a way to:

- Query the root context
- Query the sub context of a given context.  This means the object used to represent a context has to identify the
  underlying, source class, so that further queries can be made.
- Count the total number of examples descending from the root context.
- Query the examples of a given (sub-)context.

If the gateway is to be an abstract representation of context and example, it will probably need to include type
parameters for the kind of context and type of example.  Otherwise, it will be much harder for the gateway to make
queries on the sub context classes of a given context.

Using the gateway, the test runner needs to:

- Transform classes into suite descriptions
- Transform fields into test descriptions
- Transform fields into tests

Looking back on the old design, it strikes me as inconsistent that the test runner performs the description
transformation, but the gateway provides the transformation to executable tests.  That explains why tree traversal logic
exists in so many places.

If the gateway makes things that aren't simple domain objects, then isn't that performing business logic?

1. Should the gateway be dumb, and just return stuff?
2. Should it be smart, and build stuff?

So let's recap what some of the choices are:

1. Runner runs examples and reports.
   Gateway builds Descriptions and Examples.

   - Gateway is now coupled to JUnit.  Supporting another framework means writing another runner and another gateway.
     On the other hand, how likely is it to need to support another test framework?
   - Class traversal logic isolated to gateway, so this removes duplication of concepts.
   - Runner does not need to make fine-grained queries, which makes it much easier to Gateway to be abstract.
     Gateway may not need an abstract means of identifying sub-context.

2. Runner builds Descriptions, runs, and reports.
   Gateway builds Examples.

   - Runner is the only class coupled to JUnit.  Gateway can be used in other frameworks.
   - Class traversal logic is duplicated - at least conceptually - between Runner and Gateway.
   
   2a. Gateway is not abstract at all.
   
   - Simpler to design, for now.
   - Mocking is more difficult than it might be, with a suitable abstraction.
   - Will be harder to have other syntax and representation for context and example, at a later time.
   
   2b. Gateway abstracts context and example.
   
   - Need abstract means of identification and/or traversal, so that the Runner can build up Descriptions.

3. Runner builds Descriptions and Examples, runs, and reports.
   Gateway provides access to context classes and example fields in non-abstract way.
   
   How would the gateway maintain any notion of abstraction, and still give the runner the concrete data it needs
   to build examples?  You'd have to pass in factory methods that are impedance-matched to the concrete context and
   example representations, which in turn means introducing more interfaces with type parameters.  That's a lot of
   overhead and indirection for just one representation of context and example.

## Kinds of classes to allow

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


## Clarifying the terminology

- A **root context class** is the top-level class passed to `JavaSpecRunner`.  It's the outer-most level of detail
  of abstraction at which to run tests, and it may contain test fixtures and examples just like any other context class.
- All other **context classes** are [inner classes](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html) 
  enclosed within the root context class or another context class.  Each context class may contain test fixtures and
  examples, or it may exist solely for the purpose of describing the system under test in a structured and readable
  manner.
- A **nested static class** could also contain `It` fields and the like, but it is not considered a context class at
  this time.  It is, however, a perfectly reasonable place to put helper methods.


## Questions for later

- `ClassExampleGateway`: Will it cause any problems if there's an empty `Description` sub-tree?
  By that, I mean a (sequence of) suite Description that ultimately contains no test Descriptions.

## Runner to gateway interface

It's time for another one of those big dilemas in design: *what should the gateway interface be?*

Let's start with the facts that are incontrovertible and work our way back to a stable design.

1. Specs, regardless of representation, are composed of:

  - A **root context**, which may contain `0..n` examples and `0..n` sub-contexts, but has no parent context.
  - A **context** which - like the root context - may contain `0..n` examples and `0..n` sub-contexts.  Unlike the root
    context, it has 1 parent context and `1..n` ancestor contexts.
  - Each context may also contain **test fixture** that runs before and/or after each example.
  - An **example**, which performs some or all of the actions of the test.

2. Specs are currently represented in Java code by:

  - A **top level class** for the root context.
  - A hierarchy of **inner classes**, enclosed by the root context, that represent the context hierarchy.
  - Optional **test fixture fields** in each context class.
  - **Example fields** in at least 1 context class.

3. The `Runner` used to interface JavaSpec to JUnit needs to:

  - Provide a JUnit `Description` hierarchy to summarize for observers what tests are to be run.
  - Provide notifications about each test to listeners, with the `Description` associated with the test.
  - Instantiate and run each test, along with whatever before/after test fixture that is necessary.
    The manner of instantiation and execution, as well as the order of test execution, are up to the runner.
  - Generate a `Result` for each test.
  - Associate each `Result` with a `Description`.

### Design alternatives

Given those requirements, let's consider some design alternatives.

1. Smart gateway: Gateway does all the work.

  - `Runner.getDescription` delegates to `Gateway.junitDescriptionTree`.
  - `Runner.run` delegates to
    - `Gateway.getExamples` to construct examples with all necessary context and to provide a
      runtime sequence.  It also needs to return the `Description` associated with each `Example`.
    - `Example.run` does the actual running of the spec.
  - DRY: The `Gateway` handles all notions of tree traversal.
  - Abstraction: A new spec representation requires a new Runner and Gateway.

2. Thin gateway: Runner does all the work.

  - The gateway has query methods necessary to traverse the hierarchy:
    - root context (class)
    - sub-contexts of a context
    - examples in a context: This will either have to be a ready-to-execute example (complete with all test fixture), or
      it will need to be raw access to fields that are used for arrange/act/assert/cleanup.
  - DRY: Both the Runner and the Gateway will need to have tree-traversal logic, to some extent.
    - The Runner will have to start at the top, and work its way down to make `Descriptions`.
    - The gateway will have to be given a context, and work its way back up to make an `Example`.
    - The gateway will also have to go down one level at a time, to query sub-context.
  - Abstraction: 
    - The Gateway could be parameterized for a representation of a context and of an example.  That may focus the design
      on abstraction, but there won't be any alternative implementations at runtime unless resorting to the Service
      Locator pattern (ew).
    - Each Runner will have to work with concrete classes, or delegate to a parameterized Strategy that captures the
      type of context.  If there were a new representation of tests, there would have to be a new runner because the
      runner's constructor has to instantiate a concrete gateway class that knows about the structure of the test
      classes.  So there's really no notion of having a runner for which you can swap out the gateway, except during
      testing when calling a different constructor.

3. Smart solution-domain model: Extend `Description` and have it generate `Example`.

  - Feasibility: Impossible, because `Description` constructors are all private.

4. Smart problem-domain model: Query `Example` and have it build `Description`.

  - Abstraction: Now `Example` needs to know about JUnit.
  - It's also impossible for `Example` to build a complete `Description` below itself, without a really funky algorithm
    that goes up the tree, and then back down.
  - One could construct the concrete `Example` with its `Description`.  I'm pretty sure the whole `Description` tree
    will be stuck in memory anyway, as long as 1 reference exists to the root node.

Given all that, I think it's best to have a thin gateway.  It won't be so likely to change whne it's focusing on the
fundamentals.  If there's a new representation, there will have to be a new runner too.

## Why have a gateway at all?

Having a gateway seemed like a natural separation of concerns that would decrease coupling in a manner helpful for
testing, and *possibly* helpful in production.  

However, does the gateway really make testing any easier?  Let's assume for a moment that I'm using the Strategy pattern
to get around the issue with Java Generics, allowing the Runner to be agnostic of the particular Context used by its
test representation.  

Perhaps it's best that I start by defining the specific manner in which I wish to simplify my tests.  If the Runner and
Gateway are combined and tightly coupled to this representation of specs, then:

- The Runner navigates classes and fields.  It's not so easy to make classes and fields at runtime, in a test, so
  testing would have to be done with real classes.  As I've found in the time since making this distinction, putting all
  those classes inside a top-level class works out pretty well.
- I don't like having to use Test Spies for so many micro tests.  I don't mind using a few to spot-check end-to-end
  behavior, but I'm concerned that overuse of spying would lead to brittle, hard to understand tests.
- I could use the Factory Method pattern on the Runner to at least abstract the notion of creating and running Examples.
- If I want to support another representation for specs later, I'll have to write a new Runner.  It may be helpful to
  extract class traversal logic to helpers, since that behavior would at least be shared by any class-based runners.

If I did have a separate gateway and the Runner isn't tied to using classes:

- I need to stub queries at each node in the tree, for the Runner to create a full Description hierarchy and all the
  Examples.
- Running examples: For the Gateway to create each Example, it needs to get the context class and query fixture methods from each ancestor.

    Runner.run(Context c):
    Context c = Gateway.rootContext
    for Example e in Gateway.examples(c)
      Description test = lookup(c, e) //Cached during getDescription
      e.run
      RunNotifier.fireTestStarted/Ended/Ignored
    
    for Context s in Gateway.subcontexts(s)
      run(s)
    
    Gateway.examples(ClassContext c):
    Field[] befores = readBefores(c) ++ readBefores(c.parent)
    Field[] afters = readAfters(c.parent) ++ readAfters(c)
    for Field f in Class.getDeclaredFields(It.class):
      Example e = new Example(befores, afters, f)
      yield return e

    Runner.getDescription:
    Context c = Gateway.rootContext
    Description suite = new Description(c.name)
    suite += Gateway.examples(c).map(Example::name).map(Description::testDescription)
    suite += Gateway.subcontexts(c).map(getDescription)


## 2015-06-05 Wrapping up well named specs and contexts

- Dead code check: `DfsUtil`
- Format
- Organize imports
- Remove unused `ContextClasses`
- DRYness check
- `Stream` over `List`