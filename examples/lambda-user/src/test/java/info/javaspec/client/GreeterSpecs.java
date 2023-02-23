package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

public class GreeterSpecs implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Greeter.class, () -> {
			javaspec.describe("#makeGreeting", () -> {
				javaspec.it("returns 'Hello World!'", () -> {
					Greeter subject = new Greeter();
					assertEquals("Hello World!", subject.makeGreeting());
				});
			});
		});
	}
}
