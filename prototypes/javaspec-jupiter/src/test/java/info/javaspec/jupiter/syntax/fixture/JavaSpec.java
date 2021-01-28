package info.javaspec.jupiter.syntax.fixture;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class JavaSpec {
  private final Deque<DynamicNodeList> containers = new ArrayDeque<>();

  public JavaSpec() {
    //Push a null object onto the bottom of the stack, so there's always a parent list to add nodes to.
    //Unlike all other entries on the stack, the root node list does not get turned into a DynamicContainer.
    containers.addLast(new RootNodeList());
  }

  public void beforeEach(Executable arrange) {
    containers.peekLast().setBeforeEach(arrange);
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
    List<Executable> arrangements = containers.stream()
      .map(x -> x.arrange)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    return containers.peekLast().addTest(behavior, arrangements, verification);
  }

  private void addToCurrentContainer(DynamicNode testOrContainer) {
    containers.peekLast().add(testOrContainer);
  }

  private DynamicContainer declareContainer(String whatOrWhen, DeclarationBlock block) {
    //Push a fresh node list onto the stack and append declarations to that
    containers.addLast(new DynamicNodeList());
    block.declare();

    //Create and link this container, now that all specs and/or sub-containers have been declared
    DynamicNodeList childNodes = containers.removeLast();
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
    private Executable arrange;

    public void setBeforeEach(Executable arrange) {
      this.arrange = arrange;
    }

    public DynamicTest addTest(String behavior, List<Executable> arrangements, Executable verification) {
      DynamicTest test = DynamicTest.dynamicTest(behavior, () -> {
        for(Executable arrange : arrangements) {
          arrange.execute();
        }

        verification.execute();
      });

      add(test);
      return test;
    }
  }
}
