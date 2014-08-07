package org.jspec.runner;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  public class givenAnItFieldOfAnyVisibilityDeclaredInAClass {
    private final List<String> events = new LinkedList<String>();
    
    @SuppressWarnings("unused")
    private It inaccessibleThunk = () -> events.add("inaccessibleThunk ran");
    
    @Test
    public void runsTheFunctionAssignedToTheSpecifiedFieldInTheGivenObject() throws Exception {
      Example example = new FieldExample(getClass().getDeclaredField("inaccessibleThunk"));
      example.run();
      assertThat(events, contains("inaccessibleThunk ran"));
    }
  }
}