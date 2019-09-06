package info.javaspec.lang.lambda;

import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.JCommanderHelpers;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;
import info.javaspec.console.help.HelpObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class RunArgumentsTest {
  public class parseCommand {
    private RunArguments subject;
    private CommandFactory commandFactory;
    private ReporterFactory reporterFactory;

    private ArgumentCaptor<URL> runCommandUrl;

    @Before
    public void setup() throws Exception {
      runCommandUrl = ArgumentCaptor.forClass(URL.class);
      commandFactory = Mockito.mock(CommandFactory.class);
      reporterFactory = Mockito.mock(ReporterFactory.class);
      subject = new RunArguments(commandFactory, reporterFactory);
    }

    public class givenAllRequiredArguments {
      @Test
      public void createsARunCommandWithAFileUrlToTheSpecifiedJar() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithSpecClasspath("my-specs.jar"));
        subject.toExecutableCommand();

        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.any(RunObserver.class),
          runCommandUrl.capture(),
          Mockito.anyListOf(String.class)
        );

        URL specsUrl = runCommandUrl.getValue();
        assertThat(specsUrl.getProtocol(), equalTo("file"));
        assertThat(specsUrl.getPath(), endsWith("/my-specs.jar"));
      }

      @Test
      public void returnsTheRunCommand() throws Exception {
        Command toCreate = Mockito.mock(Command.class);
        Mockito.stub(commandFactory.runSpecsCommand(
          Mockito.any(RunObserver.class),
          Mockito.any(URL.class),
          Mockito.anyListOf(String.class)
        )).toReturn(toCreate);

        JCommanderHelpers.parseCommandArgs(subject, "run", runArguments());
        assertThat(subject.toExecutableCommand(), sameInstance(toCreate));
      }
    }

    public class givenNoClassNames {
      @Test
      public void usesEmptyClassNames() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithClasses(Collections.emptyList()));
        subject.toExecutableCommand();
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.any(RunObserver.class),
          Mockito.any(URL.class),
          Mockito.eq(Collections.emptyList())
        );
      }
    }

    public class givenOneOrMoreClassNames {
      @Test
      public void usesThoseClassNames() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithClasses(Arrays.asList("one", "two")));
        subject.toExecutableCommand();
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.any(RunObserver.class),
          Mockito.any(URL.class),
          Mockito.eq(Arrays.asList("one", "two"))
        );
      }
    }

    public class givenAReporterNamedPlaintext {
      @Test
      public void usesAPlaintextReporter() throws Exception {
        Reporter toCreate = Mockito.mock(Reporter.class);
        Mockito.stub(reporterFactory.plainTextReporter()).toReturn(toCreate);

        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithReporter("plaintext"));
        subject.toExecutableCommand();
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.same(toCreate),
          Mockito.any(URL.class),
          Mockito.anyListOf(String.class)
        );
      }
    }

    public class givenAHelpOption {
      @Test
      public void returnsADetailedHelpCommandForRun() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", Collections.singletonList("--help"));
        subject.toExecutableCommand();
        Mockito.verify(commandFactory).helpCommand(
          Mockito.any(HelpObserver.class),
          Mockito.eq("run")
        );
      }
    }

    public class givenAnyOtherReporterOption {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithReporter("bogus"));
      }
    }
  }

  private List<String> runArgumentsWithReporter(String reporterName) {
    return Arrays.asList(
      "--reporter=" + reporterName,
      "--spec-classpath=specs.jar"
    );
  }

  private List<String> runArgumentsWithClasses(List<String> classNames) {
    return new LinkedList<String>() {
      {
        this.add("--reporter=plaintext");
        this.add("--spec-classpath=specs.jar");
        this.addAll(classNames);
      }
    };
  }

  private List<String> runArgumentsWithSpecClasspath(String specClasspath) {
    return Arrays.asList(
      "--reporter=plaintext",
      "--spec-classpath=" + specClasspath
    );
  }

  private List<String> runArguments() {
    return Arrays.asList(
      "--reporter=plaintext",
      "--spec-classpath=specs.jar"
    );
  }
}
