package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.testutil.RunListenerSpy;
import info.javaspecproto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
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

  public class run {
    private ClassContext subject;

    public class givenAClassWith1OrMoreSpecs {
      private final List<String> events = new ArrayList<>();

      @Before
      public void setup() throws Exception {
        ContextClasses.OneIt.setEventListener(events::add);
      }

      @After
      public void cleanup() throws Exception {
        ContextClasses.OneIt.setEventListener(null);
      }

      @Test @Ignore
      public void delegatesToEachSpec() throws Exception {
        subject = AClassContext.of(ContextClasses.OneIt.class);
        subject.run(null);
        assertThat(events, contains("ContextClasses.OneIt::only_test"));
      }
    }

    public class given1OrMoreSubContexts {
      @Test @Ignore
      public void runsEachSubcontext() throws Exception {
        ClassContext firstChild = Mockito.mock(ClassContext.class);
        subject = AClassContext.withSubcontexts(firstChild);
        RunNotifier notifier = Mockito.mock(RunNotifier.class);
        subject.run(notifier);
        Mockito.verify(firstChild).run(notifier);
      }
    }
  }

  @Test
  public void aSpecIs_anNonStaticItField() throws Exception {
    assertThat(AClassContext.of(ContextClasses.StaticIt.class).numSpecs(), equalTo(0L));
  }

  @Test
  public void aSubcontextIs_aNonStaticInnerClass() throws Exception {
    assertThat(AClassContext.of(ContextClasses.NestedStaticClassIt.class).numSpecs(), equalTo(0L));
  }

  public static final class AClassContext {
    public static ClassContext of(Class<?> source) {
      return new ClassContext("id", "display", source);
    }

    public static ClassContext withSubcontexts(ClassContext... children) {
      return new ClassContext("id", "name", ContextClasses.OneIt.class, newArrayList(children));
    }
  }
}
