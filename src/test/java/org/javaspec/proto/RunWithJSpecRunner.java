package org.javaspec.proto;

import org.javaspec.dsl.It;
import org.javaspec.runner.JSpecRunner;
import org.junit.runner.RunWith;

@RunWith(JSpecRunner.class)
public class RunWithJSpecRunner extends ExecutionSpy {
  public RunWithJSpecRunner() { notifyEvent.accept("RunWithJSpecRunner::new"); }
  It only_test = () -> notifyEvent.accept("RunWithJSpecRunner::only_test");
}