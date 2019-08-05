package info.javaspec.lang.lambda;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

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

  @Parameter
  private List<String> _specClassNames;

  public List<String> specClassNames() {
    return Optional.ofNullable(this._specClassNames)
      .orElse(Collections.emptyList());
  }

  public static final class ReporterNameValidator implements IValueValidator<String> {
    @Override
    public void validate(String parameterName, String value) throws ParameterException {
      if(!"plaintext".equals(value))
        throw new ParameterException(String.format("Unknown value for %s: %s", parameterName, value));
    }
  }
}
