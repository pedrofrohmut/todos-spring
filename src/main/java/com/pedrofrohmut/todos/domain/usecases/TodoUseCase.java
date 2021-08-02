package com.pedrofrohmut.todos.domain.usecases;

import java.util.List;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.domain.dtos.TodoDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTodoDto;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.mapper.TodoMapper;

public class TodoUseCase {

  private static final String errorMessage = "[TodoUseCase] %s";

  private final UserDataAccess userDataAccess;
  private final TaskDataAccess taskDataAccess;
  private final TodoDataAccess todoDataAccess;

  public TodoUseCase(
      UserDataAccess userDataAccess,
      TaskDataAccess taskDataAccess,
      TodoDataAccess todoDataAccess) {
    this.userDataAccess = userDataAccess;
    this.taskDataAccess = taskDataAccess;
    this.todoDataAccess = todoDataAccess;
  }

  public void create(CreateTodoDto dto, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "create"));
    }
    final var foundTask = this.taskDataAccess.findById(dto.taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TodoUseCase.errorMessage, "create"));
    }
    if (!foundTask.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoUseCase.errorMessage, "create"));
    }
    final var newTodo = new Todo(dto.title, dto.description, dto.taskId, authUserId);
    this.todoDataAccess.create(newTodo);
  }

  public TodoDto findById(String todoId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "findById"));
    }
    final var foundTodo = this.todoDataAccess.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoUseCase.errorMessage, "findById"));
    }
    if (!foundTodo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoUseCase.errorMessage, "findById"));
    }
    final var todoDto = TodoMapper.mapEntityToTodoDto(foundTodo);
    return todoDto;
  }

  public List<TodoDto> findByTaskId(String taskId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "findByTaskId"));
    }
    final var foundTask = this.taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TodoUseCase.errorMessage, "findByTaskId"));
    }
    if (!foundTask.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(
          String.format(TodoUseCase.errorMessage, "findByTaskId"));
    }
    final var todos = this.todoDataAccess.findByTaskId(taskId);
    final var todoDtos = TodoMapper.mapEntityListToTodoDtoList(todos);
    return todoDtos;
  }

  public void update(String todoId, UpdateTodoDto dto, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "update"));
    }
    final var foundTodo = this.todoDataAccess.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoUseCase.errorMessage, "update"));
    }
    if (!foundTodo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoUseCase.errorMessage, "update"));
    }
    final var updatedTodo =
      new Todo(
        todoId,
        dto.title,
        dto.description,
        foundTodo.isDone(),
        foundTodo.getTaskId(),
        foundTodo.getUserId());
    this.todoDataAccess.update(updatedTodo);
  }

  public void setDone(String todoId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "setDone"));
    }
    final var foundTodo = this.todoDataAccess.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoUseCase.errorMessage, "setDone"));
    }
    if (!foundTodo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoUseCase.errorMessage, "setDone"));
    }
    this.todoDataAccess.setDone(todoId);
  }

  public void setNotDone(String todoId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "setNotDone"));
    }
    final var foundTodo = this.todoDataAccess.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoUseCase.errorMessage, "setNotDone"));
    }
    if (!foundTodo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoUseCase.errorMessage, "setNotDone"));
    }
    this.todoDataAccess.setNotDone(todoId);
  }

  public void delete(String todoId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoUseCase.errorMessage, "delete"));
    }
    final var foundTodo = this.todoDataAccess.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoUseCase.errorMessage, "delete"));
    }
    if (!foundTodo.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoUseCase.errorMessage, "delete"));
    }
    this.todoDataAccess.delete(todoId);
  }

  public void clearCompleteByTaskId(String taskId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(
          String.format(TodoUseCase.errorMessage, "clearCompleteByTaskId"));
    }
    final var foundTask = this.taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(
          String.format(TodoUseCase.errorMessage, "clearCompleteByTaskId"));
    }
    if (!foundTask.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(
          String.format(TodoUseCase.errorMessage, "clearCompleteByTaskId"));
    }
    this.todoDataAccess.clearCompleteByTaskId(taskId);
  }

}
