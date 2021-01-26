package info.javaspec.jupiter.syntax.fixture;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.Stack;

final class JavaSpec {
  private final Stack<DynamicNodeList> containers = new Stack<>();

  public JavaSpec() {
    //Push a null object onto the bottom of the stack, so there's always a parent list to add nodes to.
    //Unlike all other entries on the stack, the root node list does not get turned into a DynamicContainer.
    containers.push(new RootNodeList());
  }

  public void beforeEach(Executable arrange) {
    containers.peek().setBeforeEach(arrange);
  }

  public void context(String condition, ContextBlock block) {
    declareContainer(condition, block);
  }

  public DynamicNode describe(Class<?> actor, DescribeBlock block) {
    return declareContainer(actor.getSimpleName(), block);
  }

  public DynamicNode describe(String functionOrGenericActor, DescribeBlock block) {
    return declareContainer(functionOrGenericActor, block);
  }

  public DynamicTest it(String behavior, Executable verification) {
    return containers.peek().addTest(behavior, verification);
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

  @FunctionalInterface
  public interface ContextBlock extends DeclarationBlock { }

  @FunctionalInterface
  public interface DescribeBlock extends DeclarationBlock { }

  @FunctionalInterface
  private interface DeclarationBlock {
    void declare();
  }

  //Null object so there's always something on the stack, even if it's not a test container
  private static final class RootNodeList extends DynamicNodeList {
    @Override
    public boolean add(DynamicNode node) {
      return false;
    }
  }

  private static class DynamicNodeList extends LinkedList<DynamicNode> {
    //TODO KDK: Manage a queue of fixture lambdas, across all containers in the tree
    private Executable arrange;

    public void setBeforeEach(Executable arrange) {
      this.arrange = arrange;
    }

    public DynamicTest addTest(String behavior, Executable verification) {
      //Future work: Allow multiple beforeEach in a single container?
      DynamicTest test = DynamicTest.dynamicTest(behavior, () -> {
        if(this.arrange != null) {
          this.arrange.execute();
        }

        verification.execute();
      });

      add(test);
      return test;
    }
  }
}
