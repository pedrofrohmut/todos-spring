package com.pedrofrohmut.todos.dtos;

import org.springframework.stereotype.Component;

@Component
public class CreateTodoDto {
  public String name;
  public String description;
  public boolean isDone;
  public String taskId;
  public String userId;
}
