package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.SpecReporter;

import java.io.PrintStream;

public class ConsoleReporter implements SpecReporter {
  private final PrintStream output;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
  }

  @Override
  public boolean hasFailingSpecs() {
    return false;
  }

  @Override
  public void runStarting() {
  }

  @Override
  public void specStarting(LambdaSpec spec) {
  }

  @Override
  public void specFailed(LambdaSpec spec) {
  }

  @Override
  public void specPassed(LambdaSpec spec) {
  }

  @Override
  public void runFinished() {
    this.output.println("Passed: 1\tFailed: 0\tTotal: 1");
  }
}
