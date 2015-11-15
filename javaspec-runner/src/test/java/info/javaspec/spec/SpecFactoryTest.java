package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.AmbiguousFixture;
import info.javaspec.context.Context;
import info.javaspec.context.ContextFactory;
import info.javaspec.context.FakeContext;
import info.javaspecproto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class SpecFactoryTest {
  private Spec subject;
  private final List<String> events = new LinkedList<>();

  public class addSpecsFromClass {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class givenANestedContextWithBeforeSpecLambdasAtMultipleLevels {
      @Before
      public void setup() throws Exception {
        subject = getSpec(ContextClasses.NestedEstablish.innerContext.class, "asserts");
        ContextClasses.NestedEstablish.setEventListener(events::add);
        subject.run(notifier);
      }

      @After
      public void releaseSpy() {
        ContextClasses.NestedEstablish.setEventListener(null);
      }

      @Test
      public void runsBeforeSpecLambdasOutsideInBeforeTheAssertion() throws Exception {
        assertThat(events, equalTo(newArrayList(
          "ContextClasses.NestedEstablish::new",
          "ContextClasses.NestedEstablish.innerContext::new",
          "ContextClasses.NestedEstablish::arranges",
          "ContextClasses.NestedEstablish::innerContext::arranges",
          "ContextClasses.NestedEstablish.innerContext::asserts"
        )));
      }
    }

    public class givenANestedContextWithAfterSpecLambdasAtMultipleLevels {
      @Before
      public void setup() throws Exception {
        subject = getSpec(ContextClasses.NestedCleanup.innerContext.class, "asserts");
        ContextClasses.NestedEstablish.setEventListener(events::add);
        subject.run(notifier);
      }

      @After
      public void releaseSpy() {
        ContextClasses.NestedCleanup.setEventListener(null);
      }

      @Test
      public void runsAfterSpecLambdasInsideOutAfterTheAssertion() throws Exception {
        assertThat(events, equalTo(newArrayList(
          "ContextClasses.NestedCleanup::new",
          "ContextClasses.NestedCleanup.innerContext::new",
          "ContextClasses.NestedCleanup.innerContext::asserts",
          "ContextClasses.NestedCleanup::innerContext::cleans",
          "ContextClasses.NestedCleanup::cleans"
        )));
      }
    }

    public class givenAContextClassWithMultipleSetupFieldsOfTheSameType {
      @Test
      public void givenAContextClassWithMultipleEstablishFields_throwsAmbiguousSpecFixture() throws Exception {
        shouldThrowAmbiguousFixture(ContextClasses.TwoBecause.class);
        AmbiguousFixture ex = shouldThrowAmbiguousFixture(ContextClasses.TwoEstablish.class);
        assertThat(ex.getMessage(), matchesRegex("^Only 1 field of type Establish is allowed in context class .*TwoEstablish$"));
      }

      @Test
      public void givenAContextClassWithMultipleBecauseFields_throwsAmbiguousSpecFixture() throws Exception {
        capture(AmbiguousFixture.class, () -> ContextFactory.createRootContext(ContextClasses.TwoBecause.class));
      }
    }

    public class givenAContextClassWithMultipleTeardownFieldsOfTheSameType {
      @Test
      public void throwsAmbiguousSpecFixture() throws Exception {
        shouldThrowAmbiguousFixture(ContextClasses.TwoCleanup.class);
      }
    }

    private AmbiguousFixture shouldThrowAmbiguousFixture(Class<?> source) {
      SpecFactory factory = new SpecFactory(FakeContext.withDescription(createSuiteDescription("suite")));
      return capture(AmbiguousFixture.class, () -> factory.addSpecsFromClass(source));
    }
  }

  private static Spec getSpec(Class<?> declaringClass, String fieldName) {
    Context context = FakeContext.withDescription(createSuiteDescription(declaringClass));
    SpecFactory specFactory = new SpecFactory(context);
    return specFactory.create(readField(declaringClass, fieldName));
  }

  private static Field readField(Class<?> declaringClass, String name) {
    try {
      return declaringClass.getDeclaredField(name);
    } catch(Exception e) {
      String message = String.format("Failed to read field %s from %s", name, declaringClass);
      throw new RuntimeException(message, e);
    }
  }
}
