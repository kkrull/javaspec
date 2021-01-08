package info.javaspec.jupiter;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Explores how to use a @TestFactory method to declare a dynamic test
public class TestFactoryMethodTest {
  {
    JavaSpec.it("greets the world", () -> {
      Greeter subject = new Greeter();
      assertEquals("Hello world!", subject.makeGreeting());
    });
  }

  @TestFactory
  DynamicTest makeSingleTest() {
    return JavaSpec.getSpec().toDynamicTest();
  }

  private static class JavaSpec {
    private static JupiterSpec _spec;

    public static JupiterSpec getSpec() {
      return _spec;
    }

    public static void it(String behavior, Executable verification) {
      _spec = new JupiterSpec(behavior, verification);
    }
  }

  private static class JupiterSpec {
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
