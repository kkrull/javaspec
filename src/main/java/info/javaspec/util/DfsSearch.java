package info.javaspec.util;

import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class DfsSearch<N> {
  private final N root;
  private final Function<N, Stream<N>> getChildren;
  
  public DfsSearch(N root, Function<N, Stream<N>> getChildren) {
    this.root = root;
    this.getChildren = getChildren;
  }
  
  public boolean anyNodeMatches(Predicate<N> isMatchingNode) {
    Stack<N> toVisit = new Stack<N>();
    toVisit.push(root);
    while(!toVisit.isEmpty()) {
      N current = toVisit.pop();
      if(isMatchingNode.test(current))
        return true;
      else
        getChildren.apply(current).forEach(toVisit::push);
    }
    
    return false;
  }
}