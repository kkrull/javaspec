package info.javaspec.console;

import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.console.ArgumentParser.InvalidCommand;
import info.javaspec.console.help.HelpObserver;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

@RunWith(HierarchicalContextRunner.class)
public class ArgumentParserTest {
  private Main.CommandParser subject;
  private CommandFactory factory;
  private Reporter reporter;

  @Before
  public void setup() throws Exception {
    factory = Mockito.mock(CommandFactory.class);
    reporter = Mockito.mock(Reporter.class);
    subject = new ArgumentParser(factory, () -> reporter);
  }

  public class parseCommand {
    public class givenNoArguments {
      @Test
      public void returnsHelpCommandWithTheReporter() throws Exception {
        subject.parseCommand(Collections.emptyList());
        Mockito.verify(factory).helpCommand(Mockito.same(reporter));
      }
    }

    public class givenAHelpOptionWithNoCommand {
      @Test
      public void returnsAHelpCommand() throws Exception {
        subject.parseCommand(Collections.singletonList("--help"));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter));
      }
    }

    public class givenHelpWithNoArguments {
      @Test
      public void returnsHelpCommandWithTheReporter() throws Exception {
        subject.parseCommand(Collections.singletonList("help"));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter));
      }
    }

    public class givenHelpWithACommand {
      @Test
      public void returnsHelpCommandForTheSpecifiedCommand() throws Exception {
        subject.parseCommand(Arrays.asList("help", "run"));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter), Mockito.eq("run"));
      }
    }

    public class givenRunWithNoArguments {
      @Test(expected = ParameterException.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Arrays.asList("run"));
      }
    }

    public class givenRunWithoutAReporterOption {
      @Test(expected = ParameterException.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Arrays.asList(
          "run",
          "--spec-classpath=specs.jar",
          "one"
        ));
      }
    }

    public class givenRunWithoutASpecClassPathOption {
      @Test(expected = ParameterException.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Arrays.asList(
          "run",
          "--reporter=plaintext",
          "one"
        ));
      }
    }

    public class givenRunWithAnInvalidReporterOption {
      @Test
      public void throwsAnError() throws Exception {
        try {
          subject.parseCommand(Arrays.asList(
            "run",
            "--reporter=bogus",
            "--spec-classpath=specs.jar"
          ));
        } catch(ParameterException e) {
          assertThat(e.getMessage(), equalTo("Unknown value for --reporter: bogus"));
          return;
        }

        fail("Expected exception: " + ParameterException.class.getName());
      }
    }

    public class givenRunWithAnInvalidSpecClasspath {
      @Test
      public void throwsAnError() throws Exception {
        try {
          subject.parseCommand(Arrays.asList(
            "run",
            "--reporter=plaintext",
            "--spec-classpath="
          ));
        } catch(ParameterException e) {
          assertThat(e.getMessage(), equalTo("--spec-classpath: path may not be empty, but was <>"));
          return;
        }

        fail("Expected exception: " + ParameterException.class.getName());
      }
    }

    public class givenRunWithAHelpOption {
      @Test
      public void returnsHelpCommandForTheRunCommand() throws Exception {
        subject.parseCommand(Arrays.asList("run", "--help"));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter), Mockito.eq("run"));
      }

      @Test
      public void allowsOtherRunOptions() throws Exception {
        subject.parseCommand(Arrays.asList(
          "run",
          "--reporter=plaintext",
          "--help"
        ));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter), Mockito.eq("run"));
      }
    }

    public class givenRunWithASpecClasspathThatHasNoValue {
      @Test
      public void throwsAnError() throws Exception {
        try {
          subject.parseCommand(Arrays.asList(
            "run",
            "--reporter=plaintext",
            "--spec-classpath"
          ));
        } catch(ParameterException e) {
          assertThat(e.getMessage(), equalTo("Expected a value after parameter --spec-classpath"));
          return;
        }

        fail("Expected exception: " + ParameterException.class.getName());
      }
    }

    public class givenAValidRunCommand {
      @Test
      public void passesTheSpecifiedSpecClassPathToCreateTheRunCommand() throws Exception {
        MockCommandFactory factory = new MockCommandFactory();
        subject = new ArgumentParser(factory, () -> reporter);

        subject.parseCommand(Arrays.asList(
          "run",
          "--reporter=plaintext",
          "--spec-classpath=specs.jar"
        ));
        factory.runSpecsCommandShouldHaveReceived(
          Matchers.sameInstance(reporter),
          Matchers.endsWith("/specs.jar"),
          Matchers.equalTo(new ArrayList<>())
        );
      }
    }

    public class givenAValidRunCommandAndZeroClassNames {
      @Test
      public void createsRunSpecsCommandWithNoClassNames() throws Exception {
        subject.parseCommand(Arrays.asList(
          "run",
          "--reporter=plaintext",
          "--spec-classpath=specs.jar"
        ));
        Mockito.verify(factory).runSpecsCommand(
          Mockito.same(reporter),
          Mockito.any(URL.class),
          Mockito.eq(Collections.emptyList())
        );
      }
    }

    public class givenAValidRunCommandAndOneOrMoreClassNames {
      @Test
      public void createsRunSpecsCommandWithTheRestOfTheArgsAsClassNames() throws Exception {
        subject.parseCommand(Arrays.asList(
          "run",
          "--reporter=plaintext",
          "--spec-classpath=specs.jar",
          "one"
        ));
        Mockito.verify(factory).runSpecsCommand(
          Mockito.same(reporter),
          Mockito.any(URL.class),
          Mockito.eq(Collections.singletonList("one"))
        );
      }
    }

    public class givenAnyOtherCommand {
      @Test(expected = InvalidCommand.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Collections.singletonList("bogus"));
      }
    }
  }

  private static final class MockCommandFactory implements CommandFactory {
    private RunObserver observer;
    private URL specClassPath;
    private List<String> classNames;

    @Override
    public Command helpCommand(HelpObserver observer) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Command helpCommand(HelpObserver observer, String forCommandNamed) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Command runSpecsCommand(RunObserver observer, URL specClassPath, List<String> classNames) {
      this.observer = observer;
      this.specClassPath = specClassPath;
      this.classNames = classNames;
      return Mockito.mock(Command.class);
    }

    public void runSpecsCommandShouldHaveReceived(
      Matcher<RunObserver> matchObserver,
      Matcher<String> matchSpecClassPath,
      Matcher<List<String>> matchClassNames
    ) {
      assertThat(this.observer, matchObserver);
      assertThat(this.specClassPath.toString(), matchSpecClassPath);
      assertThat(this.classNames, matchClassNames);
    }
  }
}
