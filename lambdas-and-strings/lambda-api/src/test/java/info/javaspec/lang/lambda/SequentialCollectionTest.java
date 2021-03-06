package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class SequentialCollectionTest {
  private SequentialCollection subject;

  public class subCollections {
    public class whenNoSubCollectionsHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new SequentialCollection(anyDescription());
        assertThat(subject.subCollections(), empty());
      }
    }

    public class whenOneOrMoreSubCollectionsHaveBeenAdded {
      private SpecCollection firstChild;
      private SpecCollection secondChild;

      @Before
      public void setup() throws Exception {
        firstChild = Mockito.mock(SpecCollection.class, "AddedFirst");
        secondChild = Mockito.mock(SpecCollection.class, "AddedSecond");
        subject = new SequentialCollection(anyDescription());
        subject.addSubCollection(firstChild);
        subject.addSubCollection(secondChild);
      }

      @Test
      public void returnsThoseSubCollectionsInTheOrderTheyWereAdded() throws Exception {
        assertThat(subject.subCollections(), contains(firstChild, secondChild));
      }

      @Test
      public void mutationOfTheReturnedListDoesNotAffectTheCollection() throws Exception {
        List<SpecCollection> listToMutate = subject.subCollections();
        listToMutate.clear();
        assertThat(subject.subCollections(), not(empty()));
      }
    }
  }

  public class description {
    @Test
    public void returnsTheGivenDescription() throws Exception {
      subject = new SequentialCollection("a widget");
      assertThat(subject.description(), equalTo("a widget"));
    }
  }

  public class intendedBehaviors {
    public class whenNoSpecsHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new SequentialCollection(anyDescription());
        assertThat(subject.intendedBehaviors(), empty());
      }
    }

    public class whenSpecsHaveBeenAdded {
      @Test
      public void returnsAListOfEachSpecsBehaviorInTheOrderTheyWereAdded() throws Exception {
        subject = new SequentialCollection(anyDescription());
        subject.addSpec(specWithBehavior("FirstBehavior"));
        subject.addSpec(specWithBehavior("SecondBehavior"));
        assertThat(subject.intendedBehaviors(), contains("FirstBehavior", "SecondBehavior"));
      }
    }
  }

  public class runSpecs {
    private RunObserver observer;

    @Before
    public void setup() throws Exception {
      observer = Mockito.mock(RunObserver.class);
    }

    @Test
    public void reportsThatTheCollectionIsBeingRun() throws Exception {
      subject = new SequentialCollection(anyDescription());
      subject.runSpecs(observer);
      Mockito.verify(observer).beginCollection(subject);
      Mockito.verify(observer).endCollection(subject);
      Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void runsSpecsWithTheGivenReporter() throws Exception {
      Spec spec = Mockito.mock(Spec.class);

      subject = new SequentialCollection(anyDescription());
      subject.addSpec(spec);
      subject.runSpecs(observer);

      Mockito.verify(spec).run(observer);
    }

    @Test
    public void runsSpecsInTheOrderTheyWereAdded() throws Exception {
      Spec addedFirst = Mockito.mock(Spec.class);
      Spec addedSecond = Mockito.mock(Spec.class);

      subject = new SequentialCollection(anyDescription());
      subject.addSpec(addedFirst);
      subject.addSpec(addedSecond);
      subject.runSpecs(observer);

      InOrder order = Mockito.inOrder(addedFirst, addedSecond);
      order.verify(addedFirst).run(Mockito.any());
      order.verify(addedSecond).run(Mockito.any());
      order.verifyNoMoreInteractions();
    }

    @Test
    public void runsSubCollectionsInTheOrderTheyWereAdded() throws Exception {
      SpecCollection firstChild = Mockito.mock(SpecCollection.class, "FirstSubCollection");
      SpecCollection secondChild = Mockito.mock(SpecCollection.class, "SecondSubCollection");

      subject = new SequentialCollection(anyDescription());
      subject.addSubCollection(firstChild);
      subject.addSubCollection(secondChild);
      subject.runSpecs(observer);

      InOrder order = Mockito.inOrder(firstChild, secondChild);
      order.verify(firstChild).runSpecs(observer);
      order.verify(secondChild).runSpecs(observer);
      order.verifyNoMoreInteractions();
    }

    @Test
    public void runsSpecsInThisCollectionBeforeRunningSubCollections() throws Exception {
      SpecCollection subCollection = Mockito.mock(SpecCollection.class, "SubCollection");
      Spec spec = Mockito.mock(Spec.class, "Spec");

      subject = new SequentialCollection(anyDescription());
      subject.addSpec(spec);
      subject.addSubCollection(subCollection);
      subject.runSpecs(observer);

      InOrder order = Mockito.inOrder(subCollection, spec);
      order.verify(spec).run(observer);
      order.verify(subCollection).runSpecs(observer);
      order.verifyNoMoreInteractions();
    }

  }

  private String anyDescription() {
    return "";
  }

  private Spec specWithBehavior(String behavior) {
    Spec spec = Mockito.mock(Spec.class);
    Mockito.when(spec.intendedBehavior()).thenReturn(behavior);
    return spec;
  }
}
