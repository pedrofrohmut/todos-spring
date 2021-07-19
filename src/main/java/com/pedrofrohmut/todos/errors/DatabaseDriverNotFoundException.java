package com.pedrofrohmut.todos.errors;

public class DatabaseDriverNotFoundException extends RuntimeException {

  public static final String message = "Database Driver not found with the string passed or not installed";

  public DatabaseDriverNotFoundException() {
    super(DatabaseDriverNotFoundException.message);
  }

  public DatabaseDriverNotFoundException(String msg) {
    super(msg + ". " + DatabaseDriverNotFoundException.message);
  }

}
