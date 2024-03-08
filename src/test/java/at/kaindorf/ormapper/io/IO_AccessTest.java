package at.kaindorf.ormapper.io;

import at.kaindorf.ormapper.pojos.Airplane;
import at.kaindorf.ormapper.pojos.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IO_AccessTest {

  @Test
  @DisplayName("Test if  entity classes Airplane is found")
  void scanEntityClassesFromProject() {
    try {
      List<Class<?>> entityClasses = IO_Access.scanEntityClassesFromProject();
      Class<?> expected = Airplane.class;
      Class<?> actual = entityClasses.get(0);
      assertEquals(expected, actual);
    } catch (IOException e) {
      System.out.println(e.toString());
      fail();
    }

  }
}