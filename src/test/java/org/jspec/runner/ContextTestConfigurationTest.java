package org.jspec.runner;

import static org.hamcrest.Matchers.equalTo;
import static org.jspec.util.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.jspec.proto.JSpecExamples;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ContextTestConfigurationTest {
  public class getContextClass {
    private final TestConfiguration subject = ContextTestConfiguration.forClass(JSpecExamples.One.class);

    @Test
    public void givenAClass_returnsTheClass() {
      assertThat(subject.getContextClass(), equalTo(JSpecExamples.One.class));
    }
  }
  
  public class hasInitializationErrors {
    public class givenAClassWithNoInitializationErrors {
      private final TestConfiguration subject = ContextTestConfiguration.forClass(JSpecExamples.One.class);
      
      @Test
      public void returnsFalse() {
        assertThat(subject.hasInitializationErrors(), equalTo(false));
      }
    }
    
    public class givenAClassWith1OrMoreInitializationErrors {
      @Test @Ignore("pending")
      public void returnsTrue() {
        fail("pending");
      }
    }
  }

  public class getExamples {
    public class givenAContextWithNoItFields {
      private final TestConfiguration subject = ContextTestConfiguration.forClass(JSpecExamples.Empty.class);
      
      @Test
      public void returnsEmpty() {
        assertListEquals(Collections.emptyList(), subject.getExamples());
      }
    }
    
    public class givenAContextWith1OrMoreItFields {
      @Test @Ignore("wip")
      public void returnsAnExampleForEachItField() {
        fail("pending");
      }
    }
  }
}