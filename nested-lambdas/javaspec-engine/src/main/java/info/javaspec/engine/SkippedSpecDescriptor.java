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

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a spec that will be skipped for some reason, working like a skipped Jupiter test.
final class SkippedSpecDescriptor extends AbstractTestDescriptor implements ExecutableTestDescriptor {
	private final String reason;

	public static TestDescriptor disabled(UniqueId parentId, String intendedBehavior) {
		return new SkippedSpecDescriptor(
			parentId.append("test", intendedBehavior),
			intendedBehavior,
			"skipped"
		);
	}

	public static SkippedSpecDescriptor pending(UniqueId parentId, String futureBehavior) {
		return new SkippedSpecDescriptor(
			parentId.append("test", futureBehavior),
			futureBehavior,
			"pending"
		);
	}

	private SkippedSpecDescriptor(UniqueId uniqueId, String displayName, String reason) {
		super(uniqueId, displayName);
		this.reason = reason;
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	/* JavaSpec */

	@Override
	public void execute(EngineExecutionListener listener) {
		listener.executionStarted(this);
		listener.executionSkipped(this, this.reason);
	}
}
