package info.javaspec;

import org.hamcrest.Matchers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;

public final class MockSpecReporter implements SpecReporter {
  private int runStartingCalled;
  private int runFinishedCalled;

  private final Map<Spec, String> specStartingReceived;
  private final List<Spec> specFailedReceived;
  private final List<Spec> specPassedReceived;
  private final List<Suite> suiteStartingReceived;

  public MockSpecReporter() {
    this.runStartingCalled = 0;
    this.runFinishedCalled = 0;
    this.specStartingReceived = new LinkedHashMap<>();
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
    assertThat(this.runFinishedCalled, Matchers.equalTo(1));
  }

  @Override
  public void runStarting() {
    this.runStartingCalled += 1;
  }

  public void runStartingShouldHaveBeenCalled() {
    assertThat(this.runStartingCalled, Matchers.equalTo(1));
  }

  @Override
  public void specFailed(Spec spec) {
    this.specFailedReceived.add(spec);
  }

  public void specShouldHaveFailed(Spec spec) {
    assertThat(this.specFailedReceived, Matchers.hasItem(Matchers.sameInstance(spec)));
  }

  @Override
  public void specPassed(Spec spec) {
    this.specPassedReceived.add(spec);
  }

  public void specShouldHavePassed(Spec spec) {
    assertThat(this.specPassedReceived, Matchers.hasItem(Matchers.sameInstance(spec)));
  }

  @Override
  public void specStarting(Spec spec) {
    this.specStartingReceived.put(spec, spec.intendedBehavior());
  }

  public void specShouldHaveBeenStarted(Spec spec, String intendedBehavior) {
    assertThat(this.specStartingReceived, Matchers.hasKey(spec));
    assertThat(this.specStartingReceived.get(spec), Matchers.equalTo(intendedBehavior));
  }

  @Override
  public void suiteStarting(Suite suite) {
    this.suiteStartingReceived.add(suite);
  }

  public void suiteStartingShouldHaveBeenCalled(Suite expectedSuite) {
    assertThat(this.suiteStartingReceived, Matchers.contains(Matchers.sameInstance(expectedSuite)));
  }
}
