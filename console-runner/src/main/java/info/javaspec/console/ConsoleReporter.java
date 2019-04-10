package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

final class ConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Stack<ScopeState> scopes;

  private int numStarted;
  private int numFailed;
  private int numPassed;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new Stack<>();
    this.scopes.push(new ScopeState()); //TODO KDK: Consider putting the root scope on the stack #runStarting
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this::printMessage);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
    //TODO KDK: Push a new, derived scope onto the stack that has indentation starting at nested collections
    this.scopes.peek().println(collection.description());
  }

  @Override
  public void endCollection(SpecCollection collection) {
//    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasFailingSpecs() {
    return numFailed > 0;
  }

  @Override
  public void runStarting() { }

  @Override
  public void runFinished() {
    printSeparator();
    printMessage(
      "[Testing complete] Passed: %d, Failed: %d, Total: %d",
      this.numPassed,
      this.numFailed,
      this.numStarted
    );
  }

  @Override
  public void specStarting(Spec spec) {
    this.numStarted++;
    printListItem(spec.intendedBehavior());
  }

  @Override
  public void specFailed(Spec spec) {
    this.numFailed++;
    this.output.println(": FAIL");
  }

  @Override
  public void specPassed(Spec spec) {
    this.numPassed++;
    this.output.println(": PASS");
  }

  private void printListItem(String item) {
    this.output.print("- ");
    this.output.print(item);
  }

  private void printMessage(String format, Object... args) {
    printMessage(String.format(format, args));
  }

  private void printMessage(String message) {
    this.output.println(message);
  }

  private void printSeparator() {
    this.output.println();
  }

  private class ScopeState {
    private boolean hasPrintedAnyLines;

    public ScopeState() {
      this.hasPrintedAnyLines = false;
    }

    public void println(String lineWithinScope) {
      if(this.hasPrintedAnyLines)
        printSeparator();

      output.println(lineWithinScope);
      this.hasPrintedAnyLines = true;
    }
  }
}
