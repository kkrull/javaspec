package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

@DisplayName("Subject syntax: Composable helper")
class SubjectComposableHelperSpecs {
  //Positive: Composition restores the ability to use a base class for specs (why?).
  private Subject<List<String>> subject;

  @BeforeEach
  void setup() {
    //Negative: Referencing non-final constructor parameters requires AtomicReference?
    this.subject = new Subject<List<String>>(() -> new LinkedList<>());
  }

  @TestFactory
  DynamicNode makeTests() {
    return DynamicContainer.dynamicContainer("List", Arrays.asList(
      DynamicTest.dynamicTest("appends to the tail", () -> {
        //Negative: Name collision between helper with generator function and the subject itself.
        List<String> list = subject.make();
        list.add("append-a");
        list.add("append-b");
        assertEquals(Arrays.asList("append-a", "append-b"), list);
      }),
      DynamicTest.dynamicTest("prepends to the head", () -> {
        List<String> list = subject.make();
        list.add(0, "prepend-a");
        list.add(0, "prepend-b");
        assertEquals(Arrays.asList("prepend-b", "prepend-a"), list);
      })
    ));
  }

  //Negative: Wraps Supplier<T>, but doesn't add a whole lot of meaning.
  private static final class Subject<T> {
    private final Supplier<T> generator;

    public Subject(Supplier<T> generator) {
      this.generator = generator;
    }

    public T make() {
      return this.generator.get();
    }
  }
}
