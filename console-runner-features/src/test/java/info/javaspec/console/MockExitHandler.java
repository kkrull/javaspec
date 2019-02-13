package info.javaspec.console;

import org.hamcrest.Matchers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

final class MockExitHandler implements ExitHandler {
  private List<Integer> exitReceived;

  public MockExitHandler() {
    this.exitReceived = new LinkedList<>();
  }

  @Override
  public void exit(int code) {
    this.exitReceived.add(code);
  }

  public void exitShouldHaveReceived(int code) {
    List<Integer> expectedCodes = Stream.of(code).collect(Collectors.toList());
    assertThat(this.exitReceived, Matchers.equalTo(expectedCodes));
  }
}
