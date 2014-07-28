package org.jspec;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class ReflectionUtilTests {

  @Test
  public void fieldsOfType_givenClassWithNoMatchingFields_returnsEmptyList() {
    assertEquals(Lists.newArrayList(), ReflectionUtil.fieldsOfType(MatchingType.class, EmptyClass.class));
    assertEquals(Lists.newArrayList(), ReflectionUtil.fieldsOfType(MatchingType.class, EmptyNestedClass.class));
  }
  
  @Test
  public void fieldsOfType_givenClassWith1OrMoreMatchingFields_returnsEachField() {
    List<Field> returned = ReflectionUtil.fieldsOfType(MatchingType.class, MixedFields.class);
    List<String> returnedNames = Lists.transform(returned, Field::getName);
    assertEquals(Lists.newArrayList("matching"), returnedNames);
  }
  
  @Test
  public void fieldsOfType_givenMatchingDefaultVisibilityField_returnsMatchingField() {
    fail("pending");
  }
  
  class EmptyNestedClass { /* Not devoid of fields; there is 1 pointing to the containing class */ }
  
  class MixedFields {
    public MatchingType matching;
    public NonMatchingType nonMatching;
  }
  
  class MatchingType {}
  class NonMatchingType {}
}