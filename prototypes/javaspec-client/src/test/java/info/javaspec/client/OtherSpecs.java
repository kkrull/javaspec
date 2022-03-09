package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class OtherSpecs implements SpecClass {
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.it("answers the meaning of life", () -> {
			int answer = 42;
			assertEquals(42, answer);
		});
	}

	private static void assertEquals(int expected, int actual) {
		if (actual == expected)
			return;

		throw new AssertionError(String.format("Expected <%s>, but was <%s>", expected, actual));
	}
}
