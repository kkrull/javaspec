package org.jspec.proto;

import static org.junit.Assert.assertEquals;

import org.jspec.dsl.Because;
import org.jspec.dsl.Cleanup;
import org.jspec.dsl.Establish;
import org.jspec.dsl.It;
import org.junit.Ignore;

/** Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JSpec. */
public class JSpecExamples {
  public static class BecauseTwice {
    private int numActions;
    Because act_part_one = () -> numActions++;
    Because act_part_two_or_is_this_part_one = () -> numActions++;
    It runs = () -> assertEquals(2, numActions);
  }
  
  public static class CleanupTwice {
    private int numActions;
    Cleanup cleanup_part_one = () -> numActions++;
    Cleanup cleanup_part_two_or_is_this_part_one = () -> numActions++;
    It runs = () -> assertEquals(2, numActions);
  }
  
  public static class ConstructorHasArguments {
    public ConstructorHasArguments(int _id) { }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class Empty {}
  
  public static class EstablishOnceRunTwice {
    private String subject;
    Establish that = () -> subject = "established";
    It does_one_thing = () -> assertEquals("established", subject);
    It does_something_else = () -> assertEquals("established", subject);
  }
  
  public static class EstablishTwice {
    private int numTimesEstablished;
    Establish setup_part_one = () -> numTimesEstablished++;
    Establish setup_part_two_not_allowed = () -> numTimesEstablished++;
    It runs = () -> assertEquals(2, numTimesEstablished);
  }
  
  public static class FailingBecause {
    Because flawed_action = () -> { throw new UnsupportedOperationException("flawed_action"); };
    It will_never_run = () -> assertEquals(42, 42);
  }
  
  public static class FailingCleanup {
    Cleanup flawed_cleanup = () -> { throw new IllegalStateException("flawed_cleanup"); };
    It may_run = () -> assertEquals(42, 42);
  }
  
  public static class FailingEstablish {
    Establish flawed_setup = () -> { throw new UnsupportedOperationException("flawed_setup"); };
    It will_never_run = () -> assertEquals(42, 42);
  }

  public static class FailingEstablishWithCleanup extends ExecutionSpy {
    Establish establish = () -> {
      notifyEvent.accept("JSpecExamples.FailingEstablishWithCleanup::establish");
      throw new UnsupportedOperationException("flawed_setup"); 
    };
    
    It it = () -> assertEquals(42, 42);
    Cleanup cleanup = () -> notifyEvent.accept("JSpecExamples.FailingEstablishWithCleanup::cleanup");
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
  
  public static class FullFixture extends ExecutionSpy {
    public FullFixture() { notifyEvent.accept("JSpecExamples.FullFixture::new"); }
    Establish arranges = () -> notifyEvent.accept("JSpecExamples.FullFixture::arrange");
    Because acts = () -> notifyEvent.accept("JSpecExamples.FullFixture::act");
    It asserts = () -> notifyEvent.accept("JSpecExamples.FullFixture::assert");
    Cleanup cleans = () -> notifyEvent.accept("JSpecExamples.FullFixture::cleans");
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
  
  public static class One extends ExecutionSpy {
    public One() { notifyEvent.accept("JSpecExamples.One::new"); }
    It only_test = () -> notifyEvent.accept("JSpecExamples.One::only_test");
  }
  
  public static class Two {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
  
  @SuppressWarnings("serial")
  public static class HardToFindThrowable extends Throwable {}
}