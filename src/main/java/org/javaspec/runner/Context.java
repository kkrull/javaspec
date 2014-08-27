package org.javaspec.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Context {
  public final String name;
  private final List<String> exampleNames;

  public Context(String name, Collection<String> exampleNames) {
    this.name = name;
    this.exampleNames = new ArrayList<String>(exampleNames);
  }

  public List<String> getExampleNames() {
    return new ArrayList<String>(exampleNames);
  }
}