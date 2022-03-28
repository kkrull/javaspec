package info.javaspec.engine;

import org.junit.platform.engine.TestDescriptor;

public class JupiterSpec {
	private final String behavior;
	private final Executable verification;

	public JupiterSpec(String behavior, Executable verification) {
		this.behavior = behavior;
		this.verification = verification;
	}

	public void addTestDescriptorTo(TestDescriptor parentDescriptor) {
		TestDescriptor specDescriptor = SpecDescriptor.forSpec(
			parentDescriptor.getUniqueId(),
			this.behavior,
			this.verification
		);

		parentDescriptor.addChild(specDescriptor);
	}
}
