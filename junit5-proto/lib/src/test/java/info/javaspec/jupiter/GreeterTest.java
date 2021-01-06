package info.javaspec.jupiter;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreeterTest {
  {
    JavaSpec.it("greets the world", () -> {
      Greeter subject = new Greeter();
      assertEquals("Hello world!", subject.makeGreeting());
    });
  }

  //TODO KDK: Look for a way to turn this into a programmatic extension that invokes whatever method / instance initializer that calls JavaSpec::it
  @TestFactory
  DynamicTest makeSingleTest() {
    System.out.println("[DynamicTest#makeSingleTest]");
    return JavaSpec.getSpec().toDynamicTest();
  }

  static class JavaSpec {
    private static JupiterSpec _spec;

    public static JupiterSpec getSpec() {
      return _spec;
    }

    public static void it(String behavior, Executable verification) {
      _spec = new JupiterSpec(behavior, verification);
    }
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
