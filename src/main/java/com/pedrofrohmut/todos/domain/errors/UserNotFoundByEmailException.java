package com.pedrofrohmut.todos.domain.errors;

public class UserNotFoundByEmailException extends RuntimeException {

  public static final String message = "User not found with the e-mail passed";

  public UserNotFoundByEmailException() {
    super(UserNotFoundByEmailException.message);
  }

  public UserNotFoundByEmailException(String msg) {
    super(msg + ". " + UserNotFoundByEmailException.message);
  }

}
