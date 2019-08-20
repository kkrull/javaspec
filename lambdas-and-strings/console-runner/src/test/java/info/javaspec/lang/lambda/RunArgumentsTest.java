package info.javaspec.lang.lambda;

import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;
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
      public void createsARunCommandWithAFileURLToTheSpecifiedJar() throws Exception {
        subject.parseCommand(runArgumentsWithSpecClasspath("my-specs.jar"));
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

        assertThat(subject.parseCommand(runArguments()), sameInstance(toCreate));
      }
    }

    public class givenOneNoClassNames {
      @Test
      public void usesEmptyClassNames() throws Exception {
        subject.parseCommand(runArgumentsWithClasses(Collections.emptyList()));
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
        subject.parseCommand(runArgumentsWithClasses(Arrays.asList("one", "two")));
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

        subject.parseCommand(runArgumentsWithReporter("plaintext"));
        Mockito.verify(commandFactory).runSpecsCommand(
          Mockito.same(toCreate),
          Mockito.any(URL.class),
          Mockito.anyListOf(String.class)
        );
      }
    }

    public class givenAnyOtherReporterOption {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        subject.parseCommand(runArgumentsWithReporter("bogus"));
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
    return new LinkedList<String>(){{
      this.add("--reporter=plaintext");
      this.add("--spec-classpath=specs.jar");
      this.addAll(classNames);
    }};
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
