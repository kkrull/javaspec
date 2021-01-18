package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;

//Shows how to use static imports for spec syntax.
@DisplayName("Declaration syntax: Static methods")
class StaticMethodSyntax {
  @TestFactory
  DynamicNode itStandsAlone() {
    return it("does a thing, whatever it is", () -> assertEquals(2, 1+1));
  }

  @TestFactory
  DynamicNode describeClassWithObject() {
    return describe(Greeter.class, () -> {
      it("exists", () -> assertNotNull(new Greeter()));
    });
  }

  @TestFactory
  DynamicNode describeClassWithString() {
    //Positive: Declaration functions can be called as plain functions, instead of as methods on objects.
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

  @TestFactory
  DynamicNode disableASpec() {
    return disable("verification that needs to be updated", () -> assertEquals(1, 2));
  }

  @TestFactory
  DynamicNode pendingSpec() {
    return pending("pending spec");
  }
}
