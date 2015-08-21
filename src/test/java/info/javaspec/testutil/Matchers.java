package info.javaspec.testutil;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

public final class Matchers {
  private Matchers() { /* static class */ }

  public static Matcher<Throwable> isThrowableMatching(Class<? extends Throwable> expectedClass, String messageRegex) {
    return new BaseMatcher<Throwable>() {
      public boolean matches(Object item) {
        if(item == null || item.getClass() != expectedClass)
          return false;

        Throwable actual = expectedClass.cast(item);
        return Pattern.matches(messageRegex, actual.getMessage());
      }

      public void describeTo(Description description) {
        description.appendText("Exception of type ");
        description.appendValue(expectedClass);
        description.appendText(" with message matching regex ");
        description.appendValue(messageRegex);
      }
    };
  }

  public static Matcher<String> matchesRegex(String pattern) {
    return new BaseMatcher<String>() {
      public boolean matches(Object item) {
        if(item == null || item.getClass() != String.class)
          return false;

        String actual = (String)item;
        return Pattern.matches(pattern, actual);
      }

      public void describeTo(Description description) {
        description.appendText("String matching pattern ");
        description.appendValue(pattern);
      }
    };
  }
}
