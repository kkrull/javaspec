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
      javaspec.subject(() -> {
        LinkedList<String> list = new LinkedList<>();
        list.add("existing");
        return list;
      });

      javaspec.it("appends to the tail", () -> {
        List<String> list = javaspec.subject();
        list.add("appended");
        assertEquals(Arrays.asList("existing", "appended"), list);
      });
      javaspec.it("prepends to the head", () -> {
        List<String> list = javaspec.subject();
        list.add(0, "prepended");
        assertEquals(Arrays.asList("prepended", "existing"), list);
      });
    });
  }
}
