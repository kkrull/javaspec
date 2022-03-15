package info.javaspec.jupiter.syntax.componentparameters;

import info.javaspec.jupiter.Greeter;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.componentparameters.JavaSpec.describe;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;

//Shows how to pass `it` to describe lambdas, as a parameter.
@DisplayName("Declaration syntax: Pass distinct declaration objects to lambdas")
public class PassedComponentSyntax {
	@TestFactory
	DynamicNode generateTests() {
		return describe("Greeter", (it) -> {
			// Negative: The parameter is always an object, never a first-class function.
			it.declare("greets the world", () -> {
				// Positive: `it` is always scoped to the `describe` that provides it.
				Greeter subject = new Greeter();
				assertEquals("Hello world!", subject.makeGreeting());
			});
		});
	}
}
