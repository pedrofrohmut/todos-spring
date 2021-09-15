package com.pedrofrohmut.todos.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.entities.Todo;

public class TodoDataAccessMock {

  public static TodoDataAccess getMockForTodoFoundById(
      String todoId, String name, String description, boolean isDone, String taskId, String userId) {
    final var mockTodoDataAccess = mock(TodoDataAccess.class);
    final var todoDB = new Todo(todoId, name, description, isDone, taskId, userId);
    when(mockTodoDataAccess.findById(todoId)).thenReturn(todoDB);
    return mockTodoDataAccess;
  }

  public static TodoDataAccess getMockForTodoNotFoundById(String todoId) {
    final var mockTodoDataAccess = mock(TodoDataAccess.class);
    when(mockTodoDataAccess.findById(todoId)).thenReturn(null);
    return mockTodoDataAccess;
  }

}
