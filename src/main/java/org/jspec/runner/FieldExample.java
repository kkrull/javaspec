package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jspec.dsl.Because;
import org.jspec.dsl.Establish;
import org.jspec.dsl.It;

final class FieldExample implements Example {
  private final Field arrangeField; //TODO KDK: Flag-style class to support optional setup kind of kludgy; try something else like Decorator or Template Methods
  private final Field actionField; 
  private final Field assertionField; 
  
  FieldExample(Field arrangeField, Field actionField, Field assertionField) {
    this.arrangeField = arrangeField;
    this.actionField = actionField;
    this.assertionField = assertionField;
  }
  
  @Override
  public String describeSetup() {
    return arrangeField == null ? "" : arrangeField.getName();
  }
  
  @Override
  public String describeAction() {
    return actionField == null ? "" : actionField.getName();
  }
  
  @Override
  public String describeBehavior() {
    return assertionField.getName();
  }
  
  @Override
  public void run() throws Exception {
    Object context = newContextObject();
    Establish arrange = null;
    Because action = null;
    It assertion = null;
    try {
      arrange = arrangeField == null ? () -> { return; } : (Establish)readField(context, arrangeField);
      action = actionField == null ? () -> { return; } : (Because)readField(context, actionField);
      assertion = (It)readField(context, assertionField);
    } catch (Throwable t) {
      throw new TestSetupException(context.getClass(), t);
    }
    
    arrange.run();
    action.run();
    assertion.run();
  }

  private Object newContextObject() {
    Constructor<?> noArgConstructor;
    try {
      Class<?> contextClass = assertionField.getDeclaringClass();
      noArgConstructor = contextClass.getConstructor();
    } catch (Exception e) {
      throw new UnsupportedConstructorException(assertionField.getDeclaringClass(), e);
    }

    Object context;
    try {
      context = noArgConstructor.newInstance();
    } catch (Exception | AssertionError e) {
      throw new TestSetupException(noArgConstructor.getDeclaringClass(), e);
    }
    return context;
  }
  
  private Object readField(Object context, Field field) throws IllegalAccessException {
    field.setAccessible(true);
    return field.get(context);
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
