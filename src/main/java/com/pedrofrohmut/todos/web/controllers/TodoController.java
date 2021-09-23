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
import com.pedrofrohmut.todos.domain.usecases.todos.DeleteTodoUseCase;
import com.pedrofrohmut.todos.domain.usecases.todos.FindTodoByIdUseCase;
import com.pedrofrohmut.todos.domain.usecases.todos.FindTodosByTaskIdUseCase;
import com.pedrofrohmut.todos.domain.usecases.todos.SetDoneTodoUseCase;
import com.pedrofrohmut.todos.domain.usecases.todos.SetNotDoneTodoUseCase;
import com.pedrofrohmut.todos.domain.usecases.todos.UpdateTodoUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.TaskDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.TodoDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

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

  public ControllerResponseDto<?> findById(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var findTodoByIdUseCase = (FindTodoByIdUseCase) UseCaseFactory.getInstance("FindTodoByIdUseCase", connection);
    return findById(findTodoByIdUseCase, request);
  }

  public ControllerResponseDto<?> findById(FindTodoByIdUseCase findTodoByIdUseCase, AdaptedRequest<?> request) {
    try {
      final var foundTodo = findTodoByIdUseCase.execute(request.param, request.authUserId);
      return new ControllerResponseDto<>(200, foundTodo);
    } catch (
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> findByTaskId(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var findTodosByTaskIdUseCase =
      (FindTodosByTaskIdUseCase) UseCaseFactory.getInstance("FindTodosByTaskIdUseCase", connection);
    return findByTaskId(findTodosByTaskIdUseCase, request);
  }

  public ControllerResponseDto<?> findByTaskId(
      FindTodosByTaskIdUseCase findTodosByTaskIdUseCase, AdaptedRequest<?> request) {
    try {
      final var taskId = request.param;
      final var foundTodos = findTodosByTaskIdUseCase.execute(taskId, request.authUserId);
      return new ControllerResponseDto<>(200, foundTodos);
    } catch (
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TaskNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> update(AdaptedRequest<UpdateTodoDto> request) {
    final var connection = ConnectionFactory.getConnection();
    final var updateTodoUseCase = (UpdateTodoUseCase) UseCaseFactory.getInstance("UpdateTodoUseCase", connection);
    return update(updateTodoUseCase, request);
  }

  public ControllerResponseDto<?> update(UpdateTodoUseCase updateTodoUseCase, AdaptedRequest<UpdateTodoDto> request) {
    try {
      final var todoId = request.param;
      final var updatedTodo = request.body;
      updateTodoUseCase.execute(todoId, updatedTodo, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        MissingRequestBodyException |
        InvalidTodoException |
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException  e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> setDone(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var setDoneTodoUseCase = (SetDoneTodoUseCase) UseCaseFactory.getInstance("SetDoneTodoUseCase", connection);
    return setDone(setDoneTodoUseCase, request);
  }

  public ControllerResponseDto<?> setDone(SetDoneTodoUseCase setDoneTodoUseCase, AdaptedRequest<?> request) {
    try {
      setDoneTodoUseCase.execute(request.param, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> setNotDone(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var setNotDoneTodoUseCase = (SetNotDoneTodoUseCase) UseCaseFactory.getInstance("SetNotDoneTodoUseCase", connection);
    return setNotDone(setNotDoneTodoUseCase, request);
  }

  public ControllerResponseDto<?> setNotDone(SetNotDoneTodoUseCase setNotDoneTodoUseCase, AdaptedRequest<?> request) {
    try {
      final var todoId = request.param;
      setNotDoneTodoUseCase.execute(todoId, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> delete(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var deleteTodoUseCase = (DeleteTodoUseCase) UseCaseFactory.getInstance("DeleteTodoUseCase", connection);
    return delete(deleteTodoUseCase, request);
  }

  public ControllerResponseDto<?> delete(DeleteTodoUseCase deleteTodoUseCase, AdaptedRequest<?> request) {
    try {
      final var todoId = request.param;
      deleteTodoUseCase.execute(todoId, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TodoNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
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
        UserNotFoundByIdException |
        UserNotResourceOwnerException |
        TodoNotFoundByIdException e) {
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
