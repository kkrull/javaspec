package info.javaspec.jupiter.syntax.declarationparameter;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.declarationparameter.JavaSpec.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//Shows how to pass a declaration context to lambdas, to get nested declaration.
class PassedDeclarationSyntax {
  @TestFactory
  DynamicNode generateTests() {
    return describe("Greeter", (declare) -> {
      //Negative: Can't say `it` or `describe` by themselves.  Always calling methods on an object.
      declare.it("exists", () -> assertNotNull(new Greeter()));

      declare.it("greets the world", () -> {
        Greeter subject = new Greeter();
        assertEquals("Hello world!", subject.makeGreeting());
      });

      //Negative: Nested context objects need unique names, leading to repetition.
      declare.describe("given a name", (givenAName) -> {
        //Negative: A nested spec could misleadingly declare a spec in a parent context.
        givenAName.it("greets that person, by name", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello George!", subject.makeGreeting("George"));
        });
      });
    });
  }
}
