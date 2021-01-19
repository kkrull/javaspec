package info.javaspec.jupiter.syntax.subject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Subject syntax: Type-safe generic methods")
class SubjectViaJavaSpecInstanceSpecs {
  @TestFactory
  DynamicNode makeTests() {
    JavaSpec<List<String>> javaspec = new JavaSpec<>();
    return javaspec.describe("List", () -> {
      javaspec.subject(LinkedList::new);

      javaspec.it("appends to the tail", () -> {
        List<String> list = javaspec.subject();
        list.add("append-a");
        list.add("append-b");
        assertEquals(Arrays.asList("append-a", "append-b"), list);
      });
      javaspec.it("prepends to the head", () -> {
        List<String> list = javaspec.subject();
        list.add(0, "prepend-a");
        list.add(0, "prepend-b");
        assertEquals(Arrays.asList("prepend-b", "prepend-a"), list);
      });
    });
  }
}
