package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

//Specs that exercise JavaSpec.
//Run in IntelliJ or with ./gradlew :javaspec-client:test.
@Testable
public class GreeterSpecs implements SpecClass {
  private static int _numTimesRun = 0;

  //Called via reflection
  public static void assertRanOnce() {
    assertEquals(1, _numTimesRun, "Expected GreeterSpecs to have been run once");
  }

  public static void incrementRunCount() {
    _numTimesRun++;
  }

  public void declareSpecs(JavaSpec javaspec) {
    javaspec.it("greets the world", () -> {
      incrementRunCount();
      Greeter subject = new Greeter();
      assertEquals("Hello world!", subject.greet());
    });
  }

  private static void assertEquals(int expected, int actual, String message) {
    if(actual == expected)
      return;

    throw new AssertionError(String.format(
      "%s: Expected <%s>, but was <%s>",
      message,
      expected,
      actual
    ));
  }

  private static void assertEquals(String expected, String actual) {
    if(expected.equals(actual))
      return;

    throw new AssertionError(String.format("Expected <%s>, but was <%s>", expected, actual));
  }
}
