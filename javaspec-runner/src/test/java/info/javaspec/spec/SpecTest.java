package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.ContextFactory;
import info.javaspec.context.FakeContext;
import info.javaspec.dsl.It;
import info.javaspec.spec.ClassFactory.FaultyClassInitializer;
import info.javaspec.spec.ClassFactory.UnsupportedConstructor;
import info.javaspecproto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.isThrowableMatching;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class SpecTest {
  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    @Test
    public void itThrowsFaultyClassInitializer_givenAnExplodingClassInitializer() throws Exception {
      FaultyClassInitializer ex = capture(FaultyClassInitializer.class, () -> {
        Context context = ContextFactory.createRootContext(FailingClassInitializer.class);
        context.run(notifier);
      });

      assertThat(ex.getMessage(),
        matchesRegex("^Failed to load class .*FailingClassInitializer.* due to a faulty static initializer$"));
      assertThat(ex.getCause(), instanceOf(ExceptionInInitializerError.class));
    }
  }

  //Hide the class here to prevent the class loader from running before the test that uses it
  private static class FailingClassInitializer {
    static { explode(); }
    private static void explode() { throw new RuntimeException("Faulty class initializer"); }
    It will_fail = () -> assertEquals(1, 1);
  }
}
