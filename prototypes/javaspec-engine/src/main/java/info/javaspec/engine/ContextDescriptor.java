package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for various context blocks that makes it work like a Jupiter test container.
final class ContextDescriptor extends AbstractTestDescriptor implements ExecutableTestDescriptor {
	public static ContextDescriptor declaringClass(UniqueId parentId, Class<? extends SpecClass> declaringClass) {
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
