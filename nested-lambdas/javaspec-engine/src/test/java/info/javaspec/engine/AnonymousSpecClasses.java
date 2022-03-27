package info.javaspec.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

//Anonymous classes that only exist after methods are invoked, to avoid running side by side with project tests.
public class AnonymousSpecClasses {
	private AnonymousSpecClasses() { /* Static class */ }

	public static Class<? extends SpecClass> describeThenSpec() {
		return describeThenSpecInstance().getClass();
	}

	private static SpecClass describeThenSpecInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.describe("something", () -> {});
				javaspec.pending("spec");
			}
		};
	}

	public static Class<? extends SpecClass> describeWithOneSpec() {
		return describeWithOneSpecInstance().getClass();
	}

	private static SpecClass describeWithOneSpecInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.describe("something", () -> {
					javaspec.pending("works");
				});
			}
		};
	}

	public static Class<? extends SpecClass> emptyDescribe() {
		return emptyDescribeInstance().getClass();
	}

	private static SpecClass emptyDescribeInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.describe("something", () -> {});
			}
		};
	}

	public static Class<? extends SpecClass> emptyDescribeAClass() {
		return emptyDescribeAClassInstance().getClass();
	}

	private static SpecClass emptyDescribeAClassInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.describe(TheOracle.class, () -> {});
			}
		};
	}

	private static final class TheOracle { /* empty */ }

	public static Class<? extends SpecClass> emptyGiven() {
		return emptyGivenInstance().getClass();
	}

	private static SpecClass emptyGivenInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.given("a precondition", () -> {});
			}
		};
	}

	public static Class<? extends SpecClass> emptySpecClass() {
		return emptySpecClassInstance().getClass();
	}

	private static SpecClass emptySpecClassInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) { /* Do nothing */ }
		};
	}

	public static Class<? extends SpecClass> givenWithOneSpec() {
		return givenWithOneSpecInstance().getClass();
	}

	private static SpecClass givenWithOneSpecInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.given("a precondition", () -> {
					javaspec.pending("spec");
				});
			}
		};
	}

	public static Class<? extends SpecClass> nestedDescribe() {
		return nestedDescribeInstance().getClass();
	}

	private static SpecClass nestedDescribeInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.describe("describe-outer", () -> {
					javaspec.describe("describe-inner", () -> {});
					javaspec.pending("describe-outer-spec");
					;
				});
			}
		};
	}

	public static Class<?> notASpecClass() {
		return new Object() {}.getClass();
	}

	public static Class<? extends SpecClass> oneSpec() {
		return oneSpecInstance().getClass();
	}

	private static SpecClass oneSpecInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.it("one spec", () -> assertEquals(2, 1 + 1));
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
				javaspec.it("throws", () -> assertEquals(42, 41));
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

	public static Class<? extends SpecClass> pendingSpec() {
		return pendingSpecInstance().getClass();
	}

	private static SpecClass pendingSpecInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.pending("pending spec");
			}
		};
	}

	public static Class<? extends SpecClass> skippedSpec() {
		return skippedSpecInstance().getClass();
	}

	private static SpecClass skippedSpecInstance() {
		return new SpecClass() {
			@Override
			public void declareSpecs(JavaSpec javaspec) {
				javaspec.skip("skipped spec", () -> {});
			}
		};
	}
}