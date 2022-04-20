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

import info.javaspec.api.SpecClass;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for various context blocks that makes it work like a Jupiter test container.
final class ContextDescriptor extends AbstractTestDescriptor implements ExecutableTestDescriptor {
	public static ContextDescriptor forEngine(UniqueId engineId) {
		return new ContextDescriptor(engineId, "JavaSpec");
	}

	public static ContextDescriptor forDeclaringClass(UniqueId parentId, Class<? extends SpecClass> declaringClass) {
		return new ContextDescriptor(
			parentId.append("class", declaringClass.getName()),
			declaringClass.getName()
		);
	}

	public static ContextDescriptor describe(UniqueId parentId, String what) {
		return new ContextDescriptor(
			parentId.append("describe-block", what),
			what
		);
	}

	public static ContextDescriptor given(UniqueId parentId, String what) {
		return new ContextDescriptor(
			parentId.append("given-block", what),
			String.format("given %s", what)
		);
	}

	private ContextDescriptor(UniqueId uniqueId, String displayName) {
		super(uniqueId, displayName);
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	/* JavaSpec */

	@Override
	public void execute(EngineExecutionListener listener) {
		listener.executionStarted(this);

		for (TestDescriptor child : this.getChildren()) {
			ExecutableTestDescriptor executableChild = ExecutableTestDescriptor.class.cast(child);
			executableChild.execute(listener);
		}

		listener.executionFinished(this, TestExecutionResult.successful());
	}
}
