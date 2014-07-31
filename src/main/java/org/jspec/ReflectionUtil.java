package org.jspec;

import java.lang.reflect.Field;
import java.util.stream.Stream;

final class ReflectionUtil {

  public static Stream<Field> fieldsOfType(Class<?> fieldType, Class<?> typeToInspect) {
    return Stream.of(typeToInspect.getDeclaredFields())
      .filter(x -> x.getType() == fieldType);
  }
}