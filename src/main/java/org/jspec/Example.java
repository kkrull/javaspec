package org.jspec;

import java.lang.reflect.Field;

import org.jspec.dsl.It;
import org.junit.runner.Description;

final class Example {
  final Field behavior;
  
  public Example(Field behavior) {
    this.behavior = behavior;
  }
  
  public Description getDescription() {
    return Description.createTestDescription(behavior.getDeclaringClass(), behavior.getName());
  }
  
  public void run(Object objectDeclaringBehavior) throws Exception {
    It thunk = (It)behavior.get(objectDeclaringBehavior);
    thunk.run();
  }
}