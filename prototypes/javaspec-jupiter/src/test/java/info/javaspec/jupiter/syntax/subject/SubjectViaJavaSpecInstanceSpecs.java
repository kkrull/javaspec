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
    //Negative: Have to instantiate JavaSpec each time.
    //Positive: This is likely to be a lot more robust, if tests get declared in parallel.
    JavaSpec<List<String>> javaspec = new JavaSpec<>();

    //Negative: Have to call methods all the time (but at least there's only 1 instance, instead of one per block).
    return javaspec.describe("List", () -> {
      //Positive: It works, in supplying a fresh instance to each spec.
      //Positive: It can call any subject constructor, as long as all data is known at the time of declaration.
      javaspec.subject(() -> {
        LinkedList<String> list = new LinkedList<>();
        list.add("existing");
        return list;
      });

      //Positive: You can receive the subject as a parameter, instead of having to explicitly ask for it
      javaspec.it("appends to the tail", (subject) -> {
        subject.add("appended");
        assertEquals(Arrays.asList("existing", "appended"), subject);
      });
      javaspec.it("prepends to the head", (subject) -> {
        subject.add(0, "prepended");
        assertEquals(Arrays.asList("prepended", "existing"), subject);
      });
    });
  }
}
