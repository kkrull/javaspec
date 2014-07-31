package org.jspec.proto;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import org.jspec.dsl.It;
import org.junit.Ignore;

/** Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JSpec. */
public class JSpecExamples {
  public static class Empty {}
  
  public static class FailingTest {
    It fails = () -> assertEquals("the answer", 42);
  }
  
  public static class FaultyConstructor {
    public FaultyConstructor() throws HardToFindThrowable {
      throw new HardToFindThrowable();
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
    private final int id;
    
    public MultiplePublicConstructors() {
      this(42);
    }
    
    public MultiplePublicConstructors(int id) {
      this.id = id;
    }
    
    It is_otherwise_valid = () -> assertEquals(42, id);
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
  
  public static class OnePassOneFail {
    private static final Consumer<String> NOP = x -> { return; };
    private static Consumer<String> notifyEvent = NOP;
    
    public static void setEventListener(Consumer<String> newConsumer) {
      notifyEvent = newConsumer == null ? NOP : newConsumer;
    }
    
    public OnePassOneFail() {
      notifyEvent.accept("JSpecExamples.OnePassOneFail::new");
    }
    
    It fail = () -> {
      notifyEvent.accept("JSpecExamples.OnePassOneFail::fail");
      assertEquals("apples", "oranges");
    };
    
    It pass = () -> notifyEvent.accept("JSpecExamples.OnePassOneFail::pass");
  }
  
  public static class PublicConstructorWithArgs {
    public PublicConstructorWithArgs(int id) {}
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class Two {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
  
  @SuppressWarnings("serial")
  public static class HardToFindThrowable extends Throwable {}
}