package info.javaspec.lang.lambda;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.BaseConverter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Parameters(separators = "=")
public final class RunArguments {
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

  public List<String> specClassNames() {
    return Optional.ofNullable(this._specClassNames)
      .orElse(Collections.emptyList());
  }

  public URL specClassPath() {
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
