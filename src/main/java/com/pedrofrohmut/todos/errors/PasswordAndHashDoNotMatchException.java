package com.pedrofrohmut.todos.errors;

public class PasswordAndHashDoNotMatchException extends RuntimeException {

  public static final String message = "Password and hash passed do not match";

  public PasswordAndHashDoNotMatchException() {
    super(PasswordAndHashDoNotMatchException.message);
  }

  public PasswordAndHashDoNotMatchException(String msg) {
    super(msg + ". " + PasswordAndHashDoNotMatchException.message);
  }

}
