package com.pedrofrohmut.todos.domain.errors;

public class InvalidUserException extends RuntimeException {

  public static final String message = "User is invalid";

  public InvalidUserException() {
    super(InvalidUserException.message);
  }

  public InvalidUserException(String msg) {
    super(msg + ". " + InvalidUserException.message);
  }

}

