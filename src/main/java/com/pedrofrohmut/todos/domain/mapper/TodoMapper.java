package com.pedrofrohmut.todos.domain.mapper;

import java.util.List;

import com.pedrofrohmut.todos.domain.dtos.TodoDto;
import com.pedrofrohmut.todos.domain.entities.Todo;

public class TodoMapper {

  public static TodoDto mapEntityToTodoDto(Todo todo) {
    final var todoDto = new TodoDto();
    todoDto.id = todo.getId();
    todoDto.title = todo.getTitle();
    todoDto.description = todo.getDescription() == null ? "" : todo.getDescription();
    todoDto.isDone = todo.isDone();
    todoDto.taskId = todo.getTaskId();
    todoDto.userId = todo.getUserId();
    return todoDto;
  }

  public static List<TodoDto> mapEntityListToTodoDtoList(List<Todo> todos) {
    return
      todos
        .stream()
        .map(todo -> TodoMapper.mapEntityToTodoDto(todo))
        .toList();
  }

}
