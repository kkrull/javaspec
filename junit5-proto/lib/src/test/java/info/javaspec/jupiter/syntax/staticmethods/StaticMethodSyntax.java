package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.*;
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

  @TestFactory
  DynamicNode describeFunctionWithContext() {
    return describe("makeGreeting", () -> {
      context("given no name", () -> {
        it("greets the world", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello world!", subject.makeGreeting());
        });
      });

      context("given a name", () -> {
        it("greets that person by name", () -> {
          Greeter subject = new Greeter();
          assertEquals("Hello George!", subject.makeGreeting("George"));
        });
      });
    });
  }
}
