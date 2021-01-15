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
    declareContainer(condition, block);
  }

  public static DynamicNode describe(Class<?> actor, DescribeBlock block) {
    //Negative: Could the exposed DynamicContainer be mutated in a way JavaSpec does not support?
    return declareContainer(actor.getSimpleName(), block);
  }

  //Unknown: Could nodes be added to the wrong container, if jupiter-engine runs tests in parallel?
  public static DynamicNode describe(String functionOrGenericActor, DescribeBlock block) {
    //Negative: Could the exposed DynamicContainer be mutated in a way JavaSpec does not support?
    return declareContainer(functionOrGenericActor, block);
  }

  public static DynamicNode disable(String intendedBehavior, Executable brokenVerification) {
    DynamicTest test = DynamicTest.dynamicTest(intendedBehavior, () -> {
      //Positive: It adds the spec to the test plan, and it doesn't actually run the execution.
      //Negative: It shows a misleading and distracting stack trace, due to the unmet assumption.
      //Source: https://github.com/junit-team/junit5/issues/1439
      String description = String.format("Disabled: %s.  This is not a failed assumption in the spec; it's just how JavaSpec disables a spec.", intendedBehavior);
      assumeTrue(false, description);
    });

    addToCurrentContainer(test);
    return test;
  }

  public static DynamicTest it(String behavior, Executable verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
    addToCurrentContainer(test);
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

    addToCurrentContainer(test);
    return test;
  }

  private static void addToCurrentContainer(DynamicNode testOrContainer) {
    _containers.peek().add(testOrContainer);
  }

  private static DynamicContainer declareContainer(String whatOrWhen, DescribeBlock block) {
    //Push a fresh node list onto the stack and append declarations to that
    _containers.push(new DynamicNodeList());
    block.declare();

    //Create and link this container, now that all specs and/or sub-containers have been declared
    DynamicNodeList childNodes = _containers.pop();
    DynamicContainer childContainer = DynamicContainer.dynamicContainer(whatOrWhen, childNodes);
    addToCurrentContainer(childContainer);
    return childContainer;
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
