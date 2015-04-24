package info.javaspec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Properties;

@RunWith(HierarchicalContextRunner.class)
public class AppConfigGatewayTest {
  @Test
  public void givenANonExistentResource_throwsSomething() {
    AppConfigGateway.fromProperties(new Properties());
  }
}