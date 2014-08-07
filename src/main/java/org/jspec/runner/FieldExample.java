package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jspec.dsl.It;

final class FieldExample implements Example {
  private final Field behavior;
  
  public FieldExample(Field behavior) {
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
      Constructor<?> noArgConstructor = contextClass.getConstructors()[0];
      noArgConstructor.setAccessible(true);
      context = noArgConstructor.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      throw new UnsupportedOperationException(e);
    }
    
    It thunk;
    try {
      behavior.setAccessible(true);
      thunk = (It)behavior.get(context);
    } catch (Exception e) {
      throw new UnsupportedFieldException(behavior);
    }
    
    try {
      thunk.run();
    } catch (Exception e) {
      e.printStackTrace();
      throw new UnsupportedOperationException(e);
    }
  }
  
  public static final class UnsupportedFieldException extends RuntimeException {
    private static final long serialVersionUID = 4899732341344955353L;

    public UnsupportedFieldException(Field f) {
      super(String.format("Invalid type for %s.%s: %s", 
        f.getDeclaringClass().getName(), 
        f.getName(),
        f.getType().getName()));
    }
  }
}