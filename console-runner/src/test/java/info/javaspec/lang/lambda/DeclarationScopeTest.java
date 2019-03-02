package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static info.javaspec.testutil.Assertions.capture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class DeclarationScopeTest {
  public class createSpec {
    @Test
    public void throwsWhenNoSubjectHasBeenDefined() throws Exception {
      DeclarationScope declaration = new DeclarationScope();
      Exception exception = capture(Exceptions.NoSubjectDefined.class,
        () -> declaration.createSpec("goes boom", () -> {}));
      assertThat(exception.getMessage(), equalTo("No subject defined for spec: goes boom"));
    }
  }
}
