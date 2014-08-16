package org.javaspec.proto;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.javaspec.dsl.Because;
import org.javaspec.dsl.Cleanup;
import org.javaspec.dsl.Establish;
import org.javaspec.dsl.It;
import org.junit.Ignore;

/** Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JSpec. */
public class JSpecExamples {
  public static class ConstructorHidden {
    private ConstructorHidden() {}
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class ConstructorWithArguments {
    public ConstructorWithArguments(int _id) { }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class Empty {}

  public static class FailingBecause {
    Because flawed_action = () -> { throw new UnsupportedOperationException("flawed_action"); };
    It will_never_run = () -> assertEquals(42, 42);
  }
  
  public static class FailingCleanup {
    Cleanup flawed_cleanup = () -> { throw new IllegalStateException("flawed_cleanup"); };
    It may_run = () -> assertEquals(42, 42);
  }
  
  public static class FailingClassInitializer {
    static { assertEquals(1, 2); }
    It will_fail = () -> assertEquals(1, 1);
  }
  
  public static class FailingConstructor {
    public FailingConstructor() throws HardToFindThrowable {
      throw new HardToFindThrowable();
    }
    
    It will_fail = () -> assertEquals(1, 1);
    
    public static class HardToFindThrowable extends Throwable { 
      private static final long serialVersionUID = 1L; 
    }
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
    
    It it = () -> notifyEvent.accept("JSpecExamples.FailingEstablishWithCleanup::it");
    Cleanup cleanup = () -> notifyEvent.accept("JSpecExamples.FailingEstablishWithCleanup::cleanup");
  }

  public static class FailingIt {
    It fails = () -> assertEquals("the answer", 42);
  }
  
  public static class FullFixture extends ExecutionSpy {
    public FullFixture() { notifyEvent.accept("JSpecExamples.FullFixture::new"); }
    Establish arranges = () -> notifyEvent.accept("JSpecExamples.FullFixture::arrange");
    Because acts = () -> notifyEvent.accept("JSpecExamples.FullFixture::act");
    It asserts = () -> notifyEvent.accept("JSpecExamples.FullFixture::assert");
    Cleanup cleans = () -> notifyEvent.accept("JSpecExamples.FullFixture::cleans");
  }
  
  @Ignore
  public static class IgnoreClass {
    It gets_ignored = () -> assertEquals(1, 2);
  }
  
  public static class OneIt extends ExecutionSpy {
    public OneIt() { notifyEvent.accept("JSpecExamples.OneIt::new"); }
    It only_test = () -> notifyEvent.accept("JSpecExamples.OneIt::only_test");
  }
  
  public static class TwoBecause {
    private final List<String> orderMatters = new LinkedList<String>();
    Because act_part_one = () -> orderMatters.add("do this first");
    Because act_part_two_or_is_this_part_one = () -> orderMatters.add("do this second");
    It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
  }
  
  public static class TwoCleanup {
    private final List<String> orderMatters = new LinkedList<String>();
    Cleanup cleanup_part_one = () -> orderMatters.add("do this first");
    Cleanup cleanup_part_two_or_is_this_part_one = () -> orderMatters.add("do this second");
    It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
  }
  
  public static class TwoConstructors {
    public TwoConstructors() {
      this(42);
    }
    
    public TwoConstructors(int _id) { }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class TwoEstablish {
    private final List<String> orderMatters = new LinkedList<String>();
    Establish setup_part_one = () -> orderMatters.add("do this first");
    Establish setup_part_two_not_allowed = () -> orderMatters.add("do this second");
    It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
  }
  
  public static class TwoIt {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
  
  public static class TwoItWithEstablish {
    private String subject;
    Establish that = () -> subject = "established";
    It does_one_thing = () -> assertThat(subject, notNullValue());
    It does_something_else = () -> assertThat(subject, equalTo("established"));
  }
}