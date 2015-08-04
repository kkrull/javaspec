package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

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

    public class given1OrMoreSpecs {
      private final RunNotifier notifier = mock(RunNotifier.class);

      @Test @Ignore
      public void runsEachSpec() throws Exception {
        Spec firstChild = MockSpec.anyValid();
        Spec secondChild = MockSpec.anyValid();
        ClassContext subject = AClassContext.withSpecs(firstChild, secondChild);

        subject.run(notifier);
        Mockito.verify(firstChild).run(notifier);
        Mockito.verify(secondChild).run(notifier);
      }
    }

    public class given1OrMoreSubContexts {
      private final RunNotifier notifier = mock(RunNotifier.class);

      @Test
      public void runsEachSubcontext() throws Exception {
        Context firstChild = info.javaspec.runner.MockContext.anyValid();
        Context secondChild = info.javaspec.runner.MockContext.anyValid();
        ClassContext subject = AClassContext.withSubContexts(firstChild, secondChild);

        subject.run(notifier);
        Mockito.verify(firstChild).run(notifier);
        Mockito.verify(secondChild).run(notifier);
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
      return ClassContext.create(source);
    }

    public static ClassContext withSpecs(Spec... specs) {
      return new ClassContext("", newArrayList(specs), newArrayList());
    }

    public static ClassContext withSubContexts(Context... subcontexts) {
      return new ClassContext("", newArrayList(), newArrayList(subcontexts));
    }
  }

}
