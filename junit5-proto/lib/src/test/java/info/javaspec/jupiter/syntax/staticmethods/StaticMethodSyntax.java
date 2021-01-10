package info.javaspec.jupiter.syntax.staticmethods;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

class StaticMethodSyntax {
  @TestFactory
  DynamicNode generateTests() {
    //TODO KDK: Work here to static import JavaSpec methods
    return JavaSpec.it("asserts", () -> { assertEquals(2, 1+1); });
  }

  private static final class JavaSpec {
    public static DynamicNode it(String behavior, Executable verification) {
      return DynamicTest.dynamicTest(behavior, verification);
    }
  }
}
