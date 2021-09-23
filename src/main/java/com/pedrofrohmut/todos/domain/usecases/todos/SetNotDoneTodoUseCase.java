package com.pedrofrohmut.todos.domain.usecases.todos;

import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class SetNotDoneTodoUseCase {

  private static final String errorMessage = "SetNotDoneTodoUseCase execute";

  private final TodoDataAccess todoDataAccess;
  private final UserDataAccess userDataAccess;

  public SetNotDoneTodoUseCase(TodoDataAccess todoDataAccess, UserDataAccess userDataAccess) {
    this.todoDataAccess = todoDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public void execute(String todoId, String authUserId) {
    checkTodoId(todoId);
    checkAuthUserId(authUserId);
    checkUserExists(authUserId);
    final var foundTodo = findTodoById(todoId);
    checkTodoExists(foundTodo);
    checkTodoOwnership(foundTodo, authUserId);
    setNotDoneTodo(todoId);
  }

  private void checkTodoId(String todoId) {
    if (todoId == null) {
      throw new MissingRequestParametersException(errorMessage);
    }
    Entity.validateId(todoId);
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

  private Todo findTodoById(String todoId) {
    final var foundTodo = todoDataAccess.findById(todoId);
    return foundTodo;
  }

  private void checkTodoExists(Todo todo) {
    if (todo == null) {
      throw new TodoNotFoundByIdException(errorMessage);
    }
  }

  private void checkTodoOwnership(Todo todo, String authUserId) {
    if (!todo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(errorMessage);
    }
  }

  private void setNotDoneTodo(String todoId) {
    todoDataAccess.setNotDone(todoId);
  }

}
