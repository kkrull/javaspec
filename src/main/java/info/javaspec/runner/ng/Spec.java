package info.javaspec.runner.ng;

abstract class Spec {
  public final String id;
  public final String displayName;

  public Spec(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public abstract boolean isIgnored();
  public abstract void run();
}
