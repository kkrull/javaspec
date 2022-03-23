package info.javaspec.engine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a pending spec that makes it work like a skipped Jupiter test.
final class PendingSpecDescriptor extends AbstractTestDescriptor implements ExecutableTestDescriptor {
	private final String reason;

	public static TestDescriptor disabled(UniqueId parentId, String brokenBehavior) {
		return new PendingSpecDescriptor(parentId.append("test", brokenBehavior), brokenBehavior, "skipped");
	}

	public static PendingSpecDescriptor pending(UniqueId parentId, String behavior) {
		return new PendingSpecDescriptor(parentId.append("test", behavior), behavior, "pending");
	}

	private PendingSpecDescriptor(UniqueId uniqueId, String displayName, String reason) {
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
