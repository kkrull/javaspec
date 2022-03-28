package info.javaspec.jupiter.factory;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

class JavaSpec {
	private static JupiterSpec _spec;

	public static JupiterSpec getSpec() {
		return _spec;
	}

	public static void it(String behavior, Executable verification) {
		_spec = new JupiterSpec(behavior, verification);
	}

	static class JupiterSpec {
		private final String behavior;
		private final Executable verification;

		public JupiterSpec(String behavior, Executable verification) {
			this.behavior = behavior;
			this.verification = verification;
		}

		public DynamicTest toDynamicTest() {
			return DynamicTest.dynamicTest(behavior, verification);
		}
	}
}
