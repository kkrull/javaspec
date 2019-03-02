package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.lang.lambda.Exceptions.DeclarationAlreadyStarted;
import info.javaspec.lang.lambda.Exceptions.DeclarationNotStarted;
import info.javaspec.lang.lambda.Exceptions.NoSubjectDefined;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class ExceptionsTest {
  public class declarationAlreadyStarted {
    @Test
    public void describesTheProblemToDevelopers() throws Exception {
      Exception subject = new DeclarationAlreadyStarted();
      assertThat(
        subject.getMessage(),
        equalTo("Declaration has already been started.  Please call FunctionalDslDeclaration::endDeclaration on the prior declaration, if a brand new root suite is desired.")
      );
    }
  }

  public class declarationNotStarted {
    @Test
    public void explainsTheProblemToDevelopers() throws Exception {
      Exception subject = new DeclarationNotStarted();
      assertThat(
        subject.getMessage(),
        equalTo("No declaration has been started.  Has FunctionalDslDeclaration::beginDeclaration been called?")
      );
    }
  }

  public class noSubjectDefined {
    @Test
    public void saysWhichSpecIsMissingASubject() throws Exception {
      Exception exception = NoSubjectDefined.forSpec("does a thing");
      assertThat(exception.getMessage(), equalTo("No subject defined for spec: does a thing"));
    }
  }

  public class specDeclarationFailed {
    @Test
    public void saysWhichClassCanNotBeInstantiated() throws Exception {
      Exception exception = SpecDeclarationFailed.whenInstantiating(Test.class, null);
      assertThat(exception.getMessage(), equalTo("Failed to instantiate class org.junit.Test, to declare specs"));
    }

    @Test
    public void includesTheCause() throws Exception {
      Exception cause = new SecurityException();
      Exception exception = SpecDeclarationFailed.whenInstantiating(Test.class, cause);
      assertThat(exception.getCause(), sameInstance(cause));
    }
  }
}
