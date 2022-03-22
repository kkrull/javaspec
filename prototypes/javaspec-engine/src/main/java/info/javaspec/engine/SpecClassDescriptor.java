package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import info.javaspec.api.Verification;
import java.util.Stack;
import java.util.function.Function;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a SpecClass that makes it work like a Jupiter test container.
//Implements JavaSpec syntax on Jupiter.
final class SpecClassDescriptor extends AbstractTestDescriptor implements JavaSpec {
	private final SpecClass declaringInstance;
	private final Stack<TestDescriptor> containersInScope;

	public static SpecClassDescriptor of(UniqueId parentId, SpecClass declaringInstance) {
		Class<? extends SpecClass> declaringClass = declaringInstance.getClass();
		return new SpecClassDescriptor(
			parentId.append("class", declaringClass.getName()),
			declaringClass.getName(),
			declaringInstance
		);
	}

	private SpecClassDescriptor(UniqueId uniqueId, String displayName, SpecClass declaringInstance) {
		super(uniqueId, displayName);
		this.declaringInstance = declaringInstance;
		this.containersInScope = new Stack<>();
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	/* Context discovery */

	// Entry point into the discovery process
	public void discover() {
		enterScope(this); // no pop?
		this.declaringInstance.declareSpecs(this);
	}

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

	private void exitScope() {
		this.containersInScope.pop();
	}
}
