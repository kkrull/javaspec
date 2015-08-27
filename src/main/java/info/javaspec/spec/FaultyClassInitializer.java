package info.javaspec.spec;

final class FaultyClassInitializer extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public static FaultyClassInitializer forClass(Class<?> context, Throwable cause) {
    return new FaultyClassInitializer(
      String.format("Failed to load class %s due to a faulty static initializer", context.getName()), cause);
  }

  private FaultyClassInitializer(String message, Throwable cause) {
    super(message, cause);
  }
}
