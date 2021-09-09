package com.pedrofrohmut.todos.domain.usecases.tasks;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

public class DeleteTaskUseCase {

  private static final String errorMessage = "DeleteTaskUseCase execute";

  private final TaskDataAccess taskDataAccess;
  private final UserDataAccess userDataAccess;

  public DeleteTaskUseCase(TaskDataAccess taskDataAccess, UserDataAccess userDataAccess) {
    this.taskDataAccess = taskDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public void execute(String authUserId, String taskId) {
    checkAuthUserId(authUserId);
    checkTaskId(taskId);
    checkUserExists(authUserId);
    checkTaskExists(taskId);
    deleteTask(taskId);
  }

  private void checkAuthUserId(String authUserId) {
    if (authUserId == null) {
      throw new MissingRequestAuthUserIdException(errorMessage);
    }
    Entity.validateId(authUserId);
  }

  private void checkTaskId(String taskId) {
    if (taskId == null) {
      throw new MissingRequestParametersException(errorMessage);
    }
    Entity.validateId(taskId);
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

  private void deleteTask(String taskId) {
    taskDataAccess.delete(taskId);
  }

}
