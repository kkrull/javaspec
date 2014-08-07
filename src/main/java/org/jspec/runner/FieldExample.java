package org.jspec.runner;

import java.lang.reflect.Field;

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
  public void run() throws Exception {
//    behavior.setAccessible(true);
//    It thunk = (It)behavior.get();
//    thunk.run();
  }
}