package info.javaspec.runner.ng;

abstract class Context {
  public final String id;
  public final String displayName;

  public Context(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }
}
