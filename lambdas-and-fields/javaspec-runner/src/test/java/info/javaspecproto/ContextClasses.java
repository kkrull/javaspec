package info.javaspecproto;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import org.hamcrest.MatcherAssert;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/** Inner classes are declared static to avoid the gaze of HierarchicalContextRunner when testing JavaSpec. */
//@SuppressWarnings("unused")
public class ContextClasses {
  public static Class<?> hiddenClass() {
    Class<?> outer;
    try {
      outer = Class.forName("info.javaspecproto.HiddenContext");
      return outer.getDeclaredClasses()[0];
    } catch(ClassNotFoundException e) {
      throw new AssertionError("Failed to set up test", e);
    }
  }

  public static class ConstructorWithArguments {
    public ConstructorWithArguments(int _id) { }
    It is_otherwise_valid = () -> assertEquals(1, 1);
  }

  public static final class ConstructorWithSideEffects {
    private static int _numTimesInitialized = 0;
    public ConstructorWithSideEffects() { _numTimesInitialized++; }
    It expects_to_be_run_once = () -> MatcherAssert.assertThat(_numTimesInitialized, equalTo(1));
  }

  public static class DuplicateContextNames {
    public class Left {
      public class Duplicate {
        It runs_one = () -> assertEquals(1, 1);
      }
    }

    public class Right {
      public class Duplicate {
        It runs_another = () -> assertEquals(2, 2);
      }
    }
  }

  public static class DuplicateSpecNames {
    public class OneSetOfConditions {
      public class DuplicateContext {
        It duplicate_behavior = () -> assertEquals(1, 1);
      }
    }

    public class AnotherSetOfConditions {
      public class DuplicateContext {
        It duplicate_behavior = () -> assertEquals(2, 2);
      }
    }
  }

  public static class Empty { }

  public static class FailingCleanup {
    Cleanup flawed_cleanup = () -> assertEquals(42, -1);
    It may_run = () -> assertEquals(42, 42);
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
    Establish flawed_setup = () -> assertEquals(42, -1);
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
    It fails = () -> assertEquals(42, -1);
  }

  public static class FullFixture extends ExecutionSpy {
    public FullFixture() { notifyEvent.accept("ContextClasses.FullFixture::new"); }
    Establish arranges = () -> notifyEvent.accept("ContextClasses.FullFixture::arrange");
    Because acts = () -> notifyEvent.accept("ContextClasses.FullFixture::act");
    It asserts = () -> notifyEvent.accept("ContextClasses.FullFixture::assert");
    Cleanup cleans = () -> notifyEvent.accept("ContextClasses.FullFixture::cleans");
  }

  public static class NestedCleanup extends ExecutionSpy {
    public NestedCleanup() { notifyEvent.accept("ContextClasses.NestedCleanup::new"); }
    Cleanup cleans = () -> notifyEvent.accept("ContextClasses.NestedCleanup::cleans");

    public class innerContext {
      public innerContext() { notifyEvent.accept("ContextClasses.NestedCleanup.innerContext::new"); }
      Cleanup cleans = () -> notifyEvent.accept("ContextClasses.NestedCleanup::innerContext::cleans");
      It asserts = () -> notifyEvent.accept("ContextClasses.NestedCleanup.innerContext::asserts");
    }
  }

  public static class NestedContexts {
    public class one {
      It asserts_one = () -> assertEquals(1, 1);
    }
    public class two {
      It asserts_two = () -> assertEquals(2, 2);
    }
  }

  public static class NestedEstablish extends ExecutionSpy {
    public NestedEstablish() { notifyEvent.accept("ContextClasses.NestedEstablish::new"); }
    Establish arranges = () -> notifyEvent.accept("ContextClasses.NestedEstablish::arranges");

    public class innerContext {
      public innerContext() { notifyEvent.accept("ContextClasses.NestedEstablish.innerContext::new"); }
      Establish arranges = () -> notifyEvent.accept("ContextClasses.NestedEstablish::innerContext::arranges");
      It asserts = () -> notifyEvent.accept("ContextClasses.NestedEstablish.innerContext::asserts");
    }
  }

  public static class NestedIt {
    public class nestedContext {
      It tests_something_more_specific = () -> assertThat(1, equalTo(1));
    }
  }

  public static class NestedStaticClassIt {
    public static class Helper {
      It is_not_a_test = () -> assertEquals(1, 1);
    }
  }

  public static class OneIt extends ExecutionSpy {
    public OneIt() { notifyEvent.accept("ContextClasses.OneIt::new"); }
    It only_test = () -> notifyEvent.accept("ContextClasses.OneIt::only_test");
  }

  public static class PendingBecause {
    private Object subject;
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
    private int hashcode;

    Establish arranges = () -> subject = new Object();
    Because acts = () -> hashcode = subject.hashCode();
    It asserts;
  }

  public static class TwoEstablish {
    private final List<String> orderMatters = new LinkedList<>();
    Establish arrange_part_one = () -> orderMatters.add("do this first");
    Establish arrange_part_two_or_is_this_part_one = () -> orderMatters.add("do this second");
    It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
  }

  public static class TwoBecause {
    private final List<String> orderMatters = new LinkedList<>();
    Because act_part_one = () -> orderMatters.add("do this first");
    Because act_part_two_or_is_this_part_one = () -> orderMatters.add("do this second");
    It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
  }

  public static class TwoCleanup {
    private final List<String> orderMatters = new LinkedList<>();
    Cleanup cleanup_part_one = () -> orderMatters.add("do this first");
    Cleanup cleanup_part_two_or_is_this_part_one = () -> orderMatters.add("do this second");
    It runs = () -> assertThat(orderMatters, contains("do this first", "do this second"));
  }

  public static class TwoContexts {
    public class subcontext1 {
      It asserts = () -> assertEquals(1, 1);
    }

    public class subcontext2 {
      It asserts = () -> assertEquals(2, 2);
    }
  }

  public static class TwoIt extends ExecutionSpy {
    It first_test = () -> notifyEvent.accept("TwoIt::first_test");
    It second_test = () -> notifyEvent.accept("TwoIt::second_test");
  }

  public static class TwoIts {
    It one = () -> assertEquals(1, 1);
    It two = () -> assertEquals(2, 2);
  }

  public static class StaticIt {
    static It looks_like_an_isolated_test_but_beware = () -> assertThat("this test", not("independent"));
  }

  public static class UnderscoreIt {
    It read_me = () -> assertEquals(1, 1);
  }

  public static class UnderscoreSubContext {
    public class read_me {
      It asserts = () -> assertEquals(1, 1);
    }
  }

  public static class VaryingResults {
    It passes = () -> {};
    It fails = () -> assertThat(2, equalTo(1));
    It ignores;
  }

  public static class WrongTypeField {
    public Object inaccessibleAsIt = new Object();
  }
}