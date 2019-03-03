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

  public class childSuites {
    public class whenNoChildSuitesHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new RootSuite();
        assertThat(subject.childSuites(), empty());
      }
    }

    public class whenOneOrMoreChildSuitesHaveBeenAdded {
      private Suite firstChild, secondChild;

      @Before
      public void setup() throws Exception {
        firstChild = Mockito.mock(Suite.class, "AddedFirst");
        secondChild = Mockito.mock(Suite.class, "AddedSecond");
        subject = new RootSuite();
        subject.addChildSuite(firstChild);
        subject.addChildSuite(secondChild);
      }

      @Test
      public void returnsThoseChildSuitesInTheOrderTheyWereAdded() throws Exception {
        assertThat(subject.childSuites(), contains(firstChild, secondChild));
      }

      @Test
      public void mutationOfTheReturnedListDoesNotAffectTheSuite() throws Exception {
        List<Suite> listToMutate = subject.childSuites();
        listToMutate.clear();
        assertThat(subject.childSuites(), not(empty()));
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
    public class whenNoChildSpecsHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new RootSuite();
        assertThat(subject.intendedBehaviors(), empty());
      }
    }

    public class whenChildSpecsHaveBeenAdded {
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
      Mockito.verify(reporter).suiteStarting(subject);
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
    public void runsChildSuitesInTheOrderTheyWereAdded() throws Exception {
      Suite firstChild = Mockito.mock(Suite.class, "FirstSuite");
      Suite secondChild = Mockito.mock(Suite.class, "SecondSuite");

      subject = new RootSuite();
      subject.addChildSuite(firstChild);
      subject.addChildSuite(secondChild);
      subject.runSpecs(reporter);

      InOrder order = Mockito.inOrder(firstChild, secondChild);
      order.verify(firstChild).runSpecs(reporter);
      order.verify(secondChild).runSpecs(reporter);
      order.verifyNoMoreInteractions();
    }

    @Test
    public void runsSpecsInThisSuiteBeforeRunningChildSuites() throws Exception {
      Suite childSuite = Mockito.mock(Suite.class, "ChildSuite");
      Spec spec = Mockito.mock(Spec.class, "Spec");

      subject = new RootSuite();
      subject.addSpec(spec);
      subject.addChildSuite(childSuite);
      subject.runSpecs(reporter);

      InOrder order = Mockito.inOrder(childSuite, spec);
      order.verify(spec).run(reporter);
      order.verify(childSuite).runSpecs(reporter);
      order.verifyNoMoreInteractions();
    }

  }

  private Spec specWithBehavior(String behavior) {
    Spec spec = Mockito.mock(Spec.class);
    Mockito.when(spec.intendedBehavior()).thenReturn(behavior);
    return spec;
  }
}
