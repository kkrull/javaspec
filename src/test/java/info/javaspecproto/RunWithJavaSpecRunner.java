package info.javaspecproto;

import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import org.junit.runner.RunWith;

@RunWith(JavaSpecRunner.class)
public class RunWithJavaSpecRunner extends ExecutionSpy {
  public RunWithJavaSpecRunner() { notifyEvent.accept("RunWithJavaSpecRunner::new"); }
  It only_test = () -> notifyEvent.accept("RunWithJavaSpecRunner::only_test");
}