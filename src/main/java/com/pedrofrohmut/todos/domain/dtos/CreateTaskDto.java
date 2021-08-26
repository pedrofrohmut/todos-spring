package com.pedrofrohmut.todos.domain.dtos;

public class CreateTaskDto {

  public String name;
  public String description;

  public CreateTaskDto() {}

  public CreateTaskDto(String name, String description) {
    this.name = name;
    this.description = description;
  }

}
