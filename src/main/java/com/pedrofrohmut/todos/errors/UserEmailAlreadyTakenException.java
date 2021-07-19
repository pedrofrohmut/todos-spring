package com.pedrofrohmut.todos.errors;

public class UserEmailAlreadyTakenException extends RuntimeException {

  public static final String message = "User e-mail is already taken. But must be unique";

  public UserEmailAlreadyTakenException() {
    super(UserEmailAlreadyTakenException.message);
  }

  public UserEmailAlreadyTakenException(String msg) {
    super(msg + ". " + UserEmailAlreadyTakenException.message);
  }

}
