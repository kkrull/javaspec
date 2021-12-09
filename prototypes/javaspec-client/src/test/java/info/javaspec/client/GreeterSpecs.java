package info.javaspec.client;

import info.javaspec.api.SpecClass;
import info.javaspec.api.SpecContainer;

//TODO KDK: [2] Create a separate javaspec-launcher module that is hard-coded to run these specs, then start extracting an engine and making it dynamic.
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
