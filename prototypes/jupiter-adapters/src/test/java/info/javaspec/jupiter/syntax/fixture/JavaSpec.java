package info.javaspec.jupiter.syntax.fixture;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class JavaSpec<S> {
  private final Deque<DynamicNodeList> containers = new ArrayDeque<>();
  private Supplier<S> subjectSupplier;
  private S memoizedSubject;

  public JavaSpec() {
    //Push a null object onto the bottom of the stack, so there's always a parent list to add nodes to.
    //Unlike all other entries on the stack, the root node list does not get turned into a DynamicContainer.
    containers.addLast(new RootNodeList());
  }

  //Positive: Supports any kind of code, that needs to run after each spec.
  public void afterEach(Executable clean) {
    containers.peekLast().setAfterEach(clean);
  }

  //Positive: Supports any kind of code, that needs to run before each spec.
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
    //Positive: The algorithm for collecting fixture lambdas is straightforward enough, using a Deque.
    List<Executable> arrangements = containers.stream()
      .map(x -> x.arrange)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    List<Executable> cleaners = containers.stream()
      .map(x -> x.cleanup)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    Collections.reverse(cleaners);

    return containers.peekLast().addTest(behavior, arrangements, verification, cleaners);
  }

  public DynamicNode pending(String pendingBehavior) {
    DynamicTest test = makeSkippedTest(
      pendingBehavior,
      String.format("Pending: %s.  This is not a failed assumption in the spec; it's just how JavaSpec skips a pending a spec.", pendingBehavior)
    );

    addToCurrentContainer(test);
    return test;
  }

  public S subject() {
    if(this.memoizedSubject == null) {
      this.memoizedSubject = this.subjectSupplier.get();
    }

    return this.memoizedSubject;
  }

  //Future work: Support or reject subject overrides in nested blocks
  public void subject(Supplier<S> supplier) {
    this.subjectSupplier = supplier;
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

  private static DynamicTest makeSkippedTest(String intendedBehavior, String explanation) {
    return DynamicTest.dynamicTest(intendedBehavior, () -> {
      //Negative: It shows a misleading and distracting stack trace, due to the unmet assumption.
      //Source: https://github.com/junit-team/junit5/issues/1439
      assumeTrue(false, explanation);
    });
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
    private Executable cleanup;

    public void setAfterEach(Executable cleanup) {
      this.cleanup = cleanup;
    }

    public void setBeforeEach(Executable arrange) {
      this.arrange = arrange;
    }

    public DynamicTest addTest(String behavior, List<Executable> arrangements, Executable verification, List<Executable> cleaners) {
      DynamicTest test = DynamicTest.dynamicTest(behavior, () -> {
        for(Executable arrange : arrangements) {
          arrange.execute();
        }

        //Unknown: Should a failed cleanup cause the spec to fail, even if all the assertions passed?
        // I tend to think so, but it would be nice if the impact were clarified.
        // Future work: What if AssertionErrors thrown from afterEach lambdas caused the spec to fail
        // (sometimes they have odd assertions in them, too), but other failures caused a softer kind of failure?
        verification.execute();
        for(Executable cleanup : cleaners) {
          cleanup.execute();
        }
      });

      add(test);
      return test;
    }
  }
}
