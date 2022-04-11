package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.jupiter.api.Test;

class LibraryTest implements SpecClass {
	@Test
	void someLibraryMethodReturnsTrue() {
		Library subject = new Library();
		assertTrue(subject.someLibraryMethod(), "someLibraryMethod should return 'true'");
	}

	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Library.class, () -> {
			javaspec.describe("#someLibraryMethod", () -> {
				javaspec.it("returns true", () -> {
					Library subject = new Library();
					assertFalse(subject.someLibraryMethod());
				});
			});
		});
	}
}
