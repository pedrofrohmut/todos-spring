package com.pedrofrohmut.todos.domain.dtos;

public class TaskDto {

  public String id;
  public String name;
  public String description;
  public String userId;

  public TaskDto() {}

  public TaskDto(String id, String name, String description, String userId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.userId = userId;
  }

}
