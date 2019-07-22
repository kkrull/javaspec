package info.javaspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/** Loads configuration from a properties file, encoded in the given stream. */
final class AppConfigGateway {
  private final Properties config;

  public static AppConfigGateway fromPropertyResource() {
    return fromPropertyResource("/javaspec.properties");
  }

  public static AppConfigGateway fromPropertyResource(String resourcePath) {
    return Optional.ofNullable(AppConfigGateway.class.getResourceAsStream(resourcePath))
      .map(AppConfigGateway::loadProperties)
      .map(AppConfigGateway::new)
      .orElseThrow(() -> new InvalidProperties(resourcePath));
  }

  private AppConfigGateway(Properties config) {
    this.config = config;
  }

  public String version() {
    return Optional.ofNullable(config.getProperty("javaspec.version"))
      .orElseThrow(() -> new MissingProperty("javaspec.version"));
  }

  private static Properties loadProperties(InputStream stream) {
    Properties properties = new Properties();
    try {
      properties.load(stream);
    } catch(IOException e) {
      throw new PropertyLoadFailed(e);
    }

    return properties;
  }

  public static final class InvalidProperties extends RuntimeException {
    public InvalidProperties(String path) {
      super(String.format("Invalid property stream: %s", path));
    }
  }

  public static final class PropertyLoadFailed extends RuntimeException {
    public PropertyLoadFailed(Exception cause) {
      super("Failed to read properties", cause);
    }
  }

  public static final class MissingProperty extends RuntimeException {
    public MissingProperty(String name) {
      super(String.format("Missing property: %s", name));
    }
  }
}