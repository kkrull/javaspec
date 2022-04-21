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
 * Entrypoint into writing specs (tests) in JavaSpec, like a JUnit Test class.
 */
public interface SpecClass {
	/**
	 * Implement this method to declare specs in JavaSpec, using the given API.
	 *
	 * @param javaspec The API you need to declare specs.  Start with
	 * {@link JavaSpec#describe(Class, info.javaspec.api.JavaSpec.BehaviorDeclaration)}
	 * or
	 * {@link JavaSpec#describe(String, info.javaspec.api.JavaSpec.BehaviorDeclaration)}
	 * and then declare 1 or more specs with
	 * {@link JavaSpec#it(String, Verification)}.
	 */
	void declareSpecs(JavaSpec javaspec);
}
