package com.pedrofrohmut.todos.web.errors;

public class MissingRequestParametersException extends RuntimeException {

  public static final String message = "Request is missing the parameter(s) when it required";

  public MissingRequestParametersException() {
    super(MissingRequestParametersException.message);
  }

  public MissingRequestParametersException(String msg) {
    super(msg + ". " + MissingRequestParametersException.message);
  }

}
