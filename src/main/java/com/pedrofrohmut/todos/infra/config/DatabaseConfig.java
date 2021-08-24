package com.pedrofrohmut.todos.infra.config;

public class DatabaseConfig {

  public static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/todos_spring";
  public static final String POSTGRES_USER = DatabaseSecrets.DATABASE_USER;
  public static final String POSTGRES_PASSWORD = DatabaseSecrets.DATABASE_PASSWORD;
  public static final String POSTGRES_DRIVER = "org.postgresql.Driver";

  public static final String TEST_URL = "jdbc:postgresql://localhost:5432/todos_spring_test";
  public static final String TEST_USER = DatabaseSecrets.TEST_DATABASE_USER;
  public static final String TEST_PASSWORD = DatabaseSecrets.TEST_DATABASE_PASSWORD;
  public static final String TEST_DRIVER = "org.postgresql.Driver";

}
