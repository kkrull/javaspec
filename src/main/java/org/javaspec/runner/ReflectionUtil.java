package org.javaspec.runner;

import java.lang.reflect.Field;
import java.util.stream.Stream;

final class ReflectionUtil {
  public static Stream<Field> fieldsOfType(Class<?> fieldType, Class<?> typeToInspect) {
    return Stream.of(typeToInspect.getDeclaredFields())
      .filter(x -> x.getType() == fieldType);
  }
  
  public static boolean hasFieldsOfType(Class<?> fieldType, Class<?> typeToInspect) {
    return fieldsOfType(fieldType, typeToInspect).findAny().isPresent();
  }
}