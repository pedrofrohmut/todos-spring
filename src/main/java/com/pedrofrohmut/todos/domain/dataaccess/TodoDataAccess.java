package com.pedrofrohmut.todos.domain.dataaccess;

import java.util.List;

import com.pedrofrohmut.todos.domain.entities.Todo;

public interface TodoDataAccess {
  void create(Todo newTodo);
  Todo findById(String todoId);
  List<Todo> findByTaskId(String taskId);
  void update(Todo updatedTodo);
  void setDone(String todoId);
  void setNotDone(String todoId);
  void delete(String todoId);
  void clearCompleteByTaskId(String taskId);
}
