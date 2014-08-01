package org.jspec;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ExampleTest {
  public class run {
    public class givenAnItFieldOfAnyVisibilityDeclaredInAClass {
      private final List<String> events = new LinkedList<String>();
      
      @SuppressWarnings("unused")
      private It inaccessibleThunk = () -> events.add("inaccessibleThunk ran");
      
      @Test
      public void runsTheFunctionAssignedToTheSpecifiedFieldInTheGivenObject() throws Exception {
        Example example = new Example(getClass().getDeclaredField("inaccessibleThunk"));
        example.run(this);
        assertThat(events, contains("inaccessibleThunk ran"));
      }
    }
  }
}
