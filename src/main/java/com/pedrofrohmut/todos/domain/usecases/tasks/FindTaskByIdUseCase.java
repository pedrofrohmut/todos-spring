package com.pedrofrohmut.todos.domain.usecases.tasks;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.TaskDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.mapper.TaskMapper;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class FindTaskByIdUseCase {

  private static final String errorMessage = "FindTaskByIdUseCase execute";

  private final TaskDataAccess taskDataAccess;
  private final UserDataAccess userDataAccess;

  public FindTaskByIdUseCase(TaskDataAccess taskDataAccess, UserDataAccess userDataAccess) {
    this.taskDataAccess = taskDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public TaskDto execute(String taskId, String authUserId) {
    checkAuthUserId(authUserId);
    checkUserExists(authUserId);
    checkTaskId(taskId);
    final var foundTask = findTaskById(taskId);
    checkResourceOwnership(foundTask, authUserId);
    final var task = getTaskDto(foundTask);
    return task;
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

  private void checkTaskId(String taskId) {
    if (taskId == null) {
      throw new MissingRequestParametersException(errorMessage);
    }
    Entity.validateId(taskId);
  }

  private Task findTaskById(String taskId) {
    final var foundTask = taskDataAccess.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(errorMessage);
    }
    return foundTask;
  }

  private void checkResourceOwnership(Task task, String userId) {
    if (!task.getUserId().equals(userId)) {
      throw new UserNotResourceOwnerException(errorMessage);
    }
  }

  private TaskDto getTaskDto(Task task) {
    return TaskMapper.mapEntityToTaskDto(task);
  }

}
