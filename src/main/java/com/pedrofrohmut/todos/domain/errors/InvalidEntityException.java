package com.pedrofrohmut.todos.domain.errors;

public class InvalidEntityException extends RuntimeException {

  public static final String message = "Entity is invalid";

  public InvalidEntityException() {
    super(InvalidEntityException.message);
  }

  public InvalidEntityException(String msg) {
    super(msg + ". " + InvalidEntityException.message);
  }

}

