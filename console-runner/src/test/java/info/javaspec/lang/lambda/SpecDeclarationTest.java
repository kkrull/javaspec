package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.lang.lambda.SpecDeclaration.NoSubjectDefinedException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static info.javaspec.testutil.Assertions.capture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class SpecDeclarationTest {
  public class createSpec {
    @Test
    public void throwsWhenNoSubjectHasBeenDefined() throws Exception {
      SpecDeclaration declaration = new SpecDeclaration();
      Exception exception = capture(NoSubjectDefinedException.class,
        () -> declaration.createSpec("goes boom", () -> {}));
      assertThat(exception.getMessage(), equalTo("No subject defined for spec: goes boom"));
    }
  }
}
