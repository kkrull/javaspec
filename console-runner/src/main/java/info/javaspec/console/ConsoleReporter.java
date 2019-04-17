package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

final class ConsoleReporter implements Reporter {
  private final Stack<ReporterState> scopeStates;

  private int numStarted;
  private int numFailed;
  private int numPassed;

  public ConsoleReporter(PrintStream output) {
    this.scopeStates = new Stack<>();
    this.scopeStates.push(ReporterState.newRoot(output));
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    innerScopeState().writeMessage(lines);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
//    System.out.printf("[ConsoleReporter#beginCollection] collection=%s, scopeStates=%s\n",
//      collection.description(),
//      this.scopeStates
//    );

    ReporterState currentScope = innerScopeState();
    currentScope.beginCollection(collection);

    ReporterState newScope = currentScope.createInnerScope(collection.description());
    this.scopeStates.push(newScope);
  }

  @Override
  public void endCollection(SpecCollection collection) {
//    System.out.printf("[ConsoleReporter#endCollection] collection=%s, scopeStates=%s\n",
//      collection.description(),
//      this.scopeStates
//    );

    this.scopeStates.pop();
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
    innerScopeState().printSeparator();
    innerScopeState().writeMessage(
      "[Testing complete] Passed: %d, Failed: %d, Total: %d",
      this.numPassed,
      this.numFailed,
      this.numStarted
    );
  }

  @Override
  public void specStarting(Spec spec) {
    this.numStarted++;
    innerScopeState().specStarting(spec);
  }

  @Override
  public void specFailed(Spec spec) {
    this.numFailed++;
    innerScopeState().specFailed(spec);
  }

  @Override
  public void specPassed(Spec spec) {
    this.numPassed++;
    innerScopeState().specPassed(spec);
  }

  private ReporterState innerScopeState() {
    return this.scopeStates.peek();
  }
}
