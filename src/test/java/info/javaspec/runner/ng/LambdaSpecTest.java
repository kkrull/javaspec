package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class LambdaSpecTest {
  public class isIgnored {
    @Test
    public void givenAnItFieldWithAnAssignedNoArgLambda_returnsFalse() throws Exception {
      new LambdaSpec("one", "two").isIgnored();
    }
  }
}
