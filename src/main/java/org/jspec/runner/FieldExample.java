package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.stream.Stream;

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
  public void run() {
    Object context;
    try {
      Class<?> contextClass = behavior.getDeclaringClass();
      Constructor<?> noArgConstructor = contextClass.getConstructor();
//      context = noArgConstructor.newInstance();
    } catch (Exception e) {
      throw new UnsupportedConstructorException(behavior.getDeclaringClass());
    }
    
//    It thunk;
//    try {
//      behavior.setAccessible(true);
//      thunk = (It)behavior.get(context);
//    } catch (Exception e) {
//      throw new UnsupportedFieldException(behavior);
//    }
//    
//    try {
//      thunk.run();
//    } catch (Exception e) {
//      e.printStackTrace();
//      throw new UnsupportedOperationException(e);
//    }
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