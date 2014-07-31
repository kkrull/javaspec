package org.jspec;

import java.util.function.Consumer;

import org.jspec.dsl.It;
import org.junit.runner.RunWith;

@RunWith(JSpecRunner.class)
public class RunWithJSpecRunner {
  private static final Consumer<String> NOP = x -> { return; };
  private static Consumer<String> notifyEvent = NOP;
  
  public static void setEventListener(Consumer<String> newConsumer) {
    notifyEvent = newConsumer == null ? NOP : newConsumer;
  }
  
  public RunWithJSpecRunner() {
    notifyEvent.accept("RunWithJSpecRunner::new");
  }
  
  It only_test = () -> notifyEvent.accept("RunWithJSpecRunner::only_test");
}