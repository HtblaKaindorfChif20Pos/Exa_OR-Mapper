package at.kaindorf.ormapper.database;

import at.kaindorf.ormapper.pojos.Persistence;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 06.03.2024
 * Time: 09:35
 */
public class SQLDataTypes {
  public static final Map<Class<?>, String> SQL_TYPES = Map.ofEntries(
      Map.entry(Integer.class, "INT"),
      Map.entry(int.class, "INT"),
      Map.entry(Long.class, "BIGINT"),
      Map.entry(long.class, "BIGINT"),
      Map.entry(Double.class, "FLOAT8"),
      Map.entry(double.class, "FLOAT8"),
      Map.entry(Boolean.class, "BOOL"),
      Map.entry(boolean.class, "BOOL"),
      Map.entry(LocalDate.class, "DATE"),
      Map.entry(LocalTime.class, "TIME"),
      Map.entry(LocalDateTime.class, "TIMESTAMP"),
      Map.entry(String.class, "VARCHAR(255)")
  );

  public static final List<Class<?>> TYPES_WITH_TICKS = List.of(
      String.class,
      LocalDate.class,
      LocalTime.class,
      LocalDateTime.class
  );

  public static final Map<Class<?>, Class<?>> TO_WRAPPER = Map.of(
      boolean.class, Boolean.class,
      int.class, Integer.class,
      long.class, Long.class,
      float.class, Float.class,
      double.class, Double.class);
}

