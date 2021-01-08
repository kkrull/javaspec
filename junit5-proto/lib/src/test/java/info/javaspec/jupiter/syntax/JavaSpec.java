package info.javaspec.jupiter.syntax;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;

final class JavaSpec {
  public static DynamicNode describe(String what, Executable declaration) {
    return DynamicContainer.dynamicContainer("pretend this is a bunch of tests", Collections.emptyList());
  }

  public static void it(String behavior, Executable verification) {
  }
}
