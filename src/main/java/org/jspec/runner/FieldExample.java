package org.jspec.runner;

import java.lang.reflect.Field;

import org.jspec.dsl.It;
import org.junit.runner.Description;

final class FieldExample implements Example {
  private final Field behavior;
  
  public FieldExample(Field behavior) {
    this.behavior = behavior;
  }
  
  @Override
  public String describeBehavior() {
    return behavior.getName();
  }
  
  public Description getDescription() { //TODO KDK: Get rid of this when JSpecRunner refactoring complete
    return Description.createTestDescription(behavior.getDeclaringClass(), behavior.getName());
  }
  
  @Override
  public void run(Object objectDeclaringBehavior) throws Exception {
    behavior.setAccessible(true);
    It thunk = (It)behavior.get(objectDeclaringBehavior);
    thunk.run();
  }
}