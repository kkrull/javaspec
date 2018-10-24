package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.SpecReporter;
import org.hamcrest.Matchers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

class Mock {
  private Mock() { /* static class */ }

  static final class MockExitHandler implements ExitHandler {
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

  static final class MockSpec implements LambdaSpec {
    private boolean runCalled;
    private AssertionError runThrows;

    public static MockSpec runPasses() {
      return new MockSpec(null);
    }

    public static MockSpec runThrows(AssertionError e) {
      return new MockSpec(e);
    }

    private MockSpec(AssertionError runThrows) {
      this.runCalled = false;
      this.runThrows = runThrows;
    }

    @Override
    public void run() {
      this.runCalled = true;

      if(this.runThrows != null)
        throw this.runThrows;
    }

    public void runShouldHaveBeenCalled() {
      assertThat(this.runCalled, Matchers.equalTo(true));
    }

    @Override
    public String toString() {
      return String.format("MockSpec{runCalled=%s, runThrows=%s}", runCalled, runThrows);
    }
  }

  static final class MockSpecReporter implements SpecReporter {
    private int runStartingCalled;
    private int runFinishedCalled;

    private final List<LambdaSpec> specFailedReceived;
    private final List<LambdaSpec> specPassedReceived;
    private final List<LambdaSpec> specStartingReceived;


    public MockSpecReporter() {
      this.runStartingCalled = 0;
      this.runFinishedCalled = 0;
      this.specFailedReceived = new LinkedList<>();
      this.specPassedReceived = new LinkedList<>();
      this.specStartingReceived = new LinkedList<>();
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
    public void specFailed(LambdaSpec spec) {
      this.specFailedReceived.add(spec);
    }

    public void specFailedShouldHaveReceived(LambdaSpec spec) {
      assertThat(this.specFailedReceived, Matchers.hasItem(Matchers.sameInstance(spec)));
    }

    @Override
    public void specPassed(LambdaSpec spec) {
      this.specPassedReceived.add(spec);
    }

    public void specPassedShouldHaveReceived(LambdaSpec spec) {
      assertThat(this.specPassedReceived, Matchers.hasItem(Matchers.sameInstance(spec)));
    }

    @Override
    public void specStarting(LambdaSpec spec) {
      this.specStartingReceived.add(spec);
    }

    public void specStartingShouldHaveReceived(LambdaSpec... specs) {
      Set<LambdaSpec> specsList = Stream.of(specs).collect(Collectors.toSet());
      assertThat(new HashSet<>(this.specStartingReceived), Matchers.equalTo(specsList));
    }
  }
}
