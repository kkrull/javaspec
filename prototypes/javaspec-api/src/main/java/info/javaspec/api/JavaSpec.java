package info.javaspec.api;

//Entrypoint for all syntax used to write specs in JavaSpec
public interface JavaSpec {
	void describe(String what, BehaviorDeclaration declaration);
	void it(String behavior, Verification verification);
	void pending(String futureBehavior);

	@FunctionalInterface
	interface BehaviorDeclaration {
		void declare();
	}
}
