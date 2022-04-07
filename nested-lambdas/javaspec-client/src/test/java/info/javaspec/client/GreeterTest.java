package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GreeterTest {
	@Nested
	@DisplayName("#greet")
	class greet {
		@Test
		@DisplayName("greets the world, given no name")
		void givenNoNameGreetsTheWorld() {
			Greeter subject = new Greeter();
			assertEquals("Hello world!", subject.greet());
		}

		@Test
		@DisplayName("greets a person by name, given a name")
		void givenANameGreetsThePersonByName() {
			Greeter subject = new Greeter();
			assertEquals("Hello Adventurer!", subject.greet("Adventurer"));
		}
	}
}
