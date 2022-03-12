package info.javaspec.engine;

import java.lang.reflect.Constructor;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import info.javaspec.api.SpecClass;

public class JavaSpecEngine implements TestEngine {
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
		EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");

		discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
			.map(ClassSelector::getJavaClass)
			.filter(anyClass -> SpecClass.class.isAssignableFrom(anyClass))
			.map(specClass -> makeDeclaringObject(specClass))
			.map(SpecClass.class::cast)
			.map(declaringInstance -> {
				// declaringInstance.declareSpecs(javaspec);
				return SpecClassDescriptor.forClass(engineId, declaringInstance.getClass());
			})
			.forEach(engineDescriptor::addChild);

		return engineDescriptor;
	}

	private Object makeDeclaringObject(Class<?> clazz) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate spec class", e);
		}
	}

	@Override
	public void execute(ExecutionRequest request) {
		EngineExecutionListener listener = request.getEngineExecutionListener();
		TestDescriptor rootDescriptor = request.getRootTestDescriptor();
		listener.executionStarted(rootDescriptor);

		// for (TestDescriptor child : rootDescriptor.getChildren()) {
		// 	listener.executionStarted(child);
		// 	listener.executionFinished(child, TestExecutionResult.successful());
		// }

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
