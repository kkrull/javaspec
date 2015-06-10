package info.javaspec.runner;

abstract class Context {
  public final String id;
  public final String displayName;

  Context(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }
}
