package com.pedrofrohmut.todos.infra.errors;

public class DecodeJWTException extends RuntimeException {

  public static final String message = "The JWT passed could not be decoded";

  public DecodeJWTException() {
    super(DecodeJWTException.message);
  }

  public DecodeJWTException(String msg) {
    super(msg + ". " + DecodeJWTException.message);
  }

}
