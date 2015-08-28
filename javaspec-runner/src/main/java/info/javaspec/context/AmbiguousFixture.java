package info.javaspec.context;

public final class AmbiguousFixture extends RuntimeException {
  public static AmbiguousFixture forFieldOfType(Class<?> fieldClass, Class<?> contextClass) {
    String message = String.format("Only 1 field of type %s is allowed in context class %s",
      fieldClass.getSimpleName(), contextClass);
    return new AmbiguousFixture(message);
  }

  private AmbiguousFixture(String message) {
    super(message);
  }
}
