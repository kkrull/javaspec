package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder.DeclarationScopeFactory;
import info.javaspec.lang.lambda.InstanceSpecFinder.DeclarationStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class InstanceSpecFinderTest {
  private InstanceSpecFinder subject;
  private DeclarationScopeFactory scopeFactory;

  public class findSpecs {
    public class givenAnyNumberOfSpecClasses {
      @Before
      public void setup() throws Exception {
        scopeFactory = Mockito.mock(DeclarationScopeFactory.class);
        subject = new InstanceSpecFinder(scopeFactory);
      }

      @Test
      public void createsASpecDeclarationContext() throws Exception {
        subject.findSpecs(anySpecClasses());
        Mockito.verify(scopeFactory).declareInOwnScope(Mockito.any());
      }

      @Test
      public void returnsTheSuiteFromTheContext() throws Exception {
        Suite contextReturns = Mockito.mock(Suite.class);
        Mockito.when(scopeFactory.declareInOwnScope(Mockito.any()))
          .thenReturn(contextReturns);

        Suite returned = subject.findSpecs(anySpecClasses());
        assertThat(returned, sameInstance(contextReturns));
      }
    }

    public class given1OrMoreSpecClasses {
      @Before
      public void setup() throws Exception {
        scopeFactory = new DeclarationScopeFactoryDouble(Mockito.mock(Suite.class));
        subject = new InstanceSpecFinder(scopeFactory);
      }

      @Test
      public void instantiatesEachSpecClassInsideTheDeclarationContext() throws Exception {
        subject.findSpecs(Collections.singletonList(InstanceSpy.class));
        InstanceSpy.numTimesInstantiatedShouldBe(1);
      }
    }

    public class whenInstantiatingASpecClassThrows {
      @Before
      public void setup() throws Exception {
        scopeFactory = new DeclarationScopeFactoryDouble(Mockito.mock(Suite.class));
        subject = new InstanceSpecFinder(scopeFactory);
      }

      @Test(expected = Exceptions.SpecDeclarationFailed.class)
      public void throwsSpecDeclarationFailed() throws Exception {
        subject.findSpecs(Collections.singletonList(ConstructorGoesBoom.class));
      }
    }
  }

  private List<Class<?>> anySpecClasses() {
    return Collections.emptyList();
  }

  public static final class DeclarationScopeFactoryDouble implements InstanceSpecFinder.DeclarationScopeFactory {
    private final Suite suite;

    public DeclarationScopeFactoryDouble(Suite suite) {
      this.suite = suite;
    }

    @Override
    public Suite declareInOwnScope(DeclarationStrategy strategy) {
      strategy.declareSpecs();
      return this.suite;
    }
  }

  public static class ConstructorGoesBoom {
    public ConstructorGoesBoom() {
      throw new RuntimeException("bang!");
    }
  }

  public static class InstanceSpy {
    private static int _numTimesInstantiated = 0;

    public static void numTimesInstantiatedShouldBe(int expected) {
      assertThat(_numTimesInstantiated, equalTo(expected));
    }

    public InstanceSpy() {
      _numTimesInstantiated++;
    }
  }
}
