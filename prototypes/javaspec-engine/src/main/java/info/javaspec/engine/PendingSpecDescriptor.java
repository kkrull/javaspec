package info.javaspec.engine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a pending spec that makes it work like a skipped Jupiter test.
final class PendingSpecDescriptor extends AbstractTestDescriptor implements JavaSpecDescriptor {
	public static PendingSpecDescriptor of(UniqueId parentId, String behavior) {
		return new PendingSpecDescriptor(parentId.append("test", behavior), behavior);
	}

	private PendingSpecDescriptor(UniqueId uniqueId, String displayName) {
		super(uniqueId, displayName);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	/* JavaSpec */

	@Override
	public void execute(EngineExecutionListener listener) {
		listener.executionStarted(this);
		listener.executionSkipped(this, "pending");
	}
}
