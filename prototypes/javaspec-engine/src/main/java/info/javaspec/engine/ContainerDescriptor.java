package info.javaspec.engine;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

class ContainerDescriptor extends AbstractTestDescriptor {
  public static ContainerDescriptor forClass(UniqueId parentId, Class<?> specClass) {
    return new ContainerDescriptor(
      parentId.append("class", specClass.getName()),
      specClass.getName()
    );
  }

  private ContainerDescriptor(UniqueId uniqueId, String displayName) {
    super(uniqueId, displayName);
  }

  @Override
  public Type getType() {
    return Type.CONTAINER;
  }
}
