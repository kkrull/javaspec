package info.javaspec.testutil;

import junit.framework.AssertionFailedError;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

public final class Assertions {
  public static <E extends Exception> E capture(Class<E> toCatch, Thunk thunk) {
    Optional<Exception> thrown = thrownException(thunk);
    if(!thrown.isPresent())
      throw new AssertionFailedError(String.format("Expected an exception of type %s to be thrown", toCatch.getName()));
    else if(thrown.get().getClass() != toCatch)
      throw new AssertionFailedError(String.format("Exception was thrown, but was the wrong type.\n%s\n", describeException(thrown.get())));
    else
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