package com.pedrofrohmut.todos.web.errors;

public class MissingRequestAuthUserIdException extends RuntimeException {

  public static final String message = "Request is missing the auth user id when it required";

  public MissingRequestAuthUserIdException() {
    super(MissingRequestAuthUserIdException.message);
  }

  public MissingRequestAuthUserIdException(String msg) {
    super(msg + ". " + MissingRequestAuthUserIdException.message);
  }

}
