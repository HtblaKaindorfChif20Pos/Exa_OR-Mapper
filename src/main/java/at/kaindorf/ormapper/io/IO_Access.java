package at.kaindorf.ormapper.io;

import at.kaindorf.ormapper.annotations.Entity;
import at.kaindorf.ormapper.pojos.Persistence;
import jakarta.xml.bind.JAXB;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 01.03.2024
 * Time: 08:45
 */
public class IO_Access {

  public static Persistence loadPersistenceUnit() {
    InputStream xmlFileInputStream = IO_Access.class.getResourceAsStream("/persistence.xml");
    return JAXB.unmarshal(xmlFileInputStream, Persistence.class);
  }

  /**
   * C:\local\schule\projekte\java_intellij\_exercise\Exa_OR-Mapper_4CHIF\src\main\java\at\kaindorf\ormapper\annotations\Entity.java
   * at.kaindorf.ormapper.annotations.Airplane.java
   */
  public static List<Class<?>> scanEntityClassesFromProject() throws IOException {
    Path sourcePath = Path.of(System.getProperty("user.dir"), "src", "main", "java");
    return Files.walk(sourcePath)
        .filter(p -> p.toString().endsWith(".java"))
        .map(sourcePath::relativize)
        .map(p -> p.toString().replace(File.separator,".").replace(".java",""))
        .map(IO_Access::getClassInfo)
        .filter(c -> c.isAnnotationPresent(Entity.class))
        .collect(Collectors.toList());
  }

  private static Class<?> getClassInfo(String classname) {
    try {
      return Class.forName(classname);
    } catch (ClassNotFoundException e) {
      return Object.class;
    }
  }
  public static void main(String[] args) {
    try {
      scanEntityClassesFromProject();
    } catch (IOException e) {
      System.out.println(e.toString());
    }
  }

}
