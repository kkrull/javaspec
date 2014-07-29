package org.jspec;

import java.lang.reflect.Field;

import org.junit.runner.Description;

final class Example {
  final Field behavior;
  
  public Example(Field behavior) {
    this.behavior = behavior;
  }
  
  public Description getDescription() {
    return Description.createTestDescription(behavior.getDeclaringClass(), behavior.getName());
  }
}
