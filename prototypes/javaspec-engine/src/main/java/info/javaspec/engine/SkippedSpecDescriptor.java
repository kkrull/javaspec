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
