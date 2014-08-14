package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jspec.dsl.Establish;
import org.jspec.dsl.It;

final class FieldExample implements Example {
  private final Field setup;
  private final Field behavior;
  
  FieldExample(Field setup, Field behavior) {
    this.setup = setup;
    this.behavior = behavior;
  }
  
  @Override
  public String describeBehavior() {
    return behavior.getName();
  }
  
  @Override
  public String describeSetup() {
    return setup == null ? "" : setup.getName();
  }
  
  @Override
  public void run() throws Exception {
    Object context = newContextObject();
    Establish setupThunk = readSetupThunk(context);
    
    It thunk;
    try {
      behavior.setAccessible(true);
      thunk = (It) behavior.get(context);
    } catch (Throwable t) {
      throw new TestRunException(behavior, t);
    }
    setupThunk.run();
    thunk.run();
  }

  private Object newContextObject() {
    Constructor<?> noArgConstructor;
    try {
      Class<?> contextClass = behavior.getDeclaringClass();
      noArgConstructor = contextClass.getConstructor();
    } catch (Exception e) {
      throw new UnsupportedConstructorException(behavior.getDeclaringClass(), e);
    }

    Object context;
    try {
      context = noArgConstructor.newInstance();
    } catch (Exception | AssertionError e) {
      throw new TestSetupException(noArgConstructor.getDeclaringClass(), e);
    }
    return context;
  }
  
  private Establish readSetupThunk(Object context) {
    if(setup == null)
      return () -> { return; };
    
    Establish thunk;
    try {
      setup.setAccessible(true);
      thunk = (Establish) setup.get(context);
    } catch (Throwable t) {
      throw new TestSetupException(setup, t);
    }
    return thunk;
  }
  
  public static final class TestRunException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public TestRunException(Field exampleField, Throwable cause) {
      super(String.format("Failed to access example behavior defined by %s.%s", 
        exampleField.getDeclaringClass().getName(), exampleField.getName()), cause);
    }
  }
  
  public static final class TestSetupException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestSetupException(Class<?> context, Throwable cause) {
      super(String.format("Failed to construct test context %s", context.getName()), cause);
    }
    
    public TestSetupException(Field exampleField, Throwable cause) {
      super(String.format("Failed to access example setup defined by %s.%s", 
        exampleField.getDeclaringClass().getName(), exampleField.getName()), cause);
    }
  }
  
  public static final class UnsupportedConstructorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedConstructorException(Class<?> context, Throwable cause) {
      super(String.format("Unable to find a no-argument constructor for class %s", context.getName()), cause);
    }
  }
}