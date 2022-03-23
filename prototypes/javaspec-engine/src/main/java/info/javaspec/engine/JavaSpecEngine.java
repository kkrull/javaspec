package info.javaspec.engine;

import java.util.Optional;
import java.util.ServiceLoader;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;

// Orchestrates the process of discovering and running specs in a Jupiter runtime.
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

		ExecutableTestDescriptor engineDescriptor = ContextDescriptor.engine(engineId);
		discoveryRequest.getSelectorsByType(ClassSelector.class)
			.stream()
			.map(ClassSelector::getJavaClass)
			.map(selectedClass -> new SpecClassDeclaration(selectedClass))
			.map(declaration -> declaration.run(engineId))
			.filter(Optional::isPresent)
			.map(Optional::orElseThrow)
			.forEach(engineDescriptor::addChild);

		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		ExecutableTestDescriptor engineDescriptor = ExecutableTestDescriptor.class.cast(request.getRootTestDescriptor());
		engineDescriptor.execute(request.getEngineExecutionListener());
	}

	@Override
	public String getId() {
		return "javaspec-engine";
	}
}
