package info.javaspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/** Loads configuration from a properties file, encoded in the given stream */
final class AppConfigGateway {
  private final Properties config;

  public static AppConfigGateway fromPropertyResource() {
    return fromPropertyResource("/javaspec.properties");
  }

  public static AppConfigGateway fromPropertyResource(String resourcePath) {
    return Optional.ofNullable(AppConfigGateway.class.getResourceAsStream(resourcePath))
      .map(AppConfigGateway::loadProperties)
      .map(AppConfigGateway::new)
      .orElseThrow(() -> new InvalidPropertiesException(resourcePath));
  }

  private AppConfigGateway(Properties config) {
    this.config = config;
  }

  public String version() {
    return Optional.ofNullable(config.getProperty("javaspec.version"))
      .orElseThrow(() -> new MissingPropertyException("javaspec.version"));
  }

  private static Properties loadProperties(InputStream stream) {
    Properties properties = new Properties();
    try {
      properties.load(stream);
    } catch(IOException e) {
      throw new PropertyLoadException(e);
    }

    return properties;
  }

  public static final class InvalidPropertiesException extends RuntimeException {
    public InvalidPropertiesException(String path) {
      super(String.format("Invalid property stream: %s", path));
    }
  }

  public static final class PropertyLoadException extends RuntimeException {
    public PropertyLoadException(Exception cause) {
      super("Failed to read properties", cause);
    }
  }

  public static final class MissingPropertyException extends RuntimeException {
    public MissingPropertyException(String name) {
      super(String.format("Missing property: %s", name));
    }
  }
}