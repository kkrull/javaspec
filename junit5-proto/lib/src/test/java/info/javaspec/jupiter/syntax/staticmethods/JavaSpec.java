package info.javaspec.jupiter.syntax.staticmethods;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

final class JavaSpec {
  public static DynamicNode it(String behavior, Executable verification) {
    return DynamicTest.dynamicTest(behavior, verification);
  }
}
