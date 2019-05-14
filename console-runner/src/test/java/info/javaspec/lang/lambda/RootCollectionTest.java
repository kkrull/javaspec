package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
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
public class RootCollectionTest {
  private RootCollection subject;

  public class subCollections {
    public class whenNoSubCollectionsHaveBeenAdded {
      @Test
      public void returnsAnEmptyCollection() throws Exception {
        subject = new RootCollection();
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
        subject = new RootCollection();
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
    public void returnsAnEmptyString() throws Exception {
      subject = new RootCollection();
      assertThat(subject.description(), isEmptyString());
    }
  }

  public class intendedBehaviors {
    @Test
    public void returnsAnEmptyCollection() throws Exception {
      subject = new RootCollection();
      assertThat(subject.intendedBehaviors(), empty());
    }
  }

  public class runSpecs {
    private RunObserver observer;

    @Before
    public void setup() throws Exception {
      observer = Mockito.mock(RunObserver.class);
    }

    @Test
    public void reportsTheRunStartingAndFinishing() throws Exception {
      subject = new RootCollection();
      subject.runSpecs(observer);
      Mockito.verify(observer).runStarting();
      Mockito.verify(observer).runFinished();
    }

    @Test
    public void runsSubCollectionsInTheOrderTheyWereAdded() throws Exception {
      SpecCollection firstChild = Mockito.mock(SpecCollection.class, "FirstSubCollection");
      SpecCollection secondChild = Mockito.mock(SpecCollection.class, "SecondSubCollection");

      subject = new RootCollection();
      subject.addSubCollection(firstChild);
      subject.addSubCollection(secondChild);
      subject.runSpecs(observer);

      InOrder order = Mockito.inOrder(firstChild, secondChild);
      order.verify(firstChild).runSpecs(observer);
      order.verify(secondChild).runSpecs(observer);
      order.verifyNoMoreInteractions();
    }
  }
}
