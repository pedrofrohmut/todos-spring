package com.pedrofrohmut.todos.domain.errors;

public class UseCaseNotListedException extends RuntimeException {

  public static final String message =
    "Use case not listed in the UseCaseFactory. Check if it is spelled right or if the factory needs an extension";

  public UseCaseNotListedException() {
    super(UseCaseNotListedException.message);
  }

  public UseCaseNotListedException(String msg) {
    super(msg + ". " + UseCaseNotListedException.message);
  }

}
