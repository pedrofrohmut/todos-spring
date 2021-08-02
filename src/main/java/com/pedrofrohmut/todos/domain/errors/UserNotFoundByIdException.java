package com.pedrofrohmut.todos.domain.errors;

public class UserNotFoundByIdException extends RuntimeException {

  public static final String message = "User not found with the id passed";

  public UserNotFoundByIdException() {
    super(UserNotFoundByIdException.message);
  }

  public UserNotFoundByIdException(String msg) {
    super(msg + ". " + UserNotFoundByIdException.message);
  }

}
