package info.javaspec.jupiter.syntax.subject;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Subject syntax: Type-safe syntax method overrides")
class SubjectOverrideSpecs {
  @TestFactory
  DynamicNode makeTests() {
    //TODO KDK: This looks basically like the composable helper, so far.
    //Does it start to pull its weight if it takes on it and describe?  How much friction does that add to the declaration syntax?
    JavaSpec<List<String>> javaspec = new JavaSpec<>();
    javaspec.subject(() -> new LinkedList<>());

    return DynamicContainer.dynamicContainer("List", Arrays.asList(
      DynamicTest.dynamicTest("appends to the tail", () -> {
        List<String> list = javaspec.subject();
        list.add("append-a");
        list.add("append-b");
        assertEquals(Arrays.asList("append-a", "append-b"), list);
      }),
      DynamicTest.dynamicTest("prepends to the head", () -> {
        List<String> list = javaspec.subject();
        list.add(0, "prepend-a");
        list.add(0, "prepend-b");
        assertEquals(Arrays.asList("prepend-b", "prepend-a"), list);
      })
    ));
  }

  private static class JavaSpec<S> {
    private Supplier<S> supplier;

    public S subject() {
      return this.supplier.get();
    }

    public void subject(Supplier<S> supplier) {
      this.supplier = supplier;
    }
  }
}
