package info.javaspec.runner;

import java.lang.reflect.Constructor;

final class OuterClassFactory extends ClassFactory {
  @Override
  protected Constructor<?> getConstructor(Class<?> outerClass) throws NoSuchMethodException {
    return outerClass.getDeclaredConstructor();
  }

  @Override
  protected Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException {
    return constructor.newInstance();
  }
}
