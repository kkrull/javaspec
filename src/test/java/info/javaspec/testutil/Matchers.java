package info.javaspec.testutil;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

public final class Matchers {
  private Matchers() { /* static class */ }

  public static Matcher<String> matchesRegex(String pattern) {
    return new BaseMatcher<String>() {
      @Override
      public boolean matches(Object item) {
        if(item == null || item.getClass() != String.class)
          return false;

        String actual = (String)item;
        return Pattern.matches(pattern, actual);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("String matching pattern ");
        description.appendValue(pattern);
      }
    };
  }
}
