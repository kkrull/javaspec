package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

//Specs that exercise JavaSpec.
//Run in IntelliJ or with ./gradlew :javaspec-client:test.
//Adding @Testable shows the run icon in IntelliJ, to run this spec by itself.
//Without @Testable, you can still pick "Run Tests" on a package/directory.
@Testable
public class GreeterSpecs implements SpecClass {
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Greeter.class, () -> {
			javaspec.describe("#greet", () -> {
				javaspec.it("greets the world", () -> {
					Greeter subject = new Greeter();
					assertEquals("Hello world!", subject.greet());
				});
			});
		});
	}

	private static void assertEquals(String expected, String actual) {
		if (expected.equals(actual))
			return;

		throw new AssertionError(String.format("Expected <%s>, but was <%s>", expected, actual));
	}
}
