package info.javaspec.runner;

import org.junit.runner.Description;

abstract class Context {
  public final String id; //TODO KDK: Migrate to property-style methods
  public final String displayName;

  Context(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public abstract Description getDescription();
  public abstract boolean hasSpecs();
  public abstract long numSpecs();
}
