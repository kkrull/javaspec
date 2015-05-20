package info.javaspecproto;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/** Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JavaSpec. */
public class ContextClasses {
  public static class ConstructorWithArguments {
    public ConstructorWithArguments(int _id) { }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }
  
  public static class Empty {}
  
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
      notifyEvent.accept("ContextClasses.FailingEstablishWithCleanup::establish");
      throw new UnsupportedOperationException("flawed_setup"); 
    };
    
    It it = () -> notifyEvent.accept("ContextClasses.FailingEstablishWithCleanup::it");
    Cleanup cleanup = () -> notifyEvent.accept("ContextClasses.FailingEstablishWithCleanup::cleanup");
  }

  public static class FailingIt {
    It fails = () -> assertEquals("the answer", 42);
  }
  
  public static class FullFixture extends ExecutionSpy {
    public FullFixture() { notifyEvent.accept("ContextClasses.FullFixture::new"); }
    Establish arranges = () -> notifyEvent.accept("ContextClasses.FullFixture::arrange");
    Because acts = () -> notifyEvent.accept("ContextClasses.FullFixture::act");
    It asserts = () -> notifyEvent.accept("ContextClasses.FullFixture::assert");
    Cleanup cleans = () -> notifyEvent.accept("ContextClasses.FullFixture::cleans");
  }

  public static class NestedExamples {
    It top_level_test = () -> assertEquals(1, 1);
    
    public class middleWithNoTests {
      public class bottom {
        It bottom_test = () -> assertEquals(1, 1);
      }
    }
    
    public class middleWithTest {
      It middle_test = () -> assertEquals(1, 1);
      
      public class bottom {
        It another_bottom_test = () -> assertEquals(1, 1);
      }
    }
  }
  
  public static class NestedEstablish {
    Establish outer_arrange = () -> assertEquals(1, 1);
    
    public class inner {
      Establish inner_arrange = () -> assertEquals(1, 1);
      It asserts = () -> assertEquals(42, 42);
    }
  }
  
  public static class NestedBecause {
    Because outer_act = () -> assertEquals(1, 1);
    
    public class inner {
      Because inner_act = () -> assertEquals(1, 1);
      It asserts = () -> assertEquals(42, 42);
    }
  }

  public static class NestedEstablishBecause {
    Establish outer_arrange = () -> assertEquals(1, 1);
    Because outer_act = () -> assertEquals(1, 1);
    
    public class inner {
      Establish inner_arrange = () -> assertEquals(1, 1);
      Because inner_act = () -> assertEquals(1, 1);
      It asserts = () -> assertEquals(42, 42);
    }
  }
  
  public static class NestedCleanup {
    Cleanup outer_cleanup = () -> assertEquals(1, 1);
    
    public class inner {
      Cleanup inner_cleanup = () -> assertEquals(1, 1);
      It asserts = () -> assertEquals(42, 42);
    }
  }

  public static class NestedContext {
    public class inner {
      It asserts = () -> assertEquals(1, 1);
    }
  }

  public static class NestedFixture {
    Establish above_target_context = () -> assertEquals(1, 1);
    
    public class targetContext {
      It asserts_in_target_context = () -> assertEquals(1, 1);
      
      public class moreSpecificContext {
        Establish below_target_context = () -> assertEquals(1, 1);
        It asserts_in_more_specific_context = () -> assertEquals(1, 1);
      }
    }
  }
  
  public static class NestedFullFixture extends ExecutionSpy {
    public NestedFullFixture() { notifyEvent.accept("ContextClasses.NestedFullFixture::new"); }
    Establish arranges = () -> notifyEvent.accept("ContextClasses.NestedFullFixture::arrange");
    Cleanup cleans = () -> notifyEvent.accept("ContextClasses.NestedFullFixture::cleans");
    
    public class innerContext {
      public innerContext() { notifyEvent.accept("ContextClasses.NestedFullFixture.innerContext::new"); }
      Because acts = () -> notifyEvent.accept("ContextClasses.NestedFullFixture.innerContext::act");
      It asserts = () -> notifyEvent.accept("ContextClasses.NestedFullFixture.innerContext::assert");
    }
  }

  public static class NestedThreeDeep {
    public class middle {
      public class bottom {
        It asserts = () -> assertEquals(1, 1);
      }
    }
  }
  
  public static class NestedContextAndInnerHelperClass {
    public class context { 
      It asserts = () -> assertEquals(1, 1);
      public class HelperNotAContext { /* empty */ }
    }
    
    public class emptyContextThatShouldBeExcluded {
      public class InnerHelper { /* empty */ }
    }
  }
  
  public static class NestedContextAndStaticHelperClass {
    public class context { 
      It asserts = () -> assertEquals(1, 1);
    }
    
    public static class Helper {
      public void dryMyTest() { /* empty */ }
      It is_not_a_test = () -> assertEquals(1, 1);
    }
  }
  
  public static class StaticClassIt {
    public static class Helper {
      It is_not_a_test = () -> assertEquals(1, 1);
    }
  }

  public static class OneIt extends ExecutionSpy {
    public OneIt() { notifyEvent.accept("ContextClasses.OneIt::new"); }
    It only_test = () -> notifyEvent.accept("ContextClasses.OneIt::only_test");
  }
  
  public static class PendingBecause {
    @SuppressWarnings("unused") private Object subject;
    private int hashcode;
    
    Establish arranges = () -> subject = new Object();
    Because acts;
    It asserts = () -> assertThat(hashcode, equalTo(42));
  }
  
  public static class PendingCleanup {
    private ByteArrayOutputStream subject;
    Establish arranges = () -> subject = new ByteArrayOutputStream(4);
    Because acts = () -> subject.write(42);
    It asserts = () -> assertThat(subject.size(), equalTo(4));
    Cleanup cleans;
  }
  
  public static class PendingEstablish {
    private Object subject;
    private int hashcode;
    
    Establish arranges;
    Because acts = () -> hashcode = subject.hashCode();
    It asserts = () -> assertThat(hashcode, equalTo(42));
  }
  
  public static class PendingIt {
    private Object subject;
    @SuppressWarnings("unused") private int hashcode;
    
    Establish arranges = () -> subject = new Object();
    Because acts = () -> hashcode = subject.hashCode();
    It asserts;
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
    public class innerContext {
      private final List<String> orderMatters = new LinkedList<String>();
      Establish setup_part_one = () -> orderMatters.add("do this first");
      Establish setup_part_two_not_allowed = () -> orderMatters.add("do this second");
      It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
    }
  }
  
  public static class TwoIt extends ExecutionSpy {
    It first_test = () -> notifyEvent.accept("TwoIt::first_test");
    It second_test = () -> notifyEvent.accept("TwoIt::second_test");
  }
  
  public static class TwoItWithEstablish {
    private String subject;
    Establish that = () -> subject = "established";
    It does_one_thing = () -> assertThat(subject, notNullValue());
    It does_something_else = () -> assertThat(subject, equalTo("established"));
  }
}