package info.javaspec.jupiter.syntax.fixture;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Fixture syntax: Methods on JavaSpec instance")
public class FixtureMethodSpecs {
  @TestFactory
  DynamicNode makeSpecs() {
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
}
