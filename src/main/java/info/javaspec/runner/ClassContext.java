package info.javaspec.runner;

import org.junit.runner.Description;

class ClassContext extends Context {
  public final Class<?> source;

  public ClassContext(String id, String displayName, Class<?> source) {
    super(id, displayName);
    this.source = source;
  }

  @Override
  public Description getDescription() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasSpecs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long numSpecs() {
    throw new UnsupportedOperationException();
  }
}
