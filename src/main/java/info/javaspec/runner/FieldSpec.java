package info.javaspec.runner;

import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

final class FieldSpec extends Spec {
  private final Field assertionField;
  private final List<Field> befores;
  private final List<Field> afters;
  private TestFunction testFunction;

  public FieldSpec(String id, String displayName, Field it, List<Field> befores, List<Field> afters) {
    super(id, displayName);
    this.assertionField = it;
    this.befores = befores;
    this.afters = afters;
  }

  @Override
  public boolean isIgnored() {
    return theTestFunction().hasUnassignedFunctions();
  }

  @Override
  public void run() throws Exception {
    TestFunction f = theTestFunction();
    try {
      for(Before before : f.befores) { before.run(); }
      f.assertion.run();
    } finally {
      for(Cleanup after : f.afters) { after.run(); }
    }
  }

  private TestFunction theTestFunction() {
    if(testFunction == null) {
      TestContext context = new TestContext();
      context.init(assertionField.getDeclaringClass());
      try {
        List<Before> beforeValues = befores.stream().map(x -> (Before)context.getAssignedValue(x)).collect(toList());
        List<Cleanup> afterValues = afters.stream().map(x -> (Cleanup)context.getAssignedValue(x)).collect(toList());
        It assertion = (It)context.getAssignedValue(assertionField);
        testFunction = new TestFunction(assertion, beforeValues, afterValues);
      } catch(Throwable t) {
        throw new TestSetupFailed(assertionField.getDeclaringClass(), t);
      }
    }

    return testFunction;
  }

  private static final class TestContext {
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public void init(Class<?> innerMostContext) {
      makeAndRememberInstance(innerMostContext);
    }

    public Object getAssignedValue(Field field) {
      Class<?> declaringClass = field.getDeclaringClass();
      try {
        Object declaredContext = instances.get(declaringClass);
        field.setAccessible(true);
        return field.get(declaredContext);
      } catch(Throwable t) {
        throw new TestSetupFailed(declaringClass, t);
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

  private static final class OuterClassFactory extends ClassFactory {
    @Override
    protected Constructor<?> getConstructor(Class<?> outerClass) throws NoSuchMethodException {
      return outerClass.getDeclaredConstructor();
    }

    @Override
    protected Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException {
      return constructor.newInstance();
    }
  }

  private static final class InnerClassFactory extends ClassFactory {
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

  private abstract static class ClassFactory {
    public Object makeInstance(Class<?> aClass) {
      Constructor<?> constructor;
      try {
        constructor = getConstructor(aClass);
        constructor.setAccessible(true);
      } catch(Exception e) {
        throw new UnsupportedConstructor(aClass, e);
      }

      try {
        return makeInstance(constructor);
      } catch(Exception | AssertionError e) {
        throw new TestSetupFailed(aClass, e);
      }
    }

    protected abstract Constructor<?> getConstructor(Class<?> aClass) throws NoSuchMethodException;
    protected abstract Object makeInstance(Constructor<?> constructor) throws ReflectiveOperationException;
  }

  private static class TestFunction {
    public final It assertion;
    public final List<Before> befores;
    public final List<Cleanup> afters;

    public TestFunction(It assertion, List<Before> befores, List<Cleanup> afters) {
      this.assertion = assertion;
      this.befores = befores;
      this.afters = afters;
    }

    public boolean hasUnassignedFunctions() {
      return assertion == null || befores.contains(null) || afters.contains(null);
    }
  }

  public static final class TestSetupFailed extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestSetupFailed(Class<?> context, Throwable cause) {
      super(String.format("Failed to create test context %s", context.getName()), cause);
    }
  }

  public static final class UnsupportedConstructor extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedConstructor(Class<?> context, Throwable cause) {
      super(String.format("Unable to find a no-argument constructor for class %s", context.getName()), cause);
    }
  }
}
