package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

public class SpecClasses {
	private SpecClasses() {
		/* Static class */
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
}
