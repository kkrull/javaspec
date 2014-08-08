package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jspec.dsl.It;

final class FieldExample implements Example {
  private final Field behavior;
  
  public FieldExample(Field behavior) {
    if(!It.class.equals(behavior.getType()))
      throw new UnsupportedFieldException(behavior);
    
    this.behavior = behavior;
  }
  
  @Override
  public String describeBehavior() {
    return behavior.getName();
  }
  
  @Override
  public void run() throws Exception {
    Constructor<?> noArgConstructor;
    try {
      Class<?> contextClass = behavior.getDeclaringClass();
      noArgConstructor = contextClass.getConstructor();
    } catch (Exception e) {
      throw new UnsupportedConstructorException(behavior.getDeclaringClass());
    }

    Object context;
    try {
      context = noArgConstructor.newInstance();
    } catch (Exception e) {
      throw new TestSetupException(noArgConstructor.getDeclaringClass(), e);
    }

    It thunk;
    try {
      // behavior.setAccessible(true);
      thunk = (It) behavior.get(context);
    } catch (Exception e) {
      throw new TestRunException(behavior, e);
    }
    
    // try {
    // thunk.run();
    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new UnsupportedOperationException(e);
    // }
  }
  
  public static final class TestRunException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public TestRunException(Field exampleField, Exception cause) {
      super(String.format("Failed to access example behavior defined by %s.%s", 
        exampleField.getDeclaringClass().getName(), exampleField.getName()), cause);
    }
  }
  
  public static final class TestSetupException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestSetupException(Class<?> context, Exception cause) {
      super(String.format("Failed to construct test context %s", context.getName()), cause);
    }
  }
  
  public static final class UnsupportedConstructorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedConstructorException(Class<?> context) {
      super(String.format("Unable to find a no-argument constructor for class %s", context.getName()));
    }
  }
  
  public static final class UnsupportedFieldException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedFieldException(Field f) {
      super(String.format("Invalid type for %s.%s: %s", 
        f.getDeclaringClass().getName(), 
        f.getName(),
        f.getType().getName()));
    }
  }
}