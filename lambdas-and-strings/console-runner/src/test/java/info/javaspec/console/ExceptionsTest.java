package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Exceptions.CommandAlreadyAdded;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ExceptionsTest {
  @Test
  public void saysWhichCommandWasAlreadyAdded() throws Exception {
    CommandAlreadyAdded subject = CommandAlreadyAdded.named("popular");
    assertThat(subject.getMessage(), Matchers.equalTo("Command has already been added: popular"));
  }
}
