package com.pedrofrohmut.todos.errors;

public class InvalidTokenException extends RuntimeException {

  public static final String message = "Invalid token. The token could not be parsed";

  public InvalidTokenException() {
    super(InvalidTokenException.message);
  }

  public InvalidTokenException(String msg) {
    super(msg + ". " + InvalidTokenException.message);
  }

}
