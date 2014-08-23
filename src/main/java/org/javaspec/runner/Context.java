package org.javaspec.runner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class Context {
  public final Class<?> value;
  private final List<Context> children;
  
  public Context(Class<?> value) {
    this.value = value;
    this.children = new LinkedList<Context>();
  }
  
  public Context(Class<?> value, List<Context> subContexts) {
    this.value = value;
    this.children = new ArrayList<Context>(subContexts);
  }

  public void addChild(Class<?> child) {
    children.add(new Context(child));
  }

  public List<Context> getSubContexts() {
    return new ArrayList<Context>(children);
  }
}