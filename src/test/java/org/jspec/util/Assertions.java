package org.jspec.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Assertions {
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
  interface Thunk {
    void run();
  }
}
