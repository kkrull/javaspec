package info.javaspec.jupiter.syntax;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.JavaSpec.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//Shows how to pass a declaration context to lambdas, to get nested describe+it syntax
class PassedDeclarationSyntax {
  @TestFactory
  DynamicNode generateTests() {
    //TODO KDK: Can describe and it be passed as parameters, instead of the declaration object?
    //Can users just say it("", ..." instead of declare.it("", ...)?
    return describe("Greeter", (declare) -> {
      declare.it("exists", () -> assertNotNull(new Greeter()));

      declare.it("greets the world", () -> {
        Greeter subject = new Greeter();
        assertEquals("Hello world!", subject.makeGreeting());
      });

      declare.describe("given a name", (givenAName) -> {
        givenAName.it("greets that person, by name", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello George!", subject.makeGreeting("George"));
        });
      });
    });
  }
}
