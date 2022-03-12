package info.javaspec.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

//Anonymous classes that only exist after methods are invoked, to avoid running side by side with project tests.
public class SpecClasses {
	private SpecClasses() {
		/* Static class */
	}

	public static Class<?> notASpecClass() {
		return new Object() {}.getClass();
	}

	public static Class<? extends SpecClass> nullSpecClass() {
		return nullSpecClassInstance().getClass();
	}

	private static SpecClass nullSpecClassInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				//Do nothing
			}
		};
	}

	public static Class<? extends SpecClass> oneSpecClass() {
		return oneSpecClassInstance().getClass();
	}

	private static SpecClass oneSpecClassInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.it("one spec", () -> {
					assertEquals(2, 1 + 1);
				});
			}
		};
	}

	public static Class<? extends SpecClass> oneSpecThrowingAssertionError() {
		return oneSpecThrowingAssertionErrorInstance().getClass();
	}

	private static SpecClass oneSpecThrowingAssertionErrorInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.it("throws", () -> {
					assertEquals(42, 41);
				});
			}
		};
	}

	public static Class<? extends SpecClass> oneSpecThrowingRuntimeException() {
		return oneSpecThrowingRuntimeExceptionInstance().getClass();
	}

	private static SpecClass oneSpecThrowingRuntimeExceptionInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.it("throws", () -> {
					throw new RuntimeException("bang!");
				});
			}
		};
	}
}
