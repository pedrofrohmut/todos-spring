package com.pedrofrohmut.todos.errors;

public class UserNotFoundByEmailException extends RuntimeException {

  public static final String message = "User e-mail is already taken. But must be unique";

  public UserNotFoundByEmailException() {
    super(UserNotFoundByEmailException.message);
  }

  public UserNotFoundByEmailException(String msg) {
    super(msg + ". " + UserNotFoundByEmailException.message);
  }

}
