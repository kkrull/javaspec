package org.jspec;

import java.util.function.Consumer;

import org.jspec.dsl.It;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;

/**
 * Prototypical test classes used when testing JSpec itself; not meant to be run by themselves.
 * Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JSpec. 
 */
public class JSpecTests {
  public static class Empty {}
  
  public static class FaultyConstructor {
    public FaultyConstructor() {
      throw new IllegalArgumentException("just because");
    }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class HiddenConstructor {
    private HiddenConstructor() {}
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  @Ignore
  public static class IgnoredClass {
    It gets_ignored = () -> assertEquals(1, 2);
  }
  
  public static class MultiplePublicConstructors {
    final int id;
    
    public MultiplePublicConstructors() {
      this(42);
    }
    
    public MultiplePublicConstructors(int id) {
      this.id = id;
    }
    
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class One {
    public static Consumer<String> notifyEvent;
    It only_test = () -> notifyEvent.accept("JSpecTests.One::only_test");
  }
  
  public static class PublicConstructorWithArgs {
    private PublicConstructorWithArgs(int id) {}
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class Two {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
}