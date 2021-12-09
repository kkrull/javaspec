package info.javaspec.api;

//Something to add specs to
public interface SpecContainer {
  void addSpec(String behavior, Verification verification);

  @FunctionalInterface
  interface Verification {
    void execute() throws Throwable;
  }
}
