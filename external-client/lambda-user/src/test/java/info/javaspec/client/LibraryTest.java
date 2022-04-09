package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LibraryTest {
	@Test
	void someLibraryMethodReturnsTrue() {
		Library subject = new Library();
		assertTrue(subject.someLibraryMethod(), "someLibraryMethod should return 'true'");
	}
}
