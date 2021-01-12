package info.javaspec.jupiter.syntax.staticmethods;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;

final class JavaSpec {
  private static DynamicTestList _currentTests = new NoDynamicTests();

  public static DynamicNode describe(String actor, DescribeBlock block) {
    _currentTests = new DynamicTestList();
    block.declare();

    DynamicContainer container = DynamicContainer.dynamicContainer(actor, _currentTests);
    _currentTests = new NoDynamicTests();
    return container;
  }

  public static DynamicTest it(String behavior, Executable verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
    _currentTests.add(test);
    return test;
  }

  @FunctionalInterface
  public interface DescribeBlock {
    void declare();
  }

  private static final class NoDynamicTests extends DynamicTestList {
    @Override
    public boolean add(DynamicTest test) {
      System.out.printf("[NoDynamicTests#add] No container to add test to: %s%n", test.getDisplayName());
      return false;
    }
  }

  private static class DynamicTestList extends LinkedList<DynamicTest> { }
}
