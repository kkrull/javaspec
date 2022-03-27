package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class FunctionalDslFactoryTest {
  private SpecCollectionFactory subject;

  public class declareSpecs {
    @Before
    public void setup() throws Exception {
      FunctionalDsl.reset();
    }

    @Test
    public void triesToLoadTheSpecifiedSpecClassesFromTheGivenLoader() throws Exception {
      SpyClassLoader loader = new SpyClassLoader();
      subject = new FunctionalDslFactory(
        loader,
        Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslFactoryTest$ClassLoadingSpy")
      );

      subject.declareSpecs();
      loader.loadClassShouldHaveReceived("info.javaspec.lang.lambda.FunctionalDslFactoryTest$ClassLoadingSpy");
    }

    public class givenAClassLoaderThatHasTheSpecifiedClasses {
      @Test
      public void instantiatesEachSpecClass() throws Exception {
        subject = new FunctionalDslFactory(
          this.getClass().getClassLoader(),
          Arrays.asList(
            OneInstanceSpy.class.getName(),
            AnotherInstanceSpy.class.getName()
          )
        );

        subject.declareSpecs();
        OneInstanceSpy.numTimesInstantiatedShouldBe(1);
        AnotherInstanceSpy.numTimesInstantiatedShouldBe(1);
      }

      @Test
      public void returnsASubCollectionForEachSubjectDescribedWithTheDsl() throws Exception {
        subject = new FunctionalDslFactory(
          this.getClass().getClassLoader(),
          Collections.singletonList(DescribeSpy.class.getName())
        );
        SpecCollection returned = subject.declareSpecs();
        DescribeSpy.declarationShouldHaveBeenInvoked();

        List<String> subjectsDescribed = returned.subCollections().stream()
          .map(SpecCollection::description)
          .collect(Collectors.toList());
        assertThat(subjectsDescribed, equalTo(Collections.singletonList("DescribeSpy")));
      }
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassNameThatDoesNotExist() throws Exception {
      subject = new FunctionalDslFactory(
        this.getClass().getClassLoader(),
        Collections.singletonList("com.bogus.Class")
      );
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassThatDoesNotLoad() throws Exception {
      subject = new FunctionalDslFactory(
        this.getClass().getClassLoader(),
        Collections.singletonList("info.javaspec.lang.lambda.ExplodingStaticInitializer")
      );
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassThatCanNotBeInstantiated() throws Exception {
      subject = new FunctionalDslFactory(
        this.getClass().getClassLoader(),
        Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslFactoryTest$AbstractClass")
      );
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAClassThatFailsToInstantiate() throws Exception {
      subject = new FunctionalDslFactory(
        this.getClass().getClassLoader(),
        Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslFactoryTest$ExplodingConstructor")
      );
      subject.declareSpecs();
    }

    @Test(expected = SpecDeclarationFailed.class)
    public void throwsGivenAnInaccessibleClass() throws Exception {
      subject = new FunctionalDslFactory(
        this.getClass().getClassLoader(),
        Collections.singletonList("info.javaspec.lang.lambda.FunctionalDslFactoryTest$HiddenClass")
      );
      subject.declareSpecs();
    }
  }

  /* Spec classes that **must not be referenced anywhere else**, to avoid premature class loading */

  public abstract static class AbstractClass { }

  public static class AnotherInstanceSpy {
    private static int _numTimesInstantiated = 0;

    public static void numTimesInstantiatedShouldBe(int expected) {
      assertThat(_numTimesInstantiated, equalTo(expected));
    }

    public AnotherInstanceSpy() {
      _numTimesInstantiated++;
    }
  }

  public static class ClassLoadingSpy { }

  public static class DescribeSpy {
    private static BehaviorDeclaration _declaration;

    static void declarationShouldHaveBeenInvoked() {
      Mockito.verify(_declaration, Mockito.times(1)).declareSpecs();
    }

    {
      _declaration = Mockito.mock(BehaviorDeclaration.class);
      FunctionalDsl.describe("DescribeSpy", _declaration);
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

  /* Class loaders */

  private static final class SpyClassLoader extends ClassLoader {
    private final List<String> loadClassGot = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
      this.loadClassGot.add(name);
      return super.loadClass(name);
    }

    public void loadClassShouldHaveReceived(String name) {
      //https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-
      synchronized(this.loadClassGot) {
        assertThat(this.loadClassGot, contains(name));
      }
    }
  }
}
