package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import java.lang.reflect.Constructor;
import java.util.ServiceLoader;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class JavaSpecEngine implements TestEngine {
	private final EngineDiscoveryRequestListenerProvider loader;

	public JavaSpecEngine() {
		this.loader = () -> ServiceLoader
			.load(EngineDiscoveryRequestListener.class)
			.findFirst();
	}

	public JavaSpecEngine(EngineDiscoveryRequestListenerProvider loader) {
		this.loader = loader;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
		this.loader.findFirst()
			.ifPresent(listener -> listener.onDiscover(discoveryRequest));

		EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");
		discoveryRequest.getSelectorsByType(ClassSelector.class)
			.stream()
			.map(ClassSelector::getJavaClass)
			.filter(anyClass -> SpecClass.class.isAssignableFrom(anyClass))
			.map(specClass -> instantiate(specClass))
			.map(SpecClass.class::cast)
			.map(declaringInstance ->
			{
				SpecClassDescriptor specClassDescriptor = SpecClassDescriptor.of(engineId, declaringInstance);
				specClassDescriptor.discover();
				return specClassDescriptor;
			})
			.forEach(engineDescriptor::addChild);

		return engineDescriptor;
	}

	private Object instantiate(Class<?> clazz) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate spec class", e);
		}
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor rootDescriptor = request.getRootTestDescriptor();
		EngineExecutionListener listener = request.getEngineExecutionListener();
		execute(rootDescriptor, listener);
	}

	private void execute(TestDescriptor descriptor, EngineExecutionListener listener) {
		if (descriptor instanceof PendingSpecDescriptor) {
			PendingSpecDescriptor pendingDescriptor = PendingSpecDescriptor.class.cast(descriptor);
			pendingDescriptor.execute(listener);
			return;
		}

		switch (descriptor.getType()) {
		case CONTAINER:
			listener.executionStarted(descriptor);

			for (TestDescriptor child : descriptor.getChildren()) {
				execute(child, listener);
			}

			listener.executionFinished(descriptor, TestExecutionResult.successful());
			return;

		case TEST:
			SpecDescriptor spec = SpecDescriptor.class.cast(descriptor);
			spec.execute(listener);
			return;

		default:
			throw new UnsupportedOperationException(String.format("Unsupported TestDescriptor: %s", descriptor));
		}
	}

	@Override
	public String getId() {
		return "javaspec-engine";
	}
}
