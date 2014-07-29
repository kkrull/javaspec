package org.jspec;

import java.lang.reflect.Field;
import java.util.List;

import org.jspec.dsl.It;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JSpecRunner extends ParentRunner<Example> {

  protected JSpecRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }
  
  @Override
  protected void collectInitializationErrors(List<Throwable> errors) {
    super.collectInitializationErrors(errors);
    Class<?> contextClass = getTestClass().getJavaClass();
    if(readBehaviorFields(contextClass).isEmpty()) {
      errors.add(new InitializationError("A JSpec class must declare 1 or more It fields, or be deemed an atomic test"));
    }
  }

  @Override
  public Description getDescription() {
    Class<?> contextClass = getTestClass().getJavaClass();
    Description contextDescription = Description.createSuiteDescription(contextClass);
    for(Field itField : readBehaviorFields(contextClass)) {
      Description childDescription = Description.createTestDescription(contextClass, itField.getName());
      contextDescription.addChild(childDescription);
    }
    
    return contextDescription;
  }

  @Override
  protected List<Example> getChildren() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Description describeChild(Example child) {
    System.out.println("describeChild");
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    System.out.println("runChild");
    throw new UnsupportedOperationException();
  }
  
  List<Field> readBehaviorFields(Class<?> contextClass) {
    return ReflectionUtil.fieldsOfType(It.class, contextClass);
  }
}