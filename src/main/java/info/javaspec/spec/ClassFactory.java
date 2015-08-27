package info.javaspec.spec;

import java.lang.reflect.Constructor;

abstract class ClassFactory {
  public Object makeInstance(Class<?> aClass) {
    Constructor<?> constructor;
    try {
      constructor = getConstructor(aClass);
      constructor.setAccessible(true);
      return makeInstance(constructor);
    } catch(ExceptionInInitializerError e) {
      throw FaultyClassInitializer.forClass(aClass, e);
    } catch(Exception e) {
      throw UnsupportedConstructor.forClass(aClass, e);
    }
  }

  protected abstract Constructor<?> getConstructor(Class<?> aClass) throws NoSuchMethodException;
  protected abstract Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException;
}
