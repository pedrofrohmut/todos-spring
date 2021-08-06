package com.pedrofrohmut.todos.web.controllers;

import com.pedrofrohmut.todos.domain.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTaskDto;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.usecases.TaskUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.TaskDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;

public class TaskController {

  public ControllerResponseDto<?> create(AdaptedRequest request) {
    try {
      final var taskUseCase = createTaskUseCase();
      taskUseCase.create((CreateTaskDto) request.body, request.authUserId);
      return new ControllerResponseDto<>(201);
    } catch (UserNotFoundByIdException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> findById(AdaptedRequest request) {
    try {
      final var taskUseCase = createTaskUseCase();
      final var foundTask = taskUseCase.findById(request.param, request.authUserId);
      return new ControllerResponseDto<>(200, foundTask);
    } catch (
        UserNotFoundByIdException | TaskNotFoundByIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> findByUserId(AdaptedRequest request) {
    try {
      final var taskUseCase = createTaskUseCase();
      final var foundTasks = taskUseCase.findByUserId(request.param, request.authUserId);
      return new ControllerResponseDto<>(200, foundTasks);
    } catch (UserNotFoundByIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> update(AdaptedRequest request) {
    try {
      final var taskUseCase = createTaskUseCase();
      taskUseCase.update(request.param, (UpdateTaskDto) request.body, request.authUserId);
      return new ControllerResponseDto<>(204);
    } catch (
        UserNotFoundByIdException | TaskNotFoundByIdException | UserNotResourceOwnerException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
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
