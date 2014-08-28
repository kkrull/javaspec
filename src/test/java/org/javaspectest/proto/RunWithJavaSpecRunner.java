package org.javaspectest.proto;

import org.javaspec.dsl.It;
import org.javaspec.runner.JavaSpecRunner;
import org.junit.runner.RunWith;

@RunWith(JavaSpecRunner.class)
public class RunWithJavaSpecRunner extends ExecutionSpy {
  public RunWithJavaSpecRunner() { notifyEvent.accept("RunWithJavaSpecRunner::new"); }
  It only_test = () -> notifyEvent.accept("RunWithJavaSpecRunner::only_test");
}