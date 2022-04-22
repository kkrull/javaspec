package info.javaspec.api;

/**
 * A lambda with specs that are related to each other, or more containers. Call
 * {@link JavaSpec#it(String, Verification)} inside of this.
 */
@FunctionalInterface
public interface BehaviorDeclaration {
	/**
	 * A lambda with specs that are related to each other, or more containers. Call
	 * {@link JavaSpec#it(String, Verification)} inside of this.
	 */
	void declare();
}
