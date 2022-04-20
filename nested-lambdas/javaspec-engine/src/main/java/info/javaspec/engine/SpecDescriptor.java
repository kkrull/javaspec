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
package info.javaspec.engine;

import info.javaspec.api.Verification;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a spec that makes it work like a Jupiter test.
final class SpecDescriptor extends AbstractTestDescriptor implements ExecutableTestDescriptor {
	private final Verification verification;

	public static SpecDescriptor of(UniqueId parentId, String behavior, Verification verification) {
		return new SpecDescriptor(parentId.append("test", behavior), behavior, verification);
	}

	private SpecDescriptor(UniqueId uniqueId, String displayName, Verification verification) {
		super(uniqueId, displayName);
		this.verification = verification;
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	/* JavaSpec */

	@Override
	public void execute(EngineExecutionListener listener) {
		listener.executionStarted(this);

		try {
			this.verification.execute();
		} catch (AssertionError | Exception e) {
			listener.executionFinished(this, TestExecutionResult.failed(e));
			return;
		}

		listener.executionFinished(this, TestExecutionResult.successful());
	}
}
