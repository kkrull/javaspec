package info.javaspec.runner;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.runner.Description;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class Descriptions {
  private Descriptions() { /* static class */ }

  public static Set<String> childClassNames(Description description) {
    return description.getChildren().stream().map(Description::getClassName).collect(toSet());
  }

  public static Set<String> childMethodNames(Description description) {
    return description.getChildren().stream().map(Description::getMethodName).collect(toSet());
  }

  public static Matcher<? super Description> isTestDescription() {
    return new BaseMatcher<Description>() {
      @Override
      public boolean matches(Object item) {
        if(item.getClass() != Description.class)
          return false;

        Description description = Description.class.cast(item);
        return !description.isSuite() && description.isTest();
      }

      @Override
      public void describeTo(org.hamcrest.Description description) {
        description.appendText("a test description");
      }
    };
  }

  public static Matcher<? super Description> isSuiteDescription() {
    return new BaseMatcher<Description>() {
      @Override
      public boolean matches(Object item) {
        if(item.getClass() != Description.class)
          return false;

        Description description = Description.class.cast(item);
        return description.isSuite() && !description.isTest();
      }

      @Override
      public void describeTo(org.hamcrest.Description description) {
        description.appendText("a suite description");
      }
    };
  }
}
