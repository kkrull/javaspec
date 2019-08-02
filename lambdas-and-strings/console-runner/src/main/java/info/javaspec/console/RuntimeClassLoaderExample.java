package info.javaspec.console;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class RuntimeClassLoaderExample {
  public static void main(String[] args) throws Exception {
    String localPath = args[0];
    String className = args[1];

    URI uri = new File(localPath).toURI();
    System.out.println("uri = " + uri);
    URL localPathAsUrl = uri.toURL();
    System.out.printf("localPathAsUrl: %s\n", localPathAsUrl);

    ClassLoader runtimeLoader = new URLClassLoader(new URL[]{ localPathAsUrl });
    Class<?> runtimeClass = runtimeLoader.loadClass(className);
    System.out.printf("Loaded class: %s\n", runtimeClass.getName());
  }
}
