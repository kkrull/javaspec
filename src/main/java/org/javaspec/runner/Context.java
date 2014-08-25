package org.javaspec.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Context {
  public final String name;
  private final List<Context> children;
  
  public Context(String name, Collection<Context> subContexts) {
    this.name = name;
    this.children = new ArrayList<Context>(subContexts);
  }

  public List<Context> getSubContexts() {
    return new ArrayList<Context>(children);
  }
  
  public boolean hasChildren() {
    return !children.isEmpty();
  }
}