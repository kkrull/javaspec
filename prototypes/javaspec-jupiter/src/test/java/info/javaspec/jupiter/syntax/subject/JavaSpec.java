package info.javaspec.jupiter.syntax.subject;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Supplier;

final class JavaSpec<S> {
  private final Stack<DynamicNodeList> containers = new Stack<>();
  private Supplier<S> subjectSupplier;

  public JavaSpec() {
    //Push a null object onto the bottom of the stack, so there's always a parent list to add nodes to.
    //Unlike all other entries on the stack, the root node list does not get turned into a DynamicContainer.
    containers.push(new RootNodeList());
  }

  public DynamicNode describe(String functionOrGenericActor, DescribeBlock block) {
    return declareContainer(functionOrGenericActor, block);
  }

  public DynamicTest it(String behavior, Executable verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
    addToCurrentContainer(test);
    return test;
  }

  public DynamicTest it(String behavior, ExecutableWithSubject<S> verification) {
    DynamicTest test = DynamicTest.dynamicTest(behavior, () -> verification.execute(subject()));
    addToCurrentContainer(test);
    return test;
  }

  private void addToCurrentContainer(DynamicNode testOrContainer) {
    containers.peek().add(testOrContainer);
  }

  private DynamicContainer declareContainer(String whatOrWhen, DeclarationBlock block) {
    //Push a fresh node list onto the stack and append declarations to that
    containers.push(new DynamicNodeList());
    block.declare();

    //Create and link this container, now that all specs and/or sub-containers have been declared
    DynamicNodeList childNodes = containers.pop();
    DynamicContainer childContainer = DynamicContainer.dynamicContainer(whatOrWhen, childNodes);
    addToCurrentContainer(childContainer);
    return childContainer;
  }

  public S subject() {
    return this.subjectSupplier.get();
  }

  public void subject(Supplier<S> supplier) {
    this.subjectSupplier = supplier;
  }

  @FunctionalInterface
  public interface DescribeBlock extends DeclarationBlock { }

  @FunctionalInterface
  private interface DeclarationBlock {
    void declare();
  }

  @FunctionalInterface
  public interface ExecutableWithSubject<S> {
    void execute(S subject) throws Throwable;
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
