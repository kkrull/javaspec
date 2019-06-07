package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

final class ConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Deque<ReporterScope> scopes;
  private final EventCounter count;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
    this.count = new EventCounter();
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

    ReporterScope reportFutureEvents = ReporterScope.forCollection(collection, reportCurrentEvent);
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

    this.count.printSpecTally(this.output);
    this.scopes.removeLast();
  }

  @Override
  public void specStarting(Spec spec) {
    scopeForCurrentEvents().specStarting(spec);
    this.count.specStarting();
  }

  @Override
  public void specFailed(Spec spec, AssertionError error) {
    this.count.specFailed();
    scopeForCurrentEvents().specFailed(this.count.numSpecsFailed);
//    error.printStackTrace(this.output);
  }

  @Override
  public void specFailed(Spec spec, Exception exception) {
    scopeForCurrentEvents().specFailed();
    this.count.specFailed();
  }

  @Override
  public void specPassed(Spec spec) {
    scopeForCurrentEvents().specPassed();
    this.count.specPassed();
  }

  private ReporterScope scopeForCurrentEvents() {
    return this.scopes.peekLast();
  }

  private static final class EventCounter {
    private int numCollectionsStarted;
    private int numSpecsFailed;
    private int numSpecsPassed;
    private int numSpecsTotal;

    public EventCounter() {
      this.numSpecsFailed = 0;
      this.numSpecsPassed = 0;
      this.numSpecsTotal = 0;
    }

    public void beginCollection() {
      this.numCollectionsStarted++;
    }

    public boolean hasFailingSpecs() {
      return this.numSpecsFailed > 0;
    }

    public boolean haveAnyEventsOccurred() {
      return this.numCollectionsStarted > 0
        || this.numSpecsTotal > 0;
    }

    public void printSpecTally(PrintStream output) {
      output.println(String.format(
        "[Testing complete] Passed: %d, Failed: %d, Total: %d",
        this.numSpecsPassed,
        this.numSpecsFailed,
        this.numSpecsTotal
      ));
    }

    public void reset() {
      this.numSpecsFailed = 0;
      this.numSpecsPassed = 0;
      this.numSpecsTotal = 0;
    }

    public void specStarting() {
      this.numSpecsTotal++;
    }

    public void specFailed() {
      this.numSpecsFailed++;
    }

    public void specPassed() {
      this.numSpecsPassed++;
    }
  }

  static class RunAlreadyStarted extends IllegalStateException {
    public RunAlreadyStarted() {
      super("Tried to start a new run, before finishing the current one."
        + "  Please call #runFinished, first."
      );
    }
  }
}
