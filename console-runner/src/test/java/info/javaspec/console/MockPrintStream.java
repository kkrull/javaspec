package info.javaspec.console;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

final class MockPrintStream extends PrintStream {
  private final ByteArrayOutputStream printedBytes;

  public static MockPrintStream create() {
    return new MockPrintStream(new ByteArrayOutputStream());
  }

  public MockPrintStream(ByteArrayOutputStream printedBytes) {
    super(printedBytes);
    this.printedBytes = printedBytes;
  }

  public void outputShouldBe(Matcher<String> matcher) {
    assertThat(this.printedBytes.toString(), matcher);
  }

  public void shouldHavePrintedLine(Matcher<String> matcher) {
    assertThat(printedLines(), hasItem(matcher));
  }

  @SafeVarargs
  public final void shouldHavePrintedTheseLines(Matcher<String>... lineMatchers) {
    assertThat(printedLines(), Matchers.contains(lineMatchers));
  }

  private List<String> printedLines() {
    String concatenatedOutput = this.printedBytes.toString();
    String[] lines = concatenatedOutput.split(System.lineSeparator());
    return Arrays.asList(lines);
  }
}
