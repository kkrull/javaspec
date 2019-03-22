package info.javaspec.testutil;

import junit.framework.AssertionFailedError;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

public final class Assertions {
  public static <E extends Exception> E capture(Class<E> toCatch, Thunk thunk) {
    Optional<Exception> thrown = thrownException(thunk);
    if(!thrown.isPresent()) {
      String message = String.format("Expected an exception of type %s to be thrown", toCatch.getName());
      throw new AssertionFailedError(message);
    } else if(thrown.get().getClass() != toCatch) {
      String message = String.format(
        "Exception was thrown, but was the wrong type.\n%s\n",
        describeException(thrown.get())
      );
      throw new AssertionFailedError(message);
    } else
      return toCatch.cast(thrown.get());
  }

  private static String describeException(Exception e) {
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream(10240);
    PrintStream outputPrinter = new PrintStream(outputBytes);
    e.printStackTrace(outputPrinter);
    return outputBytes.toString();
  }

  private static Optional<Exception> thrownException(Thunk thunk) {
    try {
      thunk.run();
    } catch(Exception ex) {
      return Optional.of(ex);
    }

    return Optional.empty();
  }

  @FunctionalInterface
  public interface Thunk {
    void run() throws Exception;
  }
}