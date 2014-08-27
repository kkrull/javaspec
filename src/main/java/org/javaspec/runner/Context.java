package org.javaspec.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Context {
  public final Object id;
  public final String name;
  private final List<String> exampleNames;

  public Context(Object id, String name, Collection<String> exampleNames) {
    this.id = id;
    this.name = name;
    this.exampleNames = new ArrayList<String>(exampleNames);
  }

  public List<String> getExampleNames() {
    return new ArrayList<String>(exampleNames);
  }
}