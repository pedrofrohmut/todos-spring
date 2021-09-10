package com.pedrofrohmut.todos.domain.usecases.todos;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class CreateTodoUseCase {

  private static final String errorMessage = "CreateTodoUseCase execute";

  private final TodoDataAccess todoDataAccess;
  private final TaskDataAccess taskDataAccess;
  private final UserDataAccess userDataAccess;

  public CreateTodoUseCase(
      TodoDataAccess todoDataAccess, TaskDataAccess taskDataAccess, UserDataAccess userDataAccess) {
    this.todoDataAccess = todoDataAccess;
    this.taskDataAccess = taskDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public void execute(CreateTodoDto newTodo, String authUserId) {
    checkNewTodo(newTodo);
    checkAuthUserId(authUserId);
    checkUserExists(authUserId);
    checkTaskExists(newTodo.taskId);
    createTodo(newTodo, authUserId);
  }

  private void checkNewTodo(CreateTodoDto newTodo) {
    if (newTodo == null) {
      throw new MissingRequestBodyException(errorMessage);
    }
    Todo.validateTitle(newTodo.title);
    Todo.validateDescription(newTodo.description);
    Entity.validateId(newTodo.taskId);
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

  private void checkTaskExists(String taskId) {
    final var foundTask = taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(errorMessage);
    }
  }

  private void createTodo(CreateTodoDto todoDto, String authUserId) {
    final var newTodo = new Todo(todoDto.title, todoDto.description, todoDto.taskId, authUserId);
    todoDataAccess.create(newTodo);
  }

}
