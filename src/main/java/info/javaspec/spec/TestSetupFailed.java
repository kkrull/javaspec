package info.javaspec.spec;

final class TestSetupFailed extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public static TestSetupFailed forClass(Class<?> context, Throwable cause) {
    return new TestSetupFailed(
      String.format("Failed to create test context %s", context.getName()),
      cause);
  }

  private TestSetupFailed(String message, Throwable cause) {
    super(message, cause);
  }
}
