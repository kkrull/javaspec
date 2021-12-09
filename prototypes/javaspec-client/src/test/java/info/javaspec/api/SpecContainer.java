package info.javaspec.api;

public interface SpecContainer {
  void addSpec(String behavior, Verification verification);

  @FunctionalInterface
  interface Verification {
    void execute() throws Throwable;
  }
}
