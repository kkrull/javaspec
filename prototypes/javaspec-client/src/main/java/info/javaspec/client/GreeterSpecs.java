package info.javaspec.client;

import info.javaspec.api.SpecClass;
import info.javaspec.api.SpecContainer;

//TODO KDK: Stop hard-coding the engine to run these specs
//TODO KDK: Stop hard-coding the javaspec-launcher to use javaspec-engine
public class GreeterSpecs implements SpecClass {
  private static int _numTimesRun = 0;

  public static void assertRanOnce() {
    assertEquals(1, _numTimesRun, "Expected GreeterSpecs to have been run once");
  }

  public static void incrementRunCount() {
    _numTimesRun++;
  }

  @Override
  public void declareSpecs(SpecContainer container) {
    container.addSpec("greets the world", () -> {
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
