package info.javaspec.console;

import org.hamcrest.Matchers;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;

class Fake {
  static final class SpecRunner {
    private final MockSpecReporter reporter;

    public SpecRunner(MockSpecReporter reporter) {
      this.reporter = reporter;
    }

    public void run(Suite suite) {
      suite.runSpecs(this.reporter);
    }
  }

  static final class Suite {
    private final MockSpec spec;

    public Suite(MockSpec spec) {
      this.spec = spec;
    }

    public void runSpecs(MockSpecReporter reporter) {
      reporter.specStarting(this.spec);
      this.spec.run();
      reporter.specPassed(this.spec);
    }
  }

  static final class MockSpec {
    private boolean runCalled;

    public MockSpec() {
      this.runCalled = false;
    }

    public void run() {
      this.runCalled = true;
    }

    public void runShouldHaveBeenCalled() {
      assertThat(this.runCalled, Matchers.equalTo(true));
    }
  }

  static final class MockSpecReporter {
    private final List<MockSpec> passReceived;
    private final List<MockSpec> startingReceived;

    public MockSpecReporter() {
      this.passReceived = new LinkedList<>();
      this.startingReceived = new LinkedList<>();
    }

    public void specPassed(MockSpec spec) {
      this.passReceived.add(spec);
    }

    public void specPassedShouldHaveReceived(MockSpec spec) {
      assertThat(this.passReceived, Matchers.contains(spec));
    }

    public void specStarting(MockSpec spec) {
      this.startingReceived.add(spec);
    }

    public void specStartingShouldHaveReceived(MockSpec spec) {
      assertThat(this.startingReceived, Matchers.contains(spec));
    }
  }
}
