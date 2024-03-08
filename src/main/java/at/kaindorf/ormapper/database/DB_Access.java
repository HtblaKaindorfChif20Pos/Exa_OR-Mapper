package at.kaindorf.ormapper.database;

import at.kaindorf.ormapper.io.IO_Access;
import at.kaindorf.ormapper.pojos.Persistence;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 06.03.2024
 * Time: 08:31
 */
@Getter
public final class DB_Access {
  private static DB_Access theInstance;
  private Connection connection;
  private Statement statement;

  public static DB_Access getInstance() {
    if (theInstance == null) {
      theInstance = new DB_Access();
    }
    return theInstance;
  }

  private DB_Access() {
    loadDatabaseProperties();
    try {
      Class.forName(dbDriver);
      connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    } catch (ClassNotFoundException e) {
      System.out.println("Could not load postgres-driver");
      throw new RuntimeException(e);
    } catch (SQLException e) {
      System.out.println("Could not connect to database " + dbUrl);
      throw new RuntimeException(e);
    }
  }

  public Statement getStatement() throws SQLException {
    if (connection == null) {
      throw new RuntimeException("Not connected to database");
    }
    return connection.createStatement();
  }

  public static void main(String[] args) {
    DB_Access.getInstance();
  }

  private void loadDatabaseProperties() {
    Persistence persistence = IO_Access.loadPersistenceUnit();
    dbDriver = persistence.getDatabaseProperties().get("driver");
    dbUrl = persistence.getDatabaseProperties().get("url");
    dbUser = persistence.getDatabaseProperties().get("user");
    dbPassword = persistence.getDatabaseProperties().get("password");
    dbDdlSchema = persistence.getDatabaseProperties().get("ddl-schema");
    if (dbDriver.isBlank() || dbUrl.isBlank() || dbUser.isBlank()
        || dbPassword.isBlank() || dbDdlSchema.isBlank()) {
      throw new RuntimeException("invalid persistence-unit");
    }
  }

  private String dbDriver;
  private String dbUrl;
  private String dbUser;
  private String dbPassword;
  private String dbDdlSchema;
}
