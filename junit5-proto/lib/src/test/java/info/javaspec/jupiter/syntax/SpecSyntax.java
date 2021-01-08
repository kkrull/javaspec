package info.javaspec.jupiter.syntax;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.JavaSpec.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SpecSyntax {
  @TestFactory
  DynamicNode generateTests() {
    return describe("Greeter", () -> {
      it("exists", () -> assertNotNull(new Greeter()));

      it("greets the world", () -> {
        Greeter subject = new Greeter();
        assertEquals("Hello world!", subject.makeGreeting());
      });

      describe("given a name", () -> {
        it("greets that person, by name", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello George!", subject.makeGreeting("George"));
        });
      });
    });
  }
}
