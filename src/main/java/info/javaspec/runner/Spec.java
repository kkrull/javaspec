package info.javaspec.runner;

abstract class Spec {
  private final String id;
  private final String displayName;

  public Spec(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public String getId() { return id; }
  public String getDisplayName() { return displayName; }
  public abstract boolean isIgnored();
  public abstract void run() throws Exception;
}
