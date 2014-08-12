package org.jspec.runner;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.jspec.util.RunListenerSpy;
import org.jspec.util.RunListenerSpy.Event;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public final class Runners {
  public static List<Throwable> initializationErrorCauses(TestConfiguration config) {
    try {
      new JSpecRunner(config);
    } catch (InitializationError ex) {
      return Runners.flattenCauses(ex);
    }
    return null;
  }
  
  public static JSpecRunner of(Class<?> contextClass) {
    try {
      return new JSpecRunner(contextClass);
    } catch (InitializationError e) {
      return failForInitializationError(e);
    }
  }
  
  public static JSpecRunner of(TestConfiguration config) {
    try {
      return new JSpecRunner(config);
    } catch (InitializationError e) {
      return failForInitializationError(e);
    }
  }
  
  public static void runAll(Runner runner, Consumer<Event> notifyEvent) {
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListenerSpy(notifyEvent));
    runner.run(notifier);
  }
  
  private static JSpecRunner failForInitializationError(InitializationError e) {
    System.out.println("\nInitialization error(s)");
    flattenCauses(e).forEach(x -> {
      System.out.printf("[%s]\n", x.getClass());
      x.printStackTrace(System.out);
    });
    fail("Failed to create JSpecRunner");
    return null; //Not really returning; just more convenient to use at call sites
  }
  
  private static List<Throwable> flattenCauses(InitializationError root) {
    List<Throwable> causes = new LinkedList<Throwable>();
    Stack<InitializationError> nodesWithChildren = new Stack<InitializationError>();
    nodesWithChildren.push(root);
    while (!nodesWithChildren.isEmpty()) {
      InitializationError parent = nodesWithChildren.pop();
      for(Throwable child : parent.getCauses()) {
        if(child instanceof InitializationError) {
          nodesWithChildren.push((InitializationError) child);
        } else {
          causes.add(child);
        }
      }
    }
    return causes;
  }
}