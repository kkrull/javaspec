package info.javaspec.runner;

import info.javaspec.testutil.RunListenerSpy;
import info.javaspec.testutil.RunListenerSpy.Event;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.function.Consumer;

public final class Runners {
  public static void runAll(Runner runner, Consumer<Event> notifyEvent) {
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListenerSpy(notifyEvent));
    runner.run(notifier);
  }
}