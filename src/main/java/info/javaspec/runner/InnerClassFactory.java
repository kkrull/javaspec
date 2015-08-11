package info.javaspec.runner;

import java.lang.reflect.Constructor;

final class InnerClassFactory extends ClassFactory {
  private final Class<?> enclosingClass;
  private final Object enclosingObject;

  public InnerClassFactory(Class<?> enclosingClass, Object enclosingObject) {
    this.enclosingClass = enclosingClass;
    this.enclosingObject = enclosingObject;
  }

  @Override
  protected Constructor<?> getConstructor(Class<?> innerClass) throws NoSuchMethodException {
    return innerClass.getDeclaredConstructor(enclosingClass);
  }

  @Override
  protected Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException {
    return constructor.newInstance(enclosingObject);
  }
}
