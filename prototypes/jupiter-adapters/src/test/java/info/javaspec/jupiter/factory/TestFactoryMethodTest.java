package info.javaspec.jupiter.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

//Shows how to use a @TestFactory method to declare a dynamic test
@DisplayName("Test creation: TestFactory method")
class TestFactoryMethodTest {
	{
		JavaSpec.it("greets the world", () -> {
			Greeter subject = new Greeter();
			assertEquals("Hello world!", subject.makeGreeting());
		});
	}

	// Positive: It works, and it's not much code.
	// Negative: This code has to be repeated in every spec file.
	@TestFactory
	DynamicTest makeSingleTest() {
		return JavaSpec.getSpec().toDynamicTest();
	}
}
