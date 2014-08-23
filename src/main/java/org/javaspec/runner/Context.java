package org.javaspec.runner;

import java.util.LinkedList;
import java.util.List;

final class Context {
  public final Class<?> value;
  private final List<Context> children;
  
  public Context(Class<?> value) {
    this.value = value;
    this.children = new LinkedList<Context>();
  }
  
  private Context(Class<?> value, List<Context> children) {
    this.value = value;
    this.children = children;
  }
  
  public Context addChild(Class<?> child) {
    List<Context> withChild = new LinkedList<Context>(children);
    withChild.add(new Context(child));
    return new Context(value, withChild);
  }
}