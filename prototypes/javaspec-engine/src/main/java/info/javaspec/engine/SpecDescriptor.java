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
