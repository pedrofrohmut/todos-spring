package com.pedrofrohmut.todos.web.errors;

public class MissingRequestBodyException extends RuntimeException {

  public static final String message = "Request is missing the body when it required";

  public MissingRequestBodyException() {
    super(MissingRequestBodyException.message);
  }

  public MissingRequestBodyException(String msg) {
    super(msg + ". " + MissingRequestBodyException.message);
  }

}
