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
  
  public void addChild(Class<?> child) {
    children.add(new Context(child));
//    System.out.printf("addChild: self=%s, value=%s, children=%s\n", toString(), value, children);
  }

  public List<Context> getChildren() {
//    System.out.printf("getChildren: self=%s, value=%s, children=%s\n", toString(), value, children);
    return new ArrayList<Context>(children);
  }
  
//  @Override
//  public String toString() {
//    return String.format("<Context value=%s, children=%s>", value.getName(), children);
//  }
}