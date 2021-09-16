package com.pedrofrohmut.todos.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

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

  public static TodoDataAccess getMockForTodosFoundByTaskId(String taskId, String userId) {
    final var mockTodoDataAccess = mock(TodoDataAccess.class);
    final var todosDB = new ArrayList<Todo>();
    todosDB.add(new Todo(UUID.randomUUID().toString(), "Todo Title 1", "Todo Description 1", false, taskId, userId));
    todosDB.add(new Todo(UUID.randomUUID().toString(), "Todo Title 2", "Todo Description 2", false, taskId, userId));
    todosDB.add(new Todo(UUID.randomUUID().toString(), "Todo Title 3", "Todo Description 3", false, taskId, userId));
    when(mockTodoDataAccess.findByTaskId(taskId)).thenReturn(todosDB);
    return mockTodoDataAccess;
  }

  public static TodoDataAccess getMockForTodosNotFoundByTaskId(String taskId) {
    final var mockTodoDataAccess = mock(TodoDataAccess.class);
    final var todosDB = new ArrayList<Todo>();
    when(mockTodoDataAccess.findByTaskId(taskId)).thenReturn(todosDB);
    return mockTodoDataAccess;
  }

}
