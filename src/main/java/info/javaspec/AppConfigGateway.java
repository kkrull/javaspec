package info.javaspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Loads configuration from a properties file, encoded in the given stream */
final class AppConfigGateway {
  public static AppConfigGateway fromPropertyResource() {
    return null;
  }

  public static AppConfigGateway fromPropertyResource(String resourcePath) {
    if(resourcePath == null)
      throw new InvalidPropertiesException(resourcePath);

    InputStream propertiesStream = AppConfigGateway.class.getResourceAsStream(resourcePath);
    Properties properties = new Properties();
    try {
      properties.load(propertiesStream);
    } catch(IOException e) {
      throw new RuntimeException("Failed to read properties", e);
    }
    return null;
  }

  private AppConfigGateway() {
  }

  public String version() {
    Properties properties = new Properties();
    try {
      InputStream propertiesStream = getClass().getResourceAsStream("/javaspec.properties");
      properties.load(propertiesStream);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }

    return properties.getProperty("javaspec.version");
  }

  public static class InvalidPropertiesException extends RuntimeException {
    public InvalidPropertiesException(String path) {
      super(String.format("Invalid property stream: %s", path));
    }
  }
}