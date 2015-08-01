package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class ClassContextTest {
  public class numSpecs {
    @Test
    public void givenNoSpecsOrChildContexts_returns_0() throws Exception {
      assertThat(AClassContext.of(ContextClasses.Empty.class).numSpecs(), equalTo(0L));
    }

    @Test
    public void givenAClassWith1OrMoreSpecs_countsThoseSpecs() throws Exception {
      assertThat(AClassContext.of(ContextClasses.TwoIt.class).numSpecs(), equalTo(2L));
    }

    @Test
    public void givenAClassWithSubcontexts_sumsSpecsInThoseClasses() throws Exception {
      assertThat(AClassContext.of(ContextClasses.NestedContexts.class).numSpecs(), equalTo(2L));
    }
  }

  public static final class AClassContext {
    public static ClassContext of(Class<?> source) {
      return new ClassContext("id", "display", source);
    }
  }
}
