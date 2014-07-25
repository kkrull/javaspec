package org.jspec;

import static org.junit.Assert.*;

public class JSpecTests {
  It runs = () -> assertEquals(1, 1);
  
  @FunctionalInterface
  interface It {
    public void run() throws Exception;
  }
}