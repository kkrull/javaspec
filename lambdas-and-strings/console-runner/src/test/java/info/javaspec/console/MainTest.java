package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.List;

@RunWith(HierarchicalContextRunner.class)
public class MainTest {
  private Main.ArgumentParser cliParser;
  private Command command;
  private Result result;

  private ReporterFactory reporterFactory;
  private Reporter reporter;

  private Main.ExitHandler system;

  public class main {
    public class givenValidArguments {
      @Before
      public void setup() {
        cliParser = Mockito.mock(Main.ArgumentParser.class);
        command = Mockito.mock(Command.class);
        result = Mockito.mock(Result.class);
        Mockito.stub(cliParser.parseCommand(matchAnyArguments()))
          .toReturn(command);
        Mockito.stub(command.run()).toReturn(result);

        reporterFactory = Mockito.mock(ReporterFactory.class);
        reporter = Mockito.mock(Reporter.class);
        Mockito.stub(reporterFactory.plainTextReporter())
          .toReturn(reporter);

        system = Mockito.mock(Main.ExitHandler.class);
      }

      @Test
      public void doesNotExplode_whichIsAlwaysAPlus() throws Exception {
        Main.main(cliParser, reporterFactory, system);
      }

      @Test
      public void runsTheCommand() throws Exception {
        Mockito.stub(command.run()).toReturn(Result.success());
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(command, Mockito.times(1)).run();
      }

      @Test
      public void exitsWithTheExitCodeReturnedByTheCommand() throws Exception {
        Result failure = Result.failure(42, "...you're not going to like it.");
        Mockito.stub(command.run()).toReturn(failure);
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(system, Mockito.times(1)).exit(42);
      }

      @Test
      public void reportsTheResult() throws Exception {
        Result result = Mockito.mock(Result.class);
        Mockito.stub(command.run()).toReturn(result);
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(result).reportTo(Mockito.same(reporter));
      }
    }

    public class givenInvalidArguments {
      @Before
      public void setup() {
        cliParser = Mockito.mock(Main.ArgumentParser.class);
        command = Mockito.mock(Command.class);
        result = Mockito.mock(Result.class);
        Mockito.stub(cliParser.parseCommand(matchAnyArguments()))
          .toReturn(command);
        Mockito.stub(command.run()).toReturn(result);

        reporterFactory = Mockito.mock(ReporterFactory.class);
        reporter = Mockito.mock(Reporter.class);

        system = Mockito.mock(Main.ExitHandler.class);
      }

      @Test
      public void shouldNotRunTheCommand() throws Exception {
        Mockito.doThrow(new RuntimeException("bang!"))
          .when(cliParser).parseCommand(matchAnyArguments());
        Main.main(cliParser, reporterFactory, system);
        Mockito.verifyZeroInteractions(command);
      }

      @Test @Ignore
      public void shouldReportAnErrorMessage() throws Exception {
      }

      @Test
      public void shouldExitWithTheProvidedSystemInterface() throws Exception {
        Mockito.doThrow(new RuntimeException("bang!"))
          .when(cliParser).parseCommand(matchAnyArguments());
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(system).exit(1);
      }
    }
  }

  private Result anyResult() {
    return Result.success();
  }

  private List<String> matchAnyArguments() {
    return Mockito.anyListOf(String.class);
  }
}
