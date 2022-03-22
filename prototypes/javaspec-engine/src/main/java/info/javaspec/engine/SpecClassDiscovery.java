package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import info.javaspec.api.Verification;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Stack;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

// Runs JavaSpec declarations in a SpecClass, converting them to TestDescriptors Jupiter can use.
final class SpecClassDiscovery implements JavaSpec {
	private final Class<?> selectedClass;
	private final Stack<TestDescriptor> containersInScope;

	public SpecClassDiscovery(Class<?> selectedClass) {
		this.selectedClass = selectedClass;
		this.containersInScope = new Stack<>();
	}

	public Optional<TestDescriptor> discover(UniqueId engineId) {
		return Optional.of(this.selectedClass)
			.filter(SpecClass.class::isAssignableFrom)
			.map(this::instantiate)
			.map(SpecClass.class::cast)
			.map(declaringInstance -> this.discover(engineId, declaringInstance));
	}

	private TestDescriptor discover(UniqueId engineId, SpecClass declaringInstance) {
		enterScope(SpecClassDescriptor.of(engineId, declaringInstance.getClass()));
		declaringInstance.declareSpecs(this);
		return exitScope();
	}

	private Object instantiate(Class<?> specClass) {
		try {
			Constructor<?> constructor = specClass.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate spec class", e);
		}
	}

	/* Context discovery */

	@Override
	public void describe(String what, BehaviorDeclaration declaration) {
		pushChildContainer(
			declaration,
			parent -> ContextDescriptor.describe(parent.getUniqueId(), what)
		);
	}

	@Override
	public void given(String what, BehaviorDeclaration declaration) {
		pushChildContainer(
			declaration,
			parent -> ContextDescriptor.given(parent.getUniqueId(), what)
		);
	}

	private void pushChildContainer(BehaviorDeclaration block, ContainerDescriptorFactory factory) {
		TestDescriptor current = currentContainer();
		ContextDescriptor child = factory.makeChildContainer(current);
		current.addChild(child);

		enterScope(child);
		block.declare();
		exitScope();
	}

	// Makes a TestDescriptor for a container, as a child of the given parent
	@FunctionalInterface
	private interface ContainerDescriptorFactory {
		ContextDescriptor makeChildContainer(TestDescriptor parent);
	}

	/* Spec discovery */

	@Override
	public void it(String behavior, Verification verification) {
		addToCurrentContainer(
			container -> SpecDescriptor.of(
				container.getUniqueId(),
				behavior,
				verification
			)
		);
	}

	@Override
	public void pending(String futureBehavior) {
		addToCurrentContainer(
			container -> PendingSpecDescriptor.of(
				container.getUniqueId(),
				futureBehavior
			)
		);
	}

	private void addToCurrentContainer(TestDescriptorFactory factory) {
		TestDescriptor container = currentContainer();
		TestDescriptor specDescriptor = factory.makeTestDescriptor(container);
		container.addChild(specDescriptor);
	}

	// Makes a TestDescriptor for a spec, as a child of the given container
	@FunctionalInterface
	private interface TestDescriptorFactory {
		TestDescriptor makeTestDescriptor(TestDescriptor parent);
	}

	/* Declaration scope */

	private TestDescriptor currentContainer() {
		return this.containersInScope.peek();
	}

	private void enterScope(TestDescriptor container) {
		this.containersInScope.push(container);
	}

	private TestDescriptor exitScope() {
		return this.containersInScope.pop();
	}
}
