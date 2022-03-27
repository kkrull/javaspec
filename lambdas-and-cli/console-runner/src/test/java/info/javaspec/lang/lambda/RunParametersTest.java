package info.javaspec.lang.lambda;

import com.beust.jcommander.JCommander;
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
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
public class RunParametersTest {
  public class parseCommand {
    private RunParameters subject;
    private CommandFactory commandFactory;
    private ReporterFactory reporterFactory;

    @Before
    public void setup() throws Exception {
      commandFactory = Mockito.mock(CommandFactory.class);
      reporterFactory = Mockito.mock(ReporterFactory.class);
      subject = new RunParameters(commandFactory, reporterFactory);
    }

    public class givenAllRequiredArguments {
      @Captor
      private ArgumentCaptor<List<URL>> runCommandUrls;

      @Before
      public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
      }

      @Test
      public void createsARunCommandWithAFileUrlToTheSpecifiedJar() throws Exception {
        JCommanderHelpers.parseCommandArgs(
          subject,
          "run",
          runArgumentsWithSpecClasspath("my-specs.jar", "spec-dependency.jar")
        );
        subject.toExecutableCommand(anyJCommander());

        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.any(RunObserver.class),
          runCommandUrls.capture(),
          Mockito.anyListOf(String.class)
        );

        List<URL> specsUrls = runCommandUrls.getValue();
        assertThat(specsUrls.get(0).getProtocol(), equalTo("file"));
        assertThat(specsUrls.get(0).getPath(), endsWith("/my-specs.jar"));
        assertThat(specsUrls.get(1).getPath(), endsWith("/spec-dependency.jar"));
      }

      @Test
      public void returnsTheRunCommand() throws Exception {
        Command toCreate = Mockito.mock(Command.class);
        Mockito.stub(commandFactory.runSpecsCommand(
          Mockito.any(RunObserver.class),
          Mockito.anyListOf(URL.class),
          Mockito.anyListOf(String.class)
        )).toReturn(toCreate);

        JCommanderHelpers.parseCommandArgs(subject, "run", runArguments());
        assertThat(subject.toExecutableCommand(anyJCommander()), sameInstance(toCreate));
      }
    }

    public class givenNoClassNames {
      @Test
      public void usesEmptyClassNames() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithClasses(Collections.emptyList()));
        subject.toExecutableCommand(anyJCommander());
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.any(RunObserver.class),
          Mockito.anyListOf(URL.class),
          Mockito.eq(Collections.emptyList())
        );
      }
    }

    public class givenOneOrMoreClassNames {
      @Test
      public void usesThoseClassNames() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", runArgumentsWithClasses(Arrays.asList("one", "two")));
        subject.toExecutableCommand(anyJCommander());
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.any(RunObserver.class),
          Mockito.anyListOf(URL.class),
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
        subject.toExecutableCommand(anyJCommander());
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.same(toCreate),
          Mockito.anyListOf(URL.class),
          Mockito.anyListOf(String.class)
        );
      }
    }

    public class givenAHelpOption {
      @Test
      public void returnsADetailedHelpCommandForRun() throws Exception {
        JCommanderHelpers.parseCommandArgs(subject, "run", Collections.singletonList("--help"));
        JCommander parser = anyJCommander();
        subject.toExecutableCommand(parser);
        Mockito.verify(commandFactory).helpCommand(
          Mockito.any(HelpObserver.class),
          Mockito.same(parser)
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

  private JCommander anyJCommander() {
    return Mockito.mock(JCommander.class);
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

  private List<String> runArgumentsWithSpecClasspath(String... specClasspath) {
    return Arrays.asList(
      "--reporter=plaintext",
      "--spec-classpath=" + String.join(":", specClasspath)
    );
  }

  private List<String> runArguments() {
    return Arrays.asList(
      "--reporter=plaintext",
      "--spec-classpath=specs.jar"
    );
  }
}
