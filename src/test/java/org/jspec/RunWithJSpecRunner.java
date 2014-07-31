package org.jspec;

import java.util.function.Consumer;

import org.jspec.dsl.It;
import org.junit.runner.RunWith;

@RunWith(JSpecRunner.class)
public class RunWithJSpecRunner {
  public static final Consumer<String> NOP = x -> {
    return;
  };
  public static Consumer<String> notifyEvent = NOP;
  
  public RunWithJSpecRunner() {
    notifyEvent.accept("RunWithJSpecRunner::new");
  }
  
  It only_test = () -> notifyEvent.accept("RunWithJSpecRunner::only_test");
}