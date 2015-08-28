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

  static final class FaultyClassInitializer extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static FaultyClassInitializer forClass(Class<?> context, Throwable cause) {
      return new FaultyClassInitializer(
        String.format("Failed to load class %s due to a faulty static initializer", context.getName()), cause);
    }

    private FaultyClassInitializer(String message, Throwable cause) {
      super(message, cause);
    }
  }

  static final class UnsupportedConstructor extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static UnsupportedConstructor forClass(Class<?> context, Throwable cause) {
      return new UnsupportedConstructor(
        String.format("Unable to find a no-argument constructor for class %s", context.getName()),
        cause);
    }

    private UnsupportedConstructor(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
