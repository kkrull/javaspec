package info.javaspec.jupiter;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreeterTest {
  @TestFactory
  DynamicTest makeSingleTest() {
    return DynamicTest.dynamicTest("greets the world", () -> {
      Greeter subject = new Greeter();
      assertEquals("Hello world!", subject.makeGreeting());
    });
  }
}
