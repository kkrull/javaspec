package info.javaspec.api;

//Entrypoint for all syntax used to write specs in JavaSpec
public interface JavaSpec {
	// Context
	void describe(Class<?> aClass, BehaviorDeclaration declaration);
	void describe(String what, BehaviorDeclaration declaration);
	void given(String what, BehaviorDeclaration declaration);

	// Specs
	void it(String behavior, Verification verification);
	void pending(String futureBehavior);
	void skip(String intendedBehavior, Verification brokenVerification);

	@FunctionalInterface
	interface BehaviorDeclaration {
		void declare();
	}
}
