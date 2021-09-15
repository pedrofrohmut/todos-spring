package com.pedrofrohmut.todos.domain.usecases.todos;

import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.TodoDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.mapper.TodoMapper;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class FindTodoByIdUseCase {

  private static final String errorMessage = "FindTodoByIdUseCase execute";

  private final TodoDataAccess todoDataAccess;
  private final UserDataAccess userDataAccess;

  public FindTodoByIdUseCase(TodoDataAccess todoDataAccess, UserDataAccess userDataAccess) {
    this.todoDataAccess = todoDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public TodoDto execute(String todoId, String authUserId) {
    checkAuthUserId(authUserId);
    checkTodoId(todoId);
    checkUserExists(authUserId);
    final var foundTodo = findTodoById(todoId);
    checkResourceOwnership(foundTodo, authUserId);
    final var todo = getTodoDto(foundTodo);
    return todo;
  }

  private void checkAuthUserId(String authUserId) {
    if (authUserId == null) {
      throw new MissingRequestAuthUserIdException(errorMessage);
    }
    Entity.validateId(authUserId);
  }

  private void checkTodoId(String todoId) {
    if (todoId == null) {
      throw new MissingRequestParametersException(errorMessage);
    }
    Entity.validateId(todoId);
  }

  private void checkUserExists(String userId) {
    final var foundUser = userDataAccess.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(errorMessage);
    }
  }

  private Todo findTodoById(String todoId) {
    final var foundTodo = todoDataAccess.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(errorMessage);
    }
    return foundTodo;
  }

  private void checkResourceOwnership(Todo foundTodo, String authUserId) {
    if (!foundTodo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(errorMessage);
    }
  }

  private TodoDto getTodoDto(Todo todo) {
    return TodoMapper.mapEntityToTodoDto(todo);
  }

}
