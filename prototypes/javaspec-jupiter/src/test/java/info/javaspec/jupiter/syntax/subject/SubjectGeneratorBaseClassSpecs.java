package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

//Negative: Specs have to extend that one base class, to work with subjects.
@DisplayName("Subject syntax: Generate subjects in a base class")
class SubjectGeneratorBaseClassSpecs extends SubjectGeneratorBaseClass<List<String>> {
  @BeforeEach
  void setup() {
    //Positive: Type-safe way to generate stateful subjects, per spec.
    subjectGenerator(() -> new LinkedList<String>());
  }

  @TestFactory
  DynamicNode listSpecs() {
    return DynamicContainer.dynamicContainer("List", Arrays.asList(
      DynamicTest.dynamicTest("appends to the tail", () -> {
        List<String> subject = makeSubject();
        subject.add("append-a");
        subject.add("append-b");
        assertEquals(Arrays.asList("append-a", "append-b"), subject);
      }),
      DynamicTest.dynamicTest("prepends to the head", () -> {
        List<String> subject = makeSubject();
        subject.add(0, "prepend-a");
        subject.add(0, "prepend-b");
        assertEquals(Arrays.asList("prepend-b", "prepend-a"), subject);
      })
    ));
  }
}
