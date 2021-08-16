package com.pedrofrohmut.todos.infra.factories;

import java.sql.Connection;
import java.sql.DriverManager;

import com.pedrofrohmut.todos.infra.errors.GetConnectionException;

import static com.pedrofrohmut.todos.infra.config.DatabaseConfig.*;

public class ConnectionFactory {

  public Connection getConnection() {
    try {
      Class.forName(POSTGRES_DRIVER);
      final var connection =
        DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
      return connection;
    } catch (Exception e) {
      throw new GetConnectionException(e.getMessage());
    }
  }

  public Connection getTestConnection() {
    try {
      Class.forName(TEST_DRIVER);
      final var connection =
        DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
      return connection;
    } catch (Exception e) {
      throw new GetConnectionException(e.getMessage());
    }
  }

}
