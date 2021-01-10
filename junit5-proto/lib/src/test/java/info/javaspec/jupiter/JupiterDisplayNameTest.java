package info.javaspec.jupiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

//Shows how to use the base Jupiter API to describe a test.
@DisplayName("Library")
class JupiterDisplayNameTest {
  //Positive: Describe a test in plain language.
  //Negative: It takes extra effort, space, and duplication to say what you really meant in the name of the test method.
  @DisplayName("returns true")
  @Test void testSomeLibraryMethod() {
    Library classUnderTest = new Library();
    assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'");
  }
}
