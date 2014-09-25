package info.javaspec.runner;

import static java.util.stream.Collectors.toList;
import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class FieldExample implements Example {
  private final String contextName;
  private final Field assertionField;
  private final List<Field> befores;
  private final List<Field> afters;

  public FieldExample(String contextName, Field it, List<Field> befores, List<Field> afters) {
    this.contextName = contextName;
    this.assertionField = it;
    this.befores = befores;
    this.afters = afters;
  }

  @Override
  public String getContextName() {
    return contextName;
  }
  
  @Override
  public String getName() {
    return assertionField.getName();
  }

  @Override
  public boolean isSkipped() {
    TestFunction f = readTestFunction();
    return f.hasUnassignedFunctions();
  }

  @Override
  public void run() throws Exception {
    TestFunction f = readTestFunction();
    try {
      for(Before before : f.befores) { before.run(); }
      f.assertion.run();
    } finally {
      for(Cleanup after : f.afters) { after.run(); }
    }
  }

  private TestFunction readTestFunction() {
    ContextFactory factory = new ContextFactory();
    factory.initNewContext(assertionField.getDeclaringClass());
    try {
      List<Before> beforeValues = befores.stream().map(x -> (Before)factory.getAssignedValue(x)).collect(toList());
      List<Cleanup> afterValues = afters.stream().map(x -> (Cleanup)factory.getAssignedValue(x)).collect(toList());
      It assertion = (It)factory.getAssignedValue(assertionField);
      return new TestFunction(assertion, beforeValues, afterValues);
    } catch (Throwable t) {
      throw new TestSetupException(assertionField.getDeclaringClass(), t);
    }
  }
  
  private static class ContextFactory {
    private final Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();
    
    public void initNewContext(Class<?> innerMostContext) {
      makeInstance(innerMostContext);
    }
    
    public Object getInstance(Class<?> anyPartOfContext) {
      return instances.get(anyPartOfContext);
    }
    
    public Object getAssignedValue(Field field) {
      Class<?> declaringClass = field.getDeclaringClass();
      try {
        Object declaredContext = getInstance(declaringClass);
        field.setAccessible(true);
        return field.get(declaredContext);
      } catch (Throwable t) {
        throw new TestSetupException(declaringClass, t);
      }
    }
    
    private Object makeInstance(Class<?> contextClass) {
      Class<?> enclosingClass = contextClass.getEnclosingClass();
      if(enclosingClass == null || Modifier.isStatic(contextClass.getModifiers())) {
        return makeTopLevelInstance(contextClass);
      } else {
        return makeEnclosedInstance(contextClass, enclosingClass);
      }
    }
    
    private Object makeTopLevelInstance(Class<?> contextClass) {
      Constructor<?> noArgConstructor;
      try {
        noArgConstructor = contextClass.getDeclaredConstructor();
        noArgConstructor.setAccessible(true);
      } catch (Exception e) {
        throw new UnsupportedConstructorException(contextClass, e);
      }
      
      try {
        Object context = noArgConstructor.newInstance();
        instances.put(contextClass, context);
        return context;
      } catch (Exception | AssertionError e) {
        throw new TestSetupException(contextClass, e);
      }
    }
    
    private Object makeEnclosedInstance(Class<?> contextClass, Class<?> enclosingClass) {
      Object enclosingObject = makeInstance(enclosingClass);
      Constructor<?> constructor;
      try {
        constructor = contextClass.getDeclaredConstructor(enclosingClass);
        constructor.setAccessible(true);
      } catch (Exception e) {
        throw new UnsupportedConstructorException(contextClass, e);
      }

      try {
        Object context = constructor.newInstance(enclosingObject);
        instances.put(contextClass, context);
        return context;
      } catch (Exception | AssertionError e) {
        throw new TestSetupException(contextClass, e);
      }
    }
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
  
  public static final class TestSetupException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestSetupException(Class<?> context, Throwable cause) {
      super(String.format("Failed to create test context %s", context.getName()), cause);
    }
  }
  
  public static final class UnsupportedConstructorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedConstructorException(Class<?> context, Throwable cause) {
      super(String.format("Unable to find a no-argument constructor for class %s", context.getName()), cause);
    }
  }
}