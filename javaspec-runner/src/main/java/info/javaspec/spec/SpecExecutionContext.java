package info.javaspec.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * An instance of the object(s) in which a spec exists and executes.
 *
 * When multiple specs are in the same (declaration) context, a distinct execution context will be used for each spec.
 */
final class SpecExecutionContext {
  private final Map<Class<?>, Object> instances = new HashMap<>();

  public static SpecExecutionContext forDeclaringClass(Class<?> declaringClass) {
    SpecExecutionContext executionContext = new SpecExecutionContext();
    executionContext.makeAndRememberInstance(declaringClass);
    return executionContext;
  }

  private SpecExecutionContext() { }

  public <T> T getAssignedValue(Field field, Class<T> assignableType) {
    Class<?> declaringClass = field.getDeclaringClass();
    try {
      Object declaredContext = instances.get(declaringClass);
      field.setAccessible(true);
      Object value = field.get(declaredContext);
      return assignableType.cast(value);
    } catch(Throwable t) {
      throw TestSetupFailed.forClass(declaringClass, t);
    }
  }

  private Object makeAndRememberInstance(Class<?> contextClass) {
    Object context = makeInstance(contextClass);
    instances.put(contextClass, context);
    return context;
  }

  private Object makeInstance(Class<?> contextClass) {
    Class<?> enclosingClass = contextClass.getEnclosingClass();
    if(enclosingClass == null || Modifier.isStatic(contextClass.getModifiers())) {
      return new OuterClassFactory().makeInstance(contextClass);
    } else {
      Object enclosingObject = makeAndRememberInstance(enclosingClass);
      return new InnerClassFactory(enclosingClass, enclosingObject).makeInstance(contextClass);
    }
  }

  static final class OuterClassFactory extends ClassFactory {
    @Override
    protected Constructor<?> getConstructor(Class<?> outerClass) throws NoSuchMethodException {
      return outerClass.getDeclaredConstructor();
    }

    @Override
    protected Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException {
      return constructor.newInstance();
    }
  }

  static final class InnerClassFactory extends ClassFactory {
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

  static final class TestSetupFailed extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static TestSetupFailed forClass(Class<?> context, Throwable cause) {
      return new TestSetupFailed(
        String.format("Failed to create test context %s", context.getName()),
        cause);
    }

    private TestSetupFailed(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
