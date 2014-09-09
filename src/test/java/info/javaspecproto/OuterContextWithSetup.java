package info.javaspecproto;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import info.javaspec.dsl.Because;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;

import java.util.LinkedList;
import java.util.List;

public class OuterContextWithSetup {
  private List<String> context = new LinkedList<String>();
  
  Establish arrange = () -> context.add("OuterContextWithSetup::arrange");
  Because act = () -> context.add("OuterContextWithSetup::act");
  It asserts = () -> assertThat(context, contains("OuterContextWithSetup::arrange", "OuterContextWithSetup::act"));
  
  public class InnerContextWithSetup {
    Establish arrange = () -> context.add("InnerContextWithSetup::arrange");
    Because act = () -> context.add("InnerContextWithSetup::act");
    It asserts = () -> assertThat(context, contains(
      "OuterContextWithSetup::arrange", "OuterContextWithSetup::act",
      "InnerContextWithSetup::arrange", "InnerContextWithSetup::act"));
  }
}