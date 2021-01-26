package info.javaspec.jupiter.syntax.fixture;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Fixture syntax: Methods on JavaSpec instance")
public class FixtureMethodSpecs {
  @TestFactory
  DynamicNode beforeEachSingle() {
    JavaSpec greeterSpecs = new JavaSpec();
    return greeterSpecs.describe(Greeter.class, () -> {
      AtomicReference<Greeter> subject = new AtomicReference<>();

      //Unknown: What happens if #beforeEach is called after #it?
      greeterSpecs.beforeEach(() -> {
        subject.set(new Greeter());
      });

      greeterSpecs.it("greets the world", () -> {
        assertEquals("Hello world!", subject.get().makeGreeting());
      });

      greeterSpecs.it("greets a person by name", () -> {
        assertEquals("Hello George!", subject.get().makeGreeting("George"));
      });
    });
  }

  @TestFactory
  DynamicNode beforeEachMultiple() {
    JavaSpec specs = new JavaSpec();
    return specs.describe(List.class, () -> {
      AtomicReference<List<String>> subject = new AtomicReference<>();

      specs.beforeEach(() -> {
        //TODO KDK: Work here to get this beforeEach called in the it two levels below
        System.out.println("beforeEach describe/List");
        subject.set(new LinkedList<>());
      });

      specs.context("when the list has 1 or more elements", () -> {
        specs.beforeEach(() -> {
          System.out.println("beforeEach context/1+");
          subject.get().add("existing");
        });

        specs.it("appends to the tail", () -> {
          System.out.println("it/appends");
          subject.get().add("appended");
          assertEquals(Arrays.asList("existing", "appended"), subject.get());
        });
      });
    });
  }
}
