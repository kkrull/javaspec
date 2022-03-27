package info.javaspec.console;

import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Exceptions.InvalidArguments;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
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
        command = Mockito.mock(Command.class);
        result = Mockito.mock(Result.class);
        Mockito.stub(command.run())
          .toReturn(result);

        cliParser = Mockito.mock(Main.ArgumentParser.class);
        Mockito.stub(cliParser.parseCommand(anyArguments()))
          .toReturn(command);

        reporterFactory = Mockito.mock(ReporterFactory.class);
        reporter = Mockito.mock(Reporter.class);
        Mockito.stub(reporterFactory.plainTextReporter())
          .toReturn(reporter);

        system = Mockito.mock(Main.ExitHandler.class);
      }

      @Test
      public void doesNotExplodeWhichIsAlwaysAPlus() throws Exception {
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
        Mockito.stub(command.run()).toReturn(result);
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(result).reportTo(Mockito.same(reporter));
      }
    }

    public class givenInvalidArguments {
      private InvalidArguments parseCommandException;

      @Before
      public void setup() {
        cliParser = Mockito.mock(Main.ArgumentParser.class);
        parseCommandException = InvalidArguments.dueTo(new ParameterException("looked like it was on"));
        Mockito.doThrow(parseCommandException)
          .when(cliParser).parseCommand(anyArguments());

        command = Mockito.mock(Command.class);
        result = Mockito.mock(Result.class);
        Mockito.stub(command.run())
          .toReturn(result);

        reporterFactory = Mockito.mock(ReporterFactory.class);
        reporter = Mockito.mock(Reporter.class);
        Mockito.stub(reporterFactory.plainTextReporter())
          .toReturn(reporter);

        system = Mockito.mock(Main.ExitHandler.class);
      }

      @Test
      public void shouldNotRunTheCommand() throws Exception {
        Main.main(cliParser, reporterFactory, system);
        Mockito.verifyZeroInteractions(command);
      }

      @Test
      public void shouldReportAnErrorMessage() throws Exception {
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(reporter).invalidArguments(Matchers.same(parseCommandException));
      }

      @Test
      public void shouldExitWithTheProvidedSystemInterface() throws Exception {
        Main.main(cliParser, reporterFactory, system);
        Mockito.verify(system).exit(1);
      }
    }
  }

  private List<String> anyArguments() {
    return Mockito.anyListOf(String.class);
  }
}
