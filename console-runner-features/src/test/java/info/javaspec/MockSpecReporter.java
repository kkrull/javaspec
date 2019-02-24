package info.javaspec;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class MockSpecReporter implements SpecReporter {
  private int runStartingCalled;
  private int runFinishedCalled;

  private final List<Spec> specStartingReceived;
  private final List<Spec> specFailedReceived;
  private final List<Spec> specPassedReceived;
  private final List<Suite> suiteStartingReceived;

  public MockSpecReporter() {
    this.runStartingCalled = 0;
    this.runFinishedCalled = 0;
    this.specStartingReceived = new LinkedList<>();
    this.specFailedReceived = new LinkedList<>();
    this.specPassedReceived = new LinkedList<>();
    this.suiteStartingReceived = new LinkedList<>();
  }

  @Override
  public boolean hasFailingSpecs() {
    return !this.specFailedReceived.isEmpty();
  }

  @Override
  public void runFinished() {
    this.runFinishedCalled += 1;
  }

  public void runFinishedShouldHaveBeenCalled() {
    assertThat(this.runFinishedCalled, equalTo(1));
  }

  @Override
  public void runStarting() {
    this.runStartingCalled += 1;
  }

  public void runStartingShouldHaveBeenCalled() {
    assertThat(this.runStartingCalled, equalTo(1));
  }

  @Override
  public void specFailed(Spec spec) {
    this.specFailedReceived.add(spec);
  }

  public void specShouldHaveFailed(Spec spec) {
    assertThat(this.specFailedReceived, hasItem(sameInstance(spec)));
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

  public void specShouldHavePassed(Spec spec) {
    assertThat(this.specPassedReceived, hasItem(sameInstance(spec)));
  }

  public void specShouldHavePassed(String behavior) {
    List<String> passingSpecBehaviors = this.specPassedReceived.stream()
      .map(Spec::intendedBehavior)
      .collect(Collectors.toList());
    assertThat(passingSpecBehaviors, hasItem(equalTo(behavior)));
  }

  @Override
  public void specStarting(Spec spec) {
    this.specStartingReceived.add(spec);
  }

  public void specShouldHaveBeenStarted(Spec spec) {
    assertThat(this.specStartingReceived, hasItem(sameInstance(spec)));
  }

  @Override
  public void suiteStarting(Suite suite) {
    this.suiteStartingReceived.add(suite);
  }

  public void suiteShouldHaveBeenStarted(Suite suite) {
    assertThat(this.suiteStartingReceived, contains(sameInstance(suite)));
  }
}
