package org.javaspec.runner;

import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
interface ExampleFactory {
  NewExample makeExample(Class<?> contextClass, Field it, List<Field> runBefore, List<Field> runAfter);
}