package com.pedrofrohmut.todos.domain.usecases.tasks;

import java.util.List;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.TaskDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.mapper.TaskMapper;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class FindTasksByUserIdUseCase {

  public static final String errorMessage = "FindTasksByUserIdUseCase execute";

  private final TaskDataAccess taskDataAccess;
  private final UserDataAccess userDataAccess;

  public FindTasksByUserIdUseCase(TaskDataAccess taskDataAccess, UserDataAccess userDataAccess) {
    this.taskDataAccess = taskDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public List<TaskDto> execute(String userId, String authUserId) {
    checkUserId(userId);
    checkAuthUserId(authUserId);
    checkResourceOwnership(userId, authUserId);
    checkUserExists(userId);
    final var foundTasks = findTasksByUserId(userId);
    final var tasks = getTasksDtos(foundTasks);
    return tasks;
  }

  private void checkUserId(String userId) {
    if (userId == null) {
      throw new MissingRequestParametersException(errorMessage);
    }
    Entity.validateId(userId);
  }

  private void checkAuthUserId(String userId) {
    if (userId == null) {
      throw new MissingRequestAuthUserIdException(errorMessage);
    }
    Entity.validateId(userId);
  }

  private void checkResourceOwnership(String userId, String authUserId) {
    if (!userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(errorMessage);
    }
  }

  private void checkUserExists(String userId) {
    final var foundUser = userDataAccess.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(errorMessage);
    }
  }

  private List<Task> findTasksByUserId(String userId) {
    return taskDataAccess.findByUserId(userId);
  }

  private List<TaskDto> getTasksDtos(List<Task> tasks) {
    return TaskMapper.mapEntityListToTaskDtoList(tasks);
  }

}
