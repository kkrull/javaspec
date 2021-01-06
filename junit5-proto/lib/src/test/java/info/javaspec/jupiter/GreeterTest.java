package info.javaspec.jupiter;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreeterTest {
  @TestFactory
  DynamicTest makeSingleTest() {
    JupiterSpec spec = new JupiterSpec("greets the world", () -> {
      Greeter subject = new Greeter();
      assertEquals("Hello world!", subject.makeGreeting());
    });

    return spec.toDynamicTest();
  }

  static class JupiterSpec {
    private final String behavior;
    private final Executable verification;

    public JupiterSpec(String behavior, Executable verification) {
      this.behavior = behavior;
      this.verification = verification;
    }

    public DynamicTest toDynamicTest() {
      return DynamicTest.dynamicTest(behavior, verification);
    }
  }
}
