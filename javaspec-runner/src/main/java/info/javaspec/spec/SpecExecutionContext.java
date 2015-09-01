package info.javaspec.spec;

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

  public Object getAssignedValue(Field field) {
    Class<?> declaringClass = field.getDeclaringClass();
    try {
      Object declaredContext = instances.get(declaringClass);
      field.setAccessible(true);
      return field.get(declaredContext);
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
}
