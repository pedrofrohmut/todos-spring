package com.pedrofrohmut.todos.web.controllers;

import com.pedrofrohmut.todos.domain.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTodoDto;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.InvalidTodoException;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.factories.UseCaseFactory;
import com.pedrofrohmut.todos.domain.usecases.TodoUseCase;
import com.pedrofrohmut.todos.domain.usecases.todos.CreateTodoUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.TaskDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.TodoDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class TodoController {

  public ControllerResponseDto<?> create(AdaptedRequest<CreateTodoDto> request) {
    final var connection = ConnectionFactory.getConnection();
    final var createTodoUseCase = (CreateTodoUseCase) UseCaseFactory.getInstance("CreateTodoUseCase", connection);
    return create(createTodoUseCase, request);
  }

  public ControllerResponseDto<?> create(CreateTodoUseCase createTodoUseCase, AdaptedRequest<CreateTodoDto> request) {
    try {
      final var newTodo = request.body;
      createTodoUseCase.execute(newTodo, request.authUserId);
      return new ControllerResponseDto<>(201);
    } catch (
        MissingRequestBodyException |
        InvalidTodoException |
        InvalidEntityException |
        UserNotFoundByIdException |
        UserNotResourceOwnerException |
        TaskNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> findById(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      final var foundTodo = todoUseCase.findById(request.param, request.authUserId);
      return new ControllerResponseDto<>(200, foundTodo);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> findByTaskId(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      final var foundTodos = todoUseCase.findByTaskId(request.param, request.authUserId);
      return new ControllerResponseDto<>(200, foundTodos);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TaskNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> update(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      todoUseCase.update(request.param, (UpdateTodoDto) request.body, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> setDone(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      todoUseCase.setDone(request.param, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> setNotDone(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      todoUseCase.setNotDone(request.param, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> delete(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      todoUseCase.delete(request.param, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> clearCompleteByTaskId(AdaptedRequest request) {
    try {
      final var todoUseCase = createTodoUseCase();
      todoUseCase.clearCompleteByTaskId(request.param, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  private TodoUseCase createTodoUseCase() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userDataAccess = new UserDataAccessImpl(connection);
    final var taskDataAccess = new TaskDataAccessImpl(connection);
    final var todoDataAccess = new TodoDataAccessImpl(connection);
    final var todoUseCase = new TodoUseCase(userDataAccess, taskDataAccess, todoDataAccess);
    return todoUseCase;
  }

}
