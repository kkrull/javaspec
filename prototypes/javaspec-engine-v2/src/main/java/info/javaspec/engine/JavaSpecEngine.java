package info.javaspec.engine;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class JavaSpecEngine implements TestEngine {
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
		return new EngineDescriptor(engineId, "JavaSpec");
	}

	@Override
	public void execute(ExecutionRequest request) {
		EngineExecutionListener listener = request.getEngineExecutionListener();
		TestDescriptor rootDescriptor = request.getRootTestDescriptor();
		listener.executionStarted(rootDescriptor);
		listener.executionFinished(rootDescriptor, TestExecutionResult.successful());
	}

	@Override
	public String getId() {
		return "javaspec-engine-v2";
	}
}
