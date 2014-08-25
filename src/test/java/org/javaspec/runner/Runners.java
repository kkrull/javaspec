package org.javaspec.runner;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.javaspec.testutil.RunListenerSpy;
import org.javaspec.testutil.RunListenerSpy.Event;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public final class Runners {
  public static List<Throwable> initializationErrorCauses(ExampleGateway gateway) {
    try {
      new JavaSpecRunner(gateway);
    } catch (InitializationError ex) {
      return Runners.flattenCauses(ex);
    }
    return null;
  }
  
  public static JavaSpecRunner of(Class<?> contextClass) {
    try {
      return new JavaSpecRunner(contextClass);
    } catch (InitializationError e) {
      return failForInitializationError(e);
    }
  }
  
  public static JavaSpecRunner of(ExampleGateway gateway) {
    try {
      return new JavaSpecRunner(gateway);
    } catch (InitializationError e) {
      return failForInitializationError(e);
    }
  }
  
  public static void runAll(Runner runner, Consumer<Event> notifyEvent) {
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListenerSpy(notifyEvent));
    runner.run(notifier);
  }
  
  private static JavaSpecRunner failForInitializationError(InitializationError e) {
    System.out.println("\nInitialization error(s)");
    flattenCauses(e).forEach(x -> {
      System.out.printf("[%s]\n", x.getClass());
      x.printStackTrace(System.out);
    });
    
    String causes = flattenCauses(e).stream()
      .map(x -> String.format("[%s] %s", x.getClass().getName(), x.getMessage()))
      .collect(joining("\n- "));
    String msg = String.format("Failed to create JavaSpecRunner due to initialization errors:\n- %s\n", causes);
    fail(msg);
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