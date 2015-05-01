package info.javaspec.testutil;

import junit.framework.AssertionFailedError;
import org.hamcrest.Matcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class Assertions {
  public static <T> void assertListEquals(List<T> expecteds, List<T> actuals) {
    assertThat(actuals, notNullValue());
    assertThat(String.format("Expected <%s>, but was <%s>", expecteds, actuals),
      actuals.size(), equalTo(expecteds.size()));
    for(int i = 0; i < expecteds.size(); i++) {
      T e = expecteds.get(i);
      T a = actuals.get(i);
      assertThat(String.format("Expected <%s> at index %d in <%s> to equal <%s> in <%s>", e, i, expecteds, a, actuals),
        a, equalTo(e));
    }
  }

  public static void assertNoThrow(Thunk thunk) {
    try {
      thunk.run();
    } catch(Throwable t) {
      assertThat(t, nullValue());
    }
  }

  public static void assertThrows(Class<? extends Throwable> type, Matcher<? super String> message, Thunk thunk) {
    assertThrows(type, message, null, thunk);
  }

  public static void assertThrows(Class<? extends Throwable> expectedType, Matcher<? super String> expectedMessage,
                                  Class<? extends Throwable> expectedCause, Thunk thunk) {
    try {
      thunk.run();
    } catch(Throwable t) {
      assertThat(t.getClass(), equalTo(expectedType));
      assertThat(t.getMessage(), expectedMessage);
      if(expectedCause != null) {
        assertThat(t.getCause(), notNullValue());
        assertThat(t.getCause().getClass(), equalTo(expectedCause));
      }

      return;
    }

    fail(String.format("Expected %s to be thrown, but no exception was thrown", expectedType));
  }

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