package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.NoSubjectDefined;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

import static info.javaspec.testutil.Assertions.capture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class DeclarationScopeTest {
  private NewDeclarationScope scope;
  private SpecCollection root;
  private SpecCollection subjectCollection;

  public class createRootCollection {
    @Test
    public void returnsARootCollection() throws Exception {
      scope = anyDeclarationScope();
      root = scope.createRootCollection();

      assertThat(root, instanceOf(RootCollection.class));
    }

    public class whenNoSubjectsHaveBeenDeclared {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        scope = anyDeclarationScope();
        root = scope.createRootCollection();

        assertThat(root.intendedBehaviors(), empty());
        assertThat(root.subCollections(), empty());
      }
    }

    public class whenASubjectHasBeenDeclared {
      @Test
      public void returnsANestedSequentialCollectionForThatSubject() throws Exception {
        scope = anyDeclarationScope();
        scope.declareSpecsFor("Anvil", anyBehaviorDeclaration());
        root = scope.createRootCollection();

        shouldHaveSubCollectionsDescribing(root, contains("Anvil"));
        subjectCollection = root.subCollections().get(0);
        assertThat(subjectCollection, instanceOf(SequentialCollection.class));
      }
    }

    public class whenASubjectHasBeenDeclaredWithoutAnySpecs {
      @Test
      public void thatSubjectsCollectionHasNoSpecs() throws Exception {
        scope = anyDeclarationScope();
        scope.declareSpecsFor(anySubject(), anyBehaviorDeclaration());
        root = scope.createRootCollection();

        subjectCollection = root.subCollections().get(0);
        assertThat(subjectCollection.intendedBehaviors(), empty());
      }
    }

    public class whenASubjectHasBeenDeclaredWith1OrMoreSpecs {
      @Test
      public void thatSubjectsCollectionHasSpecsForThoseBehaviors() throws Exception {
        scope = anyDeclarationScope();
        scope.declareSpecsFor(anySubject(), () -> {
          scope.createSpec("one", anyBehaviorVerification());
          scope.createSpec("two", anyBehaviorVerification());
        });

        root = scope.createRootCollection();
        subjectCollection = root.subCollections().get(0);
        assertThat(subjectCollection.intendedBehaviors(), contains("one", "two"));
      }

      @Test
      public void thatSubjectsCollectionHasSpecsWithThoseVerifications() throws Exception {
        BehaviorVerification verification = Mockito.mock(BehaviorVerification.class);
        scope = anyDeclarationScope();
        scope.declareSpecsFor(anySubject(), () -> {
          scope.createSpec("one", verification);
        });

        root = scope.createRootCollection();
        subjectCollection = root.subCollections().get(0);
        subjectCollection.runSpecs(anyRunObserver());
        Mockito.verify(verification).run();
      }
    }

    public class whenANestedSubjectHasBeenDeclaredInsideOfAnother {
      @Test
      public void thatSubjectsCollectionHasASubCollectionForThatContext() throws Exception {
        scope = anyDeclarationScope();
        scope.declareSpecsFor("outer subject", () -> {
          scope.declareSpecsFor("inner subject", anyBehaviorDeclaration());
        });

        root = scope.createRootCollection();
        shouldHaveSubCollectionsDescribing(root, contains("outer subject"));

        subjectCollection = root.subCollections().get(0);
        shouldHaveSubCollectionsDescribing(subjectCollection, contains("inner subject"));
      }
    }

    public class whenSpecsAreDeclaredForAnOuterSubjectAfterDeclaringAnInnerSubject {
      @Test
      public void theSpecsExistInTheScopeInWhichTheyAreDeclared() throws Exception {
        scope = anyDeclarationScope();
        scope.declareSpecsFor("outer subject", () -> {
          scope.declareSpecsFor("inner subject", anyBehaviorDeclaration());

          scope.createSpec("afterthought", anyBehaviorVerification());
        });

        root = scope.createRootCollection();
        SpecCollection outerSubject = root.subCollections().get(0);
        assertThat(outerSubject.intendedBehaviors(), contains("afterthought"));

        SpecCollection innerSubject = outerSubject.subCollections().get(0);
        assertThat(innerSubject.intendedBehaviors(), empty());
      }
    }
  }

  public class createSpec {
    @Test
    public void throwsWhenNoSubjectHasBeenDefined() throws Exception {
      scope = anyDeclarationScope();
      Exception exception = capture(NoSubjectDefined.class, () ->
        scope.createSpec("goes boom", anyBehaviorVerification()));

      assertThat(exception.getMessage(), equalTo("No subject defined for spec: goes boom"));
    }
  }

  public class describeSpecsFor {
    @Test @Ignore
    public void supportsNestedContexts() throws Exception {
    }
  }

  private String anySubject() {
    return "<default subject>";
  }

  private BehaviorDeclaration anyBehaviorDeclaration() {
    return () -> {};
  }

  private BehaviorVerification anyBehaviorVerification() {
    return () -> {};
  }

  private NewDeclarationScope anyDeclarationScope() {
    return new NewDeclarationScope();
  }

  private RunObserver anyRunObserver() {
    return Mockito.mock(RunObserver.class);
  }

  private static void shouldHaveSubCollectionsDescribing(
    SpecCollection collection,
    Matcher<Iterable<? extends String>> descriptionsMatcher) {

    List<String> descriptions = collection.subCollections().stream()
      .map(SpecCollection::description)
      .collect(Collectors.toList());
    assertThat(descriptions, descriptionsMatcher);
  }
}
