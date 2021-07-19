package com.pedrofrohmut.todos.errors;

public class NotImplementedException extends RuntimeException {

  public static final String message = "Method not implemented";

  public NotImplementedException() {
    super(NotImplementedException.message);
  }

}
