package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.DeclarationNotStarted;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(HierarchicalContextRunner.class)
public class FunctionalDslTest {
  private SpecCollection returned;

  @Before
  public void setup() throws Exception {
    FunctionalDsl.reset();
  }

  @AfterClass
  public static void teardown() throws Exception {
    FunctionalDsl.reset();
  }

  public class closeScope {
    public class whenTheScopeIsOpen {
      @Test
      public void returnsTheRootSpecCollectionFromTheDeclarationScope() throws Exception {
        FunctionalDsl.openScope();
        returned = FunctionalDsl.closeScope();
        assertThat(returned, instanceOf(RootCollection.class));
      }
    }
  }

  public class describe {
    @Test(expected = DeclarationNotStarted.class)
    public void throwsWhenTheScopeIsNotOpen() throws Exception {
      FunctionalDsl.describe(anySubject(), anyBehaviorDeclaration());
    }

    public class whenTheScopeIsOpen {
      @Test
      public void addsASpecCollectionForThatSubject() throws Exception {
        FunctionalDsl.openScope();
        FunctionalDsl.describe("a widget", anyBehaviorDeclaration());

        returned = FunctionalDsl.closeScope();
        List<String> descriptions = returned.subCollections().stream()
          .map(SpecCollection::description)
          .collect(Collectors.toList());
        assertThat(descriptions, equalTo(Collections.singletonList("a widget")));
      }
    }
  }

  public class it {
    @Test(expected = DeclarationNotStarted.class)
    public void throwsWhenTheScopeIsNotOpen() throws Exception {
      FunctionalDsl.it(anySubject(), anyBehaviorVerification());
    }

    public class whenTheScopeIsOpen {
      @Test
      public void addsASpecCollectionForThatSubject() throws Exception {
        FunctionalDsl.openScope();
        FunctionalDsl.describe(anySubject(), () -> {
          FunctionalDsl.it("behaves", anyBehaviorVerification());
        });

        returned = FunctionalDsl.closeScope();
        List<String> descriptions = returned.subCollections().stream()
          .map(SpecCollection::intendedBehaviors)
          .flatMap(behaviors -> behaviors.stream())
          .collect(Collectors.toList());
        assertThat(descriptions, equalTo(Collections.singletonList("behaves")));
      }
    }
  }

  private String anyBehavior() {
    return "";
  }

  private BehaviorDeclaration anyBehaviorDeclaration() {
    return () -> {};
  }

  private BehaviorVerification anyBehaviorVerification() {
    return () -> {};
  }

  private String anySubject() {
    return "";
  }
}
