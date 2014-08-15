package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jspec.dsl.Because;
import org.jspec.dsl.Establish;
import org.jspec.dsl.It;

final class FieldExample implements Example {
  private static final Establish NOP_ESTABLISH = () -> { return; };
  private static final Because NOP_BECAUSE = () -> { return; };
  
  private final Field arrangeField; //TODO KDK: Flag-style class to support optional setup kind of kludgy; try something else like Decorator or Template Methods
  private final Field actionField;
  private final Field assertionField;
  private final Field cleanupField;
  
  FieldExample(Field arrangeField, Field actionField, Field assertionField, Field cleanupField) {
    this.arrangeField = arrangeField;
    this.actionField = actionField;
    this.assertionField = assertionField;
    this.cleanupField = cleanupField;
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
  public String describeCleanup() {
    return cleanupField == null ? "" : cleanupField.getName();
  }
  
  @Override
  public void run() throws Exception {
    Object context = newContextObject();
    TestFunction test = readTestFunctions(context);
    test.arrange.run();
    test.action.run();
    test.assertion.run();
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
  
  private TestFunction readTestFunctions(Object context) {
    try {
      return new TestFunction(
        arrangeField == null ? NOP_ESTABLISH : (Establish)assignedValue(arrangeField, context),
        actionField == null ? NOP_BECAUSE : (Because)assignedValue(actionField, context),
        (It)assignedValue(assertionField, context));
    } catch (Throwable t) {
      throw new TestSetupException(context.getClass(), t);
    }
  }
  
  private Object assignedValue(Field field, Object context) throws IllegalAccessException {
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
  
  
  private static class TestFunction {
    public final Establish arrange;
    public final Because action;
    public final It assertion;
    
    public TestFunction(Establish arrange, Because action, It assertion) {
      this.arrange = arrange;
      this.action = action;
      this.assertion = assertion;
    }
  }
}