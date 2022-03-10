package info.javaspec.engine;

import org.junit.platform.engine.*;

public class JavaSpecEngine implements TestEngine {
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
		return null;
	}

	@Override
	public void execute(ExecutionRequest request) {
	}

	@Override
	public String getId() {
		return "javaspec-engine-v2";
	}
}
