package info.javaspec.client;

import info.javaspec.api.SpecClass;
import info.javaspec.api.SpecContainer;

public class GreeterSpecs implements SpecClass {
  @Override
  public void declareSpecs(SpecContainer container) {
    //TODO KDK: Work here and do something to declare the spec.  Maybe use an instance variety of the JavaSpec class?
    container.addSpec("greets the world", () -> {});
  }
}
