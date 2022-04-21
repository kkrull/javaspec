/**
 * MIT License
 *
 * Copyright (c) 2014–2022 Kyle Krull
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.javaspec.api;

/**
 * The JavaSpec syntax used to write and organize specs.
 *
 * Start by describing a class {@link #describe(Class, BehaviorDeclaration)} or
 * any subject in general {@link #describe(String, BehaviorDeclaration)}, then
 * add 1 or more specifications inside of that block with
 * {@link #it(String, Verification)}.
 *
 * Use {@link #pending(String)} or {@link #skip(String, Verification)} for any
 * specs that you are not ready to run yet, and additional
 * {@link #describe(String, BehaviorDeclaration)} and
 * {@link #given(String, BehaviorDeclaration)} containers to organize specs, as
 * desired.
 */
public interface JavaSpec {
	/* Context */

	/**
	 * Creates a container that describes a class, resulting in a JUnit test
	 * container named after the given class.
	 *
	 * @param aClass      The [production] class to describe.
	 * @param declaration The lambda declaring specs with
	 *                    {@link #it(String, Verification)} and the like.
	 */
	void describe(Class<?> aClass, BehaviorDeclaration declaration);

	/**
	 * Creates a container that describes any generic subject, resulting in a JUnit
	 * test container with the given string.
	 *
	 * @param what        The subject or system under test that you are describing.
	 * @param declaration The lambda declaring specs with
	 *                    {@link #it(String, Verification)} and the like.
	 */
	void describe(String what, BehaviorDeclaration declaration);

	/**
	 * Create a specialized container that describes some subject's behavior, given
	 * some input or precondition. This results in a JUnit test container with the
	 * word "given" pre-pended to the given string.
	 *
	 * @param what        The input or pre-condition that has its own, specific
	 *                    behavior
	 * @param declaration The lambda declaring specs that define behavior _under the
	 *                    given pre-condition_ (or _with the given input_) with
	 *                    {@link #it(String, Verification)} and the like.
	 */
	void given(String what, BehaviorDeclaration declaration);

	/* Specs */

	/**
	 * Declare a specification (spec) that describes and verifies how the subject
	 * should behave. Results in a regular JUnit test in the container(s) in the
	 * surrounding context blocks.
	 *
	 * @param behavior     A description of what the subject is supposed to do,
	 *                     under the circumstances described in any surrounding
	 *                     context blocks.
	 * @param verification A lambda that contains the test / specification itself in
	 *                     the typical Arrange-Act-Assert pattern.
	 */
	void it(String behavior, Verification verification);

	/**
	 * Declare a spec that you're not ready to implement yet, so you remember to
	 * think about it later. Results in a disabled (skipped) JUnit test.
	 *
	 * @param futureBehavior A description of some behavior you think the subject
	 *                       should have in the near future, except that you need to
	 *                       do something else right now.
	 */
	void pending(String futureBehavior);

	/**
	 * Declare a spec that should work, if only you could finish the thought and
	 * complete the verification or fix the bug that is causing the spec to fail.
	 * Results in a disabled (skipped) JUnit test.
	 *
	 * @param intendedBehavior   A description of some behavior the subject was
	 *                           already supposed to have, except something went
	 *                           wrong and you have to do something else right now.
	 * @param brokenVerification A lambda that contains the test in the typical
	 *                           Arrange-Act-Assert pattern, except it's broken and
	 *                           you need to disable it long enough to focus on
	 *                           fixing something else right now.
	 */
	void skip(String intendedBehavior, Verification brokenVerification);

	/**
	 * A lambda with specs that are related to each other, or more containers. Call
	 * {@link JavaSpec#it(String, Verification)} inside of this.
	 */
	@FunctionalInterface
	interface BehaviorDeclaration {
		/**
		 * A lambda with specs that are related to each other, or more containers. Call
		 * {@link JavaSpec#it(String, Verification)} inside of this.
		 */
		void declare();
	}
}
