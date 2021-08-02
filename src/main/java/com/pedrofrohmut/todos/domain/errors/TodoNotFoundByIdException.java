package com.pedrofrohmut.todos.domain.errors;

public class TodoNotFoundByIdException extends RuntimeException {

  public static final String message = "Todo not found with the id passed";

  public TodoNotFoundByIdException() {
    super(TodoNotFoundByIdException.message);
  }

  public TodoNotFoundByIdException(String msg) {
    super(msg + ". " + TodoNotFoundByIdException.message);
  }

}
