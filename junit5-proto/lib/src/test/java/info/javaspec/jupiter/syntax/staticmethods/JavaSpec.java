package info.javaspec.jupiter.syntax.staticmethods;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.Stack;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class JavaSpec {
  private static final Stack<DynamicNodeList> _containers = new Stack<>();

  static {
    _containers.push(new RootNodeList());
  }

  public static void context(String condition, DescribeBlock block) {
    describe(condition, block);
  }

  public static DynamicNode describe(Class<?> actor, DescribeBlock block) {
    return describe(actor.getSimpleName(), block);
  }

  //Unknown: Could nodes be added to the wrong container, if jupiter-engine runs tests in parallel?
  public static DynamicNode describe(String actor, DescribeBlock block) {
    //Push a fresh node list onto the stack and append declarations to that
    _containers.push(new DynamicNodeList());
    block.declare();

    //Create this child container entirely, then add it to any parent container
    DynamicNodeList childNodes = _containers.pop();
    DynamicContainer thisChildContainer = DynamicContainer.dynamicContainer(actor, childNodes);
    DynamicNodeList parentContainer = _containers.peek();
    parentContainer.add(thisChildContainer);

    //Negative: Exposes DynamicContainer to developers, who might mutate it in a way that's incompatible with JavaSpec.
    return thisChildContainer;
  }

  public static DynamicTest it(String behavior, Executable verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
    _containers.peek().add(test);
    return test;
  }

  public static DynamicNode disable(String intendedBehavior, Executable brokenVerification) {
    DynamicTest test = DynamicTest.dynamicTest(intendedBehavior, () -> {
      //Positive: It adds the spec to the test plan, and it doesn't actually run the execution.
      //Negative: It shows a misleading and distracting stack trace, due to the unmet assumption.
      //Source: https://github.com/junit-team/junit5/issues/1439
      String description = String.format("Disabled: %s.  This is not a failed assumption in the spec; it's just how JavaSpec disables a spec.", intendedBehavior);
      assumeTrue(false, description);
    });
    _containers.peek().add(test);
    return test;
  }

  public static DynamicNode pending(String pendingBehavior) {
    DynamicTest test = DynamicTest.dynamicTest(pendingBehavior, () -> {
      //Positive: It adds the spec to the test plan, and it marks it as skipped.
      //Negative: It shows a misleading and distracting stack trace, due to the unmet assumption.
      //Source: https://github.com/junit-team/junit5/issues/1439
      String description = String.format("Pending: %s.  This is not a failed assumption in the spec; it's just how JavaSpec skips a pending a spec.", pendingBehavior);
      assumeTrue(false, description);
    });
    _containers.peek().add(test);
    return test;
  }

  @FunctionalInterface
  public interface DescribeBlock {
    void declare();
  }

  //Null object so there's always something on the stack, even if it's not a test container
  private static final class RootNodeList extends DynamicNodeList {
    @Override
    public boolean add(DynamicNode node) {
      return false;
    }
  }

  private static class DynamicNodeList extends LinkedList<DynamicNode> { }
}
