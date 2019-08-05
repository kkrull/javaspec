package info.javaspec.lang.lambda;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Parameters(separators = "=")
public final class RunArguments {
  @Parameter(names = "--reporter", required = true)
  private String reporterName;

  @Parameter
  private List<String> _specClassNames;

  public String reporterName() {
    return this.reporterName;
  }

  public List<String> specClassNames() {
    return Optional.ofNullable(this._specClassNames)
      .orElse(Collections.emptyList());
  }
}
