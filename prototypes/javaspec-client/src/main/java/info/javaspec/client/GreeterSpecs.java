package info.javaspec.client;

import info.javaspec.api.SpecClass;
import info.javaspec.api.SpecContainer;

//TODO KDK: [1] Create javaspec-launcher with a ::main and a TestLauncher
//TODO KDK: [2] Stop hard-coding the engine to run these specs
//TODO KDK: [3] Stop hard-coding the javaspec-launcher to use javaspec-engine
public class GreeterSpecs implements SpecClass {
  @Override
  public void declareSpecs(SpecContainer container) {
    container.addSpec("greets the world", () -> {
      Greeter subject = new Greeter();
      assertEquals("Hello world!", subject.greet());
    });
  }

  private static void assertEquals(String expected, String actual) {
    if(expected.equals(actual))
      return;

    throw new AssertionError(String.format("Expected <%s>, but was <%s>", expected, actual));
  }
}
