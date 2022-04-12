package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

public class LibraryTest implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Library.class, () -> {
			javaspec.describe("#someLibraryMethod", () -> {
				javaspec.it("returns true", () -> {
					Library subject = new Library();
					assertTrue(subject.someLibraryMethod());
				});
			});
		});
	}
}
