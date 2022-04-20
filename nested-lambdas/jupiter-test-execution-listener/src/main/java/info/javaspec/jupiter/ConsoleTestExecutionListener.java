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
package info.javaspec.jupiter;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

//Logs events to the console
//Register via: https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-discovery-listeners-custom
public class ConsoleTestExecutionListener implements TestExecutionListener {
	public ConsoleTestExecutionListener() {
		/* Required as service wrapper */ }

	@Override
	public void testPlanExecutionStarted(TestPlan plan) {
		System.out.printf(
			"[ConsoleTestExecutionListener#testPlanExecutionStarted] %d tests%n",
			plan.countTestIdentifiers(x -> true)
		);
	}

	@Override
	public void testPlanExecutionFinished(TestPlan plan) {
		System.out.printf("[ConsoleTestExecutionListener#testPlanExecutionFinished]%n");
	}

	@Override
	public void dynamicTestRegistered(TestIdentifier id) {
		System.out.printf("[ConsoleTestExecutionListener#dynamicTestRegistered] %s%n", id);
	}

	@Override
	public void executionSkipped(TestIdentifier id, String reason) {
		System.out.printf("[ConsoleTestExecutionListener#executionSkipped] %s: %s%n", id, reason);
	}

	@Override
	public void executionStarted(TestIdentifier id) {
		System.out.printf("[ConsoleTestExecutionListener#executionStarted] %s%n", id);
	}

	@Override
	public void executionFinished(TestIdentifier id, TestExecutionResult result) {
		System.out.printf("[ConsoleTestExecutionListener#executionFinished] %s: %s%n", id, result);
	}

	@Override
	public void reportingEntryPublished(TestIdentifier id, ReportEntry entry) {
		System.out.printf("[ConsoleTestExecutionListener#reportingEntryPublished] %s: %s%n", id, entry);
	}
}
