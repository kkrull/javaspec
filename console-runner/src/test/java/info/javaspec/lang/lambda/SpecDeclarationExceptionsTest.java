package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.lang.lambda.SpecDeclaration.DeclarationAlreadyStartedException;
import info.javaspec.lang.lambda.SpecDeclaration.DeclarationNotStartedException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class SpecDeclarationExceptionsTest {
  public class getMessage {
    @Test
    public void describesProblemsToDevelopersWhenDeclarationHasNotStarted() throws Exception {
      Exception subject = new DeclarationNotStartedException();
      assertThat(subject.getMessage(), equalTo("No declaration has been started.  Has SpecDeclaration::beginDeclaration been called?"));
    }

    @Test
    public void describesProblemsToDevelopersWhenDeclarationHasAlreadyBeenStarted() throws Exception {
      Exception subject = new DeclarationAlreadyStartedException();
      assertThat(subject.getMessage(), equalTo("Declaration has already been started.  Please call SpecDeclaration::endDeclaration on the prior declaration, if a brand new root suite is desired."));
    }
  }
}
