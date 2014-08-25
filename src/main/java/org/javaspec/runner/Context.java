package org.javaspec.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class Context {
  public final String name;
  private final List<Context> children;
  
  public Context(String name) {
    this.name = name;
    this.children = new LinkedList<Context>();
  }
  
  public Context(String name, Collection<Context> subContexts) {
    this.name = name;
    this.children = new ArrayList<Context>(subContexts);
  }

  public void addChild(String childName) {
    children.add(new Context(childName));
  }

  public List<Context> getSubContexts() {
    return new ArrayList<Context>(children);
  }
}