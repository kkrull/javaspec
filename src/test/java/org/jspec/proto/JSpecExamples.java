package org.jspec.proto;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import org.jspec.dsl.*;
import org.junit.Ignore;

/** Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JSpec. */
public class JSpecExamples {
  public static class ConstructorHasArguments {
    public ConstructorHasArguments(int _id) { }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class Empty {}
  
  public static class EstablishTest {
    private String subject;
    Establish that = () -> subject = "established";
    It runs = () -> assertEquals("established", subject);
  }
  
  public static class FailingTest {
    It fails = () -> assertEquals("the answer", 42);
  }
  
  public static class FaultyConstructor {
    public FaultyConstructor() throws HardToFindThrowable {
      throw new HardToFindThrowable();
    }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class FaultyClassInitializer {
    static { assertEquals(1, 2); }
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
    @SuppressWarnings("unused")
    private final int _id; //Only used as a means of getting two constructors
    
    public MultiplePublicConstructors() {
      this(42);
    }
    
    public MultiplePublicConstructors(int _id) {
      this._id = _id;
    }
    
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class One {
    private static final Consumer<String> NOP = x -> { return; };
    private static Consumer<String> notifyEvent = NOP;
    
    public static void setEventListener(Consumer<String> newConsumer) {
      notifyEvent = newConsumer == null ? NOP : newConsumer;
    }
    
    public One() {
      notifyEvent.accept("JSpecExamples.One::new");
    }
    It only_test = () -> notifyEvent.accept("JSpecExamples.One::only_test");
  }
  
  public static class Two {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
  
  public static class WrongTypeOfBehaviorField {
    Integer notAnItField;
  }
  
  @SuppressWarnings("serial")
  public static class HardToFindThrowable extends Throwable {}
}