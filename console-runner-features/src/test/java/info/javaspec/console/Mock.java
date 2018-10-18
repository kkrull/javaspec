package info.javaspec.console;

import info.javaspec.LambdaSpec;
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

  static final class MockSpecReporter {
    private final List<LambdaSpec> failReceived;
    private final List<LambdaSpec> passReceived;
    private final List<LambdaSpec> startingReceived;

    public MockSpecReporter() {
      this.failReceived = new LinkedList<>();
      this.passReceived = new LinkedList<>();
      this.startingReceived = new LinkedList<>();
    }

    public boolean hasFailingSpecs() {
      return !this.failReceived.isEmpty();
    }

    public void specFailed(LambdaSpec spec) {
      this.failReceived.add(spec);
    }

    public void specFailedShouldHaveReceived(LambdaSpec spec) {
      assertThat(this.failReceived, Matchers.hasItem(Matchers.sameInstance(spec)));
    }

    public void specPassed(LambdaSpec spec) {
      this.passReceived.add(spec);
    }

    public void specPassedShouldHaveReceived(LambdaSpec spec) {
      assertThat(this.passReceived, Matchers.hasItem(Matchers.sameInstance(spec)));
    }

    public void specStarting(LambdaSpec spec) {
      this.startingReceived.add(spec);
    }

    public void specStartingShouldHaveReceived(LambdaSpec... specs) {
      Set<LambdaSpec> specsList = Stream.of(specs).collect(Collectors.toSet());
      assertThat(new HashSet<>(this.startingReceived), Matchers.equalTo(specsList));
    }
  }
}
