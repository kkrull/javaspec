package info.javaspec;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public final class MockSpecReporter implements SpecReporter {
  private final List<Spec> specFailedReceived;
  private final List<Spec> specPassedReceived;

  public MockSpecReporter() {
    this.specFailedReceived = new LinkedList<>();
    this.specPassedReceived = new LinkedList<>();
  }

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
  public void collectionStarting(SpecCollection collection) { }
}
