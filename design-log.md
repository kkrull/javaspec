# Design Log

## Who names tests?

The context / example names that you see when running tests are from the class and method names used to build
`Description` objects in JUnit.  Providing some sort of human readable transformation from class/method names to
sentences can promote a more BDD style of writing tests.

But who should perform this transformation?

- *The test runner*: It's the only class that deals with Description objects, and it's the only interface to JUnit.  It
  would make sense for this class to be fully aware of the fact that tests are contained in fields inside classes, and
  of the transformation from those field/class names to test/suite names.  If that's the case, then the `ExampleGateway`
  should be fully transparent about it providing access to classes and fields.
- *The example gateway* could make an abstraction of class to context and of field to example.  If such an abstraction
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
