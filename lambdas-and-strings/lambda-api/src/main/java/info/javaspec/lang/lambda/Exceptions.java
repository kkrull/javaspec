package info.javaspec.lang.lambda;

final class Exceptions {
  public static final class DeclarationNotStarted extends IllegalStateException {
    DeclarationNotStarted() {
      super("No declaration has been started.  Has FunctionalDsl::openScope been called?");
    }
  }

  public static final class NoSubjectDefined extends IllegalStateException {
    static NoSubjectDefined forSpec(String intendedBehavior) {
      String message = String.format("No subject defined for spec: %s", intendedBehavior);
      return new NoSubjectDefined(message);
    }

    private NoSubjectDefined(String message) {
      super(message);
    }
  }

  public static final class SpecDeclarationFailed extends RuntimeException {
    public static SpecDeclarationFailed whenLoading(String className, Throwable cause) {
      return new SpecDeclarationFailed(
        String.format("Failed to load spec class: %s", className),
        cause
      );
    }

    static SpecDeclarationFailed whenInstantiating(Class<?> specClass, Exception cause) {
      return new SpecDeclarationFailed(
        String.format("Failed to instantiate class %s, to declare specs", specClass.getName()),
        cause
      );
    }

    private SpecDeclarationFailed(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
