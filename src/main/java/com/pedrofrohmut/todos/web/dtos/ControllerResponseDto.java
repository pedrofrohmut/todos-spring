package com.pedrofrohmut.todos.web.dtos;

public class ControllerResponseDto<T> {
  public int httpStatus;
  public T body;

  public ControllerResponseDto(int httpStatus, T body) {
    this.httpStatus = httpStatus;
    this.body = body;
  }

  public ControllerResponseDto(int httpStatus) {
    this.httpStatus = httpStatus;
    this.body = null;
  }
}
