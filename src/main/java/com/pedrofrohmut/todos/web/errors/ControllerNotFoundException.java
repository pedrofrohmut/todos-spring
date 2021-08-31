package com.pedrofrohmut.todos.web.errors;

public class ControllerNotFoundException extends RuntimeException {

  public static final String message = "Controller not found with the arguments passed";

  public ControllerNotFoundException() {
    super(ControllerNotFoundException.message);
  }

  public ControllerNotFoundException(String msg) {
    super(msg + ". " + ControllerNotFoundException.message);
  }

}
