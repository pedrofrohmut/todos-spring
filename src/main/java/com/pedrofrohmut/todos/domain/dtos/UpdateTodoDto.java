package com.pedrofrohmut.todos.domain.dtos;

public class UpdateTodoDto {

  public String title;
  public String description;

  public UpdateTodoDto() {}

  public UpdateTodoDto(String title, String description) {
    this.title = title;
    this.description = description;
  }

}
