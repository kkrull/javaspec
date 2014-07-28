package org.jspec;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public final class ReflectionUtil {

  public static List<Field> fieldsOfType(Class<?> fieldType, Class<?> typeToInspect) {
    List<Field> matchingFields = new LinkedList<Field>();
    for (Field field : typeToInspect.getDeclaredFields()) {
      if (field.getType() != fieldType) {
        continue;
      }

      matchingFields.add(field);
    }
    
    return matchingFields;
  }
}