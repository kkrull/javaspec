package info.javaspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Loads configuration from a properties file, encoded in the given stream */
final class AppConfigGateway {
  public static AppConfigGateway fromPropertyStream(InputStream input) {
    Properties props = new Properties();
    try {
      props.load(input);
    } catch(Exception e) {
      throw new InvalidPropertiesException(input);
    }
    return null;
  }

  public static AppConfigGateway fromProperties(Properties properties) {
    return new AppConfigGateway();
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
    public InvalidPropertiesException(InputStream s) {
      super(String.format("Invalid property stream: %s", s));
    }
  }
}