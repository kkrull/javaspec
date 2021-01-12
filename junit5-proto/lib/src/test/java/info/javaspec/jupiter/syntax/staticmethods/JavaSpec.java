package info.javaspec.jupiter.syntax.staticmethods;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.List;

final class JavaSpec {
  private static List<DynamicTest> currentTests;

  public static DynamicNode describe(String actor, DescribeBlock block) {
    currentTests = new LinkedList<>();
    block.declare();

    DynamicContainer container = DynamicContainer.dynamicContainer(actor, currentTests);
    currentTests = null;
    return container;
  }

  public static DynamicTest it(String behavior, Executable verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
    if(currentTests != null)
      currentTests.add(test);

    return test;
  }

  @FunctionalInterface
  public interface DescribeBlock {
    void declare();
  }
}
