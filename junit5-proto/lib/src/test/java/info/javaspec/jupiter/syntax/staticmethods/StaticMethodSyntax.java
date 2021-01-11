package info.javaspec.jupiter.syntax.staticmethods;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

//Shows how to use static imports for spec syntax.
//TODO KDK: Identify pros and cons, in relation to the other syntax prototypes.
class StaticMethodSyntax {
  @TestFactory
  DynamicNode generateTests() {
    //TODO KDK: Work here to static import JavaSpec methods
    return JavaSpec.it("asserts", () -> assertEquals(2, 1+1));
  }
}
