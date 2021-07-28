package com.pedrofrohmut.todos.errors;

public class TokenExpiredException extends RuntimeException {

  public static final String message = "The token is expired. Token exp value is less than now";

  public TokenExpiredException() {
    super(TokenExpiredException.message);
  }

  public TokenExpiredException(String msg) {
    super(msg + ". " + TokenExpiredException.message);
  }

}
