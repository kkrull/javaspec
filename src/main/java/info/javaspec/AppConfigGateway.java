package info.javaspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class AppConfigGateway {
  public String version() {
    Properties properties = new Properties();
    try {
      InputStream propertiesStream = getClass().getResourceAsStream("/javaspec.properties");
      properties.load(propertiesStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return properties.getProperty("javaspec.version");
  }
}