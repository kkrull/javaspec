package org.javaspec.util;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.javaspec.testutil.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.primitives.Booleans;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DfsSearchTest {
  public class anyNodeMatches {
    public class givenJustTheRoot {
      private final Tree tree = new Tree().withNode("root");
      
      @Test
      public void returnsThePredicatesResultOnTheRootNode() {
        assertThat(anyNodeMatches(tree, "root", "root"), equalTo(true));
        assertThat(anyNodeMatches(tree, "root", "something-else"), equalTo(false));
      }
    }
    
    public class givenATreeOf2OrMoreLevels {
      private final Tree tree = new Tree()
        .withNode("a", "ab", "ac", "ad") //L1-L2
        .withNode("ac", "ace") //L3
        .withNode("ace", "acef", "aceg"); //L4 
      
      @Test
      public void andAPredicateMatching1OrMoreNodes_returnsTrue() {
        List<Boolean> queriesOnEachNode = Stream.of("a", "ab", "ac", "ad", "ace", "acef", "aceg")
          .map(query -> anyNodeMatches(tree, "a", query))
          .collect(toList());
        boolean[] allTrue = new boolean[7];
        Arrays.fill(allTrue, true);
        Assertions.assertListEquals(Booleans.asList(allTrue), queriesOnEachNode);
      }
      
      @Test
      public void andAPredicateNotMatchingAnyNodes_returnsFalse() {
        assertThat(anyNodeMatches(tree, "a", "acz"), equalTo(false));
      }
    }
  }
  
  private static boolean anyNodeMatches(Tree tree, String rootOfSearch, String query) {
    DfsSearch<String> subject = new DfsSearch<String>(rootOfSearch, tree::getChildren);
    return subject.anyNodeMatches(query::equals);
  }
  
  static class Tree {
    private final Map<String, String[]> adjancencyList = new TreeMap<String, String[]>();
    
    public Tree withNode(String parent, String... children) {
      adjancencyList.put(parent, children);
      return this;
    }
    
    public String[] getChildren(String parent) {
      String[] children = adjancencyList.containsKey(parent) ? adjancencyList.get(parent) : new String[0];
      return Arrays.copyOf(children, children.length);
    }
  }
}