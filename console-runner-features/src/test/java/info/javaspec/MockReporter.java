package info.javaspec;

import info.javaspec.console.Reporter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public final class MockReporter implements Reporter {
  private final List<Spec> specFailedReceived;
  private final List<Spec> specPassedReceived;

  public MockReporter() {
    this.specFailedReceived = new LinkedList<>();
    this.specPassedReceived = new LinkedList<>();
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) { }

  /* RunObserver */

  @Override
  public boolean hasFailingSpecs() {
    return !this.specFailedReceived.isEmpty();
  }

  @Override
  public void runFinished() { }

  @Override
  public void runStarting() { }

  @Override
  public void specFailed(Spec spec) {
    this.specFailedReceived.add(spec);
  }

  @Override
  public void specFailed(Spec spec, AssertionError _error) {
    this.specFailedReceived.add(spec);
  }

  @Override
  public void specFailed(Spec spec, Exception exception) {
    this.specFailedReceived.add(spec);
  }

  public void specShouldHaveFailed(String behavior) {
    List<String> failingSpecBehaviors = this.specFailedReceived.stream()
      .map(Spec::intendedBehavior)
      .collect(Collectors.toList());
    assertThat(failingSpecBehaviors, hasItem(equalTo(behavior)));
  }

  @Override
  public void specPassed(Spec spec) {
    this.specPassedReceived.add(spec);
  }

  public void specShouldHavePassed(String behavior) {
    List<String> passingSpecBehaviors = this.specPassedReceived.stream()
      .map(Spec::intendedBehavior)
      .collect(Collectors.toList());
    assertThat(passingSpecBehaviors, hasItem(equalTo(behavior)));
  }

  @Override
  public void specStarting(Spec spec) { }

  @Override
  public void beginCollection(SpecCollection collection) { }

  @Override
  public void endCollection(SpecCollection collection) { }
}
