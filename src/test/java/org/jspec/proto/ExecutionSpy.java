package org.jspec.proto;

import java.util.function.Consumer;

abstract class ExecutionSpy {
  private static final Consumer<String> NOP = x -> { return; };
  protected static Consumer<String> notifyEvent = NOP;
  
  public static void setEventListener(Consumer<String> newConsumer) {
    notifyEvent = newConsumer == null ? NOP : newConsumer;
  }
}