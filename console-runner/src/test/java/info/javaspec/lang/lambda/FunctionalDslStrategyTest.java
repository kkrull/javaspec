package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(HierarchicalContextRunner.class)
public class FunctionalDslStrategyTest {
  private SpecCollectionFactory subject;

  public class declareSpecs {
    @Test
    public void instantiatesEachSpecClass() throws Exception {
      subject = new FunctionalDslStrategy(Arrays.asList(
        OneInstanceSpy.class.getName(),
        AnotherInstanceSpy.class.getName()
      ));

      subject.declareSpecs();
      OneInstanceSpy.numTimesInstantiatedShouldBe(1);
      AnotherInstanceSpy.numTimesInstantiatedShouldBe(1);
    }

    @Test
    public void returnsARootSpecCollection() throws Exception {
      subject = new FunctionalDslStrategy(anySpecClasses());
      SpecCollection returned = subject.declareSpecs();
      assertThat(returned, instanceOf(RootCollection.class));
    }

    @Test @Ignore
    public void returnsTheSpecCollectionFromFunctionalDsl() throws Exception {
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassNameThatDoesNotExist() throws Exception {
      subject = new FunctionalDslStrategy(Collections.singletonList("com.bogus.Class"));
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassThatDoesNotLoad() throws Exception {
      subject = new FunctionalDslStrategy(Collections.singletonList("info.javaspec.lang.lambda.ExplodingStaticInitializer"));
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassThatCanNotBeInstantiated() throws Exception {
      subject = new FunctionalDslStrategy(Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslStrategyTest$AbstractClass"));
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassThatFailsToInstantiate() throws Exception {
      subject = new FunctionalDslStrategy(Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslStrategyTest$ExplodingConstructor"));
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAnInaccessibleClass() throws Exception {
      subject = new FunctionalDslStrategy(Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslStrategyTest$HiddenClass"));
      subject.declareSpecs();
    }
  }

  private List<String> anySpecClasses() {
    return Collections.emptyList();
  }

  public static abstract class AbstractClass { }

  public static class AnotherInstanceSpy {
    private static int _numTimesInstantiated = 0;

    public static void numTimesInstantiatedShouldBe(int expected) {
      assertThat(_numTimesInstantiated, equalTo(expected));
    }

    public AnotherInstanceSpy() {
      _numTimesInstantiated++;
    }
  }

  public static class ExplodingConstructor {
    public ExplodingConstructor() {
      throw new RuntimeException("bang!");
    }
  }

  private static class HiddenClass { }

  public static class OneInstanceSpy {
    private static int _numTimesInstantiated = 0;

    public static void numTimesInstantiatedShouldBe(int expected) {
      assertThat(_numTimesInstantiated, equalTo(expected));
    }

    public OneInstanceSpy() {
      _numTimesInstantiated++;
    }
  }
}
