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