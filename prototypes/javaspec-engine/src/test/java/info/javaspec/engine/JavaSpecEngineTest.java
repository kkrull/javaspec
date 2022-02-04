package info.javaspec.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaSpecEngineTest {
  @Test
  public void sayHello() throws Exception {
    String greeting = "Hello world!";
    assertEquals("Hello world!", greeting);
  }
}
