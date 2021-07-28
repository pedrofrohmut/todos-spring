package com.pedrofrohmut.todos.errors;

public class TaskNotFoundByIdException extends RuntimeException {

  public static final String message = "Task not found with the id passed";

  public TaskNotFoundByIdException() {
    super(TaskNotFoundByIdException.message);
  }

  public TaskNotFoundByIdException(String msg) {
    super(msg + ". " + TaskNotFoundByIdException.message);
  }

}
