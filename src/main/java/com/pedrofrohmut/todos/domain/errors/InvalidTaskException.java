package com.pedrofrohmut.todos.domain.errors;

public class InvalidTaskException extends RuntimeException {

  public static final String message = "Task is invalid";

  public InvalidTaskException() {
    super(InvalidTaskException.message);
  }

  public InvalidTaskException(String msg) {
    super(msg + ". " + InvalidTaskException.message);
  }

}

