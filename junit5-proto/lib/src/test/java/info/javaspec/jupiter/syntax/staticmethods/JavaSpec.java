package info.javaspec.jupiter.syntax.staticmethods;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.Stack;

final class JavaSpec {
  private static final Stack<DynamicTestList> _containers = new Stack<>();

  static {
    _containers.push(new NoDynamicTests());
  }

  public static DynamicNode describe(String actor, DescribeBlock block) {
    _containers.push(new DynamicTestList());
    block.declare();
    return DynamicContainer.dynamicContainer(actor, _containers.pop());
  }

  public static DynamicTest it(String behavior, Executable verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
    _containers.peek().add(test);
    return test;
  }

  @FunctionalInterface
  public interface DescribeBlock {
    void declare();
  }

  //Null object so there's always something on the stack, even if it's not a test container
  private static final class NoDynamicTests extends DynamicTestList {
    @Override
    public boolean add(DynamicTest test) {
      System.out.printf("[NoDynamicTests#add] No container to add test to: %s%n", test.getDisplayName());
      return false;
    }
  }

  private static class DynamicTestList extends LinkedList<DynamicTest> { }
}
