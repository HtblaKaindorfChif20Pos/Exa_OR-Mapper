package at.kaindorf.ormapper.database;

import at.kaindorf.ormapper.annotations.Column;
import at.kaindorf.ormapper.annotations.Entity;
import at.kaindorf.ormapper.annotations.Id;
import at.kaindorf.ormapper.io.IO_Access;
import at.kaindorf.ormapper.pojos.Airplane;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static at.kaindorf.ormapper.database.SQLDataTypes.SQL_TYPES;

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
   * CREATE TABLE [IF NOT EXISTS] table_name (
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
          datatype = datatype.replace("255", column.length()+"");
        }
      }
      createTableString += String.format("%s %s%s,\n ", columnName, datatype, constraint);
    }
    createTableString = createTableString.substring(0,createTableString.lastIndexOf(",")) + ");";
    try {
      statement.execute(createTableString);
    } catch (SQLException e) {
      System.out.println(e.toString());
      throw new RuntimeException(e);
    }
//    System.out.println(createTableString);
    System.out.printf("Table %s created\n", tableName);

  }

  private String getEntityTableName(Class<?> entityClass) {
    String entityName = entityClass.getAnnotation(Entity.class).name();
    return entityName.isBlank() ? entityClass.getSimpleName().toLowerCase() : entityName;
  }
  public static void main(String[] args) {
    EntityManager entityManager = new EntityManager();
  }

}
