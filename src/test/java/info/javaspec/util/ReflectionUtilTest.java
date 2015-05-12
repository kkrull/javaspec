package info.javaspec.util;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.EmptyClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(HierarchicalContextRunner.class)
public class ReflectionUtilTest {
  public class givenClassWithNoMatchingFields {
    @Test
    public void hasFieldsOfType_returnsFalse() {
      assertFalse(ReflectionUtil.hasFieldsOfType(MatchingType.class, EmptyClass.class));
      assertFalse(ReflectionUtil.hasFieldsOfType(MatchingType.class, EmptyNestedClass.class));
    }
    
    @Test
    public void fieldsOfType_returnsEmptyList() {
      assertFieldsOfType(MatchingType.class, EmptyClass.class);
      assertFieldsOfType(MatchingType.class, EmptyNestedClass.class);
    }
  }
  
  public class givenClassWith1OrMoreMatchingFieldsOfAnyVisibility {
    private final Class<?> toInspect = MixedFields.class;
    
    @Test
    public void fieldsOfType_returnsEachField() {
      assertFieldsOfType(MatchingType.class, toInspect, "matching");
    }
    
    @Test
    public void hasFieldsOfType_returnsTrue() {
      assertTrue(ReflectionUtil.hasFieldsOfType(MatchingType.class, toInspect));
    }
  }
  
  public class givenAClassWhereClassAndSuperClassFieldsMatch {
    public givenAClassWhereClassAndSuperClassFieldsMatch() {
      MatchingType value = new MatchingType();
      assumeTrue("The super type's field should be inherited", new SubType(value).superTypeField == value);
    }
    
    @Test
    public void fieldsOfType_excludesThoseFields() {
      assertFieldsOfType(MatchingType.class, SubType.class, "subTypeField");
    }
  }
  
  private static void assertFieldsOfType(Class<?> fieldType, Class<?> typeToInspect, String... names) {
    final Comparator<String> alphabetical = (x, y) -> x.compareTo(y);
    List<Field> fields = ReflectionUtil.fieldsOfType(fieldType, typeToInspect).collect(Collectors.toList());
    List<String> actualNames = fields.stream().map(Field::getName).sorted(alphabetical).collect(Collectors.toList());
    List<String> expectedNames = Arrays.asList(names).stream().sorted(alphabetical).collect(Collectors.toList());
    assertEquals(expectedNames, actualNames);
  }
  
  private class EmptyNestedClass { /* Not really devoid of fields; there is one pointing to the containing class */ }
  
  @SuppressWarnings("unused")
  private static class MixedFields {
    private MatchingType matching;
    public NonMatchingType nonMatching;
  }
  
  @SuppressWarnings("unused")
  private static class SubType extends SuperType {
    public MatchingType subTypeField;
    public SubType(MatchingType superTypeField) {
      this.superTypeField = superTypeField;
    }
  }
  
  private static class SuperType {
    public MatchingType superTypeField;
  }
  
  private static class MatchingType {}
  private static class NonMatchingType {}
}