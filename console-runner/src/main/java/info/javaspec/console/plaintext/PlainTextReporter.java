package info.javaspec.console.plaintext;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import info.javaspec.console.Reporter;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlainTextReporter implements Reporter {
  private final PrintStream output;
  private final Deque<ReporterScope> scopes;
  private final EventCounter count;
  private final Map<Integer, Throwable> failures;

  public PlainTextReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
    this.count = new EventCounter();
    this.failures = new LinkedHashMap<>();
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
    ReporterScope reportCurrentEvent = scopeForCurrentEvents();
    reportCurrentEvent.beginCollection(collection);
    this.count.beginCollection();

    ReporterScope reportFutureEvents = ReporterScope.forCollection(reportCurrentEvent);
    this.scopes.addLast(reportFutureEvents);
  }

  @Override
  public void endCollection(SpecCollection collection) {
    this.scopes.removeLast();
  }

  @Override
  public boolean hasFailingSpecs() {
    return this.count.hasFailingSpecs();
  }

  @Override
  public void runStarting() {
    if(!this.scopes.isEmpty())
      throw new RunAlreadyStarted();

    this.count.reset();
    this.scopes.addLast(ReporterScope.forRoot(this.output));
  }

  @Override
  public void runFinished() {
    boolean hasPrintedAtLeastOneLine = this.count.haveAnyEventsOccurred();
    if(hasPrintedAtLeastOneLine)
      this.output.println();

    if(this.count.hasFailingSpecs()) {
      this.output.println("Specs failed:");
      failures.forEach(this::detailSpecFailure);
      this.output.println();
    }

    this.count.printSpecTally(this.output);
    this.scopes.removeLast();
  }

  @Override
  public void specStarting(Spec spec) {
    scopeForCurrentEvents().specStarting(spec);
    this.count.specStarting();
  }

  @Override
  public void specFailed(Spec spec, AssertionError failure) {
    specFailed(failure);
  }

  @Override
  public void specFailed(Spec spec, Exception failure) {
    specFailed(failure);
  }

  private void specFailed(Throwable error) {
    int howManyHaveFailedNow = this.count.specFailed();
    scopeForCurrentEvents().specFailed(howManyHaveFailedNow);
    this.failures.put(howManyHaveFailedNow, error);
  }

  @Override
  public void specPassed(Spec spec) {
    scopeForCurrentEvents().specPassed();
    this.count.specPassed();
  }

  private void detailSpecFailure(int referenceNumber, Throwable failure) {
    this.output.printf("[%d] ", referenceNumber);
    failure.printStackTrace(this.output);
  }

  private ReporterScope scopeForCurrentEvents() {
    return this.scopes.peekLast();
  }
}
