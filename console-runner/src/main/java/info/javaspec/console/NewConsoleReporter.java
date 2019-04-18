package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) { }

  @Override
  public void endCollection(SpecCollection collection) { }

  @Override
  public boolean hasFailingSpecs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void runStarting() { }

  @Override
  public void runFinished() {
    this.output.println("[Testing complete] Passed: 0, Failed: 0, Total: 0");
  }

  @Override
  public void specStarting(Spec spec) {
    this.output.print("- " + spec.intendedBehavior());
  }

  @Override
  public void specFailed(Spec spec) {
    this.output.println(": FAIL");
  }

  @Override
  public void specPassed(Spec spec) {
    this.output.println(": PASS");
  }
}
