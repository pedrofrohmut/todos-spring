package com.pedrofrohmut.todos.domain.dtos;

public class UpdateTaskDto {

  public String name;
  public String description;

  public UpdateTaskDto() {}

  public UpdateTaskDto(String name, String description) {
    this.name = name;
    this.description = description;
  }

}
