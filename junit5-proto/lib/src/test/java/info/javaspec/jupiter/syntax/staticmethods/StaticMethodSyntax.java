package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.describe;
import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.it;
import static org.junit.jupiter.api.Assertions.assertEquals;

//Shows how to use static imports for spec syntax.
//TODO KDK: Identify pros and cons, in relation to the other syntax prototypes.
class StaticMethodSyntax {
  @TestFactory
  DynamicNode generateOneDescribe() {
    return describe("Greeter", () -> {
      it("greets the world", () -> {
        Greeter subject = new Greeter();
        assertEquals("Hello world!", subject.makeGreeting());
      });
    });
  }

  @TestFactory
  DynamicNode generateOneDescribeMultipleIt() {
    //TODO KDK: Generate nested tests to drive a stack structure.
    return DynamicTest.dynamicTest("placeholder", () -> assertEquals(2, 1+1));
  }
}
