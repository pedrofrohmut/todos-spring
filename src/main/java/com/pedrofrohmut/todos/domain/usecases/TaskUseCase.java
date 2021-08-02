package com.pedrofrohmut.todos.domain.usecases;

import java.util.List;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.domain.dtos.TaskDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTaskDto;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.mapper.TaskMapper;

public class TaskUseCase {

  private static final String errorMessage = "[TaskUseCase] %s";

  private final UserDataAccess userDataAccess;
  private final TaskDataAccess taskDataAccess;

  public TaskUseCase(UserDataAccess userDataAccess, TaskDataAccess taskDataAccess) {
    this.userDataAccess = userDataAccess;
    this.taskDataAccess = taskDataAccess;
  }

  public void create(CreateTaskDto dto, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskUseCase.errorMessage, "create"));
    }
    final var newTask = new Task(dto.name, dto.description, authUserId);
    this.taskDataAccess.create(newTask);
  }

  public TaskDto findById(String taskId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskUseCase.errorMessage, "findById"));
    }
    final var foundTask = this.taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TaskUseCase.errorMessage, "findById"));
    }
    if (!foundTask.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TaskUseCase.errorMessage, "findById"));
    }
    final var taskDto = TaskMapper.mapEntityToTaskDto(foundTask);
    return taskDto;
  }

  public List<TaskDto> findByUserId(String userId, String authUserId) {
    if (!userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(
          String.format(TaskUseCase.errorMessage, "findByUserId"));
    }
    final var foundUser = this.userDataAccess.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskUseCase.errorMessage, "findByUserId"));
    }
    final var tasks = this.taskDataAccess.findByUserId(userId);
    final var taskDtos = TaskMapper.mapEntityListToTaskDtoList(tasks);
    return taskDtos;
  }

  public void update(String taskId, UpdateTaskDto dto, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskUseCase.errorMessage, "update"));
    }
    final var foundTask = this.taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TaskUseCase.errorMessage, "update"));
    }
    if (!foundTask.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TaskUseCase.errorMessage, "update"));
    }
    final var updatedTask = new Task(taskId, dto.name, dto.description, authUserId);
    this.taskDataAccess.update(updatedTask);
  }

  public void delete(String taskId, String authUserId) {
    final var foundUser = this.userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskUseCase.errorMessage, "delete"));
    }
    final var foundTask = this.taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TaskUseCase.errorMessage, "delete"));
    }
    if (!foundTask.getUserId().equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TaskUseCase.errorMessage, "delete"));
    }
    this.taskDataAccess.delete(taskId);
  }

}
