package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.lang.lambda.Exceptions.DeclarationAlreadyStarted;
import info.javaspec.lang.lambda.Exceptions.DeclarationNotStarted;
import info.javaspec.lang.lambda.Exceptions.NoSubjectDefined;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static info.javaspec.testutil.Assertions.capture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(HierarchicalContextRunner.class)
public class SpecDeclarationTest {
  @After
  public void resetStatics() {
    SpecDeclaration.reset();
  }

  public class whenDeclarationHasNotBegun { //TODO KDK: Add more tests for other, invalid state transitions
    @Test(expected = DeclarationNotStarted.class)
    public void getInstance_throwsWhenDeclarationHasNotBegun() throws Exception {
      SpecDeclaration.getInstance();
    }
  }

  public class whenDeclarationHasBegun {
    @Test(expected = DeclarationAlreadyStarted.class)
    public void beginDeclaration_throwsDeclarationAlreadyStarted() throws Exception {
      SpecDeclaration.beginDeclaration();
      SpecDeclaration.beginDeclaration();
    }

    @Test
    public void getInstance_returnsTheCurrentInstance() throws Exception {
      SpecDeclaration.beginDeclaration();
      assertThat(SpecDeclaration.getInstance(), instanceOf(SpecDeclaration.class));
    }
  }

  public class createSpec {
    @Test
    public void throwsWhenNoSubjectHasBeenDefined() throws Exception {
      SpecDeclaration declaration = new SpecDeclaration();
      Exception exception = capture(NoSubjectDefined.class,
        () -> declaration.createSpec("goes boom", () -> {}));
      assertThat(exception.getMessage(), equalTo("No subject defined for spec: goes boom"));
    }
  }
}
