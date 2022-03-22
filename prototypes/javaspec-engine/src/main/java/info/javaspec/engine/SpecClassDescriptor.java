package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for a SpecClass that makes it work like a Jupiter test container.
final class SpecClassDescriptor extends AbstractTestDescriptor {
	// TODO KDK: Is this just another form of a ContextDescriptor?
	public static SpecClassDescriptor of(UniqueId parentId, Class<? extends SpecClass> declaringClass) {
		return new SpecClassDescriptor(
			parentId.append("class", declaringClass.getName()),
			declaringClass.getName()
		);
	}

	private SpecClassDescriptor(UniqueId uniqueId, String displayName) {
		super(uniqueId, displayName);
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}
}
