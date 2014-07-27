package org.jspec;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

//@RunWith(JSpecRunner.class)
//Leave off @RunWith; otherwise this will run as part of JSpec's test suite instead of being used as an example.
public class JSpecExample {
  It runs = () -> assertEquals(1, 1);
  
  @FunctionalInterface
  interface It {
    public void run() throws Exception;
  }
}

//public class JSpecExample {
//  
//  @Test
//  public void nativeJUnitTest() {
//    assertEquals(42, 42);
//  }
//}