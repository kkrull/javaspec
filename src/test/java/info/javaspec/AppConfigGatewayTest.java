package info.javaspec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.testutil.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class AppConfigGatewayTest {
  @Test
  public void givenANonExistentResource_throwsInvalidPropertiesException() {
    AppConfigGateway.InvalidPropertiesException ex = Assertions.capture(AppConfigGateway.InvalidPropertiesException.class,
      () -> AppConfigGateway.fromPropertyResource(null));
    assertThat(ex.getMessage(), equalTo("Invalid property stream: null"));
  }

  @Test
  public void givenANonPropertyResource_throwsInvalidPropertiesException() {
    AppConfigGateway.InvalidPropertiesException ex = Assertions.capture(AppConfigGateway.InvalidPropertiesException.class,
      () -> AppConfigGateway.fromPropertyResource("/info/javaspec/not-properties.txt"));
    assertThat(ex.getMessage(), equalTo("Invalid property stream: /info/javaspec/not-properties.txt"));
  }


//  @Test
//  public void givenAStreamWithoutProperties_throwsInvalidPropertyException() {
//    AppConfigGateway.InvalidPropertiesException ex = Assertions.capture(AppConfigGateway.InvalidPropertiesException.class,
//      () -> AppConfigGateway.fromPropertyStream(new BrokenInputStream()));
//    assertThat(ex.getMessage(), containsString("BrokenInputStream"));
//  }
//
//  @Test
//  public void givenPropertiesLackingTheSpecifiedKey_returnsNull() {
//    AppConfigGateway gateway = AppConfigGateway.fromProperties(new Properties());
//    assertThat(gateway.version(), nullValue());
//  }
}