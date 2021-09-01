package com.pedrofrohmut.todos.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.entities.Task;

public class TaskDataAccessMock {

  public static TaskDataAccess getMockForTaskFoundById(String taskId, String name, String description, String userId) {
    final var taskDB = new Task(taskId, name, description, userId);
    final var mockTaskDataAccess = mock(TaskDataAccess.class);
    when(mockTaskDataAccess.findById(taskId)).thenReturn(taskDB);
    return mockTaskDataAccess;
  }

  public static TaskDataAccess getMockForTaskNotFoundById(String taskId) {
    final var mockTaskDataAccess = mock(TaskDataAccess.class);
    when(mockTaskDataAccess.findById(taskId)).thenReturn(null);
    return mockTaskDataAccess;
  }

  public static TaskDataAccess getMockForTasksFoundByUserId(String userId, List<Task> tasks) {
    final var mockTaskDataAccess = mock(TaskDataAccess.class);
    when(mockTaskDataAccess.findByUserId(userId)).thenReturn(tasks);
    return mockTaskDataAccess;
  }

  public static TaskDataAccess getMockForTasksNotFoundByUserId(String userId) {
    final var mockTaskDataAccess = mock(TaskDataAccess.class);
    when(mockTaskDataAccess.findByUserId(userId)).thenReturn(new ArrayList<Task>());
    return mockTaskDataAccess;
  }

}
