package info.javaspec.jupiter.syntax.fixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import info.javaspec.jupiter.Greeter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

@DisplayName("Fixture syntax: Methods on JavaSpec instance")
public class FixtureMethodSpecs {
	@TestFactory
	DynamicNode beforeEachSingle() {
		JavaSpec greeterSpecs = new JavaSpec();
		return greeterSpecs.describe(Greeter.class, () -> {
			// Negative: Can't just set in a beforeEach and access in it, without thinking
			// about what "volatile" means.
			AtomicReference<Greeter> subject = new AtomicReference<>();

			// Positive: beforeEach runs as expected, just before each spec.
			// Unknown: What happens if #beforeEach is called after #it?
			greeterSpecs.beforeEach(() -> {
				subject.set(new Greeter());
			});

			// Positive: It is possible to access fields that are assigned in beforeEach, as
			// long as you use AtomicReference.
			greeterSpecs.it("greets the world", () -> {
				assertEquals("Hello world!", subject.get().makeGreeting());
			});

			greeterSpecs.it("greets a person by name", () -> {
				assertEquals("Hello George!", subject.get().makeGreeting("George"));
			});
		});
	}

	@TestFactory
	DynamicNode beforeEachExample() {
		JavaSpec<List<String>> specs = new JavaSpec<>();
		return specs.describe(List.class, () -> {
			specs.subject(() -> new LinkedList<>());

			specs.context("when the list has 1 or more elements", () -> {
				specs.beforeEach(() -> {
					specs.subject().add("existing");
				});

				specs.it("appends to the tail", () -> {
					List<String> subject = specs.subject();
					subject.add("appended");
					assertEquals(Arrays.asList("existing", "appended"), subject);
				});
			});
		});
	}

	@TestFactory
	DynamicNode beforeEachMultiple() {
		JavaSpec specs = new JavaSpec();
		return specs.describe(List.class, () -> {
			AtomicReference<List<String>> subject = new AtomicReference<>();

			specs.beforeEach(() -> {
				subject.set(new LinkedList<>());
			});

			specs.context("when the list has 1 or more elements", () -> {
				// Positive: Nested beforeEach blocks do work, in a manner similar to Jasmine
				// and RSpec.
				// Negative: Multiple beforeEach blocks can be abused, as with Jasmine and
				// RSpec.
				specs.beforeEach(() -> {
					subject.get().add("existing");
				});

				specs.it("appends to the tail", () -> {
					subject.get().add("appended");
					assertEquals(Arrays.asList("existing", "appended"), subject.get());
				});
			});
		});
	}

	@TestFactory
	DynamicNode afterEachSingle() {
		JavaSpec sloppySpecs = new JavaSpec();
		return sloppySpecs.describe(Greeter.class, () -> {
			// Positive: afterEach works too.
			sloppySpecs.afterEach(() -> {
				TestObject.sharedState = null;
			});

			sloppySpecs.it("sets a static variable once", () -> {
				assertNull(TestObject.sharedState);
				TestObject.sharedState = "One";
				assertEquals("One", TestObject.sharedState);
			});

			sloppySpecs.it("sets a static variable twice", () -> {
				assertNull(TestObject.sharedState);
				TestObject.sharedState = "Other";
				assertEquals("Other", TestObject.sharedState);
			});
		});
	}

	private static final class TestObject {
		public static String sharedState;
	}
}
