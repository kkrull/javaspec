package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a SpecClass that makes it work like a Jupiter test container.
//Implements JavaSpec syntax on Jupiter.
final class SpecClassDescriptor extends AbstractTestDescriptor implements JavaSpec {
	private final SpecClass declaringInstance;

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
	public void describe(String what, BehaviorDeclaration declaration) {
		DescribeDescriptor child = DescribeDescriptor.about(getUniqueId(), what);
		this.addChild(child);

		// I haven't added any notion of a stack to track the current context yet
		// This will cause specs in the describe block to be children of this
		// SpecClassDescriptor instead of DescribeDescriptor.
//		declaration.declare();
	}

	@Override
	public void it(String behavior, Verification verification) {
		SpecDescriptor specDescriptor = SpecDescriptor.of(getUniqueId(), behavior, verification);
		this.addChild(specDescriptor);
	}

	@Override
	public void pending(String futureBehavior) {
		PendingSpecDescriptor specDescriptor = PendingSpecDescriptor.of(getUniqueId(), futureBehavior);
		this.addChild(specDescriptor);
	}

	private TestDescriptor currentContainer() {
		return this;
	}
}
