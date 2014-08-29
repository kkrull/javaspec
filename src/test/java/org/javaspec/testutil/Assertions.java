package org.javaspec.testutil;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.hamcrest.Matcher;

public final class Assertions {
  public static <T> void assertListEquals(List<T> expecteds, List<T> actuals) {
    assertThat(actuals, notNullValue());
    assertThat(String.format("Expected <%s>, but was <%s>", expecteds, actuals),
      actuals.size(), equalTo(expecteds.size()));
    for (int i = 0; i < expecteds.size(); i++) {
      T e = expecteds.get(i);
      T a = actuals.get(i);
      assertThat(String.format("Expected <%s> at index %d in <%s> to equal <%s> in <%s>", e, i, expecteds, a, actuals),
        a, equalTo(e));
    }
  }

  public static void assertThrows(Class<? extends Throwable> type, Matcher<? super String> message, Thunk thunk) {
    assertThrows(type, message, null, thunk);
  }

  public static void assertThrows(Class<? extends Throwable> expectedType, Matcher<? super String> expectedMessage, 
    Class<? extends Throwable> expectedCause, Thunk thunk) {
    try {
      thunk.run();
    } catch (Throwable t) {
      assertThat(t.getClass(), equalTo(expectedType));
      assertThat(t.getMessage(), expectedMessage);
      if (expectedCause != null) {
        assertThat(t.getCause(), notNullValue());
        assertThat(t.getCause().getClass(), equalTo(expectedCause));
      }
      
      return;
    }

    fail(String.format("Expected %s to be thrown, but no exception was thrown", expectedType));
  }
  
  @FunctionalInterface
  public interface Thunk {
    void run() throws Exception;
  }
}