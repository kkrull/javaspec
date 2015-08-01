package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class ClassContextTest {
  public class hasSpecs {
    @Test
    public void givenAClassWithoutAnySpecs_returns_false() throws Exception {
      ClassContext subject = AClassContext.of(ContextClasses.Empty.class);
      assertThat(subject.hasSpecs(), equalTo(false));
    }

    @Test
    public void givenAClassWithSpecs_returns_true() throws Exception {
      ClassContext subject = AClassContext.of(ContextClasses.OneIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }

    @Test
    public void givenAClassWhereASubcontextHasSpecs_returns_true() throws Exception {
      ClassContext subject = AClassContext.of(ContextClasses.NestedIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }
  }

  public class numSpecs {
    @Test
    public void givenNoSpecsOrChildContexts_returns_0() throws Exception {
      ClassContext subject = AClassContext.of(ContextClasses.Empty.class);
      assertThat(subject.numSpecs(), equalTo(0L));
    }

    @Test
    public void givenAClassWith1OrMoreSpecs_countsThoseSpecs() throws Exception {
      ClassContext subject = AClassContext.of(ContextClasses.TwoIt.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }

    @Test
    public void givenAClassWithSubcontexts_sumsSpecsInThoseClasses() throws Exception {
      ClassContext subject = AClassContext.of(ContextClasses.NestedContexts.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }
  }

  public static final class AClassContext {
    public static ClassContext of(Class<?> source) {
      return new ClassContext("id", "display", source);
    }
  }
}
