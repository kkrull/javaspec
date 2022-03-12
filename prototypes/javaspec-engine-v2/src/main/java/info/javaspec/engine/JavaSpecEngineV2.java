package info.javaspec.engine;

import java.lang.reflect.Constructor;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import info.javaspec.api.Verification;

public class JavaSpecEngineV2 implements TestEngine {
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
		EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");

		discoveryRequest.getSelectorsByType(ClassSelector.class).stream().map(ClassSelector::getJavaClass)
				.filter(anyClass -> SpecClass.class.isAssignableFrom(anyClass)).map(specClass -> instantiate(specClass))
				.map(SpecClass.class::cast).map(declaringInstance -> {
					SpecClassDescriptor specClassDescriptor = SpecClassDescriptor.of(engineId, declaringInstance);
					specClassDescriptor.discover();
					return specClassDescriptor;
				}).forEach(engineDescriptor::addChild);

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
		switch (descriptor.getType()) {
			case CONTAINER :
				listener.executionStarted(descriptor);

				for (TestDescriptor child : descriptor.getChildren()) {
					execute(child, listener);
				}

				listener.executionFinished(descriptor, TestExecutionResult.successful());
				return;

			case TEST :
				listener.executionStarted(descriptor);
				SpecDescriptor spec = SpecDescriptor.class.cast(descriptor);

				try {
					spec.execute();
				} catch (AssertionError | Exception e) {
					listener.executionFinished(spec, TestExecutionResult.failed(e));
					return;
				}

				listener.executionFinished(descriptor, TestExecutionResult.successful());
				return;

			default :
				throw new UnsupportedOperationException(String.format("Unsupported TestDescriptor: %s", descriptor));
		}
	}

	@Override
	public String getId() {
		return "javaspec-engine-v2";
	}

	private static final class SpecClassDescriptor extends AbstractTestDescriptor implements JavaSpec {
		private final SpecClass declaringInstance;

		public static SpecClassDescriptor of(UniqueId parentId, SpecClass declaringInstance) {
			Class<? extends SpecClass> declaringClass = declaringInstance.getClass();
			return new SpecClassDescriptor(parentId.append("class", declaringClass.getName()), declaringClass.getName(),
					declaringInstance);
		}

		private SpecClassDescriptor(UniqueId uniqueId, String displayName, SpecClass declaringInstance) {
			super(uniqueId, displayName);
			this.declaringInstance = declaringInstance;
		}

		/* Jupiter */

		@Override
		public Type getType() {
			return Type.CONTAINER;
		}

		/* JavaSpec */

		public void discover() {
			this.declaringInstance.declareSpecs(this);
		}

		@Override
		public void it(String behavior, Verification verification) {
			SpecDescriptor specDescriptor = SpecDescriptor.of(getUniqueId(), behavior, verification);
			specDescriptor.setParent(this);
			this.addChild(specDescriptor);
		}
	}

	private static final class SpecDescriptor extends AbstractTestDescriptor {
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

		public void execute() {
			this.verification.execute();
		}
	}
}
