package org.javaspec.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Context {
  public final String name;
  private final List<String> exampleNames;
  private final List<Context> subcontexts;

  public Context(String name, Collection<String> exampleNames, Collection<Context> subcontexts) {
    this.name = name;
    this.exampleNames = new ArrayList<String>(exampleNames);
    this.subcontexts = new ArrayList<Context>(subcontexts);
  }

  public List<String> getExampleNames() {
    return new ArrayList<String>(exampleNames);
  }

  public List<Context> getSubContexts() {
    return new ArrayList<Context>(subcontexts);
  }

  public boolean hasSubContexts() {
    return !subcontexts.isEmpty();
  }
}