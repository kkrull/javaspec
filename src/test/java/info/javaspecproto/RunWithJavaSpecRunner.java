package info.javaspecproto;

import info.javaspec.dsl.It;
import info.javaspec.runner.ng.NewJavaSpecRunner;
import org.junit.runner.RunWith;

@RunWith(NewJavaSpecRunner.class)
public class RunWithJavaSpecRunner extends ExecutionSpy {
  public RunWithJavaSpecRunner() { notifyEvent.accept("RunWithJavaSpecRunner::new"); }
  It only_test = () -> notifyEvent.accept("RunWithJavaSpecRunner::only_test");
}