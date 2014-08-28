package org.javaspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.javaspec.dsl.Before;
import org.javaspec.dsl.Cleanup;
import org.javaspec.dsl.It;

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

  private TestFunction readTestFunction() { //TODO KDK: Clean
    Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();
    Object context = newContextObject(assertionField.getDeclaringClass(), instances);
    try {
      List<Before> beforeValues = new LinkedList<Before>();
      for(Field before : befores) {
        Object contextForBefore = instances.get(before.getDeclaringClass());
        Before value = (Before)assignedValue(before, contextForBefore);
        beforeValues.add(value);
      }
      
      List<Cleanup> afterValues = new LinkedList<Cleanup>();
      for(Field after : afters) {
        Object contextForAfter = instances.get(after.getDeclaringClass());
        Cleanup value = (Cleanup)assignedValue(after, contextForAfter);
        afterValues.add(value);
      }
      
      return new TestFunction((It)assignedValue(assertionField, context), beforeValues, afterValues);
    } catch (Throwable t) {
      throw new TestSetupException(context.getClass(), t);
    }
  }
  
  private static Object newContextObject(Class<?> contextClass, Map<Class<?>, Object> instances) { //TODO KDK: Clean
    Class<?> enclosingClass = contextClass.getEnclosingClass();
    if(enclosingClass == null) {
      Constructor<?> noArgConstructor;
      try {
        noArgConstructor = contextClass.getConstructor();
      } catch (Exception e) {
        throw new UnsupportedConstructorException(contextClass, e);
      }

      Object context;
      try {
        context = noArgConstructor.newInstance();
        instances.put(contextClass, context);
      } catch (Exception | AssertionError e) {
        throw new TestSetupException(contextClass, e);
      }
      return context;
    } else if(Modifier.isStatic(contextClass.getModifiers())) { 
      Constructor<?> noArgConstructor;
      try {
        noArgConstructor = contextClass.getConstructor();
      } catch (Exception e) {
        throw new UnsupportedConstructorException(contextClass, e);
      }

      Object context;
      try {
        context = noArgConstructor.newInstance();
        instances.put(contextClass, context);
      } catch (Exception | AssertionError e) {
        throw new TestSetupException(contextClass, e);
      }
      return context;
    } else {
      Object enclosingObject = newContextObject(enclosingClass, instances);
      Constructor<?> constructor;
      try {
        constructor = contextClass.getConstructor(enclosingClass);
      } catch (Exception e) {
        throw new UnsupportedConstructorException(contextClass, e);
      }

      Object context;
      try {
        context = constructor.newInstance(enclosingObject);
        instances.put(contextClass, context);
      } catch (Exception | AssertionError e) {
        throw new TestSetupException(contextClass, e);
      }
      return context;
    }
  }
  
  private Object assignedValue(Field field, Object context) throws IllegalAccessException {
    field.setAccessible(true);
    return field.get(context);
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