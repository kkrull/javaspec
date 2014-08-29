package org.javaspec.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Context {
  public final Object id;
  public final String name;
  private final List<String> exampleNames; //TODO KDK: This should be a set; order is not guaranteed

  public Context(Object id, String name, Collection<String> exampleNames) {
    this.id = id;
    this.name = name;
    this.exampleNames = new ArrayList<String>(exampleNames);
  }

  public List<String> getExampleNames() {
    return new ArrayList<String>(exampleNames);
  }
}