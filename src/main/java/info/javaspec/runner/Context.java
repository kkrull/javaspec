package info.javaspec.runner;

import java.util.LinkedHashSet;
import java.util.Set;

final class Context {
  public final Object id;
  public final String name;
  private final Set<String> exampleNames;

  public Context(Object id, String name, Set<String> exampleNames) {
    this.id = id;
    this.name = name;
    this.exampleNames = new LinkedHashSet<String>(exampleNames);
  }

  public Set<String> getExampleNames() {
    return new LinkedHashSet<String>(exampleNames);
  }
}