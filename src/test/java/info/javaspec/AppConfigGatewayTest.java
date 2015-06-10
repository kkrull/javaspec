package info.javaspec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static info.javaspec.testutil.Assertions.capture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class AppConfigGatewayTest {
  public class version {
    @Test
    public void givenANonExistentResource_throwsInvalidProperties() {
      Exception ex = capture(AppConfigGateway.InvalidProperties.class,
        () -> AppConfigGateway.fromPropertyResource("does-not-exist.txt"));
      assertThat(ex.getMessage(), equalTo("Invalid property stream: does-not-exist.txt"));
    }

    @Test
    public void givenAFileWithoutThatProperty_throwsMissingProperty() {
      AppConfigGateway subject = AppConfigGateway.fromPropertyResource("/info/javaspec/not-properties.txt");
      Exception ex = capture(AppConfigGateway.MissingProperty.class, subject::version);
      assertThat(ex.getMessage(), equalTo("Missing property: javaspec.version"));
    }

    @Test
    public void givenACompletePropertyFile_returnsTheVersion() {
      AppConfigGateway subject = AppConfigGateway.fromPropertyResource("/info/javaspec/version.properties");
      assertThat(subject.version(), equalTo("1.2.4.8"));
    }
  }
}