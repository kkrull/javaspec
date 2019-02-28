package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder.DeclarerOfSpecs;
import info.javaspec.lang.lambda.InstanceSpecFinder.SpecContextFactory;
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
  private SpecContextFactory contextFactory;

  public class findSpecs {
    public class givenAnyNumberOfSpecClasses {
      @Before
      public void setup() throws Exception {
        contextFactory = Mockito.mock(SpecContextFactory.class);
        subject = new InstanceSpecFinder(contextFactory);
      }

      @Test
      public void createsASpecDeclarationContext() throws Exception {
        subject.findSpecs(anySpecClasses());
        Mockito.verify(contextFactory).withContext(Mockito.any());
      }

      @Test
      public void returnsTheSuiteFromTheContext() throws Exception {
        Suite contextReturns = Mockito.mock(Suite.class);
        Mockito.when(contextFactory.withContext(Mockito.any()))
          .thenReturn(contextReturns);

        Suite returned = subject.findSpecs(anySpecClasses());
        assertThat(returned, sameInstance(contextReturns));
      }
    }

    public class given1OrMoreSpecClasses {
      @Before
      public void setup() throws Exception {
        contextFactory = new SpecContextFactoryDouble(
          Mockito.mock(SpecDeclaration.class),
          Mockito.mock(Suite.class)
        );
        subject = new InstanceSpecFinder(contextFactory);
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
        contextFactory = new SpecContextFactoryDouble(
          Mockito.mock(SpecDeclaration.class),
          Mockito.mock(Suite.class)
        );
        subject = new InstanceSpecFinder(contextFactory);
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

  public static final class SpecContextFactoryDouble implements SpecContextFactory {
    private final SpecDeclaration context;
    private final Suite suite;

    public SpecContextFactoryDouble(SpecDeclaration context, Suite suite) {
      this.context = context;
      this.suite = suite;
    }

    @Override
    public Suite withContext(DeclarerOfSpecs declarer) {
      declarer.declareSpecs(this.context);
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
