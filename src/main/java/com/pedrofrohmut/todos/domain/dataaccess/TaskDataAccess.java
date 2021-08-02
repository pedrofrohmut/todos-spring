package com.pedrofrohmut.todos.domain.dataaccess;

import java.util.List;

import com.pedrofrohmut.todos.domain.entities.Task;

public interface TaskDataAccess {
  void create(Task newTask);
  Task findById(String taskId);
  List<Task> findByUserId(String userId);
  void update(Task updatedTask);
  void delete(String taskId);
}
