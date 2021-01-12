package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.describe;
import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.it;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//Shows how to use static imports for spec syntax.
//TODO KDK: Identify pros and cons, in relation to the other syntax prototypes.
class StaticMethodSyntax {
  @TestFactory
  DynamicNode standaloneIt() {
    return it("does a thing, whatever it is", () -> assertEquals(2, 1+1));
  }

  @TestFactory
  DynamicNode describeClass() {
    return describe("Greeter", () -> {
      it("exists", () -> {
        Greeter subject = new Greeter();
        assertNotNull(subject);
      });

      it("greets the world", () -> {
        Greeter subject = new Greeter();
        assertEquals("Hello world!", subject.makeGreeting());
      });
    });
  }

  @TestFactory
  DynamicNode describeMethodInClass() {
    return describe("Greeter", () -> {
      describe("#makeGreeting", () -> {
        it("greets the world", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello world!", subject.makeGreeting());
        });
      });
    });
  }
}
