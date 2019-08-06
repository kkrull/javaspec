package info.javaspec.console;

import info.javaspec.console.plaintext.PlainTextReporter;

import java.io.PrintStream;

public class StaticReporterFactory implements ReporterFactory {
  private final PrintStream output;
  private PlainTextReporter reporter;

  public StaticReporterFactory(PrintStream output) {
    this.output = output;
  }

  @Override
  public Reporter plainTextReporter() {
    if(this.reporter == null)
      this.reporter = new PlainTextReporter(this.output);

    return this.reporter;
  }
}
