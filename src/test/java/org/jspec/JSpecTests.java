package org.jspec;

public class JSpecTests {
  It runs = () -> { throw new TestRanException(); };

  @SuppressWarnings("serial") // Nothing to version; only used in 1 place
  public final class TestRanException extends RuntimeException {}

  @FunctionalInterface
  interface It {
    public void run() throws Exception;
  }
}