package com.pedrofrohmut.todos.infra.errors;

public class GetConnectionException extends RuntimeException {

  public static final String message = "Cannot get a connection";

  public GetConnectionException() {
    super(GetConnectionException.message);
  }

  public GetConnectionException(String msg) {
    super(msg + ". " + GetConnectionException.message);
  }

}
