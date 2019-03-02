package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.lang.lambda.Exceptions.DeclarationAlreadyStarted;
import info.javaspec.lang.lambda.Exceptions.DeclarationNotStarted;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(HierarchicalContextRunner.class)
public class FunctionalDslDeclarationTest {
  @After
  public void resetStatics() {
    FunctionalDslDeclaration.reset();
  }

  public class whenDeclarationHasNotBegun { //TODO KDK: Add more tests for other, invalid state transitions
    @Test(expected = DeclarationNotStarted.class)
    public void getInstance_throwsWhenDeclarationHasNotBegun() throws Exception {
      FunctionalDslDeclaration.getInstance();
    }
  }

  public class whenDeclarationHasBegun {
    @Test(expected = DeclarationAlreadyStarted.class)
    public void beginDeclaration_throwsDeclarationAlreadyStarted() throws Exception {
      FunctionalDslDeclaration.beginDeclaration();
      FunctionalDslDeclaration.beginDeclaration();
    }

    @Test
    public void getInstance_returnsTheCurrentInstance() throws Exception {
      FunctionalDslDeclaration.beginDeclaration();
      assertThat(FunctionalDslDeclaration.getInstance(), instanceOf(DeclarationScope.class));
    }
  }
}
