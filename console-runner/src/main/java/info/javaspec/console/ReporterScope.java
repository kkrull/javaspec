package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;

abstract class ReporterScope {
  private final PrintStream output;
  private final String collectionIndent;
  private final String specIndent;

  private boolean hasPrintedAnyCollections;

  public static ReporterScope forCollection(ReporterScope parent) {
    return new CollectionReporterScope(
      parent.output,
      parent.collectionIndent + "  ",
      parent.specIndent + parent.howMuchToIndentSpecsInChildScope()
    );
  }

  public static ReporterScope forRoot(PrintStream output) {
    return new RootReporterScope(
      output,
      "",
      ""
    );
  }

  private ReporterScope(PrintStream output, String collectionIndent, String specIndent) {
    this.output = output;
    this.collectionIndent = collectionIndent;
    this.specIndent = specIndent;
    this.hasPrintedAnyCollections = false;
  }

  public final void beginCollection(SpecCollection collection) {
    if(this.hasPrintedAnyCollections)
      this.output.println();

    this.output.println(this.collectionIndent + collection.description());
    this.hasPrintedAnyCollections = true;
  }

  protected abstract String howMuchToIndentSpecsInChildScope();

  public final void specStarting(Spec spec) {
    this.output.print(this.specIndent + "* " + spec.intendedBehavior());
  }

  public final void specFailed(int referenceNumber) {
    this.output.println(String.format(": FAIL [%d]", referenceNumber));
  }

  public final void specPassed() {
    this.output.println(": PASS");
  }

  private static final class CollectionReporterScope extends ReporterScope {
    public CollectionReporterScope(PrintStream output, String collectionIndent, String specIndent) {
      super(output, collectionIndent, specIndent);
    }

    @Override
    protected String howMuchToIndentSpecsInChildScope() {
      return "  ";
    }
  }

  private static final class RootReporterScope extends ReporterScope {
    public RootReporterScope(PrintStream output, String collectionIndent, String specIndent) {
      super(output, collectionIndent, specIndent);
    }

    @Override
    protected String howMuchToIndentSpecsInChildScope() {
      //The root collection has no output.  Its _children_ do top-level output, without indenting.
      return "";
    }
  }
}
