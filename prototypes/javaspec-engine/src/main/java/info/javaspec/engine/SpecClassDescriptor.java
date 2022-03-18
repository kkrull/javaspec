package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import info.javaspec.api.Verification;
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
	public void it(String behavior, Verification verification) {
		SpecDescriptor specDescriptor = SpecDescriptor.of(getUniqueId(), behavior, verification);
		specDescriptor.setParent(this);
		this.addChild(specDescriptor);
	}

	@Override
	public void pending(String futureBehavior) {
		PendingSpecDescriptor specDescriptor = PendingSpecDescriptor.of(getUniqueId(), futureBehavior);
		specDescriptor.setParent(this);
		this.addChild(specDescriptor);
	}
}
