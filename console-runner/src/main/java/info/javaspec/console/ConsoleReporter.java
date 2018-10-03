package info.javaspec.console;

import info.javaspec.SpecObserver;

import java.io.PrintStream;

public class ConsoleReporter implements SpecObserver {
  private final PrintStream output;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
  }

  @Override
  public void testRunStarted() { }

  @Override
  public void testRunFinished() {
    this.output.println();
    this.output.println("Passed: 1\tFailed: 0\tTotal: 1");
  }
}
