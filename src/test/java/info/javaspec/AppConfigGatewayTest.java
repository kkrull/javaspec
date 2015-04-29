package info.javaspec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.testutil.Assertions;
import org.apache.commons.io.input.BrokenInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class AppConfigGatewayTest {
  @Test
  public void givenANonExistentResource_throwsInvalidPropertiesException() {
    AppConfigGateway.InvalidPropertiesException ex = Assertions.capture(AppConfigGateway.InvalidPropertiesException.class,
      () -> AppConfigGateway.fromPropertyStream(null));
    assertThat(ex.getMessage(), equalTo("Invalid property stream: null"));
  }

  @Test
  public void givenAStreamWithoutProperties_throwsInvalidPropertyException() {
    AppConfigGateway.InvalidPropertiesException ex = Assertions.capture(AppConfigGateway.InvalidPropertiesException.class,
      () -> AppConfigGateway.fromPropertyStream(new BrokenInputStream()));
    assertThat(ex.getMessage(), containsString("BrokenInputStream"));
  }

  @Test
  public void givenPropertiesLackingTheSpecifiedKey_returnsNull() {
    AppConfigGateway gateway = AppConfigGateway.fromProperties(new Properties());
    assertThat(gateway.version(), nullValue());
  }

  @Test
  public void givenCompleteProperties_returnsVersion() {

  }
}