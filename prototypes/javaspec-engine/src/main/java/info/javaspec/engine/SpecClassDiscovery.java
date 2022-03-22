package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import info.javaspec.api.Verification;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

// Discovers specs declared in a SpecClass, converting them to a TestDescriptor that Jupiter can recognize.
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

	private Object instantiate(Class<?> clazz) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate spec class", e);
		}
	}

	/* Context discovery */

	// Entry point into the discovery process
	private TestDescriptor discover(UniqueId engineId, SpecClass declaringInstance) {
		enterScope(SpecClassDescriptor.of(engineId, declaringInstance.getClass()));
		declaringInstance.declareSpecs(this);
		return exitScope();
	}

	// TODO KDK: Do I like this?
	@Override
	public void describe(String what, BehaviorDeclaration declaration) {
		addChildContainer(
			declaration,
			parent -> ContextDescriptor.describe(parent.getUniqueId(), what)
		);
	}

	@Override
	public void given(String what, BehaviorDeclaration declaration) {
		addChildContainer(
			declaration,
			parent -> ContextDescriptor.given(parent.getUniqueId(), what)
		);
	}

	private void addChildContainer(BehaviorDeclaration block, Function<TestDescriptor, ContextDescriptor> makeChild) {
		TestDescriptor current = currentContainer();
		ContextDescriptor child = makeChild.apply(current);
		current.addChild(child);

		enterScope(child);
		block.declare();
		exitScope();
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

	private void addToCurrentContainer(Function<TestDescriptor, TestDescriptor> makeChild) {
		TestDescriptor container = currentContainer();
		TestDescriptor specDescriptor = makeChild.apply(container);
		container.addChild(specDescriptor);
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
