package info.javaspec.jupiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

//Explores how to use the base Jupiter API to describe a test
@DisplayName("Library")
class JupiterDisplayNameTest {
  @DisplayName("returns true")
  @Test void testSomeLibraryMethod() {
    Library classUnderTest = new Library();
    assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'");
  }
}
