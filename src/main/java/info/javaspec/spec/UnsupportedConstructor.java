package info.javaspec.spec;

final class UnsupportedConstructor extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public static UnsupportedConstructor forClass(Class<?> context, Throwable cause) {
    return new UnsupportedConstructor(
      String.format("Unable to find a no-argument constructor for class %s", context.getName()),
      cause);
  }

  private UnsupportedConstructor(String message, Throwable cause) {
    super(message, cause);
  }
}
