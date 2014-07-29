package org.jspec;

import org.jspec.dsl.It;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;

/**
 * Prototypical test classes used when testing JSpec itself; not meant to be run by themselves.
 * Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JSpec. 
 */
class JSpecTests {
  static class Empty {}

  static class One {
    It only_test = () -> assertEquals(1, 1);
  }

  static class Two {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
  
  @Ignore
  static class IgnoredClass {
    It gets_ignored = () -> assertEquals(1, 2);
  }
  
  static class PrivateConstructor {
    private PrivateConstructor() {}
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
}