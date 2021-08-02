package com.pedrofrohmut.todos.domain.errors;

public class UserNotResourceOwnerException extends RuntimeException {

  public static final String message = "User is not the owner of the resource";

  public UserNotResourceOwnerException() {
    super(UserNotResourceOwnerException.message);
  }

  public UserNotResourceOwnerException(String msg) {
    super(msg + ". " + UserNotResourceOwnerException.message);
  }

}
