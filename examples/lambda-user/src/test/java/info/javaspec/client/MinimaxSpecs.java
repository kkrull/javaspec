package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class MinimaxSpecs implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Minimax.class, () -> {
			javaspec.describe("#score", () -> {
				javaspec.it("exists", () -> {
					new Minimax().score();
				});
			});
		});
	}

	private static class Minimax {
		public void score() {}
	}
}
