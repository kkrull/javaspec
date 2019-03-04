package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Spec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class RootSuiteTest {
  private RootSuite subject;

  public class subCollections {
    public class whenNoSubCollectionsHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new RootSuite();
        assertThat(subject.subCollections(), empty());
      }
    }

    public class whenOneOrMoreSubCollectionsHaveBeenAdded {
      private Suite firstChild, secondChild;

      @Before
      public void setup() throws Exception {
        firstChild = Mockito.mock(Suite.class, "AddedFirst");
        secondChild = Mockito.mock(Suite.class, "AddedSecond");
        subject = new RootSuite();
        subject.addSubCollection(firstChild);
        subject.addSubCollection(secondChild);
      }

      @Test
      public void returnsThoseSubCollectionsInTheOrderTheyWereAdded() throws Exception {
        assertThat(subject.subCollections(), contains(firstChild, secondChild));
      }

      @Test
      public void mutationOfTheReturnedListDoesNotAffectTheSuite() throws Exception {
        List<Suite> listToMutate = subject.subCollections();
        listToMutate.clear();
        assertThat(subject.subCollections(), not(empty()));
      }
    }
  }

  public class description {
    @Test
    public void returnsAnEmptyString() throws Exception {
      subject = new RootSuite();
      assertThat(subject.description(), isEmptyString());
    }
  }

  public class intendedBehaviors {
    public class whenNoSpecsHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new RootSuite();
        assertThat(subject.intendedBehaviors(), empty());
      }
    }

    public class whenSpecsHaveBeenAdded {
      @Test
      public void returnsAListOfEachSpecsBehaviorInTheOrderTheyWereAdded() throws Exception {
        subject = new RootSuite();
        subject.addSpec(specWithBehavior("FirstBehavior"));
        subject.addSpec(specWithBehavior("SecondBehavior"));
        assertThat(subject.intendedBehaviors(), contains("FirstBehavior", "SecondBehavior"));
      }
    }
  }

  public class runSpecs {
    private SpecReporter reporter;

    @Before
    public void setup() throws Exception {
      reporter = Mockito.mock(SpecReporter.class);
    }

    @Test
    public void reportsThatTheSuiteIsBeingRun() throws Exception {
      subject = new RootSuite();
      subject.runSpecs(reporter);
      Mockito.verify(reporter).collectionStarting(subject);
    }

    @Test
    public void runsSpecsWithTheGivenReporter() throws Exception {
      Spec spec = Mockito.mock(Spec.class);

      subject = new RootSuite();
      subject.addSpec(spec);
      subject.runSpecs(reporter);

      Mockito.verify(spec).run(reporter);
    }

    @Test
    public void runsSpecsInTheOrderTheyWereAdded() throws Exception {
      Spec addedFirst = Mockito.mock(Spec.class);
      Spec addedSecond = Mockito.mock(Spec.class);

      subject = new RootSuite();
      subject.addSpec(addedFirst);
      subject.addSpec(addedSecond);
      subject.runSpecs(reporter);

      InOrder order = Mockito.inOrder(addedFirst, addedSecond);
      order.verify(addedFirst).run(Mockito.any());
      order.verify(addedSecond).run(Mockito.any());
      order.verifyNoMoreInteractions();
    }

    @Test
    public void runsSubCollectionsInTheOrderTheyWereAdded() throws Exception {
      Suite firstChild = Mockito.mock(Suite.class, "FirstSubCollection");
      Suite secondChild = Mockito.mock(Suite.class, "SecondSubCollection");

      subject = new RootSuite();
      subject.addSubCollection(firstChild);
      subject.addSubCollection(secondChild);
      subject.runSpecs(reporter);

      InOrder order = Mockito.inOrder(firstChild, secondChild);
      order.verify(firstChild).runSpecs(reporter);
      order.verify(secondChild).runSpecs(reporter);
      order.verifyNoMoreInteractions();
    }

    @Test
    public void runsSpecsInThisSuiteBeforeRunningSubCollections() throws Exception {
      Suite subCollection = Mockito.mock(Suite.class, "SubCollection");
      Spec spec = Mockito.mock(Spec.class, "Spec");

      subject = new RootSuite();
      subject.addSpec(spec);
      subject.addSubCollection(subCollection);
      subject.runSpecs(reporter);

      InOrder order = Mockito.inOrder(subCollection, spec);
      order.verify(spec).run(reporter);
      order.verify(subCollection).runSpecs(reporter);
      order.verifyNoMoreInteractions();
    }

  }

  private Spec specWithBehavior(String behavior) {
    Spec spec = Mockito.mock(Spec.class);
    Mockito.when(spec.intendedBehavior()).thenReturn(behavior);
    return spec;
  }
}
