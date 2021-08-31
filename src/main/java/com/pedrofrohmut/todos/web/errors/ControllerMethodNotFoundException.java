package com.pedrofrohmut.todos.web.errors;

public class ControllerMethodNotFoundException extends RuntimeException {

  public static final String message = "Controller method not found with the arguments passed";

  public ControllerMethodNotFoundException() {
    super(ControllerMethodNotFoundException.message);
  }

  public ControllerMethodNotFoundException(String msg) {
    super(msg + ". " + ControllerMethodNotFoundException.message);
  }

}
