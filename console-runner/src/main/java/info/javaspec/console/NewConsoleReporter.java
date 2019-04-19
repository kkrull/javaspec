package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;
  private boolean hasPrintedLines;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.hasPrintedLines = false;
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
    if(this.hasPrintedLines)
      this.output.println();

    this.output.println("[Testing complete] Passed: 0, Failed: 0, Total: 0");
  }

  @Override
  public void specStarting(Spec spec) {
    this.output.print("- " + spec.intendedBehavior());
  }

  @Override
  public void specFailed(Spec spec) {
    this.output.println(": FAIL");
    this.hasPrintedLines = true;
  }

  @Override
  public void specPassed(Spec spec) {
    this.output.println(": PASS");
    this.hasPrintedLines = true;
  }
}
