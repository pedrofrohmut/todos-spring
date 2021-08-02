package com.pedrofrohmut.todos.domain.errors;

public class InvalidTodoException extends RuntimeException {

  public static final String message = "Todo is invalid";

  public InvalidTodoException() {
    super(InvalidTodoException.message);
  }

  public InvalidTodoException(String msg) {
    super(msg + ". " + InvalidTodoException.message);
  }

}

