package org.jspec.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import java.util.List;

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

  public static void assertThrows(Class<? extends Exception> exceptionClass, Thunk thunk) {
    try {
      thunk.run();
    } catch (Exception e) {
      assertEquals("Unexpected type of exception thrown", exceptionClass, e.getClass());
      return;
    }

    fail(String.format("Expected %s to be thrown, but no exception was thrown", exceptionClass));
  }

  @FunctionalInterface
  public interface Thunk {
    void run();
  }
}
