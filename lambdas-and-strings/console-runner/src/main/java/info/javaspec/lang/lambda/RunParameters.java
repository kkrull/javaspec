package info.javaspec.lang.lambda;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.BaseConverter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.MultiCommandParser;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Parameters(separators = "=")
public final class RunParameters implements MultiCommandParser.JCommanderParameters {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter(
    names = "--help",
    help = true
  )
  private boolean isAskingForHelp;

  @Parameter(
    names = "--reporter",
    required = true,
    validateValueWith = ReporterNameValidator.class
  )
  private String reporterName;

  @Parameter(
    converter = PathToFileUrl.class,
    names = "--spec-classpath",
    required = true
  )
  private URL _specClassPath;

  @Parameter
  private List<String> _specClassNames;

  public RunParameters(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command toExecutableCommand() {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    if(this.isAskingForHelp)
      return this.commandFactory.helpCommand(reporter, "run");

    return this.commandFactory.runSpecsCommand(
      reporter,
      specClassPath(),
      specClassNames()
    );
  }

  private List<String> specClassNames() {
    return Optional.ofNullable(this._specClassNames)
      .orElse(Collections.emptyList());
  }

  private URL specClassPath() {
    return this._specClassPath;
  }

  public static final class PathToFileUrl extends BaseConverter<URL> {
    public PathToFileUrl() {
      super("--spec-classpath");
    }

    @Override
    public URL convert(String pathToFileOrDirectory) {
      if(pathToFileOrDirectory.isEmpty()) {
        throw new ParameterException(String.format("%s: path may not be empty, but was <%s>",
          this.getOptionName(),
          pathToFileOrDirectory)
        );
      }

      URI uri = new File(pathToFileOrDirectory).toURI();
      try {
        return uri.toURL();
      } catch(MalformedURLException e) {
        throw new RuntimeException("Failed to parse URL", e);
      }
    }
  }

  public static final class ReporterNameValidator implements IValueValidator<String> {
    @Override
    public void validate(String parameterName, String value) throws ParameterException {
      if(!"plaintext".equals(value))
        throw new ParameterException(String.format("Unknown value for %s: %s", parameterName, value));
    }
  }
}
