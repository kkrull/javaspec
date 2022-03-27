package info.javaspec.console.plaintext;

import java.io.PrintStream;

final class EventCounter {
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

  public int specFailed() {
    return ++this.numSpecsFailed;
  }

  public void specPassed() {
    this.numSpecsPassed++;
  }
}
