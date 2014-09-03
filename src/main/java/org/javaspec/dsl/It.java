package org.javaspec.dsl;

/** The Assert part of running a test.  Include one or more of these in each test class. */
@FunctionalInterface
public interface It {
  public void run() throws Exception;
}