package info.javaspec.engine;

import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//A spec that runs on JUnit
public class JupiterSpec extends AbstractTestDescriptor {
	private final Verification verification;

	public static JupiterSpec forBehavior(UniqueId parentId, String behavior, Verification verification) {
		return new JupiterSpec(parentId.append("spec", behavior), behavior, verification);
	}

	private JupiterSpec(UniqueId uniqueId, String displayName, Verification verification) {
		super(uniqueId, displayName);
		this.verification = verification;
	}

	public void addTestDescriptorTo(TestDescriptor parentDescriptor) {
		parentDescriptor.addChild(this);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	public void run() {
		this.verification.execute();
	}
}
