package info.javaspec.jupiter.syntax.componentparameters;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.componentparameters.JavaSpec.describe;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassedComponentSyntax {
  @TestFactory
  DynamicNode generateTests() {
    return describe("Greeter", (it) -> {
      //TODO KDK: Is there a way to avoid saying `it.declare` instead of `it`?
      it.declare("greets the world", () -> {
        Greeter subject = new Greeter();
        assertEquals("Hello world!", subject.makeGreeting());
      });
    });
  }
}
