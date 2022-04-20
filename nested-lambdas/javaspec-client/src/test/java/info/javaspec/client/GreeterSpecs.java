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
package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

//Specs that exercise JavaSpec.
//Run in IntelliJ or with ./gradlew :javaspec-client:test.
//Adding @Testable shows the run icon in IntelliJ, to run this spec by itself.
//Without @Testable, you can still pick "Run Tests" on a package/directory.
@Testable
public class GreeterSpecs implements SpecClass {
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Greeter.class, () -> {
			javaspec.describe("#greet", () -> {
				javaspec.it("greets the world, given no name", () -> {
					Greeter subject = new Greeter();
					assertEquals("Hello world!", subject.greet());
				});

				javaspec.it("greets a person by name, given a name", () -> {
					Greeter subject = new Greeter();
					assertEquals("Hello Adventurer!", subject.greet("Adventurer"));
				});
			});
		});
	}
}
