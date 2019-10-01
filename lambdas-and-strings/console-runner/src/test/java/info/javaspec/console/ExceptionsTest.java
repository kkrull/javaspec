package info.javaspec.console;

import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Exceptions.CommandAlreadyAdded;
import info.javaspec.console.Exceptions.InvalidArguments;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class ExceptionsTest {
  public class commandAlreadyAdded {
    public class named {
      @Test
      public void saysWhichCommandWasAlreadyAdded() throws Exception {
        CommandAlreadyAdded subject = CommandAlreadyAdded.named("popular");
        assertThat(subject.getMessage(), equalTo("Command has already been added: popular"));
      }
    }
  }

  public class invalidArguments {
    @Test
    public void includesTheCause() throws Exception {
      ParameterException cause = new ParameterException("wat");
      assertThat(InvalidArguments.dueTo(cause).getCause(), sameInstance(cause));
      assertThat(InvalidArguments.forCommand("run", cause).getCause(), sameInstance(cause));
    }
  }
}
