package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Deque<CollectionScope> scopes;
  private boolean hasEverPrintedAnything;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
    this.hasEverPrintedAnything = false;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
    CollectionScope containingScope = this.scopes.peekLast();
    containingScope.beginCollection(collection);
    this.hasEverPrintedAnything = true;

    CollectionScope newScope = CollectionScope.forCollection(collection, containingScope);
    this.scopes.addLast(newScope);
  }

  @Override
  public void endCollection(SpecCollection collection) {
    this.scopes.removeLast();
  }

  @Override
  public boolean hasFailingSpecs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void runStarting() {
    this.scopes.addLast(CollectionScope.forRoot(this.output));
  }

  @Override
  public void runFinished() {
    if(this.hasEverPrintedAnything)
      this.output.println();

    this.output.println("[Testing complete] Passed: 0, Failed: 0, Total: 0");
  }

  @Override
  public void specStarting(Spec spec) {
    this.scopes.peekLast().specStarting(spec);
  }

  @Override
  public void specFailed(Spec spec) {
    this.scopes.peekLast().specFailed(spec);
    this.hasEverPrintedAnything = true;
  }

  @Override
  public void specPassed(Spec spec) {
    this.scopes.peekLast().specPassed(spec);
    this.hasEverPrintedAnything = true;
  }

  private static final class CollectionScope {
    private final boolean isRoot;
    private final PrintStream output;
    private final String collectionIndent;
    private final String specIndent;
    private int numCollectionsPrinted;

    public static CollectionScope forCollection(SpecCollection collection, CollectionScope parent) {
      String indentSpecsOnlyInInnerCollections = parent.isRoot ? "" : parent.specIndent + "  ";
      return new CollectionScope(
        false,
        parent.output,
        parent.collectionIndent + "  ",
        indentSpecsOnlyInInnerCollections
      );
    }

    public static CollectionScope forRoot(PrintStream output) {
      return new CollectionScope(
        true,
        output,
        "",
        ""
      );
    }

    private CollectionScope(boolean isRoot, PrintStream output, String collectionIndent, String specIndent) {
      this.isRoot = isRoot;
      this.output = output;
      this.collectionIndent = collectionIndent;
      this.specIndent = specIndent;
      this.numCollectionsPrinted = 0;
    }

    public void beginCollection(SpecCollection collection) {
      if(this.numCollectionsPrinted > 0)
        this.output.println();

      this.output.println(this.collectionIndent + collection.description());
      this.numCollectionsPrinted++;
    }

    public void specStarting(Spec spec) {
      this.output.print(this.specIndent + "- " + spec.intendedBehavior());
    }

    public void specFailed(Spec spec) {
      this.output.println(": FAIL");
    }

    public void specPassed(Spec spec) {
      this.output.println(": PASS");
    }
  }
}
