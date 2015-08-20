package info.javaspec.spec;

import java.lang.reflect.Constructor;

abstract class ClassFactory {
  public Object makeInstance(Class<?> aClass) {
    Constructor<?> constructor;
    try {
      constructor = getConstructor(aClass);
      constructor.setAccessible(true);
    } catch(Exception e) {
      throw UnsupportedConstructor.forClass(aClass, e);
    }

    try {
      return makeInstance(constructor);
    } catch(Exception | AssertionError e) {
      throw new FieldSpec.TestSetupFailed(aClass, e);
    }
  }

  protected abstract Constructor<?> getConstructor(Class<?> aClass) throws NoSuchMethodException;
  protected abstract Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException;
}
