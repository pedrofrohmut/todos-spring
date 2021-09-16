package com.pedrofrohmut.todos.domain.usecases.todos;

import java.util.List;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.TodoDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.mapper.TodoMapper;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class FindTodosByTaskIdUseCase {

  private static final String errorMessage = "FindTodosByTaskIdUseCase execute";

  private final TodoDataAccess todoDataAccess;
  private final TaskDataAccess taskDataAccess;
  private final UserDataAccess userDataAccess;


  public FindTodosByTaskIdUseCase(
      TodoDataAccess todoDataAccess, TaskDataAccess taskDataAccess, UserDataAccess userDataAccess) {
    this.todoDataAccess = todoDataAccess;
    this.taskDataAccess = taskDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public List<TodoDto> execute(String taskId, String authUserId) {
    checkTaskId(taskId);
    checkAuthUserId(authUserId);
    checkUserExists(authUserId);
    final var foundTask = findTaskById(taskId);
    checkTaskExists(foundTask);
    checkTaskOwnership(foundTask, authUserId);
    final var foundTodos = findTodosByTaskId(taskId);
    checkTodosOwnership(foundTodos, authUserId);
    final var todos = getListOfTodoDtos(foundTodos);
    return todos;
  }

  public void checkTaskId(String taskId) {
    if (taskId == null) {
      throw new MissingRequestParametersException(errorMessage);
    }
    Entity.validateId(taskId);
  }

  private void checkAuthUserId(String authUserId) {
    if (authUserId == null) {
      throw new MissingRequestAuthUserIdException(errorMessage);
    }
    Entity.validateId(authUserId);
  }

  private void checkUserExists(String userId) {
    final var foundUser = userDataAccess.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(errorMessage);
    }
  }

  private Task findTaskById(String taskId) {
    final var foundTask = taskDataAccess.findById(taskId);
    return foundTask;
  }

  private void checkTaskExists(Task task) {
    if (task == null) {
      throw new TaskNotFoundByIdException(errorMessage);
    }
  }

  private void checkTaskOwnership(Task task, String authUserId) {
    if (!task.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(errorMessage);
    }
  }

  private List<Todo> findTodosByTaskId(String taskId) {
    final var foundTodos = todoDataAccess.findByTaskId(taskId);
    return foundTodos;
  }

  private void checkTodosOwnership(List<Todo> todos, String authUserId) {
    todos.forEach(todo -> {
      if (!todo.getUserId().equals(authUserId)) {
        throw new UserNotResourceOwnerException(errorMessage);
      }
    });
  }

  private List<TodoDto> getListOfTodoDtos(List<Todo> todos) {
    return TodoMapper.mapEntityListToTodoDtoList(todos);
  }

}
