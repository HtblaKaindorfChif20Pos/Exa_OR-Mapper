package at.kaindorf.ormapper.database;

import at.kaindorf.ormapper.annotations.Column;
import at.kaindorf.ormapper.annotations.Entity;
import at.kaindorf.ormapper.annotations.Id;
import at.kaindorf.ormapper.io.IO_Access;
import at.kaindorf.ormapper.pojos.Airplane;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static at.kaindorf.ormapper.database.SQLDataTypes.SQL_TYPES;
import static at.kaindorf.ormapper.database.SQLDataTypes.TYPES_WITH_TICKS;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 06.03.2024
 * Time: 08:55
 */
public class EntityManager {

  private DB_Access dbAccess = DB_Access.getInstance();
  private Statement statement;

  public EntityManager() {
    try {
      List<Class<?>> entityClasses = IO_Access.scanEntityClassesFromProject();
      statement = dbAccess.getStatement();
      if (dbAccess.getDbDdlSchema().equals("drop-and-create")) {
        entityClasses.forEach(this::deleteTable);
        entityClasses.forEach(this::createTable);
      }
    } catch (IOException e) {
      System.out.println("Failed scanning entity classes");
      throw new RuntimeException(e);
    } catch (SQLException e) {
      System.out.println("Statement creation failed");
      throw new RuntimeException(e);
    }
  }

  public void deleteTable(Class<?> entityClass) {
    String tableName = getEntityTableName(entityClass);
    String sqlString = "DROP TABLE IF EXISTS " + tableName;
    try {
      statement.execute(sqlString);
      System.out.println("Table " + tableName + " dropped");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create new table in postgres database from entity-class:
   * CREATE TABLE table_name (
   * column1 datatype(length) column_constraint,
   * column2 datatype(length) column_constraint,
   * ...
   * table_constraints
   * );
   *
   * @param entityClass
   */
  public void createTable(Class<?> entityClass) {
    // entry point for reflections:
//    Class<?> clazz1 = Airplane.class;
//    Class<?> clazz2 = entityClass.getClass();

    String tableName = getEntityTableName(entityClass);
    String createTableString = String.format("CREATE TABLE %s (\n ", tableName);

    for (Field field : entityClass.getDeclaredFields()) {
      String columnName = field.getName().toLowerCase();
      String datatype = SQL_TYPES.get(field.getType());
      String constraint = "";

      constraint += field.isAnnotationPresent(Id.class) ? " PRIMARY KEY" : "";

      if (field.isAnnotationPresent(Column.class)) {
        Column column = field.getDeclaredAnnotation(Column.class);
        // change columnname if required
        String name = column.name();
        columnName = name.isBlank() ? columnName : name;

        // insert NOT NULL and UNIQUE if required
        constraint += column.nullable() ? "" : " NOT NULL";
        constraint += column.unique() ? " UNIQUE" : "";

        // change VARCHAR-length if required;
        if (field.getType().equals(String.class) && column.length() != 255) {
          datatype = datatype.replace("255", column.length() + "");
        }
      }
      createTableString += String.format("%s %s%s,\n ", columnName, datatype, constraint);
    }
    createTableString = createTableString.substring(0, createTableString.lastIndexOf(",")) + ");";
    try {
      statement.execute(createTableString);
    } catch (SQLException e) {
      System.out.println(e.toString());
      throw new RuntimeException(e);
    }
    System.out.println(createTableString);
    System.out.printf("Table %s created\n", tableName);
  }

  /**
   * persist entity-object to database:
   * INSERT INTO tablename (fieldname, ...) VALUES (fieldvalue, ...);
   */
  public void persist(Object entityObject) {
    Class<?> entityClass = entityObject.getClass();
    // check if object is from supported entity-class:
    if (!entityClass.isAnnotationPresent(Entity.class)) {
      throw new RuntimeException("Entity-class " + entityClass.getSimpleName() + " is not supported");
    }
    String tableName = getEntityTableName(entityClass);
    List<String> fieldNames = new ArrayList<>();
    List<String> fieldValues = new ArrayList<>();

    // ToDo generate content for fieldNames and fieldValues
    fieldNames = Arrays.stream(entityClass.getDeclaredFields())
        .map(field -> {
          if (field.isAnnotationPresent(Column.class) &&
              !field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
          }
          return field.getName();
        })
        .map(String::toLowerCase)
        .collect(Collectors.toList());

    for(Field field : entityClass.getDeclaredFields()) {
      try {
        field.setAccessible(true);
        String fieldValue = field.get(entityObject).toString();
        if (TYPES_WITH_TICKS.contains(field.getType())) {
          fieldValue = String.format("'%s'", fieldValue);
        }
        fieldValues.add(fieldValue);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    // create SQL-String for database insert:
    String sqlString = String.format("INSERT INTO %s (%s) VALUES (%s);",
        tableName,
        String.join(", ", fieldNames),
        String.join(", ", fieldValues)
    );
    System.out.println(sqlString);
    try {
      statement.executeUpdate(sqlString);
      System.out.println("entity-object inserted into DB");
    } catch (SQLException e) {
      System.out.println("Insert into database failed");
      throw new RuntimeException(e);
    }
  }

  /**
   * get entity object from database by Id
   * @param entityClass
   * @return
   */
  public Object findById(Object primaryKey, Class<?> entityClass) throws SQLException {
    // ToDo Task 1: create SELECT statement: SELECT * FROM tableName WHERE pkField = ?
    String sqlQuery = "";
    ResultSet resultSet = statement.executeQuery(sqlQuery);

    // ToDo Task 2: create new entity-class-object

    // ToDo Task 3: fill entity-class-object with values from resultset

    return null;
  }

  private String getEntityTableName(Class<?> entityClass) {
    String entityName = entityClass.getAnnotation(Entity.class).name();
    return entityName.isBlank() ? entityClass.getSimpleName().toLowerCase() : entityName;
  }

  public static void main(String[] args) {
    EntityManager entityManager = new EntityManager();
    Airplane airplane = new Airplane(12L, "Boing 767",
        50.6, 375,
        LocalDate.of(2018, Month.APRIL, 1));
    entityManager.persist(airplane);
    Airplane airplaneFromDB = (Airplane)entityManager.findById(12L, Airplane.class);
  }

}
