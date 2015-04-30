package info.javaspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Loads configuration from a properties file, encoded in the given stream */
final class AppConfigGateway {
  private final Properties config;

  public static AppConfigGateway fromPropertyResource() {
    return fromPropertyResource("/javaspec.properties");
  }

  public static AppConfigGateway fromPropertyResource(String resourcePath) {
    if(resourcePath == null)
      throw new InvalidPropertiesException(resourcePath);

    InputStream propertiesStream = AppConfigGateway.class.getResourceAsStream(resourcePath);
    if(propertiesStream == null)
      throw new InvalidPropertiesException(resourcePath);

    Properties properties = new Properties();
    try {
      properties.load(propertiesStream);
    } catch(IOException e) {
      throw new RuntimeException("Failed to read properties", e);
    }

    return new AppConfigGateway(properties);
  }

  private AppConfigGateway(Properties config) {
    this.config = config;
  }

  public String version() {
    String version = config.getProperty("javaspec.version");
    if(version == null)
      throw new MissingPropertyException("javaspec.version");
    return version;
  }

  public static final class InvalidPropertiesException extends RuntimeException {
    public InvalidPropertiesException(String path) {
      super(String.format("Invalid property stream: %s", path));
    }
  }

  public static final class MissingPropertyException extends RuntimeException {
    public MissingPropertyException(String name) {
      super(String.format("Missing property: %s", name));
    }
  }
}