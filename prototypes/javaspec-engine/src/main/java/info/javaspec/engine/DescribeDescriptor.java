package info.javaspec.engine;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

//Adapter for describe block that makes it work like a Jupiter test container.
final class DescribeDescriptor extends AbstractTestDescriptor {
	public static DescribeDescriptor about(UniqueId parentId, String what) {
		return new DescribeDescriptor(parentId.append("describe-block", what), what);
	}

	private DescribeDescriptor(UniqueId uniqueId, String displayName) {
		super(uniqueId, displayName);
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}
}
