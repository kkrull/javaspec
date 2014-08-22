package org.javaspec.util;

import java.util.function.Function;
import java.util.function.Predicate;

public final class DfsSearch<N> {
  private final Function<N, N[]> getChildren;
  
  public DfsSearch(Function<N, N[]> getChildren) {
    this.getChildren = getChildren;
  }
  
  public boolean anyNodeMatches(N root, Predicate<N> isMatchingNode) {
    return isMatchingNode.test(root);
  }
}