package info.javaspec.context;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ContextFactoryTest {
  public class createRootContext {
    @Test
    public void givenAContextClassWithMultipleEstablishFields_throwsAmbiguousSpecFixture() throws Exception {
      AmbiguousFixture ex = capture(AmbiguousFixture.class,
        () -> ContextFactory.createRootContext(ContextClasses.TwoEstablish.class));
      assertThat(ex.getMessage(), matchesRegex("^Only 1 field of type Establish is allowed in context class .*TwoEstablish$"));
    }

    @Test
    public void givenAContextClassWithMultipleBecauseFields_throwsAmbiguousSpecFixture() throws Exception {
      capture(AmbiguousFixture.class, () -> ContextFactory.createRootContext(ContextClasses.TwoBecause.class));
    }

    @Test
    public void givenAContextClassWithMultipleCleanupFields_throwsAmbiguousSpecFixture() throws Exception {
      capture(AmbiguousFixture.class, () -> ContextFactory.createRootContext(ContextClasses.TwoCleanup.class));
    }
  }
}
