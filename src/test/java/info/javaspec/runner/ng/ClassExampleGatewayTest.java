package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  public class hasExamples {
    @Test
    public void givenAClassWithoutAnyItFields_returnsFalse() throws Exception {
      NewExampleGateway subject = new ClassExampleGateway(ContextClasses.Empty.class);
      assertThat(subject.hasExamples(), is(false));
    }
  }
}
