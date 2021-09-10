package com.pedrofrohmut.todos.domain.dtos;

public class CreateTodoDto {

  public String title;
  public String description;
  public String taskId;

  public CreateTodoDto() {
  }

  public CreateTodoDto(String title, String description, String taskId) {
    this.title = title;
    this.description = description;
    this.taskId = taskId;
  }

}
