package com.pedrofrohmut.todos.web.controllers;

import com.pedrofrohmut.todos.domain.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTaskDto;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.InvalidTaskException;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.factories.UseCaseFactory;
import com.pedrofrohmut.todos.domain.usecases.TaskUseCase;
import com.pedrofrohmut.todos.domain.usecases.tasks.CreateTaskUseCase;
import com.pedrofrohmut.todos.domain.usecases.tasks.FindTaskByIdUseCase;
import com.pedrofrohmut.todos.domain.usecases.tasks.FindTasksByUserIdUseCase;
import com.pedrofrohmut.todos.domain.usecases.tasks.UpdateTaskUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.TaskDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class TaskController {

  public ControllerResponseDto<?> create(AdaptedRequest<CreateTaskDto> request) {
    final var connection = ConnectionFactory.getConnection();
    final var createTaskUseCase = (CreateTaskUseCase) UseCaseFactory.getInstance("CreateTaskUseCase", connection);
    return create(createTaskUseCase, request);
  }

  public ControllerResponseDto<?> create(CreateTaskUseCase createTaskUseCase, AdaptedRequest<CreateTaskDto> request) {
    try {
      final var newTask = request.body;
      createTaskUseCase.execute(newTask, request.authUserId);
      return new ControllerResponseDto<>(201);
    } catch (MissingRequestBodyException | InvalidTaskException | InvalidEntityException | UserNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> findById(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var findTaskByIdUseCase = (FindTaskByIdUseCase) UseCaseFactory.getInstance("FindTaskByIdUseCase", connection);
    return findById(findTaskByIdUseCase, request);
  }

  public ControllerResponseDto<?> findById(FindTaskByIdUseCase findTaskByIdUseCase, AdaptedRequest<?> request) {
    try {
      final var taskId = request.param;
      final var foundTask = findTaskByIdUseCase.execute(taskId, request.authUserId);
      return new ControllerResponseDto<>(200, foundTask);
    } catch (
        UserNotFoundByIdException |
        TaskNotFoundByIdException |
        InvalidEntityException |
        MissingRequestParametersException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
        return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto <?> findByUserId(AdaptedRequest<?> request) {
    final var connection = ConnectionFactory.getConnection();
    final var findTasksByUserIdUseCase =
      (FindTasksByUserIdUseCase) UseCaseFactory.getInstance("FindTasksByUserIdUseCase", connection);
    return findByUserId(findTasksByUserIdUseCase, request);
  }

  public ControllerResponseDto<?> findByUserId(
      FindTasksByUserIdUseCase findTasksByUserIdUseCase, AdaptedRequest<?> request) {
    try {
      final var foundTasks = findTasksByUserIdUseCase.execute(request.param, request.authUserId);
      return new ControllerResponseDto<>(200, foundTasks);
    } catch (
        UserNotFoundByIdException |
        InvalidEntityException |
        MissingRequestParametersException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> update(AdaptedRequest<UpdateTaskDto> request) {
    final var connection = ConnectionFactory.getConnection();
    final var updateTaskUseCase = (UpdateTaskUseCase) UseCaseFactory.getInstance("UpdateTaskUseCase", connection);
    return update(updateTaskUseCase, request);
  }

  public ControllerResponseDto<?> update(UpdateTaskUseCase updateTaskUseCase, AdaptedRequest<UpdateTaskDto> request) {
    try {
      final var updatedTask = (UpdateTaskDto) request.body;
      final var taskId = request.param;
      updateTaskUseCase.execute(updatedTask, request.authUserId, taskId);
      return new ControllerResponseDto<>(204);
    } catch (
        MissingRequestBodyException |
        InvalidTaskException |
        InvalidEntityException |
        MissingRequestParametersException |
        UserNotFoundByIdException |
        TaskNotFoundByIdException |
        UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (MissingRequestAuthUserIdException e) {
      return new ControllerResponseDto<>(401, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> delete(AdaptedRequest request) {
    try {
      final var taskUseCase = createTaskUseCase();
      taskUseCase.delete(request.param, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | TaskNotFoundByIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  private TaskUseCase createTaskUseCase() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userDataAccess = new UserDataAccessImpl(connection);
    final var taskDataAccess = new TaskDataAccessImpl(connection);
    final var taskUseCase = new TaskUseCase(userDataAccess, taskDataAccess);
    return taskUseCase;
  }

}
