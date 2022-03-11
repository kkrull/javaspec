package info.javaspec.engine;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class JavaSpecEngine implements TestEngine {
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
		EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");

		discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
			.map(ClassSelector::getJavaClass)
			.map(specClass -> SpecClassDescriptor.forClass(engineId, specClass))
			.forEach(engineDescriptor::addChild);

		return engineDescriptor;
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

	private static final class SpecClassDescriptor extends AbstractTestDescriptor {
		public static SpecClassDescriptor forClass(UniqueId parentId, Class<?> specClass) {
			return new SpecClassDescriptor(parentId.append("class", specClass.getName()), specClass.getName());
		}

		private SpecClassDescriptor(UniqueId uniqueId, String displayName) {
			super(uniqueId, displayName);
		}

		@Override
		public Type getType() {
			return Type.CONTAINER;
		}
	}
}
