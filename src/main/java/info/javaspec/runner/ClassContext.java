package info.javaspec.runner;

class ClassContext extends Context {
  public final Class<?> source;

  public ClassContext(String id, String displayName, Class<?> source) {
    super(id, displayName);
    this.source = source;
  }
}
