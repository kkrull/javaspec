package org.javaspec.runner;

import java.lang.reflect.Field;

@FunctionalInterface
interface ExampleFactory {
  NewExample makeExample(Class<?> contextClass, Field it);
}