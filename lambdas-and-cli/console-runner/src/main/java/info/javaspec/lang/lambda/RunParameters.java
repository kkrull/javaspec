package info.javaspec.lang.lambda;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Parameters(commandDescription = "Run specs", separators = "=")
public final class RunParameters implements MultiCommandParser.JCommanderParameters {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter(
    help = true,
    hidden = true,
    names = "--help"
  )
  private boolean isAskingForHelp;

  @Parameter(
    description = "Choose how specs are reported to the console: { plaintext }",
    names = "--reporter",
    required = true,
    validateValueWith = ReporterNameValidator.class
  )
  private String reporterName = "plaintext";

  @Parameter(
    description = "The classpath from which to load spec classes: either a directory of .class files or a .jar file.",
    listConverter = PathToFileUrl.class,
    names = "--spec-classpath",
    required = true
  )
  private List<URL> _specClassPath;

  @Parameter
  private List<String> _specClassNames;

  public RunParameters(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command toExecutableCommand(JCommander parser) {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    if(this.isAskingForHelp)
      return this.commandFactory.helpCommand(reporter, parser);

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

  private List<URL> specClassPath() {
    return new ArrayList<>(this._specClassPath);
  }

  public static final class PathToFileUrl extends BaseConverter<List<URL>> {
    public PathToFileUrl() {
      super("--spec-classpath");
    }

    @Override
    public List<URL> convert(String pathToFileOrDirectory) {
      if(pathToFileOrDirectory.isEmpty()) {
        throw new ParameterException(String.format("%s: path may not be empty, but was <%s>",
          getOptionName(),
          pathToFileOrDirectory)
        );
      }

      return Arrays.stream(pathToFileOrDirectory.split(":"))
        .map(this::parseUrl)
        .collect(Collectors.toList());
    }

    private URL parseUrl(String pathToFileOrDirectory) {
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
