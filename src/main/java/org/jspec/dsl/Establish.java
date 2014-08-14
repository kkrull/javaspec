package org.jspec.dsl;

/** A thunk that executes the Arrange step of a test.  Runs first. */
@FunctionalInterface
public interface Establish {
  public void run() throws Exception;
}